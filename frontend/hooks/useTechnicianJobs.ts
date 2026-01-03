import { useState, useEffect, useCallback, useRef } from 'react'
import { API_ENDPOINTS } from '@/lib/api-config'
import { toast } from 'sonner'
import { isSameDay } from 'date-fns'
import * as React from 'react'

export const useTechnicianJobs = () => {
    const [jobs, setJobs] = useState<any[]>([])
    const [selectedJob, setSelectedJob] = useState<any>(null)
    const [isModalOpen, setIsModalOpen] = useState(false)
    const [availableParts, setAvailableParts] = useState<any[]>([])
    const [availableServices, setAvailableServices] = useState<any[]>([])
    
    // Modal Form State
    const [tempStatus, setTempStatus] = useState<string>('')
    const [partsToAdd, setPartsToAdd] = useState<any[]>([])
    const [servicesToAdd, setServicesToAdd] = useState<any[]>([])
    const [selectedPartId, setSelectedPartId] = useState<string>('')
    const [selectedServiceId, setSelectedServiceId] = useState<string>('')
    const [partQuantity, setPartQuantity] = useState<number>(1)
    const [techNotes, setTechNotes] = useState<string>('')
    const [isSaving, setIsSaving] = useState(false)
    
    // Search Ref
    const [partSearch, setPartSearch] = useState('')
    const [openPartCombobox, setOpenPartCombobox] = useState(false)
    const commandListRef = useRef<HTMLDivElement>(null)

    // Reset scroll when searching parts
    useEffect(() => {
        if (commandListRef.current) {
            commandListRef.current.scrollTop = 0
        }
    }, [partSearch])

    const getStatusColor = (status: string) => {
        switch (status) {
            case 'PENDING': return 'bg-yellow-100 text-yellow-800 border-yellow-200'
            case 'CONFIRMED': return 'bg-orange-100 text-orange-800 border-orange-200'
            case 'IN_PROGRESS': return 'bg-blue-100 text-blue-800 border-blue-200'
            case 'COMPLETED': return 'bg-green-100 text-green-800 border-green-200'
            case 'CANCELLED': return 'bg-red-100 text-red-800 border-red-200'
            default: return 'bg-gray-100 text-gray-800 border-gray-200'
        }
    }

    const fetchBookings = useCallback(async (authToken: string) => {
        try {
            const res = await fetch(API_ENDPOINTS.BOOKINGS, {
                headers: { 'Authorization': `Bearer ${authToken}` }
            })
            if (res.ok) {
                const data = await res.json()
                const mappedJobs = data.map((b: any) => ({
                    id: b.id,
                    displayId: `BK-${b.id}`,
                    customer: b.customer_name,
                    phone: b.customer_phone,
                    device: b.device_info.split(' - ')[0] || b.device_info,
                    serial: b.device_info.split(' - ')[1] || 'N/A',
                    description: b.issue_description,
                    status: b.status,
                    priority: 'medium',
                    date: new Date(b.scheduled_date),
                    scheduledDate: b.scheduled_date,
                    scheduledTime: b.scheduled_time || '00:00',
                    color: getStatusColor(b.status),
                    items: b.items ? b.items.map((i: any) => ({
                        id: i.id,
                        name: i.service ? i.service.title : (i.part ? i.part.name : 'Unknown Item'),
                        type: i.service ? 'Dịch vụ' : 'Linh kiện',
                        quantity: i.quantity,
                        price: i.price,
                        total: Number(i.price) * Number(i.quantity)
                    })) : [],
                    total_amount: Number(b.total_amount),
                    techNotes: b.tech_notes || ''
                }))
                setJobs(mappedJobs)
            }
        } catch (err) {
            console.error('Failed to fetch jobs', err)
        }
    }, [])

    const fetchParts = async (authToken: string) => {
        try {
            const res = await fetch(API_ENDPOINTS.PARTS, {
                headers: { 'Authorization': `Bearer ${authToken}` }
            })
            if (res.ok) setAvailableParts(await res.json())
        } catch (e) { console.error("Failed to load parts", e) }
    }

    const fetchServices = async (authToken: string) => {
        try {
            const res = await fetch(API_ENDPOINTS.SERVICES, {
                headers: { 'Authorization': `Bearer ${authToken}` }
            })
            if (res.ok) setAvailableServices(await res.json())
        } catch (e) { console.error("Failed to load services", e) }
    }

    const refreshData = () => {
        const token = localStorage.getItem('access_token')
        if (token) fetchBookings(token)
    }

    // Modal Actions
    const handleJobClick = (job: any) => {
        setSelectedJob(job)
        setTempStatus(job.status)
        setPartsToAdd([])
        setServicesToAdd([])
        setSelectedPartId('')
        setSelectedServiceId('')
        setPartQuantity(1)
        setTechNotes(job.techNotes || '')
        setIsModalOpen(true)
    }

    const handleAddPart = () => {
        if (!selectedPartId) return
        const part = availableParts.find(p => p.id.toString() === selectedPartId)
        if (part) {
            setPartsToAdd([...partsToAdd, {
                partId: part.id,
                name: part.name,
                price: Number(part.price),
                quantity: partQuantity,
                total: Number(part.price) * partQuantity
            }])
            setSelectedPartId('')
            setPartQuantity(1)
        }
    }

    const handleAddService = () => {
        if (!selectedServiceId) return
        const service = availableServices.find(s => s.id === selectedServiceId)
        if (service) {
            setServicesToAdd([...servicesToAdd, {
                serviceId: service.id,
                name: service.title,
                price: Number(service.base_price),
                quantity: 1,
                total: Number(service.base_price)
            }])
            setSelectedServiceId('')
        }
    }

    const handleSaveUpdate = async () => {
        const token = localStorage.getItem('access_token')
        if (!selectedJob || !token) return
        setIsSaving(true)
        try {
            // 1. Update Status
            if (tempStatus !== selectedJob.status) {
                await fetch(`${API_ENDPOINTS.BOOKINGS}/${selectedJob.id}/status`, {
                    method: 'PATCH',
                    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                    body: JSON.stringify({ status: tempStatus })
                })
            }
            // 2. Add Parts
            for (const part of partsToAdd) {
                await fetch(`${API_ENDPOINTS.BOOKINGS}/${selectedJob.id}/parts`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                    body: JSON.stringify({ partId: part.partId, quantity: part.quantity })
                })
            }
            // 3. Add Services
            for (const svc of servicesToAdd) {
                await fetch(`${API_ENDPOINTS.BOOKINGS}/${selectedJob.id}/services`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                    body: JSON.stringify({ serviceId: svc.serviceId })
                })
            }
            // 4. Update Tech Notes
            if (techNotes !== (selectedJob.techNotes || '')) {
                await fetch(`${API_ENDPOINTS.BOOKINGS}/${selectedJob.id}/notes`, {
                    method: 'PATCH',
                    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                    body: JSON.stringify({ tech_notes: techNotes })
                })
            }
            fetchBookings(token)
            setIsModalOpen(false)
            toast.success('Cập nhật thành công!')
        } catch (e: any) {
            toast.error(`Lỗi: ${e.message}`)
        } finally {
            setIsSaving(false)
        }
    }

    const calculateTotal = () => {
        const currentTotal = selectedJob?.items?.reduce((acc: number, item: any) => acc + item.total, 0) || 0
        const newPartsTotal = partsToAdd.reduce((acc, item) => acc + item.total, 0)
        const newServicesTotal = servicesToAdd.reduce((acc, item) => acc + item.total, 0)
        return currentTotal + newPartsTotal + newServicesTotal
    }

    return {
        jobs, fetchBookings, fetchParts, fetchServices, refreshData,
        selectedJob, setSelectedJob, handleJobClick, isModalOpen, setIsModalOpen,
        availableParts, availableServices,
        tempStatus, setTempStatus,
        partsToAdd, setPartsToAdd, handleAddPart,
        servicesToAdd, setServicesToAdd, handleAddService,
        selectedPartId, setSelectedPartId,
        selectedServiceId, setSelectedServiceId,
        partQuantity, setPartQuantity,
        techNotes, setTechNotes,
        isSaving, handleSaveUpdate, calculateTotal,
        partSearch, setPartSearch, commandListRef,
        openPartCombobox, setOpenPartCombobox
    }
}

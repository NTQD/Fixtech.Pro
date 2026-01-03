import { useState, useEffect, useCallback } from 'react'
import { API_ENDPOINTS } from '@/lib/api-config'
import { toast } from 'sonner'

export const useInventory = () => {
    const [parts, setParts] = useState<any[]>([])
    const [partSearch, setPartSearch] = useState('')
    const [partSort, setPartSort] = useState('id-asc')
    const [isModalOpen, setIsModalOpen] = useState(false)
    const [editingPart, setEditingPart] = useState<any>(null)
    const [partForm, setPartForm] = useState({ name: '', price: 0, stock: 0, description: '' })
    const [partToDelete, setPartToDelete] = useState<number | null>(null)

    const fetchParts = useCallback(async () => {
        const token = localStorage.getItem('access_token')
        if (!token) return
        try {
            const res = await fetch(API_ENDPOINTS.PARTS, {
                headers: { 'Authorization': `Bearer ${token}` }
            })
            if (res.ok) {
                setParts(await res.json())
            }
        } catch (e) {
            console.error("Failed to load parts", e)
        }
    }, [])

    const openModal = (part?: any) => {
        if (part) {
            setEditingPart(part)
            setPartForm({ name: part.name, price: Number(part.price), stock: part.stock, description: part.description || '' })
        } else {
            setEditingPart(null)
            setPartForm({ name: '', price: 0, stock: 0, description: '' })
        }
        setIsModalOpen(true)
    }

    const savePart = async () => {
        const token = localStorage.getItem('access_token')
        const headers = { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` }

        try {
            if (editingPart) {
                const res = await fetch(`${API_ENDPOINTS.PARTS}/${editingPart.id}`, {
                    method: 'PATCH', headers, body: JSON.stringify(partForm)
                })
                if (res.ok) {
                    toast.success('Cập nhật linh kiện thành công')
                    fetchParts()
                    setIsModalOpen(false)
                } else toast.error('Lỗi khi cập nhật')
            } else {
                const res = await fetch(API_ENDPOINTS.PARTS, {
                    method: 'POST', headers, body: JSON.stringify(partForm)
                })
                if (res.ok) {
                    toast.success('Thêm linh kiện thành công')
                    fetchParts()
                    setIsModalOpen(false)
                } else toast.error('Lỗi khi thêm')
            }
        } catch (e) { toast.error('Lỗi kết nối') }
    }

    const confirmDeletePart = async () => {
        if (!partToDelete) return
        const token = localStorage.getItem('access_token')
        try {
            const res = await fetch(`${API_ENDPOINTS.PARTS}/${partToDelete}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            })
            if (res.ok) {
                toast.success('Đã xóa linh kiện')
                fetchParts()
            } else toast.error('Không thể xóa')
        } catch (e) { console.error(e) }
        setPartToDelete(null)
    }

    // Filter Logic
    const filteredParts = parts.filter(p =>
        p.name.toLowerCase().includes(partSearch.toLowerCase())
    ).sort((a, b) => {
        if (partSort === 'price-asc') return Number(a.price) - Number(b.price)
        if (partSort === 'price-desc') return Number(b.price) - Number(a.price)
        return a.id - b.id
    })

    useEffect(() => {
        fetchParts()
    }, [fetchParts])

    return {
        parts,
        filteredParts,
        partSearch,
        setPartSearch,
        partSort,
        setPartSort,
        isModalOpen,
        setIsModalOpen,
        openModal,
        partForm,
        setPartForm,
        savePart,
        partToDelete,
        setPartToDelete, // set ID to trigger delete confirmation
        confirmDeletePart,
        editingPart
    }
}

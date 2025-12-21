'use client'

import { useState, useEffect } from 'react'
import { toast } from 'sonner'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
    DialogFooter,
} from '@/components/ui/dialog'


import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from '@/components/ui/select'
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table"
import {
    LayoutDashboard,
    Calendar as CalendarIcon,
    History,
    User,
    LogOut,
    CheckCircle2,
    Clock,
    Wrench,
    ChevronLeft,
    ChevronRight,
    Kanban as KanbanIcon,
    Search,
    MoreHorizontal,
    AlertCircle,
    RotateCw,
    CheckCircle,
    Plus,
    Trash,
    Save
} from 'lucide-react'
import { useRouter } from 'next/navigation'
import Link from 'next/link'
import {
    format,
    addMonths,
    subMonths,
    startOfMonth,
    endOfMonth,
    eachDayOfInterval,
    startOfWeek,
    endOfWeek,
    isSameMonth,
    isSameDay,
    addWeeks,
    subWeeks,
    addDays,
    subDays,
    startOfDay,
    isToday
} from 'date-fns'
import { enUS } from 'date-fns/locale'
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"

// Utility to create dates relative to today
const getRelativeDate = (daysOffset: number) => {
    const date = new Date()
    date.setDate(date.getDate() + daysOffset)
    return date
}

type CalendarView = 'month' | 'week' | 'day'

export default function TechnicianPage() {
    const [jobs, setJobs] = useState<any[]>([])
    const [selectedJob, setSelectedJob] = useState<any>(null)
    const [isModalOpen, setIsModalOpen] = useState(false)
    const router = useRouter()
    const [token, setToken] = useState<string | null>(null)
    const [currentUser, setCurrentUser] = useState<any>(null)

    // Feature Refinement State
    const [availableParts, setAvailableParts] = useState<any[]>([])
    const [availableServices, setAvailableServices] = useState<any[]>([]) // [NEW]
    const [tempStatus, setTempStatus] = useState<string>('')
    const [partsToAdd, setPartsToAdd] = useState<any[]>([])
    const [servicesToAdd, setServicesToAdd] = useState<any[]>([]) // [NEW]
    const [selectedPartId, setSelectedPartId] = useState<string>('')
    const [selectedServiceId, setSelectedServiceId] = useState<string>('') // [NEW]
    const [partQuantity, setPartQuantity] = useState<number>(1)
    const [isSaving, setIsSaving] = useState(false)

    // Dashboard Calendar State
    const [currentDate, setCurrentDate] = useState(new Date())
    const [calendarView, setCalendarView] = useState<CalendarView>('month')
    const [isSidebarOpen, setIsSidebarOpen] = useState(true)
    const [activeTab, setActiveTab] = useState('kanban')

    useEffect(() => {
        const userStr = localStorage.getItem('user')
        const tokenStr = localStorage.getItem('access_token')

        if (!userStr || !tokenStr) {
            router.push('/login')
            return
        }
        setToken(tokenStr)

        try {
            const user = JSON.parse(userStr)
            if (!user || user.role.toUpperCase() !== 'TECHNICIAN' && user.role.toUpperCase() !== 'ADMIN') {
                if (user.role.toUpperCase() !== 'TECHNICIAN') router.push('/login')
            }
            setCurrentUser(user)
        } catch (e) {
            router.push('/login')
        }

        fetchBookings(tokenStr)
        fetchBookings(tokenStr)
        fetchParts(tokenStr)
        fetchServices(tokenStr) // [NEW]
    }, [router])

    const fetchServices = async (authToken: string) => {
        try {
            const res = await fetch('http://localhost:3000/catalog/services', {
                headers: { 'Authorization': `Bearer ${authToken}` }
            })
            if (res.ok) {
                const data = await res.json()
                setAvailableServices(data)
            }
        } catch (e) {
            console.error("Failed to load services", e)
        }
    }

    const fetchParts = async (authToken: string) => {
        try {
            const res = await fetch('http://localhost:3000/catalog/parts', {
                headers: { 'Authorization': `Bearer ${authToken}` }
            })
            if (res.ok) {
                const data = await res.json()
                setAvailableParts(data)
            }
        } catch (e) {
            console.error("Failed to load parts", e)
        }
    }

    const fetchBookings = async (authToken: string) => {
        try {
            const res = await fetch('http://localhost:3000/bookings', {
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
                    color: getStatusColor(b.status),
                    items: b.items ? b.items.map((i: any) => ({
                        id: i.id,
                        name: i.service ? i.service.title : (i.part ? i.part.name : 'Unknown Item'),
                        type: i.service ? 'Dịch vụ' : 'Linh kiện',
                        quantity: i.quantity,
                        price: i.price,
                        total: Number(i.price) * Number(i.quantity)
                    })) : [],
                    total_amount: Number(b.total_amount)
                }))
                setJobs(mappedJobs)
            }
        } catch (err) {
            console.error('Failed to fetch jobs', err)
        }
    }

    const getStatusColor = (status: string) => {
        switch (status) {
            case 'PENDING': return 'bg-yellow-100 text-yellow-800 border-yellow-200'
            case 'IN_PROGRESS': return 'bg-blue-100 text-blue-800 border-blue-200'
            case 'COMPLETED': return 'bg-green-100 text-green-800 border-green-200'
            case 'CANCELLED': return 'bg-red-100 text-red-800 border-red-200'
            default: return 'bg-gray-100 text-gray-800 border-gray-200'
        }
    }

    // Filter Jobs for "My Schedule" (Today) and "History"
    const todayJobs = jobs.filter(job => isSameDay(job.date, new Date()))
    const historyJobs = jobs.filter(job => job.status === 'COMPLETED')

    // Kanban Columns
    const kanbanColumns = [
        { id: 'PENDING', title: 'Chờ tiếp nhận', icon: AlertCircle, color: 'text-yellow-600 bg-yellow-50 dark:bg-yellow-900/20' },
        { id: 'CONFIRMED', title: 'Đã tiếp nhận', icon: CheckCircle2, color: 'text-orange-600 bg-orange-50 dark:bg-orange-900/20' },
        { id: 'IN_PROGRESS', title: 'Đang sửa chữa', icon: RotateCw, color: 'text-blue-600 bg-blue-50 dark:bg-blue-900/20' },
        { id: 'COMPLETED', title: 'Hoàn thành', icon: CheckCircle, color: 'text-green-600 bg-green-50 dark:bg-green-900/20' },
    ]

    const getTasksByStatus = (status: string) => {
        return jobs.filter(t => t.status === status)
    }

    // Navigation & Calendar Logic
    const handlePrev = () => {
        if (calendarView === 'month') setCurrentDate(subMonths(currentDate, 1))
        if (calendarView === 'week') setCurrentDate(subWeeks(currentDate, 1))
        if (calendarView === 'day') setCurrentDate(subDays(currentDate, 1))
    }
    const handleNext = () => {
        if (calendarView === 'month') setCurrentDate(addMonths(currentDate, 1))
        if (calendarView === 'week') setCurrentDate(addWeeks(currentDate, 1))
        if (calendarView === 'day') setCurrentDate(addDays(currentDate, 1))
    }
    const handleToday = () => { setCurrentDate(new Date()) }
    const generateCalendarDays = () => {
        if (calendarView === 'month') {
            const start = startOfWeek(startOfMonth(currentDate), { weekStartsOn: 1 })
            const end = endOfWeek(endOfMonth(currentDate), { weekStartsOn: 1 })
            return eachDayOfInterval({ start, end })
        }
        if (calendarView === 'week') {
            const start = startOfWeek(currentDate, { weekStartsOn: 1 })
            const end = endOfWeek(currentDate, { weekStartsOn: 1 })
            return eachDayOfInterval({ start, end })
        }
        return [currentDate]
    }
    const calendarDays = generateCalendarDays()

    const handleLogout = () => {
        localStorage.clear()
        router.push('/login')
    }

    // Handlers
    const handleJobClick = (job: any) => {
        setSelectedJob(job)
        setTempStatus(job.status)
        setPartsToAdd([])
        setServicesToAdd([]) // [NEW]
        setSelectedPartId('')
        setSelectedServiceId('') // [NEW]
        setPartQuantity(1)
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
                quantity: 1, // Services usually 1
                total: Number(service.base_price)
            }])
            setSelectedServiceId('')
        }
    }

    const handleRemoveService = (index: number) => {
        const newList = [...servicesToAdd]
        newList.splice(index, 1)
        setServicesToAdd(newList)
    }

    const handleRemovePart = (index: number) => {
        const newList = [...partsToAdd]
        newList.splice(index, 1)
        setPartsToAdd(newList)
    }

    const handleSaveUpdate = async () => {
        if (!selectedJob || !token) return
        setIsSaving(true)

        try {
            // 1. Update Status if changed
            if (tempStatus !== selectedJob.status) {
                const res = await fetch(`http://localhost:3000/bookings/${selectedJob.id}/status`, {
                    method: 'PATCH',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify({ status: tempStatus })
                })
                if (!res.ok) throw new Error('Status update failed')
            }

            // 2. Add Parts
            if (partsToAdd.length > 0) {
                for (const part of partsToAdd) {
                    const res = await fetch(`http://localhost:3000/bookings/${selectedJob.id}/parts`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'Authorization': `Bearer ${token}`
                        },
                        body: JSON.stringify({ partId: part.partId, quantity: part.quantity })
                    })
                    if (!res.ok) throw new Error(`Failed to add part: ${part.name}`)
                }
            }

            // 3. Add Services [NEW]
            if (servicesToAdd.length > 0) {
                for (const svc of servicesToAdd) {
                    const res = await fetch(`http://localhost:3000/bookings/${selectedJob.id}/services`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'Authorization': `Bearer ${token}`
                        },
                        body: JSON.stringify({ serviceId: svc.serviceId })
                    })
                    if (!res.ok) throw new Error(`Failed to add service: ${svc.name}`)
                }
            }

            // Refresh
            await fetchBookings(token)
            setIsModalOpen(false)
            toast.success('Cập nhật thành công!')

        } catch (e: any) {
            console.error(e)
            toast.error(`Có lỗi xảy ra: ${e.message}`)
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

    return (
        <div className="min-h-screen bg-background flex text-foreground">
            {/* Sidebar */}
            <aside
                className={`border-r border-border bg-card fixed h-full hidden md:block transition-all duration-300 z-50
                    ${isSidebarOpen ? 'w-64' : 'w-20'}
                `}
            >
                <div className="p-6 border-b border-border flex items-center justify-between">
                    <div className={`flex items-center gap-3 ${!isSidebarOpen && 'justify-center w-full'}`}>
                        <div className="w-10 h-10 bg-primary rounded-lg flex items-center justify-center flex-shrink-0">
                            <Wrench className="w-6 h-6 text-primary-foreground" />
                        </div>
                        {isSidebarOpen && <span className="font-bold text-xl whitespace-nowrap">TechFix Pro</span>}
                    </div>
                </div>
                <nav className="p-4 space-y-2">
                    <Button
                        variant={activeTab === 'kanban' ? 'secondary' : 'ghost'}
                        className={`w-full flex items-center gap-3 justify-start ${!isSidebarOpen && 'justify-center px-2'}`}
                        onClick={() => setActiveTab('kanban')}
                    >
                        <KanbanIcon className="w-4 h-4 flex-shrink-0" />
                        {isSidebarOpen && <span>Kanban Board</span>}
                    </Button>
                    <Button
                        variant={activeTab === 'dashboard' ? 'secondary' : 'ghost'}
                        className={`w-full flex items-center gap-3 justify-start ${!isSidebarOpen && 'justify-center px-2'}`}
                        onClick={() => setActiveTab('dashboard')}
                    >
                        <CalendarIcon className="w-4 h-4 flex-shrink-0" />
                        {isSidebarOpen && <span>Calendar</span>}
                    </Button>
                    <Button
                        variant={activeTab === 'schedule' ? 'secondary' : 'ghost'}
                        className={`w-full flex items-center gap-3 justify-start ${!isSidebarOpen && 'justify-center px-2'}`}
                        onClick={() => setActiveTab('schedule')}
                    >
                        <LayoutDashboard className="w-4 h-4 flex-shrink-0" />
                        {isSidebarOpen && <span>My Schedule</span>}
                    </Button>
                    <Button
                        variant={activeTab === 'history' ? 'secondary' : 'ghost'}
                        className={`w-full flex items-center gap-3 justify-start ${!isSidebarOpen && 'justify-center px-2'}`}
                        onClick={() => setActiveTab('history')}
                    >
                        <History className="w-4 h-4 flex-shrink-0" />
                        {isSidebarOpen && <span>History</span>}
                    </Button>
                </nav>

                {/* Sidebar Toggle Button */}
                <div className="absolute bottom-4 left-0 w-full flex justify-center px-4">
                    <Button
                        variant="ghost"
                        size="icon"
                        onClick={() => setIsSidebarOpen(!isSidebarOpen)}
                        className="w-full flex items-center justify-center hover:bg-muted"
                    >
                        {isSidebarOpen ? (
                            <div className="flex items-center gap-2">
                                <ChevronLeft className="w-4 h-4" />
                                <span className="text-sm">Collapse</span>
                            </div>
                        ) : (
                            <ChevronRight className="w-4 h-4" />
                        )}
                    </Button>
                </div>
            </aside>

            {/* Main Content */}
            <main className={`flex-1 bg-muted/20 min-h-screen transition-all duration-300 ${isSidebarOpen ? 'md:ml-64' : 'md:ml-20'}`}>
                {/* Header */}
                <header className="bg-card border-b border-border h-16 flex items-center justify-between px-6 sticky top-0 z-10">
                    <h1 className="font-semibold text-lg">
                        {activeTab === 'kanban' && 'Kanban Board'}
                        {activeTab === 'dashboard' && 'Calendar Dashboard'}
                        {activeTab === 'schedule' && 'My Schedule'}
                        {activeTab === 'history' && 'Job History'}
                    </h1>
                    <div className="flex items-center gap-3">
                        <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                                <Button variant="ghost" className="relative h-10 w-10 rounded-full">
                                    <Avatar className="h-10 w-10">
                                        <AvatarImage src={currentUser?.avatar_url || "/avatars/tech.png"} alt="@tech" />
                                        <AvatarFallback className="bg-primary/10 text-primary font-bold">{currentUser?.email?.[0].toUpperCase() || 'T'}</AvatarFallback>
                                    </Avatar>
                                </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent className="w-56" align="end" forceMount>
                                <DropdownMenuLabel className="font-normal">
                                    <div className="flex flex-col space-y-1">
                                        <p className="text-sm font-medium leading-none">{currentUser?.name || currentUser?.full_name || 'Technician'}</p>
                                        <p className="text-xs leading-none text-muted-foreground">
                                            {currentUser?.email}
                                        </p>
                                    </div>
                                </DropdownMenuLabel>
                                <DropdownMenuSeparator />
                                <DropdownMenuItem asChild>
                                    <Link href="/profile" className="cursor-pointer">
                                        <User className="mr-2 h-4 w-4" />
                                        <span>Hồ sơ Kỹ thuật viên</span>
                                    </Link>
                                </DropdownMenuItem>
                                <DropdownMenuSeparator />
                                <DropdownMenuItem onClick={handleLogout} className="text-destructive focus:text-destructive">
                                    <LogOut className="mr-2 h-4 w-4" />
                                    <span>Đăng xuất</span>
                                </DropdownMenuItem>
                            </DropdownMenuContent>
                        </DropdownMenu>
                    </div>
                </header>

                {/* Content Area */}
                <div className="p-6 overflow-x-auto">
                    {/* KANBAN BOARD VIEW */}
                    {activeTab === 'kanban' && (
                        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 pb-4">
                            {kanbanColumns.map((col) => (
                                <div key={col.id} className="flex flex-col h-full">
                                    <div className={`flex items-center justify-between p-3 rounded-t-lg border-b-2 ${col.color} border-${col.color.split(' ')[0].replace('text-', '')}`}>
                                        <div className="flex items-center gap-2 font-semibold">
                                            <col.icon className="w-4 h-4" />
                                            {col.title}
                                            <Badge variant="secondary" className="ml-1">{getTasksByStatus(col.id).length}</Badge>
                                        </div>
                                        <MoreHorizontal className="w-4 h-4 text-muted-foreground cursor-pointer" />
                                    </div>
                                    <div className="bg-muted/30 p-3 rounded-b-lg flex-grow space-y-3 min-h-[500px]">
                                        {getTasksByStatus(col.id).map((task) => (
                                            <Card key={task.id} className="cursor-pointer hover:shadow-md transition-shadow" onClick={() => handleJobClick(task)}>
                                                <CardContent className="p-4 space-y-3">
                                                    <div className="flex justify-between items-start">
                                                        <Badge variant="outline" className="font-mono text-xs">{task.displayId || task.id}</Badge>
                                                        {task.priority === 'high' && <Badge className="bg-red-100 text-red-700 hover:bg-red-100 dark:bg-red-900/30 dark:text-red-300 border-none">High</Badge>}
                                                    </div>
                                                    <div>
                                                        <h4 className="font-semibold text-sm">{task.device}</h4>
                                                        <p className="text-xs text-muted-foreground">{task.description}</p>
                                                    </div>
                                                    <div className="flex items-center justify-between text-xs text-muted-foreground pt-2 border-t">
                                                        <div className="flex items-center gap-1">
                                                            <User className="w-3 h-3" /> {task.customer}
                                                        </div>
                                                        <span>{format(task.date, 'dd/MM')}</span>
                                                    </div>
                                                </CardContent>
                                            </Card>
                                        ))}
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}

                    {/* Dashboard: Advanced Calendar View */}
                    {activeTab === 'dashboard' && (
                        <div className="bg-card rounded-xl border border-border shadow-sm overflow-hidden flex flex-col">
                            {/* Calendar Header with Navigation */}
                            <div className="p-4 border-b border-border flex flex-col sm:flex-row items-center justify-between gap-4">
                                <div className="flex items-center gap-4">
                                    <h2 className="font-bold text-2xl capitalize">
                                        {format(currentDate, 'MMMM yyyy', { locale: enUS })}
                                    </h2>
                                    <div className="flex items-center border border-border rounded-md bg-background">
                                        <Button variant="ghost" size="icon" onClick={handlePrev}>
                                            <ChevronLeft className="w-4 h-4" />
                                        </Button>
                                        <Button variant="ghost" size="icon" onClick={handleNext}>
                                            <ChevronRight className="w-4 h-4" />
                                        </Button>
                                        <div className="w-px h-6 bg-border mx-1"></div>
                                        <Button variant="ghost" size="sm" onClick={handleToday}>
                                            Today
                                        </Button>
                                    </div>
                                </div>

                                <div className="flex bg-muted p-1 rounded-md">
                                    <Button
                                        variant={calendarView === 'month' ? 'default' : 'ghost'}
                                        size="sm"
                                        className="h-8 text-xs"
                                        onClick={() => setCalendarView('month')}
                                    >
                                        Month
                                    </Button>
                                    <Button
                                        variant={calendarView === 'week' ? 'default' : 'ghost'}
                                        size="sm"
                                        className="h-8 text-xs"
                                        onClick={() => setCalendarView('week')}
                                    >
                                        Week
                                    </Button>
                                    <Button
                                        variant={calendarView === 'day' ? 'default' : 'ghost'}
                                        size="sm"
                                        className="h-8 text-xs"
                                        onClick={() => setCalendarView('day')}
                                    >
                                        Day
                                    </Button>
                                </div>
                            </div>

                            {/* Weekday Header */}
                            <div className="grid grid-cols-7 border-b border-border bg-muted/30 flex-shrink-0">
                                {['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'].map(day => (
                                    <div key={day} className="py-3 text-center text-xs font-bold text-muted-foreground border-r border-border last:border-r-0">
                                        {day}
                                    </div>
                                ))}
                            </div>

                            {/* Calendar Grid Body */}
                            <div className={`grid ${calendarView === 'day' ? 'grid-cols-1' : 'grid-cols-7'} auto-rows-fr flex-1 overflow-y-auto`}>
                                {calendarDays.map((date, idx) => (
                                    <div
                                        key={idx}
                                        className={`
                                    min-h-[120px] border-b border-r border-border p-2 relative group hover:bg-muted/10 transition-colors
                                    ${!isSameMonth(date, currentDate) && calendarView === 'month' ? 'bg-muted/20 text-muted-foreground' : ''}
                                    ${calendarView === 'day' ? 'min-h-[300px]' : ''}
                                `}
                                    >
                                        <div className="flex justify-between items-start mb-2">
                                            <span
                                                className={`
                                            text-sm font-medium w-7 h-7 flex items-center justify-center rounded-full
                                            ${isToday(date) ? 'bg-primary text-primary-foreground' : ''}
                                        `}
                                            >
                                                {format(date, 'd')}
                                            </span>
                                        </div>

                                        <div className="space-y-1">
                                            {jobs.filter(job => isSameDay(job.date, date)).map(job => (
                                                <div
                                                    key={job.id}
                                                    onClick={() => handleJobClick(job)}
                                                    className={`
                                                px-2 py-1.5 rounded-md cursor-pointer border text-xs shadow-sm hover:shadow-md transition-all
                                                ${job.color}
                                            `}
                                                >
                                                    <p className="font-semibold truncate">{job.customer}</p>
                                                    <p className="truncate opacity-80">{job.device}</p>
                                                    {calendarView !== 'month' && (
                                                        <p className="truncate opacity-75 mt-1">{job.description}</p>
                                                    )}
                                                </div>
                                            ))}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}

                    {/* My Schedule: Today's Jobs */}
                    {activeTab === 'schedule' && (
                        <div className="space-y-6">
                            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                                <div className="bg-card p-4 rounded-xl border border-border shadow-sm">
                                    <p className="text-sm text-muted-foreground">Chờ xử lý</p>
                                    <div className="flex items-center justify-between mt-2">
                                        <span className="text-2xl font-bold">{todayJobs.filter(j => j.status === 'PENDING').length}</span>
                                        <Clock className="w-8 h-8 text-orange-500 opacity-20" />
                                    </div>
                                </div>
                                <div className="bg-card p-4 rounded-xl border border-border shadow-sm">
                                    <p className="text-sm text-muted-foreground">Đang thực hiện</p>
                                    <div className="flex items-center justify-between mt-2">
                                        <span className="text-2xl font-bold">{todayJobs.filter(j => j.status === 'IN_PROGRESS').length}</span>
                                        <Wrench className="w-8 h-8 text-blue-500 opacity-20" />
                                    </div>
                                </div>
                                <div className="bg-card p-4 rounded-xl border border-border shadow-sm">
                                    <p className="text-sm text-muted-foreground">Hoàn thành hôm nay</p>
                                    <div className="flex items-center justify-between mt-2">
                                        <span className="text-2xl font-bold">{todayJobs.filter(j => j.status === 'COMPLETED').length}</span>
                                        <CheckCircle2 className="w-8 h-8 text-green-500 opacity-20" />
                                    </div>
                                </div>
                            </div>

                            <div className="space-y-4">
                                <h2 className="font-semibold text-lg">Danh sách công việc</h2>
                                {todayJobs.length === 0 ? (
                                    <p className="text-muted-foreground italic">Không có công việc nào hôm nay.</p>
                                ) : (
                                    todayJobs.map(job => (
                                        <div key={job.id} onClick={() => handleJobClick(job)} className="bg-card p-4 rounded-xl border border-border shadow-sm hover:border-primary transition-colors cursor-pointer flex justify-between items-center">
                                            <div className="flex gap-4">
                                                <div className={`w-2 h-full rounded-full ${job.status === 'COMPLETED' ? 'bg-green-500' : job.status === 'IN_PROGRESS' ? 'bg-blue-500' : 'bg-orange-500'}`}></div>
                                                <div>
                                                    <h3 className="font-bold">{job.customer} <span className="text-sm font-normal text-muted-foreground">({job.phone})</span></h3>
                                                    <p className="text-sm text-foreground">{job.device} - <span className="text-muted-foreground">{job.serial}</span></p>
                                                    <p className="text-sm text-foreground mt-1">{job.description}</p>
                                                </div>
                                            </div>
                                            <div className={`px-3 py-1 rounded-full text-xs font-medium ${job.color}`}>
                                                {job.status}
                                            </div>
                                        </div>
                                    ))
                                )}
                            </div>
                        </div>
                    )}

                    {/* History: Completed Jobs */}
                    {activeTab === 'history' && (
                        <div className="bg-card rounded-xl border border-border shadow-sm overflow-hidden">
                            <table className="w-full text-sm">
                                <thead className="border-b border-border bg-muted/50">
                                    <tr>
                                        <th className="text-left py-3 px-4">Ngày</th>
                                        <th className="text-left py-3 px-4">Khách hàng</th>
                                        <th className="text-left py-3 px-4">Thiết bị</th>
                                        <th className="text-left py-3 px-4">Trạng thái</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {historyJobs.map(job => (
                                        <tr key={job.id} onClick={() => handleJobClick(job)} className="border-b border-border hover:bg-muted/50 cursor-pointer">
                                            <td className="py-3 px-4">{format(job.date, 'dd/MM/yyyy')}</td>
                                            <td className="py-3 px-4 font-medium">{job.customer}</td>
                                            <td className="py-3 px-4">{job.device}</td>
                                            <td className="py-3 px-4">
                                                <span className="bg-green-100 text-green-800 px-2 py-1 rounded-full text-xs font-medium">
                                                    {job.status}
                                                </span>
                                            </td>
                                        </tr>
                                    ))}
                                    {historyJobs.length === 0 && (
                                        <tr>
                                            <td colSpan={4} className="py-8 text-center text-muted-foreground">Chưa có lịch sử công việc.</td>
                                        </tr>
                                    )}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            </main>

            {/* Repair Details Modal */}
            <Dialog open={isModalOpen} onOpenChange={setIsModalOpen}>
                <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
                    <DialogHeader>
                        <DialogTitle>Chi tiết sửa chữa: #{selectedJob?.displayId || selectedJob?.id}</DialogTitle>
                        <DialogDescription>
                            Cập nhật thông tin sửa chữa, thêm linh kiện hoặc dịch vụ cho đơn hàng này.
                        </DialogDescription>
                    </DialogHeader>
                    {selectedJob && (
                        <div className="space-y-6 py-4">
                            {/* Info Grid */}
                            <div className="grid grid-cols-2 gap-8">
                                <div>
                                    <h4 className="font-semibold text-sm mb-1 text-muted-foreground">Khách hàng</h4>
                                    <p className="text-sm font-medium">{selectedJob.customer}</p>
                                    <p className="text-sm text-muted-foreground">{selectedJob.phone}</p>
                                </div>
                                <div>
                                    <h4 className="font-semibold text-sm mb-1 text-muted-foreground">Thiết bị</h4>
                                    <p className="text-sm font-medium">{selectedJob.device}</p>
                                    <p className="text-sm text-muted-foreground">S/N: {selectedJob.serial}</p>
                                </div>
                            </div>

                            {/* Problem Description */}
                            <div className="bg-muted/50 p-3 rounded-lg border border-border">
                                <h4 className="font-semibold text-sm mb-2">Mô tả lỗi của khách</h4>
                                <p className="text-sm text-foreground">
                                    {selectedJob.description}
                                </p>
                            </div>

                            {/* Bill of Materials & Services */}
                            <div className="space-y-4">
                                <h4 className="font-semibold text-sm">Dịch vụ & Linh kiện</h4>
                                <div className="border border-border rounded-lg overflow-hidden">
                                    <Table>
                                        <TableHeader>
                                            <TableRow>
                                                <TableHead>Mục</TableHead>
                                                <TableHead>Loại</TableHead>
                                                <TableHead>SL</TableHead>
                                                <TableHead className="text-right">Đơn giá</TableHead>
                                                <TableHead className="text-right">Thành tiền</TableHead>
                                            </TableRow>
                                        </TableHeader>
                                        <TableBody>
                                            {selectedJob.items?.map((item: any) => (
                                                <TableRow key={item.id}>
                                                    <TableCell className="break-words whitespace-normal">{item.name}</TableCell>
                                                    <TableCell><Badge variant="outline">{item.type}</Badge></TableCell>
                                                    <TableCell>{item.quantity}</TableCell>
                                                    <TableCell className="text-right whitespace-nowrap">{item.price?.toLocaleString()} đ</TableCell>
                                                    <TableCell className="text-right whitespace-nowrap">{item.total?.toLocaleString()} đ</TableCell>
                                                </TableRow>
                                            ))}
                                            {/* New Services Pending Add */}
                                            {servicesToAdd.map((svc, index) => (
                                                <TableRow key={`svc-${index}`} className="bg-blue-50/50">
                                                    <TableCell className="break-words whitespace-normal">{svc.name} <Badge className="ml-2 text-[10px] bg-blue-500 hover:bg-blue-600">Dịch vụ Mới</Badge></TableCell>
                                                    <TableCell className="whitespace-nowrap">Dịch vụ</TableCell>
                                                    <TableCell>{svc.quantity}</TableCell>
                                                    <TableCell className="text-right whitespace-nowrap">{svc.price.toLocaleString()} đ</TableCell>
                                                    <TableCell className="text-right flex items-center justify-end gap-2 whitespace-nowrap">
                                                        {svc.total.toLocaleString()} đ
                                                        <Button variant="ghost" size="icon" className="h-6 w-6 text-destructive" onClick={() => handleRemoveService(index)}>
                                                            <Trash className="w-3 h-3" />
                                                        </Button>
                                                    </TableCell>
                                                </TableRow>
                                            ))}
                                            {/* New Parts Pending Add */}
                                            {partsToAdd.map((part, index) => (
                                                <TableRow key={index} className="bg-muted/30">
                                                    <TableCell className="break-words whitespace-normal">{part.name} <Badge variant="secondary" className="ml-2 text-[10px]">Mới</Badge></TableCell>
                                                    <TableCell className="whitespace-nowrap">Linh kiện</TableCell>
                                                    <TableCell>{part.quantity}</TableCell>
                                                    <TableCell className="text-right whitespace-nowrap">{part.price.toLocaleString()} đ</TableCell>
                                                    <TableCell className="text-right flex items-center justify-end gap-2 whitespace-nowrap">
                                                        {part.total.toLocaleString()} đ
                                                        <Button variant="ghost" size="icon" className="h-6 w-6 text-destructive" onClick={() => handleRemovePart(index)}>
                                                            <Trash className="w-3 h-3" />
                                                        </Button>
                                                    </TableCell>
                                                </TableRow>
                                            ))}
                                        </TableBody>
                                    </Table>
                                </div>

                                {/* Add Service Section [NEW] */}
                                <div className="flex items-end gap-3 bg-blue-50/50 p-3 border border-blue-100 rounded-lg">
                                    <div className="flex-1 space-y-2 min-w-0">
                                        <label className="text-xs font-medium text-blue-700">Thêm dịch vụ phát sinh</label>
                                        <Select value={selectedServiceId} onValueChange={setSelectedServiceId}>
                                            <SelectTrigger className="h-auto whitespace-normal text-left bg-white">
                                                <SelectValue placeholder="Chọn dịch vụ..." />
                                            </SelectTrigger>
                                            <SelectContent className="max-w-[400px]">
                                                {availableServices.map(svc => (
                                                    <SelectItem key={svc.id} value={svc.id} className="whitespace-normal break-words">
                                                        {svc.title} - {Number(svc.base_price).toLocaleString()} đ
                                                    </SelectItem>
                                                ))}
                                            </SelectContent>
                                        </Select>
                                    </div>
                                    <Button onClick={handleAddService} disabled={!selectedServiceId} className="bg-blue-600 hover:bg-blue-700">
                                        <Plus className="w-4 h-4" />
                                    </Button>
                                </div>

                                {/* Add Part Section */}
                                <div className="flex items-end gap-3 bg-card p-3 border border-border rounded-lg">
                                    <div className="flex-1 space-y-2 min-w-0">
                                        <label className="text-xs font-medium">Thêm vật tư/linh kiện</label>
                                        <Select value={selectedPartId} onValueChange={setSelectedPartId}>
                                            <SelectTrigger className="h-auto whitespace-normal text-left">
                                                <SelectValue placeholder="Chọn linh kiện..." />
                                            </SelectTrigger>
                                            <SelectContent className="max-w-[400px]">
                                                {availableParts.map(part => (
                                                    <SelectItem key={part.id} value={part.id.toString()} className="whitespace-normal break-words">
                                                        {part.name} (Tồn: {part.stock}) - {Number(part.price).toLocaleString()} đ
                                                    </SelectItem>
                                                ))}
                                            </SelectContent>
                                        </Select>
                                    </div>
                                    <div className="w-20 space-y-2">
                                        <label className="text-xs font-medium">SL</label>
                                        <Input
                                            type="number"
                                            min={1}
                                            value={partQuantity}
                                            onChange={(e) => setPartQuantity(Number(e.target.value))}
                                        />
                                    </div>
                                    <Button onClick={handleAddPart} disabled={!selectedPartId}>
                                        <Plus className="w-4 h-4" />
                                    </Button>
                                </div>

                                {/* Total Price */}
                                <div className="flex justify-between items-center text-lg font-bold border-t pt-4">
                                    <span>Tổng cộng dự kiến:</span>
                                    <span className="text-primary">{calculateTotal().toLocaleString()} đ</span>
                                </div>
                            </div>

                            {/* Actions */}
                            <div className="space-y-4 pt-4 border-t">
                                <div className="space-y-2">
                                    <label className="text-sm font-medium">Cập nhật trạng thái đơn</label>
                                    <Select
                                        value={tempStatus}
                                        onValueChange={setTempStatus}
                                    >
                                        <SelectTrigger className={getStatusColor(tempStatus)}>
                                            <SelectValue />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="PENDING">Chờ tiếp nhận (Pending)</SelectItem>
                                            <SelectItem value="CONFIRMED">Đã tiếp nhận (Confirmed)</SelectItem>
                                            <SelectItem value="IN_PROGRESS">Đang sửa chữa (In Progress)</SelectItem>
                                            <SelectItem value="COMPLETED">Hoàn thành (Completed)</SelectItem>
                                            <SelectItem value="CANCELLED">Hủy bỏ (Cancelled)</SelectItem>
                                        </SelectContent>
                                    </Select>
                                    <p className="text-xs text-muted-foreground">Thay đổi sẽ chỉ được lưu khi bạn nhấn nút "Lưu cập nhật".</p>
                                </div>

                                <div className="space-y-2">
                                    <label className="text-sm font-medium">Ghi chú kỹ thuật</label>
                                    <Textarea placeholder="Ghi chú công việc đã làm..." className="h-24" />
                                </div>
                            </div>
                        </div>
                    )}
                    <DialogFooter>
                        <Button variant="outline" onClick={() => setIsModalOpen(false)}>Đóng</Button>
                        <Button onClick={handleSaveUpdate} disabled={isSaving}>
                            {isSaving ? 'Đang lưu...' : 'Lưu cập nhật'}
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    )
}

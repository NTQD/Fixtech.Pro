'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuLabel, DropdownMenuSeparator, DropdownMenuTrigger, DropdownMenuSub, DropdownMenuSubTrigger, DropdownMenuSubContent } from "@/components/ui/dropdown-menu"
import { Button } from '@/components/ui/button'
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { User, LogOut, Clock, Wrench, CheckCircle2, Sun, Moon, Laptop } from 'lucide-react'
import { useTheme } from 'next-themes'
import Link from 'next/link'
import { isSameDay } from 'date-fns'

// Custom Hooks
import { useTechnicianJobs } from '@/hooks/useTechnicianJobs'

// Components
import { TechnicianSidebar } from '@/components/technician/TechnicianSidebar'
import { KanbanBoard } from '@/components/technician/KanbanBoard'
import { CalendarDashboard } from '@/components/technician/CalendarDashboard'
import { JobHistoryTable } from '@/components/technician/JobHistoryTable'
import { RepairDetailModal } from '@/components/technician/RepairDetailModal'

export default function TechnicianPage() {
    const router = useRouter()
    const [activeTab, setActiveTab] = useState('kanban')
    const [isSidebarOpen, setIsSidebarOpen] = useState(true)
    const [currentUser, setCurrentUser] = useState<any>(null)
    const { theme, setTheme } = useTheme()

    const hookData = useTechnicianJobs()
    const { jobs, fetchBookings, fetchParts, fetchServices, handleJobClick } = hookData

    useEffect(() => {
        const userStr = localStorage.getItem('user')
        const token = localStorage.getItem('access_token')

        if (!userStr || !token) { router.push('/login'); return }
        
        try {
            const user = JSON.parse(userStr)
            if (user.role !== 'TECHNICIAN' && user.role !== 'ADMIN') router.push('/login')
            setCurrentUser(user)
        } catch { router.push('/login') }

        fetchBookings(token)
        fetchParts(token)
        fetchServices(token)
    }, [router, fetchBookings]) // Added valid dependencies

    const handleLogout = () => {
        localStorage.clear()
        router.push('/login')
    }

    const todayJobs = jobs.filter(job => isSameDay(job.date, new Date()))

    return (
        <div className="min-h-screen bg-background flex text-foreground">
            <TechnicianSidebar isSidebarOpen={isSidebarOpen} setIsSidebarOpen={setIsSidebarOpen} activeTab={activeTab} setActiveTab={setActiveTab} />

            <div className={`flex-1 bg-muted/20 min-h-screen transition-all duration-300 ${isSidebarOpen ? 'md:ml-64' : 'md:ml-20'}`}>
                {/* Header */}
                <header className="bg-card border-b border-border h-16 flex items-center justify-between px-6 sticky top-0 z-10">
                    <h1 className="font-semibold text-lg capitalize">{activeTab.replace('-', ' ')}</h1>
                    <div className="flex items-center gap-3">
                        <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                                <Button variant="ghost" className="relative h-10 w-10 rounded-full ring-2 ring-black/10 hover:ring-black/30 dark:ring-white/20 dark:hover:ring-white/50 transition-all">
                                    <Avatar className="h-10 w-10">
                                        <AvatarImage 
                                            className={!currentUser?.avatar_url || ['user.png', 'admin.png', 'tech.png', 'technician.png', 'default'].some(s => currentUser.avatar_url?.includes(s)) ? "dark:invert" : ""}
                                            src={currentUser?.avatar_url?.replace(/\/uploads\/avatars?/, '/public/avatars') || 'http://localhost:3000/public/avatars/technician.png'} 
                                        />
                                        <AvatarFallback className="bg-primary/10 text-primary font-bold">{currentUser?.email?.[0].toUpperCase()}</AvatarFallback>
                                    </Avatar>
                                </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent className="w-56" align="end">
                                <DropdownMenuLabel className="font-normal">
                                    <div className="flex flex-col space-y-1">
                                        <p className="text-sm font-medium leading-none">Tài khoản</p>
                                        <p className="text-xs leading-none text-muted-foreground">{currentUser?.email}</p>
                                    </div>
                                </DropdownMenuLabel>
                                <DropdownMenuSeparator />
                                <DropdownMenuItem asChild>
                                    <Link href="/profile" className="flex items-center cursor-pointer">
                                        <User className="mr-2 h-4 w-4" />
                                        <span>Hồ sơ cá nhân</span>
                                    </Link>
                                </DropdownMenuItem>
                                <DropdownMenuSeparator />
                                <DropdownMenuSub>
                                    <DropdownMenuSubTrigger>
                                        <Sun className="mr-2 h-4 w-4 rotate-0 scale-100 transition-all dark:-rotate-90 dark:scale-0" />
                                        <Moon className="absolute mr-2 h-4 w-4 rotate-90 scale-0 transition-all dark:rotate-0 dark:scale-100" />
                                        <span>Giao diện</span>
                                    </DropdownMenuSubTrigger>
                                    <DropdownMenuSubContent>
                                        <DropdownMenuItem onClick={() => setTheme("light")}>
                                            <Sun className="mr-2 h-4 w-4" />
                                            <span>Sáng</span>
                                        </DropdownMenuItem>
                                        <DropdownMenuItem onClick={() => setTheme("dark")}>
                                            <Moon className="mr-2 h-4 w-4" />
                                            <span>Tối</span>
                                        </DropdownMenuItem>
                                        <DropdownMenuItem onClick={() => setTheme("system")}>
                                            <Laptop className="mr-2 h-4 w-4" />
                                            <span>Hệ thống</span>
                                        </DropdownMenuItem>
                                    </DropdownMenuSubContent>
                                </DropdownMenuSub>
                                <DropdownMenuSeparator />
                                <DropdownMenuItem onClick={handleLogout} className="text-destructive focus:text-destructive cursor-pointer">
                                    <LogOut className="mr-2 h-4 w-4" />
                                    <span>Đăng xuất</span>
                                </DropdownMenuItem>
                            </DropdownMenuContent>
                        </DropdownMenu>
                    </div>
                </header>

                <div className="p-6">
                    {activeTab === 'kanban' && <KanbanBoard jobs={jobs} onJobClick={handleJobClick} />}
                    {activeTab === 'calendar' && <CalendarDashboard jobs={jobs} onJobClick={handleJobClick} />}
                    {activeTab === 'history' && <JobHistoryTable jobs={jobs} />}
                    
                    {activeTab === 'schedule' && (
                        <div className="space-y-6">
                            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                                <div className="bg-card p-4 rounded-xl border border-border shadow-sm">
                                    <div className="flex justify-between"><span className="text-sm text-muted-foreground">Chờ xử lý</span><Clock className="opacity-20 text-orange-500" /></div>
                                    <div className="text-2xl font-bold">{todayJobs.filter(j => j.status === 'PENDING').length}</div>
                                </div>
                                <div className="bg-card p-4 rounded-xl border border-border shadow-sm">
                                    <div className="flex justify-between"><span className="text-sm text-muted-foreground">Đang làm</span><Wrench className="opacity-20 text-blue-500" /></div>
                                    <div className="text-2xl font-bold">{todayJobs.filter(j => j.status === 'IN_PROGRESS').length}</div>
                                </div>
                                <div className="bg-card p-4 rounded-xl border border-border shadow-sm">
                                    <div className="flex justify-between"><span className="text-sm text-muted-foreground">Hoàn thành</span><CheckCircle2 className="opacity-20 text-green-500" /></div>
                                    <div className="text-2xl font-bold">{todayJobs.filter(j => j.status === 'COMPLETED').length}</div>
                                </div>
                            </div>
                            <div className="space-y-4">
                                <h2 className="font-semibold text-lg">Hôm nay</h2>
                                {todayJobs.map(job => (
                                    <div key={job.id} onClick={() => handleJobClick(job)} className="bg-card p-4 rounded-xl border border-border shadow-sm hover:border-primary cursor-pointer flex justify-between items-center">
                                        <div>
                                            <h3 className="font-bold">{job.customer}</h3>
                                            <p className="text-sm text-muted-foreground">{job.device} - {job.description}</p>
                                        </div>
                                        <div className={`px-3 py-1 rounded-full text-xs font-medium ${job.color}`}>{job.status}</div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}
                </div>
            </div>

            <RepairDetailModal 
                isOpen={hookData.isModalOpen} 
                onOpenChange={hookData.setIsModalOpen}
                job={hookData.selectedJob}
                {...hookData}
            /> 
        </div>
    )
}

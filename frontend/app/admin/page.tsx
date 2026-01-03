'use client'

import { Button } from '@/components/ui/button'
import Link from 'next/link'
import { Wrench, LogOut, BarChart3, Calendar, Users, Settings, ChevronLeft, ChevronRight, User, Package, Sun, Moon, Laptop } from 'lucide-react'
import { useTheme } from 'next-themes'
import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
    DropdownMenuSub,
    DropdownMenuSubTrigger,
    DropdownMenuSubContent,
} from "@/components/ui/dropdown-menu"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"

// Import Custom Hooks
import { useAdminData } from '@/hooks/useAdminData'

// Import Components
import { AdminOverview } from '@/components/admin/AdminOverview'
import { BookingManager } from '@/components/admin/BookingManager'
import { CustomerManager } from '@/components/admin/CustomerManager'
import { InventoryManager } from '@/components/admin/InventoryManager'
import { SystemSettings } from '@/components/admin/SystemSettings'

export default function AdminDashboard() {
    const router = useRouter()
    const [activeTab, setActiveTab] = useState('overview')
    const [isSidebarOpen, setIsSidebarOpen] = useState(true)
    const [currentUser, setCurrentUser] = useState<any>(null)
    const { theme, setTheme } = useTheme()

    // Load Data using Custom Hook
    const {
        bookings, users, stats, config, setConfig,
        updateStatus, assignTechnician, saveSettings,
        updateUserRole, banUser, unbanUser
    } = useAdminData()

    useEffect(() => {
        const userStr = localStorage.getItem('user')
        if (!userStr) {
            router.push('/login')
            return
        }
        try {
            const user = JSON.parse(userStr)
            if (!user || user.role.toUpperCase() !== 'ADMIN') {
                router.push('/login')
            }
            setCurrentUser(user)
        } catch (e) { router.push('/login') }
    }, [router])

    const handleLogout = () => {
        localStorage.clear()
        router.push('/login')
    }

    return (
        <div className="min-h-screen bg-background flex">
            {/* Sidebar */}
            <div className={`fixed left-0 top-0 h-screen bg-card border-r border-border transition-all duration-300 z-50 hidden md:block ${isSidebarOpen ? 'w-64' : 'w-20'}`}>
                <div className="p-6 border-b border-border flex items-center justify-between">
                    <div className={`flex items-center gap-2 ${!isSidebarOpen && 'justify-center w-full'}`}>
                        <div className="w-10 h-10 bg-primary rounded-lg flex items-center justify-center flex-shrink-0">
                            <Wrench className="w-6 h-6 text-primary-foreground" />
                        </div>
                        {isSidebarOpen && <span className="font-bold text-lg whitespace-nowrap">TechFix Admin</span>}
                    </div>
                </div>

                <nav className="p-4 space-y-2">
                    {[
                        { id: 'overview', icon: BarChart3, label: 'Tổng quan' },
                        { id: 'bookings', icon: Calendar, label: 'Quản lý Đặt lịch' },
                        { id: 'customers', icon: Users, label: 'Khách hàng' },
                        { id: 'inventory', icon: Package, label: 'Kho hàng' },
                        { id: 'settings', icon: Settings, label: 'Cài đặt' },
                    ].map(item => (
                        <button
                            key={item.id}
                            onClick={() => setActiveTab(item.id)}
                            className={`w-full flex items-center gap-3 px-4 py-2 rounded-lg transition ${activeTab === item.id ? 'bg-primary text-primary-foreground' : 'text-foreground hover:bg-muted'} ${!isSidebarOpen && 'justify-center px-2'}`}
                        >
                            <item.icon className="w-5 h-5 flex-shrink-0" />
                            {isSidebarOpen && <span>{item.label}</span>}
                        </button>
                    ))}
                </nav>

                <div className="absolute bottom-4 left-0 w-full flex justify-center px-4">
                    <Button variant="ghost" size="icon" onClick={() => setIsSidebarOpen(!isSidebarOpen)} className="w-full">
                        {isSidebarOpen ? (
                            <div className="flex items-center gap-2"><ChevronLeft className="w-4 h-4" /><span className="text-sm">Collapse</span></div>
                        ) : <ChevronRight className="w-4 h-4" />}
                    </Button>
                </div>
            </div>

            {/* Main Content */}
            <div className={`flex-1 transition-all duration-300 ${isSidebarOpen ? 'md:ml-64' : 'md:ml-20'}`}>
                <header className="border-b border-border bg-card sticky top-0 z-40">
                    <div className="px-6 py-4 flex items-center justify-between">
                        <h1 className="text-2xl font-bold">Admin Dashboard</h1>
                        <div className="flex items-center gap-3">
                            <DropdownMenu>
                                <DropdownMenuTrigger asChild>
                                    <Button variant="ghost" className="relative h-10 w-10 rounded-full ring-2 ring-black/10 hover:ring-black/30 dark:ring-white/20 dark:hover:ring-white/50 transition-all">
                                        <Avatar className="h-10 w-10">
                                            <AvatarImage 
                                                className={!currentUser?.avatar_url || ['user.png', 'admin.png', 'tech.png', 'technician.png', 'default'].some(s => currentUser.avatar_url?.includes(s)) ? "dark:invert" : ""}
                                                src={currentUser?.avatar_url?.replace(/\/uploads\/avatars?/, '/public/avatars') || 'http://localhost:3000/public/avatars/admin.png'} 
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
                    </div>
                </header>

                <main className="p-6">
                    {activeTab === 'overview' && <AdminOverview stats={stats} bookings={bookings} setActiveTab={setActiveTab} />}
                    {activeTab === 'bookings' && <BookingManager bookings={bookings} users={users} updateStatus={updateStatus} assignTechnician={assignTechnician} />}
                    {activeTab === 'customers' && <CustomerManager users={users} updateUserRole={updateUserRole} banUser={banUser} unbanUser={unbanUser} />}
                    {activeTab === 'inventory' && <InventoryManager />}
                    {activeTab === 'settings' && <SystemSettings config={config} setConfig={setConfig} saveSettings={saveSettings} />}
                </main>
            </div>
        </div>
    )
}

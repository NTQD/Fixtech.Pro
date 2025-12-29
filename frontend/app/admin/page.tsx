'use client'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input' // Added Input
import Link from 'next/link'
import { Wrench, LogOut, BarChart3, Calendar, Users, Settings, ChevronLeft, ChevronRight, User, DollarSign, ShoppingCart, Activity, CalendarDays, Download, Ban, LockOpen, Search, Package, Plus, Trash, Edit, Star } from 'lucide-react'
import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { toast } from 'sonner' // Added import
import { useTheme } from "next-themes"
import { Switch } from "@/components/ui/switch"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { BarChart, Bar, XAxis, YAxis, ResponsiveContainer } from 'recharts'

import { useConfig } from '@/contexts/ConfigContext'

export default function AdminDashboard() {
  const { refreshConfig } = useConfig()
  const [activeTab, setActiveTab] = useState('overview')
  const [isSidebarOpen, setIsSidebarOpen] = useState(true)
  const router = useRouter()
  const { setTheme } = useTheme()

  // State for Real Data
  const [bookings, setBookings] = useState<any[]>([])
  const [users, setUsers] = useState<any[]>([])
  const [stats, setStats] = useState({
    totalRevenue: 0,
    pendingOrders: 0,
    completedOrders: 0,
    totalCustomers: 0
  })

  // Search & Filter State
  const [bookingSearch, setBookingSearch] = useState('')
  const [bookingSort, setBookingSort] = useState('newest') // newest, name-asc, name-desc, price-asc, price-desc
  const [customerSearch, setCustomerSearch] = useState('')

  // Settings State to simulate persistent config
  const [activeSettingsTab, setActiveSettingsTab] = useState('brand')

  // Inventory State
  const [parts, setParts] = useState<any[]>([])
  const [partSearch, setPartSearch] = useState('')
  const [partSort, setPartSort] = useState('id-asc')
  const [isPartModalOpen, setIsPartModalOpen] = useState(false)
  const [editingPart, setEditingPart] = useState<any>(null)
  const [partForm, setPartForm] = useState({ name: '', price: 0, stock: 0, description: '' })
  const [partToDelete, setPartToDelete] = useState<number | null>(null)

  const [config, setConfig] = useState({
    // Brand
    storeName: 'TechFix Pro',
    phone: '1900 1234',
    email: 'support@techfix.pro',
    address: '123 Đường Công Nghệ, Hà Nội',
    // Interface
    theme: 'system',
    density: 'comfortable',
    notifications: true,
    // Interaction
    announcementBar: true,
    announcementText: '🔥 Giảm giá 50% phí dịch vụ vệ sinh Laptop trong tháng này!',
    welcomeText: 'Chào mừng đến với hệ thống quản lý sửa chữa chuyên nghiệp.'
  })



  const [currentUser, setCurrentUser] = useState<any>(null)

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
    } catch (e) {
      router.push('/login')
    }

    fetchData()
  }, [router])

  const handleLogout = () => {
    localStorage.clear()
    router.push('/login')
  }

  const handleSaveSettings = async () => {
    const token = localStorage.getItem('access_token')
    try {
      const payload = { ...config }
      const res = await fetch('http://localhost:3000/catalog/config', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      })
      if (res.ok) {
        toast.success('Đã lưu cấu hình hệ thống!')
        await refreshConfig()
        fetchData()
      } else {
        toast.error('Lỗi khi lưu cấu hình')
      }
    } catch (e) {
      toast.error('Lỗi kết nối')
    }
  }

  const fetchData = async () => {
    const token = localStorage.getItem('access_token')
    const headers = { 'Authorization': `Bearer ${token}` }
    let fetchedUsers: any[] = []

    try {
      // Fetch Users
      const usersRes = await fetch('http://localhost:3000/users', { headers })
      if (usersRes.ok) {
        fetchedUsers = await usersRes.json()
        setUsers(fetchedUsers)
      }

      // Fetch Bookings
      const bookingsRes = await fetch('http://localhost:3000/bookings', { headers })
      if (bookingsRes.ok) {
        const bookingsData = await bookingsRes.json()
        setBookings(bookingsData)

        // Calculate Stats
        const totalRev = bookingsData.reduce((acc: number, curr: any) => {
          if (curr.status === 'CANCELLED') return acc;
          return acc + Number(curr.total_amount);
        }, 0)
        const pending = bookingsData.filter((b: any) => b.status === 'PENDING').length
        const completed = bookingsData.filter((b: any) => b.status === 'COMPLETED').length

        setStats({
          totalRevenue: totalRev,
          pendingOrders: pending,
          completedOrders: completed,
          totalCustomers: fetchedUsers.length
        })
      }

      // Fetch Parts
      const partsRes = await fetch('http://localhost:3000/catalog/parts', { headers })
      if (partsRes.ok) {
        setParts(await partsRes.json())
      }

      // Fetch System Config
      const configRes = await fetch('http://localhost:3000/catalog/config')
      if (configRes.ok) {
        const fetchedConfig = await configRes.json()
        if (Object.keys(fetchedConfig).length > 0) {
          setConfig(prev => ({
            ...prev, ...fetchedConfig,
            notifications: String(fetchedConfig.notifications) === 'true',
            announcementBar: String(fetchedConfig.announcementBar) === 'true',
          }))
          if (fetchedConfig.theme) setTheme(fetchedConfig.theme)
        }
      }
    } catch (error) {
      console.error("Failed to fetch data", error)
    }
  }

  const handleUpdateStatus = async (bookingId: number, newStatus: string) => {
    const token = localStorage.getItem('access_token')
    try {
      const res = await fetch(`http://localhost:3000/bookings/${bookingId}/status`, {
        method: 'PATCH',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ status: newStatus })
      })

      if (res.ok) {
        fetchData() // Refresh data
        toast.success('Cập nhật trạng thái thành công')
      } else {
        toast.error('Cập nhật thất bại!')
      }
    } catch (error) {
      console.error("Error updating status", error)
    }
  }

  const handleAssignTechnician = async (bookingId: number, technicianId: string) => {
    const token = localStorage.getItem('access_token')
    try {
      const res = await fetch(`http://localhost:3000/bookings/${bookingId}/assign`, {
        method: 'PATCH',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ technicianId: Number(technicianId) })
      })

      if (res.ok) {
        fetchData()
        toast.success('Đã phân công kỹ thuật viên!')
      } else {
        toast.error('Lỗi phân công!')
      }
    } catch (e) {
      console.error("Assign error", e)
    }
  }

  const handleUpdateUserRole = async (userId: number, newRole: string) => {
    try {
      const token = localStorage.getItem('access_token');
      const res = await fetch(`http://localhost:3000/users/${userId}/role`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ role: newRole })
      });

      if (res.ok) {
        toast.success('Cập nhật vai trò thành công!');
        fetchData();
      } else {
        toast.error('Cập nhật thất bại');
      }
    } catch (e) {
      console.error(e);
    }
  }

  const handleBanUser = async (userId: number) => {
    try {
      const token = localStorage.getItem('access_token');
      const res = await fetch(`http://localhost:3000/users/${userId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (res.ok) {
        toast.success('Đã cấm tài khoản thành công!');
        fetchData();
      } else {
        toast.error('Thao tác thất bại');
      }
    } catch (e) {
      console.error(e);
    }
  }

  const handleUnbanUser = async (userId: number) => {
    try {
      const token = localStorage.getItem('access_token');
      const res = await fetch(`http://localhost:3000/users/${userId}/unban`, {
        method: 'PATCH',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (res.ok) {
        toast.success('Đã mở khóa tài khoản thành công!');
        fetchData();
      } else {
        toast.error('Thao tác thất bại');
      }
    } catch (e) {
      console.error(e);
    }
  }

  // --- Inventory Handlers ---
  const openPartModal = (part?: any) => {
    if (part) {
      setEditingPart(part)
      setPartForm({ name: part.name, price: Number(part.price), stock: part.stock, description: part.description || '' })
    } else {
      setEditingPart(null)
      setPartForm({ name: '', price: 0, stock: 0, description: '' })
    }
    setIsPartModalOpen(true)
  }

  const handleSavePart = async () => {
    const token = localStorage.getItem('access_token')
    const headers = { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` }

    try {
      if (editingPart) {
        // Update
        const res = await fetch(`http://localhost:3000/catalog/parts/${editingPart.id}`, {
          method: 'PATCH', headers, body: JSON.stringify(partForm)
        })
        if (res.ok) { toast.success('Cập nhật linh kiện thành công'); fetchData(); setIsPartModalOpen(false) }
        else toast.error('Lỗi khi cập nhật')
      } else {
        // Create
        const res = await fetch(`http://localhost:3000/catalog/parts`, {
          method: 'POST', headers, body: JSON.stringify(partForm)
        })
        if (res.ok) { toast.success('Thêm linh kiện thành công'); fetchData(); setIsPartModalOpen(false) }
        else toast.error('Lỗi khi thêm')
      }
    } catch (e) { toast.error('Lỗi kết nối') }
  }

  const handleDeletePart = (id: number) => {
    setPartToDelete(id)
  }

  const confirmDeletePart = async () => {
    if (!partToDelete) return
    const token = localStorage.getItem('access_token')
    try {
      const res = await fetch(`http://localhost:3000/catalog/parts/${partToDelete}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      })
      if (res.ok) { toast.success('Đã xóa linh kiện'); fetchData() }
      else toast.error('Không thể xóa')
    } catch (e) { console.error(e) }
    setPartToDelete(null)
  }


  // Filtered Logic
  const filteredBookings = bookings.filter(b => {
    const term = bookingSearch.toLowerCase()
    return (
      b.id.toString().includes(term) ||
      b.customer_name?.toLowerCase().includes(term) ||
      b.customer_phone?.includes(term)
    )
  }).sort((a, b) => {
    if (bookingSort === 'name-asc') return a.customer_name.localeCompare(b.customer_name)
    if (bookingSort === 'name-desc') return b.customer_name.localeCompare(a.customer_name)
    if (bookingSort === 'price-asc') return Number(a.total_amount) - Number(b.total_amount)
    if (bookingSort === 'price-desc') return Number(b.total_amount) - Number(a.total_amount)
    return 0 // Default (usually created_at desc from API)
  })

  const filteredUsers = users.filter(u => {
    const term = customerSearch.toLowerCase()
    return (
      u.full_name?.toLowerCase().includes(term) ||
      u.email?.toLowerCase().includes(term)
    )
  })

  const filteredParts = parts.filter(p =>
    p.name.toLowerCase().includes(partSearch.toLowerCase())
  ).sort((a, b) => {
    if (partSort === 'price-asc') return Number(a.price) - Number(b.price)
    if (partSort === 'price-desc') return Number(b.price) - Number(a.price)
    return a.id - b.id
  })

  // Chart Data (Derived from real bookings)
  const chartData = [
    { name: 'Th2', revenue: 0 },
    { name: 'Th3', revenue: 0 },
    { name: 'Th4', revenue: 0 },
    { name: 'Th5', revenue: 0 },
    { name: 'Th6', revenue: 0 },
    { name: 'Th7', revenue: 0 },
    { name: 'CN', revenue: 0 },
  ]

  return (
    <div className="min-h-screen bg-background flex">
      {/* Sidebar */}
      <div
        className={`fixed left-0 top-0 h-screen bg-card border-r border-border transition-all duration-300 z-50 hidden md:block
          ${isSidebarOpen ? 'w-64' : 'w-20'}
        `}
      >
        <div className="p-6 border-b border-border flex items-center justify-between">
          <div className={`flex items-center gap-2 ${!isSidebarOpen && 'justify-center w-full'}`}>
            <div className="w-10 h-10 bg-primary rounded-lg flex items-center justify-center flex-shrink-0">
              <Wrench className="w-6 h-6 text-primary-foreground" />
            </div>
            {isSidebarOpen && <span className="font-bold text-lg whitespace-nowrap">TechFix Admin</span>}
          </div>
        </div>

        <nav className="p-4 space-y-2">
          <button
            onClick={() => setActiveTab('overview')}
            className={`w-full flex items-center gap-3 px-4 py-2 rounded-lg transition ${activeTab === 'overview'
              ? 'bg-primary text-primary-foreground'
              : 'text-foreground hover:bg-muted'
              } ${!isSidebarOpen && 'justify-center px-2'}`}
          >
            <BarChart3 className="w-5 h-5 flex-shrink-0" />
            {isSidebarOpen && <span>Tổng quan</span>}
          </button>

          <button
            onClick={() => setActiveTab('bookings')}
            className={`w-full flex items-center gap-3 px-4 py-2 rounded-lg transition ${activeTab === 'bookings'
              ? 'bg-primary text-primary-foreground'
              : 'text-foreground hover:bg-muted'
              } ${!isSidebarOpen && 'justify-center px-2'}`}
          >
            <Calendar className="w-5 h-5 flex-shrink-0" />
            {isSidebarOpen && <span>Quản lý Đặt lịch</span>}
          </button>

          <button
            onClick={() => setActiveTab('customers')}
            className={`w-full flex items-center gap-3 px-4 py-2 rounded-lg transition ${activeTab === 'customers'
              ? 'bg-primary text-primary-foreground'
              : 'text-foreground hover:bg-muted'
              } ${!isSidebarOpen && 'justify-center px-2'}`}
          >
            <Users className="w-5 h-5 flex-shrink-0" />
            {isSidebarOpen && <span>Khách hàng</span>}
          </button>

          <button
            onClick={() => setActiveTab('inventory')}
            className={`w-full flex items-center gap-3 px-4 py-2 rounded-lg transition ${activeTab === 'inventory'
              ? 'bg-primary text-primary-foreground'
              : 'text-foreground hover:bg-muted'
              } ${!isSidebarOpen && 'justify-center px-2'}`}
          >
            <Package className="w-5 h-5 flex-shrink-0" />
            {isSidebarOpen && <span>Kho hàng</span>}
          </button>
          <button
            onClick={() => setActiveTab('settings')}
            className={`w-full flex items-center gap-3 px-4 py-2 rounded-lg transition ${activeTab === 'settings'
              ? 'bg-primary text-primary-foreground'
              : 'text-foreground hover:bg-muted'
              } ${!isSidebarOpen && 'justify-center px-2'}`}
          >
            <Settings className="w-5 h-5 flex-shrink-0" />
            {isSidebarOpen && <span>Cài đặt</span>}
          </button>
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
      </div>

      {/* Main Content */}
      <div className={`flex-1 transition-all duration-300 ${isSidebarOpen ? 'md:ml-64' : 'md:ml-20'}`}>
        {/* Header */}
        <header className="border-b border-border bg-card sticky top-0 z-40">
          <div className="px-6 py-4 flex items-center justify-between">
            <h1 className="text-2xl font-bold">Admin Dashboard</h1>
            <div className="flex items-center gap-3">
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="ghost" className="relative h-10 w-10 rounded-full ring-2 ring-black/10 hover:ring-black/30 dark:ring-white dark:hover:ring-white/90 transition-all">
                    <Avatar className="h-10 w-10">
                      <AvatarImage
                        className={!currentUser?.avatar_url || ['user.png', 'admin.png', 'tech.png', 'technician.png', 'default'].some(s => currentUser.avatar_url?.includes(s)) ? "dark:invert" : ""}
                        src={currentUser?.avatar_url?.replace(/\/uploads\/avatars?/, '/public/avatars') || (currentUser?.role === 'ADMIN' ? 'http://localhost:3000/public/avatars/admin.png' : "/avatars/admin.png")}
                        alt="@admin"
                        onError={(e) => e.currentTarget.src = "/placeholder-user.jpg"}
                      />
                      <AvatarFallback className="bg-primary/10 text-primary font-bold">{currentUser?.email?.[0].toUpperCase() || 'A'}</AvatarFallback>
                    </Avatar>
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent className="w-56" align="end" forceMount>
                  <DropdownMenuLabel className="font-normal">
                    <div className="flex flex-col space-y-1">
                      <p className="text-sm font-medium leading-none">{currentUser?.name || currentUser?.full_name || 'Administrator'}</p>
                      <p className="text-xs leading-none text-muted-foreground">
                        {currentUser?.email}
                      </p>
                    </div>
                  </DropdownMenuLabel>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem asChild>
                    <Link href="/profile" className="cursor-pointer">
                      <User className="mr-2 h-4 w-4" />
                      <span>Hồ sơ Admin</span>
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
          </div>
        </header>

        {/* Content */}
        <main className="p-6">
          {activeTab === 'overview' && (
            <div className="space-y-6">
              <div className="grid md:grid-cols-4 gap-4">
                <Card>
                  <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle className="text-sm font-medium">Tổng doanh thu</CardTitle>
                    <DollarSign className="h-4 w-4 text-muted-foreground" />
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold">{stats.totalRevenue.toLocaleString()} đ</div>
                    <p className="text-xs text-muted-foreground">Tổng doanh thu hệ thống</p>
                  </CardContent>
                </Card>
                <Card>
                  <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle className="text-sm font-medium">Đơn hàng mới</CardTitle>
                    <ShoppingCart className="h-4 w-4 text-muted-foreground" />
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold">{stats.pendingOrders}</div>
                    <p className="text-xs text-muted-foreground">Đang chờ xử lý</p>
                  </CardContent>
                </Card>
                <Card>
                  <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle className="text-sm font-medium">Đã hoàn thành</CardTitle>
                    <Activity className="h-4 w-4 text-muted-foreground" />
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold">{stats.completedOrders}</div>
                    <p className="text-xs text-muted-foreground">Đơn hàng đã xong</p>
                  </CardContent>
                </Card>
                <Card>
                  <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle className="text-sm font-medium">Khách hàng</CardTitle>
                    <Users className="h-4 w-4 text-muted-foreground" />
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold">{stats.totalCustomers}</div>
                    <p className="text-xs text-muted-foreground">Tổng số tài khoản</p>
                  </CardContent>
                </Card>
              </div>

              <div className="bg-card p-6 rounded-lg border border-border">
                <div className="flex items-center justify-between mb-4">
                  <h2 className="text-xl font-bold">Đặt lịch gần đây</h2>
                  <Button variant="outline" size="sm" onClick={() => setActiveTab('bookings')}>Xem tất cả</Button>
                </div>
                <div className="overflow-x-auto">
                  <table className="w-full text-sm">
                    <thead className="border-b border-border">
                      <tr>
                        <th className="text-left py-3 px-4">Khách hàng</th>
                        <th className="text-left py-3 px-4">Thiết bị</th>
                        <th className="text-left py-3 px-4">Ngày hẹn</th>
                        <th className="text-left py-3 px-4">Trạng thái</th>
                        <th className="text-left py-3 px-4">Tổng tiền</th>
                      </tr>
                    </thead>
                    <tbody>
                      {bookings.slice(0, 5).map((booking) => (
                        <tr key={booking.id} className="border-b border-border hover:bg-muted/50">
                          <td className="py-3 px-4">
                            <div>
                              <div className="font-medium">{booking.customer_name}</div>
                              <div className="text-xs text-muted-foreground">{booking.customer_phone}</div>
                            </div>
                          </td>
                          <td className="py-3 px-4">{booking.device_info}</td>
                          <td className="py-3 px-4">{booking.scheduled_date}</td>
                          <td className="py-3 px-4">
                            <span className={`px-3 py-1 rounded-full text-xs font-medium 
                                ${booking.status === 'COMPLETED' ? 'bg-green-100 text-green-800' :
                                booking.status === 'IN_PROGRESS' ? 'bg-blue-100 text-blue-800' :
                                  'bg-yellow-100 text-yellow-800'}`}>
                              {booking.status}
                            </span>
                          </td>
                          <td className="py-3 px-4">{Number(booking.total_amount).toLocaleString()} đ</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'bookings' && (
            <div className="bg-card p-6 rounded-lg border border-border">
              <div className="flex items-center justify-between mb-6">
                <div>
                  <h2 className="text-2xl font-bold mb-1">Quản lý đặt lịch</h2>
                  <p className="text-muted-foreground">Sử dụng menu trên để xem danh sách tất cả đặt lịch</p>
                </div>
                <div className="flex items-center gap-2">
                  <div className="relative">
                    <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
                    <Input
                      type="search"
                      placeholder="Tìm theo Tên, SĐT..."
                      className="w-[250px] pl-9"
                      value={bookingSearch}
                      onChange={(e) => setBookingSearch(e.target.value)}
                    />
                  </div>
                  <Select value={bookingSort} onValueChange={setBookingSort}>
                    <SelectTrigger className="w-[180px]">
                      <SelectValue placeholder="Sắp xếp" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="newest">Mới nhất</SelectItem>
                      <SelectItem value="name-asc">Tên (A-Z)</SelectItem>
                      <SelectItem value="name-desc">Tên (Z-A)</SelectItem>
                      <SelectItem value="price-asc">Giá (Thấp - Cao)</SelectItem>
                      <SelectItem value="price-desc">Giá (Cao - Thấp)</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>

              {/* Full Bookings Table */}
              <div className="rounded-md border bg-card">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Mã đơn</TableHead>
                      <TableHead>Khách hàng</TableHead>
                      <TableHead>Thiết bị</TableHead>
                      <TableHead>Kỹ thuật viên</TableHead>
                      <TableHead>Trạng thái</TableHead>
                      <TableHead className="text-right">Tổng tiền</TableHead>
                      <TableHead className="text-right">Hành động</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {filteredBookings.map((booking: any) => (
                      <TableRow key={booking.id}>
                        <TableCell className="font-medium">#{booking.id}</TableCell>
                        <TableCell>
                          <div>
                            <div className="font-medium">{booking.customer_name}</div>
                            <div className="text-xs text-muted-foreground">{booking.customer_phone}</div>
                          </div>
                        </TableCell>
                        <TableCell>{booking.device_info}</TableCell>
                        <TableCell>
                          <Select
                            value={booking.technician?.id ? booking.technician.id.toString() : ''}
                            onValueChange={(val) => handleAssignTechnician(booking.id, val)}
                          >
                            <SelectTrigger className="w-[180px] h-8 text-xs">
                              <SelectValue placeholder="Chưa phân công" />
                            </SelectTrigger>
                            <SelectContent>
                              {users.filter((u: any) => u.role === 'TECHNICIAN').map((tech: any) => (
                                <SelectItem key={tech.id} value={tech.id.toString()}>
                                  {tech.full_name} ({tech.email})
                                </SelectItem>
                              ))}
                            </SelectContent>
                          </Select>
                        </TableCell>
                        <TableCell>
                          <div className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium 
                                          ${booking.status === 'COMPLETED' ? 'bg-green-100 text-green-800' :
                              booking.status === 'IN_PROGRESS' ? 'bg-blue-100 text-blue-800' :
                                'bg-yellow-100 text-yellow-800'}`}>
                            {booking.status}
                          </div>
                        </TableCell>
                        <TableCell className="text-right">{Number(booking.total_amount).toLocaleString()} đ</TableCell>
                        <TableCell className="text-right">
                          <div className="flex justify-end gap-2">
                            <Button variant="outline" size="sm" onClick={() => handleUpdateStatus(booking.id, 'IN_PROGRESS')}>Xử lý</Button>
                            <Button variant="ghost" size="sm" onClick={() => handleUpdateStatus(booking.id, 'COMPLETED')}>Xong</Button>
                          </div>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            </div>
          )}

          {activeTab === 'customers' && (
            <div className="bg-card p-6 rounded-lg border border-border">
              <div className="flex items-center justify-between mb-4">
                <div>
                  <h2 className="text-2xl font-bold mb-1">Quản lý khách hàng</h2>
                  <p className="text-muted-foreground">Danh sách tài khoản đã đăng ký trên hệ thống.</p>
                </div>
                <div className="relative">
                  <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
                  <Input
                    type="search"
                    placeholder="Tìm Tên hoặc Email..."
                    className="w-[250px] pl-9"
                    value={customerSearch}
                    onChange={(e) => setCustomerSearch(e.target.value)}
                  />
                </div>
              </div>

              <div className="rounded-md border">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>ID</TableHead>
                      <TableHead>Tên đầy đủ</TableHead>
                      <TableHead>Email</TableHead>
                      <TableHead>Vai trò</TableHead>
                      <TableHead>Uy tín</TableHead>
                      <TableHead className="text-right">Hành động</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {filteredUsers.map((u: any) => (
                      <TableRow key={u.id} className={u.status === 0 ? 'opacity-50 bg-muted/50' : ''}>
                        <TableCell>{u.id}</TableCell>
                        <TableCell className="font-medium">{u.full_name}</TableCell>
                        <TableCell>
                          <div>{u.email}</div>
                          {u.status === 0 && <span className="text-xs text-red-500 font-bold">Bị cấm</span>}
                        </TableCell>
                        <TableCell>
                          <Select
                            disabled={u.status === 0 || u.role === 'ADMIN'}
                            value={u.role}
                            onValueChange={(val) => handleUpdateUserRole(u.id, val)}
                          >
                            <SelectTrigger className="w-[140px] h-8">
                              <SelectValue />
                            </SelectTrigger>
                            <SelectContent>
                              <SelectItem value="CUSTOMER">CUSTOMER</SelectItem>
                              <SelectItem value="TECHNICIAN">TECHNICIAN</SelectItem>
                              <SelectItem value="ADMIN">ADMIN</SelectItem>
                            </SelectContent>
                          </Select>
                        </TableCell>
                        <TableCell>
                          {u.role === 'TECHNICIAN' ? (
                            <div className="flex items-center gap-1 font-medium text-amber-600">
                              <Star className="w-4 h-4 fill-current" />
                              <span>{u.reputation_score || 0}</span>
                              <span className="text-muted-foreground text-xs">({u.total_rated_orders || 0})</span>
                            </div>
                          ) : '-'}
                        </TableCell>
                        <TableCell className="text-right">
                          {u.role !== 'ADMIN' && (
                            u.status !== 0 ? (
                              <Button
                                variant="ghost"
                                size="icon"
                                className="text-red-500 hover:text-red-700 hover:bg-red-100"
                                onClick={() => {
                                  toast(`Cấm tài khoản ${u.email}?`, {
                                    description: 'Hành động này sẽ ngăn người dùng đăng nhập.',
                                    action: {
                                      label: 'Xác nhận',
                                      onClick: () => handleBanUser(u.id)
                                    },
                                    cancel: {
                                      label: 'Hủy',
                                      onClick: () => { }
                                    }
                                  })
                                }}
                              >
                                <Ban className="h-4 w-4" />
                              </Button>
                            ) : (
                              <Button
                                variant="ghost"
                                size="icon"
                                className="text-green-500 hover:text-green-700 hover:bg-green-100"
                                onClick={() => handleUnbanUser(u.id)}
                              >
                                <LockOpen className="h-4 w-4" />
                              </Button>
                            )
                          )}
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            </div>
          )}

          {activeTab === 'inventory' && (
            <div className="bg-card p-6 rounded-lg border border-border">
              <div className="flex items-center justify-between mb-6">
                <div>
                  <h2 className="text-2xl font-bold mb-1">Quản lý kho hàng</h2>
                  <p className="text-muted-foreground">Quản lý danh sách linh kiện và giá cả.</p>
                </div>
                <div className="flex items-center gap-2">
                  <div className="relative">
                    <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
                    <Input
                      placeholder="Tìm tên linh kiện..."
                      className="w-[250px] pl-9"
                      value={partSearch}
                      onChange={(e) => setPartSearch(e.target.value)}
                    />
                  </div>
                  <Select value={partSort} onValueChange={setPartSort}>
                    <SelectTrigger className="w-[180px]">
                      <SelectValue placeholder="Sắp xếp" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="id-asc">Mặc định</SelectItem>
                      <SelectItem value="price-asc">Giá tăng dần</SelectItem>
                      <SelectItem value="price-desc">Giá giảm dần</SelectItem>
                    </SelectContent>
                  </Select>
                  <Button onClick={() => openPartModal()}>
                    <Plus className="h-4 w-4 mr-2" /> Thêm linh kiện
                  </Button>
                </div>
              </div>

              <div className="rounded-md border">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>ID</TableHead>
                      <TableHead>Tên linh kiện</TableHead>
                      <TableHead>Mô tả</TableHead>
                      <TableHead className="text-right">Giá bán</TableHead>
                      <TableHead className="text-right">Tồn kho</TableHead>
                      <TableHead className="text-right">Hành động</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {filteredParts.map((part) => (
                      <TableRow key={part.id}>
                        <TableCell>#{part.id}</TableCell>
                        <TableCell className="font-medium">{part.name}</TableCell>
                        <TableCell className="text-muted-foreground text-sm truncate max-w-[200px]">{part.description}</TableCell>
                        <TableCell className="text-right">{Number(part.price).toLocaleString()} đ</TableCell>
                        <TableCell className="text-right">{part.stock}</TableCell>
                        <TableCell className="text-right">
                          <div className="flex justify-end gap-2">
                            <Button variant="ghost" size="icon" onClick={() => openPartModal(part)}>
                              <Edit className="h-4 w-4 text-blue-500" />
                            </Button>
                            <Button variant="ghost" size="icon" onClick={() => handleDeletePart(part.id)}>
                              <Trash className="h-4 w-4 text-red-500" />
                            </Button>
                          </div>
                        </TableCell>
                      </TableRow>
                    ))}
                    {filteredParts.length === 0 && (
                      <TableRow>
                        <TableCell colSpan={6} className="text-center py-8 text-muted-foreground">Không tìm thấy linh kiện nào.</TableCell>
                      </TableRow>
                    )}
                  </TableBody>
                </Table>
              </div>
            </div>
          )}

          {activeTab === 'settings' && (
            <div className="space-y-6">
              <h2 className="text-2xl font-bold">Cài đặt hệ thống</h2>

              <Tabs value={activeSettingsTab} onValueChange={setActiveSettingsTab} className="w-full">
                <TabsList>
                  <TabsTrigger value="brand">Cấu hình thương hiệu</TabsTrigger>
                  <TabsTrigger value="interface">Cấu hình giao diện</TabsTrigger>
                  <TabsTrigger value="interaction">Cấu hình tương tác</TabsTrigger>
                </TabsList>

                {/* BRAND CONFIG */}
                <TabsContent value="brand" className="space-y-4 py-4">
                  <div className="grid md:grid-cols-2 gap-6">
                    <Card>
                      <CardHeader>
                        <CardTitle>Thông tin cửa hàng</CardTitle>
                        <CardDescription>Thông tin liên hệ hiển thị trên Website.</CardDescription>
                      </CardHeader>
                      <CardContent className="space-y-4">
                        <div className="space-y-2">
                          <Label>Tên cửa hàng</Label>
                          <Input value={config.storeName} onChange={(e) => setConfig({ ...config, storeName: e.target.value })} />
                        </div>
                        <div className="space-y-2">
                          <Label>Hotline</Label>
                          <Input value={config.phone} onChange={(e) => setConfig({ ...config, phone: e.target.value })} />
                        </div>
                        <div className="space-y-2">
                          <Label>Email hỗ trợ</Label>
                          <Input value={config.email} onChange={(e) => setConfig({ ...config, email: e.target.value })} />
                        </div>
                        <div className="space-y-2">
                          <Label>Địa chỉ</Label>
                          <Input value={config.address} onChange={(e) => setConfig({ ...config, address: e.target.value })} />
                        </div>
                      </CardContent>
                    </Card>

                    <Card>
                      <CardHeader>
                        <CardTitle>Hình ảnh thương hiệu</CardTitle>
                        <CardDescription>Logo và Favicon.</CardDescription>
                      </CardHeader>
                      <CardContent className="space-y-4">
                        <div className="space-y-2">
                          <Label>Logo Website</Label>
                          <div className="flex items-center gap-4">
                            <div className="w-16 h-16 bg-muted rounded-lg flex items-center justify-center border">
                              <span className="text-xs text-muted-foreground">Logo</span>
                            </div>
                            <Input type="file" className="cursor-pointer" />
                          </div>
                        </div>
                        <div className="space-y-2">
                          <Label>Favicon</Label>
                          <div className="flex items-center gap-4">
                            <div className="w-10 h-10 bg-muted rounded flex items-center justify-center border">
                              <span className="text-[10px] text-muted-foreground">Icon</span>
                            </div>
                            <Input type="file" className="cursor-pointer" />
                          </div>
                        </div>
                      </CardContent>
                    </Card>
                  </div>
                </TabsContent>

                {/* INTERFACE CONFIG */}
                <TabsContent value="interface" className="space-y-4 py-4">
                  <Card>
                    <CardHeader>
                      <CardTitle>Giao diện & Trải nghiệm</CardTitle>
                    </CardHeader>
                    <CardContent className="space-y-6">
                      <div className="flex items-center justify-between">
                        <div className="space-y-0.5">
                          <Label className="text-base">Chế độ tối (Dark Mode)</Label>
                          <p className="text-sm text-muted-foreground">Thiết lập giao diện mặc định cho người dùng.</p>
                        </div>
                        <Select value={config.theme} onValueChange={(val) => {
                          setConfig({ ...config, theme: val })
                          setTheme(val)
                        }}>
                          <SelectTrigger className="w-[180px]">
                            <SelectValue placeholder="Theme" />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectItem value="light">Sáng (Light)</SelectItem>
                            <SelectItem value="dark">Tối (Dark)</SelectItem>
                            <SelectItem value="system">Theo hệ thống</SelectItem>
                          </SelectContent>
                        </Select>
                      </div>

                      <div className="flex items-center justify-between">
                        <div className="space-y-0.5">
                          <Label className="text-base">Mật độ hiển thị</Label>
                          <p className="text-sm text-muted-foreground">Độ giãn dòng của bảng dữ liệu.</p>
                        </div>
                        <Select value={config.density} onValueChange={(val) => setConfig({ ...config, density: val })}>
                          <SelectTrigger className="w-[180px]">
                            <SelectValue placeholder="Density" />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectItem value="compact">Nhỏ gọn (Compact)</SelectItem>
                            <SelectItem value="comfortable">Thoải mái (Default)</SelectItem>
                          </SelectContent>
                        </Select>
                      </div>

                      <div className="flex items-center justify-between">
                        <div className="space-y-0.5">
                          <Label className="text-base">Âm thanh thông báo</Label>
                          <p className="text-sm text-muted-foreground">Phát âm thanh khi có đơn hàng mới.</p>
                        </div>
                        <Switch checked={config.notifications} onCheckedChange={(checked) => setConfig({ ...config, notifications: checked })} />
                      </div>
                    </CardContent>
                  </Card>
                </TabsContent>

                {/* INTERACTION CONFIG */}
                <TabsContent value="interaction" className="space-y-4 py-4">
                  <Card>
                    <CardHeader>
                      <CardTitle>Nội dung động</CardTitle>
                      <CardDescription>Quản lý các thông điệp hiển thị trên website.</CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-6">
                      <div className="flex flex-col space-y-4">
                        <div className="flex items-center justify-between">
                          <div className="space-y-0.5">
                            <Label className="text-base">Thanh thông báo (Announcement Bar)</Label>
                            <p className="text-sm text-muted-foreground">Hiển thị dòng thông báo trên cùng của trang web.</p>
                          </div>
                          <Switch checked={config.announcementBar} onCheckedChange={(checked) => setConfig({ ...config, announcementBar: checked })} />
                        </div>
                        {config.announcementBar && (
                          <Input
                            value={config.announcementText}
                            onChange={(e) => setConfig({ ...config, announcementText: e.target.value })}
                            placeholder="Nhập nội dung thông báo..."
                          />
                        )}
                      </div>

                      <div className="space-y-2 pt-4 border-t">
                        <Label>Tiêu đề chào mừng (Trang chủ)</Label>
                        <Textarea
                          value={config.welcomeText}
                          onChange={(e) => setConfig({ ...config, welcomeText: e.target.value })}
                          rows={3}
                        />
                      </div>
                    </CardContent>
                  </Card>
                </TabsContent>
              </Tabs>

              <div className="flex justify-end gap-4">
                <Button variant="outline" onClick={() => toast.info('Đã hủy thay đổi.')}>Hủy bỏ</Button>
                <Button onClick={handleSaveSettings}>Lưu cấu hình</Button>
              </div>
            </div>
          )}
        </main>
      </div>

      {/* Part Modal */}
      <Dialog open={isPartModalOpen} onOpenChange={setIsPartModalOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{editingPart ? 'Cập nhật linh kiện' : 'Thêm linh kiện mới'}</DialogTitle>
            <DialogDescription>Nhập thông tin chi tiết cho linh kiện.</DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label>Tên linh kiện</Label>
              <Input value={partForm.name} onChange={(e) => setPartForm({ ...partForm, name: e.target.value })} placeholder="Ví dụ: RAM 8GB DDR4" />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label>Giá bán (VNĐ)</Label>
                <Input type="number" value={partForm.price} onChange={(e) => setPartForm({ ...partForm, price: Number(e.target.value) })} />
              </div>
              <div className="space-y-2">
                <Label>Số lượng tồn</Label>
                <Input type="number" value={partForm.stock} onChange={(e) => setPartForm({ ...partForm, stock: Number(e.target.value) })} />
              </div>
            </div>
            <div className="space-y-2">
              <Label>Mô tả</Label>
              <Textarea value={partForm.description} onChange={(e) => setPartForm({ ...partForm, description: e.target.value })} placeholder="Thông tin kỹ thuật..." />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsPartModalOpen(false)}>Hủy</Button>
            <Button onClick={handleSavePart}>Lưu</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Delete Alert */}
      <AlertDialog open={!!partToDelete} onOpenChange={(open) => !open && setPartToDelete(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Bạn có chắc chắn muốn xóa?</AlertDialogTitle>
            <AlertDialogDescription>
              Hành động này không thể hoàn tác. Linh kiện sẽ bị xóa vĩnh viễn khỏi hệ thống.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Hủy bỏ</AlertDialogCancel>
            <AlertDialogAction onClick={confirmDeletePart} className="bg-red-600 hover:bg-red-700">Xóa linh kiện</AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div >
  )
}

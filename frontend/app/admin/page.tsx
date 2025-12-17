'use client'

import { Button } from '@/components/ui/button'
import Link from 'next/link'
import { Wrench, LogOut, BarChart3, Calendar, Users, Settings, ChevronLeft, ChevronRight, User, DollarSign, ShoppingCart, Activity, CalendarDays, Download } from 'lucide-react'
import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
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

export default function AdminDashboard() {
  const [activeTab, setActiveTab] = useState('overview')
  const [isSidebarOpen, setIsSidebarOpen] = useState(true)
  const router = useRouter()

  // State for Real Data
  const [bookings, setBookings] = useState<any[]>([])
  const [users, setUsers] = useState<any[]>([])
  const [stats, setStats] = useState({
    totalRevenue: 0,
    pendingOrders: 0,
    completedOrders: 0,
    totalCustomers: 0
  })

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
    } catch (e) {
      router.push('/login')
    }

    fetchData()
  }, [router])

  const handleLogout = () => {
    localStorage.clear()
    router.push('/login')
  }

  const fetchData = async () => {
    const token = localStorage.getItem('access_token')
    const headers = { 'Authorization': `Bearer ${token}` }

    try {
      // Fetch Users
      const usersRes = await fetch('http://localhost:3000/users', { headers })
      if (usersRes.ok) {
        const usersData = await usersRes.json()
        setUsers(usersData)
      }

      // Fetch Bookings
      const bookingsRes = await fetch('http://localhost:3000/bookings', { headers })
      if (bookingsRes.ok) {
        const bookingsData = await bookingsRes.json()
        setBookings(bookingsData)

        // Calculate Stats
        const totalRev = bookingsData.reduce((acc: number, curr: any) => acc + Number(curr.total_amount), 0)
        const pending = bookingsData.filter((b: any) => b.status === 'PENDING').length
        const completed = bookingsData.filter((b: any) => b.status === 'COMPLETED').length

        setStats({
          totalRevenue: totalRev,
          pendingOrders: pending,
          completedOrders: completed,
          totalCustomers: users.length // approximates
        })
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
      } else {
        alert('Cập nhật thất bại!')
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
        alert('Đã phân công kỹ thuật viên!')
      } else {
        alert('Lỗi phân công!')
      }
    } catch (e) {
      console.error("Assign error", e)
    }
  }

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
                  <Button variant="ghost" className="relative h-10 w-10 rounded-full">
                    <Avatar className="h-10 w-10">
                      <AvatarImage src="/avatars/admin.png" alt="@admin" />
                      <AvatarFallback className="bg-primary/10 text-primary font-bold">AD</AvatarFallback>
                    </Avatar>
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent className="w-56" align="end" forceMount>
                  <DropdownMenuLabel className="font-normal">
                    <div className="flex flex-col space-y-1">
                      <p className="text-sm font-medium leading-none">Administrator</p>
                      <p className="text-xs leading-none text-muted-foreground">
                        admin@techfix.pro
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
                <Button>
                  <Calendar className="w-4 h-4 mr-2" />
                  Đặt lịch mới
                </Button>
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
                    {bookings.map((booking: any) => (
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
              <h2 className="text-2xl font-bold mb-4">Quản lý khách hàng</h2>
              <p className="text-muted-foreground mb-4">Danh sách tài khoản đã đăng ký trên hệ thống.</p>

              <div className="rounded-md border">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>ID</TableHead>
                      <TableHead>Email</TableHead>
                      <TableHead>Vai trò</TableHead>
                      <TableHead>Họ tên</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {users.map((u: any) => (
                      <TableRow key={u.id}>
                        <TableCell>{u.id}</TableCell>
                        <TableCell>{u.email}</TableCell>
                        <TableCell>{u.role}</TableCell>
                        <TableCell>{u.first_name} {u.last_name}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            </div>
          )}

          {activeTab === 'settings' && (
            <div className="bg-card p-6 rounded-lg border border-border">
              <h2 className="text-2xl font-bold mb-4">Cài đặt</h2>
              <p className="text-muted-foreground">Cấu hình ứng dụng</p>
            </div>
          )}
        </main>
      </div>
    </div>
  )
}

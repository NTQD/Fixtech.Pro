'use client'

import { Button } from '@/components/ui/button'
import Link from 'next/link'
import { ArrowLeft, Search, Filter, Trash2, Check, Clock, X } from 'lucide-react'
import { useState } from 'react'

export default function AdminBookingsPage() {
  const [searchTerm, setSearchTerm] = useState('')
  const [filterStatus, setFilterStatus] = useState('all')

  // Mock data - lịch đặt từ khách hàng
  const allBookings = [
    { 
      id: 1, 
      name: 'Nguyễn Văn A', 
      phone: '0912345678',
      email: 'nguyenvana@example.com',
      service: 'Sửa chữa Mainboard', 
      date: '2025-01-15',
      time: '10:00',
      status: 'pending',
      notes: 'Laptop không khởi động được'
    },
    { 
      id: 2, 
      name: 'Trần Thị B', 
      phone: '0987654321',
      email: 'tranthib@example.com',
      service: 'Nâng cấp RAM', 
      date: '2025-01-14',
      time: '14:30',
      status: 'completed',
      notes: 'Nâng cấp từ 8GB lên 16GB'
    },
    { 
      id: 3, 
      name: 'Phạm Văn C', 
      phone: '0901234567',
      email: 'phamvanc@example.com',
      service: 'Vệ sinh Laptop', 
      date: '2025-01-13',
      time: '09:00',
      status: 'completed',
      notes: 'Vệ sinh toàn bộ'
    },
    { 
      id: 4, 
      name: 'Lê Thị D', 
      phone: '0945678901',
      email: 'lethid@example.com',
      service: 'Thay thế Pin', 
      date: '2025-01-20',
      time: '15:00',
      status: 'pending',
      notes: 'Pin laptop yếu'
    },
    { 
      id: 5, 
      name: 'Hoàng Văn E', 
      phone: '0923456789',
      email: 'hoangvane@example.com',
      service: 'Cài đặt Windows', 
      date: '2025-01-19',
      time: '11:00',
      status: 'confirmed',
      notes: 'Cài Windows 11 Pro'
    },
    { 
      id: 6, 
      name: 'Vũ Thị F', 
      phone: '0956789012',
      email: 'vuthif@example.com',
      service: 'Sửa chữa Bàn phím', 
      date: '2025-01-16',
      time: '13:30',
      status: 'pending',
      notes: 'Một số phím không hoạt động'
    },
  ]

  // Filter bookings
  const filteredBookings = allBookings.filter(booking => {
    const matchSearch = 
      booking.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      booking.phone.includes(searchTerm) ||
      booking.service.toLowerCase().includes(searchTerm.toLowerCase())
    
    const matchStatus = filterStatus === 'all' || booking.status === filterStatus
    
    return matchSearch && matchStatus
  })

  const getStatusColor = (status: string) => {
    switch(status) {
      case 'pending':
        return 'bg-yellow-100 text-yellow-800'
      case 'confirmed':
        return 'bg-blue-100 text-blue-800'
      case 'completed':
        return 'bg-green-100 text-green-800'
      case 'cancelled':
        return 'bg-red-100 text-red-800'
      default:
        return 'bg-gray-100 text-gray-800'
    }
  }

  const getStatusLabel = (status: string) => {
    switch(status) {
      case 'pending':
        return 'Chờ xử lý'
      case 'confirmed':
        return 'Đã xác nhận'
      case 'completed':
        return 'Hoàn thành'
      case 'cancelled':
        return 'Hủy'
      default:
        return status
    }
  }

  const getStatusIcon = (status: string) => {
    switch(status) {
      case 'pending':
        return <Clock className="w-4 h-4" />
      case 'confirmed':
        return <Check className="w-4 h-4" />
      case 'completed':
        return <Check className="w-4 h-4" />
      case 'cancelled':
        return <X className="w-4 h-4" />
      default:
        return null
    }
  }

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="border-b border-border bg-card sticky top-0 z-40">
        <div className="px-6 py-4 flex items-center gap-4">
          <Link href="/admin">
            <Button variant="ghost" size="sm">
              <ArrowLeft className="w-4 h-4" />
            </Button>
          </Link>
          <h1 className="text-2xl font-bold">Quản lý Đặt lịch</h1>
        </div>
      </header>

      {/* Content */}
      <main className="p-6 max-w-7xl mx-auto">
        {/* Search and Filter */}
        <div className="mb-6 space-y-4">
          <div className="flex flex-col md:flex-row gap-4">
            {/* Search */}
            <div className="flex-1 relative">
              <Search className="absolute left-3 top-3 w-5 h-5 text-muted-foreground" />
              <input
                type="text"
                placeholder="Tìm kiếm theo tên, số điện thoại hoặc dịch vụ..."
                className="w-full pl-10 pr-4 py-2 rounded-lg border border-border bg-background text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>

            {/* Filter Status */}
            <div className="flex gap-2">
              <Filter className="w-5 h-5 text-muted-foreground mt-2.5" />
              <select
                value={filterStatus}
                onChange={(e) => setFilterStatus(e.target.value)}
                className="px-4 py-2 rounded-lg border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary cursor-pointer"
              >
                <option value="all">Tất cả trạng thái</option>
                <option value="pending">Chờ xử lý</option>
                <option value="confirmed">Đã xác nhận</option>
                <option value="completed">Hoàn thành</option>
                <option value="cancelled">Hủy</option>
              </select>
            </div>
          </div>

          {/* Stats */}
          <div className="grid md:grid-cols-4 gap-4">
            <div className="bg-card p-4 rounded-lg border border-border">
              <p className="text-sm text-muted-foreground mb-1">Tổng cộng</p>
              <p className="text-2xl font-bold">{allBookings.length}</p>
            </div>
            <div className="bg-card p-4 rounded-lg border border-border">
              <p className="text-sm text-muted-foreground mb-1">Chờ xử lý</p>
              <p className="text-2xl font-bold text-yellow-600">{allBookings.filter(b => b.status === 'pending').length}</p>
            </div>
            <div className="bg-card p-4 rounded-lg border border-border">
              <p className="text-sm text-muted-foreground mb-1">Đã xác nhận</p>
              <p className="text-2xl font-bold text-blue-600">{allBookings.filter(b => b.status === 'confirmed').length}</p>
            </div>
            <div className="bg-card p-4 rounded-lg border border-border">
              <p className="text-sm text-muted-foreground mb-1">Hoàn thành</p>
              <p className="text-2xl font-bold text-green-600">{allBookings.filter(b => b.status === 'completed').length}</p>
            </div>
          </div>
        </div>

        {/* Bookings Table */}
        <div className="bg-card rounded-lg border border-border overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="border-b border-border bg-muted/50">
                <tr>
                  <th className="text-left py-3 px-4 font-semibold">Khách hàng</th>
                  <th className="text-left py-3 px-4 font-semibold">Liên hệ</th>
                  <th className="text-left py-3 px-4 font-semibold">Dịch vụ</th>
                  <th className="text-left py-3 px-4 font-semibold">Ngày / Giờ</th>
                  <th className="text-left py-3 px-4 font-semibold">Trạng thái</th>
                  <th className="text-left py-3 px-4 font-semibold">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {filteredBookings.length > 0 ? (
                  filteredBookings.map((booking) => (
                    <tr key={booking.id} className="border-b border-border hover:bg-muted/50 transition">
                      <td className="py-4 px-4">
                        <div>
                          <p className="font-medium">{booking.name}</p>
                          <p className="text-xs text-muted-foreground">{booking.email}</p>
                        </div>
                      </td>
                      <td className="py-4 px-4">
                        <p className="text-sm">{booking.phone}</p>
                      </td>
                      <td className="py-4 px-4">
                        <p className="text-sm">{booking.service}</p>
                        <p className="text-xs text-muted-foreground mt-1">{booking.notes}</p>
                      </td>
                      <td className="py-4 px-4">
                        <div>
                          <p className="text-sm font-medium">{booking.date}</p>
                          <p className="text-xs text-muted-foreground">{booking.time}</p>
                        </div>
                      </td>
                      <td className="py-4 px-4">
                        <span className={`inline-flex items-center gap-1 px-3 py-1.5 rounded-full text-xs font-medium ${getStatusColor(booking.status)}`}>
                          {getStatusIcon(booking.status)}
                          {getStatusLabel(booking.status)}
                        </span>
                      </td>
                      <td className="py-4 px-4">
                        <div className="flex gap-2">
                          <Button variant="outline" size="sm">Xem</Button>
                          <Button variant="destructive" size="sm" className="gap-1">
                            <Trash2 className="w-3 h-3" />
                            Xóa
                          </Button>
                        </div>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan={6} className="py-8 px-4 text-center text-muted-foreground">
                      Không tìm thấy đặt lịch nào
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </main>
    </div>
  )
}

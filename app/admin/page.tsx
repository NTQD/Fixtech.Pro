'use client'

import { Button } from '@/components/ui/button'
import Link from 'next/link'
import { Wrench, LogOut, BarChart3, Calendar, Users, Settings } from 'lucide-react'
import { useState } from 'react'

export default function AdminDashboard() {
  const [activeTab, setActiveTab] = useState('overview')

  const stats = [
    { label: 'Đặt lịch hôm nay', value: '12', icon: Calendar },
    { label: 'Khách hàng', value: '248', icon: Users },
    { label: 'Doanh thu', value: '45.2M', icon: BarChart3 },
  ]

  const bookings = [
    { id: 1, name: 'Nguyễn Văn A', service: 'Sửa chữa Mainboard', date: '2025-01-15', status: 'pending' },
    { id: 2, name: 'Trần Thị B', service: 'Nâng cấp RAM', date: '2025-01-14', status: 'completed' },
    { id: 3, name: 'Phạm Văn C', service: 'Vệ sinh Laptop', date: '2025-01-13', status: 'completed' },
  ]

  return (
    <div className="min-h-screen bg-background">
      {/* Sidebar */}
      <div className="md:fixed md:left-0 md:top-0 md:w-64 md:h-screen bg-card border-r border-border">
        <div className="p-6 border-b border-border">
          <div className="flex items-center gap-2">
            <div className="w-10 h-10 bg-primary rounded-lg flex items-center justify-center">
              <Wrench className="w-6 h-6 text-primary-foreground" />
            </div>
            <span className="font-bold text-lg">TechFix Admin</span>
          </div>
        </div>
        
        <nav className="p-4 space-y-2">
          <button
            onClick={() => setActiveTab('overview')}
            className={`w-full flex items-center gap-3 px-4 py-2 rounded-lg transition ${
              activeTab === 'overview' 
                ? 'bg-primary text-primary-foreground' 
                : 'text-foreground hover:bg-muted'
            }`}
          >
            <BarChart3 className="w-5 h-5" />
            Tổng quan
          </button>
          <Link href="/admin/bookings">
            <button
              className={`w-full flex items-center gap-3 px-4 py-2 rounded-lg transition text-foreground hover:bg-muted`}
            >
              <Calendar className="w-5 h-5" />
              Quản lý Đặt lịch
            </button>
          </Link>
          <button
            onClick={() => setActiveTab('customers')}
            className={`w-full flex items-center gap-3 px-4 py-2 rounded-lg transition ${
              activeTab === 'customers' 
                ? 'bg-primary text-primary-foreground' 
                : 'text-foreground hover:bg-muted'
            }`}
          >
            <Users className="w-5 h-5" />
            Khách hàng
          </button>
          <button
            onClick={() => setActiveTab('settings')}
            className={`w-full flex items-center gap-3 px-4 py-2 rounded-lg transition ${
              activeTab === 'settings' 
                ? 'bg-primary text-primary-foreground' 
                : 'text-foreground hover:bg-muted'
            }`}
          >
            <Settings className="w-5 h-5" />
            Cài đặt
          </button>
        </nav>

        <div className="absolute bottom-4 left-4 right-4">
          <Link href="/">
            <Button variant="outline" className="w-full gap-2">
              <LogOut className="w-4 h-4" />
              Đăng xuất
            </Button>
          </Link>
        </div>
      </div>

      {/* Main Content */}
      <div className="md:ml-64">
        {/* Header */}
        <header className="border-b border-border bg-card sticky top-0 z-40">
          <div className="px-6 py-4 flex items-center justify-between">
            <h1 className="text-2xl font-bold">Admin Dashboard</h1>
            <div className="flex items-center gap-3">
              <span className="text-sm text-muted-foreground">Admin</span>
            </div>
          </div>
        </header>

        {/* Content */}
        <main className="p-6">
          {activeTab === 'overview' && (
            <div className="space-y-6">
              <div className="grid md:grid-cols-3 gap-4">
                {stats.map((stat, idx) => {
                  const Icon = stat.icon
                  return (
                    <div key={idx} className="bg-card p-6 rounded-lg border border-border">
                      <div className="flex items-start justify-between">
                        <div>
                          <p className="text-sm text-muted-foreground mb-1">{stat.label}</p>
                          <p className="text-3xl font-bold">{stat.value}</p>
                        </div>
                        <div className="w-12 h-12 bg-primary/10 rounded-lg flex items-center justify-center">
                          <Icon className="w-6 h-6 text-primary" />
                        </div>
                      </div>
                    </div>
                  )
                })}
              </div>

              <div className="bg-card p-6 rounded-lg border border-border">
                <div className="flex items-center justify-between mb-4">
                  <h2 className="text-xl font-bold">Đặt lịch gần đây</h2>
                  <Link href="/admin/bookings">
                    <Button variant="outline" size="sm">Xem tất cả</Button>
                  </Link>
                </div>
                <div className="overflow-x-auto">
                  <table className="w-full text-sm">
                    <thead className="border-b border-border">
                      <tr>
                        <th className="text-left py-3 px-4">Khách hàng</th>
                        <th className="text-left py-3 px-4">Dịch vụ</th>
                        <th className="text-left py-3 px-4">Ngày</th>
                        <th className="text-left py-3 px-4">Trạng thái</th>
                      </tr>
                    </thead>
                    <tbody>
                      {bookings.map((booking) => (
                        <tr key={booking.id} className="border-b border-border hover:bg-muted/50">
                          <td className="py-3 px-4">{booking.name}</td>
                          <td className="py-3 px-4">{booking.service}</td>
                          <td className="py-3 px-4">{booking.date}</td>
                          <td className="py-3 px-4">
                            <span className={`px-3 py-1 rounded-full text-xs font-medium ${
                              booking.status === 'pending' 
                                ? 'bg-yellow-100 text-yellow-800' 
                                : 'bg-green-100 text-green-800'
                            }`}>
                              {booking.status === 'pending' ? 'Chờ xử lý' : 'Hoàn thành'}
                            </span>
                          </td>
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
              <h2 className="text-2xl font-bold mb-4">Quản lý đặt lịch</h2>
              <p className="text-muted-foreground mb-6">Sử dụng menu trên để xem danh sách tất cả đặt lịch</p>
              <Link href="/admin/bookings">
                <Button>Quản lý Đặt lịch →</Button>
              </Link>
            </div>
          )}

          {activeTab === 'customers' && (
            <div className="bg-card p-6 rounded-lg border border-border">
              <h2 className="text-2xl font-bold mb-4">Quản lý khách hàng</h2>
              <p className="text-muted-foreground">Hiển thị danh sách tất cả khách hàng</p>
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

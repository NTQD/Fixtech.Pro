'use client'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Calendar } from 'lucide-react'
import { useState } from 'react'
import { useRouter } from 'next/navigation'
import { Navbar } from '@/components/layout/Navbar'
import { Footer } from '@/components/layout/Footer'

export default function BookingPage() {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    service: '',
    date: '',
    time: '',
    notes: ''
  })

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    })
  }

  const router = useRouter()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    const token = localStorage.getItem('access_token')
    if (!token) {
      alert('Vui lòng đăng nhập để đặt lịch!')
      router.push('/login')
      return
    }

    const payload = {
      customer_name: formData.name,
      customer_phone: formData.phone,
      customer_email: formData.email,
      device_info: formData.service, // Temporary mapping
      issue_description: formData.notes,
      scheduled_date: formData.date,
      scheduled_time: formData.time,
      items: [] // Required by backend DTO, empty for now
    }

    try {
      const res = await fetch('http://localhost:3000/bookings', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(payload)
      })

      if (res.ok) {
        alert('Đặt lịch thành công! Chúng tôi sẽ liên hệ sớm.')
        router.push('/profile') // Redirect to profile to see the new booking
      } else {
        const errData = await res.json()
        alert(`Đặt lịch thất bại: ${errData.message || 'Lỗi không xác định'}`)
      }
    } catch (error) {
      console.error('Booking Error:', error)
      alert('Có lỗi xảy ra khi kết nối server')
    }
  }

  return (
    <div className="min-h-screen bg-background flex flex-col">
      <Navbar />

      <main className="flex-grow">
        {/* Header */}
        <section className="py-12 px-4 sm:px-6 lg:px-8 bg-gradient-to-br from-primary/5 to-accent/5">
          <div className="max-w-7xl mx-auto">
            <div className="flex items-center gap-3 mb-4">
              <Calendar className="w-8 h-8 text-primary" />
              <h1 className="text-4xl font-bold">Đặt lịch dịch vụ</h1>
            </div>
            <p className="text-lg text-muted-foreground">Điền thông tin để đặt lịch sửa chữa laptop của bạn</p>
          </div>
        </section>

        {/* Form */}
        <section className="py-12 px-4 sm:px-6 lg:px-8">
          <div className="max-w-2xl mx-auto">
            <div className="bg-card p-8 rounded-lg border border-border bg-white/50 backdrop-blur-sm shadow-sm">
              <form onSubmit={handleSubmit} className="space-y-6">
                <div className="grid md:grid-cols-2 gap-4">
                  <div>
                    <label className="text-sm font-medium block mb-2">Tên đầy đủ</label>
                    <Input
                      type="text"
                      name="name"
                      placeholder="Nguyễn Văn A"
                      value={formData.name}
                      onChange={handleChange}
                      required
                    />
                  </div>
                  <div>
                    <label className="text-sm font-medium block mb-2">Email</label>
                    <Input
                      type="email"
                      name="email"
                      placeholder="your@email.com"
                      value={formData.email}
                      onChange={handleChange}
                      required
                    />
                  </div>
                </div>

                <div className="grid md:grid-cols-2 gap-4">
                  <div>
                    <label className="text-sm font-medium block mb-2">Số điện thoại</label>
                    <Input
                      type="tel"
                      name="phone"
                      placeholder="0123456789"
                      value={formData.phone}
                      onChange={handleChange}
                      required
                    />
                  </div>
                  <div>
                    <label className="text-sm font-medium block mb-2">Dịch vụ</label>
                    <select
                      name="service"
                      value={formData.service}
                      onChange={handleChange}
                      className="w-full h-10 px-3 py-2 border border-border rounded-md bg-background text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                      required
                    >
                      <option value="">Chọn dịch vụ</option>
                      <option value="repair">Sửa chữa</option>
                      <option value="upgrade">Nâng cấp</option>
                      <option value="cleaning">Vệ sinh</option>
                    </select>
                  </div>
                </div>

                <div className="grid md:grid-cols-2 gap-4">
                  <div>
                    <label className="text-sm font-medium block mb-2">Ngày dự kiến</label>
                    <Input
                      type="date"
                      name="date"
                      value={formData.date}
                      onChange={handleChange}
                      required
                    />
                  </div>
                  <div>
                    <label className="text-sm font-medium block mb-2">Thời gian</label>
                    <Input
                      type="time"
                      name="time"
                      value={formData.time}
                      onChange={handleChange}
                      required
                    />
                  </div>
                </div>

                <div>
                  <label className="text-sm font-medium block mb-2">Ghi chú thêm</label>
                  <textarea
                    name="notes"
                    placeholder="Mô tả tình trạng laptop, vấn đề cần sửa..."
                    value={formData.notes}
                    onChange={handleChange}
                    className="w-full px-3 py-2 border border-border rounded-md bg-background min-h-[100px] text-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
                    rows={4}
                  ></textarea>
                </div>

                <Button type="submit" className="w-full text-lg h-12" size="lg">Xác nhận đặt lịch</Button>
              </form>
            </div>
          </div>
        </section>
      </main>

      <Footer />
    </div>
  )
}

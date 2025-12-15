'use client'

import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle, CardFooter } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Navbar } from '@/components/layout/Navbar'
import { Footer } from '@/components/layout/Footer'
import { Wrench, Laptop, Shield, Search, Check, AlertCircle, ArrowRight } from 'lucide-react'
import Link from 'next/link'

const services = [
  {
    id: 'repair',
    title: 'Sửa chữa Phần cứng',
    icon: Wrench,
    description: 'Chuyên trị các bệnh khó của Laptop: Mất nguồn, Vô nước, Chipset...',
    features: ['Sửa mainboard lấy ngay', 'Thay thế linh kiện chính hãng', 'Bảo hành 6-12 tháng'],
    price: 'Từ 300.000đ',
    popular: true
  },
  {
    id: 'upgrade',
    title: 'Nâng cấp Linh kiện',
    icon: Laptop,
    description: 'Tăng tốc máy tính của bạn lên tầm cao mới với SSD và RAM xịn.',
    features: ['RAM chính hãng (Kingston, Samsung)', 'SSD tốc độ cao NVMe', 'Miễn phí công lắp đặt'],
    price: 'Theo linh kiện',
    popular: false
  },
  {
    id: 'maintenance',
    title: 'Vệ sinh & Bảo dưỡng',
    icon: Shield,
    description: 'Quy trình 12 bước vệ sinh chuyên sâu, tra keo tản nhiệt MX-4/TM-30.',
    features: ['Vệ sinh sạch sẽ từng chi tiết', 'Tra keo tản nhiệt cao cấp', 'Kiểm tra nhiệt độ sau khi làm'],
    price: '150.000đ',
    popular: true
  },
  {
    id: 'software',
    title: 'Cài đặt Phần mềm',
    icon: Search,
    description: 'Cài đặt Windows, MacOS, Office và các phần mềm đồ họa, văn phòng.',
    features: ['Windows bản quyền', 'Office 365, Adobe Full Suite', 'Hỗ trợ từ xa qua UltraViewer'],
    price: '100.000đ',
    popular: false
  }
]

const pricingList = [
  { name: 'Vệ sinh tiêu chuẩn', price: '100.000đ', category: 'Bảo dưỡng' },
  { name: 'Vệ sinh chuyên sâu (Keo MX-4)', price: '150.000đ', category: 'Bảo dưỡng' },
  { name: 'Cài Win + Phần mềm cơ bản', price: '150.000đ', category: 'Phần mềm' },
  { name: 'Thay bàn phím Laptop (Phổ thông)', price: '350.000đ - 550.000đ', category: 'Linh kiện' },
  { name: 'Thay màn hình FHD IPS', price: '1.500.000đ - 2.500.000đ', category: 'Linh kiện' },
  { name: 'Sửa nguồn Laptop (Bảo hành 6T)', price: '450.000đ - 850.000đ', category: 'Sửa chữa' },
]

export default function ServicesPage() {
  const [filter, setFilter] = useState('all')

  const filteredServices = filter === 'all'
    ? services
    : services.filter(s => s.id === filter)

  return (
    <div className="min-h-screen bg-background flex flex-col">
      <Navbar />

      <main className="flex-grow">
        {/* Header */}
        <div className="bg-muted/30 py-16 border-b border-border">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
            <h1 className="text-4xl font-extrabold tracking-tight mb-4">Dịch vụ & Bảng giá</h1>
            <p className="text-xl text-muted-foreground max-w-2xl mx-auto">
              Minh bạch, rõ ràng và cạnh tranh. Chúng tôi cam kết không phát sinh chi phí sau khi báo giá.
            </p>
          </div>
        </div>

        {/* Categories */}
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <div className="flex flex-wrap justify-center gap-4 mb-12">
            <Button variant={filter === 'all' ? 'default' : 'outline'} onClick={() => setFilter('all')} className="rounded-full">
              Tất cả
            </Button>
            <Button variant={filter === 'repair' ? 'default' : 'outline'} onClick={() => setFilter('repair')} className="rounded-full">
              Sửa chữa
            </Button>
            <Button variant={filter === 'upgrade' ? 'default' : 'outline'} onClick={() => setFilter('upgrade')} className="rounded-full">
              Nâng cấp
            </Button>
            <Button variant={filter === 'maintenance' ? 'default' : 'outline'} onClick={() => setFilter('maintenance')} className="rounded-full">
              Bảo dưỡng
            </Button>
          </div>

          {/* Service Cards */}
          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6 mb-20">
            {filteredServices.map((service) => (
              <Card key={service.id} className={`flex flex-col ${service.popular ? 'border-primary ring-1 ring-primary/20 shadow-lg' : ''}`}>
                <CardHeader>
                  <div className="w-12 h-12 rounded-lg bg-primary/10 text-primary flex items-center justify-center mb-4">
                    <service.icon className="w-6 h-6" />
                  </div>
                  <CardTitle className="flex justify-between items-start gap-2">
                    <span>{service.title}</span>
                    {service.popular && <Badge variant="secondary" className="text-xs bg-orange-100 text-orange-700 hover:bg-orange-100">Hot</Badge>}
                  </CardTitle>
                  <CardDescription>
                    {service.description}
                  </CardDescription>
                </CardHeader>
                <CardContent className="flex-grow space-y-4">
                  <ul className="space-y-2 text-sm text-foreground/80">
                    {service.features.map((feature, i) => (
                      <li key={i} className="flex items-start gap-2">
                        <Check className="w-4 h-4 text-green-500 mt-0.5 flex-shrink-0" />
                        <span>{feature}</span>
                      </li>
                    ))}
                  </ul>
                </CardContent>
                <CardFooter className="pt-4 border-t border-border mt-auto flex flex-col gap-4">
                  <div className="w-full flex items-center justify-between">
                    <span className="text-sm text-muted-foreground">Giá từ</span>
                    <span className="text-xl font-bold text-primary">{service.price}</span>
                  </div>
                  <Button className="w-full" asChild>
                    <Link href="/booking/wizard">Đặt lịch ngay <ArrowRight className="w-4 h-4 ml-1" /></Link>
                  </Button>
                </CardFooter>
              </Card>
            ))}
          </div>

          {/* Detailed Pricing Table */}
          <div className="max-w-4xl mx-auto">
            <div className="text-center mb-10">
              <h2 className="text-3xl font-bold mb-4">Bảng giá tham khảo</h2>
              <p className="text-muted-foreground">Giá có thể thay đổi tùy theo thời điểm và model máy cụ thể</p>
            </div>

            <div className="bg-card rounded-xl border border-border shadow-sm overflow-hidden">
              <div className="grid grid-cols-12 bg-muted/50 p-4 font-medium text-sm text-muted-foreground border-b border-border">
                <div className="col-span-6 md:col-span-8">Dịch vụ</div>
                <div className="col-span-3 md:col-span-2 text-right">Giá tiền</div>
                <div className="col-span-3 md:col-span-2 text-right pl-4">Phân loại</div>
              </div>
              {pricingList.map((item, index) => (
                <div key={index} className="grid grid-cols-12 p-4 border-b border-border last:border-0 hover:bg-muted/20 transition-colors items-center">
                  <div className="col-span-6 md:col-span-8 font-medium">{item.name}</div>
                  <div className="col-span-3 md:col-span-2 text-right text-primary font-bold">{item.price}</div>
                  <div className="col-span-3 md:col-span-2 text-right pl-4">
                    <Badge variant="outline" className="font-normal">{item.category}</Badge>
                  </div>
                </div>
              ))}
            </div>

            <div className="mt-8 flex gap-4 p-4 bg-yellow-50 dark:bg-yellow-900/10 border border-yellow-200 dark:border-yellow-900/30 rounded-lg text-sm text-yellow-800 dark:text-yellow-200 items-start">
              <AlertCircle className="w-5 h-5 flex-shrink-0 mt-0.5" />
              <p>
                <strong>Lưu ý:</strong> Bảng giá trên chưa bao gồm 10% VAT. Đối với các dòng máy cao cấp (Macbook, Surface, Alienware...) hoặc các lỗi phức tạp không có trong bảng giá, vui lòng liên hệ trực tiếp hoặc mang máy đến cửa hàng để được kỹ thuật viên kiểm tra và báo giá chính xác nhất.
              </p>
            </div>
          </div>
        </div>
      </main>

      <Footer />
    </div>
  )
}

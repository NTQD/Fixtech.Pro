'use client'

import { Button } from '@/components/ui/button'
import Link from 'next/link'
import { Wrench, Clock, Users, Check } from 'lucide-react'
import { useParams } from 'next/navigation'

export default function ServiceDetailPage() {
  const params = useParams()
  const serviceId = params.id as string

  // Mock data - thay bằng API call thực tế
  const service = {
    id: serviceId,
    title: "Sửa chữa Mainboard",
    description: "Dịch vụ sửa chữa mainboard chuyên nghiệp với thiết bị hiện đại",
    price: "250,000đ",
    duration: "1-2 ngày",
    warranty: "6 tháng",
    includes: [
      "Kiểm tra kỹ lưỡng",
      "Sửa chữa chuyên nghiệp",
      "Bảo hành 6 tháng",
      "Tư vấn miễn phí"
    ]
  }

  return (
    <div className="min-h-screen bg-background">
      {/* Navigation */}
      <nav className="border-b border-border bg-card sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex items-center justify-between h-16">
          <Link href="/" className="flex items-center gap-2">
            <div className="w-8 h-8 bg-primary rounded-lg flex items-center justify-center">
              <Wrench className="w-5 h-5 text-primary-foreground" />
            </div>
            <span className="font-bold text-lg">TechFix Pro</span>
          </Link>
          <div className="flex items-center gap-3">
            <Link href="/services">
              <Button variant="ghost" size="sm">Quay lại</Button>
            </Link>
          </div>
        </div>
      </nav>

      {/* Content */}
      <section className="py-12 px-4 sm:px-6 lg:px-8">
        <div className="max-w-4xl mx-auto">
          <div className="bg-card p-8 rounded-lg border border-border">
            <h1 className="text-4xl font-bold mb-4">{service.title}</h1>
            <p className="text-lg text-muted-foreground mb-8">{service.description}</p>

            <div className="grid md:grid-cols-3 gap-6 mb-8">
              <div className="flex items-start gap-3">
                <div className="w-10 h-10 bg-primary/10 rounded-lg flex items-center justify-center flex-shrink-0">
                  <Wrench className="w-5 h-5 text-primary" />
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Giá dịch vụ</p>
                  <p className="text-2xl font-bold text-accent">{service.price}</p>
                </div>
              </div>
              <div className="flex items-start gap-3">
                <div className="w-10 h-10 bg-primary/10 rounded-lg flex items-center justify-center flex-shrink-0">
                  <Clock className="w-5 h-5 text-primary" />
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Thời gian</p>
                  <p className="font-bold">{service.duration}</p>
                </div>
              </div>
              <div className="flex items-start gap-3">
                <div className="w-10 h-10 bg-primary/10 rounded-lg flex items-center justify-center flex-shrink-0">
                  <Users className="w-5 h-5 text-primary" />
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Bảo hành</p>
                  <p className="font-bold">{service.warranty}</p>
                </div>
              </div>
            </div>

            <div className="mb-8">
              <h2 className="text-2xl font-bold mb-4">Dịch vụ bao gồm</h2>
              <ul className="space-y-3">
                {service.includes.map((item, index) => (
                  <li key={index} className="flex items-center gap-3">
                    <div className="w-5 h-5 bg-primary rounded-full flex items-center justify-center flex-shrink-0">
                      <Check className="w-3 h-3 text-primary-foreground" />
                    </div>
                    <span>{item}</span>
                  </li>
                ))}
              </ul>
            </div>

            <div className="flex gap-4">
              <Link href="/booking" className="flex-1">
                <Button className="w-full" size="lg">Đặt lịch ngay</Button>
              </Link>
              <Link href="/services">
                <Button variant="outline" size="lg">Xem dịch vụ khác</Button>
              </Link>
            </div>
          </div>
        </div>
      </section>
    </div>
  )
}

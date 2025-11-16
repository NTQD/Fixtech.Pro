'use client'

import { Button } from '@/components/ui/button'
import Link from 'next/link'
import { Wrench, Zap, Cpu, Droplet } from 'lucide-react'
import { useState } from 'react'

export default function ServicesPage() {
  const [selectedCategory, setSelectedCategory] = useState('all')

  const allServices = [
    {
      id: 1,
      category: 'repair',
      icon: <Wrench className="w-8 h-8" />,
      title: "Sửa chữa Mainboard",
      description: "Sửa chữa, thay thế mainboard bị hỏng",
      price: "250,000đ"
    },
    {
      id: 2,
      category: 'repair',
      icon: <Wrench className="w-8 h-8" />,
      title: "Sửa chữa Màn hình",
      description: "Thay thế màn hình bị vỡ hoặc lỗi",
      price: "350,000đ"
    },
    {
      id: 3,
      category: 'upgrade',
      icon: <Zap className="w-8 h-8" />,
      title: "Nâng cấp RAM",
      description: "Nâng cấp RAM đến 64GB",
      price: "150,000đ"
    },
    {
      id: 4,
      category: 'upgrade',
      icon: <Cpu className="w-8 h-8" />,
      title: "Nâng cấp SSD",
      description: "Thay SSD lên tốc độ cao 1TB",
      price: "400,000đ"
    },
    {
      id: 5,
      category: 'cleaning',
      icon: <Droplet className="w-8 h-8" />,
      title: "Vệ sinh Laptop",
      description: "Vệ sinh bụi, thay keo tản nhiệt",
      price: "100,000đ"
    },
    {
      id: 6,
      category: 'cleaning',
      icon: <Droplet className="w-8 h-8" />,
      title: "Sửa lỗi Phần mềm",
      description: "Cài đặt lại OS, khắc phục malware",
      price: "120,000đ"
    }
  ]

  const filtered = selectedCategory === 'all' 
    ? allServices 
    : allServices.filter(s => s.category === selectedCategory)

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
            <Link href="/booking">
              <Button size="sm">Đặt lịch</Button>
            </Link>
          </div>
        </div>
      </nav>

      {/* Header */}
      <section className="py-12 px-4 sm:px-6 lg:px-8 bg-gradient-to-br from-primary/5 to-accent/5">
        <div className="max-w-7xl mx-auto">
          <h1 className="text-4xl font-bold mb-2">Dịch vụ của chúng tôi</h1>
          <p className="text-lg text-muted-foreground">Chọn dịch vụ và đặt lịch cho laptop của bạn</p>
        </div>
      </section>

      {/* Filter */}
      <section className="py-8 px-4 sm:px-6 lg:px-8 bg-card border-b border-border">
        <div className="max-w-7xl mx-auto">
          <div className="flex flex-wrap gap-2">
            {['all', 'repair', 'upgrade', 'cleaning'].map((cat) => (
              <Button
                key={cat}
                variant={selectedCategory === cat ? "default" : "outline"}
                onClick={() => setSelectedCategory(cat)}
                size="sm"
              >
                {cat === 'all' && 'Tất cả'}
                {cat === 'repair' && 'Sửa chữa'}
                {cat === 'upgrade' && 'Nâng cấp'}
                {cat === 'cleaning' && 'Vệ sinh'}
              </Button>
            ))}
          </div>
        </div>
      </section>

      {/* Services Grid */}
      <section className="py-12 px-4 sm:px-6 lg:px-8">
        <div className="max-w-7xl mx-auto">
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            {filtered.map((service) => (
              <div key={service.id} className="bg-card p-6 rounded-lg border border-border hover:border-primary transition hover:shadow-lg group">
                <div className="text-primary mb-4 group-hover:scale-110 transition">
                  {service.icon}
                </div>
                <h3 className="text-xl font-semibold mb-2">{service.title}</h3>
                <p className="text-muted-foreground mb-4 text-sm">{service.description}</p>
                <div className="flex items-center justify-between">
                  <span className="text-lg font-bold text-accent">{service.price}</span>
                  <Link href={`/services/${service.id}`}>
                    <Button size="sm" variant="outline">Chi tiết</Button>
                  </Link>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-card border-t border-border py-8 px-4 sm:px-6 lg:px-8">
        <div className="max-w-7xl mx-auto text-center text-sm text-muted-foreground">
          <p>&copy; 2025 TechFix Pro. All rights reserved.</p>
        </div>
      </footer>
    </div>
  )
}

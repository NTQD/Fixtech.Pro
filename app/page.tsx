'use client'

import { Button } from '@/components/ui/button'
import Link from 'next/link'
import { Wrench, Zap, Calendar, Users, ArrowRight, Check } from 'lucide-react'

export default function HomePage() {
  const services = [
    {
      icon: <Wrench className="w-8 h-8" />,
      title: "Sửa chữa Laptop",
      description: "Sửa chữa các lỗi phần cứng và phần mềm với đội kỹ thuật chuyên nghiệp"
    },
    {
      icon: <Zap className="w-8 h-8" />,
      title: "Nâng cấp Cứng",
      description: "Nâng cấp RAM, SSD, bộ xử lý để tăng hiệu năng"
    },
    {
      icon: <Calendar className="w-8 h-8" />,
      title: "Đặt lịch Dễ dàng",
      description: "Đặt lịch sửa chữa trực tuyến, chọn thời gian phù hợp"
    }
  ]

  const features = [
    "Đặt lịch nhanh chóng online",
    "Kỹ thuật viên giàu kinh nghiệm",
    "Giá cạnh tranh, rõ ràng",
    "Bảo hành dịch vụ 100%"
  ]

  return (
    <div className="min-h-screen bg-background">
      {/* Navigation */}
      <nav className="border-b border-border bg-card sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex items-center justify-between h-16">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 bg-primary rounded-lg flex items-center justify-center">
              <Wrench className="w-5 h-5 text-primary-foreground" />
            </div>
            <span className="font-bold text-lg">TechFix Pro</span>
          </div>
          <div className="hidden md:flex items-center gap-8">
            <a href="#services" className="text-foreground hover:text-primary transition">Dịch vụ</a>
            <a href="#features" className="text-foreground hover:text-primary transition">Đặc điểm</a>
            <Link href="/services" className="text-foreground hover:text-primary transition">Xem Dịch vụ</Link>
          </div>
          <div className="flex items-center gap-3">
            <Link href="/login">
              <Button variant="ghost" size="sm">Đăng nhập</Button>
            </Link>
            <Link href="/register">
              <Button size="sm">Đăng ký</Button>
            </Link>
          </div>
        </div>
      </nav>

      {/* Hero Section */}
      <section className="py-20 px-4 sm:px-6 lg:px-8 bg-gradient-to-br from-primary/5 via-background to-accent/5">
        <div className="max-w-7xl mx-auto">
          <div className="grid md:grid-cols-2 gap-12 items-center">
            <div className="space-y-6">
              <h1 className="text-4xl sm:text-5xl font-bold leading-tight text-balance">
                Laptop của bạn cần chăm sóc
              </h1>
              <p className="text-lg text-muted-foreground leading-relaxed">
                Chúng tôi cung cấp dịch vụ sửa chữa và nâng cấp laptop chuyên nghiệp. Đặt lịch ngay và để chúng tôi giúp bạn.
              </p>
              <div className="flex flex-col sm:flex-row gap-4">
                <Link href="/services">
                  <Button size="lg" className="gap-2">
                    Xem Dịch vụ <ArrowRight className="w-4 h-4" />
                  </Button>
                </Link>
                <Link href="/booking">
                  <Button size="lg" variant="outline">Đặt lịch ngay</Button>
                </Link>
              </div>
            </div>
            <div className="flex items-center justify-center">
              <div className="w-full max-w-md aspect-square bg-gradient-to-br from-primary to-accent rounded-2xl flex items-center justify-center shadow-2xl">
                <Zap className="w-40 h-40 text-primary-foreground opacity-20" />
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Services Section */}
      <section id="services" className="py-20 px-4 sm:px-6 lg:px-8 bg-card">
        <div className="max-w-7xl mx-auto">
          <h2 className="text-3xl sm:text-4xl font-bold text-center mb-4">Dịch vụ chúng tôi cung cấp</h2>
          <p className="text-center text-muted-foreground mb-12 max-w-2xl mx-auto">
            Từ sửa chữa cơ bản đến nâng cấp hiệu năng, chúng tôi có đủ giải pháp cho bạn
          </p>
          <div className="grid md:grid-cols-3 gap-8">
            {services.map((service, index) => (
              <div key={index} className="p-6 bg-background rounded-lg border border-border hover:border-primary transition group">
                <div className="text-primary mb-4 group-hover:scale-110 transition">
                  {service.icon}
                </div>
                <h3 className="text-xl font-semibold mb-2">{service.title}</h3>
                <p className="text-muted-foreground">{service.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section id="features" className="py-20 px-4 sm:px-6 lg:px-8 bg-background">
        <div className="max-w-7xl mx-auto">
          <div className="grid md:grid-cols-2 gap-12 items-center">
            <div className="space-y-8">
              <h2 className="text-3xl sm:text-4xl font-bold">Tại sao chọn TechFix Pro?</h2>
              {features.map((feature, index) => (
                <div key={index} className="flex gap-4 items-start">
                  <div className="flex-shrink-0 w-6 h-6 bg-primary rounded-full flex items-center justify-center mt-0.5">
                    <Check className="w-4 h-4 text-primary-foreground" />
                  </div>
                  <p className="text-lg text-foreground">{feature}</p>
                </div>
              ))}
            </div>
            <div className="flex items-center justify-center">
              <div className="w-full max-w-md aspect-square bg-gradient-to-br from-accent/20 to-primary/20 rounded-2xl flex items-center justify-center border-2 border-dashed border-primary/30">
                <Users className="w-40 h-40 text-primary/30" />
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 px-4 sm:px-6 lg:px-8 bg-primary text-primary-foreground">
        <div className="max-w-4xl mx-auto text-center space-y-6">
          <h2 className="text-3xl sm:text-4xl font-bold">Sẵn sàng cho dịch vụ tốt?</h2>
          <p className="text-lg opacity-90 text-balance">
            Hãy đặt lịch hôm nay và để chúng tôi giúp laptop của bạn hoạt động như mới
          </p>
          <Link href="/booking">
            <Button size="lg" variant="secondary">Đặt lịch ngay</Button>
          </Link>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-card border-t border-border py-12 px-4 sm:px-6 lg:px-8">
        <div className="max-w-7xl mx-auto">
          <div className="grid md:grid-cols-4 gap-8 mb-8">
            <div>
              <div className="flex items-center gap-2 mb-4">
                <div className="w-6 h-6 bg-primary rounded-lg flex items-center justify-center">
                  <Wrench className="w-4 h-4 text-primary-foreground" />
                </div>
                <span className="font-bold">TechFix Pro</span>
              </div>
              <p className="text-sm text-muted-foreground">Dịch vụ sửa chữa laptop chuyên nghiệp</p>
            </div>
            <div>
              <h4 className="font-semibold mb-4">Sản phẩm</h4>
              <ul className="space-y-2 text-sm text-muted-foreground">
                <li><a href="#" className="hover:text-primary transition">Dịch vụ</a></li>
                <li><a href="#" className="hover:text-primary transition">Giá cả</a></li>
              </ul>
            </div>
            <div>
              <h4 className="font-semibold mb-4">Công ty</h4>
              <ul className="space-y-2 text-sm text-muted-foreground">
                <li><a href="#" className="hover:text-primary transition">Về chúng tôi</a></li>
                <li><a href="#" className="hover:text-primary transition">Liên hệ</a></li>
              </ul>
            </div>
            <div>
              <h4 className="font-semibold mb-4">Hợp pháp</h4>
              <ul className="space-y-2 text-sm text-muted-foreground">
                <li><a href="#" className="hover:text-primary transition">Quyền riêng tư</a></li>
                <li><a href="#" className="hover:text-primary transition">Điều khoản</a></li>
              </ul>
            </div>
          </div>
          <div className="border-t border-border pt-8 text-center text-sm text-muted-foreground">
            <p>&copy; 2025 TechFix Pro. All rights reserved.</p>
          </div>
        </div>
      </footer>
    </div>
  )
}

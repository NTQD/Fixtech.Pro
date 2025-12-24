'use client'

import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { Navbar } from '@/components/layout/Navbar'
import { Footer } from '@/components/layout/Footer'
import Link from 'next/link'
import { ArrowRight, CheckCircle, Laptop, Wrench, Shield, Clock } from 'lucide-react'

import { AutoImageSlider } from '@/components/ui/auto-image-slider'

export default function LandingPage() {
  const heroImages = [
    "http://localhost:3000/public/home/lap1.webp",
    "http://localhost:3000/public/home/lap2.webp",
    "http://localhost:3000/public/home/lap3.webp",
    "http://localhost:3000/public/home/lap4.webp",
    "http://localhost:3000/public/home/lap5.webp"
  ];

  const whyChooseUsImages = [
    "http://localhost:3000/public/home/book.webp",
    "http://localhost:3000/public/home/good_tech.webp",
    "http://localhost:3000/public/home/price_ok.webp",
    "http://localhost:3000/public/home/guarantee.webp"
  ];

  const testimonials = [
    { name: "Minh Tuấn", device: "MacBook Pro 2020", comment: "Máy mình bị vô nước tưởng chừng như bỏ đi rồi, may mà đem qua TechFix cứu được mainboard. Giá cả hợp lý, nhân viên nhiệt tình.", rating: 5, color: "bg-blue-100 text-blue-700" },
    { name: "Bảo Châu", device: "Dell Inspiron", comment: "Nâng cấp SSD và RAM ở đây rất nhanh, lấy ngay trong 30 phút. Máy chạy mượt hơn hẳn. Rất hài lòng!", rating: 5, color: "bg-purple-100 text-purple-700" },
    { name: "Xuân Trường", device: "Asus TUF Gaming", comment: "Dịch vụ chuyên nghiệp, có bảo hành đàng hoàng. Mình hay vệ sinh máy ở đây, kỹ thuật viên làm rất kỹ.", rating: 5, color: "bg-green-100 text-green-700" },
    { name: "Ngọc Lan", device: "HP Pavilion", comment: "Thay màn hình chính hãng giá tốt hơn hãng nhiều. Màu sắc đẹp, không bị ám vàng. Vote 5 sao!", rating: 5, color: "bg-pink-100 text-pink-700" },
    { name: "Minh Đức", device: "Lenovo ThinkPad", comment: "Cài lại Win bản quyền và bộ Office rất nhanh. Kỹ thuật viên hướng dẫn tận tình cách backup dữ liệu.", rating: 5, color: "bg-orange-100 text-orange-700" },
    { name: "Thành Nam", device: "Acer Nitro 5", comment: "Tra keo tản nhiệt xong máy mát hẳn, chơi game không còn bị drop FPS nữa. Tuyệt vời!", rating: 5, color: "bg-red-100 text-red-700" },
    { name: "Văn Ba", device: "Sony Vaio cũ", comment: "Máy chú cũ rồi mà các cháu vẫn sửa được, cứu lại được bao nhiêu ảnh kỷ niệm. Cảm ơn cửa hàng nhiều lắm.", rating: 5, color: "bg-indigo-100 text-indigo-700" },
    { name: "Mỹ Hạnh", device: "MacBook Air M1", comment: "Cài bộ Adobe trọn gói dùng mượt mà, không bị lỗi vặt. Sẽ giới thiệu bạn bè qua đây.", rating: 5, color: "bg-teal-100 text-teal-700" },
    { name: "Như Quỳnh", device: "Surface Pro 7", comment: "Thay pin chính hãng dùng trâu như mới mua. Nhân viên tư vấn rất có tâm, không vẽ bệnh.", rating: 5, color: "bg-yellow-100 text-yellow-700" },
    { name: "Thanh Tùng", device: "MSI Gaming", comment: "Sửa nguồn laptop gaming uy tín nhất khu vực này. Bảo hành 6 tháng yên tâm hẳn.", rating: 5, color: "bg-cyan-100 text-cyan-700" }
  ];

  return (
    <div className="min-h-screen bg-background flex flex-col">
      <Navbar />

      <main className="flex-grow">
        {/* Hero Section */}
        <section className="relative py-20 lg:py-32 overflow-hidden">
          <div className="absolute inset-0 bg-gradient-to-br from-primary/5 to-secondary/5 -z-10" />
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="grid lg:grid-cols-2 gap-12 items-center">
              <div className="space-y-8">
                <h1 className="text-4xl lg:text-6xl font-extrabold tracking-tight text-foreground">
                  Sửa chữa Laptop <span className="text-primary">Chuyên nghiệp</span> & <span className="text-blue-500">Uy tín</span>
                </h1>
                <p className="text-xl text-muted-foreground leading-relaxed">
                  Dịch vụ sửa chữa, nâng cấp và bảo dưỡng laptop hàng đầu. Đội ngũ kỹ thuật viên tay nghề cao, linh kiện chính hãng, bảo hành dài hạn.
                </p>
                <div className="flex flex-col sm:flex-row gap-4">
                  <Link href="/booking/wizard">
                    <Button size="lg" className="h-14 px-8 text-lg shadow-lg shadow-primary/20 hover:scale-105 hover:shadow-primary/40 transition-all duration-300">
                      Đặt lịch ngay <ArrowRight className="ml-2 w-5 h-5" />
                    </Button>
                  </Link>
                  <Link href="/tracking">
                    <Button size="lg" variant="outline" className="h-14 px-8 text-lg bg-background/50 backdrop-blur-sm">
                      Tra cứu đơn hàng
                    </Button>
                  </Link>
                </div>
                <div className="flex items-center gap-6 text-sm font-medium text-muted-foreground pt-4">
                  <div className="flex items-center gap-2">
                    <CheckCircle className="w-5 h-5 text-green-500" /> <span>Bảo hành 12 tháng</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <CheckCircle className="w-5 h-5 text-green-500" /> <span>Lấy ngay trong 1h</span>
                  </div>
                </div>
              </div>
              <div className="relative lg:block">
                <div className="relative rounded-2xl overflow-hidden shadow-2xl border border-border bg-card p-2">
                  <div className="aspect-[4/3] bg-muted rounded-xl bg-transparent relative overflow-hidden">
                    <AutoImageSlider images={heroImages} interval={2000} className="rounded-xl" />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        {/* Services Highlight */}
        <section className="py-20 bg-muted/30">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="text-center max-w-2xl mx-auto mb-16">
              <h2 className="text-3xl font-bold mb-4">Dịch vụ nổi bật</h2>
              <p className="text-muted-foreground">Chúng tôi cung cấp giải pháp toàn diện cho mọi vấn đề của laptop</p>
            </div>

            <div className="grid md:grid-cols-3 gap-8">
              <Card className="hover:shadow-xl hover:-translate-y-2 transition-all duration-300 hover:border-primary/50 cursor-pointer group">
                <CardContent className="p-8 space-y-4">
                  <div className="w-14 h-14 rounded-2xl bg-blue-100 dark:bg-blue-900/20 text-blue-600 flex items-center justify-center group-hover:scale-110 transition-transform">
                    <Wrench className="w-7 h-7" />
                  </div>
                  <h3 className="text-xl font-bold">Sửa chữa Phần cứng</h3>
                  <p className="text-muted-foreground leading-relaxed">
                    Khắc phục các lỗi mainboard, nguồn, màn hình, bàn phím với linh kiện chính hãng.
                  </p>
                  <Link href="/services" className="inline-flex items-center text-primary font-medium hover:underline">
                    Xem chi tiết <ArrowRight className="w-4 h-4 ml-1" />
                  </Link>
                </CardContent>
              </Card>

              <Card className="hover:shadow-xl hover:-translate-y-2 transition-all duration-300 hover:border-primary/50 cursor-pointer group">
                <CardContent className="p-8 space-y-4">
                  <div className="w-14 h-14 rounded-2xl bg-purple-100 dark:bg-purple-900/20 text-purple-600 flex items-center justify-center group-hover:scale-110 transition-transform">
                    <Laptop className="w-7 h-7" />
                  </div>
                  <h3 className="text-xl font-bold">Nâng cấp Linh kiện</h3>
                  <p className="text-muted-foreground leading-relaxed">
                    Tăng tốc máy tính với RAM, SSD chính hãng. Tư vấn cấu hình tối ưu nhất.
                  </p>
                  <Link href="/services" className="inline-flex items-center text-primary font-medium hover:underline">
                    Xem chi tiết <ArrowRight className="w-4 h-4 ml-1" />
                  </Link>
                </CardContent>
              </Card>

              <Card className="hover:shadow-xl hover:-translate-y-2 transition-all duration-300 hover:border-primary/50 cursor-pointer group">
                <CardContent className="p-8 space-y-4">
                  <div className="w-14 h-14 rounded-2xl bg-orange-100 dark:bg-orange-900/20 text-orange-600 flex items-center justify-center group-hover:scale-110 transition-transform">
                    <Shield className="w-7 h-7" />
                  </div>
                  <h3 className="text-xl font-bold">Bảo dưỡng & Vệ sinh</h3>
                  <p className="text-muted-foreground leading-relaxed">
                    Vệ sinh chuyên sâu, tra keo tản nhiệt xịn. Miễn phí kiểm tra tổng quát.
                  </p>
                  <Link href="/services" className="inline-flex items-center text-primary font-medium hover:underline">
                    Xem chi tiết <ArrowRight className="w-4 h-4 ml-1" />
                  </Link>
                </CardContent>
              </Card>
            </div>
          </div>
        </section>

        {/* Testimonials Section */}
        <section className="py-24 bg-background overflow-hidden relative">
          <div className="absolute inset-x-0 top-0 h-24 bg-gradient-to-b from-background to-transparent z-10 pointer-events-none" />
          <div className="absolute inset-x-0 bottom-0 h-24 bg-gradient-to-t from-background to-transparent z-10 pointer-events-none" />

          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center mb-12">
            <h2 className="text-3xl font-bold">Khách hàng nói gì về chúng tôi?</h2>
            <p className="text-muted-foreground mt-2">Hơn 10,000 khách hàng hài lòng với dịch vụ tại TechFix Pro</p>
          </div>

          <div className="relative w-full">
            <div className="flex w-max animate-marquee hover:[animation-play-state:paused] gap-6 px-6">
              {[...testimonials, ...testimonials].map((t, i) => (
                <Card key={i} className="flex-shrink-0 w-[350px] md:w-[400px] hover:shadow-lg transition-all border-primary/10 bg-card/50 backdrop-blur-sm">
                  <CardContent className="p-6 text-left space-y-3">
                    {/* Row 1: Avatar + Name */}
                    <div className="flex items-center gap-3">
                      <div className={`w-10 h-10 rounded-full flex items-center justify-center font-bold text-sm ${t.color}`}>
                        {t.name.split(' ').pop()?.[0]}
                      </div>
                      <h4 className="font-bold text-md">{t.name}</h4>
                    </div>

                    {/* Row 2: Stars */}
                    <div className="flex text-yellow-500">
                      {[1, 2, 3, 4, 5].map(star => <span key={star}>★</span>)}
                    </div>

                    {/* Row 3: Device */}
                    <div className="flex items-center gap-2 text-xs text-muted-foreground font-medium uppercase tracking-wide">
                      <Laptop className="w-3 h-3" />
                      {t.device}
                    </div>

                    {/* Row 4: Comment */}
                    <p className="text-muted-foreground text-sm leading-relaxed">
                      "{t.comment}"
                    </p>
                  </CardContent>
                </Card>
              ))}
            </div>
          </div>
        </section>

        {/* Why Choose Us Section */}
        <section className="py-20 bg-background">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="grid md:grid-cols-2 gap-12 items-center">
              <div className="space-y-8">
                <h2 className="text-3xl sm:text-4xl font-bold text-foreground">Tại sao chọn TechFix Pro?</h2>
                <div className="space-y-6">
                  {[
                    "Đặt lịch nhanh chóng online",
                    "Kỹ thuật viên giàu kinh nghiệm",
                    "Giá cạnh tranh, rõ ràng",
                    "Bảo hành dịch vụ 100%"
                  ].map((item, index) => (
                    <div key={index} className="flex gap-4 items-center">
                      <div className="flex-shrink-0 w-8 h-8 bg-primary rounded-full flex items-center justify-center">
                        <CheckCircle className="w-5 h-5 text-primary-foreground" />
                      </div>
                      <p className="text-lg font-medium text-foreground">{item}</p>
                    </div>
                  ))}
                </div>
              </div>
              <div className="flex items-center justify-center">
                <div className="w-full max-w-md aspect-square bg-gradient-to-br from-primary/10 to-secondary/10 rounded-3xl flex items-center justify-center border border-border/50 shadow-2xl relative overflow-hidden">
                  <AutoImageSlider images={whyChooseUsImages} interval={2000} className="rounded-3xl" />
                </div>
              </div>
            </div>
          </div>
        </section>

        {/* CTA Section */}
        <section className="py-20 bg-primary text-primary-foreground">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
            <h2 className="text-3xl font-bold mb-6">Sẵn sàng để máy tính của bạn chạy như mới?</h2>
            <p className="text-xl mb-8 opacity-90 max-w-2xl mx-auto">
              Đừng để máy chậm làm gián đoạn công việc của bạn. Đặt lịch ngay hôm nay để nhận ưu đãi kiểm tra miễn phí.
            </p>
            <div className="flex gap-4 justify-center">
              <Link href="/booking/wizard">
                <Button size="lg" variant="secondary" className="text-lg px-8 py-6 h-auto font-semibold shadow-lg hover:shadow-xl hover:scale-105 transition-all duration-300">
                  Đặt lịch ngay
                </Button>
              </Link>
              <Link href="/contact">
                <Button size="lg" variant="outline" className="text-lg px-8 py-6 h-auto font-semibold bg-transparent border-primary-foreground text-primary-foreground hover:bg-primary-foreground hover:text-primary">
                  Liên hệ tư vấn
                </Button>
              </Link>
            </div>
          </div>
        </section>

      </main>
      <Footer />
    </div>
  )
}

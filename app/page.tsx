'use client'

import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { Navbar } from '@/components/layout/Navbar'
import { Footer } from '@/components/layout/Footer'
import Link from 'next/link'
import { ArrowRight, CheckCircle, Laptop, Wrench, Shield, Clock } from 'lucide-react'

export default function LandingPage() {
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
                    <Button size="lg" className="h-14 px-8 text-lg shadow-lg shadow-primary/20">
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
                  <div className="aspect-[4/3] bg-muted rounded-xl flex items-center justify-center relative overflow-hidden">
                    {/* Placeholder for Hero Image */}
                    <div className="absolute inset-0 bg-gradient-to-tr from-blue-500/10 to-purple-500/10" />
                    <Laptop className="w-32 h-32 text-primary/20" />

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
              <Card className="hover:shadow-lg transition-all hover:border-primary/50 cursor-pointer group">
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

              <Card className="hover:shadow-lg transition-all hover:border-primary/50 cursor-pointer group">
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

              <Card className="hover:shadow-lg transition-all hover:border-primary/50 cursor-pointer group">
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
        <section className="py-24 bg-background">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
            <h2 className="text-3xl font-bold mb-12">Khách hàng nói gì về chúng tôi?</h2>
            <div className="grid md:grid-cols-3 gap-8">
              <Card className="hover:shadow-md transition-shadow">
                <CardContent className="p-8 text-left space-y-4">
                  <div className="flex text-yellow-500">
                    {[1, 2, 3, 4, 5].map(i => <span key={i}>★</span>)}
                  </div>
                  <p className="text-muted-foreground italic">"Máy mình bị vô nước tưởng chừng như bỏ đi rồi, may mà đem qua TechFix cứu được mainboard. Giá cả hợp lý, nhân viên nhiệt tình."</p>
                  <div className="flex items-center gap-3 pt-4 border-t border-border">
                    <div className="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center font-bold text-blue-700">A</div>
                    <div>
                      <h4 className="font-bold text-sm">Anh Tuấn</h4>
                      <p className="text-xs text-muted-foreground">MacBook Pro 2020</p>
                    </div>
                  </div>
                </CardContent>
              </Card>
              <Card className="hover:shadow-md transition-shadow">
                <CardContent className="p-8 text-left space-y-4">
                  <div className="flex text-yellow-500">
                    {[1, 2, 3, 4, 5].map(i => <span key={i}>★</span>)}
                  </div>
                  <p className="text-muted-foreground italic">"Nâng cấp SSD và RAM ở đây rất nhanh, lấy ngay trong 30 phút. Máy chạy mượt hơn hẳn. Rất hài lòng!"</p>
                  <div className="flex items-center gap-3 pt-4 border-t border-border">
                    <div className="w-10 h-10 rounded-full bg-purple-100 flex items-center justify-center font-bold text-purple-700">M</div>
                    <div>
                      <h4 className="font-bold text-sm">Chị Mai</h4>
                      <p className="text-xs text-muted-foreground">Dell Inspiron</p>
                    </div>
                  </div>
                </CardContent>
              </Card>
              <Card className="hover:shadow-md transition-shadow">
                <CardContent className="p-8 text-left space-y-4">
                  <div className="flex text-yellow-500">
                    {[1, 2, 3, 4, 5].map(i => <span key={i}>★</span>)}
                  </div>
                  <p className="text-muted-foreground italic">"Dịch vụ chuyên nghiệp, có bảo hành đàng hoàng. Mình hay vệ sinh máy ở đây, kỹ thuật viên làm rất kỹ."</p>
                  <div className="flex items-center gap-3 pt-4 border-t border-border">
                    <div className="w-10 h-10 rounded-full bg-green-100 flex items-center justify-center font-bold text-green-700">H</div>
                    <div>
                      <h4 className="font-bold text-sm">Bạn Huy</h4>
                      <p className="text-xs text-muted-foreground">Asus TUF Gaming</p>
                    </div>
                  </div>
                </CardContent>
              </Card>
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
                  {/* Decorative elements */}
                  <div className="absolute top-0 right-0 w-32 h-32 bg-primary/10 rounded-bl-full" />
                  <div className="absolute bottom-0 left-0 w-24 h-24 bg-secondary/10 rounded-tr-full" />

                  <div className="relative z-10 text-center">
                    <div className="w-24 h-24 bg-background rounded-2xl shadow-lg mx-auto mb-4 flex items-center justify-center">
                      <Shield className="w-12 h-12 text-primary" />
                    </div>
                    <div className="space-y-2">
                      <div className="h-2 w-32 bg-muted rounded-full mx-auto" />
                      <div className="h-2 w-24 bg-muted/50 rounded-full mx-auto" />
                    </div>
                  </div>
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
                <Button size="lg" variant="secondary" className="text-lg px-8 py-6 h-auto font-semibold shadow-lg hover:shadow-xl transition-all">
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

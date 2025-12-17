import Link from 'next/link'
import { Wrench, Facebook, Twitter, Instagram, Mail, Phone, MapPin } from 'lucide-react'

export function Footer() {
    return (
        <footer className="bg-card border-t border-border">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
                <div className="grid grid-cols-1 md:grid-cols-4 gap-8">

                    {/* Brand */}
                    <div className="space-y-4">
                        <Link href="/" className="flex items-center gap-2">
                            <div className="w-8 h-8 bg-primary rounded-lg flex items-center justify-center">
                                <Wrench className="w-5 h-5 text-primary-foreground" />
                            </div>
                            <span className="font-bold text-lg">TechFix Pro</span>
                        </Link>
                        <p className="text-sm text-muted-foreground leading-relaxed">
                            Dịch vụ sửa chữa và nâng cấp laptop chuyên nghiệp, uy tín hàng đầu. Cam kết chất lượng và bảo hành dài hạn.
                        </p>
                    </div>

                    {/* Quick Links */}
                    <div>
                        <h3 className="font-semibold mb-4">Dịch vụ</h3>
                        <ul className="space-y-2 text-sm text-muted-foreground">
                            <li><Link href="/services/repair" className="hover:text-primary transition">Sửa chữa phần cứng</Link></li>
                            <li><Link href="/services/upgrade" className="hover:text-primary transition">Nâng cấp linh kiện</Link></li>
                            <li><Link href="/services/cleaning" className="hover:text-primary transition">Vệ sinh & Bảo dưỡng</Link></li>
                            <li><Link href="/services/software" className="hover:text-primary transition">Cài đặt phần mềm</Link></li>
                        </ul>
                    </div>

                    {/* Company */}
                    <div>
                        <h3 className="font-semibold mb-4">Công ty</h3>
                        <ul className="space-y-2 text-sm text-muted-foreground">
                            <li><Link href="/about" className="hover:text-primary transition">Về chúng tôi</Link></li>
                            <li><Link href="/contact" className="hover:text-primary transition">Liên hệ</Link></li>
                            <li><Link href="/careers" className="hover:text-primary transition">Tuyển dụng</Link></li>
                            <li><Link href="/privacy" className="hover:text-primary transition">Chính sách bảo mật</Link></li>
                        </ul>
                    </div>

                    {/* Contact */}
                    <div>
                        <h3 className="font-semibold mb-4">Liên hệ</h3>
                        <ul className="space-y-4 text-sm text-muted-foreground">
                            <li className="flex items-center gap-2">
                                <MapPin className="w-4 h-4 text-primary" />
                                <span>56 Đ. Trần Phú, P. Mộ Lao, Hà Đông, Hà Nội</span>
                            </li>
                            <li className="flex items-center gap-2">
                                <Phone className="w-4 h-4 text-primary" />
                                <span>1900 1009</span>
                            </li>
                            <li className="flex items-center gap-2">
                                <Mail className="w-4 h-4 text-primary" />
                                <span>support@techfix.pro</span>
                            </li>
                        </ul>
                        <div className="flex gap-4 mt-4">
                            <a href="#" className="p-2 bg-muted rounded-full hover:bg-primary hover:text-white transition">
                                <Facebook className="w-4 h-4" />
                            </a>
                            <a href="#" className="p-2 bg-muted rounded-full hover:bg-primary hover:text-white transition">
                                <Twitter className="w-4 h-4" />
                            </a>
                            <a href="#" className="p-2 bg-muted rounded-full hover:bg-primary hover:text-white transition">
                                <Instagram className="w-4 h-4" />
                            </a>
                        </div>
                    </div>
                </div>

                <div className="border-t border-border mt-12 pt-8 text-center text-sm text-muted-foreground">
                    <p>&copy; 2025 TechFix Pro. All rights reserved.</p>
                </div>
            </div>
        </footer>
    )
}

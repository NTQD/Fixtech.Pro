'use client'

import React, { useState, useEffect } from 'react'
import Link from 'next/link'
import { Button } from '@/components/ui/button'
import { Wrench, User, LogOut, LayoutDashboard } from 'lucide-react'
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { useRouter } from 'next/navigation'

export function Navbar() {
    const [user, setUser] = useState<{ role: string, email: string, avatar_url?: string } | null>(null)
    const [scrolled, setScrolled] = useState(false)
    const router = useRouter()

    useEffect(() => {
        // Auth check
        const userStr = localStorage.getItem('user')
        if (userStr) {
            try {
                const userObj = JSON.parse(userStr)
                if (userObj && userObj.role && userObj.email) {
                    setUser(userObj)
                }
            } catch (e) {
                console.error("Failed to parse user data", e)
            }
        }

        // Scroll effect
        const handleScroll = () => {
            setScrolled(window.scrollY > 20)
        }
        window.addEventListener('scroll', handleScroll)
        return () => window.removeEventListener('scroll', handleScroll)
    }, [])

    const handleLogout = () => {
        localStorage.removeItem('user')
        localStorage.removeItem('access_token')
        setUser(null)
        router.push('/login')
    }

    return (
        <nav className={`sticky top-0 z-50 transition-all duration-300 border-b ${scrolled ? 'bg-background/95 backdrop-blur-md border-border shadow-sm' : 'bg-background/0 border-transparent'
            }`}>
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex items-center justify-between h-16 relative">
                {/* Logo */}
                <Link href="/" className="flex items-center gap-2 group">
                    <div className="w-8 h-8 bg-primary rounded-lg flex items-center justify-center transition-transform group-hover:scale-110">
                        <Wrench className="w-5 h-5 text-primary-foreground" />
                    </div>
                    <span className="font-bold text-lg tracking-tight">TechFix Pro</span>
                </Link>

                {/* Centered Navigation */}
                <div className="hidden md:flex items-center gap-8 absolute left-1/2 top-1/2 transform -translate-x-1/2 -translate-y-1/2">
                    <Link href="/services" className="text-sm font-medium text-muted-foreground hover:text-primary transition-colors">
                        Dịch vụ
                    </Link>
                    <Link href="/booking/wizard" className="text-sm font-medium text-muted-foreground hover:text-primary transition-colors">
                        Đặt lịch
                    </Link>
                    <Link href="/tracking" className="text-sm font-medium text-muted-foreground hover:text-primary transition-colors">
                        Tra cứu
                    </Link>
                </div>

                {/* User Actions */}
                <div className="flex items-center gap-3">
                    {user ? (
                        <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                                <Button variant="ghost" className="relative h-9 w-9 rounded-full ring-2 ring-primary/10 hover:ring-primary/30 transition-all">
                                    <Avatar className="h-9 w-9">
                                        <AvatarImage src={user.avatar_url || "/avatars/01.png"} alt="@user" />
                                        <AvatarFallback className="bg-primary/5 text-primary font-bold">
                                            {user.email[0].toUpperCase()}
                                        </AvatarFallback>
                                    </Avatar>
                                </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent className="w-56" align="end" forceMount>
                                <DropdownMenuLabel className="font-normal">
                                    <div className="flex flex-col space-y-1">
                                        <p className="text-sm font-medium leading-none">Tài khoản</p>
                                        <p className="text-xs leading-none text-muted-foreground">
                                            {user.email}
                                        </p>
                                    </div>
                                </DropdownMenuLabel>
                                <DropdownMenuSeparator />

                                {(user.role === 'admin' || user.role === 'technician') && (
                                    <>
                                        <DropdownMenuItem asChild>
                                            <Link href={user.role === 'admin' ? '/admin' : '/technician'}>
                                                <LayoutDashboard className="mr-2 h-4 w-4" />
                                                <span>{user.role === 'admin' ? 'Trang Quản trị' : 'Bảng công việc'}</span>
                                            </Link>
                                        </DropdownMenuItem>
                                        <DropdownMenuSeparator />
                                    </>
                                )}

                                <DropdownMenuItem asChild>
                                    <Link href="/profile">
                                        <User className="mr-2 h-4 w-4" />
                                        <span>Hồ sơ cá nhân</span>
                                    </Link>
                                </DropdownMenuItem>
                                <DropdownMenuSeparator />
                                <DropdownMenuItem onClick={handleLogout} className="text-destructive focus:text-destructive">
                                    <LogOut className="mr-2 h-4 w-4" />
                                    <span>Đăng xuất</span>
                                </DropdownMenuItem>
                            </DropdownMenuContent>
                        </DropdownMenu>
                    ) : (
                        <div className="flex items-center gap-2">
                            <Link href="/login">
                                <Button variant="ghost" size="sm" className="font-medium text-muted-foreground hover:text-primary">
                                    Đăng nhập
                                </Button>
                            </Link>
                            <Link href="/register">
                                <Button size="sm" className="font-medium shadow-md shadow-primary/20">
                                    Đăng ký
                                </Button>
                            </Link>
                        </div>
                    )}
                </div>
            </div>
        </nav>
    )
}

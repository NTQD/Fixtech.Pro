'use client'

import { useState, useEffect, useRef } from 'react'
import { useRouter } from 'next/navigation'
import Link from 'next/link'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import { ArrowLeft, Camera, Loader2, Save, Eye, EyeOff } from 'lucide-react'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'

export default function ProfilePage() {
    const router = useRouter()
    const [isLoading, setIsLoading] = useState(true)
    const [role, setRole] = useState<string | null>(null)
    const [email, setEmail] = useState<string | null>(null)
    const [isSaving, setIsSaving] = useState(false)

    // Form Fields
    const [name, setName] = useState('')
    const [phone, setPhone] = useState('')
    const [avatarPreview, setAvatarPreview] = useState<string | null>(null)

    // Password Fields
    const [currentPassword, setCurrentPassword] = useState('')
    const [newPassword, setNewPassword] = useState('')
    const [confirmPassword, setConfirmPassword] = useState('')

    // Visibility State
    const [showCurrentPassword, setShowCurrentPassword] = useState(false)
    const [showNewPassword, setShowNewPassword] = useState(false)
    const [showConfirmPassword, setShowConfirmPassword] = useState(false)

    // Refs
    const fileInputRef = useRef<HTMLInputElement>(null)

    const [history, setHistory] = useState<any[]>([])

    useEffect(() => {
        const userStr = localStorage.getItem('user')
        const tokenStr = localStorage.getItem('access_token')

        if (!userStr) {
            router.push('/login')
            return
        }

        try {
            const user = JSON.parse(userStr)
            if (!user) {
                router.push('/login')
                return
            }

            const userRole = user.role.toLowerCase()
            const userEmail = user.email

            setRole(userRole)
            setEmail(userEmail)

            // Set User Info from LocalStorage
            setName(user.name || (userRole === 'admin' ? 'Administrator' : 'Khách hàng'))
            setPhone(user.phone || '')

            setIsLoading(false)

            // Fetch History
            if (tokenStr) {
                fetchHistory(tokenStr)
            }
        } catch (e) {
            router.push('/login')
        }
    }, [router])

    const fetchHistory = async (token: string) => {
        try {
            const res = await fetch('http://localhost:3000/bookings/my-bookings', {
                headers: { 'Authorization': `Bearer ${token}` }
            })
            if (res.ok) {
                const data = await res.json()
                setHistory(data)
            }
        } catch (err) {
            console.error('Failed to fetch history', err)
        }
    }

    const handleBack = () => {
        if (role === 'admin') router.push('/admin')
        else if (role?.includes('tech')) router.push('/technician')
        else router.push('/')
    }

    const handleAvatarClick = () => {
        fileInputRef.current?.click()
    }

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0]
        if (file) {
            const reader = new FileReader()
            reader.onloadend = () => {
                setAvatarPreview(reader.result as string)
            }
            reader.readAsDataURL(file)
        }
    }

    const handleSaveProfile = () => {
        setIsSaving(true)
        setTimeout(() => {
            setIsSaving(false)
            alert('Thông tin cá nhân và ảnh đại diện đã được cập nhật!')
        }, 1000)
    }

    const handleChangePassword = () => {
        if (newPassword !== confirmPassword) {
            alert('Mật khẩu mới không khớp!')
            return
        }
        setIsSaving(true)
        setTimeout(() => {
            setIsSaving(false)
            setCurrentPassword('')
            setNewPassword('')
            setConfirmPassword('')
            alert('Đổi mật khẩu thành công!')
        }, 1000)
    }

    if (isLoading) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <Loader2 className="h-8 w-8 animate-spin text-primary" />
            </div>
        )
    }

    const defaultAvatar = role === 'admin' ? '/avatars/admin.png' : role?.includes('tech') ? '/avatars/tech.png' : '/avatars/user.png'
    const avatarFallback = role === 'admin' ? 'AD' : role?.includes('tech') ? 'AT' : 'US'
    const passwordsMatch = newPassword === confirmPassword || confirmPassword === ''

    return (
        <div className="min-h-screen bg-muted/20 p-4 md:p-8 font-sans">
            <div className="max-w-4xl mx-auto space-y-6">
                <div className="flex items-center gap-4">
                    <Button variant="ghost" size="icon" onClick={handleBack}>
                        <ArrowLeft className="h-5 w-5" />
                    </Button>
                    <h1 className="text-2xl font-bold tracking-tight">Hồ sơ cá nhân</h1>
                </div>

                <div className="grid md:grid-cols-[300px_1fr] gap-6">
                    {/* Sidebar Info Card */}
                    <Card>
                        <CardHeader className="text-center">
                            <div className="mx-auto w-32 h-32 relative mb-4 group cursor-pointer" onClick={handleAvatarClick}>
                                <Avatar className="w-32 h-32 transition-opacity group-hover:opacity-80">
                                    <AvatarImage src={avatarPreview || defaultAvatar} className="object-cover" />
                                    <AvatarFallback className="text-4xl">{avatarFallback}</AvatarFallback>
                                </Avatar>
                                <div className="absolute inset-0 flex items-center justify-center bg-black/40 rounded-full opacity-0 group-hover:opacity-100 transition-opacity">
                                    <Camera className="h-8 w-8 text-white" />
                                </div>
                                <input
                                    type="file"
                                    ref={fileInputRef}
                                    className="hidden"
                                    accept="image/*"
                                    onChange={handleFileChange}
                                />
                            </div>
                            <Button variant="outline" size="sm" onClick={handleAvatarClick}>
                                Thay đổi ảnh
                            </Button>

                            <div className="mt-4">
                                <CardTitle>{name}</CardTitle>
                                <CardDescription className="capitalize">{role}</CardDescription>
                            </div>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            <div className="space-y-1">
                                <Label className="text-xs text-muted-foreground">Email</Label>
                                <div className="font-medium break-all">{email}</div>
                            </div>
                            <div className="space-y-1">
                                <Label className="text-xs text-muted-foreground">Số điện thoại</Label>
                                <div className="font-medium">{phone}</div>
                            </div>
                            <div className="space-y-1">
                                <Label className="text-xs text-muted-foreground">Tham gia từ</Label>
                                <div className="font-medium">01/01/2024</div>
                            </div>
                        </CardContent>
                    </Card>

                    {/* Main Content Tabs */}
                    <div className="space-y-6">
                        <Tabs defaultValue="general" className="w-full">
                            <TabsList className="grid w-full grid-cols-3">
                                <TabsTrigger value="general">Thông tin chung</TabsTrigger>
                                <TabsTrigger value="security">Bảo mật</TabsTrigger>
                                <TabsTrigger value="bookings">Lịch sử đơn hàng</TabsTrigger>
                            </TabsList>

                            <TabsContent value="general">
                                <Card>
                                    <CardHeader>
                                        <CardTitle>Thông tin cơ bản</CardTitle>
                                        <CardDescription>
                                            Cập nhật thông tin hiển thị của bạn trên hệ thống.
                                        </CardDescription>
                                    </CardHeader>
                                    <CardContent className="space-y-4">
                                        <div className="space-y-2">
                                            <Label htmlFor="name">Họ và tên</Label>
                                            <Input
                                                id="name"
                                                value={name}
                                                onChange={(e) => setName(e.target.value)}
                                            />
                                        </div>
                                        <div className="space-y-2">
                                            <Label htmlFor="phone">Số điện thoại</Label>
                                            <Input
                                                id="phone"
                                                value={phone}
                                                onChange={(e) => setPhone(e.target.value)}
                                                type="tel"
                                            />
                                        </div>
                                        <div className="space-y-2">
                                            <Label htmlFor="email">Email</Label>
                                            <Input id="email" value={email || ''} disabled className="bg-muted" />
                                            <p className="text-xs text-muted-foreground">Không thể thay đổi địa chỉ email.</p>
                                        </div>
                                    </CardContent>
                                    <CardFooter className="flex justify-end">
                                        <Button onClick={handleSaveProfile} disabled={isSaving}>
                                            {isSaving && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                                            Lưu thay đổi
                                        </Button>
                                    </CardFooter>
                                </Card>
                            </TabsContent>

                            <TabsContent value="security">
                                <Card>
                                    <CardHeader>
                                        <CardTitle>Đổi mật khẩu</CardTitle>
                                        <CardDescription>
                                            Thay đổi mật khẩu đăng nhập của bạn.
                                        </CardDescription>
                                    </CardHeader>
                                    <CardContent className="space-y-4">
                                        <div className="space-y-2">
                                            <Label htmlFor="current-password">Mật khẩu hiện tại</Label>
                                            <div className="relative">
                                                <Input
                                                    id="current-password"
                                                    type={showCurrentPassword ? "text" : "password"}
                                                    value={currentPassword}
                                                    onChange={(e) => setCurrentPassword(e.target.value)}
                                                />
                                                <Button
                                                    type="button"
                                                    variant="ghost"
                                                    size="icon"
                                                    className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                                                    onClick={() => setShowCurrentPassword(!showCurrentPassword)}
                                                >
                                                    {showCurrentPassword ? (
                                                        <EyeOff className="h-4 w-4 text-muted-foreground" />
                                                    ) : (
                                                        <Eye className="h-4 w-4 text-muted-foreground" />
                                                    )}
                                                </Button>
                                            </div>
                                        </div>
                                        <div className="space-y-2">
                                            <Label htmlFor="new-password">Mật khẩu mới</Label>
                                            <div className="relative">
                                                <Input
                                                    id="new-password"
                                                    type={showNewPassword ? "text" : "password"}
                                                    value={newPassword}
                                                    onChange={(e) => setNewPassword(e.target.value)}
                                                />
                                                <Button
                                                    type="button"
                                                    variant="ghost"
                                                    size="icon"
                                                    className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                                                    onClick={() => setShowNewPassword(!showNewPassword)}
                                                >
                                                    {showNewPassword ? (
                                                        <EyeOff className="h-4 w-4 text-muted-foreground" />
                                                    ) : (
                                                        <Eye className="h-4 w-4 text-muted-foreground" />
                                                    )}
                                                </Button>
                                            </div>
                                        </div>
                                        <div className="space-y-2">
                                            <Label htmlFor="confirm-password">Xác nhận mật khẩu mới</Label>
                                            <div className="relative">
                                                <Input
                                                    id="confirm-password"
                                                    type={showConfirmPassword ? "text" : "password"}
                                                    value={confirmPassword}
                                                    onChange={(e) => setConfirmPassword(e.target.value)}
                                                    className={!passwordsMatch ? 'border-destructive' : ''}
                                                />
                                                <Button
                                                    type="button"
                                                    variant="ghost"
                                                    size="icon"
                                                    className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                                                    onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                                >
                                                    {showConfirmPassword ? (
                                                        <EyeOff className="h-4 w-4 text-muted-foreground" />
                                                    ) : (
                                                        <Eye className="h-4 w-4 text-muted-foreground" />
                                                    )}
                                                </Button>
                                            </div>
                                            {!passwordsMatch && (
                                                <p className="text-xs text-destructive">Mật khẩu xác nhận không khớp.</p>
                                            )}
                                        </div>
                                    </CardContent>
                                    <CardFooter className="flex justify-end">
                                        <Button
                                            onClick={handleChangePassword}
                                            disabled={isSaving || !passwordsMatch || !currentPassword || !newPassword}
                                        >
                                            {isSaving && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                                            Đổi mật khẩu
                                        </Button>
                                    </CardFooter>
                                </Card>
                            </TabsContent>

                            <TabsContent value="bookings">
                                <Card>
                                    <CardHeader>
                                        <CardTitle>Lịch sử đơn hàng</CardTitle>
                                        <CardDescription>
                                            Theo dõi các đơn hàng sửa chữa của bạn.
                                        </CardDescription>
                                    </CardHeader>
                                    <CardContent className="space-y-4">
                                        {history.length === 0 ? (
                                            <p className="text-muted-foreground text-center py-4">Bạn chưa có đơn hàng nào.</p>
                                        ) : (
                                            history.map((booking: any) => {
                                                const getStatusColor = (status: string) => {
                                                    switch (status) {
                                                        case 'PENDING': return 'bg-yellow-100 text-yellow-800 border-yellow-200'
                                                        case 'CONFIRMED': return 'bg-orange-100 text-orange-800 border-orange-200'
                                                        case 'IN_PROGRESS': return 'bg-blue-100 text-blue-800 border-blue-200'
                                                        case 'COMPLETED': return 'bg-green-100 text-green-800 border-green-200'
                                                        case 'CANCELLED': return 'bg-red-100 text-red-800 border-red-200'
                                                        default: return 'bg-gray-100 text-gray-800 border-gray-200'
                                                    }
                                                }

                                                return (
                                                    <div key={booking.id} className="flex items-center justify-between p-4 border rounded-lg hover:bg-muted/50 transition-colors cursor-pointer bg-card">
                                                        <div className="space-y-1">
                                                            <div className="flex items-center gap-2">
                                                                <span className="font-semibold text-lg">{booking.device_info.split(' - ')[0]}</span>
                                                                <span className={`text-xs px-2.5 py-0.5 rounded-full font-medium border ${getStatusColor(booking.status)}`}>
                                                                    {booking.status}
                                                                </span>
                                                            </div>
                                                            <p className="text-sm text-muted-foreground">
                                                                Mã đơn: <span className="font-mono text-foreground font-medium">#{booking.id || `BK-${booking.id}`}</span> • {new Date(booking.scheduled_date).toLocaleDateString('vi-VN')}
                                                            </p>
                                                            {booking.status === 'COMPLETED' && booking.total_amount > 0 && (
                                                                <p className="text-sm font-medium text-primary pt-1">
                                                                    Tổng tiền: {Number(booking.total_amount).toLocaleString()} đ
                                                                </p>
                                                            )}
                                                        </div>
                                                        <Button variant="outline" size="sm" asChild className="hover:bg-primary hover:text-primary-foreground transition-colors">
                                                            <Link href={`/tracking?id=${booking.id}`}>Chi tiết</Link>
                                                        </Button>
                                                    </div>
                                                )
                                            })
                                        )}
                                    </CardContent>
                                </Card>
                            </TabsContent>
                        </Tabs>
                    </div>
                </div>
            </div>
        </div>
    )
}

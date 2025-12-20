'use client'

import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import Link from 'next/link'
import { useState } from 'react'
import { useRouter } from 'next/navigation'
import { Wrench, Eye, EyeOff } from 'lucide-react'

export default function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [error, setError] = useState('')

  const router = useRouter()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')

    try {
      const response = await fetch('http://localhost:3000/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          email,
          password
        }),
      })

      if (response.ok) {
        const data = await response.json()
        if (data.access_token) {
          // Save token and user info
          localStorage.setItem('access_token', data.access_token)
          localStorage.setItem('user', JSON.stringify(data.user))

          // Redirect based on role
          if (data.user.role === 'ADMIN') {
            router.push('/admin')
          } else if (data.user.role === 'TECHNICIAN') {
            router.push('/technician')
          } else {
            router.push('/')
          }
        }
      } else {
        const errData = await response.json();
        if (errData.message === 'Account is banned') {
          setError('Tài khoản của bạn đã bị cấm. Vui lòng liên hệ quản trị viên.');
        } else {
          setError('Đăng nhập thất bại. Kiểm tra lại email hoặc mật khẩu.');
        }
      }
    } catch (err) {
      console.error(err)
      setError('Lỗi kết nối đến server.')
    }
  }

  return (
    <div className="min-h-screen bg-background flex items-center justify-center px-4">
      <div className="w-full max-w-md">
        <div className="space-y-8">
          {/* Logo */}
          <div className="flex flex-col items-center gap-3">
            <div className="w-12 h-12 bg-primary rounded-lg flex items-center justify-center">
              <Wrench className="w-6 h-6 text-primary-foreground" />
            </div>
            <h1 className="text-2xl font-bold">TechFix Pro</h1>
          </div>

          {/* Form */}
          <div className="bg-card p-8 rounded-lg border border-border">
            <h2 className="text-2xl font-bold mb-6 text-center">Đăng nhập</h2>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="text-sm font-medium">Email</label>
                <Input
                  type="email"
                  placeholder="your@email.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="mt-1"
                />
              </div>
              <div>
                <label className="text-sm font-medium">Mật khẩu</label>
                <div className="relative mt-1">
                  <Input
                    type={showPassword ? "text" : "password"}
                    placeholder="••••••••"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="pr-10"
                  />
                  <Button
                    type="button"
                    variant="ghost"
                    size="icon"
                    className="absolute right-0 top-0 h-full px-3 py-2 hover:bg-transparent"
                    onClick={() => setShowPassword(!showPassword)}
                  >
                    {showPassword ? (
                      <EyeOff className="h-4 w-4 text-muted-foreground" />
                    ) : (
                      <Eye className="h-4 w-4 text-muted-foreground" />
                    )}
                    <span className="sr-only">
                      {showPassword ? "Ẩn mật khẩu" : "Hiện mật khẩu"}
                    </span>
                  </Button>
                </div>
              </div>
              {error && <p className="text-sm text-red-500 font-medium mb-2">{error}</p>}
              <Button type="submit" className="w-full">Đăng nhập</Button>
            </form>
            <div className="mt-6 text-center text-sm">
              <span className="text-muted-foreground">Chưa có tài khoản? </span>
              <Link href="/register" className="text-primary hover:underline font-medium">Đăng ký ngay</Link>
            </div>
          </div>

          {/* Back Link */}
          <div className="text-center">
            <Link href="/" className="text-primary hover:underline text-sm">Quay lại trang chủ</Link>
          </div>
        </div>
      </div>
    </div>
  )
}

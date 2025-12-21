'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { toast } from 'sonner'
import { Card, CardContent } from '@/components/ui/card'
import { Navbar } from '@/components/layout/Navbar'
import { Footer } from '@/components/layout/Footer'
import { Laptop, Wrench, Calendar as CalendarIcon, User, CheckCircle, ArrowRight, ArrowLeft } from 'lucide-react'

// Steps Definition
const STEPS = [
    { id: 1, title: 'Thiết bị', icon: Laptop },
    { id: 2, title: 'Dịch vụ', icon: Wrench },
    { id: 3, title: 'Thời gian', icon: CalendarIcon },
    { id: 4, title: 'Xác nhận', icon: User },
]

export default function BookingWizardPage() {
    const [currentStep, setCurrentStep] = useState(1)
    const [bookingId, setBookingId] = useState<number | null>(null)
    const [formData, setFormData] = useState({
        // Step 1: Device
        deviceType: 'Laptop',
        brand: '',
        model: '',
        serial: '',
        issueDescription: '',

        // Step 2: Service
        serviceType: '', // 'repair', 'upgrade', 'maintenance'
        selectedServices: [],

        // Step 3: Schedule
        date: '',
        time: '',

        // Step 4: Contact (if not logged in) or Auto-fill
        name: '',
        phone: '',
        email: '',
        notes: ''
    })

    const router = useRouter()

    // Navigation Handlers
    const nextStep = async () => {
        if (currentStep === 4) {
            await handleSubmit()
        } else {
            setCurrentStep(prev => Math.min(prev + 1, STEPS.length + 1))
        }
    }
    const prevStep = () => setCurrentStep(prev => Math.max(prev - 1, 1))

    const handleSubmit = async () => {
        const token = localStorage.getItem('access_token')
        if (!token) {
            toast.error('Vui lòng đăng nhập để đặt lịch!')
            router.push('/login')
            return
        }

        // Map simplified wizard services to Database IDs (Strings)
        const serviceMapping: Record<string, string> = {
            'repair': 'repair-hw',
            'upgrade': 'upgrade-comp',
            'maintenance': 'maintenance-gen',
            'software': 'software-install'
        }

        const selectedServiceId = serviceMapping[formData.serviceType] || 'repair-hw' // Default fallback

        const payload = {
            customer_name: formData.name,
            customer_phone: formData.phone,
            customer_email: formData.email,
            device_info: `${formData.deviceType} - ${formData.brand} ${formData.model} (${formData.serial})`,
            issue_description: `${formData.issueDescription} \nNotes: ${formData.notes}`,
            scheduled_date: formData.date,
            scheduled_time: formData.time,
            items: [
                {
                    service_id: selectedServiceId,
                    quantity: 1
                }
            ]
        }

        console.log('Sending Wizard Payload:', payload)

        try {
            const res = await fetch('http://localhost:3000/bookings', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            })

            if (res.ok) {
                const data = await res.json()
                console.log('Wizard Success:', data)
                setBookingId(data.id)
                setCurrentStep(5) // Move to Success Step
            } else {
                const errData = await res.json()
                console.error('Wizard Error:', JSON.stringify(errData, null, 2))
                toast.error(`Lỗi đặt lịch: ${errData.message || JSON.stringify(errData)}`)
            }
        } catch (error) {
            console.error('Network Error:', error)
            toast.error('Lỗi kết nối server!')
        }
    }

    // Render Steps
    const renderStepContent = () => {
        switch (currentStep) {
            case 1:
                return <DeviceStep data={formData} updateData={setFormData} />
            case 2:
                return <ServiceStep data={formData} updateData={setFormData} />
            case 3:
                return <ScheduleStep data={formData} updateData={setFormData} />
            case 4:
                return <ConfirmationStep data={formData} updateData={setFormData} />
            case 5:
                return <SuccessStep bookingId={bookingId} />
            default:
                return null
        }
    }

    return (
        <div className="min-h-screen bg-background flex flex-col">
            <Navbar />

            <main className="flex-grow py-12 px-4 sm:px-6 lg:px-8 bg-muted/20">
                <div className="max-w-4xl mx-auto">
                    {/* Progress Bar */}
                    <div className="mb-8">
                        <div className="flex items-center justify-between relative">
                            <div className="absolute left-0 top-1/2 transform -translate-y-1/2 w-full h-1 bg-muted -z-10" />
                            <div
                                className="absolute left-0 top-1/2 transform -translate-y-1/2 h-1 bg-primary -z-10 transition-all duration-500"
                                style={{ width: `${((currentStep - 1) / (STEPS.length - 1)) * 100}%` }}
                            />

                            {STEPS.map((step) => {
                                const isActive = currentStep >= step.id
                                const isCurrent = currentStep === step.id
                                return (
                                    <div key={step.id} className="flex flex-col items-center gap-2 bg-background px-2">
                                        <div className={`w-10 h-10 rounded-full flex items-center justify-center border-2 transition-all ${isActive ? 'border-primary bg-primary text-primary-foreground' : 'border-muted-foreground text-muted-foreground'
                                            } ${isCurrent ? 'ring-4 ring-primary/20' : ''}`}>
                                            <step.icon className="w-5 h-5" />
                                        </div>
                                        <span className={`text-sm font-medium ${isActive ? 'text-primary' : 'text-muted-foreground'}`}>{step.title}</span>
                                    </div>
                                )
                            })}
                        </div>
                    </div>

                    {/* Step Content */}
                    <Card className="border-none shadow-xl">
                        <CardContent className="p-6 sm:p-10">
                            {renderStepContent()}

                            {/* Navigation Buttons (Hide on Success Step) */}
                            {currentStep <= 4 && (
                                <div className="flex justify-between mt-8 pt-6 border-t">
                                    <Button
                                        variant="outline"
                                        onClick={prevStep}
                                        disabled={currentStep === 1}
                                        className="gap-2"
                                    >
                                        <ArrowLeft className="w-4 h-4" /> Quay lại
                                    </Button>
                                    <Button onClick={nextStep} className="gap-2">
                                        {currentStep === 4 ? 'Xác nhận Đặt lịch' : 'Tiếp tục'} <ArrowRight className="w-4 h-4" />
                                    </Button>
                                </div>
                            )}
                        </CardContent>
                    </Card>
                </div>
            </main>

            <Footer />
        </div>
    )
}

// --- Sub-components (Placeholders for now) ---
function DeviceStep({ data, updateData }: any) {
    return (
        <div className="space-y-6">
            <div className="text-center mb-8">
                <h2 className="text-2xl font-bold">Thông tin thiết bị</h2>
                <p className="text-muted-foreground">Cho chúng tôi biết về thiết bị cần sửa chữa của bạn</p>
            </div>

            <div className="grid md:grid-cols-2 gap-6">
                <div className="space-y-2">
                    <label className="text-sm font-medium">Hãng máy (Brand)</label>
                    <Input
                        placeholder="Ví dụ: Dell, HP, MacBook..."
                        value={data.brand}
                        onChange={(e) => updateData({ ...data, brand: e.target.value })}
                    />
                </div>
                <div className="space-y-2">
                    <label className="text-sm font-medium">Model (Nếu biết)</label>
                    <Input
                        placeholder="Ví dụ: XPS 15 9500"
                        value={data.model}
                        onChange={(e) => updateData({ ...data, model: e.target.value })}
                    />
                </div>
                <div className="space-y-2 md:col-span-2">
                    <label className="text-sm font-medium">Mô tả vấn đề</label>
                    <textarea
                        className="w-full px-3 py-2 border rounded-md min-h-[100px] bg-background text-sm"
                        placeholder="Mô tả chi tiết vấn đề bạn đang gặp phải..."
                        value={data.issueDescription}
                        onChange={(e) => updateData({ ...data, issueDescription: e.target.value })}
                    />
                </div>
            </div>
        </div>
    )
}

function ServiceStep({ data, updateData }: any) {
    const services = [
        { id: 'repair', title: 'Sửa chữa', icon: Wrench, desc: 'Khắc phục lỗi phần cứng, mất nguồn, vỡ màn hình...' },
        { id: 'upgrade', title: 'Nâng cấp', icon: Laptop, desc: 'Nâng cấp RAM, SSD, CPU để máy mạnh hơn.' },
        { id: 'maintenance', title: 'Bảo dưỡng', icon: CheckCircle, desc: 'Vệ sinh, tra keo tản nhiệt, cài lại Win.' },
        { id: 'software', title: 'Cài đặt Phần mềm', icon: Laptop, desc: 'Cài Win, Office, Photoshop...' },
    ]

    return (
        <div className="space-y-6">
            <div className="text-center mb-8">
                <h2 className="text-2xl font-bold">Chọn dịch vụ</h2>
                <p className="text-muted-foreground">Bạn cần chúng tôi giúp gì với thiết bị này?</p>
            </div>

            <div className="grid md:grid-cols-3 gap-6">
                {services.map((s) => {
                    const isSelected = data.serviceType === s.id
                    return (
                        <div
                            key={s.id}
                            onClick={() => updateData({ ...data, serviceType: s.id })}
                            className={`cursor-pointer rounded-xl border-2 p-6 transition-all hover:border-primary/50 hover:bg-muted/50 ${isSelected ? 'border-primary bg-primary/5 ring-2 ring-primary/20' : 'border-border'
                                }`}
                        >
                            <div className={`w-12 h-12 rounded-lg flex items-center justify-center mb-4 ${isSelected ? 'bg-primary text-primary-foreground' : 'bg-muted text-muted-foreground'
                                }`}>
                                <s.icon className="w-6 h-6" />
                            </div>
                            <h3 className="font-bold text-lg mb-2">{s.title}</h3>
                            <p className="text-sm text-muted-foreground">{s.desc}</p>
                        </div>
                    )
                })}
            </div>

            {data.serviceType && (
                <div className="mt-8 p-4 bg-blue-50 dark:bg-blue-900/20 text-blue-800 dark:text-blue-200 rounded-lg text-sm flex items-center gap-2">
                    <div className="w-2 h-2 bg-blue-500 rounded-full" />
                    <span>Bạn đã chọn: <strong>{services.find(s => s.id === data.serviceType)?.title}</strong></span>
                </div>
            )}
        </div>
    )
}

function ScheduleStep({ data, updateData }: any) {
    const isValidTime = !data.time || (data.time >= "08:00" && data.time <= "20:00")

    return (
        <div className="space-y-6">
            <div className="text-center mb-8">
                <h2 className="text-2xl font-bold">Chọn lịch hẹn</h2>
                <p className="text-muted-foreground">Khi nào bạn có thể mang máy đến?</p>
            </div>

            <div className="grid md:grid-cols-2 gap-8 max-w-2xl mx-auto">
                <div className="space-y-4">
                    <label className="text-sm font-medium">Ngày dự kiến</label>
                    <Input
                        type="date"
                        className="w-full h-12 text-lg"
                        value={data.date}
                        onChange={(e) => updateData({ ...data, date: e.target.value })}
                    />
                    <p className="text-xs text-muted-foreground">Chúng tôi làm việc từ 8:00 - 20:00 hàng ngày</p>
                </div>

                <div className="space-y-4">
                    <label className="text-sm font-medium">Giờ hẹn (Dự kiến)</label>
                    <Input
                        type="time"
                        className={`w-full h-12 text-lg ${!isValidTime ? 'border-destructive focus-visible:ring-destructive' : ''}`}
                        value={data.time}
                        onChange={(e) => updateData({ ...data, time: e.target.value })}
                    />
                    {!isValidTime && (
                        <p className="text-sm text-destructive font-medium">
                            Giờ làm việc từ 08:00 - 20:00. Vui lòng chọn lại.
                        </p>
                    )}
                </div>
            </div>

            {data.date && data.time && (
                <div className={`text-center mt-6 p-4 border rounded-lg ${isValidTime
                    ? 'bg-green-50 dark:bg-green-900/10 text-green-700 dark:text-green-300'
                    : 'bg-destructive/10 text-destructive border-destructive/20'}`}>
                    Lịch hẹn: <strong>{data.time}</strong> ngày <strong>{data.date}</strong>
                    {!isValidTime && <span className="block mt-1 font-bold"> (Ngoài giờ làm việc)</span>}
                </div>
            )}
        </div>
    )
}

function ConfirmationStep({ data, updateData }: any) {
    return (
        <div className="space-y-8">
            <div className="text-center mb-4">
                <h2 className="text-2xl font-bold">Xác nhận & Thông tin liên hệ</h2>
                <p className="text-muted-foreground">Kiểm tra lại thông tin và để lại liên lạc</p>
            </div>

            <div className="grid md:grid-cols-2 gap-8">
                {/* Summary Card */}
                <div className="space-y-4 p-6 border rounded-xl bg-muted/30">
                    <h3 className="font-semibold text-lg flex items-center gap-2">
                        <Laptop className="w-4 h-4" /> Tóm tắt yêu cầu
                    </h3>
                    <div className="space-y-2 text-sm">
                        <div className="flex justify-between py-2 border-b">
                            <span className="text-muted-foreground">Thiết bị:</span>
                            <span className="font-medium">{data.brand} {data.model}</span>
                        </div>
                        <div className="flex justify-between py-2 border-b">
                            <span className="text-muted-foreground">Dịch vụ:</span>
                            <span className="font-medium uppercase">{data.serviceType}</span>
                        </div>
                        <div className="flex justify-between py-2 border-b">
                            <span className="text-muted-foreground">Vấn đề:</span>
                            <span className="font-medium truncate max-w-[150px]">{data.issueDescription}</span>
                        </div>
                        <div className="flex justify-between py-2 border-b">
                            <span className="text-muted-foreground">Lịch hẹn:</span>
                            <span className="font-medium text-primary">{data.time} - {data.date}</span>
                        </div>
                    </div>
                </div>

                {/* Contact Form */}
                <div className="space-y-4">
                    <h3 className="font-semibold text-lg flex items-center gap-2">
                        <User className="w-4 h-4" /> Thông tin khách hàng
                    </h3>

                    <div className="space-y-2">
                        <label className="text-sm">Họ và tên</label>
                        <Input
                            value={data.name}
                            onChange={(e) => updateData({ ...data, name: e.target.value })}
                            placeholder="Nguyễn Văn A"
                        />
                    </div>

                    <div className="space-y-2">
                        <label className="text-sm">Số điện thoại <span className="text-destructive">*</span></label>
                        <Input
                            value={data.phone}
                            onChange={(e) => updateData({ ...data, phone: e.target.value })}
                            placeholder="0912 345 678"
                        />
                    </div>

                    <div className="space-y-2">
                        <label className="text-sm">Email (Để nhận hóa đơn)</label>
                        <Input
                            value={data.email}
                            onChange={(e) => updateData({ ...data, email: e.target.value })}
                            placeholder="email@example.com"
                        />
                    </div>
                </div>
            </div>
        </div>
    )
}

function SuccessStep({ bookingId }: { bookingId: number | null }) {
    return (
        <div className="text-center py-10">
            <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6">
                <CheckCircle className="w-10 h-10 text-green-600" />
            </div>
            <h2 className="text-2xl font-bold mb-2">Đặt lịch thành công!</h2>
            <p className="text-muted-foreground mb-6">Mã đơn hàng của bạn là <span className="font-mono font-bold text-foreground">
                {bookingId ? `#ORD-${bookingId}` : '...'}
            </span></p>
            <Button asChild>
                <a href="/tracking">Theo dõi đơn hàng</a>
            </Button>
        </div>
    )
}

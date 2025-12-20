'use client'

import { useState, useEffect, Suspense } from 'react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Navbar } from '@/components/layout/Navbar'
import { Footer } from '@/components/layout/Footer'
import { Search, CheckCircle, Clock, Wrench } from 'lucide-react'
import { useSearchParams } from 'next/navigation'

function TrackingContent() {
    const searchParams = useSearchParams()
    const initialOrderId = searchParams.get('id') || ''

    const [orderId, setOrderId] = useState(initialOrderId)
    const [trackingResult, setTrackingResult] = useState<any>(null)
    const [isLoading, setIsLoading] = useState(false)
    const [error, setError] = useState('')

    useEffect(() => {
        if (initialOrderId) {
            handleSearch(null, initialOrderId)
        }
    }, [initialOrderId])

    const handleSearch = async (e: React.FormEvent | null, idOverride?: string) => {
        if (e) e.preventDefault()
        const query = idOverride || orderId
        if (!query) return

        setIsLoading(true)
        setError('')
        setTrackingResult(null)

        try {
            const res = await fetch(`http://localhost:3000/bookings/search?q=${encodeURIComponent(query)}`)
            if (!res.ok) throw new Error('Không tìm thấy đơn hàng')

            const data = await res.json()
            if (data.length === 0) {
                setError('Không tìm thấy đơn hàng nào với thông tin này.')
            } else {
                // Prioritize Exact ID Match
                const cleanQuery = query.replace(/\D/g, ''); // Extract '3' from 'ORD-3'
                const exactMatch = data.find((b: any) => b.id.toString() === cleanQuery);
                const booking = exactMatch || data[0];

                setTrackingResult(mapBookingToTracking(booking))
            }
        } catch (err) {
            console.error(err)
            setError('Có lỗi xảy ra khi tra cứu. Vui lòng thử lại.')
        } finally {
            setIsLoading(false)
        }
    }

    const mapBookingToTracking = (b: any) => {
        // Map backend status to timeline steps
        const steps = [
            { id: 'PENDING', label: 'Đã tiếp nhận' },
            { id: 'CONFIRMED', label: 'Đang kiểm tra' },
            { id: 'IN_PROGRESS', label: 'Đang sửa chữa' },
            { id: 'COMPLETED', label: 'Hoàn tất' }
        ]

        const history = b.history || [];
        const currentStatus = b.status;

        // Helper to find time for a specific status
        const getTimeForStatus = (statusId: string) => {
            // Find the *first* time this status was recorded (or last, depending on preference. Usually first occurrence)
            // But usually we want to know when it *entered* that stage.
            const entry = history.find((h: any) => h.status === statusId);
            if (entry) {
                return new Date(entry.created_at).toLocaleString('vi-VN');
            }
            // Fallback for PENDING if history missing (legacy)
            if (statusId === 'PENDING' && b.created_at) {
                return new Date(b.created_at).toLocaleString('vi-VN');
            }
            return '-';
        }

        // Determine if a step is "passed" or "active"
        // We can use the index of the current status in the steps array
        const statusOrder = ['PENDING', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'];
        const currentStatusIndex = statusOrder.indexOf(currentStatus);

        const timeline = steps.map((s, index) => {
            const isCompleted = index <= currentStatusIndex && currentStatusIndex !== -1;
            const time = getTimeForStatus(s.id);

            return {
                status: s.id,
                label: s.label,
                completed: isCompleted,
                time: isCompleted && time === '-' ? (index === currentStatusIndex ? new Date(b.updated_at).toLocaleString('vi-VN') : '-') : time
            }
        })

        return {
            id: b.id,
            status: b.status,
            device: b.device_info,
            service: b.issue_description,
            estimatedCompletion: b.scheduled_date ? new Date(b.scheduled_date).toLocaleDateString('vi-VN') : 'N/A',
            timeline,
            items: b.items || [],
            total_amount: b.total_amount
        }
    }

    return (
        <div className="max-w-3xl mx-auto space-y-8">
            <div className="text-center space-y-4">
                <h1 className="text-3xl font-bold tracking-tight">Tra cứu đơn hàng</h1>
                <p className="text-muted-foreground">Nhập mã đơn hàng hoặc số điện thoại để theo dõi tiến độ sửa chữa</p>
            </div>

            {/* Search Box */}
            <Card>
                <CardContent className="p-6">
                    <form onSubmit={handleSearch} className="flex gap-4">
                        <Input
                            placeholder="Mã đơn hàng (VD: 1) hoặc SĐT"
                            className="text-lg h-12"
                            value={orderId}
                            onChange={(e) => setOrderId(e.target.value)}
                        />
                        <Button size="lg" className="h-12 px-8" disabled={isLoading}>
                            {isLoading ? <Clock className="w-5 h-5 animate-spin mr-2" /> : <Search className="w-5 h-5 mr-2" />}
                            Tra cứu
                        </Button>
                    </form>
                    {error && <p className="text-destructive mt-2 text-center">{error}</p>}
                </CardContent>
            </Card>

            {/* Result */}
            {trackingResult && (
                <Card className="overflow-hidden">
                    <CardHeader className="bg-primary/5 border-b">
                        <div className="flex justify-between items-start">
                            <div>
                                <CardTitle className="text-xl">Đơn hàng #BK-{trackingResult.id}</CardTitle>
                                <p className="text-sm text-muted-foreground mt-1">Thiết bị: {trackingResult.device}</p>
                            </div>
                            <div className="flex gap-2 items-center">
                                {['PENDING', 'CONFIRMED'].includes(trackingResult.status) && (
                                    <Button
                                        variant="destructive"
                                        size="sm"
                                        onClick={async () => {
                                            if (confirm('Bạn có chắc chắn muốn hủy đơn hàng này không? Hành động này không thể hoàn tác.')) {
                                                try {
                                                    const token = localStorage.getItem('token');
                                                    const headers: any = { 'Content-Type': 'application/json' };
                                                    if (token) headers['Authorization'] = `Bearer ${token}`;

                                                    const res = await fetch(`http://localhost:3000/bookings/${trackingResult.id}/cancel`, {
                                                        method: 'PATCH',
                                                        headers
                                                    });

                                                    if (!res.ok) {
                                                        const err = await res.json();
                                                        throw new Error(err.message || 'Hủy thất bại');
                                                    }

                                                    alert('Đã hủy đơn hàng thành công');
                                                    handleSearch(null, trackingResult.id.toString()); // Refresh
                                                } catch (e: any) {
                                                    alert(e.message);
                                                }
                                            }
                                        }}
                                    >
                                        Hủy đơn hàng
                                    </Button>
                                )}
                                <div className={`px-3 py-1 rounded-full text-sm font-medium ${trackingResult.status === 'CANCELLED' ? 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-300' :
                                    'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300'
                                    }`}>
                                    {trackingResult.status}
                                </div>
                            </div>
                        </div>
                    </CardHeader>
                    <CardContent className="p-8">
                        {/* Progress Timeline */}
                        <div className="relative">
                            <div className="absolute left-[19px] top-0 bottom-0 w-0.5 bg-muted -z-10" />
                            <div className="space-y-8">
                                {trackingResult.timeline.map((step: any, index: number) => (
                                    <div key={index} className="flex gap-6">
                                        <div className={`w-10 h-10 rounded-full flex items-center justify-center border-2 bg-background ${step.completed ? 'border-primary text-primary' : 'border-muted text-muted-foreground'
                                            }`}>
                                            {step.completed ? <CheckCircle className="w-5 h-5" /> : <Clock className="w-5 h-5" />}
                                        </div>
                                        <div className="pt-1">
                                            <h4 className={`font-semibold ${step.completed ? 'text-foreground' : 'text-muted-foreground'}`}>
                                                {step.label}
                                            </h4>
                                            <p className="text-sm text-muted-foreground">{step.time}</p>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>

                        <div className="mt-8 pt-8 border-t grid grid-cols-2 gap-4">
                            <div>
                                <h4 className="font-semibold mb-1">Mô tả lỗi</h4>
                                <p className="text-muted-foreground">{trackingResult.service}</p>
                            </div>
                            <div>
                                <h4 className="font-semibold mb-1">Dự kiến hoàn thành</h4>
                                <p className="text-muted-foreground">{trackingResult.estimatedCompletion}</p>
                            </div>
                        </div>
                    </CardContent>
                </Card>
            )}

            {/* Invoice Details [NEW] */}
            {trackingResult && trackingResult.items.length > 0 && (
                <Card>
                    <CardHeader className="border-b">
                        <CardTitle className="text-xl">Chi tiết dịch vụ & Linh kiện</CardTitle>
                    </CardHeader>
                    <CardContent className="p-0">
                        <div className="overflow-x-auto">
                            <table className="w-full text-sm">
                                <thead className="bg-muted/50 border-b">
                                    <tr>
                                        <th className="text-left py-3 px-6 font-medium">Hạng mục</th>
                                        <th className="text-center py-3 px-4 font-medium">Loại</th>
                                        <th className="text-center py-3 px-4 font-medium">SL</th>
                                        <th className="text-right py-3 px-6 font-medium">Đơn giá</th>
                                        <th className="text-right py-3 px-6 font-medium">Thành tiền</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {trackingResult.items.map((item: any) => (
                                        <tr key={item.id} className="border-b last:border-0 hover:bg-muted/20">
                                            <td className="py-3 px-6">
                                                <div className="font-medium">{item.service ? item.service.title : (item.part ? item.part.name : 'Unknown')}</div>
                                            </td>
                                            <td className="py-3 px-4 text-center">
                                                <span className={`px-2 py-0.5 rounded-full text-xs border ${item.service ? 'bg-blue-50 text-blue-700 border-blue-200' : 'bg-purple-50 text-purple-700 border-purple-200'}`}>
                                                    {item.service ? 'Dịch vụ' : 'Linh kiện'}
                                                </span>
                                            </td>
                                            <td className="py-3 px-4 text-center">{item.quantity}</td>
                                            <td className="py-3 px-6 text-right">{Number(item.price).toLocaleString()} đ</td>
                                            <td className="py-3 px-6 text-right font-medium">{(Number(item.price) * item.quantity).toLocaleString()} đ</td>
                                        </tr>
                                    ))}
                                </tbody>
                                <tfoot className="bg-muted/20 font-medium">
                                    <tr>
                                        <td colSpan={4} className="py-4 px-6 text-right text-base">Tổng cộng:</td>
                                        <td className="py-4 px-6 text-right text-base text-primary font-bold">
                                            {Number(trackingResult.total_amount).toLocaleString()} đ
                                        </td>
                                    </tr>
                                </tfoot>
                            </table>
                        </div>
                    </CardContent>
                </Card>
            )}
        </div>
    )
}

export default function TrackingPage() {
    return (
        <div className="min-h-screen bg-background flex flex-col">
            <Navbar />
            <main className="flex-grow py-12 px-4 sm:px-6 lg:px-8 bg-muted/20">
                <Suspense fallback={<div>Loading...</div>}>
                    <TrackingContent />
                </Suspense>
            </main>
            <Footer />
        </div>
    )
}

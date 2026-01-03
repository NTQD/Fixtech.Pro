import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { DollarSign, ShoppingCart, Activity, Users } from 'lucide-react'
import { Button } from '@/components/ui/button'

interface AdminOverviewProps {
    stats: any
    bookings: any[]
    setActiveTab: (tab: string) => void
}

export function AdminOverview({ stats, bookings, setActiveTab }: AdminOverviewProps) {
    return (
        <div className="space-y-6">
            <div className="grid md:grid-cols-4 gap-4">
                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Tổng doanh thu</CardTitle>
                        <DollarSign className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{stats.totalRevenue.toLocaleString()} đ</div>
                        <p className="text-xs text-muted-foreground">Tổng doanh thu hệ thống</p>
                    </CardContent>
                </Card>
                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Đơn hàng mới</CardTitle>
                        <ShoppingCart className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{stats.pendingOrders}</div>
                        <p className="text-xs text-muted-foreground">Đang chờ xử lý</p>
                    </CardContent>
                </Card>
                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Đã hoàn thành</CardTitle>
                        <Activity className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{stats.completedOrders}</div>
                        <p className="text-xs text-muted-foreground">Đơn hàng đã xong</p>
                    </CardContent>
                </Card>
                <Card>
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium">Khách hàng</CardTitle>
                        <Users className="h-4 w-4 text-muted-foreground" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold">{stats.totalCustomers}</div>
                        <p className="text-xs text-muted-foreground">Tổng số tài khoản</p>
                    </CardContent>
                </Card>
            </div>

            <div className="bg-card p-6 rounded-lg border border-border">
                <div className="flex items-center justify-between mb-4">
                    <h2 className="text-xl font-bold">Đặt lịch gần đây</h2>
                    <Button variant="outline" size="sm" onClick={() => setActiveTab('bookings')}>Xem tất cả</Button>
                </div>
                <div className="overflow-x-auto">
                    <table className="w-full text-sm">
                        <thead className="border-b border-border">
                            <tr>
                                <th className="text-left py-3 px-4">Khách hàng</th>
                                <th className="text-left py-3 px-4">Thiết bị</th>
                                <th className="text-left py-3 px-4">Ngày hẹn</th>
                                <th className="text-left py-3 px-4">Trạng thái</th>
                                <th className="text-left py-3 px-4">Tổng tiền</th>
                            </tr>
                        </thead>
                        <tbody>
                            {bookings.slice(0, 5).map((booking) => (
                                <tr key={booking.id} className="border-b border-border hover:bg-muted/50">
                                    <td className="py-3 px-4">
                                        <div>
                                            <div className="font-medium">{booking.customer_name}</div>
                                            <div className="text-xs text-muted-foreground">{booking.customer_phone}</div>
                                        </div>
                                    </td>
                                    <td className="py-3 px-4">{booking.device_info}</td>
                                    <td className="py-3 px-4">{booking.scheduled_date}</td>
                                    <td className="py-3 px-4">
                                        <span className={`px-3 py-1 rounded-full text-xs font-medium 
                                            ${booking.status === 'COMPLETED' ? 'bg-green-100 text-green-800' :
                                                booking.status === 'IN_PROGRESS' ? 'bg-blue-100 text-blue-800' :
                                                    'bg-yellow-100 text-yellow-800'}`}>
                                            {booking.status}
                                        </span>
                                    </td>
                                    <td className="py-3 px-4">{Number(booking.total_amount).toLocaleString()} đ</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    )
}

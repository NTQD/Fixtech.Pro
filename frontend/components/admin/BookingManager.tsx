import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Button } from '@/components/ui/button'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Input } from '@/components/ui/input'
import { Search, Eye } from 'lucide-react'
import { useState } from 'react'
import { BookingDetailModal } from './BookingDetailModal'

interface BookingManagerProps {
    bookings: any[]
    users: any[] // to list technicians
    updateStatus: (id: number, status: string) => void
    assignTechnician: (bookingId: number, techId: string) => void
}

export function BookingManager({ bookings, users, updateStatus, assignTechnician }: BookingManagerProps) {
    const [bookingSearch, setBookingSearch] = useState('')
    const [bookingSort, setBookingSort] = useState('newest')
    const [selectedBooking, setSelectedBooking] = useState<any>(null)

    const filteredBookings = bookings.filter(b => {
        const term = bookingSearch.toLowerCase()
        return (
            b.id.toString().includes(term) ||
            b.customer_name?.toLowerCase().includes(term) ||
            b.customer_phone?.includes(term)
        )
    }).sort((a, b) => {
        if (bookingSort === 'name-asc') return a.customer_name.localeCompare(b.customer_name)
        if (bookingSort === 'name-desc') return b.customer_name.localeCompare(a.customer_name)
        if (bookingSort === 'price-asc') return Number(a.total_amount) - Number(b.total_amount)
        if (bookingSort === 'price-desc') return Number(b.total_amount) - Number(a.total_amount)
        // newest default (booking IDs are usually chronological or created_at)
        return 0
    })

    return (
        <div className="bg-card p-6 rounded-lg border border-border">
            <div className="flex items-center justify-between mb-6">
                <div>
                    <h2 className="text-2xl font-bold mb-1">Quản lý đặt lịch</h2>
                    <p className="text-muted-foreground">Sử dụng menu trên để xem danh sách tất cả đặt lịch</p>
                </div>
                <div className="flex items-center gap-2">
                    <div className="relative">
                        <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
                        <Input
                            type="search"
                            placeholder="Tìm theo Tên, SĐT..."
                            className="w-[250px] pl-9"
                            value={bookingSearch}
                            onChange={(e) => setBookingSearch(e.target.value)}
                        />
                    </div>
                    <Select value={bookingSort} onValueChange={setBookingSort}>
                        <SelectTrigger className="w-[180px]">
                            <SelectValue placeholder="Sắp xếp" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="newest">Mới nhất</SelectItem>
                            <SelectItem value="name-asc">Tên (A-Z)</SelectItem>
                            <SelectItem value="name-desc">Tên (Z-A)</SelectItem>
                            <SelectItem value="price-asc">Giá (Thấp - Cao)</SelectItem>
                            <SelectItem value="price-desc">Giá (Cao - Thấp)</SelectItem>
                        </SelectContent>
                    </Select>
                </div>
            </div>

            <div className="rounded-md border bg-card">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>Mã đơn</TableHead>
                            <TableHead>Khách hàng</TableHead>
                            <TableHead>Thiết bị</TableHead>
                            <TableHead>Kỹ thuật viên</TableHead>
                            <TableHead>Trạng thái</TableHead>
                            <TableHead className="text-right">Tổng tiền</TableHead>
                            <TableHead className="text-right">Hành động</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {filteredBookings.map((booking: any) => (
                            <TableRow key={booking.id}>
                                <TableCell className="font-medium">#{booking.id}</TableCell>
                                <TableCell>
                                    <div>
                                        <div className="font-medium">{booking.customer_name}</div>
                                        <div className="text-xs text-muted-foreground">{booking.customer_phone}</div>
                                    </div>
                                </TableCell>
                                <TableCell>{booking.device_info}</TableCell>
                                <TableCell>
                                    <Select
                                        value={booking.technician?.id ? booking.technician.id.toString() : ''}
                                        onValueChange={(val) => assignTechnician(booking.id, val)}
                                    >
                                        <SelectTrigger className="w-[180px] h-8 text-xs">
                                            <SelectValue placeholder="Chưa phân công" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            {users.filter((u: any) => u.role === 'TECHNICIAN').map((tech: any) => (
                                                <SelectItem key={tech.id} value={tech.id.toString()}>
                                                    {tech.full_name} ({tech.email})
                                                </SelectItem>
                                            ))}
                                        </SelectContent>
                                    </Select>
                                </TableCell>
                                <TableCell>
                                    <div className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium 
                                        ${booking.status === 'COMPLETED' ? 'bg-green-100 text-green-800' :
                                            booking.status === 'IN_PROGRESS' ? 'bg-blue-100 text-blue-800' :
                                                'bg-yellow-100 text-yellow-800'}`}>
                                        {booking.status}
                                    </div>
                                </TableCell>
                                <TableCell className="text-right">{Number(booking.total_amount).toLocaleString()} đ</TableCell>
                                <TableCell className="text-right">
                                    <div className="flex justify-end gap-2">
                                        <Button variant="ghost" size="icon" onClick={() => setSelectedBooking(booking)}>
                                            <Eye className="w-4 h-4" />
                                        </Button>
                                        <Button variant="outline" size="sm" onClick={() => updateStatus(booking.id, 'IN_PROGRESS')}>Xử lý</Button>
                                        <Button variant="ghost" size="sm" onClick={() => updateStatus(booking.id, 'COMPLETED')}>Xong</Button>
                                    </div>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </div>

            <BookingDetailModal 
                booking={selectedBooking} 
                isOpen={!!selectedBooking} 
                onClose={() => setSelectedBooking(null)} 
            />
        </div>
    )
}

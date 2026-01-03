import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"

interface BookingDetailModalProps {
    booking: any
    isOpen: boolean
    onClose: () => void
}

export function BookingDetailModal({ booking, isOpen, onClose }: BookingDetailModalProps) {
    if (!booking) return null

    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle>Chi tiết đơn hàng: #{booking.id}</DialogTitle>
                    <DialogDescription>Thông tin chi tiết đơn hàng.</DialogDescription>
                </DialogHeader>

                <div className="space-y-6 py-4">
                    {/* Info Grid */}
                    <div className="grid grid-cols-2 gap-8">
                        <div>
                            <h4 className="font-semibold text-sm mb-1 text-muted-foreground">Khách hàng</h4>
                            <p className="text-sm font-medium">{booking.customer_name}</p>
                            <p className="text-sm text-muted-foreground">{booking.customer_phone}</p>
                            <p className="text-xs text-muted-foreground mt-1">Email: {booking.customer_email || 'N/A'}</p>
                        </div>
                        <div>
                            <h4 className="font-semibold text-sm mb-1 text-muted-foreground">Thiết bị</h4>
                            <p className="text-sm font-medium">{booking.device_info}</p>
                            <div className="mt-2">
                                <h4 className="font-semibold text-sm mb-1 text-muted-foreground">Lịch hẹn</h4>
                                <p className="text-sm">{booking.scheduled_date} {booking.scheduled_time ? `- ${booking.scheduled_time}` : ''}</p>
                            </div>
                        </div>
                    </div>

                    <div className="space-y-2">
                        <label className="text-sm font-medium">Mô tả lỗi</label>
                        <div className="bg-muted/50 p-3 rounded-md text-sm">{booking.issue_description}</div>
                        {booking.notes && (
                             <div className="mt-2 text-xs italic text-muted-foreground">Ghi chú: {booking.notes}</div>
                        )}
                    </div>

                    <div className="space-y-4">
                        <h3 className="font-semibold text-sm">Chi tiết dịch vụ & linh kiện</h3>
                        <div className="border rounded-lg overflow-hidden">
                            <Table>
                                <TableHeader>
                                    <TableRow className="bg-muted/50">
                                        <TableHead>Hạng mục</TableHead>
                                        <TableHead className="text-center">SL</TableHead>
                                        <TableHead className="text-right">Đơn giá</TableHead>
                                        <TableHead className="text-right">Thành tiền</TableHead>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {booking.items && booking.items.length > 0 ? (
                                        booking.items.map((item: any, idx: number) => (
                                            <TableRow key={idx}>
                                                <TableCell className="font-medium max-w-[250px] whitespace-normal break-words">
                                                    {item.service ? item.service.title : (item.part ? item.part.name : 'Unknown Item')}
                                                    <div className="text-xs text-muted-foreground">
                                                        {item.service ? 'Dịch vụ' : 'Linh kiện'}
                                                    </div>
                                                </TableCell>
                                                <TableCell className="text-center">{item.quantity}</TableCell>
                                                <TableCell className="text-right">{Number(item.price).toLocaleString()} đ</TableCell>
                                                <TableCell className="text-right">{(Number(item.price) * item.quantity).toLocaleString()} đ</TableCell>
                                            </TableRow>
                                        ))
                                    ) : (
                                        <TableRow>
                                            <TableCell colSpan={4} className="text-center text-muted-foreground h-24">Chưa có dịch vụ nào được thêm.</TableCell>
                                        </TableRow>
                                    )}
                                </TableBody>
                            </Table>
                        </div>
                    </div>

                    <div className="flex justify-between items-center p-4 bg-muted/30 rounded-lg">
                        <span className="font-semibold">Tổng cộng:</span>
                        <span className="text-xl font-bold text-primary">{Number(booking.total_amount).toLocaleString()} đ</span>
                    </div>
                </div>

                <DialogFooter>
                    <Button onClick={onClose}>Đóng</Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    )
}

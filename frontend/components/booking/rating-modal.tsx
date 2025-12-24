'use client'

import { useState } from 'react'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Textarea } from '@/components/ui/textarea'
import { Label } from '@/components/ui/label'
import { Star } from 'lucide-react'
import { toast } from 'sonner'

interface RatingModalProps {
    isOpen: boolean
    onClose: () => void
    onSubmit: (technicianRating: number, comment: string) => Promise<void>
    isSubmitting?: boolean
}

export function RatingModal({ isOpen, onClose, onSubmit, isSubmitting }: RatingModalProps) {
    const [orderRating, setOrderRating] = useState(0)
    const [techRating, setTechRating] = useState(0)
    const [comment, setComment] = useState('')

    const handleSubmit = async () => {
        if (orderRating === 0) {
            toast.error('Vui lòng đánh giá đơn hàng')
            return
        }
        if (techRating === 0) {
            toast.error('Vui lòng đánh giá kỹ thuật viên')
            return
        }
        await onSubmit(techRating, comment)
    }

    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent className="sm:max-w-[425px]">
                <DialogHeader>
                    <DialogTitle>Đánh giá đơn hàng</DialogTitle>
                    <DialogDescription>
                        Ý kiến của bạn giúp chúng tôi cải thiện chất lượng dịch vụ.
                    </DialogDescription>
                </DialogHeader>
                <div className="grid gap-6 py-4">
                    {/* Order Rating */}
                    <div className="flex flex-col items-center gap-2">
                        <Label className="text-base font-semibold">Đánh giá chung về đơn hàng</Label>
                        <div className="flex gap-1">
                            {[1, 2, 3, 4, 5].map((star) => (
                                <Star
                                    key={`order-${star}`}
                                    className={`w-8 h-8 cursor-pointer transition-colors ${orderRating >= star ? 'fill-yellow-400 text-yellow-400' : 'text-gray-300'}`}
                                    onClick={() => setOrderRating(star)}
                                />
                            ))}
                        </div>
                    </div>

                    {/* Technician Rating */}
                    <div className="flex flex-col items-center gap-2 border-t pt-4">
                        <Label className="text-base font-semibold">Đánh giá Kỹ thuật viên</Label>
                        <p className="text-xs text-muted-foreground">Điểm số này sẽ tính vào uy tín của KTV</p>
                        <div className="flex gap-1">
                            {[1, 2, 3, 4, 5].map((star) => (
                                <Star
                                    key={`tech-${star}`}
                                    className={`w-8 h-8 cursor-pointer transition-colors ${techRating >= star ? 'fill-yellow-400 text-yellow-400' : 'text-gray-300'}`}
                                    onClick={() => setTechRating(star)}
                                />
                            ))}
                        </div>
                    </div>

                    <div className="grid gap-2">
                        <Label htmlFor="comment">Nhận xét thêm (Tùy chọn)</Label>
                        <Textarea
                            id="comment"
                            placeholder="Hãy chia sẻ thêm về trải nghiệm của bạn..."
                            value={comment}
                            onChange={(e) => setComment(e.target.value)}
                        />
                    </div>
                </div>
                <DialogFooter>
                    <Button variant="outline" onClick={onClose} disabled={isSubmitting}>Để sau</Button>
                    <Button onClick={handleSubmit} disabled={isSubmitting}>
                        {isSubmitting ? 'Đang gửi...' : 'Gửi đánh giá'}
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    )
}

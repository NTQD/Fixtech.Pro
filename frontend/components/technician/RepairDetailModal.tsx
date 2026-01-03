import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogFooter } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Plus, Trash, Save, Search, Check } from 'lucide-react'
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem, CommandList } from "@/components/ui/command"
import { Card, CardContent } from "@/components/ui/card"
import { cn } from "@/lib/utils"

interface RepairDetailModalProps {
    isOpen: boolean
    onOpenChange: (open: boolean) => void
    job: any
    availableParts: any[]
    availableServices: any[]
    tempStatus: string
    setTempStatus: (s: string) => void
    techNotes: string
    setTechNotes: (n: string) => void
    partsToAdd: any[]
    setPartsToAdd: (parts: any[]) => void
    servicesToAdd: any[] // [NEW]
    setServicesToAdd: (svcs: any[]) => void // [NEW]
    selectedPartId: string
    setSelectedPartId: (id: string) => void
    selectedServiceId: string // [NEW]
    setSelectedServiceId: (id: string) => void // [NEW]
    partQuantity: number
    setPartQuantity: (q: number) => void
    handleAddPart: () => void
    handleAddService: () => void // [NEW]
    isSaving: boolean
    handleSaveUpdate: () => void
    calculateTotal: () => number
    partSearch: string
    setPartSearch: (s: string) => void
    commandListRef: any
    openPartCombobox: boolean
    setOpenPartCombobox: (v: boolean) => void
}

export function RepairDetailModal(props: RepairDetailModalProps) {
    const {
        isOpen, onOpenChange, job, availableParts, availableServices,
        tempStatus, setTempStatus, techNotes, setTechNotes,
        partsToAdd, setPartsToAdd, servicesToAdd, setServicesToAdd,
        selectedPartId, setSelectedPartId, selectedServiceId, setSelectedServiceId,
        partQuantity, setPartQuantity,
        handleAddPart, handleAddService,
        isSaving, handleSaveUpdate, calculateTotal,
        partSearch, setPartSearch, commandListRef,
        openPartCombobox, setOpenPartCombobox
    } = props

    if (!job) return null

    const handleRemovePart = (index: number) => {
        const list = [...partsToAdd]; list.splice(index, 1); setPartsToAdd(list)
    }
    const handleRemoveService = (index: number) => {
        const list = [...servicesToAdd]; list.splice(index, 1); setServicesToAdd(list)
    }

    return (
        <Dialog open={isOpen} onOpenChange={onOpenChange}>
            <DialogContent className="max-w-3xl max-h-[90vh] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle>Chi tiết sửa chữa: #{job.displayId || job.id}</DialogTitle>
                    <DialogDescription>Cập nhật thông tin sửa chữa, thêm linh kiện hoặc dịch vụ cho đơn hàng này.</DialogDescription>
                </DialogHeader>

                <div className="space-y-6 py-4">
                    {/* Info Grid */}
                    <div className="grid grid-cols-3 gap-6">
                        <div>
                            <h4 className="font-semibold text-sm mb-1 text-muted-foreground">Khách hàng</h4>
                            <p className="text-sm font-medium">{job.customer}</p>
                            <p className="text-sm text-muted-foreground">{job.phone}</p>
                        </div>
                        <div>
                            <h4 className="font-semibold text-sm mb-1 text-muted-foreground">Thiết bị</h4>
                            <p className="text-sm font-medium">{job.device}</p>
                            <p className="text-sm text-muted-foreground">S/N: {job.serial}</p>
                        </div>
                        <div>
                            <h4 className="font-semibold text-sm mb-1 text-muted-foreground">Lịch hẹn</h4>
                            <p className="text-sm font-medium">{job.scheduledDate ? new Date(job.scheduledDate).toLocaleDateString('vi-VN') : 'N/A'}</p>
                            <p className="text-sm text-muted-foreground font-semibold text-primary">{job.scheduledTime || 'N/A'}</p>
                        </div>
                    </div>

                    <div className="space-y-2">
                        <label className="text-sm font-medium">Mô tả lỗi</label>
                        <div className="bg-muted/50 p-3 rounded-md text-sm">{job.description}</div>
                    </div>

                    {/* PARTS & SERVICES TABLE */}
                    <div className="space-y-4">
                        <h3 className="font-semibold flex items-center gap-2"><Plus className="w-4 h-4" /> Linh kiện & Dịch vụ</h3>
                        
                        {/* Add Service Block */}
                        <div className="flex gap-2 items-end bg-card p-3 rounded-lg border border-border">
                            <div className="flex-1 space-y-1">
                                <label className="text-xs text-muted-foreground">Chọn dịch vụ</label>
                                <Select value={selectedServiceId} onValueChange={setSelectedServiceId}>
                                    <SelectTrigger><SelectValue placeholder="Chọn dịch vụ..." /></SelectTrigger>
                                    <SelectContent>
                                        {availableServices.map((s: any) => (
                                            <SelectItem key={s.id} value={s.id}>{s.title} - {Number(s.base_price).toLocaleString()}đ</SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>
                            <Button variant="secondary" onClick={handleAddService}>Thêm DV</Button>
                        </div>

                        {/* Add Part Block */}
                        <div className="flex gap-2 items-end bg-card p-3 rounded-lg border border-border">
                            <div className="flex-1 space-y-1">
                                <label className="text-xs text-muted-foreground">Chọn linh kiện</label>
                                <Popover open={openPartCombobox} onOpenChange={setOpenPartCombobox} modal={true}>
                                    <PopoverTrigger asChild>
                                        <Button variant="outline" role="combobox" aria-expanded={openPartCombobox} className="w-full justify-between font-normal">
                                            {selectedPartId ? availableParts.find((p) => p.id.toString() === selectedPartId)?.name : "Tìm linh kiện..."}
                                            <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                                        </Button>
                                    </PopoverTrigger>
                                    <PopoverContent className="w-[400px] p-0 z-[9999]" align="start" side="bottom">
                                        <Command shouldFilter={false}>
                                            <CommandInput placeholder="Nhập tên linh kiện..." value={partSearch} onValueChange={setPartSearch} />
                                            <CommandList ref={commandListRef} className="max-h-[300px] overflow-y-auto overflow-x-hidden p-1">
                                                <CommandEmpty>Không tìm thay.</CommandEmpty>
                                                <CommandGroup>
                                                    {availableParts.filter(p => !partSearch || p.name.toLowerCase().includes(partSearch.toLowerCase())).map((part) => (
                                                        <CommandItem
                                                            key={part.id}
                                                            value={part.name}
                                                            onSelect={() => {
                                                                setSelectedPartId(part.id.toString())
                                                                setOpenPartCombobox(false)
                                                            }}
                                                        >
                                                            <Check className={cn("mr-2 h-4 w-4", selectedPartId === part.id.toString() ? "opacity-100" : "opacity-0")} />
                                                            <div className="flex flex-col">
                                                                <span>{part.name}</span>
                                                                <span className="text-xs text-muted-foreground">Tồn: {part.stock} - {Number(part.price).toLocaleString()}đ</span>
                                                            </div>
                                                        </CommandItem>
                                                    ))}
                                                </CommandGroup>
                                            </CommandList>
                                        </Command>
                                    </PopoverContent>
                                </Popover>
                            </div>
                            <div className="w-20 space-y-1">
                                <label className="text-xs text-muted-foreground">SL</label>
                                <Input type="number" min={1} value={partQuantity} onChange={(e) => setPartQuantity(parseInt(e.target.value) || 1)} />
                            </div>
                            <Button variant="secondary" onClick={handleAddPart}>Thêm LK</Button>
                        </div>

                        {/* Items Table */}
                        <div className="border rounded-lg overflow-hidden">
                            <Table>
                                <TableHeader>
                                    <TableRow className="bg-muted/50">
                                        <TableHead>Hạng mục</TableHead>
                                        <TableHead>Loại</TableHead>
                                        <TableHead className="text-center">SL</TableHead>
                                        <TableHead className="text-right">Thành tiền</TableHead>
                                        <TableHead className="w-[50px]"></TableHead>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {job.items.map((item: any, idx: number) => (
                                        <TableRow key={'existing-' + idx} className="opacity-70 bg-muted/20">
                                            <TableCell className="font-medium max-w-[250px] whitespace-normal break-words">{item.name}</TableCell>
                                            <TableCell>{item.type}</TableCell>
                                            <TableCell className="text-center">{item.quantity}</TableCell>
                                            <TableCell className="text-right">{item.total.toLocaleString()} đ</TableCell>
                                            <TableCell><span className="text-xs text-muted-foreground">Đã lưu</span></TableCell>
                                        </TableRow>
                                    ))}
                                    {servicesToAdd.map((svc, idx) => (
                                        <TableRow key={'new-svc-' + idx} className="bg-blue-50/50">
                                            <TableCell className="font-medium max-w-[250px] whitespace-normal break-words">{svc.name}</TableCell>
                                            <TableCell>Dịch vụ</TableCell>
                                            <TableCell className="text-center">1</TableCell>
                                            <TableCell className="text-right">{svc.total.toLocaleString()} đ</TableCell>
                                            <TableCell>
                                                <Button variant="ghost" size="icon" onClick={() => handleRemoveService(idx)} className="h-6 w-6 text-red-500">
                                                    <Trash className="w-3 h-3" />
                                                </Button>
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                    {partsToAdd.map((part, idx) => (
                                        <TableRow key={'new-part-' + idx} className="bg-green-50/50">
                                            <TableCell className="font-medium max-w-[250px] whitespace-normal break-words">{part.name}</TableCell>
                                            <TableCell>Linh kiện</TableCell>
                                            <TableCell className="text-center">{part.quantity}</TableCell>
                                            <TableCell className="text-right">{part.total.toLocaleString()} đ</TableCell>
                                            <TableCell>
                                                <Button variant="ghost" size="icon" onClick={() => handleRemovePart(idx)} className="h-6 w-6 text-red-500">
                                                    <Trash className="w-3 h-3" />
                                                </Button>
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        </div>
                    </div>

                    <div className="flex justify-between items-center p-4 bg-muted/30 rounded-lg">
                        <span className="font-semibold">Tổng chi phí ước tính:</span>
                        <span className="text-xl font-bold text-primary">{calculateTotal().toLocaleString()} đ</span>
                    </div>

                    {/* Status and Tech Notes - Stacked vertically */}
                    <div className="space-y-4">
                        <div className="space-y-2">
                            <label className="text-sm font-medium">Cập nhật trạng thái đơn</label>
                            <Select value={tempStatus} onValueChange={setTempStatus}>
                                <SelectTrigger className={cn("w-fit border-2", {
                                    'bg-yellow-100 text-yellow-800 border-yellow-300': tempStatus === 'PENDING',
                                    'bg-orange-100 text-orange-800 border-orange-300': tempStatus === 'CONFIRMED',
                                    'bg-blue-100 text-blue-800 border-blue-300': tempStatus === 'IN_PROGRESS',
                                    'bg-green-100 text-green-800 border-green-300': tempStatus === 'COMPLETED',
                                    'bg-red-100 text-red-800 border-red-300': tempStatus === 'CANCELLED',
                                })}>
                                    <SelectValue placeholder="Chọn trạng thái" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="PENDING">
                                        <span className="inline-flex items-center gap-2">
                                            <span className="w-2 h-2 rounded-full bg-yellow-500"></span>
                                            Chờ tiếp nhận
                                        </span>
                                    </SelectItem>
                                    <SelectItem value="CONFIRMED">
                                        <span className="inline-flex items-center gap-2">
                                            <span className="w-2 h-2 rounded-full bg-orange-500"></span>
                                            Đã tiếp nhận (Confirmed)
                                        </span>
                                    </SelectItem>
                                    <SelectItem value="IN_PROGRESS">
                                        <span className="inline-flex items-center gap-2">
                                            <span className="w-2 h-2 rounded-full bg-blue-500"></span>
                                            Đang sửa chữa
                                        </span>
                                    </SelectItem>
                                    <SelectItem value="COMPLETED">
                                        <span className="inline-flex items-center gap-2">
                                            <span className="w-2 h-2 rounded-full bg-green-500"></span>
                                            Hoàn thành
                                        </span>
                                    </SelectItem>
                                    <SelectItem value="CANCELLED">
                                        <span className="inline-flex items-center gap-2">
                                            <span className="w-2 h-2 rounded-full bg-red-500"></span>
                                            Đã hủy
                                        </span>
                                    </SelectItem>
                                </SelectContent>
                            </Select>
                            <p className="text-xs text-muted-foreground italic">Thay đổi sẽ chỉ được lưu khi bạn nhấn nút "Lưu cập nhật".</p>
                        </div>
                        <div className="space-y-2">
                            <label className="text-sm font-medium">Ghi chú kỹ thuật</label>
                            <Textarea 
                                placeholder="Ghi chú công việc đã làm..."
                                value={techNotes}
                                onChange={(e) => setTechNotes(e.target.value)}
                                rows={6}
                                className="resize-none"
                            />
                        </div>
                    </div>
                </div>

                <DialogFooter className="gap-4">
                    <Button variant="outline" onClick={() => onOpenChange(false)}>Đóng</Button>
                    <Button onClick={handleSaveUpdate} disabled={isSaving}>
                        <Save className="w-4 h-4 mr-2" />
                        {isSaving ? 'Đang lưu...' : 'Lưu thay đổi'}
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    )
}

// Helper icon component
function ChevronsUpDown({ className }: { className?: string }) {
    return (
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}>
            <path d="m7 15 5 5 5-5" />
            <path d="m7 9 5-5 5 5" />
        </svg>
    )
}

import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Button } from '@/components/ui/button'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Input } from '@/components/ui/input'
import { Search, Package, Plus, Edit, Trash } from 'lucide-react'
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle } from "@/components/ui/alert-dialog"
import { useInventory } from '@/hooks/useInventory'

export function InventoryManager() {
    const {
        filteredParts, partSearch, setPartSearch, partSort, setPartSort,
        isModalOpen, setIsModalOpen, openModal,
        partForm, setPartForm, savePart,
        partToDelete, setPartToDelete, confirmDeletePart, editingPart
    } = useInventory()

    return (
        <div className="bg-card p-6 rounded-lg border border-border">
            <div className="flex items-center justify-between mb-6">
                <div>
                    <h2 className="text-2xl font-bold mb-1">Quản lý kho hàng</h2>
                    <p className="text-muted-foreground">Quản lý danh sách linh kiện và giá cả.</p>
                </div>
                <div className="flex items-center gap-2">
                    <div className="relative">
                        <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
                        <Input
                            placeholder="Tìm tên linh kiện..."
                            className="w-[250px] pl-9"
                            value={partSearch}
                            onChange={(e) => setPartSearch(e.target.value)}
                        />
                    </div>
                    <Select value={partSort} onValueChange={setPartSort}>
                        <SelectTrigger className="w-[180px]">
                            <SelectValue placeholder="Sắp xếp" />
                        </SelectTrigger>
                        <SelectContent>
                            <SelectItem value="id-asc">Mặc định (ID)</SelectItem>
                            <SelectItem value="price-asc">Giá tăng dần</SelectItem>
                            <SelectItem value="price-desc">Giá giảm dần</SelectItem>
                        </SelectContent>
                    </Select>
                    <Button onClick={() => openModal()}>
                        <Plus className="h-4 w-4 mr-2" /> Thêm linh kiện
                    </Button>
                </div>
            </div>

            <div className="rounded-md border">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead className="w-[80px]">ID</TableHead>
                            <TableHead>Tên linh kiện</TableHead>
                            <TableHead>Mô tả</TableHead>
                            <TableHead className="text-right">Giá bán</TableHead>
                            <TableHead className="text-center">Tồn kho</TableHead>
                            <TableHead className="text-right">Hành động</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {filteredParts.map((part) => (
                            <TableRow key={part.id}>
                                <TableCell className="font-medium">#{part.id}</TableCell>
                                <TableCell className="font-medium">{part.name}</TableCell>
                                <TableCell className="opacity-70 truncate max-w-[200px]">{part.description}</TableCell>
                                <TableCell className="text-right">{Number(part.price).toLocaleString()} đ</TableCell>
                                <TableCell className="text-center">
                                    <span className={`px-2 py-1 rounded text-xs font-bold ${part.stock < 10 ? 'bg-red-100 text-red-800' : 'bg-green-100 text-green-800'}`}>
                                        {part.stock}
                                    </span>
                                </TableCell>
                                <TableCell className="text-right">
                                    <div className="flex justify-end gap-2">
                                        <Button variant="ghost" size="icon" onClick={() => openModal(part)} className="text-blue-500 hover:bg-blue-50">
                                            <Edit className="h-4 w-4" />
                                        </Button>
                                        <Button variant="ghost" size="icon" onClick={() => setPartToDelete(part.id)} className="text-red-500 hover:bg-red-50">
                                            <Trash className="h-4 w-4" />
                                        </Button>
                                    </div>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </div>

            <Dialog open={isModalOpen} onOpenChange={setIsModalOpen}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>{editingPart ? 'Cập nhật linh kiện' : 'Thêm linh kiện mới'}</DialogTitle>
                        <DialogDescription>Nhập thông tin chi tiết cho linh kiện.</DialogDescription>
                    </DialogHeader>
                    <div className="space-y-4 py-4">
                        <div className="space-y-2">
                            <Label>Tên linh kiện</Label>
                            <Input value={partForm.name} onChange={(e) => setPartForm({ ...partForm, name: e.target.value })} placeholder="Ví dụ: RAM 8GB DDR4" />
                        </div>
                        <div className="grid grid-cols-2 gap-4">
                            <div className="space-y-2">
                                <Label>Giá bán (VNĐ)</Label>
                                <Input type="number" value={partForm.price} onChange={(e) => setPartForm({ ...partForm, price: Number(e.target.value) })} />
                            </div>
                            <div className="space-y-2">
                                <Label>Số lượng tồn</Label>
                                <Input type="number" value={partForm.stock} onChange={(e) => setPartForm({ ...partForm, stock: Number(e.target.value) })} />
                            </div>
                        </div>
                        <div className="space-y-2">
                            <Label>Mô tả</Label>
                            <Textarea value={partForm.description} onChange={(e) => setPartForm({ ...partForm, description: e.target.value })} placeholder="Thông tin kỹ thuật..." />
                        </div>
                    </div>
                    <DialogFooter>
                        <Button variant="outline" onClick={() => setIsModalOpen(false)}>Hủy</Button>
                        <Button onClick={savePart}>Lưu</Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>

            <AlertDialog open={!!partToDelete} onOpenChange={(open) => !open && setPartToDelete(null)}>
                <AlertDialogContent>
                    <AlertDialogHeader>
                        <AlertDialogTitle>Bạn có chắc chắn muốn xóa?</AlertDialogTitle>
                        <AlertDialogDescription>
                            Hành động này không thể hoàn tác. Linh kiện sẽ bị xóa vĩnh viễn khỏi hệ thống.
                        </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                        <AlertDialogCancel>Hủy bỏ</AlertDialogCancel>
                        <AlertDialogAction onClick={confirmDeletePart} className="bg-red-600 hover:bg-red-700">Xóa linh kiện</AlertDialogAction>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialog>
        </div>
    )
}

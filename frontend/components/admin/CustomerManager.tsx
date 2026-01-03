import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Button } from '@/components/ui/button'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Input } from '@/components/ui/input'
import { Search, Ban, LockOpen, Star } from 'lucide-react'
import { useState } from 'react'

interface CustomerManagerProps {
    users: any[]
    updateUserRole: (id: number, role: string) => void
    banUser: (id: number) => void
    unbanUser: (id: number) => void
}

export function CustomerManager({ users, updateUserRole, banUser, unbanUser }: CustomerManagerProps) {
    const [customerSearch, setCustomerSearch] = useState('')

    const filteredUsers = users.filter(u => {
        const term = customerSearch.toLowerCase()
        return (
            u.full_name?.toLowerCase().includes(term) ||
            u.email?.toLowerCase().includes(term)
        )
    })

    return (
        <div className="bg-card p-6 rounded-lg border border-border">
            <div className="flex items-center justify-between mb-4">
                <div>
                    <h2 className="text-2xl font-bold mb-1">Quản lý khách hàng</h2>
                    <p className="text-muted-foreground">Danh sách tài khoản đã đăng ký trên hệ thống.</p>
                </div>
                <div className="relative">
                    <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
                    <Input
                        type="search"
                        placeholder="Tìm Tên hoặc Email..."
                        className="w-[250px] pl-9"
                        value={customerSearch}
                        onChange={(e) => setCustomerSearch(e.target.value)}
                    />
                </div>
            </div>

            <div className="rounded-md border">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>ID</TableHead>
                            <TableHead>Tên đầy đủ</TableHead>
                            <TableHead>Email</TableHead>
                            <TableHead>Vai trò</TableHead>
                            <TableHead>Uy tín</TableHead>
                            <TableHead className="text-right">Hành động</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {filteredUsers.map((u: any) => (
                            <TableRow key={u.id} className={u.status === 0 ? 'opacity-50 bg-muted/50' : ''}>
                                <TableCell>{u.id}</TableCell>
                                <TableCell className="font-medium">{u.full_name}</TableCell>
                                <TableCell>
                                    <div>{u.email}</div>
                                    {u.status === 0 && <span className="text-xs text-red-500 font-bold">Bị cấm</span>}
                                </TableCell>
                                <TableCell>
                                    <Select
                                        disabled={u.status === 0 || u.role === 'ADMIN'}
                                        value={u.role}
                                        onValueChange={(val) => updateUserRole(u.id, val)}
                                    >
                                        <SelectTrigger className="w-[130px] h-8 text-xs">
                                            <SelectValue />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectItem value="CUSTOMER">Khách hàng</SelectItem>
                                            <SelectItem value="TECHNICIAN">Kỹ thuật viên</SelectItem>
                                            <SelectItem value="ADMIN">Quản trị viên</SelectItem>
                                        </SelectContent>
                                    </Select>
                                </TableCell>
                                <TableCell>
                                    {u.role === 'TECHNICIAN' && u.reputation_score != null ? (
                                        <div className="flex items-center gap-1">
                                            <Star className="w-4 h-4 text-yellow-500 fill-yellow-500" />
                                            <span className="font-bold text-yellow-600 dark:text-yellow-400">
                                                {Number(u.reputation_score).toFixed(1)}/5.0
                                            </span>
                                        </div>
                                    ) : (
                                        <span className="text-muted-foreground">-</span>
                                    )}
                                </TableCell>
                                <TableCell className="text-right">
                                    {u.role !== 'ADMIN' && (
                                        u.status !== 0 ? (
                                            <Button
                                                variant="ghost"
                                                size="icon"
                                                className="text-red-500 hover:text-red-700 hover:bg-red-100"
                                                onClick={() => {
                                                    if (confirm('Bạn có chắc chắn muốn cấm người dùng này?')) {
                                                        banUser(u.id)
                                                    }
                                                }}
                                            >
                                                <Ban className="h-4 w-4" />
                                            </Button>
                                        ) : (
                                            <Button
                                                variant="ghost"
                                                size="icon"
                                                className="text-green-500 hover:text-green-700 hover:bg-green-100"
                                                onClick={() => unbanUser(u.id)}
                                            >
                                                <LockOpen className="h-4 w-4" />
                                            </Button>
                                        )
                                    )}
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </div>
        </div>
    )
}

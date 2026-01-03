import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Label } from "@/components/ui/label"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Switch } from "@/components/ui/switch"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Button } from '@/components/ui/button'
import { toast } from 'sonner'
import { useState } from 'react'

interface SystemSettingsProps {
    config: any
    setConfig: (cfg: any) => void
    saveSettings: (cfg: any) => void
}

export function SystemSettings({ config, setConfig, saveSettings }: SystemSettingsProps) {
    const [activeSettingsTab, setActiveSettingsTab] = useState('brand')

    return (
        <div className="space-y-6">
            <h2 className="text-2xl font-bold">Cài đặt hệ thống</h2>
            <Tabs value={activeSettingsTab} onValueChange={setActiveSettingsTab} className="w-full">
                <TabsList className="grid w-full grid-cols-3">
                    <TabsTrigger value="brand">Thương hiệu</TabsTrigger>
                    <TabsTrigger value="interface">Giao diện</TabsTrigger>
                    <TabsTrigger value="interaction">Tương tác</TabsTrigger>
                </TabsList>

                {/* BRAND CONFIG */}
                <TabsContent value="brand" className="space-y-4 py-4">
                    <Card>
                        <CardHeader>
                            <CardTitle>Thông tin cửa hàng</CardTitle>
                            <CardDescription>Các thông tin hiển thị trên website và hóa đơn.</CardDescription>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            <div className="grid grid-cols-2 gap-4">
                                <div className="space-y-2">
                                    <Label>Tên cửa hàng</Label>
                                    <Input value={config.storeName} onChange={(e) => setConfig({ ...config, storeName: e.target.value })} />
                                </div>
                                <div className="space-y-2">
                                    <Label>Số điện thoại</Label>
                                    <Input value={config.phone} onChange={(e) => setConfig({ ...config, phone: e.target.value })} />
                                </div>
                            </div>
                            <div className="space-y-2">
                                <Label>Email hỗ trợ</Label>
                                <Input value={config.email} onChange={(e) => setConfig({ ...config, email: e.target.value })} />
                            </div>
                            <div className="space-y-2">
                                <Label>Địa chỉ</Label>
                                <Input value={config.address} onChange={(e) => setConfig({ ...config, address: e.target.value })} />
                            </div>
                        </CardContent>
                    </Card>

                    <Card>
                        <CardHeader>
                            <CardTitle>Hình ảnh thương hiệu</CardTitle>
                            <CardDescription>Logo và Favicon.</CardDescription>
                        </CardHeader>
                        <CardContent className="space-y-4">
                            <div className="space-y-2">
                                <Label>Logo Website</Label>
                                <div className="flex items-center gap-4">
                                    <div className="w-16 h-16 bg-muted rounded-lg flex items-center justify-center border">
                                        <span className="text-xs text-muted-foreground">Logo</span>
                                    </div>
                                    <Input type="file" className="cursor-pointer" />
                                </div>
                            </div>
                            <div className="space-y-2">
                                <Label>Favicon</Label>
                                <div className="flex items-center gap-4">
                                    <div className="w-10 h-10 bg-muted rounded flex items-center justify-center border">
                                        <span className="text-[10px] text-muted-foreground">Icon</span>
                                    </div>
                                    <Input type="file" className="cursor-pointer" />
                                </div>
                            </div>
                        </CardContent>
                    </Card>
                </TabsContent>

                {/* INTERFACE CONFIG */}
                <TabsContent value="interface" className="space-y-4 py-4">
                    <Card>
                        <CardHeader>
                            <CardTitle>Giao diện & Hiển thị</CardTitle>
                            <CardDescription>Tùy chỉnh theme và bố cục.</CardDescription>
                        </CardHeader>
                        <CardContent className="space-y-6">
                            <div className="flex items-center justify-between">
                                <div className="space-y-0.5">
                                    <Label className="text-base">Chế độ tối (Dark Mode)</Label>
                                    <p className="text-sm text-muted-foreground">Mặc định sử dụng giao diện hệ thống.</p>
                                </div>
                                <Select value={config.theme} onValueChange={(val) => setConfig({ ...config, theme: val })}>
                                    <SelectTrigger className="w-[180px]">
                                        <SelectValue placeholder="Theme" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="light">Sáng (Light)</SelectItem>
                                        <SelectItem value="dark">Tối (Dark)</SelectItem>
                                        <SelectItem value="system">Hệ thống</SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>

                            <div className="flex items-center justify-between">
                                <div className="space-y-0.5">
                                    <Label className="text-base">Mật độ hiển thị</Label>
                                    <p className="text-sm text-muted-foreground">Điều chỉnh khoảng cách giữa các phần tử.</p>
                                </div>
                                <Select value={config.density} onValueChange={(val) => setConfig({ ...config, density: val })}>
                                    <SelectTrigger className="w-[180px]">
                                        <SelectValue placeholder="Density" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="compact">Nhỏ gọn (Compact)</SelectItem>
                                        <SelectItem value="comfortable">Thoải mái (Default)</SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>

                            <div className="flex items-center justify-between">
                                <div className="space-y-0.5">
                                    <Label className="text-base">Âm thanh thông báo</Label>
                                    <p className="text-sm text-muted-foreground">Phát âm thanh khi có đơn hàng mới.</p>
                                </div>
                                <Switch checked={config.notifications} onCheckedChange={(checked) => setConfig({ ...config, notifications: checked })} />
                            </div>
                        </CardContent>
                    </Card>
                </TabsContent>

                {/* INTERACTION CONFIG */}
                <TabsContent value="interaction" className="space-y-4 py-4">
                    <Card>
                        <CardHeader>
                            <CardTitle>Nội dung động</CardTitle>
                            <CardDescription>Quản lý các thông điệp hiển thị trên website.</CardDescription>
                        </CardHeader>
                        <CardContent className="space-y-6">
                            <div className="flex flex-col space-y-4">
                                <div className="flex items-center justify-between">
                                    <div className="space-y-0.5">
                                        <Label className="text-base">Thanh thông báo (Announcement Bar)</Label>
                                        <p className="text-sm text-muted-foreground">Hiển thị dòng thông báo trên cùng của trang web.</p>
                                    </div>
                                    <Switch checked={config.announcementBar} onCheckedChange={(checked) => setConfig({ ...config, announcementBar: checked })} />
                                </div>
                                {config.announcementBar && (
                                    <Input
                                        value={config.announcementText}
                                        onChange={(e) => setConfig({ ...config, announcementText: e.target.value })}
                                        placeholder="Nhập nội dung thông báo..."
                                    />
                                )}
                            </div>

                            <div className="space-y-2 pt-4 border-t">
                                <Label>Tiêu đề chào mừng (Trang chủ)</Label>
                                <Textarea
                                    value={config.welcomeText}
                                    onChange={(e) => setConfig({ ...config, welcomeText: e.target.value })}
                                    rows={3}
                                />
                            </div>
                        </CardContent>
                    </Card>
                </TabsContent>
            </Tabs>

            <div className="flex justify-end gap-4">
                <Button variant="outline" onClick={() => toast.info('Đã hủy thay đổi.')}>Hủy bỏ</Button>
                <Button onClick={() => saveSettings(config)}>Lưu cấu hình</Button>
            </div>
        </div>
    )
}

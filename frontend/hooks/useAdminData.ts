import { useState, useEffect, useCallback } from 'react'
import { API_ENDPOINTS } from '@/lib/api-config'
import { toast } from 'sonner'
import { useConfig } from '@/contexts/ConfigContext'
import { useTheme } from "next-themes"

export const useAdminData = () => {
    const { refreshConfig } = useConfig()
    const { setTheme } = useTheme()
    const [bookings, setBookings] = useState<any[]>([])
    const [users, setUsers] = useState<any[]>([])
    const [stats, setStats] = useState({
        totalRevenue: 0,
        pendingOrders: 0,
        completedOrders: 0,
        totalCustomers: 0
    })
    const [config, setConfig] = useState({
        storeName: 'TechFix Pro',
        phone: '1900 1234',
        email: 'support@techfix.pro',
        address: '123 Đường Công Nghệ, Hà Nội',
        theme: 'system',
        density: 'comfortable',
        notifications: true,
        announcementBar: true,
        announcementText: '',
        welcomeText: ''
    })

    const fetchData = useCallback(async () => {
        const token = localStorage.getItem('access_token')
        if (!token) return
        const headers = { 'Authorization': `Bearer ${token}` }

        try {
            // Fetch Users
            const usersRes = await fetch(API_ENDPOINTS.USERS, { headers })
            let fetchedUsers: any[] = []
            if (usersRes.ok) {
                fetchedUsers = await usersRes.json()
                setUsers(fetchedUsers)
            }

            // Fetch Bookings
            const bookingsRes = await fetch(API_ENDPOINTS.BOOKINGS, { headers })
            if (bookingsRes.ok) {
                const bookingsData = await bookingsRes.json()
                setBookings(bookingsData)

                const totalRev = bookingsData.reduce((acc: number, curr: any) => {
                    if (curr.status === 'CANCELLED') return acc;
                    return acc + Number(curr.total_amount);
                }, 0)
                const pending = bookingsData.filter((b: any) => b.status === 'PENDING').length
                const completed = bookingsData.filter((b: any) => b.status === 'COMPLETED').length

                setStats({
                    totalRevenue: totalRev,
                    pendingOrders: pending,
                    completedOrders: completed,
                    totalCustomers: fetchedUsers.length
                })
            }

            // Fetch Config
            const configRes = await fetch(API_ENDPOINTS.CONFIG)
            if (configRes.ok) {
                const fetchedConfig = await configRes.json()
                if (Object.keys(fetchedConfig).length > 0) {
                    setConfig(prev => ({
                        ...prev, ...fetchedConfig,
                        notifications: String(fetchedConfig.notifications) === 'true',
                        announcementBar: String(fetchedConfig.announcementBar) === 'true',
                    }))
                    // if (fetchedConfig.theme) setTheme(fetchedConfig.theme)
                }
            }
        } catch (error) {
            console.error("Failed to fetch data", error)
        }
    }, [setTheme])

    const updateStatus = async (bookingId: number, newStatus: string) => {
        const token = localStorage.getItem('access_token')
        try {
            const res = await fetch(`${API_ENDPOINTS.BOOKINGS}/${bookingId}/status`, {
                method: 'PATCH',
                headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
                body: JSON.stringify({ status: newStatus })
            })
            if (res.ok) {
                fetchData()
                toast.success('Cập nhật trạng thái thành công')
            } else {
                toast.error('Cập nhật thất bại!')
            }
        } catch (e) {
            console.error("Error updating status", e)
        }
    }

    const assignTechnician = async (bookingId: number, technicianId: string) => {
        const token = localStorage.getItem('access_token')
        try {
            const res = await fetch(`${API_ENDPOINTS.BOOKINGS}/${bookingId}/assign`, {
                method: 'PATCH',
                headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
                body: JSON.stringify({ technicianId: Number(technicianId) })
            })
            if (res.ok) {
                fetchData()
                toast.success('Đã phân công kỹ thuật viên!')
            } else {
                toast.error('Lỗi phân công!')
            }
        } catch (e) {
            console.error("Assign error", e)
        }
    }

    const saveSettings = async (newConfig: any) => {
        const token = localStorage.getItem('access_token')
        try {
            const res = await fetch(API_ENDPOINTS.CONFIG, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                body: JSON.stringify(newConfig)
            })
            if (res.ok) {
                toast.success('Đã lưu cấu hình hệ thống!')
                await refreshConfig()
                fetchData() // Refresh local config state
            } else {
                toast.error('Lỗi khi lưu cấu hình')
            }
        } catch (e) {
            toast.error('Lỗi kết nối')
        }
    }

    const updateUserRole = async (userId: number, newRole: string) => {
        try {
            const token = localStorage.getItem('access_token');
            const res = await fetch(`${API_ENDPOINTS.USERS}/${userId}/role`, {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                body: JSON.stringify({ role: newRole })
            });
            if (res.ok) {
                toast.success('Cập nhật vai trò thành công!');
                fetchData();
            } else {
                toast.error('Cập nhật thất bại');
            }
        } catch (e) { console.error(e) }
    }

    const banUser = async (userId: number) => {
        try {
            const token = localStorage.getItem('access_token');
            const res = await fetch(`${API_ENDPOINTS.USERS}/${userId}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (res.ok) {
                toast.success('Đã cấm tài khoản thành công!');
                fetchData();
            } else {
                toast.error('Thao tác thất bại');
            }
        } catch (e) { console.error(e) }
    }

    const unbanUser = async (userId: number) => {
        try {
            const token = localStorage.getItem('access_token');
            const res = await fetch(`${API_ENDPOINTS.USERS}/${userId}/unban`, {
                method: 'PATCH',
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (res.ok) {
                toast.success('Đã mở khóa tài khoản thành công!');
                fetchData();
            } else {
                toast.error('Thao tác thất bại');
            }
        } catch (e) { console.error(e) }
    }

    useEffect(() => {
        fetchData()
    }, [fetchData])

    return {
        bookings,
        users,
        stats,
        config,
        setConfig, // For local updates before save
        updateStatus,
        assignTechnician,
        saveSettings,
        updateUserRole,
        banUser,
        unbanUser,
        fetchData
    }
}

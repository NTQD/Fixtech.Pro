'use client'

import React, { createContext, useContext, useState, useEffect } from 'react'

interface SystemConfig {
    storeName: string
    phone: string
    email: string
    address: string
    theme: string
    density: string
    notifications: boolean
    announcementBar: boolean
    announcementText: string
    welcomeText: string
}

const defaultConfig: SystemConfig = {
    storeName: 'TechFix Pro',
    phone: '1900 1234',
    email: 'support@techfix.pro',
    address: '123 Đường Công Nghệ, Hà Nội',
    theme: 'system',
    density: 'comfortable',
    notifications: true,
    announcementBar: true,
    announcementText: '🔥 Giảm giá 50% phí dịch vụ vệ sinh Laptop trong tháng này!',
    welcomeText: 'Chào mừng đến với hệ thống quản lý sửa chữa chuyên nghiệp.'
}

interface ConfigContextType {
    config: SystemConfig
    unratedCount: number
    refreshConfig: () => Promise<void>
    refreshNotifications: () => Promise<void>
}

const ConfigContext = createContext<ConfigContextType>({
    config: defaultConfig,
    unratedCount: 0,
    refreshConfig: async () => { },
    refreshNotifications: async () => { }
})

export const useConfig = () => useContext(ConfigContext)

export const ConfigProvider = ({ children }: { children: React.ReactNode }) => {
    const [config, setConfig] = useState<SystemConfig>(defaultConfig)
    const [unratedCount, setUnratedCount] = useState(0)

    const fetchConfig = async () => {
        try {
            const res = await fetch('http://localhost:3000/catalog/config')
            if (res.ok) {
                const data = await res.json()
                if (Object.keys(data).length > 0) {
                    setConfig(prev => ({
                        ...prev,
                        ...data,
                        notifications: String(data.notifications) === 'true',
                        announcementBar: String(data.announcementBar) === 'true',
                    }))
                }
            }
        } catch (error) {
            console.error("Failed to load system config", error)
        }
    }

    const fetchNotifications = async () => {
        const token = localStorage.getItem('access_token')
        if (!token) return

        try {
            const res = await fetch('http://localhost:3000/bookings/notifications/count', {
                headers: { 'Authorization': `Bearer ${token}` }
            })
            if (res.ok) {
                const data = await res.json() // returns number directly from service return? Controller returns number.
                // Wait, NestJS controller default returns just the value if it's a primitive? Or object?
                // service returns number. Controller returns number.
                setUnratedCount(Number(data) || 0)
            }
        } catch (error) {
            console.error("Failed to fetch notifications", error)
        }
    }

    useEffect(() => {
        fetchConfig()
        fetchNotifications()
    }, [])

    return (
        <ConfigContext.Provider value={{ config, unratedCount, refreshConfig: fetchConfig, refreshNotifications: fetchNotifications }}>
            {children}
        </ConfigContext.Provider>
    )
}

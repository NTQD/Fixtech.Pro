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
    refreshConfig: () => Promise<void>
}

const ConfigContext = createContext<ConfigContextType>({
    config: defaultConfig,
    refreshConfig: async () => { }
})

export const useConfig = () => useContext(ConfigContext)

export const ConfigProvider = ({ children }: { children: React.ReactNode }) => {
    const [config, setConfig] = useState<SystemConfig>(defaultConfig)

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

    useEffect(() => {
        fetchConfig()
    }, [])

    return (
        <ConfigContext.Provider value={{ config, refreshConfig: fetchConfig }}>
            {children}
        </ConfigContext.Provider>
    )
}

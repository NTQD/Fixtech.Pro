import { Button } from '@/components/ui/button'
import { LayoutDashboard, Calendar as CalendarIcon, History, Kanban as KanbanIcon, Wrench, ChevronLeft, ChevronRight, LogOut } from 'lucide-react'

interface TechnicianSidebarProps {
    isSidebarOpen: boolean
    setIsSidebarOpen: (v: boolean) => void
    activeTab: string
    setActiveTab: (v: string) => void
}

export function TechnicianSidebar({ isSidebarOpen, setIsSidebarOpen, activeTab, setActiveTab }: TechnicianSidebarProps) {
    const navItems = [
        { id: 'kanban', icon: KanbanIcon, label: 'Kanban Board' },
        { id: 'calendar', icon: CalendarIcon, label: 'Calendar' },
        { id: 'schedule', icon: LayoutDashboard, label: 'My Schedule' },
        { id: 'history', icon: History, label: 'History' },
    ]

    return (
        <aside className={`border-r border-border bg-card fixed h-full hidden md:block transition-all duration-300 z-50 ${isSidebarOpen ? 'w-64' : 'w-20'}`}>
            <div className="p-6 border-b border-border flex items-center justify-between">
                <div className={`flex items-center gap-3 ${!isSidebarOpen && 'justify-center w-full'}`}>
                    <div className="w-10 h-10 bg-primary rounded-lg flex items-center justify-center flex-shrink-0">
                        <Wrench className="w-6 h-6 text-primary-foreground" />
                    </div>
                    {isSidebarOpen && <span className="font-bold text-xl whitespace-nowrap">TechFix Pro</span>}
                </div>
            </div>
            
            <nav className="p-4 space-y-2">
                {navItems.map(item => (
                    <Button
                        key={item.id}
                        variant={activeTab === item.id ? 'secondary' : 'ghost'}
                        className={`w-full flex items-center gap-3 justify-start ${!isSidebarOpen && 'justify-center px-2'}`}
                        onClick={() => setActiveTab(item.id)}
                    >
                        <item.icon className="w-4 h-4 flex-shrink-0" />
                        {isSidebarOpen && <span>{item.label}</span>}
                    </Button>
                ))}
            </nav>

            <div className="absolute bottom-4 left-0 w-full flex justify-center px-4">
                <Button variant="ghost" size="icon" onClick={() => setIsSidebarOpen(!isSidebarOpen)} className="w-full">
                    {isSidebarOpen ? (
                        <div className="flex items-center gap-2"><ChevronLeft className="w-4 h-4" /><span className="text-sm">Collapse</span></div>
                    ) : <ChevronRight className="w-4 h-4" />}
                </Button>
            </div>
        </aside>
    )
}

import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { AlertCircle, CheckCircle2, RotateCw, CheckCircle, MoreHorizontal, User } from 'lucide-react'
import { format } from 'date-fns'

interface KanbanBoardProps {
    jobs: any[]
    onJobClick: (job: any) => void
}

export function KanbanBoard({ jobs, onJobClick }: KanbanBoardProps) {
    const kanbanColumns = [
        { id: 'PENDING', title: 'Chờ tiếp nhận', icon: AlertCircle, color: 'text-yellow-600 bg-yellow-50 dark:bg-yellow-900/20' },
        { id: 'CONFIRMED', title: 'Đã tiếp nhận', icon: CheckCircle2, color: 'text-orange-600 bg-orange-50 dark:bg-orange-900/20' },
        { id: 'IN_PROGRESS', title: 'Đang sửa chữa', icon: RotateCw, color: 'text-blue-600 bg-blue-50 dark:bg-blue-900/20' },
        { id: 'COMPLETED', title: 'Hoàn thành', icon: CheckCircle, color: 'text-green-600 bg-green-50 dark:bg-green-900/20' },
    ]

    const getTasksByStatus = (status: string) => jobs.filter(t => t.status === status)

    return (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 pb-4">
            {kanbanColumns.map((col) => (
                <div key={col.id} className="flex flex-col h-full">
                    <div className={`flex items-center justify-between p-3 rounded-t-lg border-b-2 ${col.color} border-${col.color.split(' ')[0].replace('text-', '')}`}>
                        <div className="flex items-center gap-2 font-semibold">
                            <col.icon className="w-4 h-4" />
                            {col.title}
                            <Badge variant="secondary" className="ml-1">{getTasksByStatus(col.id).length}</Badge>
                        </div>
                        <MoreHorizontal className="w-4 h-4 text-muted-foreground cursor-pointer" />
                    </div>
                    <div className="bg-muted/30 p-3 rounded-b-lg flex-grow space-y-3 min-h-[500px]">
                        {getTasksByStatus(col.id).map((task) => (
                            <Card key={task.id} className="cursor-pointer hover:shadow-md transition-shadow" onClick={() => onJobClick(task)}>
                                <CardContent className="p-4 space-y-3">
                                    <div className="flex justify-between items-start">
                                        <Badge variant="outline" className="font-mono text-xs">{task.displayId || task.id}</Badge>
                                        {task.priority === 'high' && <Badge className="bg-red-100 text-red-700 hover:bg-red-100 dark:bg-red-900/30 dark:text-red-300 border-none">High</Badge>}
                                    </div>
                                    <div>
                                        <h4 className="font-semibold text-sm">{task.device}</h4>
                                        <p className="text-xs text-muted-foreground line-clamp-2">{task.description}</p>
                                    </div>
                                    <div className="flex items-center justify-between text-xs text-muted-foreground pt-2 border-t">
                                        <div className="flex items-center gap-1">
                                            <User className="w-3 h-3" /> {task.customer}
                                        </div>
                                        <span>{format(task.date, 'dd/MM')}</span>
                                    </div>
                                </CardContent>
                            </Card>
                        ))}
                    </div>
                </div>
            ))}
        </div>
    )
}

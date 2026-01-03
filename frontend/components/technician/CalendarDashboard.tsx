import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { ChevronLeft, ChevronRight } from 'lucide-react'
import {
    format, addMonths, subMonths, startOfMonth, endOfMonth,
    eachDayOfInterval, startOfWeek, endOfWeek, isSameMonth,
    isSameDay, addWeeks, subWeeks, addDays, subDays, isToday
} from 'date-fns'
import { enUS } from 'date-fns/locale'

type CalendarView = 'month' | 'week' | 'day'

interface CalendarDashboardProps {
    jobs: any[]
    onJobClick: (job: any) => void
}

export function CalendarDashboard({ jobs, onJobClick }: CalendarDashboardProps) {
    const [currentDate, setCurrentDate] = useState(new Date())
    const [calendarView, setCalendarView] = useState<CalendarView>('month')

    const handlePrev = () => {
        if (calendarView === 'month') setCurrentDate(subMonths(currentDate, 1))
        if (calendarView === 'week') setCurrentDate(subWeeks(currentDate, 1))
        if (calendarView === 'day') setCurrentDate(subDays(currentDate, 1))
    }
    const handleNext = () => {
        if (calendarView === 'month') setCurrentDate(addMonths(currentDate, 1))
        if (calendarView === 'week') setCurrentDate(addWeeks(currentDate, 1))
        if (calendarView === 'day') setCurrentDate(addDays(currentDate, 1))
    }
    const handleToday = () => { setCurrentDate(new Date()) }

    const generateCalendarDays = () => {
        if (calendarView === 'month') {
            const start = startOfWeek(startOfMonth(currentDate), { weekStartsOn: 1 })
            const end = endOfWeek(endOfMonth(currentDate), { weekStartsOn: 1 })
            return eachDayOfInterval({ start, end })
        }
        if (calendarView === 'week') {
            const start = startOfWeek(currentDate, { weekStartsOn: 1 })
            const end = endOfWeek(currentDate, { weekStartsOn: 1 })
            return eachDayOfInterval({ start, end })
        }
        return [currentDate]
    }
    const calendarDays = generateCalendarDays()

    return (
        <div className="bg-card rounded-xl border border-border shadow-sm flex flex-col min-h-[600px]">
            <div className="p-4 border-b border-border flex flex-col sm:flex-row items-center justify-between gap-4">
                <div className="flex items-center gap-4">
                    <h2 className="font-bold text-2xl capitalize min-w-[200px]">
                        {format(currentDate, 'MMMM yyyy', { locale: enUS })}
                    </h2>
                    <div className="flex items-center border border-border rounded-md bg-background">
                        <Button variant="ghost" size="icon" onClick={handlePrev}><ChevronLeft className="w-4 h-4" /></Button>
                        <Button variant="ghost" size="icon" onClick={handleNext}><ChevronRight className="w-4 h-4" /></Button>
                        <div className="w-px h-6 bg-border mx-1"></div>
                        <Button variant="ghost" size="sm" onClick={handleToday}>Today</Button>
                    </div>
                </div>
                <div className="flex bg-muted p-1 rounded-md">
                    <Button variant={calendarView === 'month' ? 'default' : 'ghost'} size="sm" className="h-8 text-xs" onClick={() => setCalendarView('month')}>Month</Button>
                    <Button variant={calendarView === 'week' ? 'default' : 'ghost'} size="sm" className="h-8 text-xs" onClick={() => setCalendarView('week')}>Week</Button>
                    <Button variant={calendarView === 'day' ? 'default' : 'ghost'} size="sm" className="h-8 text-xs" onClick={() => setCalendarView('day')}>Day</Button>
                </div>
            </div>

            <div className="grid grid-cols-7 border-b border-border bg-muted/30 flex-shrink-0">
                {['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'].map(day => (
                    <div key={day} className="py-3 text-center text-xs font-bold text-muted-foreground border-r border-border last:border-r-0">
                        {day}
                    </div>
                ))}
            </div>

            <div className={`grid ${calendarView === 'day' ? 'grid-cols-1' : 'grid-cols-7'} auto-rows-[minmax(120px,auto)] flex-1`}>
                {calendarDays.map((date, idx) => (
                    <div key={idx} className={`min-h-[120px] border-b border-r border-border p-2 relative hover:bg-muted/10 transition-colors ${!isSameMonth(date, currentDate) && calendarView === 'month' ? 'bg-muted/20 text-muted-foreground' : ''}`}>
                        <div className="flex justify-between items-start mb-1">
                            <span className={`text-sm font-medium w-6 h-6 flex items-center justify-center rounded-full ${isToday(date) ? 'bg-primary text-primary-foreground' : ''}`}>{format(date, 'd')}</span>
                        </div>
                        <div className="space-y-1">
                            {jobs.filter(job => isSameDay(job.date, date)).map(job => (
                                <div key={job.id} onClick={() => onJobClick(job)} className={`px-2 py-1.5 rounded cursor-pointer border text-[10px] sm:text-xs shadow-sm ${job.color} mb-1`}>
                                    <div className="font-medium truncate">{job.customer}</div>
                                    <div className="text-[9px] sm:text-[10px] opacity-80">{job.scheduledTime?.slice(0, 5) || ''}</div>
                                </div>
                            ))}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}

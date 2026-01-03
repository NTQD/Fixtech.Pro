import { format } from 'date-fns'

interface JobHistoryTableProps {
    jobs: any[]
}

export function JobHistoryTable({ jobs }: JobHistoryTableProps) {
    const historyJobs = jobs.filter(job => job.status === 'COMPLETED')

    return (
        <div className="bg-card rounded-xl border border-border shadow-sm overflow-hidden">
            <table className="w-full text-sm">
                <thead className="border-b border-border bg-muted/50">
                    <tr>
                        <th className="text-left py-3 px-4">Ngày</th>
                        <th className="text-left py-3 px-4">Khách hàng</th>
                        <th className="text-left py-3 px-4">Thiết bị</th>
                        <th className="text-left py-3 px-4">Trạng thái</th>
                        <th className="text-right py-3 px-4">Tổng tiền</th>
                    </tr>
                </thead>
                <tbody>
                    {historyJobs.map(job => (
                        <tr key={job.id} className="border-b border-border hover:bg-muted/50">
                            <td className="py-3 px-4">{format(job.date, 'dd/MM/yyyy')}</td>
                            <td className="py-3 px-4 font-medium">{job.customer}</td>
                            <td className="py-3 px-4">{job.device}</td>
                            <td className="py-3 px-4">
                                <span className="bg-green-100 text-green-800 px-2 py-1 rounded-full text-xs font-medium">
                                    {job.status}
                                </span>
                            </td>
                            <td className="py-3 px-4 text-right">{job.total_amount?.toLocaleString()} đ</td>
                        </tr>
                    ))}
                    {historyJobs.length === 0 && (
                        <tr>
                            <td colSpan={5} className="py-8 text-center text-muted-foreground">Chưa có lịch sử công việc.</td>
                        </tr>
                    )}
                </tbody>
            </table>
        </div>
    )
}

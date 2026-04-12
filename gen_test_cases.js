const fs = require('fs');
const path = require('path');
const ExcelJS = require('exceljs');

async function generateExcel() {
  const workbook = new ExcelJS.Workbook();
  workbook.creator = 'AI Assistant';
  workbook.created = new Date();

  // STYLES
  const headerStyle = {
    font: { bold: true, color: { argb: 'FFFFFFFF' } },
    fill: { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FF0070C0' } },
    alignment: { vertical: 'middle', horizontal: 'center', wrapText: true },
    border: {
      top: { style: 'thin' }, left: { style: 'thin' },
      bottom: { style: 'thin' }, right: { style: 'thin' }
    }
  };

  const rowStyle = {
    alignment: { vertical: 'top', horizontal: 'left', wrapText: true },
    border: {
      top: { style: 'thin' }, left: { style: 'thin' },
      bottom: { style: 'thin' }, right: { style: 'thin' }
    }
  };

  // 1. BLACK BOX TESTING SHEET
  const blackBoxSheet = workbook.addWorksheet('Black Box Testing');
  blackBoxSheet.columns = [
    { header: 'Test Case ID', key: 'id', width: 15 },
    { header: 'Module', key: 'module', width: 20 },
    { header: 'Role (Vai trò)', key: 'role', width: 20 },
    { header: 'Mục tiêu Test (Test Objective)', key: 'objective', width: 45 },
    { header: 'Các bước thực hiện (Steps)', key: 'steps', width: 55 },
    { header: 'Dữ liệu Test (Input Data)', key: 'input', width: 35 },
    { header: 'Kết quả mong đợi (Expected Result)', key: 'expected', width: 45 },
    { header: 'Trạng thái', key: 'status', width: 15 }
  ];

  const blackBoxData = [
    {
      id: 'BBT-AUTH-01', module: 'Authentication', role: 'Khách hàng/NV',
      objective: 'Kiểm tra đăng nhập thành công với tài khoản hợp lệ',
      steps: '1. Mở trang Đăng nhập\n2. Nhập Email hợp lệ\n3. Nhập Mật khẩu đúng\n4. Nhấn "Đăng nhập"',
      input: 'Email: customer@test.com\nPass: 12345678',
      expected: 'Đăng nhập thành công, chuyển hướng đến trang Home/Dashboard tương ứng với quyền của user. Lưu token.',
      status: 'Chưa test'
    },
    {
      id: 'BBT-AUTH-02', module: 'Authentication', role: 'Khách hàng/NV',
      objective: 'Kiểm tra đăng nhập với sai mật khẩu',
      steps: '1. Mở trang Đăng nhập\n2. Nhập Email hợp lệ\n3. Nhập sai Mật khẩu\n4. Nhấn "Đăng nhập"',
      input: 'Email: customer@test.com\nPass: wrongpass',
      expected: 'Hệ thống từ chối đăng nhập. Hiển thị thông báo lỗi "Email hoặc mật khẩu không chính xác".',
      status: 'Chưa test'
    },
    {
      id: 'BBT-BOOK-01', module: 'Booking', role: 'Khách hàng',
      objective: 'Đặt lịch dịch vụ thành công đầy đủ thông tin',
      steps: '1. Chọn dịch vụ cần sửa chữa\n2. Chọn ngày & giờ\n3. Nhập đầy đủ địa chỉ, số điện thoại\n4. Nhấn "Đặt lịch"',
      input: 'Service: Sửa máy lạnh\nDate: 10/06/2026\nPhone: 0987654321',
      expected: 'Tạo Booking thành công, hiển thị mã Booking. Thông báo realtime gởi đến hệ thống Admin.',
      status: 'Chưa test'
    },
    {
      id: 'BBT-BOOK-02', module: 'Booking', role: 'Khách hàng',
      objective: 'Kiểm tra Ràng buộc (Validation) khi bỏ trống trường bắt buộc',
      steps: '1. Chọn dịch vụ\n2. Bỏ trống khoảng thời gian (Time slot)\n3. Nhấn "Đặt lịch"',
      input: 'Trống trường Time slot',
      expected: 'Form báo lỗi đỏ dưới trường Thời gian: "Vui lòng chọn thời gian thực hiện dịch vụ". Form không được submit.',
      status: 'Chưa test'
    },
    {
      id: 'BBT-TRACK-01', module: 'Tracking', role: 'Khách hàng',
      objective: 'Kiểm tra hiển thị trạng thái Booking theo thời gian thực',
      steps: '1. Admin đổi trạng thái thành "In Progress" ở backend\n2. Quan sát trang Tracking trên máy KH',
      input: 'Thay đổi status từ backend (Pending -> In Progress)',
      expected: 'Trang Tracking của KH tự động re-render hiển thị thanh tiến trình ở bước "Đang tiến hành" (Socket/Polling hoạt động tốt).',
      status: 'Chưa test'
    },
    {
      id: 'BBT-TECH-01', module: 'Technician Kanban', role: 'Kỹ thuật viên',
      objective: 'Kéo thả công việc để nhận việc (Drag & Drop)',
      steps: '1. Mở KanbanBoard\n2. Kéo một Job từ "Todo/New" sang "In Progress"',
      input: 'Action: Kéo Box của Booking ID: BK-123',
      expected: 'Job lưu thành công vào cột In Progress, gọi API cập nhật status hệ thống mà không lỗi (200 OK).',
      status: 'Chưa test'
    },
    {
      id: 'BBT-TECH-02', module: 'Technician Job', role: 'Kỹ thuật viên',
      objective: 'Cập nhật hoàn thành sửa chữa kèm tải ảnh lên',
      steps: '1. Nhấn trực tiếp vào thẻ job hiện modal\n2. Cập nhật note, upload 2 tấm ảnh hiện trạng sửa chữa\n3. Trạng thái -> "Completed"',
      input: 'Images (jpg), Note: "Đã thay ga máy lạnh"',
      expected: 'Upload ảnh thành công, Booking chuyển sang hoàn tất. Chuyển thông báo rate cho KH.',
      status: 'Chưa test'
    },
    {
      id: 'BBT-ADM-01', module: 'Admin Overview', role: 'Admin',
      objective: 'Xem báo cáo doanh thu theo tháng trên biểu đồ',
      steps: '1. Đăng nhập Admin\n2. Vào AdminOverview\n3. Chọn bộ lọc "Tháng này"',
      input: 'Filter Date: 01/03/2026 - 31/03/2026',
      expected: 'Biểu đồ (Chart) thống kê và các thẻ tổng hợp (Total Revenue) hiển thị dữ liệu khớp với DB.',
      status: 'Chưa test'
    },
    {
      id: 'BBT-RATE-01', module: 'Rating Modal', role: 'Khách hàng',
      objective: 'Gửi đánh giá sau khi hoàn thành đơn',
      steps: '1. Khách hàng nhận được prompt Rating Modal (rating-modal.tsx)\n2. Chọn 5 sao\n3. Để lại Comment\n4. Submit',
      input: 'Score: 5, Comment: "Nhanh chóng, thợ nhiệt tình"',
      expected: 'Lưu điểm Rate vào DB, Rating Modal đóng lại và hiển thị thông báo "Cảm ơn bạn đã đánh giá!"',
      status: 'Chưa test'
    }
  ];

  blackBoxSheet.addRows(blackBoxData);
  blackBoxSheet.getRow(1).eachCell((cell) => { cell.style = headerStyle; });
  blackBoxSheet.eachRow((row, rowNumber) => {
    if (rowNumber > 1) {
      row.eachCell({ includeEmpty: true }, (cell) => { cell.style = rowStyle; });
    }
  });

  // 2. WHITE BOX TESTING SHEET
  const whiteBoxSheet = workbook.addWorksheet('White Box Testing');
  whiteBoxSheet.columns = [
    { header: 'Test Case ID', key: 'id', width: 15 },
    { header: 'Module/Component', key: 'module', width: 25 },
    { header: 'File Name', key: 'file', width: 30 },
    { header: 'Mục tiêu kiểm thử & Logics kiểm tra', key: 'objective', width: 45 },
    { header: 'Đầu vào (Mocks/Inputs)', key: 'input', width: 40 },
    { header: 'Đầu ra mong đợi (Expected Output/Return)', key: 'expected', width: 45 },
    { header: 'Coverage Target', key: 'coverage', width: 20 }
  ];

  const whiteBoxData = [
    {
      id: 'WBT-JWT-01', module: 'Auth Guard (Backend)', file: 'jwt-auth.guard.ts',
      objective: 'Kiểm tra cơ chế chặn truy cập khi Token hết hạn (Expired)',
      input: 'Request Header kèm `Authorization: Bearer <EXPIRED_TOKEN>`',
      expected: 'Guard decode token phát hiện hết hạn -> Return false -> throw `UnauthorizedException(401)`. Không gọi tới resolver/controller.',
      coverage: 'Branch Coverage (Token validation flow)'
    },
    {
      id: 'WBT-ROLE-01', module: 'Role Guard (Backend)', file: 'role.guard.ts',
      objective: 'Kiểm tra Auth Role: User cố tình gọi API của Admin',
      input: 'Mục tiêu: Gọi endpoint `@Roles("Admin")` bằng Token có role=`Customer`',
      expected: 'Nhánh logic kiểm tra `roles.includes(user.role)` ra false -> Throw `ForbiddenException(403)`.',
      coverage: 'Statement Coverage'
    },
    {
      id: 'WBT-DB-01', module: 'Database Transactions', file: 'booking.service.ts',
      objective: 'Kiểm tra Rollback khi Transaction ghi lỗi giữa chừng (ACID)',
      input: 'Khởi tạo transaction -> Lưu Booking(Success) -> Lưu BookingServices(Throw Fake Error)',
      expected: 'Bắt được Exception -> Gọi `queryRunner.rollbackTransaction()` -> Bảng Booking không xuất hiện record mới (không dính rác).',
      coverage: 'Path Coverage (Catch Block)'
    },
    {
      id: 'WBT-LOG-01', module: 'Pricing Logic', file: 'invoice.service.ts (hoặc tương đương)',
      objective: 'Tính toán chính xác tổng tiền khi có Dịch vụ + Vật tư + Thuế VAT',
      input: 'Services = 300k, Materials = 200k, VAT = 10%.',
      expected: 'Hàm trả về tổng tiền: (300+200) * 1.1 = 550k. Hàm thực hiện theo đúng phép tính nhân/chia số thập phân, không làm tròn gãy.',
      coverage: 'Statement Coverage'
    },
    {
      id: 'WBT-HOOK-01', module: 'React Hooks (Frontend)', file: 'useTechnicianJobs.ts',
      objective: 'Kiểm tra luồng xử lý Async state `isLoading`, `error` trong hook',
      input: 'Mock API trả về Error(500)',
      expected: '1. Render 1: `isLoading = true`\n2. Render 2: `isLoading = false`, `error = "Internal Server Error"`, dữ liệu rỗng.',
      coverage: 'State Transition Coverage'
    },
    {
      id: 'WBT-COMP-01', module: 'React Components', file: 'KanbanBoard.tsx',
      objective: 'Kiểm tra logic Drag and drop xử lý array slice/splice trong local state khi sự kiện drop kích hoạt',
      input: 'Drop Object X (Id: BK123) từ ListA index 0 sang ListB index 2.',
      expected: 'Sửa đổi Context Data/State đúng: Khỏi `Todo` (splice index 0) -> Thêm vào `InProgress` (splice index 2) -> gọi Callback Update Backend.',
      coverage: 'Branch Coverage (DragEnd event)'
    },
    {
      id: 'WBT-RATE-01', module: 'React Component Logic', file: 'rating-modal.tsx',
      objective: 'Chặn submit nếu người dùng chọn 0 sao (Chưa chọn sao nào)',
      input: 'State `rating` = 0. Gọi hàm `onSubmit()`',
      expected: 'Logic kiểm tra `if (rating === 0)` chạy -> Return early, hiển thị `toast.error("Vui lòng chọn số sao")` -> Không gọi API endpoint',
      coverage: 'Statement / Branch Coverage'
    }
  ];

  whiteBoxSheet.addRows(whiteBoxData);
  whiteBoxSheet.getRow(1).eachCell((cell) => { cell.style = headerStyle; });
  whiteBoxSheet.eachRow((row, rowNumber) => {
    if (rowNumber > 1) {
      row.eachCell({ includeEmpty: true }, (cell) => { cell.style = rowStyle; });
    }
  });

  const outputPath = path.join('e:', 'PM dich vu', 'Templatelord', 'Test_Cases_FixtechPro.xlsx');
  await workbook.xlsx.writeFile(outputPath);
  console.log('File created successfully at:', outputPath);
}

generateExcel().catch(err => {
  console.error(err);
});

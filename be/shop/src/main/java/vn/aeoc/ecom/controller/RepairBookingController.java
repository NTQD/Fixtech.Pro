package vn.aeoc.ecom.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.aeoc.base.config.extention.paging.Page;
import vn.aeoc.base.config.extention.paging.Pageable;
import vn.aeoc.base.config.model.ApiResponse;
import vn.aeoc.base.util.Mapper;
import vn.aeoc.ecom.dto.request.*;
import vn.aeoc.ecom.service.RepairBookingFlowService;
import vn.aeoc.ecom.service.RepairBookingService;
import vn.aeoc.entity.data.mysql.RepairBooking;
import vn.aeoc.entity.data.mysql.User;
import vn.aeoc.rbac.annotation.IsAdmin;
import vn.aeoc.rbac.annotation.IsSeller;
import vn.aeoc.rbac.annotation.IsUser;
import vn.aeoc.rbac.config.principal.impl.AppPrincipal;

@RestController
@AllArgsConstructor
public class RepairBookingController {
    private final RepairBookingService bookingService;
    private final RepairBookingFlowService flowService;

    @PostMapping("/bookings")
    @IsUser
    public ResponseEntity<ApiResponse<RepairBooking>> create(@RequestBody CreateBookingRequest request,
                                                             AppPrincipal<User> principal) {
        User user = principal != null ? principal.getOtherInfo() : null;
        return Mapper.map(flowService.createBooking(request, user), ApiResponse::okEntity);
    }

    @GetMapping("/bookings/me")
    @IsUser
    public ResponseEntity<ApiResponse<Page<RepairBooking>>> myBookings(
            @RequestParam(required = false) String status,
            AppPrincipal<User> principal,
            Pageable pageable) {
        Integer userId = Integer.valueOf(principal.getName());
        return Mapper.map(bookingService.getByCriteria(null, status, userId, null, pageable), ApiResponse::okEntity);
    }

    @GetMapping("/bookings/{id}")
    @IsUser
    public ResponseEntity<ApiResponse<vn.aeoc.ecom.dto.response.BookingDetailResponse>> detail(@PathVariable Integer id) {
        return Mapper.map(flowService.getDetail(id), ApiResponse::okEntity);
    }

    @GetMapping("/admin/bookings")
    @IsAdmin
    public ResponseEntity<ApiResponse<Page<RepairBooking>>> adminList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer customerId,
            @RequestParam(required = false) Integer technicianId,
            Pageable pageable) {
        return Mapper.map(bookingService.getByCriteria(keyword, status, customerId, technicianId, pageable), ApiResponse::okEntity);
    }

    @PostMapping("/bookings/{id}/cancel")
    @IsUser
    public ResponseEntity<ApiResponse<String>> cancel(@PathVariable Integer id, @RequestBody BookingCancelRequest request) {
        flowService.cancelBooking(id, request);
        return Mapper.map("Hủy booking thành công", ApiResponse::okEntity);
    }

    @PostMapping("/admin/bookings/{id}/confirm")
    @IsAdmin
    public ResponseEntity<ApiResponse<String>> confirm(@PathVariable Integer id, @RequestBody ChangeBookingStatusRequest request) {
        request.setStatus("CONFIRMED");
        flowService.changeStatus(id, request);
        return Mapper.map("Xác nhận thành công", ApiResponse::okEntity);
    }

    @PostMapping("/admin/bookings/{id}/assign-technician")
    @IsAdmin
    public ResponseEntity<ApiResponse<String>> assignTechnician(@PathVariable Integer id, @RequestBody AssignTechnicianRequest request) {
        flowService.assignTechnician(id, request);
        return Mapper.map("Phân công thành công", ApiResponse::okEntity);
    }

    @PostMapping("/admin/bookings/{id}/status")
    @IsAdmin
    public ResponseEntity<ApiResponse<String>> adminChangeStatus(@PathVariable Integer id, @RequestBody ChangeBookingStatusRequest request) {
        flowService.changeStatus(id, request);
        return Mapper.map("Cập nhật trạng thái thành công", ApiResponse::okEntity);
    }

    @PostMapping("/technician/bookings/{id}/status")
    @IsSeller
    public ResponseEntity<ApiResponse<String>> changeStatus(@PathVariable Integer id, @RequestBody ChangeBookingStatusRequest request) {
        flowService.changeStatus(id, request);
        return Mapper.map("Cập nhật trạng thái thành công", ApiResponse::okEntity);
    }

    @PostMapping("/technician/bookings/{id}/notes")
    @IsSeller
    public ResponseEntity<ApiResponse<String>> addNote(@PathVariable Integer id, @RequestBody String note, AppPrincipal<User> principal) {
        Integer createdBy = principal != null ? Integer.valueOf(principal.getName()) : null;
        flowService.addNote(id, note, createdBy);
        return Mapper.map("Thêm ghi chú thành công", ApiResponse::okEntity);
    }
}

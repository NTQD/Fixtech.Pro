package vn.aeoc.ecom.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.aeoc.base.config.extention.paging.Page;
import vn.aeoc.base.config.extention.paging.Pageable;
import vn.aeoc.base.config.model.ApiResponse;
import vn.aeoc.base.util.Mapper;
import vn.aeoc.ecom.dto.request.CreateReviewRequest;
import vn.aeoc.ecom.service.RepairBookingFlowService;
import vn.aeoc.ecom.service.RepairReviewService;
import vn.aeoc.entity.data.mysql.RepairReview;
import vn.aeoc.entity.data.mysql.User;
import vn.aeoc.rbac.annotation.IsUser;
import vn.aeoc.rbac.config.principal.impl.AppPrincipal;

@RestController
@AllArgsConstructor
public class RepairReviewController {
    private final RepairReviewService reviewService;
    private final RepairBookingFlowService flowService;

    @PostMapping("/bookings/{id}/review")
    @IsUser
    public ResponseEntity<ApiResponse<RepairReview>> create(@PathVariable Integer id,
                                                            @RequestBody CreateReviewRequest request,
                                                            AppPrincipal<User> principal) {
        User user = principal != null ? principal.getOtherInfo() : null;
        return Mapper.map(flowService.createReview(id, request, user), ApiResponse::okEntity);
    }

    @GetMapping("/services/{serviceId}/reviews")
    public ResponseEntity<ApiResponse<Page<RepairReview>>> list(@PathVariable Integer serviceId,
                                                                 Pageable pageable) {
        return Mapper.map(reviewService.getByServiceId(serviceId, pageable), ApiResponse::okEntity);
    }
}

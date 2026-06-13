package vn.aeoc.ecom.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.aeoc.base.config.extention.paging.Page;
import vn.aeoc.base.config.extention.paging.Pageable;
import vn.aeoc.base.config.model.ApiResponse;
import vn.aeoc.base.util.Mapper;
import vn.aeoc.ecom.dto.response.DashboardOverviewDto;
import vn.aeoc.ecom.dto.response.RevenueDetailDto;
import vn.aeoc.ecom.service.AdminDashboardService;
import vn.aeoc.rbac.annotation.IsAdmin;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {
    private final AdminDashboardService adminDashboardService;

    @GetMapping("/overview")
    @IsAdmin
    public ResponseEntity<ApiResponse<DashboardOverviewDto>> getOverview() {
        return Mapper.map(adminDashboardService.getOverview(), ApiResponse::okEntity);
    }

    @GetMapping("/revenue-details")
    @IsAdmin
    public ResponseEntity<ApiResponse<Page<RevenueDetailDto>>> getRevenueDetails(Pageable pageable) {
        return Mapper.map(adminDashboardService.getRevenueDetails(pageable), ApiResponse::okEntity);
    }
}

package vn.aeoc.ecom.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.aeoc.base.config.extention.paging.Page;
import vn.aeoc.base.config.extention.paging.Pageable;
import vn.aeoc.base.config.model.ApiResponse;
import vn.aeoc.base.util.Mapper;
import vn.aeoc.ecom.dto.request.RepairServiceRequest;
import vn.aeoc.ecom.service.RepairServiceService;
import vn.aeoc.entity.data.mysql.RepairService;
import vn.aeoc.rbac.annotation.IsAdmin;

@RestController
@AllArgsConstructor
public class RepairServiceController {
    private final RepairServiceService service;

    @GetMapping("/services")
    public ResponseEntity<ApiResponse<Page<RepairService>>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            Pageable pageable) {
        return Mapper.map(service.getByCriteria(keyword, categoryId, pageable), ApiResponse::okEntity);
    }

    @GetMapping("/services/{id}")
    public ResponseEntity<ApiResponse<RepairService>> detail(@PathVariable Integer id) {
        return Mapper.map(service.getById(id), ApiResponse::okEntity);
    }

    @PostMapping("/admin/services")
    @IsAdmin
    public ResponseEntity<ApiResponse<RepairService>> create(@RequestBody RepairServiceRequest request) {
        RepairService entity = new RepairService();
        entity.setCategoryId(request.getCategoryId());
        entity.setName(request.getName());
        entity.setShortDescription(request.getShortDescription());
        entity.setDescription(request.getDescription());
        entity.setBasePrice(request.getBasePrice());
        entity.setEstimatedMinutes(request.getEstimatedMinutes());
        entity.setWarrantyDays(request.getWarrantyDays());
        entity.setActive(request.getActive() == null || request.getActive());
        return Mapper.map(service.insert(entity), ApiResponse::okEntity);
    }

    @PutMapping("/admin/services/{id}")
    @IsAdmin
    public ResponseEntity<ApiResponse<RepairService>> update(@PathVariable Integer id, @RequestBody RepairServiceRequest request) {
        RepairService entity = new RepairService();
        entity.setCategoryId(request.getCategoryId());
        entity.setName(request.getName());
        entity.setShortDescription(request.getShortDescription());
        entity.setDescription(request.getDescription());
        entity.setBasePrice(request.getBasePrice());
        entity.setEstimatedMinutes(request.getEstimatedMinutes());
        entity.setWarrantyDays(request.getWarrantyDays());
        entity.setActive(request.getActive());
        return Mapper.map(service.update(id, entity), ApiResponse::okEntity);
    }

    @DeleteMapping("/admin/services/{id}")
    @IsAdmin
    public ResponseEntity<ApiResponse<Integer>> delete(@PathVariable Integer id) {
        return Mapper.map(service.delete(id), ApiResponse::okEntity);
    }
}

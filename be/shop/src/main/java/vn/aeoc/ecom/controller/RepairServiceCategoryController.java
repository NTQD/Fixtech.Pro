package vn.aeoc.ecom.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.aeoc.base.config.extention.paging.Page;
import vn.aeoc.base.config.extention.paging.Pageable;
import vn.aeoc.base.config.model.ApiResponse;
import vn.aeoc.base.util.Mapper;
import vn.aeoc.ecom.dto.request.RepairServiceCategoryRequest;
import vn.aeoc.ecom.service.RepairServiceCategoryService;
import vn.aeoc.entity.data.mysql.RepairServiceCategory;
import vn.aeoc.rbac.annotation.IsAdmin;

@RestController
@AllArgsConstructor
@RequestMapping("/admin/service-categories")
public class RepairServiceCategoryController {
    private final RepairServiceCategoryService service;

    @GetMapping
    @IsAdmin
    public ResponseEntity<ApiResponse<Page<RepairServiceCategory>>> list(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        return Mapper.map(service.getByCriteria(keyword, pageable), ApiResponse::okEntity);
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity<ApiResponse<RepairServiceCategory>> create(@RequestBody RepairServiceCategoryRequest request) {
        RepairServiceCategory entity = new RepairServiceCategory();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setActive(request.getActive() == null || request.getActive());
        return Mapper.map(service.insert(entity), ApiResponse::okEntity);
    }

    @PutMapping("/{id}")
    @IsAdmin
    public ResponseEntity<ApiResponse<RepairServiceCategory>> update(@PathVariable Integer id, @RequestBody RepairServiceCategoryRequest request) {
        RepairServiceCategory entity = new RepairServiceCategory();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setActive(request.getActive());
        return Mapper.map(service.update(id, entity), ApiResponse::okEntity);
    }

    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity<ApiResponse<Integer>> delete(@PathVariable Integer id) {
        return Mapper.map(service.delete(id), ApiResponse::okEntity);
    }
}

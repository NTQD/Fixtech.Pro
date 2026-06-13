package vn.aeoc.ecom.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.aeoc.base.config.extention.paging.Page;
import vn.aeoc.base.config.extention.paging.Pageable;
import vn.aeoc.base.config.model.ApiResponse;
import vn.aeoc.base.util.Mapper;
import vn.aeoc.ecom.dto.request.InventoryItemRequest;
import vn.aeoc.ecom.dto.response.InventoryItemDto;
import vn.aeoc.ecom.service.InventoryItemService;
import vn.aeoc.rbac.annotation.IsAdmin;

@RestController
@RequestMapping("/api/v1/admin/inventory")
@RequiredArgsConstructor
public class AdminInventoryController {

    private final InventoryItemService inventoryItemService;

    @GetMapping
    @IsAdmin
    public ResponseEntity<ApiResponse<Page<InventoryItemDto>>> getInventoryItems(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        return Mapper.map(inventoryItemService.getInventoryItems(keyword, pageable), ApiResponse::okEntity);
    }

    @GetMapping("/{id}")
    @IsAdmin
    public ResponseEntity<ApiResponse<InventoryItemDto>> getInventoryItem(@PathVariable Integer id) {
        return Mapper.map(inventoryItemService.getInventoryItem(id), ApiResponse::okEntity);
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity<ApiResponse<InventoryItemDto>> createInventoryItem(@RequestBody InventoryItemRequest request) {
        return Mapper.map(inventoryItemService.createInventoryItem(request), ApiResponse::okEntity);
    }

    @PutMapping("/{id}")
    @IsAdmin
    public ResponseEntity<ApiResponse<InventoryItemDto>> updateInventoryItem(@PathVariable Integer id, @RequestBody InventoryItemRequest request) {
        return Mapper.map(inventoryItemService.updateInventoryItem(id, request), ApiResponse::okEntity);
    }

    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity<ApiResponse<Integer>> deleteInventoryItem(@PathVariable Integer id) {
        return Mapper.map(inventoryItemService.deleteInventoryItem(id), ApiResponse::okEntity);
    }
}

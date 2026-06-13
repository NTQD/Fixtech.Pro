package vn.aeoc.ecom.dto.response;

import lombok.Data;
import vn.aeoc.entity.data.mysql.InventoryItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InventoryItemDto {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static InventoryItemDto fromEntity(InventoryItem entity) {
        if (entity == null) return null;
        InventoryItemDto dto = new InventoryItemDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setStock(entity.getStock());
        dto.setImageUrl(entity.getImageUrl());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}

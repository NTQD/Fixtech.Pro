package vn.aeoc.ecom.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InventoryItemRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
}

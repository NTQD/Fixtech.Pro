package vn.aeoc.entity.data.mysql;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryItem {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal sellingPrice;
    private Integer stockQuantity;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BigDecimal getPrice() {
        return sellingPrice;
    }

    public void setPrice(BigDecimal price) {
        this.sellingPrice = price;
    }

    public Integer getStock() {
        return stockQuantity;
    }

    public void setStock(Integer stock) {
        this.stockQuantity = stock;
    }
}

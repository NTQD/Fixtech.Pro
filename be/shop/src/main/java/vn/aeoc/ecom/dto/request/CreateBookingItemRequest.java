package vn.aeoc.ecom.dto.request;

import lombok.Data;

@Data
public class CreateBookingItemRequest {
    private Integer serviceId;
    private Integer quantity;
}

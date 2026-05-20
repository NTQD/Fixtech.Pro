package vn.aeoc.ecom.dto.request;

import lombok.Data;

@Data
public class RepairServiceRequest {
    private Integer categoryId;
    private String name;
    private String shortDescription;
    private String description;
    private Long basePrice;
    private Integer estimatedMinutes;
    private Integer warrantyDays;
    private Boolean active;
}

package vn.aeoc.ecom.dto.request;

import lombok.Data;

@Data
public class RepairServiceCategoryRequest {
    private String name;
    private String description;
    private Boolean active;
}

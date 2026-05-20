package vn.aeoc.entity.data.mysql;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepairService {
    private Integer id;
    private Integer categoryId;
    private String name;
    private String shortDescription;
    private String description;
    private Long basePrice;
    private Integer estimatedMinutes;
    private Integer warrantyDays;
    private Boolean active;
}

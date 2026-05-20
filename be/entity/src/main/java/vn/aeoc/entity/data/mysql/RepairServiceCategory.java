package vn.aeoc.entity.data.mysql;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepairServiceCategory {
    private Integer id;
    private String name;
    private String description;
    private Boolean active;
}

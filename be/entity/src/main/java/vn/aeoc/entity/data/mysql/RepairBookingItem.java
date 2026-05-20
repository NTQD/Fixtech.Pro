package vn.aeoc.entity.data.mysql;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepairBookingItem {
    private Integer id;
    private Integer bookingId;
    private Integer serviceId;
    private String serviceName;
    private Integer quantity;
    private Long estimatedPrice;
    private Long finalPrice;
}

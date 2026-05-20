package vn.aeoc.entity.data.mysql;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepairBookingStatusHistory {
    private Integer id;
    private Integer bookingId;
    private String fromStatus;
    private String toStatus;
    private String note;
    private Integer changedBy;
    private Timestamp createdAt;
}

package vn.aeoc.entity.data.mysql;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepairBookingNote {
    private Integer id;
    private Integer bookingId;
    private Integer createdBy;
    private String note;
    private Timestamp createdAt;
}

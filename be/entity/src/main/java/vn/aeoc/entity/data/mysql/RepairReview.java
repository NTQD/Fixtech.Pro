package vn.aeoc.entity.data.mysql;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepairReview {
    private Integer id;
    private Integer bookingId;
    private Integer serviceId;
    private Integer customerId;
    private Integer rating;
    private String comment;
    private Timestamp createdAt;
}

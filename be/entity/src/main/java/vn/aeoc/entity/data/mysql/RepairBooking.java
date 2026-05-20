package vn.aeoc.entity.data.mysql;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepairBooking {
    private Integer id;
    private String bookingCode;
    private Integer customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String deviceType;
    private String deviceBrand;
    private String deviceModel;
    private String issueDescription;
    private String preferredDate;
    private String preferredTimeSlot;
    private String address;
    private String note;
    private String status;
    private Integer technicianId;
    private Long totalEstimatedPrice;
    private Integer totalEstimatedMinutes;
    private Timestamp scheduledAt;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}

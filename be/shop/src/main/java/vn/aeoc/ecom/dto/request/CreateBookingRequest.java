package vn.aeoc.ecom.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateBookingRequest {
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String deviceType;
    private String deviceBrand;
    private String deviceModel;
    private String issueDescription;
    private LocalDate preferredDate;
    private String preferredTimeSlot;
    private String address;
    private String note;
    private List<CreateBookingItemRequest> items;
}

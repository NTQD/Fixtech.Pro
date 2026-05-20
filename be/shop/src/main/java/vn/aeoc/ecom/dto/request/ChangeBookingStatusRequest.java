package vn.aeoc.ecom.dto.request;

import lombok.Data;

@Data
public class ChangeBookingStatusRequest {
    private String status;
    private String note;
}

package vn.aeoc.ecom.dto.response;

import lombok.Data;
import vn.aeoc.entity.data.mysql.RepairBooking;
import vn.aeoc.entity.data.mysql.RepairBookingItem;
import vn.aeoc.entity.data.mysql.RepairBookingStatusHistory;

import java.util.List;

@Data
public class BookingDetailResponse {
    private RepairBooking booking;
    private List<RepairBookingItem> items;
    private List<RepairBookingStatusHistory> statusHistory;
}

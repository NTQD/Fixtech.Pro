package vn.aeoc.ecom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOverviewDto {
    private Double totalRevenue;
    private Integer totalBookings;
    private Integer totalUsers;
    private Integer totalInventoryItems;
}

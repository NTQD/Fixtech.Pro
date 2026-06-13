package vn.aeoc.ecom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueDetailDto {
    private Long bookingId;
    private String bookingCode;
    private Double amount;
    private Integer rating;
    private String customerName;
    private LocalDateTime completedAt;
}

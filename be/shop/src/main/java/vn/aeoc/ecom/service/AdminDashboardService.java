package vn.aeoc.ecom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import vn.aeoc.base.config.extention.paging.Page;
import vn.aeoc.base.config.extention.paging.Pageable;
import vn.aeoc.ecom.dto.response.DashboardOverviewDto;
import vn.aeoc.ecom.dto.response.RevenueDetailDto;

import java.math.BigDecimal;
import java.util.List;

import static vn.entity.backend.tables.InventoryItem.INVENTORY_ITEM;
import static vn.entity.backend.tables.RepairBooking.REPAIR_BOOKING;
import static vn.entity.backend.tables.RepairReview.REPAIR_REVIEW;
import static vn.entity.backend.tables.User.USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminDashboardService {
    private final DSLContext dslContext;

    public DashboardOverviewDto getOverview() {
        // Total revenue from completed orders
        BigDecimal totalRevenue = dslContext.select(DSL.sum(REPAIR_BOOKING.TOTAL_ESTIMATED_PRICE))
                .from(REPAIR_BOOKING)
                .where(REPAIR_BOOKING.STATUS.eq("COMPLETED"))
                .fetchOneInto(BigDecimal.class);

        // Total bookings (not cancelled)
        Integer totalBookings = dslContext.selectCount()
                .from(REPAIR_BOOKING)
                .where(REPAIR_BOOKING.STATUS.ne("CANCELLED"))
                .fetchOne(0, Integer.class);

        // Total users (customers only)
        Integer totalUsers = dslContext.selectCount()
                .from(USER)
                .where(USER.ROLE.eq("CUSTOMER"))
                .fetchOne(0, Integer.class);

        // Total inventory items
        Integer totalInventoryItems = dslContext.selectCount()
                .from(INVENTORY_ITEM)
                .fetchOne(0, Integer.class);

        return DashboardOverviewDto.builder()
                .totalRevenue(totalRevenue != null ? totalRevenue.doubleValue() : 0.0)
                .totalBookings(totalBookings != null ? totalBookings : 0)
                .totalUsers(totalUsers != null ? totalUsers : 0)
                .totalInventoryItems(totalInventoryItems != null ? totalInventoryItems : 0)
                .build();
    }

    public Page<RevenueDetailDto> getRevenueDetails(Pageable pageable) {
        long totalElements = dslContext.selectCount()
                .from(REPAIR_BOOKING)
                .where(REPAIR_BOOKING.STATUS.eq("COMPLETED"))
                .fetchOne(0, Long.class);

        var result = dslContext.select(
                REPAIR_BOOKING.ID,
                REPAIR_BOOKING.BOOKING_CODE,
                REPAIR_BOOKING.TOTAL_ESTIMATED_PRICE,
                REPAIR_BOOKING.CUSTOMER_NAME,
                REPAIR_BOOKING.UPDATED_AT,
                REPAIR_REVIEW.RATING
        )
                .from(REPAIR_BOOKING)
                .leftJoin(REPAIR_REVIEW).on(REPAIR_BOOKING.ID.eq(REPAIR_REVIEW.BOOKING_ID.cast(Integer.class)))
                .where(REPAIR_BOOKING.STATUS.eq("COMPLETED"))
                .orderBy(REPAIR_BOOKING.UPDATED_AT.desc())
                .limit(pageable.getLimit())
                .offset(pageable.getOffset())
                .fetch();

        List<RevenueDetailDto> items = result.map(record -> RevenueDetailDto.builder()
                .bookingId(record.get(REPAIR_BOOKING.ID).longValue())
                .bookingCode(record.get(REPAIR_BOOKING.BOOKING_CODE))
                .amount(record.get(REPAIR_BOOKING.TOTAL_ESTIMATED_PRICE) != null ? record.get(REPAIR_BOOKING.TOTAL_ESTIMATED_PRICE).doubleValue() : 0.0)
                .rating(record.get(REPAIR_REVIEW.RATING))
                .customerName(record.get(REPAIR_BOOKING.CUSTOMER_NAME))
                .completedAt(record.get(REPAIR_BOOKING.UPDATED_AT))
                .build());

        return new Page<>(totalElements, pageable.getPage(), items);
    }
}

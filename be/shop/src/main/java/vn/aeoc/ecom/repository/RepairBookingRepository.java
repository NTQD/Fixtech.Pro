package vn.aeoc.ecom.repository;

import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.springframework.stereotype.Repository;
import vn.aeoc.base.config.extention.paging.Pageable;
import vn.aeoc.base.repo.AbsMysqlRepository;
import vn.aeoc.entity.data.mysql.RepairBooking;
import vn.entity.backend.tables.records.RepairBookingRecord;

import java.util.List;

import static vn.entity.backend.tables.RepairBooking.REPAIR_BOOKING;

@Repository
public class RepairBookingRepository extends AbsMysqlRepository<RepairBookingRecord, RepairBooking, Integer> {
    @Override
    protected TableImpl<RepairBookingRecord> getTable() {
        return REPAIR_BOOKING;
    }

    private Condition getWhereCondition(String keyword, String status, Integer customerId, Integer technicianId) {
        Condition condition = DSL.trueCondition();
        if (keyword != null && !keyword.isBlank()) {
            String likePattern = "%" + keyword.trim().toLowerCase() + "%";
            condition = condition.and(
                    DSL.lower(REPAIR_BOOKING.BOOKING_CODE).like(likePattern)
                            .or(DSL.lower(REPAIR_BOOKING.CUSTOMER_NAME).like(likePattern))
                            .or(DSL.lower(REPAIR_BOOKING.CUSTOMER_PHONE).like(likePattern))
                            .or(DSL.lower(REPAIR_BOOKING.DEVICE_TYPE).like(likePattern))
            );
        }
        if (status != null && !status.isBlank()) {
            condition = condition.and(REPAIR_BOOKING.STATUS.eq(status));
        }
        if (customerId != null) {
            condition = condition.and(REPAIR_BOOKING.CUSTOMER_ID.eq(customerId));
        }
        if (technicianId != null) {
            condition = condition.and(REPAIR_BOOKING.TECHNICIAN_ID.eq(technicianId));
        }
        return condition;
    }

    public List<RepairBooking> getByCriteria(String keyword, String status, Integer customerId, Integer technicianId, Pageable pageable) {
        return getListByCriteria(getWhereCondition(keyword, status, customerId, technicianId), REPAIR_BOOKING.ID.desc(), pageable);
    }

    public Long countByCriteria(String keyword, String status, Integer customerId, Integer technicianId) {
        return getCountWithCriteria(getWhereCondition(keyword, status, customerId, technicianId));
    }

    public Long countOverlappingBookings(java.time.LocalDate date, String timeSlot) {
        return getCountWithCriteria(
                REPAIR_BOOKING.PREFERRED_DATE.eq(date)
                        .and(REPAIR_BOOKING.PREFERRED_TIME_SLOT.eq(timeSlot))
                        .and(REPAIR_BOOKING.STATUS.notIn("CANCELLED", "COMPLETED"))
        );
    }
}

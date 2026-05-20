package vn.aeoc.ecom.repository;

import org.jooq.impl.TableImpl;
import org.springframework.stereotype.Repository;
import vn.aeoc.base.repo.AbsMysqlRepository;
import vn.aeoc.entity.data.mysql.RepairBookingStatusHistory;
import vn.entity.backend.tables.records.RepairBookingStatusHistoryRecord;

import static vn.entity.backend.tables.RepairBookingStatusHistory.REPAIR_BOOKING_STATUS_HISTORY;

@Repository
public class RepairBookingStatusHistoryRepository extends AbsMysqlRepository<RepairBookingStatusHistoryRecord, RepairBookingStatusHistory, Integer> {
    @Override
    protected TableImpl<RepairBookingStatusHistoryRecord> getTable() {
        return REPAIR_BOOKING_STATUS_HISTORY;
    }
}

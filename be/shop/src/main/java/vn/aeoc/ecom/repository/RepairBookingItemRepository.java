package vn.aeoc.ecom.repository;

import org.jooq.impl.TableImpl;
import org.springframework.stereotype.Repository;
import vn.aeoc.base.repo.AbsMysqlRepository;
import vn.aeoc.entity.data.mysql.RepairBookingItem;
import vn.entity.backend.tables.records.RepairBookingItemRecord;

import static vn.entity.backend.tables.RepairBookingItem.REPAIR_BOOKING_ITEM;

@Repository
public class RepairBookingItemRepository extends AbsMysqlRepository<RepairBookingItemRecord, RepairBookingItem, Integer> {
    @Override
    protected TableImpl<RepairBookingItemRecord> getTable() {
        return REPAIR_BOOKING_ITEM;
    }
}

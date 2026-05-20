package vn.aeoc.ecom.repository;

import org.jooq.impl.TableImpl;
import org.springframework.stereotype.Repository;
import vn.aeoc.base.repo.AbsMysqlRepository;
import vn.aeoc.entity.data.mysql.RepairBookingNote;
import vn.entity.backend.tables.records.RepairBookingNoteRecord;

import static vn.entity.backend.tables.RepairBookingNote.REPAIR_BOOKING_NOTE;

@Repository
public class RepairBookingNoteRepository extends AbsMysqlRepository<RepairBookingNoteRecord, RepairBookingNote, Integer> {
    @Override
    protected TableImpl<RepairBookingNoteRecord> getTable() {
        return REPAIR_BOOKING_NOTE;
    }
}

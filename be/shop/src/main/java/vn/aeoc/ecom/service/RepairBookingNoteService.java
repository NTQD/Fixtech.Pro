package vn.aeoc.ecom.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import vn.aeoc.base.service.BaseService;
import vn.aeoc.ecom.repository.RepairBookingNoteRepository;
import vn.aeoc.entity.data.mysql.RepairBookingNote;

@Service
public class RepairBookingNoteService extends BaseService<RepairBookingNoteRepository, RepairBookingNote, Integer> {
    public RepairBookingNoteService(RepairBookingNoteRepository repository) {
        super(repository);
    }
}

package vn.aeoc.ecom.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import vn.aeoc.base.config.extention.paging.Page;
import vn.aeoc.base.config.extention.paging.Pageable;
import vn.aeoc.base.service.BaseService;
import vn.aeoc.ecom.repository.RepairBookingRepository;
import vn.aeoc.entity.data.mysql.RepairBooking;

@Service
public class RepairBookingService extends BaseService<RepairBookingRepository, RepairBooking, Integer> {
    public RepairBookingService(RepairBookingRepository repository) {
        super(repository);
    }

    public Page<RepairBooking> getByCriteria(String keyword, String status, Integer customerId, Integer technicianId, Pageable pageable) {
        return pageQuery(pageable,
                () -> repository.getByCriteria(keyword, status, customerId, technicianId, pageable),
                () -> repository.countByCriteria(keyword, status, customerId, technicianId));
    }
}

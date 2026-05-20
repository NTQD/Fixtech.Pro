package vn.aeoc.ecom.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import vn.aeoc.base.config.extention.paging.Page;
import vn.aeoc.base.config.extention.paging.Pageable;
import vn.aeoc.base.service.BaseService;
import vn.aeoc.ecom.repository.RepairReviewRepository;
import vn.aeoc.entity.data.mysql.RepairReview;

@Service
public class RepairReviewService extends BaseService<RepairReviewRepository, RepairReview, Integer> {
    public RepairReviewService(RepairReviewRepository repository) {
        super(repository);
    }

    public Page<RepairReview> getByServiceId(Integer serviceId, Pageable pageable) {
        return pageQuery(pageable,
                () -> repository.getByServiceId(serviceId, pageable),
                () -> repository.countByServiceId(serviceId));
    }
}

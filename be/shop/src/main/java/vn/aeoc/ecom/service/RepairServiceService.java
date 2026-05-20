package vn.aeoc.ecom.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import vn.aeoc.base.config.extention.paging.Page;
import vn.aeoc.base.config.extention.paging.Pageable;
import vn.aeoc.base.service.BaseService;
import vn.aeoc.ecom.repository.RepairServiceRepository;
import vn.aeoc.entity.data.mysql.RepairService;

@Service
public class RepairServiceService extends BaseService<RepairServiceRepository, RepairService, Integer> {
    public RepairServiceService(RepairServiceRepository repository) {
        super(repository);
    }

    public Page<RepairService> getByCriteria(String keyword, Integer categoryId, Pageable pageable) {
        return pageQuery(pageable,
                () -> repository.getByCriteria(keyword, categoryId, pageable),
                () -> repository.countByCriteria(keyword, categoryId));
    }
}

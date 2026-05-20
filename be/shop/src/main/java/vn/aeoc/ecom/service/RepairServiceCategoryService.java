package vn.aeoc.ecom.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import vn.aeoc.base.config.extention.paging.Page;
import vn.aeoc.base.config.extention.paging.Pageable;
import vn.aeoc.base.service.BaseService;
import vn.aeoc.ecom.repository.RepairServiceCategoryRepository;
import vn.aeoc.entity.data.mysql.RepairServiceCategory;

@Service
public class RepairServiceCategoryService extends BaseService<RepairServiceCategoryRepository, RepairServiceCategory, Integer> {
    public RepairServiceCategoryService(RepairServiceCategoryRepository repository) {
        super(repository);
    }

    public Page<RepairServiceCategory> getByCriteria(String keyword, Pageable pageable) {
        return pageQuery(pageable,
                () -> repository.getByCriteria(keyword, pageable),
                () -> repository.countByCriteria(keyword));
    }
}

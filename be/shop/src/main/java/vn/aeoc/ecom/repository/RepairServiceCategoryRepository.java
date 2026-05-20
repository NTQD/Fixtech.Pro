package vn.aeoc.ecom.repository;

import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.springframework.stereotype.Repository;
import vn.aeoc.base.config.extention.paging.Pageable;
import vn.aeoc.base.repo.AbsMysqlRepository;
import vn.aeoc.entity.data.mysql.RepairServiceCategory;
import vn.entity.backend.tables.records.RepairServiceCategoryRecord;

import java.util.List;

import static vn.entity.backend.tables.RepairServiceCategory.REPAIR_SERVICE_CATEGORY;

@Repository
public class RepairServiceCategoryRepository extends AbsMysqlRepository<RepairServiceCategoryRecord, RepairServiceCategory, Integer> {
    @Override
    protected TableImpl<RepairServiceCategoryRecord> getTable() {
        return REPAIR_SERVICE_CATEGORY;
    }

    private Condition getWhereCondition(String keyword) {
        Condition condition = DSL.trueCondition();
        if (keyword != null && !keyword.isBlank()) {
            String likePattern = "%" + keyword.trim().toLowerCase() + "%";
            condition = condition.and(DSL.lower(REPAIR_SERVICE_CATEGORY.NAME).like(likePattern));
        }
        return condition;
    }

    public List<RepairServiceCategory> getByCriteria(String keyword, Pageable pageable) {
        return getListByCriteria(getWhereCondition(keyword), REPAIR_SERVICE_CATEGORY.ID.desc(), pageable);
    }

    public Long countByCriteria(String keyword) {
        return getCountWithCriteria(getWhereCondition(keyword));
    }
}

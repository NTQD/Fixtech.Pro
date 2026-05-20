package vn.aeoc.ecom.repository;

import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.springframework.stereotype.Repository;
import vn.aeoc.base.config.extention.paging.Pageable;
import vn.aeoc.base.repo.AbsMysqlRepository;
import vn.aeoc.entity.data.mysql.RepairService;
import vn.entity.backend.tables.records.RepairServiceRecord;

import java.util.List;

import static vn.entity.backend.tables.RepairService.REPAIR_SERVICE;

@Repository
public class RepairServiceRepository extends AbsMysqlRepository<RepairServiceRecord, RepairService, Integer> {
    @Override
    protected TableImpl<RepairServiceRecord> getTable() {
        return REPAIR_SERVICE;
    }

    private Condition getWhereCondition(String keyword, Integer categoryId) {
        Condition condition = DSL.trueCondition();
        if (keyword != null && !keyword.isBlank()) {
            String likePattern = "%" + keyword.trim().toLowerCase() + "%";
            condition = condition.and(
                    DSL.lower(REPAIR_SERVICE.NAME).like(likePattern)
                            .or(DSL.lower(REPAIR_SERVICE.SHORT_DESCRIPTION).like(likePattern))
                            .or(DSL.lower(REPAIR_SERVICE.DESCRIPTION).like(likePattern))
            );
        }
        if (categoryId != null) {
            condition = condition.and(REPAIR_SERVICE.CATEGORY_ID.eq(categoryId));
        }
        return condition;
    }

    public List<RepairService> getByCriteria(String keyword, Integer categoryId, Pageable pageable) {
        return getListByCriteria(getWhereCondition(keyword, categoryId), REPAIR_SERVICE.ID.desc(), pageable);
    }

    public Long countByCriteria(String keyword, Integer categoryId) {
        return getCountWithCriteria(getWhereCondition(keyword, categoryId));
    }
}

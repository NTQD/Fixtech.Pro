package vn.aeoc.ecom.repository;

import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.springframework.stereotype.Repository;
import vn.aeoc.base.config.extention.paging.Pageable;
import vn.aeoc.base.repo.AbsMysqlRepository;
import vn.aeoc.entity.data.mysql.RepairReview;
import vn.entity.backend.tables.records.RepairReviewRecord;

import java.util.List;

import static vn.entity.backend.tables.RepairReview.REPAIR_REVIEW;

@Repository
public class RepairReviewRepository extends AbsMysqlRepository<RepairReviewRecord, RepairReview, Integer> {
    @Override
    protected TableImpl<RepairReviewRecord> getTable() {
        return REPAIR_REVIEW;
    }

    private Condition getWhereCondition(Integer serviceId) {
        Condition condition = DSL.trueCondition();
        if (serviceId != null) {
            condition = condition.and(REPAIR_REVIEW.SERVICE_ID.eq(serviceId));
        }
        return condition;
    }

    public List<RepairReview> getByServiceId(Integer serviceId, Pageable pageable) {
        return getListByCriteria(getWhereCondition(serviceId), REPAIR_REVIEW.ID.desc(), pageable);
    }

    public Long countByServiceId(Integer serviceId) {
        return getCountWithCriteria(getWhereCondition(serviceId));
    }
}

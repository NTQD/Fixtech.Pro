package vn.aeoc.ecom.repository;

import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.springframework.stereotype.Repository;
import vn.aeoc.base.config.extention.paging.Pageable;
import vn.aeoc.base.repo.AbsMysqlRepository;
import vn.aeoc.entity.data.mysql.InventoryItem;
import vn.entity.backend.tables.records.InventoryItemRecord;

import java.util.List;

import static vn.entity.backend.tables.InventoryItem.INVENTORY_ITEM;

@Repository
public class InventoryItemRepository extends AbsMysqlRepository<InventoryItemRecord, InventoryItem, Integer> {
    @Override
    protected TableImpl<InventoryItemRecord> getTable() {
        return INVENTORY_ITEM;
    }

    private Condition getWhereCondition(String keyword) {
        Condition condition = DSL.trueCondition();
        if (keyword != null && !keyword.isBlank()) {
            String likePattern = "%" + keyword.trim().toLowerCase() + "%";
            condition = condition.and(
                    DSL.lower(INVENTORY_ITEM.NAME).like(likePattern)
                            .or(DSL.lower(INVENTORY_ITEM.DESCRIPTION).like(likePattern))
            );
        }
        return condition;
    }

    public List<InventoryItem> getByCriteria(String keyword, Pageable pageable) {
        return getListByCriteria(getWhereCondition(keyword), INVENTORY_ITEM.ID.desc(), pageable);
    }

    public Long countByCriteria(String keyword) {
        return getCountWithCriteria(getWhereCondition(keyword));
    }
}

package vn.aeoc.ecom.repository;

import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Repository;
import vn.aeoc.base.config.extention.paging.Pageable;
import vn.aeoc.base.repo.AbsMysqlRepository;
import vn.aeoc.entity.data.mysql.User;
import vn.entity.backend.tables.records.UserRecord;

import java.util.List;

import static vn.entity.backend.tables.User.USER;

@Repository
public class UserRepository extends AbsMysqlRepository<UserRecord, User, Integer> {
    @Override
    protected TableImpl<UserRecord> getTable() {
        return USER;
    }

    private Condition getWhereCondition(String keyword) {
        Condition condition = DSL.trueCondition();

        if (keyword != null && !keyword.isBlank()) {
            String likePattern = "%" + keyword.trim().toLowerCase() + "%";
            condition = condition.and(
                    DSL.lower(USER.NAME).like(likePattern).or(DSL.lower(USER.EMAIL).like(likePattern)).or(DSL.lower(USER.PHONE).like(likePattern)));
        }

        return condition;
    }

    public List<User> getByCriteria(String keyword, Pageable pageable) {
        return getListByCriteria(getWhereCondition(keyword), USER.ID.desc(), pageable);
    }

    public Long countByCriteria(String keyword) {
        return getCountWithCriteria(getWhereCondition(keyword));
    }

    public boolean existsByPhone(String phone) {
        return exists(USER.PHONE.eq(phone));
    }

    // Dùng nội bộ
    public User getUserAllFieldById(Integer id) {
        return getById(id);
    }

    // Hàm dùng nội bộ
    public User getByPhone(String phone) {
        return getOne(USER.PHONE.eq(phone));
    }

    // Hàm dùng nội bộ
    public User getByEmail(String email) {
        return getOne(USER.EMAIL.eq(email));
    }

    /* ============================================================
     *  Mutations
     * ============================================================ */

    public Integer deleteById(Integer id) {
        return delete(USER.ID.eq(id));
    }

    public void updateBasicInfo(Integer id, User user) {
        update(id,
                user,
                USER.NAME, USER.EMAIL, USER.PHONE);
    }

    public void updatePassword(Integer id, String password) {
        updateField(id, USER.PASSWORD, BCrypt.hashpw(password, BCrypt.gensalt()));
    }

    public void setActive(Integer id, boolean active) {
        updateField(id, USER.ACTIVE, active);
    }

    public User register(User user) {
        return insertReturning(user);
    }
}

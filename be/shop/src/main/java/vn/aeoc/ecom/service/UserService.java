package vn.aeoc.ecom.service;

import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import vn.aeoc.base.config.extention.paging.Page;
import vn.aeoc.base.config.extention.paging.Pageable;
import vn.aeoc.base.constant.ErrorCode;
import vn.aeoc.base.exception.AppException;
import vn.aeoc.base.service.BaseService;
import vn.aeoc.ecom.repository.UserRepository;
import vn.aeoc.entity.data.mysql.User;
import vn.aeoc.rbac.config.AppUserService;

@Service
public class UserService
        extends BaseService<UserRepository, User, Integer>
        implements AppUserService<User> {

    protected UserService(UserRepository repository) {
        super(repository);
    }

    @Override
    public User findById(Integer id) {
        return repository.getById(id);
    }

    public Page<User> getByCriteria(String keyword, String role, Boolean active, Pageable pageable) {
        return pageQuery(pageable,
                () -> repository.getByCriteria(keyword, role, active, pageable),
                () -> repository.countByCriteria(keyword, role, active));
    }

    @Override
    public User findForAuthentication(String email, String phone) {
        if (StringUtils.isNotBlank(email)) {
            return repository.getByEmail(email);
        }
        if (StringUtils.isNotBlank(phone)) {
            return repository.getByPhone(phone);
        }
        throw new AppException(ErrorCode.MISSING_EMAIL_OR_PHONE);
    }

    @Override
    public void register(User user) {
        if (repository.existsByPhone(user.getPhone())) {
            throw new AppException(ErrorCode.DUPLICATED_USER);
        }
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        user.setActive(true);
        repository.register(user);
    }

    @Override
    public User getAllFieldById(Integer id) {
        return repository.getUserAllFieldById(id);
    }

    @Override
    public void updatePassword(Integer userId, String newPassword) {
        repository.updatePassword(userId, newPassword);
    }

}

package vn.aeoc.rbac.config;

import org.springframework.stereotype.Service;

public interface AppUserService<T> {
    T findById(Integer id);

    T findForAuthentication(String email, String phone);

    void register(T user);

    T getAllFieldById(Integer id);

    void updatePassword(Integer userId, String newPassword);
}

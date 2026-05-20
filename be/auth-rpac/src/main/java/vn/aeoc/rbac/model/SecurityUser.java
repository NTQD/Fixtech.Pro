package vn.aeoc.rbac.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SecurityUser<T extends AppUser> {
    private Integer id;
    private String username;
    private String name;
    private String role;

    public SecurityUser(T user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.role = user.getRole();
    }
}

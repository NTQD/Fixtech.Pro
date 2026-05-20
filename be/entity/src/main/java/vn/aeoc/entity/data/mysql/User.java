package vn.aeoc.entity.data.mysql;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import vn.aeoc.rbac.model.AppUser;

import java.sql.Timestamp;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User implements AppUser {
    private Integer id;

    private String name;

    private String phone;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String plainPassword;

    private String email;

    private String role;

    private boolean active;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUsername() {
        return phone != null ? phone : (email != null ? email : "");
    }

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public boolean isActive() {
        return active;
    }
}

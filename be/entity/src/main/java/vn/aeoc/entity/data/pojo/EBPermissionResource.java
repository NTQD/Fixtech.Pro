package vn.aeoc.entity.data.pojo;

import lombok.Data;

@Data
public class EBPermissionResource {
    //Mã tài nguyên
    private String code;

    //Quyền được thực hiện
    private String actions;
}

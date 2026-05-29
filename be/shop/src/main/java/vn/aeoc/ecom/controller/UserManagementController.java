package vn.aeoc.ecom.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.aeoc.base.config.extention.paging.Page;
import vn.aeoc.base.config.extention.paging.Pageable;
import vn.aeoc.base.config.model.ApiResponse;
import vn.aeoc.base.util.Mapper;
import vn.aeoc.ecom.dto.request.UpdateUserRequest;
import vn.aeoc.ecom.service.UserService;
import vn.aeoc.entity.data.mysql.User;
import vn.aeoc.rbac.annotation.IsAdmin;

@RestController
@AllArgsConstructor
@RequestMapping("/admin/users")
public class UserManagementController {
    private final UserService userService;

    @GetMapping
    @IsAdmin
    public ResponseEntity<ApiResponse<Page<User>>> list(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        return Mapper.map(userService.getByCriteria(keyword, pageable), ApiResponse::okEntity);
    }

    @PutMapping("/{id}")
    @IsAdmin
    public ResponseEntity<ApiResponse<User>> update(@PathVariable Integer id, @RequestBody UpdateUserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }
        return Mapper.map(userService.update(id, user), ApiResponse::okEntity);
    }

    @PostMapping("/{id}/active")
    @IsAdmin
    public ResponseEntity<ApiResponse<String>> active(@PathVariable Integer id, @RequestBody UpdateUserRequest request) {
        if (request.getActive() != null) {
            userService.getRepository().setActive(id, request.getActive());
        }
        return Mapper.map("Cập nhật trạng thái thành công", ApiResponse::okEntity);
    }

    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Integer id) {
        userService.delete(id);
        return Mapper.map("Xóa thành công", ApiResponse::okEntity);
    }
}



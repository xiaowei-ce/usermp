package cc.xiaowei.usermp.controller;

import cc.xiaowei.usermp.common.Result;
import cc.xiaowei.usermp.common.UserContext;
import cc.xiaowei.usermp.dto.*;
import cc.xiaowei.usermp.entity.User;
import cc.xiaowei.usermp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员接口：用户管理 CRUD
 */
@RestController
@RequestMapping("/api/v1/admin/user")
public class AdminUserController {

    @Autowired
    private UserService userService;

    /**
     * 分页获取用户列表
     */
    @GetMapping("/page")
    public Result<PageResult<User>> page(@Valid PageRequest req) {
        Long adminId = UserContext.getUserId();
        PageResult<User> result = userService.pageUsers(req.getPage(), req.getSize(), adminId);
        return Result.success("获取用户列表成功", result);
    }

    /**
     * 搜索用户
     */
    @GetMapping("/search")
    public Result<PageResult<User>> search(@Valid SearchRequest req) {
        Long adminId = UserContext.getUserId();
        PageResult<User> result = userService.searchUsers(req, adminId);
        return Result.success("搜索用户成功", result);
    }

    /**
     * 新增用户
     */
    @PostMapping
    public Result<User> create(@Valid @RequestBody CreateUserRequest req) {
        User created = userService.createUser(req);
        return Result.success("用户新增成功", created);
    }

    /**
     * 修改用户信息
     */
    @PutMapping("/{id}")
    public Result<User> update(@PathVariable Long id, @Valid @RequestBody AdminUpdateRequest req) {
        Long adminId = UserContext.getUserId();
        User updated = userService.adminUpdateUser(id, req, adminId);
        return Result.success("用户信息修改成功", updated);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Long adminId = UserContext.getUserId();
        userService.deleteUser(id, adminId);
        return Result.success("用户删除成功", null);
    }

    /**
     * 禁用/启用用户
     */
    @PutMapping("/{id}/disable")
    public Result<User> toggleDisable(@PathVariable Long id, @Valid @RequestBody DisableRequest req) {
        Long adminId = UserContext.getUserId();
        User updated = userService.toggleDisable(id, req.getDisabled(), adminId);
        String msg = Boolean.TRUE.equals(req.getDisabled()) ? "用户已禁用" : "用户已启用";
        return Result.success(msg, updated);
    }
}

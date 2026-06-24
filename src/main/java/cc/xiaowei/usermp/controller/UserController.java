package cc.xiaowei.usermp.controller;

import cc.xiaowei.usermp.common.Result;
import cc.xiaowei.usermp.common.UserContext;
import cc.xiaowei.usermp.dto.UpdateSelfRequest;
import cc.xiaowei.usermp.entity.User;
import cc.xiaowei.usermp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 普通用户接口：获取/修改自己的信息、注销账号
 */
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/me")
    public Result<User> getMe() {
        return Result.success("获取用户信息成功", userService.getCurrentUser());
    }

    /**
     * 修改当前登录用户信息
     */
    @PutMapping("/me")
    public Result<User> updateMe(@Valid @RequestBody UpdateSelfRequest req) {
        Long userId = UserContext.getUserId();
        User updated = userService.updateSelf(userId, req);
        return Result.success("用户信息修改成功", updated);
    }

    /**
     * 用户自注销（删除自己账号）
     */
    @DeleteMapping("/me")
    public Result<Void> deleteMe(HttpServletRequest request) {
        Long userId = UserContext.getUserId();
        String token = (String) request.getAttribute("currentToken");
        userService.deleteSelf(userId, token);
        return Result.success("账号注销成功", null);
    }
}

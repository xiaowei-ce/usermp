package cc.xiaowei.usermp.controller;

import cc.xiaowei.usermp.common.Result;
import cc.xiaowei.usermp.dto.LoginRequest;
import cc.xiaowei.usermp.dto.LoginResponse;
import cc.xiaowei.usermp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证接口：登录、退出
 */
@RestController
@RequestMapping("/api/v1")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录（公开接口）
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        LoginResponse resp = userService.login(req.getName(), req.getPassword());
        return Result.success("登录成功", resp);
    }

    /**
     * 用户退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        userService.logout(token);
        return Result.success("退出登录成功", null);
    }
}

package cc.xiaowei.usermp.dto;

import cc.xiaowei.usermp.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录响应
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private User user;
}

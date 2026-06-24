package cc.xiaowei.usermp.interceptor;

import cc.xiaowei.usermp.common.Result;
import cc.xiaowei.usermp.common.UserContext;
import cc.xiaowei.usermp.entity.User;
import cc.xiaowei.usermp.mapper.UserMapper;
import cc.xiaowei.usermp.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * 认证拦截器
 * <p>拦截 /api/v1/**（排除 /login），校验 JWT → 黑名单检查 → 加载用户 → disabled 检查</p>
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 处理 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 1. 提取 token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeError(response, "请先登录");
            return false;
        }
        String token = authHeader.substring(7);

        // 2. 校验 JWT
        Claims claims = jwtUtil.parseToken(token);
        if (claims == null) {
            writeError(response, "token已过期，请重新登录");
            return false;
        }

        // 3. 检查 Redis 黑名单
        String blacklistKey = "blacklist:" + token;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey))) {
            writeError(response, "token已失效，请重新登录");
            return false;
        }

        // 4. 加载用户
        Long userId = jwtUtil.getUserId(claims);
        User user = userMapper.selectById(userId);
        if (user == null) {
            writeError(response, "用户不存在");
            return false;
        }

        // 5. 检查是否被禁用（DELETE /api/v1/user/me 自注销除外，禁用用户仍可注销）
        if (Boolean.TRUE.equals(user.getDisabled())) {
            String path = request.getRequestURI();
            if (!("DELETE".equalsIgnoreCase(request.getMethod()) && path.startsWith(request.getContextPath() + "/api/v1/user/me"))) {
                writeError(response, "该账号已被禁用，请联系管理员");
                return false;
            }
        }

        // 6. 清除敏感字段，存入上下文
        user.setPassword(null);
        UserContext.set(user);

        // 7. 将 token 存入 request attribute，供 logout 使用
        request.setAttribute("currentToken", token);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.remove();
    }

    private void writeError(HttpServletResponse response, String msg) throws Exception {
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> result = Result.error(msg);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}

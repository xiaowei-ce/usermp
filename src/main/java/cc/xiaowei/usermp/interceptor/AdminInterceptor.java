package cc.xiaowei.usermp.interceptor;

import cc.xiaowei.usermp.common.Result;
import cc.xiaowei.usermp.common.UserContext;
import cc.xiaowei.usermp.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 管理员权限拦截器
 * <p>只拦截 /api/v1/admin/**，校验当前用户是否为管理员（role=1）</p>
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 处理 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        User user = UserContext.get();
        if (user == null || user.getRole() == null || user.getRole() != 1) {
            writeError(response, "权限不足，需要管理员权限");
            return false;
        }

        return true;
    }

    private void writeError(HttpServletResponse response, String msg) throws Exception {
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> result = Result.error(msg);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}

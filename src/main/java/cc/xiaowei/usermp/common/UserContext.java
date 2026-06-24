package cc.xiaowei.usermp.common;

import cc.xiaowei.usermp.entity.User;

/**
 * 请求级用户上下文，通过 ThreadLocal 存储当前请求的用户信息
 */
public class UserContext {
    private static final ThreadLocal<User> CURRENT_USER = new ThreadLocal<>();

    public static void set(User user) {
        CURRENT_USER.set(user);
    }

    public static User get() {
        return CURRENT_USER.get();
    }

    public static Long getUserId() {
        User user = get();
        return user != null ? user.getId() : null;
    }

    public static void remove() {
        CURRENT_USER.remove();
    }
}

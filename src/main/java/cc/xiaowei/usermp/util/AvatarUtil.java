package cc.xiaowei.usermp.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * DiceBear 头像 URL 生成工具
 */
public class AvatarUtil {
    private static final String AVATAR_TEMPLATE =
            "https://api.dicebear.com/10.x/lorelei/svg?seed=%s";

    /**
     * 根据用户名生成头像 URL
     */
    public static String generate(String username) {
        if (username == null || username.isEmpty()) {
            return String.format(AVATAR_TEMPLATE, "default");
        }
        return String.format(AVATAR_TEMPLATE, URLEncoder.encode(username, StandardCharsets.UTF_8));
    }
}

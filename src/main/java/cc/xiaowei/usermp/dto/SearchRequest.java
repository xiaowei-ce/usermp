package cc.xiaowei.usermp.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 搜索用户请求参数
 * <p>至少传入一个搜索条件；多个条件为 AND 关系</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SearchRequest extends PageRequest {
    /** 按用户ID精确匹配 */
    private Long id;
    /** 按用户名模糊搜索 */
    private String name;
    /** 按邮箱模糊搜索 */
    private String email;
    /** 按电话模糊搜索 */
    private String phone;

    /**
     * 判断是否至少有一个搜索条件
     */
    public boolean hasAnyCondition() {
        return id != null
                || (name != null && !name.isEmpty())
                || (email != null && !email.isEmpty())
                || (phone != null && !phone.isEmpty());
    }
}

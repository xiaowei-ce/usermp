package cc.xiaowei.usermp.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理员修改用户信息的请求
 * <p>包含 role 和 disabled 字段</p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminUpdateRequest extends UpdateSelfRequest {
    /** 0=普通用户, 1=管理员 */
    private Integer role;
    /** false=正常, true=禁用 */
    private Boolean disabled;
}

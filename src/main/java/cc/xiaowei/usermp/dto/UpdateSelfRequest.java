package cc.xiaowei.usermp.dto;

import lombok.Data;

/**
 * 普通用户修改自己信息的请求
 * <p>所有字段可选；role/disabled 字段不出现在此 DTO 中，从结构上阻止普通用户修改</p>
 */
@Data
public class UpdateSelfRequest {
    /** 用户名，最长10字符 */
    private String name;
    /** 年龄，>=0 */
    private Integer age;
    /** true=男, false=女, null=保密 */
    private Boolean gender;
    /** 新密码（明文） */
    private String password;
    /** 手机号，最长11，唯一 */
    private String phone;
    /** 邮箱，最长100，唯一 */
    private String email;
}

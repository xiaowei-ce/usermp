package cc.xiaowei.usermp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

/**
 * 管理员新增用户的请求
 * <p>必填字段：name, age, password, phone, email</p>
 */
@Data
public class CreateUserRequest {
    /** 用户名，最长10字符 */
    @NotBlank(message = "name不能为空")
    private String name;

    /** 年龄，>=0 */
    @NotNull(message = "age不能为空")
    @PositiveOrZero(message = "年龄不能为负数")
    private Integer age;

    /** true=男, false=女, null=保密 */
    private Boolean gender;

    /** 明文密码，最长1024字符 */
    @NotBlank(message = "password不能为空")
    private String password;

    /** 0=普通用户, 1=管理员，默认0 */
    private Integer role;

    /** false=正常, true=禁用，默认false */
    private Boolean disabled;

    /** 手机号，最长11，唯一 */
    @NotBlank(message = "phone不能为空")
    private String phone;

    /** 邮箱，最长100，唯一 */
    @NotBlank(message = "email不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
}

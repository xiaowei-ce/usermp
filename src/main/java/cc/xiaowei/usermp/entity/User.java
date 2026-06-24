package cc.xiaowei.usermp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户实体，对应数据库 user 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private Integer age;
    /** true=男, false=女, null=保密 */
    private Boolean gender;
    /** BCrypt 密文，永远不返回给前端 */
    private String password;
    /** 0=普通用户, 1=管理员 */
    private Integer role;
    private String insertTime;
    private String updateTime;
    /** DiceBear 头像 URL，由后端根据 name 自动生成 */
    private String avatar;
    /** false=正常, true=禁用 */
    private Boolean disabled;
    /** 手机号，唯一 */
    private String phone;
    /** 邮箱，唯一 */
    private String email;

    /** 非数据库字段，标识是否为当前登录管理员本人 */
    private transient Boolean isMe;
}

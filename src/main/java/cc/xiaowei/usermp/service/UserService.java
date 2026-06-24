package cc.xiaowei.usermp.service;

import cc.xiaowei.usermp.dto.*;
import cc.xiaowei.usermp.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /** 用户登录 */
    LoginResponse login(String name, String rawPassword);

    /** 退出登录，将 token 加入黑名单 */
    void logout(String token);

    /** 获取当前登录用户信息 */
    User getCurrentUser();

    /** 普通用户修改自己的信息 */
    User updateSelf(Long userId, UpdateSelfRequest req);

    /** 分页获取用户列表（管理员） */
    PageResult<User> pageUsers(int page, int size, Long currentUserId);

    /** 搜索用户（管理员） */
    PageResult<User> searchUsers(SearchRequest req, Long currentUserId);

    /** 管理员修改任意用户信息 */
    User adminUpdateUser(Long targetId, AdminUpdateRequest req, Long adminId);

    /** 管理员删除用户 */
    void deleteUser(Long targetId, Long adminId);

    /** 管理员禁用/启用用户 */
    User toggleDisable(Long targetId, Boolean disabled, Long adminId);

    /** 管理员新增用户 */
    User createUser(CreateUserRequest req);

    /** 用户自注销（物理删除自己账号） */
    void deleteSelf(Long userId, String token);
}

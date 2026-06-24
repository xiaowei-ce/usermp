package cc.xiaowei.usermp.mapper;

import cc.xiaowei.usermp.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户 Mapper 接口
 */
@Mapper
public interface UserMapper {

    /** 按ID查询（含password，内部使用） */
    User selectById(@Param("id") Long id);

    /** 按用户名查询（含password，登录用） */
    User selectByName(@Param("name") String name);

    /** 按手机号查询（含password） */
    User selectByPhone(@Param("phone") String phone);

    /** 按邮箱查询（含password） */
    User selectByEmail(@Param("email") String email);

    /** 分页查询（不含password） */
    List<User> selectPage(@Param("offset") int offset, @Param("size") int size);

    /** 条件搜索（不含password） */
    List<User> selectByCondition(@Param("id") Long id,
                                  @Param("name") String name,
                                  @Param("email") String email,
                                  @Param("phone") String phone,
                                  @Param("offset") int offset,
                                  @Param("size") int size);

    /** 插入用户 */
    int insert(User user);

    /** 动态更新用户 */
    int update(User user);

    /** 按ID删除 */
    int deleteById(@Param("id") Long id);

    /** 统计总数 */
    long countAll();

    /** 条件搜索结果总数 */
    long countByCondition(@Param("id") Long id,
                          @Param("name") String name,
                          @Param("email") String email,
                          @Param("phone") String phone);

    /** 检查手机号唯一性（排除自身） */
    int countByPhoneExcludingId(@Param("phone") String phone, @Param("excludeId") Long excludeId);

    /** 检查邮箱唯一性（排除自身） */
    int countByEmailExcludingId(@Param("email") String email, @Param("excludeId") Long excludeId);
}

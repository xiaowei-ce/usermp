package cc.xiaowei.usermp;

import cc.xiaowei.usermp.mapper.UserMapper;
import cc.xiaowei.usermp.entity.User;
import cc.xiaowei.usermp.util.AvatarUtil;
import cc.xiaowei.usermp.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootTest
class UsermpApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    void contextLoads() {
    }

    @Test
    void insertTest() {
        // 先查一下是否已存在 id=1（自增列，id=0 实际会走自增，这里用 insert 指定值）
        // 如果要用指定的 id，需要让数据库支持显式插入自增主键
        // 这里假设需求是插入一个 admin 管理员用户，id 由数据库自增生成
        User existing = userMapper.selectByName("admin");
        if (existing != null) {
            System.out.println("admin 用户已存在，跳过插入。id=" + existing.getId());
            return;
        }

        String now = LocalDateTime.now().format(DTF);

        User user = new User();
        user.setName("admin");
        user.setAge(25);
        user.setGender(true);
        user.setPassword(passwordEncoder.encode("wei5877261"));
        user.setRole(1);
        user.setDisabled(false);
        user.setPhone("13800000000");
        user.setEmail("admin@example.com");
        user.setAvatar(AvatarUtil.generate("admin"));
        user.setInsertTime(now);
        user.setUpdateTime(now);

        userMapper.insert(user);

        System.out.println("插入成功！admin 用户 id=" + user.getId());
        System.out.println("name=" + user.getName());
        System.out.println("password(密文)=" + user.getPassword());
        System.out.println("role=" + user.getRole());
    }

}

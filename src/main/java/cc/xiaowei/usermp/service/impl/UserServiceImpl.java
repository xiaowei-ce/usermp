package cc.xiaowei.usermp.service.impl;

import cc.xiaowei.usermp.dto.*;
import cc.xiaowei.usermp.entity.User;
import cc.xiaowei.usermp.exception.BusinessException;
import cc.xiaowei.usermp.mapper.UserMapper;
import cc.xiaowei.usermp.service.UserService;
import cc.xiaowei.usermp.util.AvatarUtil;
import cc.xiaowei.usermp.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public LoginResponse login(String name, String rawPassword) {
        User user = userMapper.selectByName(name);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (Boolean.TRUE.equals(user.getDisabled())) {
            throw new BusinessException("该账号已被禁用，请联系管理员");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        user.setPassword(null);
        return new LoginResponse(token, user);
    }

    @Override
    public void logout(String token) {
        Claims claims = jwtUtil.parseTokenLenient(token);
        if (claims != null) {
            long remainingSeconds = jwtUtil.getRemainingSeconds(claims);
            if (remainingSeconds > 0) {
                redisTemplate.opsForValue().set("blacklist:" + token, "1", remainingSeconds, TimeUnit.SECONDS);
            }
        }
    }

    @Override
    public User getCurrentUser() {
        User user = cc.xiaowei.usermp.common.UserContext.get();
        if (user == null) {
            throw new BusinessException("请先登录");
        }
        return user;
    }

    @Override
    public User updateSelf(Long userId, UpdateSelfRequest req) {
        User user = new User();
        user.setId(userId);

        boolean hasUpdate = false;

        if (req.getName() != null) {
            if (req.getName().length() > 10) {
                throw new BusinessException("用户名最长10个字符");
            }
            user.setName(req.getName());
            user.setAvatar(AvatarUtil.generate(req.getName()));
            hasUpdate = true;
        }

        if (req.getAge() != null) {
            if (req.getAge() < 0) {
                throw new BusinessException("年龄不能为负数");
            }
            user.setAge(req.getAge());
            hasUpdate = true;
        }

        if (req.getGender() != null) {
            user.setGender(req.getGender());
            hasUpdate = true;
        }

        if (req.getPassword() != null && !req.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
            hasUpdate = true;
        }

        if (req.getPhone() != null) {
            if (req.getPhone().length() > 11) {
                throw new BusinessException("电话号码最长11个字符");
            }
            int count = userMapper.countByPhoneExcludingId(req.getPhone(), userId);
            if (count > 0) {
                throw new BusinessException("电话号码已被占用");
            }
            user.setPhone(req.getPhone());
            hasUpdate = true;
        }

        if (req.getEmail() != null) {
            if (req.getEmail().length() > 100) {
                throw new BusinessException("邮箱最长100个字符");
            }
            int count = userMapper.countByEmailExcludingId(req.getEmail(), userId);
            if (count > 0) {
                throw new BusinessException("邮箱已被占用");
            }
            user.setEmail(req.getEmail());
            hasUpdate = true;
        }

        if (!hasUpdate) {
            return userMapper.selectById(userId);
        }

        user.setUpdateTime(LocalDateTime.now().format(DTF));
        userMapper.update(user);

        User updated = userMapper.selectById(userId);
        if (updated != null) {
            updated.setPassword(null);
        }
        return updated;
    }

    @Override
    public PageResult<User> pageUsers(int page, int size, Long currentUserId) {
        long total = userMapper.countAll();
        int offset = (page - 1) * size;
        List<User> records = userMapper.selectPage(offset, size);

        for (User u : records) {
            u.setIsMe(u.getId().equals(currentUserId));
        }

        return new PageResult<>(total, page, size, records);
    }

    @Override
    public PageResult<User> searchUsers(SearchRequest req, Long currentUserId) {
        if (!req.hasAnyCondition()) {
            throw new BusinessException("请输入至少一个搜索条件");
        }

        long total = userMapper.countByCondition(req.getId(), req.getName(), req.getEmail(), req.getPhone());
        int offset = req.getOffset();
        int size = req.getSize();
        List<User> records = userMapper.selectByCondition(req.getId(), req.getName(), req.getEmail(), req.getPhone(), offset, size);

        for (User u : records) {
            u.setIsMe(u.getId().equals(currentUserId));
        }

        return new PageResult<>(total, req.getPage(), size, records);
    }

    @Override
    public User adminUpdateUser(Long targetId, AdminUpdateRequest req, Long adminId) {
        if (targetId.equals(adminId)) {
            throw new BusinessException("不能修改自己的账号");
        }

        User target = userMapper.selectById(targetId);
        if (target == null) {
            throw new BusinessException("目标用户不存在");
        }

        User updateUser = new User();
        updateUser.setId(targetId);
        boolean hasUpdate = false;

        if (req.getName() != null) {
            if (req.getName().length() > 10) {
                throw new BusinessException("用户名最长10个字符");
            }
            updateUser.setName(req.getName());
            updateUser.setAvatar(AvatarUtil.generate(req.getName()));
            hasUpdate = true;
        }

        if (req.getAge() != null) {
            if (req.getAge() < 0) {
                throw new BusinessException("年龄不能为负数");
            }
            updateUser.setAge(req.getAge());
            hasUpdate = true;
        }

        if (req.getGender() != null) {
            updateUser.setGender(req.getGender());
            hasUpdate = true;
        }

        if (req.getPassword() != null && !req.getPassword().isEmpty()) {
            updateUser.setPassword(passwordEncoder.encode(req.getPassword()));
            hasUpdate = true;
        }

        if (req.getPhone() != null) {
            if (req.getPhone().length() > 11) {
                throw new BusinessException("电话号码最长11个字符");
            }
            int count = userMapper.countByPhoneExcludingId(req.getPhone(), targetId);
            if (count > 0) {
                throw new BusinessException("电话号码已被占用");
            }
            updateUser.setPhone(req.getPhone());
            hasUpdate = true;
        }

        if (req.getEmail() != null) {
            if (req.getEmail().length() > 100) {
                throw new BusinessException("邮箱最长100个字符");
            }
            int count = userMapper.countByEmailExcludingId(req.getEmail(), targetId);
            if (count > 0) {
                throw new BusinessException("邮箱已被占用");
            }
            updateUser.setEmail(req.getEmail());
            hasUpdate = true;
        }

        if (req.getRole() != null) {
            updateUser.setRole(req.getRole());
            hasUpdate = true;
        }

        if (req.getDisabled() != null) {
            updateUser.setDisabled(req.getDisabled());
            hasUpdate = true;
        }

        if (!hasUpdate) {
            target.setPassword(null);
            return target;
        }

        updateUser.setUpdateTime(LocalDateTime.now().format(DTF));
        userMapper.update(updateUser);

        User updated = userMapper.selectById(targetId);
        if (updated != null) {
            updated.setPassword(null);
        }
        return updated;
    }

    @Override
    public void deleteUser(Long targetId, Long adminId) {
        if (targetId.equals(adminId)) {
            throw new BusinessException("不能删除自己的账号");
        }

        User target = userMapper.selectById(targetId);
        if (target == null) {
            throw new BusinessException("目标用户不存在");
        }

        userMapper.deleteById(targetId);
    }

    @Override
    public User toggleDisable(Long targetId, Boolean disabled, Long adminId) {
        if (targetId.equals(adminId)) {
            throw new BusinessException("不能禁用/启用自己的账号");
        }

        User target = userMapper.selectById(targetId);
        if (target == null) {
            throw new BusinessException("目标用户不存在");
        }

        User updateUser = new User();
        updateUser.setId(targetId);
        updateUser.setDisabled(disabled);
        updateUser.setUpdateTime(LocalDateTime.now().format(DTF));
        userMapper.update(updateUser);

        User updated = userMapper.selectById(targetId);
        if (updated != null) {
            updated.setPassword(null);
        }
        return updated;
    }

    @Override
    public User createUser(CreateUserRequest req) {
        // 校验用户名长度
        if (req.getName().length() > 10) {
            throw new BusinessException("用户名最长10个字符");
        }

        // 校验年龄
        if (req.getAge() < 0) {
            throw new BusinessException("年龄不能为负数");
        }

        // 校验手机号长度
        if (req.getPhone().length() > 11) {
            throw new BusinessException("电话号码最长11个字符");
        }

        // 校验邮箱长度
        if (req.getEmail().length() > 100) {
            throw new BusinessException("邮箱最长100个字符");
        }

        // 唯一性校验
        if (userMapper.selectByPhone(req.getPhone()) != null) {
            throw new BusinessException("电话号码已被占用");
        }
        if (userMapper.selectByEmail(req.getEmail()) != null) {
            throw new BusinessException("邮箱已被占用");
        }

        String now = LocalDateTime.now().format(DTF);

        User user = new User();
        user.setName(req.getName());
        user.setAge(req.getAge());
        user.setGender(req.getGender());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(req.getRole() != null ? req.getRole() : 0);
        user.setDisabled(req.getDisabled() != null ? req.getDisabled() : false);
        user.setPhone(req.getPhone());
        user.setEmail(req.getEmail());
        user.setAvatar(AvatarUtil.generate(req.getName()));
        user.setInsertTime(now);
        user.setUpdateTime(now);

        userMapper.insert(user);

        // insert 后 id 已回填到 user 对象
        user.setPassword(null);
        return user;
    }
}

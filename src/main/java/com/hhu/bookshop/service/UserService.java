package com.hhu.bookshop.service;

import com.hhu.bookshop.dao.UserDao;
import com.hhu.bookshop.entity.User;
import com.hhu.bookshop.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 用户业务逻辑层
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户注册
     *
     * @param user 用户对象
     * @return 注册成功返回用户对象，失败返回null
     */
    public User register(User user) {
        // 检查用户名是否已存在
        if (userDao.existsByUsername(user.getUsername())) {
            return null;
        }
        // BCrypt加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userDao.save(user);
    }

    /**
     * 用户登录验证
     *
     * @param username 用户名
     * @param password 明文密码
     * @return 验证通过返回用户对象，失败返回null
     */
    public User login(String username, String password) {
        Optional<User> optUser = userDao.findByUsername(username);
        if (optUser.isPresent()) {
            User user = optUser.get();
            // BCrypt校验密码
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    /**
     * 生成JWT Token
     */
    public String generateToken(User user) {
        return jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole());
    }

    /**
     * 根据用户名查询用户
     */
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    /**
     * 重置密码（忘记密码功能）
     * 通过账号+昵称验证身份，验证通过后将密码重置为 123456
     */
    public boolean resetPassword(String username, String nickname) {
        Optional<User> optUser = userDao.findByUsername(username);
        if (optUser.isPresent()) {
            User user = optUser.get();
            // 校验昵称是否匹配
            if (nickname.equals(user.getNickname())) {
                user.setPassword(passwordEncoder.encode("123456"));
                userDao.save(user);
                return true;
            }
        }
        return false;
    }
}

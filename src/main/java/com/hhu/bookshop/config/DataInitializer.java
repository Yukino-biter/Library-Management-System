package com.hhu.bookshop.config;

import com.hhu.bookshop.dao.UserDao;
import com.hhu.bookshop.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据初始化器 - 启动时校验并修正测试用户密码
 * 确保预置用户的密码为 123456 的BCrypt加密值
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserDao userDao;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) {
        // 检查预置用户密码是否正确，不正确则修正
        String[] testUsers = {"admin", "zhangsan", "lisi"};
        for (String username : testUsers) {
            userDao.findByUsername(username).ifPresent(user -> {
                if (!passwordEncoder.matches("123456", user.getPassword())) {
                    user.setPassword(passwordEncoder.encode("123456"));
                    userDao.save(user);
                    System.out.println("[DataInit] 已修正用户 " + username + " 的密码为 123456");
                }
            });
        }

        // 如果admin用户不存在，创建默认管理员
        if (userDao.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setNickname("管理员");
            admin.setRole("admin");
            userDao.save(admin);
            System.out.println("[DataInit] 已创建默认管理员账号 admin/123456");
        }
    }
}

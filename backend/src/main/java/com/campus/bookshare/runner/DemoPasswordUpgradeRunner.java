package com.campus.bookshare.runner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.bookshare.entity.User;
import com.campus.bookshare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DemoPasswordUpgradeRunner implements ApplicationRunner {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        List<User> users = userService.list(new LambdaQueryWrapper<User>().select(User::getId, User::getPassword));
        for (User user : users) {
            if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode("123456"));
                userService.updateById(user);
            }
        }
    }
}

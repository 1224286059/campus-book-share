package com.campus.bookshare.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.campus.bookshare.common.BusinessException;
import com.campus.bookshare.context.UserContext;
import com.campus.bookshare.dto.LoginDTO;
import com.campus.bookshare.dto.RegisterDTO;
import com.campus.bookshare.entity.User;
import com.campus.bookshare.enums.UserRoleEnum;
import com.campus.bookshare.service.AuthService;
import com.campus.bookshare.service.UserService;
import com.campus.bookshare.utils.BeanCopyUtils;
import com.campus.bookshare.utils.JwtUtils;
import com.campus.bookshare.vo.LoginVO;
import com.campus.bookshare.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void register(RegisterDTO dto) {
        User exist = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, dto.getUsername()));
        if (exist != null) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setCollege(dto.getCollege());
        user.setMajor(dto.getMajor());
        user.setGrade(dto.getGrade());
        user.setPoints(0);
        user.setCreditScore(100);
        user.setRole(UserRoleEnum.USER.name());
        user.setStatus(1);
        userService.save(user);
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = userService.getByUsername(dto.getUsername());
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus().intValue() != 1) {
            throw new BusinessException("账号已被禁用");
        }
        LoginVO vo = new LoginVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRole(user.getRole());
        vo.setToken(jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole()));
        return vo;
    }

    @Override
    public UserVO me() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("当前未登录");
        }
        User user = userService.getById(userId);
        return BeanCopyUtils.copy(user, UserVO.class);
    }
}

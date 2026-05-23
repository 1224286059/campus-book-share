package com.campus.bookshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.bookshare.common.BusinessException;
import com.campus.bookshare.context.UserContext;
import com.campus.bookshare.dto.UpdatePasswordDTO;
import com.campus.bookshare.dto.UpdateProfileDTO;
import com.campus.bookshare.entity.PointsRecord;
import com.campus.bookshare.entity.User;
import com.campus.bookshare.mapper.UserMapper;
import com.campus.bookshare.service.PointsRecordService;
import com.campus.bookshare.service.UserService;
import com.campus.bookshare.utils.BeanCopyUtils;
import com.campus.bookshare.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PointsRecordService pointsRecordService;

    @Override
    public User getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    @Override
    public UserVO getCurrentUserInfo() {
        User user = getById(UserContext.getCurrentUserId());
        return BeanCopyUtils.copy(user, UserVO.class);
    }

    @Override
    public UserVO updateProfile(UpdateProfileDTO dto) {
        User user = getById(UserContext.getCurrentUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setCollege(dto.getCollege());
        user.setMajor(dto.getMajor());
        user.setGrade(dto.getGrade());
        updateById(user);
        return BeanCopyUtils.copy(user, UserVO.class);
    }

    @Override
    public void updatePassword(UpdatePasswordDTO dto) {
        User user = getById(UserContext.getCurrentUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        updateById(user);
    }

    @Override
    public void changePoints(Long userId, Integer pointsChange, String sourceType, String description) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        int current = user.getPoints() == null ? 0 : user.getPoints().intValue();
        user.setPoints(current + pointsChange);
        updateById(user);

        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setPointsChange(pointsChange);
        record.setSourceType(sourceType);
        record.setDescription(description);
        record.setCreateTime(new Date());
        pointsRecordService.save(record);
    }

    @Override
    public List<UserVO> adminList(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>();
        if (StringUtils.hasText(username)) {
            wrapper.like(User::getUsername, username);
        }
        wrapper.orderByDesc(User::getCreateTime);
        return BeanCopyUtils.copyList(list(wrapper), UserVO.class);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        Long currentUserId = UserContext.getCurrentUserId();
        if (currentUserId != null && currentUserId.equals(id)) {
            throw new BusinessException("不能禁用或恢复当前登录管理员");
        }
        user.setStatus(status);
        updateById(user);
    }
}

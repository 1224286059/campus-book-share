package com.campus.bookshare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.bookshare.dto.UpdatePasswordDTO;
import com.campus.bookshare.dto.UpdateProfileDTO;
import com.campus.bookshare.entity.User;
import com.campus.bookshare.vo.UserVO;

import java.util.List;

public interface UserService extends IService<User> {

    User getByUsername(String username);

    UserVO getCurrentUserInfo();

    UserVO updateProfile(UpdateProfileDTO dto);

    void updatePassword(UpdatePasswordDTO dto);

    void changePoints(Long userId, Integer pointsChange, String sourceType, String description);

    List<UserVO> adminList(String username);

    void updateStatus(Long id, Integer status);
}

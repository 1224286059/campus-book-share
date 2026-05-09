package com.campus.bookshare.service;

import com.campus.bookshare.dto.LoginDTO;
import com.campus.bookshare.dto.RegisterDTO;
import com.campus.bookshare.vo.LoginVO;
import com.campus.bookshare.vo.UserVO;

public interface AuthService {

    void register(RegisterDTO dto);

    LoginVO login(LoginDTO dto);

    UserVO me();
}

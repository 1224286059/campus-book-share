package com.campus.bookshare.context;

import com.campus.bookshare.vo.LoginUserVO;

public class UserContext {

    private static final ThreadLocal<LoginUserVO> USER_HOLDER = new ThreadLocal<LoginUserVO>();

    private UserContext() {
    }

    public static void setCurrentUser(LoginUserVO user) {
        USER_HOLDER.set(user);
    }

    public static LoginUserVO getCurrentUser() {
        return USER_HOLDER.get();
    }

    public static Long getCurrentUserId() {
        LoginUserVO user = USER_HOLDER.get();
        return user == null ? null : user.getId();
    }

    public static void clear() {
        USER_HOLDER.remove();
    }
}

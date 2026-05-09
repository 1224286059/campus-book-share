package com.campus.bookshare.interceptor;

import com.campus.bookshare.context.UserContext;
import com.campus.bookshare.enums.UserRoleEnum;
import com.campus.bookshare.exception.ForbiddenException;
import com.campus.bookshare.exception.UnauthorizedException;
import com.campus.bookshare.utils.JwtUtils;
import com.campus.bookshare.vo.LoginUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI();
        if (isPublicPath(path, request.getMethod())) {
            return true;
        }
        LoginUserVO loginUser = parseUser(request);
        checkPermission(path, request.getMethod(), loginUser);
        UserContext.setCurrentUser(loginUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private LoginUserVO parseUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.trim().length() == 0) {
            throw new UnauthorizedException("请先登录");
        }
        return jwtUtils.parseToken(token);
    }

    private void checkPermission(String path, String method, LoginUserVO loginUser) {
        if (path.startsWith("/api/admin/")) {
            if (!UserRoleEnum.ADMIN.name().equals(loginUser.getRole())) {
                throw new ForbiddenException("无管理员权限");
            }
            return;
        }
        if ((path.startsWith("/api/books") && !"GET".equalsIgnoreCase(method))
                || path.startsWith("/api/orders")
                || path.startsWith("/api/borrows")
                || path.startsWith("/api/points")
                || path.startsWith("/api/evaluations")
                || path.startsWith("/api/reports")) {
            if (!UserRoleEnum.USER.name().equals(loginUser.getRole())) {
                throw new ForbiddenException("只有普通用户可以访问该接口");
            }
        }
    }

    private boolean isPublicPath(String path, String method) {
        if ("/api/auth/login".equals(path) || "/api/auth/register".equals(path)) {
            return true;
        }
        if ("GET".equalsIgnoreCase(method)
                && ("/api/books".equals(path) || path.matches("^/api/books/\\d+$"))) {
            return true;
        }
        if ("/api/categories".equals(path) && "GET".equalsIgnoreCase(method)) {
            return true;
        }
        return "/api/health".equals(path);
    }
}

package com.campus.bookshare.controller;

import com.campus.bookshare.common.Result;
import com.campus.bookshare.service.UserService;
import com.campus.bookshare.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Result<List<UserVO>> list(@RequestParam(required = false) String username) {
        return Result.success(userService.adminList(username));
    }

    @PutMapping("/{id}/disable")
    public Result<Void> disable(@PathVariable Long id) {
        userService.updateStatus(id, 0);
        return Result.success("绂佺敤鎴愬姛", null);
    }

    @PutMapping("/{id}/enable")
    public Result<Void> enable(@PathVariable Long id) {
        userService.updateStatus(id, 1);
        return Result.success("鎭㈠鎴愬姛", null);
    }
}

package com.campus.bookshare.controller;

import com.campus.bookshare.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("project", "campus-book-share");
        map.put("status", "ok");
        return Result.success(map);
    }
}

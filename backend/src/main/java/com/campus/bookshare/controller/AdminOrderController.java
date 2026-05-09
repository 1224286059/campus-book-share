package com.campus.bookshare.controller;

import com.campus.bookshare.common.Result;
import com.campus.bookshare.service.BookOrderService;
import com.campus.bookshare.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    @Autowired
    private BookOrderService orderService;

    @GetMapping
    public Result<List<OrderVO>> list(@RequestParam(required = false) String orderType,
                                      @RequestParam(required = false) String status) {
        return Result.success(orderService.adminList(orderType, status));
    }
}

package com.campus.bookshare.controller;

import com.campus.bookshare.common.Result;
import com.campus.bookshare.dto.BookQueryDTO;
import com.campus.bookshare.service.BookService;
import com.campus.bookshare.vo.BookVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/books")
public class AdminBookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/pending")
    public Result<List<BookVO>> pending() {
        return Result.success(bookService.listPending());
    }

    @GetMapping
    public Result<List<BookVO>> list(BookQueryDTO dto, String status) {
        return Result.success(bookService.adminList(dto, status));
    }

    @PutMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id) {
        bookService.approve(id);
        return Result.success("审核通过", null);
    }

    @PutMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id) {
        bookService.reject(id);
        return Result.success("审核驳回", null);
    }

    @PutMapping("/{id}/off-shelf")
    public Result<Void> offShelf(@PathVariable Long id) {
        bookService.adminOffShelf(id);
        return Result.success("下架成功", null);
    }
}

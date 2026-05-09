package com.campus.bookshare.controller;

import com.campus.bookshare.common.Result;
import com.campus.bookshare.service.BorrowRecordService;
import com.campus.bookshare.vo.BorrowRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/borrows")
public class BorrowController {

    @Autowired
    private BorrowRecordService borrowRecordService;

    @PutMapping("/{id}/return")
    public Result<Void> returnBook(@PathVariable Long id) {
        borrowRecordService.returnBook(id);
        return Result.success("归还成功", null);
    }

    @GetMapping("/my")
    public Result<List<BorrowRecordVO>> myBorrows() {
        return Result.success(borrowRecordService.myRecords());
    }
}

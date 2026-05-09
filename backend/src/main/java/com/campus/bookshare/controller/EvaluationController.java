package com.campus.bookshare.controller;

import com.campus.bookshare.common.Result;
import com.campus.bookshare.dto.EvaluationCreateDTO;
import com.campus.bookshare.service.EvaluationService;
import com.campus.bookshare.vo.EvaluationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    @PostMapping("/api/evaluations")
    public Result<EvaluationVO> create(@Valid @RequestBody EvaluationCreateDTO dto) {
        return Result.success(evaluationService.create(dto));
    }

    @GetMapping("/api/evaluations/book/{bookId}")
    public Result<List<EvaluationVO>> listByBook(@PathVariable Long bookId) {
        return Result.success(evaluationService.listByBookId(bookId));
    }

    @GetMapping("/api/evaluations/user/{userId}")
    public Result<List<EvaluationVO>> listByUser(@PathVariable Long userId) {
        return Result.success(evaluationService.listByUserId(userId));
    }

    @DeleteMapping("/api/admin/evaluations/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        evaluationService.deleteByAdmin(id);
        return Result.success("删除成功", null);
    }
}

package com.campus.bookshare.controller;

import com.campus.bookshare.common.Result;
import com.campus.bookshare.service.EvaluationService;
import com.campus.bookshare.vo.EvaluationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/evaluations")
public class AdminEvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    @GetMapping
    public Result<List<EvaluationVO>> list() {
        return Result.success(evaluationService.listAll());
    }
}

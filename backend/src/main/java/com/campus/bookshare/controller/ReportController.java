package com.campus.bookshare.controller;

import com.campus.bookshare.common.Result;
import com.campus.bookshare.dto.ReportCreateDTO;
import com.campus.bookshare.dto.ReportProcessDTO;
import com.campus.bookshare.service.ReportService;
import com.campus.bookshare.vo.ReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping("/api/reports")
    public Result<ReportVO> create(@Valid @RequestBody ReportCreateDTO dto) {
        return Result.success(reportService.create(dto));
    }

    @GetMapping("/api/admin/reports")
    public Result<List<ReportVO>> list() {
        return Result.success(reportService.listAll());
    }

    @PutMapping("/api/admin/reports/{id}/process")
    public Result<Void> process(@PathVariable Long id, @RequestBody(required = false) ReportProcessDTO dto) {
        reportService.process(id, dto == null ? new ReportProcessDTO() : dto);
        return Result.success("处理成功", null);
    }
}

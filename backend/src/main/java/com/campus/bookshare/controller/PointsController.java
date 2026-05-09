package com.campus.bookshare.controller;

import com.campus.bookshare.common.Result;
import com.campus.bookshare.entity.PointsRecord;
import com.campus.bookshare.service.PointsRecordService;
import com.campus.bookshare.vo.PointsSummaryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/points")
public class PointsController {

    @Autowired
    private PointsRecordService pointsRecordService;

    @GetMapping("/my")
    public Result<PointsSummaryVO> myPoints() {
        return Result.success(pointsRecordService.myPoints());
    }

    @GetMapping("/my-records")
    public Result<List<PointsRecord>> myRecords() {
        return Result.success(pointsRecordService.myRecords());
    }
}

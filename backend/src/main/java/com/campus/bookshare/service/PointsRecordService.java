package com.campus.bookshare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.bookshare.entity.PointsRecord;
import com.campus.bookshare.vo.PointsSummaryVO;

import java.util.List;

public interface PointsRecordService extends IService<PointsRecord> {

    PointsSummaryVO myPoints();

    List<PointsRecord> myRecords();
}

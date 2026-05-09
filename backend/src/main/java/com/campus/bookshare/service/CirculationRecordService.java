package com.campus.bookshare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.bookshare.entity.CirculationRecord;
import com.campus.bookshare.enums.CirculationTypeEnum;

public interface CirculationRecordService extends IService<CirculationRecord> {

    void addRecord(Long bookId, Long fromUserId, Long toUserId, CirculationTypeEnum type, Long orderId, String remark);
}

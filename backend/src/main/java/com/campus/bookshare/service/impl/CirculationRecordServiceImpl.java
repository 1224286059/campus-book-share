package com.campus.bookshare.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.bookshare.entity.CirculationRecord;
import com.campus.bookshare.enums.CirculationTypeEnum;
import com.campus.bookshare.mapper.CirculationRecordMapper;
import com.campus.bookshare.service.CirculationRecordService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CirculationRecordServiceImpl extends ServiceImpl<CirculationRecordMapper, CirculationRecord> implements CirculationRecordService {

    @Override
    public void addRecord(Long bookId, Long fromUserId, Long toUserId, CirculationTypeEnum type, Long orderId, String remark) {
        CirculationRecord record = new CirculationRecord();
        record.setBookId(bookId);
        record.setFromUserId(fromUserId);
        record.setToUserId(toUserId);
        record.setCirculationType(type.name());
        record.setOrderId(orderId);
        record.setRemark(remark);
        record.setCreateTime(new Date());
        save(record);
    }
}

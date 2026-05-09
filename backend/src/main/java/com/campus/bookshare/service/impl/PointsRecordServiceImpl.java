package com.campus.bookshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.bookshare.context.UserContext;
import com.campus.bookshare.entity.PointsRecord;
import com.campus.bookshare.entity.User;
import com.campus.bookshare.mapper.PointsRecordMapper;
import com.campus.bookshare.mapper.UserMapper;
import com.campus.bookshare.service.PointsRecordService;
import com.campus.bookshare.vo.PointsSummaryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointsRecordServiceImpl extends ServiceImpl<PointsRecordMapper, PointsRecord> implements PointsRecordService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public PointsSummaryVO myPoints() {
        User user = userMapper.selectById(UserContext.getCurrentUserId());
        PointsSummaryVO vo = new PointsSummaryVO();
        vo.setUserId(user.getId());
        vo.setPoints(user.getPoints());
        return vo;
    }

    @Override
    public List<PointsRecord> myRecords() {
        return list(new LambdaQueryWrapper<PointsRecord>()
                .eq(PointsRecord::getUserId, UserContext.getCurrentUserId())
                .orderByDesc(PointsRecord::getCreateTime));
    }
}

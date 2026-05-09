package com.campus.bookshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.bookshare.common.BusinessException;
import com.campus.bookshare.context.UserContext;
import com.campus.bookshare.dto.ReportCreateDTO;
import com.campus.bookshare.dto.ReportProcessDTO;
import com.campus.bookshare.entity.Report;
import com.campus.bookshare.entity.User;
import com.campus.bookshare.enums.ReportStatusEnum;
import com.campus.bookshare.enums.ReportTargetTypeEnum;
import com.campus.bookshare.mapper.ReportMapper;
import com.campus.bookshare.service.ReportService;
import com.campus.bookshare.service.UserService;
import com.campus.bookshare.vo.ReportVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements ReportService {

    @Autowired
    private UserService userService;

    @Override
    public ReportVO create(ReportCreateDTO dto) {
        try {
            ReportTargetTypeEnum.valueOf(dto.getTargetType());
        } catch (Exception e) {
            throw new BusinessException("举报对象类型不合法");
        }
        Report report = new Report();
        report.setReporterId(UserContext.getCurrentUserId());
        report.setTargetType(dto.getTargetType());
        report.setTargetId(dto.getTargetId());
        report.setReason(dto.getReason());
        report.setStatus(ReportStatusEnum.PENDING.name());
        report.setCreateTime(new Date());
        save(report);
        return toVO(report);
    }

    @Override
    public List<ReportVO> listAll() {
        return convert(list(new LambdaQueryWrapper<Report>().orderByDesc(Report::getCreateTime)));
    }

    @Override
    public void process(Long id, ReportProcessDTO dto) {
        Report report = getById(id);
        if (report == null) {
            throw new BusinessException("举报不存在");
        }
        report.setStatus(ReportStatusEnum.PROCESSED.name());
        report.setHandleTime(new Date());
        updateById(report);
    }

    private List<ReportVO> convert(List<Report> reports) {
        List<ReportVO> list = new ArrayList<ReportVO>();
        for (Report report : reports) {
            list.add(toVO(report));
        }
        return list;
    }

    private ReportVO toVO(Report report) {
        ReportVO vo = new ReportVO();
        BeanUtils.copyProperties(report, vo);
        User reporter = userService.getById(report.getReporterId());
        vo.setReporterUsername(reporter == null ? null : reporter.getUsername());
        return vo;
    }
}

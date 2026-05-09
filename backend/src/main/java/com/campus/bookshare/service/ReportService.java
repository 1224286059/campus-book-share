package com.campus.bookshare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.bookshare.dto.ReportCreateDTO;
import com.campus.bookshare.dto.ReportProcessDTO;
import com.campus.bookshare.entity.Report;
import com.campus.bookshare.vo.ReportVO;

import java.util.List;

public interface ReportService extends IService<Report> {

    ReportVO create(ReportCreateDTO dto);

    List<ReportVO> listAll();

    void process(Long id, ReportProcessDTO dto);
}

package com.campus.bookshare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.bookshare.dto.EvaluationCreateDTO;
import com.campus.bookshare.entity.Evaluation;
import com.campus.bookshare.vo.EvaluationVO;

import java.util.List;

public interface EvaluationService extends IService<Evaluation> {

    EvaluationVO create(EvaluationCreateDTO dto);

    List<EvaluationVO> listByBookId(Long bookId);

    List<EvaluationVO> listByUserId(Long userId);

    List<EvaluationVO> listAll();

    void deleteByAdmin(Long id);
}

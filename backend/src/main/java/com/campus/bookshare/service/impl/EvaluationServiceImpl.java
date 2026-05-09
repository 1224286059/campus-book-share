package com.campus.bookshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.bookshare.common.BusinessException;
import com.campus.bookshare.context.UserContext;
import com.campus.bookshare.dto.EvaluationCreateDTO;
import com.campus.bookshare.entity.Book;
import com.campus.bookshare.entity.BookOrder;
import com.campus.bookshare.entity.Evaluation;
import com.campus.bookshare.entity.User;
import com.campus.bookshare.enums.OrderStatusEnum;
import com.campus.bookshare.mapper.EvaluationMapper;
import com.campus.bookshare.service.BookOrderService;
import com.campus.bookshare.service.BookService;
import com.campus.bookshare.service.EvaluationService;
import com.campus.bookshare.service.UserService;
import com.campus.bookshare.vo.EvaluationVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EvaluationServiceImpl extends ServiceImpl<EvaluationMapper, Evaluation> implements EvaluationService {

    @Autowired
    private BookOrderService orderService;
    @Autowired
    private BookService bookService;
    @Autowired
    private UserService userService;

    @Override
    public EvaluationVO create(EvaluationCreateDTO dto) {
        BookOrder order = orderService.getById(dto.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        Long currentUserId = UserContext.getCurrentUserId();
        if (!currentUserId.equals(order.getOwnerId()) && !currentUserId.equals(order.getApplicantId())) {
            throw new BusinessException("只有订单相关用户可以评价");
        }
        if (!OrderStatusEnum.COMPLETED.name().equals(order.getStatus())) {
            throw new BusinessException("只有订单完成后可以评价");
        }
        if (!dto.getTargetUserId().equals(order.getOwnerId()) && !dto.getTargetUserId().equals(order.getApplicantId())) {
            throw new BusinessException("评价对象不合法");
        }
        if (dto.getTargetUserId().equals(currentUserId)) {
            throw new BusinessException("不能评价自己");
        }
        if (count(new LambdaQueryWrapper<Evaluation>()
                .eq(Evaluation::getOrderId, dto.getOrderId())
                .eq(Evaluation::getEvaluatorId, currentUserId)
                .eq(Evaluation::getTargetUserId, dto.getTargetUserId())) > 0) {
            throw new BusinessException("同一订单不能重复评价同一对象");
        }
        Evaluation evaluation = new Evaluation();
        evaluation.setOrderId(dto.getOrderId());
        evaluation.setEvaluatorId(currentUserId);
        evaluation.setTargetUserId(dto.getTargetUserId());
        evaluation.setBookScore(dto.getBookScore());
        evaluation.setUserScore(dto.getUserScore());
        evaluation.setContent(dto.getContent());
        evaluation.setCreateTime(new Date());
        save(evaluation);
        return toVO(evaluation);
    }

    @Override
    public List<EvaluationVO> listByBookId(Long bookId) {
        Book book = bookService.getById(bookId);
        if (book == null) {
            throw new BusinessException("书籍不存在");
        }
        List<BookOrder> orders = orderService.list(new LambdaQueryWrapper<BookOrder>().eq(BookOrder::getBookId, bookId));
        List<Long> orderIds = new ArrayList<Long>();
        for (BookOrder order : orders) {
            orderIds.add(order.getId());
        }
        if (orderIds.isEmpty()) {
            return new ArrayList<EvaluationVO>();
        }
        return convert(list(new LambdaQueryWrapper<Evaluation>().in(Evaluation::getOrderId, orderIds).orderByDesc(Evaluation::getCreateTime)));
    }

    @Override
    public List<EvaluationVO> listByUserId(Long userId) {
        return convert(list(new LambdaQueryWrapper<Evaluation>()
                .eq(Evaluation::getTargetUserId, userId)
                .orderByDesc(Evaluation::getCreateTime)));
    }

    @Override
    public List<EvaluationVO> listAll() {
        return convert(list(new LambdaQueryWrapper<Evaluation>().orderByDesc(Evaluation::getCreateTime)));
    }

    @Override
    public void deleteByAdmin(Long id) {
        removeById(id);
    }

    private List<EvaluationVO> convert(List<Evaluation> evaluations) {
        List<EvaluationVO> list = new ArrayList<EvaluationVO>();
        for (Evaluation evaluation : evaluations) {
            list.add(toVO(evaluation));
        }
        return list;
    }

    private EvaluationVO toVO(Evaluation evaluation) {
        EvaluationVO vo = new EvaluationVO();
        BeanUtils.copyProperties(evaluation, vo);
        User evaluator = userService.getById(evaluation.getEvaluatorId());
        User target = userService.getById(evaluation.getTargetUserId());
        vo.setEvaluatorUsername(evaluator == null ? null : evaluator.getUsername());
        vo.setTargetUsername(target == null ? null : target.getUsername());
        return vo;
    }
}

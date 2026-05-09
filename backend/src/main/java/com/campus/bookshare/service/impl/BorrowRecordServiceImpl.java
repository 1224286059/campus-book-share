package com.campus.bookshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.bookshare.common.BusinessException;
import com.campus.bookshare.context.UserContext;
import com.campus.bookshare.entity.Book;
import com.campus.bookshare.entity.BookOrder;
import com.campus.bookshare.entity.BorrowRecord;
import com.campus.bookshare.entity.User;
import com.campus.bookshare.enums.BookStatusEnum;
import com.campus.bookshare.enums.BorrowStatusEnum;
import com.campus.bookshare.enums.CirculationTypeEnum;
import com.campus.bookshare.enums.OrderStatusEnum;
import com.campus.bookshare.enums.PointsSourceTypeEnum;
import com.campus.bookshare.mapper.BookOrderMapper;
import com.campus.bookshare.mapper.BorrowRecordMapper;
import com.campus.bookshare.service.BookService;
import com.campus.bookshare.service.BorrowRecordService;
import com.campus.bookshare.service.CirculationRecordService;
import com.campus.bookshare.service.UserService;
import com.campus.bookshare.utils.DateUtils;
import com.campus.bookshare.vo.BorrowRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BorrowRecordServiceImpl extends ServiceImpl<BorrowRecordMapper, BorrowRecord> implements BorrowRecordService {

    @Autowired
    private BookOrderMapper bookOrderMapper;
    @Autowired
    private BookService bookService;
    @Autowired
    private UserService userService;
    @Autowired
    private CirculationRecordService circulationRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createBorrowRecord(Long orderId) {
        BookOrder order = bookOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (count(new LambdaQueryWrapper<BorrowRecord>().eq(BorrowRecord::getOrderId, orderId)) > 0) {
            return;
        }
        BorrowRecord record = new BorrowRecord();
        record.setOrderId(orderId);
        record.setBookId(order.getBookId());
        record.setLenderId(order.getOwnerId());
        record.setBorrowerId(order.getApplicantId());
        record.setBorrowTime(new Date());
        record.setExpectedReturnTime(order.getFinishTime());
        record.setStatus(BorrowStatusEnum.BORROWING.name());
        save(record);

        Book book = bookService.getById(order.getBookId());
        book.setStatus(BookStatusEnum.SHARING.name());
        bookService.updateById(book);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnBook(Long id) {
        BorrowRecord record = getById(id);
        if (record == null) {
            throw new BusinessException("借阅记录不存在");
        }
        if (!UserContext.getCurrentUserId().equals(record.getBorrowerId())) {
            throw new BusinessException("只有借阅人可以归还");
        }
        if (!BorrowStatusEnum.BORROWING.name().equals(record.getStatus())) {
            throw new BusinessException("当前借阅记录不可归还");
        }
        Date now = new Date();
        record.setActualReturnTime(now);
        boolean overdue = DateUtils.isOverdue(record.getExpectedReturnTime(), now);
        record.setStatus(overdue ? BorrowStatusEnum.OVERDUE.name() : BorrowStatusEnum.RETURNED.name());
        updateById(record);

        Book book = bookService.getById(record.getBookId());
        book.setStatus(BookStatusEnum.ON_SHELF.name());
        bookService.updateById(book);

        if (overdue) {
            userService.changePoints(record.getBorrowerId(), -5, PointsSourceTypeEnum.BORROW_RETURN_OVERDUE.name(), "逾期归还书籍，积分-5");
        } else {
            userService.changePoints(record.getBorrowerId(), 3, PointsSourceTypeEnum.BORROW_RETURN_ON_TIME.name(), "按时归还书籍，积分+3");
        }

        BookOrder order = bookOrderMapper.selectById(record.getOrderId());
        if (order != null) {
            order.setStatus(OrderStatusEnum.COMPLETED.name());
            order.setFinishTime(now);
            bookOrderMapper.updateById(order);
        }

        circulationRecordService.addRecord(record.getBookId(), record.getLenderId(), record.getBorrowerId(),
                CirculationTypeEnum.BORROW, record.getOrderId(), overdue ? "借阅后逾期归还" : "借阅后按时归还");
    }

    @Override
    public List<BorrowRecordVO> myRecords() {
        Long currentUserId = UserContext.getCurrentUserId();
        List<BorrowRecord> records = list(new LambdaQueryWrapper<BorrowRecord>()
                .and(wrapper -> wrapper.eq(BorrowRecord::getBorrowerId, currentUserId)
                        .or().eq(BorrowRecord::getLenderId, currentUserId))
                .orderByDesc(BorrowRecord::getBorrowTime));
        List<BorrowRecordVO> list = new ArrayList<BorrowRecordVO>();
        for (BorrowRecord record : records) {
            list.add(toVO(record));
        }
        return list;
    }

    private BorrowRecordVO toVO(BorrowRecord record) {
        BorrowRecordVO vo = new BorrowRecordVO();
        BeanUtils.copyProperties(record, vo);
        Book book = bookService.getById(record.getBookId());
        User lender = userService.getById(record.getLenderId());
        User borrower = userService.getById(record.getBorrowerId());
        vo.setBookTitle(book == null ? null : book.getTitle());
        vo.setLenderUsername(lender == null ? null : lender.getUsername());
        vo.setBorrowerUsername(borrower == null ? null : borrower.getUsername());
        return vo;
    }
}

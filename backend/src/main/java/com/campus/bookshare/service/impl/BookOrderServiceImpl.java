package com.campus.bookshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.bookshare.common.BusinessException;
import com.campus.bookshare.context.UserContext;
import com.campus.bookshare.entity.Book;
import com.campus.bookshare.entity.BookOrder;
import com.campus.bookshare.entity.User;
import com.campus.bookshare.dto.OrderCreateDTO;
import com.campus.bookshare.enums.BookStatusEnum;
import com.campus.bookshare.enums.CirculationTypeEnum;
import com.campus.bookshare.enums.OrderStatusEnum;
import com.campus.bookshare.enums.OrderTypeEnum;
import com.campus.bookshare.enums.PointsSourceTypeEnum;
import com.campus.bookshare.enums.ShareTypeEnum;
import com.campus.bookshare.enums.UserRoleEnum;
import com.campus.bookshare.mapper.BookOrderMapper;
import com.campus.bookshare.service.BookOrderService;
import com.campus.bookshare.service.BookService;
import com.campus.bookshare.service.BorrowRecordService;
import com.campus.bookshare.service.CirculationRecordService;
import com.campus.bookshare.service.UserService;
import com.campus.bookshare.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BookOrderServiceImpl extends ServiceImpl<BookOrderMapper, BookOrder> implements BookOrderService {

    @Autowired
    private BookService bookService;
    @Autowired
    private UserService userService;
    @Autowired
    private BorrowRecordService borrowRecordService;
    @Autowired
    private CirculationRecordService circulationRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO create(OrderCreateDTO dto) {
        OrderTypeEnum orderType = parseOrderType(dto.getOrderType());
        Long applicantId = UserContext.getCurrentUserId();
        User applicant = userService.getById(applicantId);
        if (UserRoleEnum.ADMIN.name().equals(applicant.getRole())) {
            throw new BusinessException("管理员不能发起共享申请");
        }
        Book book = bookService.getAvailableBook(dto.getBookId());
        if (!BookStatusEnum.ON_SHELF.name().equals(book.getStatus())) {
            throw new BusinessException("当前书籍不可申请");
        }
        if (applicantId.equals(book.getOwnerId())) {
            throw new BusinessException("不能申请自己的书籍");
        }
        if (!book.getShareType().equals(orderType.name())) {
            throw new BusinessException("订单类型必须与书籍共享方式一致");
        }
        if (hasProcessingOrder(book.getId())) {
            throw new BusinessException("该书籍已有处理中订单，暂不能重复申请");
        }
        if (OrderTypeEnum.EXCHANGE.equals(orderType)) {
            if (dto.getExchangeBookId() == null) {
                throw new BusinessException("交换订单必须选择自己的可交换书籍");
            }
            Book exchangeBook = bookService.getById(dto.getExchangeBookId());
            if (exchangeBook == null || !applicantId.equals(exchangeBook.getOwnerId())) {
                throw new BusinessException("交换书籍必须属于当前申请者");
            }
        }
        if (OrderTypeEnum.BORROW.equals(orderType) && dto.getExpectedReturnTime() == null) {
            throw new BusinessException("借阅订单必须填写预计归还时间");
        }
        BookOrder order = new BookOrder();
        order.setBookId(dto.getBookId());
        order.setOwnerId(book.getOwnerId());
        order.setApplicantId(applicantId);
        order.setExchangeBookId(dto.getExchangeBookId());
        order.setOrderType(orderType.name());
        order.setStatus(OrderStatusEnum.PENDING.name());
        order.setRemark(dto.getRemark());
        order.setCreateTime(new Date());
        order.setFinishTime(dto.getExpectedReturnTime());
        save(order);
        return toVO(order);
    }

    @Override
    public List<OrderVO> myCreated() {
        return convertOrders(list(new LambdaQueryWrapper<BookOrder>()
                .eq(BookOrder::getApplicantId, UserContext.getCurrentUserId())
                .orderByDesc(BookOrder::getCreateTime)));
    }

    @Override
    public List<OrderVO> myReceived() {
        return convertOrders(list(new LambdaQueryWrapper<BookOrder>()
                .eq(BookOrder::getOwnerId, UserContext.getCurrentUserId())
                .orderByDesc(BookOrder::getCreateTime)));
    }

    @Override
    public void accept(Long id) {
        BookOrder order = checkOwnerOrder(id);
        if (!OrderStatusEnum.PENDING.name().equals(order.getStatus())) {
            throw new BusinessException("当前订单不可同意");
        }
        order.setStatus(OrderStatusEnum.ACCEPTED.name());
        updateById(order);
        if (OrderTypeEnum.BORROW.name().equals(order.getOrderType())) {
            borrowRecordService.createBorrowRecord(order.getId());
        } else {
            Book book = bookService.getById(order.getBookId());
            book.setStatus(BookStatusEnum.SHARING.name());
            bookService.updateById(book);
        }
    }

    @Override
    public void reject(Long id) {
        BookOrder order = checkOwnerOrder(id);
        if (!OrderStatusEnum.PENDING.name().equals(order.getStatus())) {
            throw new BusinessException("当前订单不可拒绝");
        }
        order.setStatus(OrderStatusEnum.REJECTED.name());
        updateById(order);
    }

    @Override
    public void cancel(Long id) {
        BookOrder order = getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!UserContext.getCurrentUserId().equals(order.getApplicantId())) {
            throw new BusinessException("只有申请者可以取消订单");
        }
        if (OrderStatusEnum.COMPLETED.name().equals(order.getStatus())) {
            throw new BusinessException("已完成订单不能取消");
        }
        if (OrderStatusEnum.ACCEPTED.name().equals(order.getStatus())) {
            throw new BusinessException("已同意订单不能取消，请联系发布者处理");
        }
        if (OrderStatusEnum.CANCELLED.name().equals(order.getStatus()) || OrderStatusEnum.REJECTED.name().equals(order.getStatus())) {
            throw new BusinessException("当前订单不能重复取消");
        }
        order.setStatus(OrderStatusEnum.CANCELLED.name());
        updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void complete(Long id) {
        BookOrder order = getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!UserContext.getCurrentUserId().equals(order.getApplicantId())) {
            throw new BusinessException("只有申请者可以确认完成");
        }
        if (!OrderStatusEnum.ACCEPTED.name().equals(order.getStatus())) {
            throw new BusinessException("只有已同意订单才能确认完成");
        }
        if (OrderTypeEnum.BORROW.name().equals(order.getOrderType())) {
            throw new BusinessException("借阅订单请通过归还接口完成");
        }
        order.setStatus(OrderStatusEnum.COMPLETED.name());
        order.setFinishTime(new Date());
        updateById(order);

        Book book = bookService.getById(order.getBookId());
        if (OrderTypeEnum.SALE.name().equals(order.getOrderType())) {
            completeTransfer(book, order.getOwnerId(), order.getApplicantId(), order.getId(), CirculationTypeEnum.SALE, "出售共享完成");
            userService.changePoints(order.getOwnerId(), 5, PointsSourceTypeEnum.SHARE_COMPLETED.name(), "完成出售共享，积分+5");
            userService.changePoints(order.getApplicantId(), 5, PointsSourceTypeEnum.SHARE_COMPLETED.name(), "完成购书共享，积分+5");
        } else if (OrderTypeEnum.DONATE.name().equals(order.getOrderType())) {
            completeTransfer(book, order.getOwnerId(), order.getApplicantId(), order.getId(), CirculationTypeEnum.DONATE, "捐赠共享完成");
            userService.changePoints(order.getOwnerId(), 8, PointsSourceTypeEnum.DONATE_GIVER.name(), "完成捐赠共享，积分+8");
            userService.changePoints(order.getApplicantId(), 5, PointsSourceTypeEnum.DONATE_RECEIVER.name(), "完成领取捐赠，积分+5");
        } else if (OrderTypeEnum.EXCHANGE.name().equals(order.getOrderType())) {
            completeExchange(order, book);
        }
    }

    @Override
    public List<OrderVO> adminList(String orderType, String status) {
        LambdaQueryWrapper<BookOrder> wrapper = new LambdaQueryWrapper<BookOrder>();
        if (StringUtils.hasText(orderType)) {
            wrapper.eq(BookOrder::getOrderType, orderType);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(BookOrder::getStatus, status);
        }
        wrapper.orderByDesc(BookOrder::getCreateTime);
        return convertOrders(list(wrapper));
    }

    private BookOrder checkOwnerOrder(Long id) {
        BookOrder order = getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!UserContext.getCurrentUserId().equals(order.getOwnerId())) {
            throw new BusinessException("只有发布者可以操作该订单");
        }
        return order;
    }

    private boolean hasProcessingOrder(Long bookId) {
        return count(new LambdaQueryWrapper<BookOrder>()
                .eq(BookOrder::getBookId, bookId)
                .in(BookOrder::getStatus, OrderStatusEnum.PENDING.name(), OrderStatusEnum.ACCEPTED.name())) > 0;
    }

    private OrderTypeEnum parseOrderType(String orderType) {
        try {
            return OrderTypeEnum.valueOf(orderType);
        } catch (Exception e) {
            throw new BusinessException("订单类型不合法");
        }
    }

    private List<OrderVO> convertOrders(List<BookOrder> orders) {
        List<OrderVO> list = new ArrayList<OrderVO>();
        for (BookOrder order : orders) {
            list.add(toVO(order));
        }
        return list;
    }

    private OrderVO toVO(BookOrder order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        Book book = bookService.getById(order.getBookId());
        Book exchangeBook = order.getExchangeBookId() == null ? null : bookService.getById(order.getExchangeBookId());
        User owner = userService.getById(order.getOwnerId());
        User applicant = userService.getById(order.getApplicantId());
        vo.setBookTitle(book == null ? null : book.getTitle());
        vo.setExchangeBookTitle(exchangeBook == null ? null : exchangeBook.getTitle());
        vo.setOwnerUsername(owner == null ? null : owner.getUsername());
        vo.setApplicantUsername(applicant == null ? null : applicant.getUsername());
        return vo;
    }

    private void completeTransfer(Book book, Long fromUserId, Long toUserId, Long orderId, CirculationTypeEnum type, String remark) {
        book.setOwnerId(toUserId);
        book.setCirculationCount((book.getCirculationCount() == null ? 0 : book.getCirculationCount()) + 1);
        book.setStatus(BookStatusEnum.OFF_SHELF.name());
        bookService.updateById(book);
        circulationRecordService.addRecord(book.getId(), fromUserId, toUserId, type, orderId, remark);
    }

    private void completeExchange(BookOrder order, Book targetBook) {
        Book exchangeBook = bookService.getById(order.getExchangeBookId());
        if (exchangeBook == null) {
            throw new BusinessException("交换书籍不存在");
        }
        Long originalOwner = targetBook.getOwnerId();
        Long applicant = order.getApplicantId();
        targetBook.setOwnerId(applicant);
        targetBook.setCirculationCount((targetBook.getCirculationCount() == null ? 0 : targetBook.getCirculationCount()) + 1);
        targetBook.setStatus(BookStatusEnum.OFF_SHELF.name());
        bookService.updateById(targetBook);

        exchangeBook.setOwnerId(originalOwner);
        exchangeBook.setCirculationCount((exchangeBook.getCirculationCount() == null ? 0 : exchangeBook.getCirculationCount()) + 1);
        exchangeBook.setStatus(BookStatusEnum.OFF_SHELF.name());
        bookService.updateById(exchangeBook);

        circulationRecordService.addRecord(targetBook.getId(), originalOwner, applicant, CirculationTypeEnum.EXCHANGE, order.getId(), "交换共享完成");
        circulationRecordService.addRecord(exchangeBook.getId(), applicant, originalOwner, CirculationTypeEnum.EXCHANGE, order.getId(), "交换共享完成");
        userService.changePoints(originalOwner, 5, PointsSourceTypeEnum.SHARE_COMPLETED.name(), "完成交换共享，积分+5");
        userService.changePoints(applicant, 5, PointsSourceTypeEnum.SHARE_COMPLETED.name(), "完成交换共享，积分+5");
    }
}

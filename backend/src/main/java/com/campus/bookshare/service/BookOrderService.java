package com.campus.bookshare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.bookshare.dto.OrderCreateDTO;
import com.campus.bookshare.entity.BookOrder;
import com.campus.bookshare.vo.OrderVO;

import java.util.List;

public interface BookOrderService extends IService<BookOrder> {

    OrderVO create(OrderCreateDTO dto);

    List<OrderVO> myCreated();

    List<OrderVO> myReceived();

    void accept(Long id);

    void reject(Long id);

    void cancel(Long id);

    void complete(Long id);

    List<OrderVO> adminList(String orderType, String status);
}

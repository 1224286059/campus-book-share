package com.campus.bookshare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.bookshare.entity.BorrowRecord;
import com.campus.bookshare.vo.BorrowRecordVO;

import java.util.List;

public interface BorrowRecordService extends IService<BorrowRecord> {

    void createBorrowRecord(Long orderId);

    void returnBook(Long id);

    List<BorrowRecordVO> myRecords();
}

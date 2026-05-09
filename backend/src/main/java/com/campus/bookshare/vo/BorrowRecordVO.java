package com.campus.bookshare.vo;

import lombok.Data;

import java.util.Date;

@Data
public class BorrowRecordVO {
    private Long id;
    private Long orderId;
    private Long bookId;
    private String bookTitle;
    private Long lenderId;
    private String lenderUsername;
    private Long borrowerId;
    private String borrowerUsername;
    private Date borrowTime;
    private Date expectedReturnTime;
    private Date actualReturnTime;
    private String status;
}

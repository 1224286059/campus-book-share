package com.campus.bookshare.vo;

import lombok.Data;

import java.util.Date;

@Data
public class OrderVO {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private Long ownerId;
    private String ownerUsername;
    private Long applicantId;
    private String applicantUsername;
    private Long exchangeBookId;
    private String exchangeBookTitle;
    private String orderType;
    private String status;
    private String remark;
    private Date createTime;
    private Date finishTime;
}

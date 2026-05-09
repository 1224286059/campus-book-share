package com.campus.bookshare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("book_order")
public class BookOrder {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("book_id")
    private Long bookId;
    @TableField("owner_id")
    private Long ownerId;
    @TableField("applicant_id")
    private Long applicantId;
    @TableField("exchange_book_id")
    private Long exchangeBookId;
    @TableField("order_type")
    private String orderType;
    private String status;
    private String remark;
    @TableField("create_time")
    private Date createTime;
    @TableField("finish_time")
    private Date finishTime;
}

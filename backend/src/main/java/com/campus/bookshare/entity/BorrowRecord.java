package com.campus.bookshare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("borrow_record")
public class BorrowRecord {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("order_id")
    private Long orderId;
    @TableField("book_id")
    private Long bookId;
    @TableField("lender_id")
    private Long lenderId;
    @TableField("borrower_id")
    private Long borrowerId;
    @TableField("borrow_time")
    private Date borrowTime;
    @TableField("expected_return_time")
    private Date expectedReturnTime;
    @TableField("actual_return_time")
    private Date actualReturnTime;
    private String status;
}

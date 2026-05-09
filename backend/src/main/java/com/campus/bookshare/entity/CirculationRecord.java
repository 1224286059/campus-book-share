package com.campus.bookshare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("circulation_record")
public class CirculationRecord {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("book_id")
    private Long bookId;
    @TableField("from_user_id")
    private Long fromUserId;
    @TableField("to_user_id")
    private Long toUserId;
    @TableField("circulation_type")
    private String circulationType;
    @TableField("order_id")
    private Long orderId;
    private String remark;
    @TableField("create_time")
    private Date createTime;
}

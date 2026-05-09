package com.campus.bookshare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("evaluation")
public class Evaluation {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("order_id")
    private Long orderId;
    @TableField("evaluator_id")
    private Long evaluatorId;
    @TableField("target_user_id")
    private Long targetUserId;
    @TableField("book_score")
    private Integer bookScore;
    @TableField("user_score")
    private Integer userScore;
    private String content;
    @TableField("create_time")
    private Date createTime;
}

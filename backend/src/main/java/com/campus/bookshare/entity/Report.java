package com.campus.bookshare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("report")
public class Report {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("reporter_id")
    private Long reporterId;
    @TableField("target_type")
    private String targetType;
    @TableField("target_id")
    private Long targetId;
    private String reason;
    private String status;
    @TableField("create_time")
    private Date createTime;
    @TableField("handle_time")
    private Date handleTime;
}

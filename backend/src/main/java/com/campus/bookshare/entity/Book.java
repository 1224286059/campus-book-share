package com.campus.bookshare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("book")
public class Book {

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("owner_id")
    private Long ownerId;
    @TableField("original_owner_id")
    private Long originalOwnerId;
    @TableField("category_id")
    private Long categoryId;
    private String title;
    private String author;
    private String publisher;
    @TableField("course_name")
    private String courseName;
    private String major;
    @TableField("condition_level")
    private String conditionLevel;
    @TableField("book_location")
    private String bookLocation;
    @TableField("cover_url")
    private String coverUrl;
    private BigDecimal price;
    @TableField("share_type")
    private String shareType;
    private String status;
    @TableField("circulation_count")
    private Integer circulationCount;
    private String description;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
}

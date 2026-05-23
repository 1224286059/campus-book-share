package com.campus.bookshare.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BookVO {
    private Long id;
    private Long ownerId;
    private String ownerUsername;
    private Long originalOwnerId;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String author;
    private String publisher;
    private String courseName;
    private String major;
    private String conditionLevel;
    private String bookLocation;
    private String coverUrl;
    private BigDecimal price;
    private String shareType;
    private String status;
    private Integer circulationCount;
    private String description;
    private Date createTime;
    private Date updateTime;
}

package com.campus.bookshare.vo;

import lombok.Data;

import java.util.Date;

@Data
public class CategoryVO {
    private Long id;
    private String name;
    private String description;
    private Date createTime;
}

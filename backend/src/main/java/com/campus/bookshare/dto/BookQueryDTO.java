package com.campus.bookshare.dto;

import lombok.Data;

@Data
public class BookQueryDTO {

    private String keyword;
    private Long categoryId;
    private String major;
    private String courseName;
    private String shareType;
    private Integer page = 1;
    private Integer size = 10;
}

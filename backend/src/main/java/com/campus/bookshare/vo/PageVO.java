package com.campus.bookshare.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageVO<T> {
    private Long total;
    private Long current;
    private Long size;
    private List<T> records;
}

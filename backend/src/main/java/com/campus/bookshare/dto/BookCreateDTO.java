package com.campus.bookshare.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class BookCreateDTO {

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @NotBlank(message = "书名不能为空")
    private String title;

    private String author;
    private String publisher;
    private String courseName;
    private String major;
    private String conditionLevel;
    private String coverUrl;

    @DecimalMin(value = "0.0", inclusive = true, message = "价格不能小于0")
    private BigDecimal price;

    @NotBlank(message = "共享方式不能为空")
    private String shareType;

    private String description;
}

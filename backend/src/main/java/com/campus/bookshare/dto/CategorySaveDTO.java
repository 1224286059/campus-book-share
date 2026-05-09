package com.campus.bookshare.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CategorySaveDTO {

    @NotBlank(message = "分类名称不能为空")
    private String name;

    private String description;
}

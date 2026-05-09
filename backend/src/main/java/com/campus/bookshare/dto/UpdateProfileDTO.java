package com.campus.bookshare.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdateProfileDTO {

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotBlank(message = "学院不能为空")
    private String college;

    @NotBlank(message = "专业不能为空")
    private String major;

    @NotBlank(message = "年级不能为空")
    private String grade;
}

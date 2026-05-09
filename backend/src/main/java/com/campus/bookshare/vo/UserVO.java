package com.campus.bookshare.vo;

import lombok.Data;

import java.util.Date;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String phone;
    private String college;
    private String major;
    private String grade;
    private Integer points;
    private Integer creditScore;
    private String role;
    private Integer status;
    private Date createTime;
}

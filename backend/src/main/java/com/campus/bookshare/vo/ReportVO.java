package com.campus.bookshare.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ReportVO {
    private Long id;
    private Long reporterId;
    private String reporterUsername;
    private String targetType;
    private Long targetId;
    private String reason;
    private String status;
    private Date createTime;
    private Date handleTime;
}

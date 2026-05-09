package com.campus.bookshare.vo;

import lombok.Data;

import java.util.Date;

@Data
public class EvaluationVO {
    private Long id;
    private Long orderId;
    private Long evaluatorId;
    private String evaluatorUsername;
    private Long targetUserId;
    private String targetUsername;
    private Integer bookScore;
    private Integer userScore;
    private String content;
    private Date createTime;
}

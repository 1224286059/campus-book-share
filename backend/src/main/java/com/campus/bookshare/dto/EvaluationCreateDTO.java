package com.campus.bookshare.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class EvaluationCreateDTO {

    @NotNull(message = "订单不能为空")
    private Long orderId;

    @NotNull(message = "被评价用户不能为空")
    private Long targetUserId;

    @NotNull(message = "书籍评分不能为空")
    @Min(value = 1, message = "书籍评分最小为1")
    @Max(value = 5, message = "书籍评分最大为5")
    private Integer bookScore;

    @NotNull(message = "用户评分不能为空")
    @Min(value = 1, message = "用户评分最小为1")
    @Max(value = 5, message = "用户评分最大为5")
    private Integer userScore;

    private String content;
}

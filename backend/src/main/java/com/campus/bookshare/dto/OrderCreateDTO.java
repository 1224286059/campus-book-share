package com.campus.bookshare.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class OrderCreateDTO {

    @NotNull(message = "书籍不能为空")
    private Long bookId;

    @NotBlank(message = "订单类型不能为空")
    private String orderType;

    private Long exchangeBookId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expectedReturnTime;

    private String remark;
}

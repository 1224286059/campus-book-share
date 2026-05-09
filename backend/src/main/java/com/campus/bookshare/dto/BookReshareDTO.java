package com.campus.bookshare.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class BookReshareDTO {

    @NotBlank(message = "共享方式不能为空")
    private String shareType;

    @DecimalMin(value = "0.0", inclusive = true, message = "价格不能小于0")
    private BigDecimal price;

    private String description;
    private String conditionLevel;
    private String coverUrl;
}

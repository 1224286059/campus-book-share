package com.campus.bookshare.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class BookReshareDTO {

    @NotBlank(message = "共享方式不能为空")
    private String shareType;

    @DecimalMin(value = "0.0", inclusive = true, message = "价格不能小于0")
    private BigDecimal price;

    private String description;
    private String conditionLevel;
    @Size(max = 255, message = "书籍位置长度不能超过255")
    private String bookLocation;
    private String coverUrl;
}

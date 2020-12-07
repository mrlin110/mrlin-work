package com.mrlin.redis.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;


import java.util.Date;

@Setter
@Getter
@ApiModel(description = "抢购代金券信息")
public class SeckillVouchers extends BaseModel {

    @ApiModelProperty("代金券外键")
    private Integer fkVoucherId;
    @ApiModelProperty("数量")
    private int amount;
    @ApiModelProperty(value = "抢购开始时间",example = "2020-02-05 13:30:41")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    @ApiModelProperty(value = "抢购结束时间",example = "2020-02-05 13:30:41")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

}
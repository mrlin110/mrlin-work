package com.mrlin.redis.po;

/**
 * @author wmy
 * @create 2020-11-24 19:13
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 实体对象公共属性
 */
@Getter
@Setter
public class BaseModel implements Serializable {

    private Integer id;
    @ApiModelProperty(example = "2020-02-05 13:30:41")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;
    @ApiModelProperty(example = "2020-02-05 13:30:41")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateDate;
    private int isValid;

}

package com.mrlin.commons.domain;

/**
 * @author wmy
 * @create 2020-11-21 11:32
 */

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 公共返回对象
 */
@Getter
@Setter
@ApiModel(value = "返回说明")
public class ResultInfo<T> implements Serializable {

    @ApiModelProperty(value = "成功标识0=失败，1=成功")
    private Integer code;
    @ApiModelProperty(value = "描述信息")
    private String message;
    @ApiModelProperty(value = "返回数据对象")
    private T data;

}

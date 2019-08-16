package com.guangmushikong.lbi.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ProjectDO {
    /**
     * ID，主键
     */
    Long id;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    String name;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    String memo;
    /**
     * 数据集ID
     */
    String datasetIds;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss.SSS")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", locale = "zh", timezone = "GMT+8")
    Date createTime;
    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss.SSS")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", locale = "zh", timezone = "GMT+8")
    Date modifyTime;
}

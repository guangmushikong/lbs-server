package com.guangmushikong.lbi.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/*************************************
 * Class Name: DataSetDO
 * Description:〈数据集〉
 * @since 1.0.0
 ************************************/
@Data
public class DataSetDO {
    /**
     * ID，主键
     */
    long id;
    /**
     * 地图ID
     */
    @JSONField(serialize=false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    long mapId;
    /**
     * 名称
     */
    String name;
    /**
     * 备注
     */
    String memo;
    /**
     * 数据记录日期，用于影像时间序列
     */
    String recordDate;
    /**
     * 图层分组。L0-L4
     */
    String group;
    /**
     * 图层种类。1为普通图层，2为时序图层'
     */
    int kind;
    /**
     * 数据类型。栅格、矢量、照片
     */
    int type;

    /**
     * 创建时间
     */
    //@JSONField(format = "yyyy-MM-dd HH:mm:ss.SSS")
    //@JSONField(serialize=false)
    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Date createTime;
    /**
     * 修改时间
     */
    //@JSONField(format = "yyyy-MM-dd HH:mm:ss.SSS")
    //@JSONField(serialize=false)
    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Date modifyTime;

    List<TileMap> maps;
}

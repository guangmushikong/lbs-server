package com.guangmushikong.lbi.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TileMap {
    long id;
    /**
     * 服务ID
     */
    long serviceId;
    /**
     * 标题
     */
    String title;
    /**
     * 数据记录日期，用于影像时间序列
     */
    String recordDate;
    /**
     * 摘要
     */
    @JSONField(serialize=false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String _abstract;

    String srs;
    String profile;
    String href;
    /**
     * 最小经度
     */
    double minX;
    /**
     * 最小纬度
     */
    double minY;
    /**
     * 最大经度
     */
    double maxX;
    /**
     * 最大纬度
     */
    double maxY;
    /**
     * 原点经度
     */
    double originX;
    /**
     * 原点纬度
     */
    double originY;
    /**
     * 最小缩放级别
     */
    int minZoom;
    /**
     * 最大缩放级别
     */
    int maxZoom;
    /**
     * 瓦片宽度
     */
    int width;
    /**
     * 瓦片高度
     */
    int height;
    /**
     * 媒体类型
     */
    String mimeType;
    /**
     * 瓦片扩展名
     */
    String extension;

    /**
     * 类别
     */
    @JSONField(serialize=false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    int kind;
    /**
     * 图层组
     */
    String group;
    /**
     * 数据源
     */
    @JSONField(serialize=false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String source;
    /**
     * 配置
     */
    @JSONField(serialize=false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String prop;

    /**
     * 文件扩展名
     */
    String fileExtension;

    public void setAbstract(String val){
        this._abstract=val;
    }
    public String getAbstract(){
        return this._abstract;
    }
}

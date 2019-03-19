package com.guangmushikong.lbi.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TileMap {
    long id;

    long serviceId;
    /**
     * 标题
     */
    String title;
    /**
     * 数据记录日期，用于影像时间序列
     */
    String recordDate;
    String _abstract;
    String srs;
    String profile;
    String href;

    double minX;
    double minY;
    double maxX;
    double maxY;
    double originX;
    double originY;
    int minZoom;
    int maxZoom;

    int width;
    int height;
    String mimeType;
    String extension;

    @JSONField(serialize=false)
    int kind;

    String group;//图层组

    @JSONField(serialize=false)
    String source;

    String fileExtension;

    public void setAbstract(String val){
        this._abstract=val;
    }
    public String getAbstract(){
        return this._abstract;
    }
}

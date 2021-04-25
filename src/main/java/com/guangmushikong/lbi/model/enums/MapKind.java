package com.guangmushikong.lbi.model.enums;

/**
 * 地图瓦片类别
 */
public enum MapKind {
    GeoServer(1,"Geoserver服务"),
    OSSCache(2,"OSS缓存图片"),
    OSSTimeCache(3,"OSS时序缓存图片"),
    LocalCache(4,"本地缓存图片"),
    LocalTimeCache(5,"本地时序缓存图片"),
    PGLayer(6,"PG图层"),
    XYZLayer(7,"XYZ图层"),
    ExtXyzLayer(8,"外部XYZ图层"),
    ExtTmsLayer(9,"外部TMS图层");

    int code;
    String name;

    MapKind(int code,String name) {
        this.code = code;
        this.name=name;
    }
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String msg) {
        this.name = msg;
    }

    public static MapKind getByValue(int value) {
        for (MapKind code : values()) {
            if (code.getCode() == value) {
                return code;
            }
        }
        return null;
    }
}

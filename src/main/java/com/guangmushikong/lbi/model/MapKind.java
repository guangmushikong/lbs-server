package com.guangmushikong.lbi.model;

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
    XYZLayer(7,"XYZ图层");

    int code;
    String msg;

    //构造器默认也只能是private, 从而保证构造函数只能在内部使用
    MapKind(int code,String msg) {
        this.code = code;
        this.msg=msg;
    }
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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

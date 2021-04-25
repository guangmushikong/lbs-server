package com.guangmushikong.lbi.model.enums;

/**
 * 图层组
 */
public enum LayerGroup {
    BaseRaster("L0","基础栅格图层"),
    ProjectRaster("L1","项目栅格图层"),
    BaseVector("L2","基础矢量图层"),
    ProjectVector("L3","项目矢量图层"),
    Draw("L4","绘制图层");

    String code;
    String name;

    LayerGroup(String code,String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static LayerGroup getByValue(String value) {
        for (LayerGroup code : values()) {
            if (code.getCode() == value) {
                return code;
            }
        }
        return null;
    }
}

package com.guangmushikong.lbi.model.enums;

import org.springframework.http.MediaType;

/**
 * 瓦片类型
 */
public enum TileType {
    Json("json","JSON",MediaType.APPLICATION_JSON),
    Png("png","PNG",MediaType.IMAGE_PNG),
    Jpeg("jpeg","JPEG",MediaType.IMAGE_JPEG),
    Tif("tif","TIF",MediaType.valueOf("image/tif")),
    Terrain("terrain","TERRAIN",MediaType.valueOf("application/vnd.quantized-mesh"));

    String code;
    String name;
    MediaType content;

    TileType(String code, String name, MediaType content){
        this.code=code;
        this.name=name;
        this.content=content;
    }

    public String getCode(){
        return this.code;
    }

    public String getName(){
        return this.name;
    }

    public MediaType getContent(){
        return this.content;
    }

    public static TileType getByCode(String code){
        for(TileType type:values()){
            if(type.getCode().equalsIgnoreCase(code)){
                return type;
            }
        }
        return null;
    }
}

package com.guangmushikong.lbi.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TileMapService {
    long id;
    String title;
    String _abstract;
    String version;
    String href;
    int kind;

    public void setAbstract(String val){
        this._abstract=val;
    }
    public String getAbstract(){
        return this._abstract;
    }
}

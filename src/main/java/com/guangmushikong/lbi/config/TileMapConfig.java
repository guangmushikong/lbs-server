package com.guangmushikong.lbi.config;

import com.guangmushikong.lbi.model.TileMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileMapConfig {
    Map<String, TileMap> tileMaps = new HashMap<>();
    Map<String, TileMap> xyzMaps = new HashMap<>();

    public TileMap getTileMap(String key){
        if(key==null || tileMaps.isEmpty())return null;
        return tileMaps.get(key);
    }

    public TileMap getXYZMap(String key){
        if(key==null || xyzMaps.isEmpty())return null;
        return xyzMaps.get(key);
    }

    public void setTileMaps(List<TileMap> list){
        xyzMaps.clear();
        tileMaps.clear();
        for(TileMap map:list){
            long serviceId=map.getServiceId();
            String key=map.getTitle()+"@"+map.getSrs()+"@"+map.getExtension();
            if(serviceId==1){
                xyzMaps.put(key,map);
            }else if(serviceId==2){
                tileMaps.put(key,map);
            }
        }
        System.out.println("【xyzMaps】"+xyzMaps.size());
        System.out.println("【tileMaps】"+tileMaps.size());
    }
}

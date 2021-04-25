package com.guangmushikong.lbi.controller;

import com.google.common.collect.Sets;
import com.guangmushikong.lbi.model.enums.ServiceType;
import com.guangmushikong.lbi.model.enums.TileType;
import com.guangmushikong.lbi.service.TileService;
import com.guangmushikong.lbi.model.Tile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


/**
 * XYZ地图服务
 */
@RestController
@RequestMapping("/xyz")
@Slf4j
public class XYZController {
    @Autowired
    TileService tileService;

    /**
     * 获取XYZ瓦片
     * @param version 版本号
     * @param tileset 版本号
     * @param x 瓦片X值
     * @param y 瓦片Y值
     * @param z 瓦片Z值
     * @param extension 后缀
     */
    @RequestMapping(value="/{version}/{tileset}/{x}/{y}/{z}.{extension}",method = RequestMethod.GET)
    public ResponseEntity getTile(
            @PathVariable("version") String version,
            @PathVariable("tileset") String tileset,
            @PathVariable("x") long x,
            @PathVariable("y") long y,
            @PathVariable("z") int z,
            @PathVariable("extension") String extension) {
        try{
            Tile tile;
            Set<String> extensionSet= Sets.newHashSet("json","geojson");
            int y2=new Double(Math.pow(2,z)-1-y).intValue();
            tile=new Tile(x,y2,z);
            /*if(!extensionSet.contains(extension.toLowerCase())){

                tile=new Tile(x,y2,z);
            }else {
                tile=new Tile(x,y,z);
            }*/
            byte[] bytes=tileService.getTile(ServiceType.XYZ,tileset,tile);

            ResponseEntity.BodyBuilder bodyBuilder=ResponseEntity.ok();
            TileType tileType= TileType.getByCode(extension.toLowerCase());
            if(tileType!=null){
                bodyBuilder.contentType(tileType.getContent());
            }
            return bodyBuilder.body(bytes);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
    }

    /**
     * 获取XYZ瓦片
     * @param layerName 图层名
     * @param x 瓦片X值
     * @param y 瓦片Y值
     * @param z 瓦片Z值
     * @param extension 后缀
     */
    @RequestMapping(value="/{layerName}/{x}/{y}/{z}.{extension}",method = RequestMethod.GET)
    public ResponseEntity getTile2(
            @PathVariable("layerName") String layerName,
            @PathVariable("x") long x,
            @PathVariable("y") long y,
            @PathVariable("z") int z,
            @PathVariable("extension") String extension) {
        String tileset=layerName+"@EPSG:900913"+extension;
        return getTile("1.0.0",tileset,x,y,z,extension);
    }
}

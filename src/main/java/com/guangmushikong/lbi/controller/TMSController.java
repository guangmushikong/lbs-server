package com.guangmushikong.lbi.controller;

import com.guangmushikong.lbi.model.ServiceType;
import com.guangmushikong.lbi.model.Tile;
import com.guangmushikong.lbi.service.TileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * TMS地图服务
 */
@RestController
@RequestMapping("/tms")
@Slf4j
public class TMSController {
    @Autowired
    TileService tileService;

    /**
     * 获取TMS瓦片
     * @param version 版本号
     * @param tileset 版本号
     * @param z 瓦片Z值
     * @param x 瓦片X值
     * @param y 瓦片Y值
     * @param extension 后缀
     */
    @GetMapping("/{version}/{tileset}/{z}/{x}/{y}.{extension}")
    public ResponseEntity getTile(
            @PathVariable("version") String version,
            @PathVariable("tileset") String tileset,
            @PathVariable("z") int z,
            @PathVariable("x") long x,
            @PathVariable("y") long y,
            @PathVariable("extension") String extension) {
        try{
            Tile tile=new Tile(x,y,z);
            byte[] bytes=tileService.getTile(ServiceType.TMS,tileset,tile);
            ResponseEntity.BodyBuilder bodyBuilder=ResponseEntity.ok();
            if("json".equalsIgnoreCase(extension)){
                bodyBuilder.contentType(MediaType.APPLICATION_JSON);
            }else if("png".equalsIgnoreCase(extension)){
                bodyBuilder.contentType(MediaType.IMAGE_PNG);
            }else if("jpeg".equalsIgnoreCase(extension)){
                bodyBuilder.contentType(MediaType.IMAGE_JPEG);
            }else if("tif".equalsIgnoreCase(extension)){
                bodyBuilder.contentType(MediaType.valueOf("image/tif"));
            }else if("terrain".equalsIgnoreCase(extension)){
                bodyBuilder.contentType(MediaType.valueOf("application/vnd.quantized-mesh"));
            }
            return bodyBuilder.body(bytes);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/{version}/{tileset}/layer.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public byte[] getLayer(
            @PathVariable("version") String version,
            @PathVariable("tileset") String tileset)throws IOException {
        String[] args=tileset.split("@");
        return tileService.getLayer(version,args[0],args[1],args[2]);
    }

    @GetMapping(value = "/{version}/{tileset}/meta.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public byte[] getMeta(
            @PathVariable("version") String version,
            @PathVariable("tileset") String tileset)throws IOException {
        String[] args=tileset.split("@");
        return tileService.getMeta(version,args[0],args[1],args[2]);
    }
}

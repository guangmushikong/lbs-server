package com.guangmushikong.lbi.controller;

import com.google.common.collect.Sets;
import com.guangmushikong.lbi.model.ServiceType;
import com.guangmushikong.lbi.service.TileService;
import com.guangmushikong.lbi.model.Tile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Api(value = "XYZ地图服务", tags = "XYZ", description = "XYZ协议瓦片地图服务相关接口")
@RestController
@RequestMapping("/xyz")
@Slf4j
public class XYZController {
    @Autowired
    TileService tileService;

    @ApiOperation(value = "XYZ瓦片地图服务", notes = "获取XYZ瓦片")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "version", value = "版本号", required = true, dataType = "string"),
            @ApiImplicitParam(name = "tileset", value = "数据集名", required = true, dataType = "string"),
            @ApiImplicitParam(name = "x", value = "瓦片X值", required = true, dataType = "long"),
            @ApiImplicitParam(name = "y", value = "瓦片Y值", required = true, dataType = "long"),
            @ApiImplicitParam(name = "z", value = "瓦片Z值", required = true, dataType = "int"),
            @ApiImplicitParam(name = "extension", value = "后缀", required = true, dataType = "string"),
    })
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
            if(!extensionSet.contains(extension.toLowerCase())){
                int y2=new Double(Math.pow(2,z)-1-y).intValue();
                tile=new Tile(x,y2,z);
            }else {
                tile=new Tile(x,y,z);
            }
            byte[] bytes=tileService.getTile(ServiceType.XYZ,tileset,tile);
            ResponseEntity.BodyBuilder bodyBuilder=ResponseEntity.ok();
            if(extensionSet.contains(extension.toLowerCase())){
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

    @ApiOperation(value = "XYZ瓦片地图服务", notes = "获取XYZ瓦片")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "layerName", value = "图层名", required = true, dataType = "string"),
            @ApiImplicitParam(name = "x", value = "瓦片X值", required = true, dataType = "long"),
            @ApiImplicitParam(name = "y", value = "瓦片Y值", required = true, dataType = "long"),
            @ApiImplicitParam(name = "z", value = "瓦片Z值", required = true, dataType = "int"),
            @ApiImplicitParam(name = "extension", value = "后缀", required = true, dataType = "string"),
    })
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

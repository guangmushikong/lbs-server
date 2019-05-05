package com.guangmushikong.lbi.controller;

import com.alibaba.fastjson.JSONObject;
import com.guangmushikong.lbi.model.ServiceType;
import com.guangmushikong.lbi.model.TileMap;
import com.guangmushikong.lbi.service.TileService;
import com.lbi.model.Tile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Api(value = "XYZ地图服务", tags = "XYZ", description = "XYZ协议瓦片地图服务相关接口")
@RestController
@RequestMapping("/xyz")
public class XYZController {
    @Resource(name="tileService")
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
        Tile tile=new Tile(x,y,z);
        String[] args=tileset.split("@");
        TileMap tileMap=tileService.getTileMapById(ServiceType.XYZ.getValue(),args[0],args[1],args[2]);
        byte[] bytes;
        ResponseEntity.BodyBuilder bodyBuilder=ResponseEntity.ok();
        if(extension.equalsIgnoreCase("geojson")
                ||extension.equalsIgnoreCase("json")){
            bytes=tileService.getTile(tileMap,tile);
            bodyBuilder.contentType(MediaType.APPLICATION_JSON);
        }else{
            int alterY=new Double(Math.pow(2,z)-1-y).intValue();
            tile.setY(alterY);
            bytes=tileService.getTile(tileMap,tile);
            if("png".equalsIgnoreCase(extension)){
                bodyBuilder.contentType(MediaType.IMAGE_PNG);
            }else if("jpeg".equalsIgnoreCase(extension)){
                bodyBuilder.contentType(MediaType.IMAGE_JPEG);
            }else if("tif".equalsIgnoreCase(extension)){
                bodyBuilder.contentType(MediaType.valueOf("image/tif"));
            }else if("terrain".equalsIgnoreCase(extension)){
                bodyBuilder.contentType(MediaType.valueOf("application/vnd.quantized-mesh"));
            }
        }
        return bodyBuilder.body(bytes);
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
    public ResponseEntity getXYZ(
            @PathVariable("layerName") String layerName,
            @PathVariable("x") long x,
            @PathVariable("y") long y,
            @PathVariable("z") int z,
            @PathVariable("extension") String extension) {
        Tile tile=new Tile(x,y,z);
        TileMap tileMap=tileService.getTileMapById(ServiceType.XYZ.getValue(),layerName,extension);
        byte[] bytes;
        ResponseEntity.BodyBuilder bodyBuilder=ResponseEntity.ok();
        if(extension.equalsIgnoreCase("json")
                || extension.equalsIgnoreCase("geojson")){
            bytes=tileService.getTile(tileMap,tile);
            bodyBuilder.contentType(MediaType.APPLICATION_JSON);
        }else{
            int alterY=new Double(Math.pow(2,z)-1-y).intValue();
            tile.setY(alterY);
            if(layerName.equalsIgnoreCase("gujiao")){
                layerName="gujiao_satellite_raster";
            } else if(layerName.equalsIgnoreCase("city")){
                layerName="china_city_polygon";
            } else if(layerName.equalsIgnoreCase("world")){
                layerName="world_satellite_raster";
            }
            bytes=tileService.getTile(tileMap,tile);
            if("png".equalsIgnoreCase(extension)){
                bodyBuilder.contentType(MediaType.IMAGE_PNG);
            }else if("jpeg".equalsIgnoreCase(extension)){
                bodyBuilder.contentType(MediaType.IMAGE_JPEG);
            }else if("tif".equalsIgnoreCase(extension)){
                bodyBuilder.contentType(MediaType.valueOf("image/tif"));
            }
        }
        return bodyBuilder.body(bytes);
    }

    @ApiOperation(value = "等高线瓦片瓦片地图服务", notes = "获取XYZ等高线瓦片")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x", value = "瓦片X值", required = true, dataType = "long"),
            @ApiImplicitParam(name = "y", value = "瓦片Y值", required = true, dataType = "long"),
            @ApiImplicitParam(name = "z", value = "瓦片Z值", required = true, dataType = "int")
    })
    @GetMapping(value = "/contour/{x}/{y}/{z}.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONObject getContour(
            @PathVariable("x") long x,
            @PathVariable("y") long y,
            @PathVariable("z") int z){
        Tile tile=new Tile(x,y,z);
        JSONObject body=new JSONObject();
        //JSONObject body=xyzService.getJingZhuangContourByTile(tile);
        return body;
    }

}

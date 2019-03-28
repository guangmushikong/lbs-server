package com.guangmushikong.lbi.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.guangmushikong.lbi.service.XYZService;
import com.lbi.model.Tile;
import com.lbi.util.ImageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Api(value = "XYZ地图服务", tags = "XYZ", description = "XYZ协议瓦片地图服务相关接口")
@RestController
@RequestMapping("/xyz")
public class XYZController {
    @Resource(name="xyzService")
    XYZService xyzService;

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
        int alterY=new Double(Math.pow(2,z)-1-y).intValue();
        tile.setY(alterY);
        String[] args=tileset.split("@");
        if(extension.equalsIgnoreCase("geojson")
                ||extension.equalsIgnoreCase("json")){
            tile=new Tile(x,y,z);
            String layerName=args[0];
            if(layerName.equalsIgnoreCase("china_city_polygon")){
                JSONArray body=xyzService.getCityRegionByTile(tile);
                if(body!=null)return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);
            }else if(layerName.equalsIgnoreCase("liupanshui_extent_line")) {
                JSONArray body=xyzService.getLPSByTile("liupanshui_extent_line","green",tile);
                if(body!=null)return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);
            }else if(layerName.equalsIgnoreCase("liupanshui_track_line")) {
                JSONArray body=xyzService.getLPSByTile("liupanshui_track_line","yellow",tile);
                if(body!=null)return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);
            }else if(layerName.equalsIgnoreCase("liupanshui_point")) {
                JSONArray body=xyzService.getLPSByTile("liupanshui_point","red",tile);
                if(body!=null)return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);
            }else{
                String body=xyzService.getCacheJsonTile(version,args[0],args[1],args[2],tile);
                if(body!=null)return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);
            }
        }else if(extension.equalsIgnoreCase("png")){
            byte[] bytes=xyzService.getXYZ_Tile(version,args[0],args[1],args[2],tile);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(bytes);
        }else if(extension.equalsIgnoreCase("jpeg")){
            byte[] bytes=xyzService.getXYZ_Tile(version,args[0],args[1],args[2],tile);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
        }else if(extension.equalsIgnoreCase("tif")){
            byte[] bytes=xyzService.getXYZ_Tile(version,args[0],args[1],args[2],tile);
            return ResponseEntity.ok().contentType(MediaType.valueOf("image/tif")).body(bytes);
        }else if(extension.equalsIgnoreCase("terrain")){
            byte[] bytes=xyzService.getXYZ_Tile(version,args[0],args[1],args[2],tile);
            return ResponseEntity.ok().contentType(MediaType.valueOf("application/terrain")).body(bytes);
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
    public ResponseEntity getXYZ(
            @PathVariable("layerName") String layerName,
            @PathVariable("x") long x,
            @PathVariable("y") long y,
            @PathVariable("z") int z,
            @PathVariable("extension") String extension) {
        Tile tile=new Tile(x,y,z);
        if(extension.equalsIgnoreCase("json")){
            JSONArray body=null;
            if(layerName.equalsIgnoreCase("gujiao_contour50_line")){
                body=xyzService.getGujiaoContour50ByTile(tile);
            }else if(layerName.equalsIgnoreCase("gujiao_contour100_line")){
                body=xyzService.getGujiaoContour100ByTile(tile);
            }else if(layerName.equalsIgnoreCase("gujiao_contour200_line")){
                body=xyzService.getGujiaoContour200ByTile(tile);
            }else if(layerName.equalsIgnoreCase("city")){
                body=xyzService.getCityRegionByTile(tile);
            }else if(layerName.equalsIgnoreCase("liupanshui_extent_line")){
                body=xyzService.getLPSByTile("liupanshui_extent_line","green",tile);
            }else if(layerName.equalsIgnoreCase("liupanshui_point")){
                body=xyzService.getLPSByTile("liupanshui_point","red",tile);
            }else if(layerName.equalsIgnoreCase("liupanshui_track_line")){
                body=xyzService.getLPSByTile("liupanshui_track_line","yellow",tile);
            }
            if(body!=null)return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);
        }else if(extension.equalsIgnoreCase("png")){
            if(layerName.equalsIgnoreCase("gujiao"))layerName="gujiao_satellite_raster";
            else if(layerName.equalsIgnoreCase("city"))layerName="china_city_polygon";
            int alterY=new Double(Math.pow(2,z)-1-y).intValue();
            tile.setY(alterY);
            byte[] bytes=xyzService.getXYZ_Tile(layerName,extension,tile);
            if(bytes==null)bytes= ImageUtil.emptyImage();
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(bytes);
        }else if(extension.equalsIgnoreCase("jpeg")){
            if(layerName.equalsIgnoreCase("world"))layerName="world_satellite_raster";
            int alterY=new Double(Math.pow(2,z)-1-y).intValue();
            tile.setY(alterY);
            byte[] bytes=xyzService.getXYZ_Tile(layerName,extension,tile);
            if(bytes==null)bytes= ImageUtil.emptyImage();
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
        }else if(extension.equalsIgnoreCase("tif")){
            int alterY=new Double(Math.pow(2,z)-1-y).intValue();
            tile.setY(alterY);
            byte[] bytes=xyzService.getXYZ_Tile(layerName,extension,tile);
            if(bytes==null)bytes=ImageUtil.emptyImage();
            return ResponseEntity.ok().contentType(MediaType.valueOf("image/tif")).body(bytes);
        }
        return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "等高线瓦片瓦片地图服务", notes = "获取XYZ等高线瓦片")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "x", value = "瓦片X值", required = true, dataType = "long"),
            @ApiImplicitParam(name = "y", value = "瓦片Y值", required = true, dataType = "long"),
            @ApiImplicitParam(name = "z", value = "瓦片Z值", required = true, dataType = "int")
    })
    @RequestMapping(value="/contour/{x}/{y}/{z}.json",method = RequestMethod.GET)
    public ResponseEntity getContour(
            @PathVariable("x") long x,
            @PathVariable("y") long y,
            @PathVariable("z") int z){
        Tile tile=new Tile(x,y,z);
        JSONObject body=xyzService.getJingZhuangContourByTile(tile);
        if(body!=null)return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(body);
        return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
    }

}

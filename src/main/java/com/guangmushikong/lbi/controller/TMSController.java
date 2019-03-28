package com.guangmushikong.lbi.controller;

import com.guangmushikong.lbi.service.TMSService;
import com.lbi.model.Tile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(value = "TMS地图服务", tags = "TMS", description = "TMS协议瓦片地图服务相关接口")
@RestController
@RequestMapping("/tms")
public class TMSController {
    @Resource(name="tmsService")
    TMSService tmsService;

    @ApiOperation(value = "TMS瓦片地图服务", notes = "获取TMS瓦片")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "version", value = "版本号", required = true, dataType = "string"),
            @ApiImplicitParam(name = "tileset", value = "数据集名", required = true, dataType = "string"),
            @ApiImplicitParam(name = "z", value = "瓦片Z值", required = true, dataType = "int"),
            @ApiImplicitParam(name = "x", value = "瓦片X值", required = true, dataType = "long"),
            @ApiImplicitParam(name = "y", value = "瓦片Y值", required = true, dataType = "long"),
            @ApiImplicitParam(name = "extension", value = "后缀", required = true, dataType = "string"),
    })
    @RequestMapping(value="/{version}/{tileset}/{z}/{x}/{y}.{extension}",method = RequestMethod.GET)
    public ResponseEntity getTile(
            @PathVariable("version") String version,
            @PathVariable("tileset") String tileset,
            @PathVariable("z") int z,
            @PathVariable("x") long x,
            @PathVariable("y") long y,
            @PathVariable("extension") String extension) {
        Tile tile=new Tile(x,y,z);
        String[] args=tileset.split("@");
        try{
            if(extension.equalsIgnoreCase("json")){

            }else if(extension.equalsIgnoreCase("png")){
                byte[] bytes=tmsService.getTMS_Tile(version,args[0],args[1],args[2],tile);
                return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(bytes);
            }else if(extension.equalsIgnoreCase("jpeg")){
                byte[] bytes=tmsService.getTMS_Tile(version,args[0],args[1],args[2],tile);
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
            }else if(extension.equalsIgnoreCase("tif")){
                byte[] bytes=tmsService.getTMS_Tile(version,args[0],args[1],args[2],tile);
                return ResponseEntity.ok().contentType(MediaType.valueOf("image/tif")).body(bytes);
            }else if(extension.equalsIgnoreCase("terrain")){
                byte[] bytes=tmsService.getTMS_Tile(version,args[0],args[1],args[2],tile);
                return ResponseEntity.ok().contentType(MediaType.valueOf("application/vnd.quantized-mesh")).body(bytes);
            }
        }catch (Exception e){
            e.printStackTrace();
            //log.error(e.getMessage());
        }
        return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value="/{version}/{tileset}/layer.json",method = RequestMethod.GET)
    public ResponseEntity getLayer(@PathVariable("version") String version,
                                   @PathVariable("tileset") String tileset){
        String[] args=tileset.split("@");
        byte[] bytes=tmsService.getLayer(version,args[0],args[1],args[2]);
        if(bytes!=null)return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(bytes);
        else return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
    }
}

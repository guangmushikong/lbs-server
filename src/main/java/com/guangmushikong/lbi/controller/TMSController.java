package com.guangmushikong.lbi.controller;

import com.guangmushikong.lbi.model.ServiceType;
import com.guangmushikong.lbi.model.TileMap;
import com.guangmushikong.lbi.service.TileService;
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
import java.io.IOException;

@Api(value = "TMS地图服务", tags = "TMS", description = "TMS协议瓦片地图服务相关接口")
@RestController
@RequestMapping("/tms")
@Slf4j
public class TMSController {
    @Resource(name="tileService")
    TileService tileService;

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
        try{
            Tile tile=new Tile(x,y,z);
            String[] args=tileset.split("@");
            TileMap tileMap=tileService.getTileMapById(ServiceType.TMS.getValue(),args[0],args[1],args[2]);
            byte[] bytes=tileService.getTile(tileMap,tile);
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
}

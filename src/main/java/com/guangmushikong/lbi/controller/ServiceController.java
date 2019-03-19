
package com.guangmushikong.lbi.controller;


import com.guangmushikong.lbi.model.xml.Root_Services;
import com.guangmushikong.lbi.model.xml.Root_TileMap;
import com.guangmushikong.lbi.model.xml.Root_TileMapService;
import com.guangmushikong.lbi.service.MetaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(value = "瓦片服务资源接口", tags = "Service", description = "瓦片服务资源相关接口")
@RestController
@RequestMapping("/service")
public class ServiceController {
    @Resource(name="metaService")
    MetaService metaService;

    @ApiOperation(value = "地图服务类型列表", notes = "获取地图服务类型列表", produces = "application/xml")
    @RequestMapping(value="", method = RequestMethod.GET)
    public ResponseEntity getService() {
        Root_Services u = metaService.getServices();
        if (u != null) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(u);
        }
        return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "地图服务列表", notes = "获取地图服务列表", produces = "application/xml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "service", value = "服务名", required = true, dataType = "string"),
            @ApiImplicitParam(name = "version", value = "版本号", required = true, dataType = "string")
    })
    @RequestMapping(value="/{service}/{version}", method = RequestMethod.GET)
    public ResponseEntity getTileMapService(
            @PathVariable("service") String service,
            @PathVariable("version") String version) {
        Root_TileMapService u=null;
        if(service.equalsIgnoreCase("xyz")){
            u = metaService.getTileMapService(1,version);
        }else if(service.equalsIgnoreCase("tms")){
            u = metaService.getTileMapService(2,version);
        }

        if (u != null) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(u);
        }
        return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "地图服务数据集", notes = "获取地图服务数据集", produces = "application/xml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "service", value = "服务名", required = true, dataType = "string"),
            @ApiImplicitParam(name = "version", value = "版本号", required = true, dataType = "string"),
            @ApiImplicitParam(name = "tileset", value = "数据集名", required = true, dataType = "string")
    })
    @RequestMapping(value="/{service}/{version}/{tileset}", method = RequestMethod.GET)
    public ResponseEntity getTileMap(
            @PathVariable("service") String service,
            @PathVariable("version") String version,
            @PathVariable("tileset") String tileset) {
        String[] args=tileset.split("@");
        Root_TileMap u = null;
        if(service.equalsIgnoreCase("xyz")){
            u = metaService.getTileMap(1,version,args[0],args[1],args[2]);
        }else if(service.equalsIgnoreCase("tms")){
            u = metaService.getTileMap(2,version,args[0],args[1],args[2]);
        }

        if (u != null) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(u);
        }
        return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
    }
}


package com.guangmushikong.lbi.controller;


import com.guangmushikong.lbi.model.ServiceType;
import com.guangmushikong.lbi.model.xml.Root_Services;
import com.guangmushikong.lbi.model.xml.Root_TileMap;
import com.guangmushikong.lbi.model.xml.Root_TileMapService;
import com.guangmushikong.lbi.service.MetaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(value = "瓦片服务资源接口", tags = "Service", description = "瓦片服务资源相关接口")
@RestController
public class ServiceController {
    @Resource(name="metaService")
    MetaService metaService;

    @ApiOperation(value = "地图服务类型列表", notes = "获取地图服务类型列表", produces = "application/xml")
    @GetMapping(value = "", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    public Root_Services getService() {
        Root_Services u = metaService.getServices();
        return u;
    }

    @ApiOperation(value = "地图服务列表", notes = "获取地图服务列表", produces = "application/xml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "service", value = "服务名", required = true, dataType = "string"),
            @ApiImplicitParam(name = "version", value = "版本号", required = true, dataType = "string")
    })
    @GetMapping(value = "/{service}/{version}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    public Root_TileMapService getTileMapService(
            @PathVariable("service") String service,
            @PathVariable("version") String version) {
        Root_TileMapService u=null;
        ServiceType type=ServiceType.valueOf(service.toUpperCase());
        if(type==ServiceType.XYZ){
            u = metaService.getTileMapService(type.getValue(),version);
        }else if(type==ServiceType.TMS){
            u = metaService.getTileMapService(type.getValue(),version);
        }
        return u;
    }

    @ApiOperation(value = "地图服务数据集", notes = "获取地图服务数据集", produces = "application/xml")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "service", value = "服务名", required = true, dataType = "string"),
            @ApiImplicitParam(name = "version", value = "版本号", required = true, dataType = "string"),
            @ApiImplicitParam(name = "tileset", value = "数据集名", required = true, dataType = "string")
    })
    @GetMapping(value = "/{service}/{version}/{tileset}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    public Root_TileMap getTileMap(
            @PathVariable("service") String service,
            @PathVariable("version") String version,
            @PathVariable("tileset") String tileset) {
        String[] args=tileset.split("@");
        ServiceType type=ServiceType.valueOf(service.toUpperCase());
        Root_TileMap u = null;
        if(type==ServiceType.XYZ){
            u = metaService.getTileMap(type.getValue(),version,args[0],args[1],args[2]);
        }else if(type==ServiceType.TMS){
            u = metaService.getTileMap(type.getValue(),version,args[0],args[1],args[2]);
        }
        return u;
    }
}

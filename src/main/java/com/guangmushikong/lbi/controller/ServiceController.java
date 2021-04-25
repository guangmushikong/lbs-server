
package com.guangmushikong.lbi.controller;


import com.guangmushikong.lbi.model.enums.ServiceType;
import com.guangmushikong.lbi.model.xml.Root_Services;
import com.guangmushikong.lbi.model.xml.Root_TileMap;
import com.guangmushikong.lbi.model.xml.Root_TileMapService;
import com.guangmushikong.lbi.service.MetaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


/**
 * 瓦片服务资源接口
 */
@RestController
@RequestMapping("/service")
@Slf4j
public class ServiceController {
    @Autowired
    MetaService metaService;

    /**
     * 获取地图服务类型列表
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    public Root_Services getService() {
        Root_Services u = metaService.getServices();
        return u;
    }

    /**
     * 获取地图服务列表
     * @param service 服务名
     * @param version 版本号
     */
    @GetMapping(value = "/{service}/{version}", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    public Root_TileMapService getTileMapService(
            @PathVariable("service") String service,
            @PathVariable("version") String version) {
        log.info("【getTileMapService】service:{},version:{}",service,version);
        Root_TileMapService u=null;
        ServiceType type=ServiceType.valueOf(service.toUpperCase());
        if(type==ServiceType.XYZ){
            u = metaService.getTileMapService(type.getValue(),version);
        }else if(type==ServiceType.TMS){
            u = metaService.getTileMapService(type.getValue(),version);
        }
        return u;
    }

    /**
     * 获取地图服务数据集
     * @param service 服务名
     * @param version 版本号
     * @param tileset 数据集名
     */
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

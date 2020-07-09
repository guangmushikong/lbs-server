package com.guangmushikong.lbi.controller;

import com.guangmushikong.lbi.model.*;
import com.guangmushikong.lbi.service.MetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 元数据接口
 */
@RestController
@RequestMapping("/meta")
public class MetaController {
    @Autowired
    MetaService metaService;

    /**
     * 获取瓦片地图服务列表
     */
    @GetMapping(value = "/maps", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultBody listMap() {
        List<TileMap> list=metaService.getTileMapList();
        return new ResultBody(list);
    }

    /**
     * 获取瓦片地图服务
     * @param mapid 地图ID
     */
    @GetMapping(value = "/maps/{mapid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultBody getMap(@PathVariable("mapid") long mapid) {
        TileMap map=metaService.getTileMapById(mapid);
        return new ResultBody(map);
    }

    /**
     * 获取瓦片数据集列表
     * @param mapid 地图ID
     */
    @GetMapping(value = "/maps/{mapid}/mapsets", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultBody listMapSet(@PathVariable("mapid") long mapid) {
        List<TileSet> list=metaService.getTileSetList(mapid);
        return new ResultBody(list);
    }
}

package com.guangmushikong.lbi.controller;

import com.guangmushikong.lbi.model.*;
import com.guangmushikong.lbi.service.MetaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "元数据接口", tags = "Meta", description = "元数据相关接口")
@RestController
@RequestMapping("/meta")
public class MetaController {
    @Resource(name="metaService")
    MetaService metaService;


    @ApiOperation(value = "瓦片地图服务列表", notes = "获取瓦片地图服务列表", produces = "application/json")
    @RequestMapping(value="/maps", method = RequestMethod.GET)
    public ResultBody getMapList() {
        List<TileMap> list=metaService.getTileMapList();
        return new ResultBody(list);
    }

    @ApiOperation(value = "瓦片地图服务", notes = "获取瓦片地图服务", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mapid", value = "地图ID", required = true, dataType = "long")
    })
    @RequestMapping(value="/maps/{mapid}", method = RequestMethod.GET)
    public ResultBody getMapById(@PathVariable("mapid") long mapid) {
        TileMap map=metaService.getTileMapById(mapid);
        return new ResultBody(map);
    }

    @ApiOperation(value = "瓦片数据集列表", notes = "获取瓦片数据集列表", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mapid", value = "地图ID", required = true, dataType = "long")
    })
    @RequestMapping(value="/maps/{mapid}/mapsets", method = RequestMethod.GET)
    public ResultBody getMapSetList(@PathVariable("mapid") long mapid) {
        List<TileSet> list=metaService.getTileSetList(mapid);
        return new ResultBody(list);
    }
}

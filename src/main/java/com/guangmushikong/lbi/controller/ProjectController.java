
package com.guangmushikong.lbi.controller;


import com.guangmushikong.lbi.model.*;
import com.guangmushikong.lbi.service.MetaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "项目管理接口", tags = "Project", description = "项目管理管理相关接口")
@RestController
@RequestMapping("/projects")
public class ProjectController {
    @Resource(name="metaService")
    MetaService metaService;

    @ApiOperation(value = "项目列表", notes = "获取项目列表", produces = "application/json")
    @RequestMapping(value="", method = RequestMethod.GET)
    public ResultBody getProjectList() {
        List<ProjectDO> list=metaService.getProjectList();
        ResultBody result=new ResultBody();
        if(!list.isEmpty())result.setData(list);
        return result;
    }

    @ApiOperation(value = "项目信息", notes = "获取项目信息", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目ID", required = true, dataType = "long")
    })
    @RequestMapping(value="/{projectId}", method = RequestMethod.GET)
    public ResultBody getProject(@PathVariable("projectId") long projectId) {
        ProjectDO result=metaService.getProjectById(projectId);
        return new ResultBody(result);
    }

    @ApiOperation(value = "数据集列表", notes = "获取数据集列表", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目ID", required = true, dataType = "long")
    })
    @RequestMapping(value="/{projectId}/datasets", method = RequestMethod.GET)
    public ResultBody getDatasetList(@PathVariable("projectId") long projectId) {
        List<DataSetDO> list=metaService.getDataSetList(projectId);
        return new ResultBody(list);
    }

    @ApiOperation(value = "数据集信息", notes = "获取数据集信息", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目ID", required = true, dataType = "long"),
            @ApiImplicitParam(name = "datasetId", value = "数据集ID", required = true, dataType = "long")
    })
    @RequestMapping(value="/{projectId}/datasets/{datasetId}", method = RequestMethod.GET)
    public ResultBody getDataset(
            @PathVariable("projectId") long projectId,
            @PathVariable("datasetId") long datasetId) {
        DataSetDO result=metaService.getDataSetById(datasetId);
        return new ResultBody(result);
    }

}

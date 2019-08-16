
package com.guangmushikong.lbi.controller;

import com.guangmushikong.lbi.config.JwtTokenFilter;
import com.guangmushikong.lbi.model.*;
import com.guangmushikong.lbi.service.MetaService;
import com.guangmushikong.lbi.util.JwtTokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(value = "项目管理接口", tags = "Project", description = "项目管理管理相关接口")
@RestController
@RequestMapping("/projects")
public class ProjectController {
    @Autowired
    MetaService metaService;

    @ApiOperation(value = "项目列表", notes = "获取项目列表", produces = "application/json")
    @GetMapping("")
    public ResultBody getProjectList(HttpServletRequest request) {
        String token=request.getHeader(JwtTokenFilter.HEADER_STRING);
        String username = JwtTokenUtil.getUsernameFromToken(token);
        List<ProjectDO> list;
        if(StringUtils.isNotEmpty(username)){
            list=metaService.getProjectList(username);
        }else {
            list=metaService.getProjectList();
        }
        return new ResultBody(list);
    }

    @ApiOperation(value = "项目信息", notes = "获取项目信息", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目ID", required = true, dataType = "long")
    })
    @GetMapping("/{projectId}")
    public ResultBody getProject(@PathVariable("projectId") long projectId) {
        ProjectDO result=metaService.getProjectById(projectId);
        return new ResultBody(result);
    }

    @ApiOperation(value = "数据集列表", notes = "获取数据集列表", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目ID", required = true, dataType = "long")
    })
    @GetMapping("/{projectId}/datasets")
    public ResultBody getDatasetList(@PathVariable("projectId") long projectId) {
        List<DataSetDO> list=metaService.getDataSetList(projectId);
        return new ResultBody(list);
    }

    @ApiOperation(value = "全部数据集列表", notes = "获取数据集列表", produces = "application/json")
    @GetMapping("/{projectId}/alldatasets")
    public ResultBody getAllatasetList(@PathVariable("projectId") long projectId) {
        List<DataSetDO> list=metaService.getDataSetList();
        return new ResultBody(list);
    }

    @ApiOperation(value = "数据集信息", notes = "获取数据集信息", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectId", value = "项目ID", required = true, dataType = "long"),
            @ApiImplicitParam(name = "datasetId", value = "数据集ID", required = true, dataType = "long")
    })
    @GetMapping("/{projectId}/datasets/{datasetId}")
    public ResultBody getDataset(
            @PathVariable("projectId") long projectId,
            @PathVariable("datasetId") long datasetId) {
        DataSetDO result=metaService.getDataSetById(datasetId);
        return new ResultBody(result);
    }

}

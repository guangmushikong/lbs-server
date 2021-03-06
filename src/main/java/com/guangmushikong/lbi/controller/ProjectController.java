
package com.guangmushikong.lbi.controller;

import com.guangmushikong.lbi.config.JwtTokenFilter;
import com.guangmushikong.lbi.model.*;
import com.guangmushikong.lbi.service.MetaService;
import com.guangmushikong.lbi.util.JwtTokenUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 项目管理接口
 */
@RestController
@RequestMapping("/projects")
public class ProjectController {
    @Autowired
    MetaService metaService;

    /**
     * 获取项目列表
     */
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

    /**
     * 获取项目信息
     * @param projectId 项目ID
     */
    @GetMapping("/{projectId}")
    public ResultBody getProject(@PathVariable("projectId") long projectId) {
        ProjectDO result=metaService.getProjectById(projectId);
        return new ResultBody(result);
    }

    /**
     * 获取数据集列表
     * @param projectId 项目ID
     */
    @GetMapping("/{projectId}/datasets")
    public ResultBody getDatasetList(@PathVariable("projectId") long projectId) {
        List<DataSetDO> list=metaService.getDataSetList(projectId);
        return new ResultBody(list);
    }

    /**
     * 获取全部数据集列表
     * @param projectId 项目ID
     */
    @GetMapping("/{projectId}/alldatasets")
    public ResultBody getAllatasetList(@PathVariable("projectId") long projectId) {
        List<DataSetDO> list=metaService.getDataSetList();
        return new ResultBody(list);
    }

    /**
     * 获取数据集信息
     * @param projectId 项目ID
     * @param datasetId 数据集ID
     */
    @GetMapping("/{projectId}/datasets/{datasetId}")
    public ResultBody getDataset(
            @PathVariable("projectId") long projectId,
            @PathVariable("datasetId") long datasetId) {
        DataSetDO result=metaService.getDataSetById(datasetId);
        return new ResultBody(result);
    }

}

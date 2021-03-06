
package com.guangmushikong.lbi.controller;

import com.guangmushikong.lbi.config.JwtTokenFilter;
import com.guangmushikong.lbi.model.UserDataDO;
import com.guangmushikong.lbi.model.ResultBody;
import com.guangmushikong.lbi.service.UserDataService;
import com.guangmushikong.lbi.service.UserService;
import com.guangmushikong.lbi.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/*************************************
 * Class Name: DataController
 * Description:〈数据管理接口〉
 * @author deyi
 * @since 1.0.0
 ************************************/
@RestController
@RequestMapping("/data")
@Slf4j
public class DataController {
    @Autowired
    UserService userService;
    @Autowired
    UserDataService userDataService;

    @GetMapping(value="/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultBody listProject(@RequestParam("projectId") long projectId) {
        List<UserDataDO> list=userDataService.listUserData(projectId);
        return new ResultBody<>(list);
    }

    @PostMapping(value="/batchSave", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultBody batchSaveUserData(
            @RequestBody List<UserDataDO> customs,
            HttpServletRequest request) {
        try{
            String token=request.getHeader(JwtTokenFilter.HEADER_STRING);
            String username = JwtTokenUtil.getUsernameFromToken(token);
            for(UserDataDO custom: customs){
                custom.setUserName(username);
                userDataService.saveUserData(custom);
            }
            return new ResultBody<>(0,"OK");
        }catch (Exception e){
            e.printStackTrace();
            return new ResultBody<>(-1,e.getMessage());
        }
    }

    @PostMapping(value="/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultBody saveUserData(
            @RequestBody UserDataDO custom,
            HttpServletRequest request) {
        try{
            String token=request.getHeader(JwtTokenFilter.HEADER_STRING);
            String username = JwtTokenUtil.getUsernameFromToken(token);
            custom.setUserName(username);
            userDataService.saveUserData(custom);
            return new ResultBody<>(0,"OK");
        }catch (Exception e){
            e.printStackTrace();
            return new ResultBody<>(-1,e.getMessage());
        }
    }

    @DeleteMapping(value="/del", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultBody delCustomData(
            @RequestParam("uuid") String uuid,
            @RequestParam("projectId") long projectId){
        userDataService.delUserData(uuid,projectId);
        return new ResultBody<>(0,"OK");
    }

    @PostMapping(value="/imgUpload")
    public ResultBody imgUpload(
            @RequestParam("projectId") long projectId,
            @RequestParam("file") MultipartFile file){
        try{
            if (file != null) {
                //获取上传原图片名称
                String fileName = file.getOriginalFilename();
                log.info("【imgUpload】pid:{},fileName:{}",projectId,fileName);
                String savePath=userDataService.saveJpg(projectId,fileName,file.getBytes());
                String fileUrl=String.format("http://111.202.109.211:8080/image/project/%d/img/%s",projectId,fileName);
                return new ResultBody<>(0,"OK",fileUrl);
            }else {
                return new ResultBody<>(-1,"没有上传文件");
            }
        }catch (IOException e){
            e.printStackTrace();
            return new ResultBody<>(-1,e.getMessage());
        }
    }

    @PostMapping(value="/kmlUpload")
    public ResultBody kmlUpload(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request){
        try{
            String token=request.getHeader(JwtTokenFilter.HEADER_STRING);
            String username = JwtTokenUtil.getUsernameFromToken(token);
            if (file != null) {
                //获取上传原图片名称
                String fileName = file.getOriginalFilename();
                log.info("【kmlUpload】fileName:{}",fileName);
                int total=userDataService.kml2PgTable(username,file.getInputStream());
                return new ResultBody<>(0,"OK",total);
            }else {
                return new ResultBody<>(-1,"没有上传文件");
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ResultBody<>(-1,e.getMessage());
        }
    }

    @GetMapping("/syncShp")
    public ResultBody syncShp(@RequestParam("layerName") String layerName) {
        try{
            userDataService.shp2PgTable(layerName);
            return new ResultBody<>(0,"OK");
        }catch (Exception e){
            e.printStackTrace();
            return new ResultBody<>(-1,e.getMessage());
        }
    }

    @GetMapping("/downKml")
    public void downKml(
            @RequestParam("type") String type,
            HttpServletRequest request,
            HttpServletResponse response) {
        if (response == null || type == null || type.equals("")) {
            return;
        }
        response.setContentType("application/x-msdownload");
        String token=request.getHeader(JwtTokenFilter.HEADER_STRING);
        String username = JwtTokenUtil.getUsernameFromToken(token);
        String fileName=String.format("%s_%s.kml",username,type);
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
        Long filesize=userDataService.getKmlFileSize(username,type);
        log.info("文件大小{}",filesize);
        response.addHeader("Content-Length",filesize.toString());
        ServletOutputStream sos = null;
        try {
            sos = response.getOutputStream();
            userDataService.getKmlFile(username,type,sos);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}

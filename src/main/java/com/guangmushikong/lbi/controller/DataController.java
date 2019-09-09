
package com.guangmushikong.lbi.controller;

import com.guangmushikong.lbi.config.JwtTokenFilter;
import com.guangmushikong.lbi.model.CustomVO;
import com.guangmushikong.lbi.model.ResultBody;
import com.guangmushikong.lbi.service.CustomDataSetService;
import com.guangmushikong.lbi.service.UserService;
import com.guangmushikong.lbi.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
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
    CustomDataSetService customDataSetService;

    @GetMapping(value="/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultBody listProject(@RequestParam("projectId") long projectId) {
        List<CustomVO> list=customDataSetService.listCustomDataSet(projectId);
        return new ResultBody<>(list);
    }

    @PostMapping(value="/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultBody addCustomData(
            @RequestBody CustomVO custom,
            HttpServletRequest request) {
        try{
            String token=request.getHeader(JwtTokenFilter.HEADER_STRING);
            String username = JwtTokenUtil.getUsernameFromToken(token);
            custom.setUserName(username);
            customDataSetService.addCustomDataSet(custom);
            return new ResultBody<>(0,"OK");
        }catch (Exception e){
            e.printStackTrace();
            return new ResultBody<>(-1,e.getMessage());
        }
    }

    @PostMapping(value="/batchAdd", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultBody batchAddCustomData(
            @RequestBody List<CustomVO> customs,
            HttpServletRequest request) {
        try{
            String token=request.getHeader(JwtTokenFilter.HEADER_STRING);
            String username = JwtTokenUtil.getUsernameFromToken(token);
            for(CustomVO custom: customs){
                custom.setUserName(username);
                customDataSetService.addCustomDataSet(custom);
            }
            return new ResultBody<>(0,"OK");
        }catch (Exception e){
            e.printStackTrace();
            return new ResultBody<>(-1,e.getMessage());
        }
    }

    @PostMapping(value="/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultBody updateCustomData(
            @RequestBody CustomVO custom,
            HttpServletRequest request) {
        try{
            String token=request.getHeader(JwtTokenFilter.HEADER_STRING);
            String username = JwtTokenUtil.getUsernameFromToken(token);
            custom.setUserName(username);
            customDataSetService.updateCustomDataSet(custom);
            return new ResultBody<>(0,"OK");
        }catch (Exception e){
            e.printStackTrace();
            return new ResultBody<>(-1,e.getMessage());
        }
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
                String savePath=customDataSetService.saveJpg(projectId,fileName,file.getBytes());
                return new ResultBody<>(0,"OK",savePath);
            }else {
                return new ResultBody<>(-1,"没有上传文件");
            }
        }catch (IOException e){
            e.printStackTrace();
            return new ResultBody<>(-1,e.getMessage());
        }
    }

    @GetMapping(value="/imgGet")
    public ResponseEntity imgGet(
            @RequestParam("projectId") long projectId,
            @RequestParam("fileName") String fileName){
        log.info("【imgGet】pid:{},fileName:{}",projectId,fileName);
        try{
            String suffix=getFileSuffix(fileName);
            ResponseEntity.BodyBuilder bodyBuilder=ResponseEntity.ok();
            byte[] bytes=customDataSetService.getFile(projectId,fileName);
            if("jpg".equalsIgnoreCase(suffix)){
                bodyBuilder.contentType(MediaType.IMAGE_JPEG);
            }else if("jpeg".equalsIgnoreCase(suffix)){
                bodyBuilder.contentType(MediaType.IMAGE_JPEG);
            }else if("png".equalsIgnoreCase(suffix)){
                bodyBuilder.contentType(MediaType.IMAGE_PNG);
            }else{
                bodyBuilder.contentType(MediaType.IMAGE_JPEG);
            }
            return bodyBuilder.body(bytes);
        }catch (IOException e){
            e.printStackTrace();
        }
        return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
    }

    private String getFileSuffix(String name) {
        return name.substring(name.lastIndexOf(".")+1, name.length());
    }
}

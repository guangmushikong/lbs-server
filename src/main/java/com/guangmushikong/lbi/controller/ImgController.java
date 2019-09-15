/* *************************************
 * Copyright (C), Navinfo
 * Package: com.guangmushikong.lbi.controller
 * Author: liumingkai
 * Date: Created in 2019/9/10 10:49
 **************************************/
package com.guangmushikong.lbi.controller;

import com.guangmushikong.lbi.service.UserDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/*************************************
 * Class Name: ImgController
 * Description:〈ImgController〉
 * @author liumingkai
 * @since 1.0.0
 ************************************/
@RestController
@RequestMapping("/image")
@Slf4j
public class ImgController {
    @Autowired
    UserDataService customDataSetService;

    @GetMapping(value="/project/{projectId}/img/{imgName}")
    public ResponseEntity imgGet(
            @PathVariable("projectId") long projectId,
            @PathVariable("imgName") String imgName){
        log.info("【imgGet】pid:{},imgName:{}",projectId,imgName);
        try{
            String suffix=getFileSuffix(imgName);
            ResponseEntity.BodyBuilder bodyBuilder=ResponseEntity.ok();
            byte[] bytes=customDataSetService.getFile(projectId,imgName);
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

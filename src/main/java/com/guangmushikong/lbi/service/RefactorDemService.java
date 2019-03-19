
package com.guangmushikong.lbi.service;


import com.guangmushikong.lbi.model.ContourPoint;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("lbi-dem")
public interface RefactorDemService {
    @RequestMapping(value="/dem/contour/jingzhuang")
    List<ContourPoint> getHeight_jingzhuang(@RequestBody List<ContourPoint> list)throws Exception;

    @RequestMapping(value="/dem/contour/gujiao")
    List<ContourPoint> getHeight_gujiao(@RequestBody List<ContourPoint> list)throws Exception;
}

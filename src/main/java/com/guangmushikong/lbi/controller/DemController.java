package com.guangmushikong.lbi.controller;

import com.guangmushikong.lbi.model.ContourPoint;
import com.guangmushikong.lbi.model.ResultBody;
import com.guangmushikong.lbi.service.DemService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import static com.guangmushikong.lbi.model.LBSConstants.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Dem数据接口
 */
@RestController
@RequestMapping("/dem")
public class DemController {
    @Autowired
    DemService demService;

    /**
     * 获取等高线列表
     * @param layerName 图层
     * @param xys 剖面曲线
     */
    @GetMapping(value="/contour",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultBody contour(
            @RequestParam(value = "layerName",defaultValue = "") String layerName,
            @RequestParam("xys") String xys) {
        try{
            if(StringUtils.isNoneEmpty(xys)){
                String[] arr=xys.split(";");
                List<ContourPoint> ptList=new ArrayList<>();
                for(String tmp:arr){
                    String[] pts=tmp.split(",");
                    double x=Double.parseDouble(pts[0]);
                    double y=Double.parseDouble(pts[1]);
                    ContourPoint pt=new ContourPoint();
                    pt.setLongitude(x);
                    pt.setLatitude(y);
                    pt.setKind(1);
                    ptList.add(pt);
                }
                if(StringUtils.isEmpty(layerName)){
                    layerName=GUJIAO;
                }
                if(ptList.size()>0){
                    List<ContourPoint> list=demService.listContourPoint(ptList,layerName);
                    return new ResultBody<>(list);
                }else{
                    return new ResultBody<>(-1,"xys数据不能为空");
                }
            }else {
                return new ResultBody<>(-1,"xys不能为空");
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ResultBody<>(-1,e.getMessage());
        }
    }
}

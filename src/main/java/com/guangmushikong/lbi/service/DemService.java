package com.guangmushikong.lbi.service;

import com.guangmushikong.lbi.dao.DemDao;
import com.guangmushikong.lbi.model.ContourPoint;
import lombok.extern.slf4j.Slf4j;
import org.geotools.coverage.grid.GridCoordinates2D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/*************************************
 * Class Name: DemService
 * Description:〈DemService〉
 * @author deyi
 * @since 1.0.0
 ************************************/
@Service
@Slf4j
public class DemService {
    @Autowired
    DemDao demDao;

    public List<ContourPoint> listContourPoint(List<ContourPoint> ptList,String demName)throws Exception{
        //经纬度转像素坐标
        List<ContourPoint> pixelList=new ArrayList<>();
        for(ContourPoint point:ptList){
            GridCoordinates2D pixel=demDao.point2Pixel(point.getLongitude(),point.getLatitude(),demName);
            if(pixel!=null){
                point.setX(pixel.x);
                point.setY(pixel.y);
                pixelList.add(point);
            }
        }
        //log.info("【listContourPoint】demName:{},ptList:{},pixelList:{}",demName,ptList.size(),pixelList.size());
        //线内插值
        List<ContourPoint> list=new ArrayList<>();
        if(pixelList.size()>1){
            //首节点
            ContourPoint point=pixelList.get(0);
            double height=demDao.getHeightByPixel(point.getX(),point.getY(),demName);
            point.setHeight((int)height);
            list.add(point);

            for(int i=1;i<pixelList.size();i++){
                ContourPoint point1=pixelList.get(i-1);
                ContourPoint point2=pixelList.get(i);
                height=demDao.getHeightByPixel(point2.getX(),point2.getY(),demName);
                point2.setHeight((int)height);

                //线两端点之间插值
                List<GridCoordinates2D> scanPixellist=demDao.getPixelListByScanLine(point1.getX(),point1.getY(),point2.getX(),point2.getY());
                List<ContourPoint> scanPointList=demDao.fixPoint(scanPixellist,point1,point2,demName);
                list.addAll(scanPointList);

                list.add(point2);
            }
        }else {
            ContourPoint point=pixelList.get(0);
            double height=demDao.getHeightByPixel(point.getX(),point.getY(),demName);
            point.setHeight((int)height);
            list.add(point);
        }
        return list;
    }
}

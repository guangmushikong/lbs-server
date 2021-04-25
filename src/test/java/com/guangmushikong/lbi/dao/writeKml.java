package com.guangmushikong.lbi.dao;

import com.guangmushikong.lbi.model.KmlDO;
import com.guangmushikong.lbi.util.KmlUtil;
import com.vividsolutions.jts.geom.Geometry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class writeKml {
    @Autowired
    UserDataDao userDataDao;

    @Test
    public void test1()throws Exception{
        String path="F:/data/kml/point2.kml";
        String userName="admin";
        String type="Point";
        List<KmlDO> list=userDataDao.listKml(userName,type);
        log.info("list:{}",list.size());
        List<Geometry> geometries=new ArrayList();
        for(KmlDO kmlDO:list){

            geometries.add(kmlDO.getGeometry());
        }
        KmlUtil.write2Wml(path,type,geometries);
    }

    @Test
    public void test2()throws Exception{
        String path="F:/data/kml/point3.kml";
        String userName="admin";
        String type="Point";
        List<KmlDO> list=userDataDao.listKml(userName,type);
        log.info("list:{}",list.size());
        List<String> attrKeys=new ArrayList();
        attrKeys.add("name");
        List<Map<String,Object>> geomList = new ArrayList<>();
        for(KmlDO kmlDO:list){
            Map<String,Object> item = new HashMap<>();
            if(StringUtils.isNotEmpty(kmlDO.getName())){
                item.put("name",kmlDO.getName());
            }else {
                item.put("name","");
            }
            item.put("geoKey",kmlDO.getGeometry());
            geomList.add(item);
        }
        KmlUtil.write2Wml(path,type,"geoKey",attrKeys,geomList);
    }


}

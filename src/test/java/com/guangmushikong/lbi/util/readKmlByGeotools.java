package com.guangmushikong.lbi.util;

import com.guangmushikong.lbi.model.Tile;
import com.vividsolutions.jts.geom.Geometry;
import lombok.extern.slf4j.Slf4j;
import org.geotools.kml.KMLConfiguration;
import org.geotools.xml.PullParser;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class readKmlByGeotools {

    @Test
    public void test1(){
        String url="http://webrd02.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=8&x={x}&y={y}&z={z}";
        Tile tile=new Tile(53815,24897,16);
        url=url.replace("{x}",String.valueOf(tile.getX()));
        url.replace("{y}",String.valueOf(tile.getY()));
        url.replace("{z}",String.valueOf(tile.getZ()));
        log.info("url:"+url);
    }
    @Test
    public void readPoint()throws Exception{
        String path="F:/data/kml/point.kml";
        //String path="F:/data/kml/line.kml";
        //String path="F:/data/kml/polygon.kml";
        FileInputStream reader = new FileInputStream(path);
        PullParser parser = new PullParser(new KMLConfiguration(), reader, SimpleFeature.class);
        List<SimpleFeature> features = new ArrayList<>();
        SimpleFeature simpleFeature = (SimpleFeature) parser.parse();
        while (simpleFeature != null) {
            log.info(simpleFeature.getFeatureType().getTypeName());
            if("placemark".equalsIgnoreCase(simpleFeature.getFeatureType().getTypeName())){
                features.add(simpleFeature);
            }else if("placemark".equalsIgnoreCase(simpleFeature.getFeatureType().getTypeName())){
                features.add(simpleFeature);
            }
            simpleFeature = (SimpleFeature) parser.parse();
        }
        for(SimpleFeature feature:features){
            //System.out.println(feature);
            String id=feature.getID();
            Object name=feature.getAttribute("name");
            String aname="";
            if(name!=null)aname=name.toString();
            SimpleFeatureType type=feature.getFeatureType();
            Geometry geometry=(Geometry)feature.getDefaultGeometry();
            log.info("id:{},type:{},name:{},geom:{}",id,type.getTypeName(),aname,geometry.getGeometryType());
           /* List<AttributeType> attributeTypes=type.getTypes();
            for(AttributeType aType:attributeTypes){
                log.info(aType.toString()+"|"+aType.getName().toString());
            }*/

        }
    }
}

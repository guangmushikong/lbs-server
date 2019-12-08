/* *************************************
 * Copyright (C), Navinfo
 * Package: com.guangmushikong.lbi
 * Author: liumingkai
 * Date: Created in 2019/11/11 9:56
 **************************************/
package com.guangmushikong.lbi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.junit.Test;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

/*************************************
 * Class Name: testShp
 * Description:〈testShp〉
 * @author liumingkai
 * @since 1.0.0
 ************************************/
@Slf4j
public class testShp {
    @Test
    public void 读取shp(){
        String tableName="test";
        String filePath="E:/data/liupanshui_point/点位.shp";
        filePath="E:/data/1：100万矢量图/LRRL.shp";
        readShp(tableName,filePath);
        /*int[] types=IntStream.range(0, 4).map(u-> Types.VARCHAR).toArray();
        for(int t:types){
            System.out.println(t);
        }*/
    }

    private void readShp(String tableName,String filePath){
        ShapefileDataStore shpDataStore = null;
        try{
            shpDataStore = new ShapefileDataStore(new File(filePath).toURI().toURL());
            shpDataStore.setCharset(Charset.forName("GBK"));
            String typeName = shpDataStore.getTypeNames()[0];
            log.info("【typeName】"+typeName);
            SimpleFeatureType simpleFeatureType=shpDataStore.getSchema();
            GeometryType geometryType=simpleFeatureType.getGeometryDescriptor().getType();
            log.info("【geometryType】"+geometryType.getName());
            List<Name> fieldNames=new ArrayList<>();
            for(int i=0;i<simpleFeatureType.getAttributeCount();i++){
                AttributeType attributeType=simpleFeatureType.getType(i);
                //log.info(i+"|"+attributeType.getName());
                if(geometryType.getName()!=attributeType.getName()){
                    fieldNames.add(attributeType.getName());
                }
            }
            String createTableSql=getBuildTableSql(tableName,simpleFeatureType);
            log.info("【creatTable】"+createTableSql);
            FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = shpDataStore.getFeatureSource(typeName);
            FeatureCollection<SimpleFeatureType, SimpleFeature> result = featureSource.getFeatures();
            log.info("【total】"+result.size());
            FeatureIterator<SimpleFeature> itertor = result.features();

            /*while(itertor.hasNext()){
                SimpleFeature feature = itertor.next();
                String iSql=getInsertSql(tableName,feature,fieldNames);
                log.info("【sql】"+iSql);
                *//*Geometry geometry = (Geometry) feature.getDefaultGeometry();
                String wkt=geometry.toText();
                JSONObject item=new JSONObject();
                for(int k=0;k<fieldNames.size();k++){
                    Name fieldName=fieldNames.get(k);
                    item.put(fieldName.toString(),feature.getAttribute(fieldName).toString());
                }
                log.info("【row】"+item.toJSONString());*//*
            }*/
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getBuildTableSql(String tableName,SimpleFeatureType type){
        StringBuilder sb=new StringBuilder();
        sb.append("create table data.").append(tableName);
        sb.append("(id bigserial primary key,");
        GeometryType geometryType=type.getGeometryDescriptor().getType();
        log.info("【geometryType】"+geometryType.getName());
        if(geometryType.equals("MultiLineString"))
        for(int i=0;i<type.getAttributeCount();i++){
            AttributeType attributeType=type.getType(i);
            if(geometryType.getName()!=attributeType.getName()){
                Name fieldName=attributeType.getName();
                sb.append(fieldName.toString().toLowerCase()).append(" text,");
            }
        }
        sb.append("geom geometry)");

        return sb.toString();
    }

    private String getInsertSql(String tableName,SimpleFeature feature,List<Name> fieldNames) {
        Geometry geometry = (Geometry) feature.getDefaultGeometry();
        List<String> names = new ArrayList<>();
        List<String> vals = new ArrayList<>();
        for (Name fieldName : fieldNames) {
            names.add(fieldName.toString().toLowerCase());
            vals.add("'" + feature.getAttribute(fieldName).toString() + "'");
        }

        return String.format("insert into %s(%s,geom) values(%s,ST_GeomFromText(%s,4326))",
                tableName,
                StringUtils.join(names, ","),
                StringUtils.join(vals, ","),
                geometry.toText());
    }
}

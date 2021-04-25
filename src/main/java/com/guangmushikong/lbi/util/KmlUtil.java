package com.guangmushikong.lbi.util;


import com.vividsolutions.jts.geom.*;
import lombok.extern.slf4j.Slf4j;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.kml.v22.KML;
import org.geotools.kml.v22.KMLConfiguration;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.xml.Encoder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class KmlUtil {


    /**
     * 生成wml文件
     * @param wmlPath wml文件路径（包含文件名称） 如：D:\data\tmp\ceshi.kml
     * @param type 图幅元素类型
     * @param geometries 图幅集合
     * @throws Exception
     */
    public static void write2Wml(String wmlPath, String type, List<Geometry> geometries) throws Exception {
        FileOutputStream fos = new FileOutputStream(wmlPath,true);
        String str = write2WmlStr(type, geometries);
        //true表示在文件末尾追加
        fos.write(str.getBytes());
        fos.close();
    }

    /**
     * 生成wml文件
     * @param wmlPath wml文件路径（包含文件名称） 如：D:\data\tmp\ceshi.kml
     * @param type 图幅元素类型
     * @param geoKey data中图幅的key
     * @param attrKeys 属性key集合
     * @param geomList 图幅和属性集合
     * @throws Exception
     */
    public static void write2Wml(String wmlPath, String type, String geoKey, List<String> attrKeys, List<Map<String, Object>> geomList) throws Exception {
        FileOutputStream fos = new FileOutputStream(wmlPath,true);
        String str = write2WmlStr(type, geoKey, attrKeys, geomList);
        //true表示在文件末尾追加
        fos.write(str.getBytes());
        fos.close();
    }

    /**
     * 生成wml格式的字符串
     * @param type 图幅元素类型
     * @param geometrys 图幅集合
     * @return 字符串
     * @throws Exception
     */
    public static String write2WmlStr(String type, List<Geometry> geometrys) throws Exception {
        ByteArrayOutputStream os = write2WmlStream(type, geometrys);
        String out = os.toString().replaceAll("kml:", "");
        return out;
    }

    /**
     * 生成wml格式的字符串
     * @param type 图幅元素类型
     * @param geoKey data中图幅的key
     * @param attrKeys 属性key集合
     * @param geomList 图幅和属性集合
     * @return 字符串
     * @throws Exception
     */
    public static String write2WmlStr(String type, String geoKey, List<String> attrKeys, List<Map<String, Object>> geomList) throws Exception {
        ByteArrayOutputStream os = write2WmlStream(type, geoKey, attrKeys, geomList);
        String out = os.toString().replaceAll("kml:", "");
        return out;
    }

    /**
     * 生成wml格式的字节流
     * @param type 图幅元素类型
     * @param geometrys 图幅元素集合
     * @return 字节流
     * @throws Exception
     */
    public static ByteArrayOutputStream write2WmlStream(String type, List<Geometry> geometrys) throws Exception {
        SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
        tb.setCRS(DefaultGeographicCRS.WGS84);
        tb.setName("kmlfile");
        if ("Polygon".equals(type)) {
            tb.add("the_geom", Polygon.class);
        } else if ("MultiPolygon".equals(type)) {
            tb.add("the_geom", MultiPolygon.class);
        } else if ("Point".equals(type)) {
            tb.add("the_geom", Point.class);
        } else if ("MultiPoint".equals(type)) {
            tb.add("the_geom", MultiPoint.class);
        } else if ("LineString".equals(type)) {
            tb.add("the_geom", LineString.class);
        } else if ("MultiLineString".equals(type)) {
            tb.add("the_geom", MultiLineString.class);
        } else {
            throw new Exception("Geometry中没有该类型：" + type);
        }
        SimpleFeatureType simpleFeatureType=tb.buildFeatureType();
        List<SimpleFeature> features = new ArrayList<>();
        for (Geometry geometry : geometrys) {
            log.info("geom:"+geometry.toText());
            SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(simpleFeatureType);
            sfb.add(geometry);
            //构建要素
            SimpleFeature feature = sfb.buildFeature(null);

            features.add(feature);
        }

        SimpleFeatureCollection fc = DataUtilities.collection(features);
        Encoder encoder = new Encoder(new KMLConfiguration());
        //设置编码
        Charset charset = Charset.forName("utf-8");
        encoder.setEncoding(charset);
        encoder.setIndenting(true);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        encoder.encode(fc, KML.kml, os);
        return os;
    }

    /**
     * 生成wml格式的字节流
     * @param type 图幅元素类型
     * @param geoKey data中图幅的key
     * @param attrKeys 属性key集合
     * @param geomList 图幅和属性集合
     * @return 字节流
     * @throws Exception
     */
    public static ByteArrayOutputStream write2WmlStream(String type, String geoKey, List<String> attrKeys, List<Map<String, Object>> geomList) throws Exception {
        SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
        tb.setCRS(DefaultGeographicCRS.WGS84);
        tb.setName("kmlfile");
        if ("Polygon".equals(type)) {
            tb.add("the_geom", Polygon.class);
        } else if ("MultiPolygon".equals(type)) {
            tb.add("the_geom", MultiPolygon.class);
        } else if ("Point".equals(type)) {
            tb.add("the_geom", Point.class);
        } else if ("MultiPoint".equals(type)) {
            tb.add("the_geom", MultiPoint.class);
        } else if ("LineString".equals(type)) {
            tb.add("the_geom", LineString.class);
        } else if ("MultiLineString".equals(type)) {
            tb.add("the_geom", MultiLineString.class);
        } else {
            throw new Exception("Geometry中没有该类型：" + type);
        }

        //设置用户属性参数
        for (String key : attrKeys) {
            tb.add(key, String.class);
        }
        SimpleFeatureType simpleFeatureType=tb.buildFeatureType();

        List<SimpleFeature> features = new ArrayList<>();
        for (Map<String, Object> obj : geomList) {
            Object geoObj = obj.get(geoKey);
            if (geoObj instanceof Geometry) {
                Geometry geometry = (Geometry) geoObj;
                SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(simpleFeatureType);
                sfb.add(geometry);
                //构建要素
                SimpleFeature feature = sfb.buildFeature(null);

                for (String key : obj.keySet()) {
                    log.info("key:{},val:{}",key,obj.get(key));
                    if (!key.equals(geoKey)) {
                        Object val=obj.get(key);
                        feature.setAttribute(key, val.toString());
                    }
                }
                features.add(feature);
            } else {
                throw new Exception("图幅元素集合中为geoKey:" + geoKey + "的图幅元素类型必须是Geometry");
            }
        }

        SimpleFeatureCollection fc = DataUtilities.collection(features);
        Encoder encoder = new Encoder(new KMLConfiguration());
        //设置编码
        Charset charset = Charset.forName("utf-8");
        encoder.setEncoding(charset);
        encoder.setIndenting(true);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        encoder.encode(fc, KML.kml, os);
        return os;
    }

}

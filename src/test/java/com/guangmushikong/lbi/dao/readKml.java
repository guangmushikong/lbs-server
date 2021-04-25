package com.guangmushikong.lbi.dao;

import com.alibaba.fastjson.JSON;
import com.guangmushikong.lbi.util.KmlUtil;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.WKTReader;
import lombok.extern.slf4j.Slf4j;
import org.geotools.data.DataUtilities;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;

import org.geotools.kml.v22.KML;
import org.geotools.kml.v22.KMLConfiguration;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Symbolizer;
import org.geotools.xml.Encoder;
import org.geotools.xml.Parser;
import org.geotools.xml.StreamingParser;
import org.opengis.feature.simple.SimpleFeature;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.*;

@Slf4j
public class readKml {

    @Test
    public void wkt2Kml()throws Exception{


        List<String> list = new ArrayList<>();
        list.add("POLYGON ((116.21278950384274 39.90557982319698, 116.21177234433465 39.90610963061354, 116.21106912279264 39.90264172209895, 116.21399502548638 39.902612822554126, 116.21629305278306 39.905011479365406, 116.21278950384274 39.90557982319698))");
        list.add("POLYGON((113.38185597038 34.54828048706,113.38224220848 34.548355588913,113.38249970055 34.548108825684,113.38237095451 34.54787279129,113.38208127594 34.547786960602,113.38185597038 34.54828048706))");

        List<Geometry> geometryList = new ArrayList<>();
        for (String wkt : list) {
            WKTReader reader = new WKTReader();
            geometryList.add(reader.read(wkt));
        }
        List<String> attrKeys = new ArrayList<>();
        attrKeys.add("ceshi1");
        attrKeys.add("ceshi2");
        attrKeys.add("ceshi3");

       /* int i = 1;
        List<Map<String, Object>> geomList = new ArrayList<>();
        for (String str : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("geoKey", WKTUtil.wktToGeom(str));
            map.put("ceshi1", "测试" + i++);
            map.put("ceshi2", "测试" + i++);
            map.put("ceshi3", "测试" + i++);
            geomList.add(map);
        }*/
        String path1="F:/data/kml/test_1.kml";
        String path2="F:/data/kml/test_2.kml";
        KmlUtil.write2Wml(path1, "Polygon", geometryList);
        //KmlUtil.write2Wml(path2, "Polygon", "geoKey", attrKeys, geomList)
    }

    @Test
    public void read()throws Exception{
        String path="F:/data/kml/point.kml";
        FileInputStream fis=new FileInputStream(new File(path));
        Parser parser = new Parser(new KMLConfiguration());
        SimpleFeature f = (SimpleFeature) parser.parse( fis );
        log.info(f.getFeatureType().getTypeName());
        Collection placemarks = (Collection) f.getAttribute("Feature");
        for(Object placemark:placemarks){
            SimpleFeature point=(SimpleFeature)placemark;
            //log.info(placemark.toString());
            log.info(point.getID()+","+point.getFeatureType());
        }

    }

    /**
     * 生成wml文件
     * @param wmlPath wml文件路径（包含文件名称） 如：D:\data\tmp\ceshi.kml
     * @param type 图幅元素类型
     * @param geometries 图幅集合
     * @throws Exception
     */
    public void write2Wml(
            String wmlPath,
            String type,
            List<Geometry> geometries) throws Exception {
        FileOutputStream fos = new FileOutputStream(wmlPath,true);
        String str = write2WmlStr(type, geometries);
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
    public String write2WmlStr(
            String type,
            List<Geometry> geometrys) throws Exception {
        ByteArrayOutputStream os = write2WmlStream(type, geometrys);
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
    public ByteArrayOutputStream write2WmlStream(
            String type,
            List<Geometry> geometrys) throws Exception {
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
        List<SimpleFeature> features = new ArrayList<>();
        for (Geometry geometry : geometrys) {

            SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(tb.buildFeatureType());
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

}

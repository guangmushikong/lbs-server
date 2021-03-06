
package com.guangmushikong.lbi.service;

import com.guangmushikong.lbi.dao.UserDataDao;
import com.guangmushikong.lbi.model.KmlDO;
import com.guangmushikong.lbi.model.UserDataDO;
import com.guangmushikong.lbi.util.IOUtils;
import com.guangmushikong.lbi.util.KmlUtil;
import com.vividsolutions.jts.geom.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.kml.KMLConfiguration;
import org.geotools.xml.PullParser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/*************************************
 * Class Name: CustomDataSetService
 * Description:〈CustomDataSetService〉
 * @author deyi
 * @since 1.0.0
 ************************************/
@Service
@Slf4j
public class UserDataService {
    @Autowired
    UserDataDao userDataDao;

    @Value("${spring.img.path}")
    String imgPath;
    @Value("${spring.kml.path}")
    String kmlPath;
    @Value("${service.tiledata}")
    String tiledata;

    public List<UserDataDO> listUserData(long projectId){
        return userDataDao.listUserData(projectId);
    }
    public void addUserData(UserDataDO customVO){
        userDataDao.addUserData(customVO);
    }

    public void saveUserData(UserDataDO customVO){
        UserDataDO customVO2=userDataDao.getUserData(customVO.getUuid());
        if(customVO2!=null){
            userDataDao.updateUserData(customVO);
        }else {
            userDataDao.addUserData(customVO);
        }
    }

    public void updateUserData(UserDataDO customVO){
        userDataDao.updateUserData(customVO);
    }

    public void delUserData(String uuid,long projectId){
        userDataDao.delUserData(uuid,projectId);
    }

    public String saveJpg(long projectId,String fileName,byte[] bytes)throws IOException {
        String uploadFolder=imgPath+File.separator+projectId;
        File dir=new File(uploadFolder);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filePath=dir+File.separator+fileName;
        File outFile=new File(filePath);
        FileCopyUtils.copy(bytes,outFile);
        return filePath;
    }

    public byte[] getFile(long projectId,String fileName)throws IOException{
        String filePath=imgPath+File.separator+projectId+File.separator+fileName;
        File file=new File(filePath);
        return FileCopyUtils.copyToByteArray(file);
    }

    public int kml2PgTable(String username,InputStream is)throws Exception{
        PullParser parser = new PullParser(new KMLConfiguration(), is, SimpleFeature.class);
        List<SimpleFeature> features = new ArrayList<>();
        SimpleFeature simpleFeature = (SimpleFeature) parser.parse();
        while (simpleFeature != null) {
            if("placemark".equalsIgnoreCase(simpleFeature.getFeatureType().getTypeName())){
                features.add(simpleFeature);
            }
            simpleFeature = (SimpleFeature) parser.parse();
        }
        for(SimpleFeature feature:features){
            KmlDO kmlDO=new KmlDO();
            kmlDO.setId(feature.getID());
            kmlDO.setUserName(username);
            Object name=feature.getAttribute("name");
            if(name!=null) {
                kmlDO.setName(name.toString());
            }
            Geometry geom=(Geometry)feature.getDefaultGeometry();
            kmlDO.setType(geom.getGeometryType());
            kmlDO.setGeometry(geom);
            //log.info("id:{},name:{},geom:{}",kmlDO.getId(),kmlDO.getName(),geom.getGeometryType());
            userDataDao.addKml(kmlDO);
        }
        return features.size();
    }

    public void getKmlFile(
            String userName,
            String type,
            ServletOutputStream outputStream)throws IOException{
        String path=String.format("%s/%s_%s.kml",kmlPath,userName,type);
        List<KmlDO> list=userDataDao.listKml(userName,type);
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
        try{
            KmlUtil.write2Wml(path,type,"geoKey",attrKeys,geomList);
        }catch (Exception e){
            e.printStackTrace();
        }
        IOUtils.downFile(outputStream,path);
    }

    public Long getKmlFileSize(
            String userName,
            String type){
        String path=String.format("%s/%s_%s.kml",kmlPath,userName,type);
        File f=new File(path);
        if(!f.exists()){
            log.error("文件不存在{}",path);
            return -1L;
        }
        return f.length();
    }

    public void shp2PgTable(String layerName)throws Exception{
        String filePath=tiledata+File.separator+layerName+File.separator+layerName+".shp";
        log.info("【path】"+filePath);
        ShapefileDataStore shpDataStore = new ShapefileDataStore(new File(filePath).toURI().toURL());
        shpDataStore.setCharset(Charset.forName("GBK"));
        String typeName = shpDataStore.getTypeNames()[0];
        log.info("【typeName】"+typeName);
        SimpleFeatureType simpleFeatureType=shpDataStore.getSchema();
        GeometryType geometryType=simpleFeatureType.getGeometryDescriptor().getType();
        log.info("【geometryType】"+geometryType.getName());
        List<Name> nameList=new ArrayList<>();
        for(int i=0;i<simpleFeatureType.getAttributeCount();i++){
            AttributeType attributeType=simpleFeatureType.getType(i);
            if(geometryType.getName()!=attributeType.getName()){
                nameList.add(attributeType.getName());
            }
        }
        //创建表
        buildTable(layerName,simpleFeatureType,nameList);

        List<String> names = new ArrayList<>();
        List<String> vals = new ArrayList<>();
        for (Name fieldName : nameList) {
            names.add(fieldName.toString().toLowerCase());
            vals.add("?");
        }
        String iSql=String.format("insert into data.%s(%s,geom) values(%s,ST_GeomFromText(?,4326))",
                layerName,
                StringUtils.join(names, ","),
                StringUtils.join(vals, ","));
        int[] types= IntStream.range(0, nameList.size()+1).map(u-> Types.VARCHAR).toArray();
        saveFeatures(iSql,types,shpDataStore,nameList);
    }

    private void buildTable(
            String tableName,
            SimpleFeatureType type,
            List<Name> nameList){
        String dropTableSql=String.format("drop table if exists data.%s",tableName);
        log.info("【dropTable】"+dropTableSql);
        userDataDao.executeSql(dropTableSql);

        StringBuilder sb=new StringBuilder();
        sb.append("create table if not exists data.").append(tableName);
        sb.append("(id bigserial primary key,");
        for(Name name:nameList){
            AttributeType attributeType=type.getType(name);
            sb.append(attributeType.getName().toString().toLowerCase()).append(" text,");
        }
        sb.append("geom geometry)");
        log.info("【createTable】"+sb.toString());
        userDataDao.executeSql(sb.toString());
        String indexSql=String.format("create index data_%s_geom_gist on data.%s using gist(geom)",tableName,tableName);
        log.info("【createIndex】"+indexSql);
        userDataDao.executeSql(indexSql);
    }

    private void saveFeatures(
            String iSql,
            int[] types,
            ShapefileDataStore shpDataStore,
            List<Name> nameList)throws Exception {

        String typeName = shpDataStore.getTypeNames()[0];
        FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = shpDataStore.getFeatureSource(typeName);
        FeatureCollection<SimpleFeatureType, SimpleFeature> result = featureSource.getFeatures();
        log.info("【total】"+result.size());
        FeatureIterator<SimpleFeature> itertor = result.features();
        while(itertor.hasNext()) {
            SimpleFeature feature = itertor.next();

            Object[] objs=new Object[nameList.size()+1];
            for(int i=0;i<nameList.size();i++){
                Name fieldName=nameList.get(i);
                objs[i]=feature.getAttribute(fieldName);
            }
            Geometry geometry = (Geometry) feature.getDefaultGeometry();
            List<Geometry> geomList=simplifyGeometry(geometry);
            for(Geometry geom:geomList){
                objs[nameList.size()]=geom.toText();
                userDataDao.saveRow(iSql,objs,types);
            }
        }
    }

    /**
     * 简化复杂空间类型为简单类型
     * @param geometry 空间对象
     * @return 简单类型空间对象
     */
    private List<Geometry> simplifyGeometry(Geometry geometry){
        List<Geometry> list=new ArrayList<>();
        if (geometry instanceof MultiPolygon) {
            MultiPolygon m = (MultiPolygon) geometry;
            for (int i = 0; i < m.getNumGeometries(); i++) {
                Polygon polygon = (Polygon) m.getGeometryN(i);
                list.add(polygon);
            }
        }else if(geometry instanceof MultiLineString) {
            MultiLineString m = (MultiLineString) geometry;
            for (int i = 0; i < m.getNumGeometries(); i++) {
                LineString lineString = (LineString) m.getGeometryN(i);
                list.add(lineString);
            }
        }else if(geometry instanceof MultiPoint) {
            MultiPoint m = (MultiPoint) geometry;
            for (int i = 0; i < m.getNumGeometries(); i++) {
                Point point = (Point) m.getGeometryN(i);
                list.add(point);
            }
        }else if(geometry instanceof GeometryCollection) {
            GeometryCollection m = (GeometryCollection) geometry;
            for (int i = 0; i < m.getNumGeometries(); i++) {
                Geometry geom = m.getGeometryN(i);
                List<Geometry> geomList=simplifyGeometry(geom);
                list.addAll(geomList);
            }
        }else {
            list.add(geometry);
        }
        return list;
    }
}

package com.guangmushikong.lbi.service;

import com.alibaba.fastjson.JSON;
import com.guangmushikong.lbi.dao.MetaDao;
import com.guangmushikong.lbi.dao.TileDao;
import com.guangmushikong.lbi.model.FeatureCollectionVO;
import com.guangmushikong.lbi.model.FeatureVO;
import com.guangmushikong.lbi.model.MapKind;
import com.guangmushikong.lbi.model.TileMap;
import com.guangmushikong.lbi.util.CommonUtil;
import com.lbi.model.Tile;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Service("tileService")
public class TileService {
    @Resource(name="tileDao")
    TileDao tileDao;
    @Resource(name="metaDao")
    MetaDao metaDao;
    @Value("${service.geoserver}")
    String geoserver;
    @Value("${service.tiledata}")
    String tiledata;

    public TileMap getTileMapById(
            long serviceId,
            String title,
            String srs,
            String extension){
        return metaDao.getTileMapById(serviceId,title,srs,extension);
    }

    public TileMap getTileMapById(
            long serviceId,
            String title,
            String extension){
        return getTileMapById(serviceId,title,"EPSG:900913",extension);
    }

    public byte[] getTile(
            TileMap tileMap,
            Tile tile){
        if(tileMap==null){
            return null;
        }
        MapKind mapKind=MapKind.getByValue(tileMap.getKind());
        if(mapKind==MapKind.LocalCache){
            return getCacheTile(tileMap,tile);
        }else if(mapKind==MapKind.LocalTimeCache){
            return getCacheTimeTile(tileMap,tile);
        }else if(mapKind==MapKind.PGLayer){
            return getPGLayerTile(tileMap,tile);
        }else {
            return null;
        }
    }

    public byte[] getLayer(
            String version,
            String layerName,
            String srs,
            String extension)throws IOException {
        String path=tiledata+File.separator+layerName+File.separator+"layer.json";
        //System.out.println("【path】"+sb.toString());
        return CommonUtil.fileToByteArray(path);
    }

    private byte[] getCacheTile(TileMap tileMap, Tile tile){
        try{
            StringBuilder sb=new StringBuilder();
            sb.append(tiledata);
            sb.append(File.separator).append(tileMap.getTitle());
            sb.append(File.separator).append(tile.getZ());
            sb.append(File.separator).append(tile.getX());
            sb.append(File.separator).append(tile.getY());
            sb.append(".").append(tileMap.getFileExtension());
            String path=sb.toString();
            //System.out.println(tile.getX()+","+tile.getY()+","+tile.getZ()+"|"+path);
            if(tileMap.getExtension().equalsIgnoreCase("tif")
                    || tileMap.getExtension().equalsIgnoreCase("geojson")
                    || tileMap.getExtension().equalsIgnoreCase("terrain")){
                return CommonUtil.fileToByteArray(path);
            }else {
                return CommonUtil.imageToByteArray(path);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    private byte[] getCacheTimeTile(TileMap tileMap, Tile tile){
        try{
            StringBuilder sb=new StringBuilder();
            sb.append(tiledata);
            String title=tileMap.getTitle();
            title=title.replace("_"+tileMap.getRecordDate(),"");
            sb.append(File.separator).append(title);
            sb.append(File.separator).append(tileMap.getRecordDate());
            sb.append(File.separator).append(tile.getZ());
            sb.append(File.separator).append(tile.getX());
            sb.append(File.separator).append(tile.getY());
            sb.append(".").append(tileMap.getFileExtension());
            String path=sb.toString();
            //System.out.println(tile.getX()+","+tile.getY()+","+tile.getZ()+"|"+path);
            if(tileMap.getExtension().equalsIgnoreCase("tif")){
                return CommonUtil.fileToByteArray(path);
            }else{
                return CommonUtil.imageToByteArray(path);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    private byte[] getPGLayerTile(TileMap tileMap, Tile tile){
        //String[] fields={"id"};
        String prop=tileMap.getProp();
        List<FeatureVO> featureList;
        if(StringUtils.isNotEmpty(prop)){
            featureList=tileDao.getFeatureListByTile(tileMap.getTitle(),null,JSON.parseObject(prop),tile);
        }else {
            featureList=tileDao.getFeatureListByTile(tileMap.getTitle(),null,null,tile);
        }
        FeatureCollectionVO result=new FeatureCollectionVO();
        result.setFeatures(featureList);
        return JSON.toJSONBytes(result);
    }
}

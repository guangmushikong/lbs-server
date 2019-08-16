package com.guangmushikong.lbi.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.guangmushikong.lbi.dao.MetaDao;
import com.guangmushikong.lbi.dao.TileDao;
import com.guangmushikong.lbi.model.*;
import com.guangmushikong.lbi.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
public class TileService {
    @Autowired
    TileDao tileDao;
    @Autowired
    MetaDao metaDao;

    @Value("${service.tiledata}")
    String tiledata;

    public byte[] getTile(
            ServiceType serviceType,
            String tileset,
            Tile tile)throws Exception{
        String[] args=tileset.split("@");
        String title=args[0];
        String srs=args[1];
        String extension=args[2];
        TileMap tileMap;
        if(serviceType==ServiceType.TMS){
            tileMap=metaDao.getTileMapById(ServiceType.TMS.getValue(),title,srs,extension);
        }else if(serviceType==ServiceType.XYZ){
            tileMap=metaDao.getTileMapById(ServiceType.XYZ.getValue(),title,srs,extension);
        }else {
            throw new Exception("瓦片地图协议:"+serviceType.getValue()+"不符合要求");
        }

        MapKind mapKind=MapKind.getByValue(tileMap.getKind());
        if(mapKind==MapKind.LocalCache){
            return getCacheTile(tileMap,tile);
        }else if(mapKind==MapKind.LocalTimeCache){
            return getCacheTimeTile(tileMap,tile);
        }else if(mapKind==MapKind.PGLayer){
            return getPGLayerTile(tileMap,tile);
        }else if(mapKind==MapKind.XYZLayer){
            return getXYZTile(tileMap,tile);
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
        return CommonUtil.fileToByteArray(path);
    }

    /**
     * 获取本地缓存Tile图像
     * @param tileMap 瓦片地图对象
     * @param tile 瓦片对象
     * @return 图像字节流
     */
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

            Set<String> extensionSet= Sets.newHashSet("tif","geojson","terrain");
            if(extensionSet.contains(tileMap.getExtension().toLowerCase())){
                return CommonUtil.fileToByteArray(path);
            }else {
                return CommonUtil.imageToByteArray(path);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 获取本地缓存时序Tile图像
     * @param tileMap 瓦片地图对象
     * @param tile 瓦片对象
     * @return 图像字节流
     */
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

            if("tif".equalsIgnoreCase(tileMap.getExtension())){
                return CommonUtil.fileToByteArray(path);
            }else{
                return CommonUtil.imageToByteArray(path);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 获取PG图层Tile图像
     * @param tileMap 瓦片地图对象
     * @param tile 瓦片对象
     * @return geojson字节流
     */
    private byte[] getPGLayerTile(TileMap tileMap, Tile tile){
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

    /**
     * 获取本地缓存XYZ型Tile图像
     * @param tileMap 瓦片地图对象
     * @param tile 瓦片对象
     * @return 图像字节流
     */
    private byte[] getXYZTile(TileMap tileMap, Tile tile){
        try{
            int alterY=new Double(Math.pow(2,tile.getZ())-1-tile.getY()).intValue();
            tile.setY(alterY);
            StringBuilder sb=new StringBuilder();
            sb.append(tiledata);
            sb.append(File.separator).append(tileMap.getTitle());
            sb.append(File.separator).append(tile.getZ());
            sb.append(File.separator).append(tile.getX());
            sb.append(File.separator).append(tile.getY());
            sb.append(".").append(tileMap.getFileExtension());
            String path=sb.toString();

            Set<String> extensionSet= Sets.newHashSet("tif","geojson","terrain");
            if(extensionSet.contains(tileMap.getExtension().toLowerCase())){
                return CommonUtil.fileToByteArray(path);
            }else {
                return CommonUtil.imageToByteArray(path);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}

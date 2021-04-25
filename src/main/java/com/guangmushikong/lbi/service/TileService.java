package com.guangmushikong.lbi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.guangmushikong.lbi.dao.MetaDao;
import com.guangmushikong.lbi.dao.RestTileDao;
import com.guangmushikong.lbi.dao.TileDao;
import com.guangmushikong.lbi.model.*;
import com.guangmushikong.lbi.model.enums.MapKind;
import com.guangmushikong.lbi.model.enums.ServiceType;
import com.guangmushikong.lbi.model.geojson.FeatureCollectionVO;
import com.guangmushikong.lbi.model.geojson.FeatureVO;
import com.guangmushikong.lbi.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class TileService {
    @Autowired
    TileDao tileDao;
    @Autowired
    MetaDao metaDao;
    @Autowired
    RestTileDao restTileDao;

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
        //服务协议类型
        if(serviceType==ServiceType.TMS){
            tileMap=metaDao.getTileMapById(ServiceType.TMS.getValue(),title,srs,extension);
        }else if(serviceType==ServiceType.XYZ){
            tileMap=metaDao.getTileMapById(ServiceType.XYZ.getValue(),title,srs,extension);
        }else {
            throw new Exception("瓦片地图协议:"+serviceType.getValue()+"不符合要求");
        }
        if(tileMap==null){
            throw new Exception("该瓦片地图不存在:"+tileset);
        }
        //fixme 瓦片地图类型
        MapKind mapKind=MapKind.getByValue(tileMap.getKind());
        if(mapKind==MapKind.LocalCache){
            return getCacheTile(tileMap,tile);
        }else if(mapKind==MapKind.LocalTimeCache){
            return getCacheTimeTile(tileMap,tile);
        }else if(mapKind==MapKind.PGLayer){
            return getPgLayerTile(tileMap,tile);
        }else if(mapKind==MapKind.XYZLayer){
            return getXyzTile(tileMap,tile);
        }else if(mapKind==MapKind.ExtXyzLayer){
            return getExtXyzTile(tileMap,tile);
        }else if(mapKind==MapKind.ExtTmsLayer){
            return getExtTmsTile(tileMap,tile);
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

    public byte[] getMeta(
            String version,
            String layerName,
            String srs,
            String extension)throws IOException {
        String path=tiledata+File.separator+layerName+File.separator+"meta.json";
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
            return restTileDao.getLocalTmsTile(
                    tileMap.getTitle(),
                    null,
                    tileMap.getFileExtension(),
                    tile.getX(),
                    tile.getY(),
                    tile.getZ());
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
            String title=tileMap.getTitle();
            title=title.replace("_"+tileMap.getRecordDate(),"");

            return restTileDao.getLocalTmsTile(
                    title,
                    tileMap.getRecordDate(),
                    tileMap.getFileExtension(),
                    tile.getX(),
                    tile.getY(),
                    tile.getZ());
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
    private byte[] getPgLayerTile(TileMap tileMap, Tile tile){
        String prop=tileMap.getProp();
        List<FeatureVO> featureList;
        if(StringUtils.isNotEmpty(prop)){
            featureList=tileDao.getFeatureListByTile(tileMap.getTitle(),null,JSON.parseObject(prop),tile);
        }else {
            featureList=tileDao.getFeatureListByTile(tileMap.getTitle(),null,null,tile);
        }
        FeatureCollectionVO result=new FeatureCollectionVO();
        result.setFeatures(featureList);
        return JSON.toJSONBytes(result, SerializerFeature.DisableCircularReferenceDetect);
    }

    /**
     * 获取本地缓存XYZ型Tile图像
     * @param tileMap 瓦片地图对象
     * @param tile 瓦片对象
     * @return 图像字节流
     */
    private byte[] getXyzTile(TileMap tileMap, Tile tile){
        try{
            int alterY=new Double(Math.pow(2,tile.getZ())-1-tile.getY()).intValue();
            tile.setY(alterY);

            return restTileDao.getLocalTmsTile(
                    tileMap.getTitle(),
                    null,
                    tileMap.getFileExtension(),
                    tile.getX(),
                    tile.getY(),
                    tile.getZ());
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    private byte[] getExtXyzTile(TileMap tileMap, Tile tile){
        JSONObject prop=JSON.parseObject(tileMap.getProp());
        String template=prop.getString("template");
        try{
            /*byte[] bytes= restTileDao.getLocalTmsTile(
                    tileMap.getTitle(),
                    null,
                    tileMap.getFileExtension(),
                    tile.getX(),
                    tile.getY(),
                    tile.getZ());
            if(bytes==null){
                return restTileDao.getExtXyzTile(template,tile.getX(),tile.getY(),tile.getZ());
            }*/
            return restTileDao.cacheTmsTile(
                    tileMap.getTitle(),
                    tileMap.getFileExtension(),
                    template,
                    tile.getX(),
                    tile.getY(),
                    tile.getZ());
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    private byte[] getExtTmsTile(TileMap tileMap, Tile tile){
        JSONObject prop=JSON.parseObject(tileMap.getProp());
        String template=prop.getString("template");
        return restTileDao.getExtTmsTile(template,tile.getX(),tile.getY(),tile.getZ());
    }
}

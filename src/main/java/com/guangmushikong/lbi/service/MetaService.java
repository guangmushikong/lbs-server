package com.guangmushikong.lbi.service;

import com.guangmushikong.lbi.dao.MetaDao;
import com.guangmushikong.lbi.model.*;
import com.guangmushikong.lbi.model.xml.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("metaService")
public class MetaService {
    @Resource(name="metaDao")
    private MetaDao metaDao;
    @Value("${service.mapserver}")
    String mapserver;

    public Root_Services getServices(){
        Root_Services u=new Root_Services();
        List<TileMapService> serviceList=metaDao.getTileMapServiceList();
        List<Node_TileMapService> nServiceList=new ArrayList<>();
        for(TileMapService s:serviceList){
            String href=s.getHref();
            href=href.replace("${mapserver}",mapserver);
            Node_TileMapService nService=new Node_TileMapService(s.getTitle(),s.getVersion(),href);
            nServiceList.add(nService);
        }
        u.setTileMapServices(nServiceList);
        return u;
    }

    public Root_TileMapService getTileMapService(long serviceId,String version){
        TileMapService nService=metaDao.getTileMapServiceById(serviceId);
        Root_TileMapService u = new Root_TileMapService();
        u.setVersion(nService.getVersion());
        u.setTitle(nService.getTitle());
        if(StringUtils.isNotEmpty(nService.getAbstract()))u.setAbstract(nService.getAbstract());
        //parent href
        u.setServices("http://"+mapserver);
        //child list
        List<TileMap> tileMapList=metaDao.getTileMapList(serviceId);
        List<Node_TileMap> nTileMapList=new ArrayList<>();
        for(TileMap m:tileMapList){
            String href=m.getHref();
            href=href.replace("${mapserver}",mapserver);
            Node_TileMap nTileMap=new Node_TileMap(m.getTitle(),m.getSrs(),m.getProfile(),href);
            nTileMap.setGroup(m.getGroup());
            nTileMapList.add(nTileMap);
        }
        u.setTileMaps(nTileMapList);
        return u;
    }
    public Root_TileMap getTileMap(
            long serviceId,
            String version,
            String layerName,
            String srs,
            String extension){
        TileMap u =metaDao.getTileMapById(serviceId,layerName,srs,extension);
        if(u!=null){
            Root_TileMap item=new Root_TileMap();
            item.setVersion(version);
            //parent href
            TileMapService nService=metaDao.getTileMapServiceById(u.getServiceId());
            String tilemapservice=nService.getHref();
            tilemapservice=tilemapservice.replace("${mapserver}",mapserver);
            item.setServices(tilemapservice);
            if(StringUtils.isNotEmpty(u.getAbstract()))item.setAbstract(u.getAbstract());
            item.setTitle(u.getTitle());
            item.setSRS(u.getSrs());

            Node_BoundingBox boundingBox =new Node_BoundingBox();
            boundingBox.setMinX(u.getMinX());
            boundingBox.setMinY(u.getMinY());
            boundingBox.setMaxX(u.getMaxX());
            boundingBox.setMaxY(u.getMaxY());
            item.setXBoundingBox(boundingBox);

            Node_Origin origin =new Node_Origin();
            origin.setX(u.getOriginX());
            origin.setY(u.getOriginY());
            item.setXOrigin(origin);

            Node_TileFormat tileFormat =new Node_TileFormat();
            tileFormat.setWidth(u.getWidth());
            tileFormat.setHeight(u.getHeight());
            tileFormat.setMimeType(u.getMimeType());
            tileFormat.setExtension(u.getExtension());
            item.setXTileFormat(tileFormat);

            //child list
            Node_TileSets tileSets=new Node_TileSets();
            List<Node_TileSet> nTileSetList=new ArrayList<>();
            List<TileSet> tileSetList=metaDao.getTileSetList(u.getId());
            for(TileSet t:tileSetList){
                String href=t.getHref();
                href=href.replace("${mapserver}",mapserver);
                Node_TileSet nTileSet=new Node_TileSet(href,t.getUnitsPerPixel(),t.getSortOrder());
                nTileSetList.add(nTileSet);
            }
            tileSets.setProfile(u.getProfile());
            tileSets.setTileSets(nTileSetList);
            item.setTileSets(tileSets);
            return item;
        }
        return null;
    }

    public List<TileMap> getTileMapList(){
        List<TileMap> mapList=new ArrayList<>();
        List<TileMap> list=null;
        list=metaDao.getTileMapList(1);
        if(list!=null)mapList.addAll(list);
        list=metaDao.getTileMapList(2);
        if(list!=null)mapList.addAll(list);
        for(TileMap m:mapList){
            String href=m.getHref();
            href=href.replace("${mapserver}",mapserver);
            m.setHref(href);
        }
        return mapList;
    }

    public TileMap getTileMapById(long id){
        TileMap m=metaDao.getTileMapById(id);
        String href=m.getHref();
        href=href.replace("${mapserver}",mapserver);
        m.setHref(href);
        return m;
    }
    public List<TileSet> getTileSetList(long mapId){
        List<TileSet> mapSetList=metaDao.getTileSetList(mapId);
        for(TileSet t:mapSetList){
            String href=t.getHref();
            href=href.replace("${mapserver}",mapserver);
            t.setHref(href);
        }
        return mapSetList;
    }

    public List<ProjectDO> getProjectList(){
        return metaDao.getProjectList();
    }

    public ProjectDO getProjectById(long id){
        return metaDao.getProjectById(id);
    }

    public List<DataSetDO> getDataSetList(long projectId){
        List<DataSetDO> list= metaDao.getDataSetList(projectId);
        //分组
        Map<String,List<DataSetDO>> groupDict=new HashMap<>();
        for(DataSetDO dataSet:list){
            List<DataSetDO> dataSetList;
            if(groupDict.containsKey(dataSet.getName())){
                dataSetList=groupDict.get(dataSet.getName());
            }else {
                dataSetList=new ArrayList<>();
            }
            dataSetList.add(dataSet);
            groupDict.put(dataSet.getName(),dataSetList);
        }

        List<DataSetDO> result=new ArrayList<>();
        for(String name:groupDict.keySet()){
            List<DataSetDO> dataSetList=groupDict.get(name);
            List<Long> idList=new ArrayList<>();
            for(DataSetDO dataSet:dataSetList){
                idList.add(dataSet.getMapId());
            }
            DataSetDO dataSetDO=dataSetList.get(0);
            List<TileMap> mapList=metaDao.getTileMapList(idList);
            dataSetDO.setMaps(mapList);
            result.add(dataSetDO);
        }
        return result;
    }

    public DataSetDO getDataSetById(long id){
        return metaDao.getDataSetById(id);
    }
}

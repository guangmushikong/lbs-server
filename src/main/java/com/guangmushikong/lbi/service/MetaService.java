package com.guangmushikong.lbi.service;

import com.google.common.collect.Lists;
import com.guangmushikong.lbi.dao.MetaDao;
import com.guangmushikong.lbi.model.*;
import com.guangmushikong.lbi.model.xml.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("metaService")
public class MetaService {
    @Resource(name="metaDao")
    private MetaDao metaDao;
    @Value("${service.mapserver}")
    String mapserver;

    public Root_Services getServices(){
        List<TileMapService> serviceList=metaDao.getTileMapServiceList();
        List<Node_TileMapService> nServiceList=serviceList
                .stream()
                .map(s->new Node_TileMapService(s.getTitle(),s.getVersion(),s.getHref()))
                .collect(Collectors.toList());

        Root_Services u=new Root_Services();
        u.setTileMapServices(nServiceList);
        return u;
    }

    public Root_TileMapService getTileMapService(long serviceId,String version){
        TileMapService nService=metaDao.getTileMapServiceById(serviceId);
        Root_TileMapService u = new Root_TileMapService();
        u.setVersion(nService.getVersion());
        u.setTitle(nService.getTitle());
        if(StringUtils.isNotEmpty(nService.getAbstract())){
            u.setAbstract(nService.getAbstract());
        }
        //parent href
        u.setServices("http://"+mapserver+"/service");
        //child list
        List<TileMap> tileMapList=metaDao.getTileMapList(serviceId);
        List<Node_TileMap> nTileMapList=tileMapList
                .stream().map(m->{
                    Node_TileMap nTileMap=new Node_TileMap(m.getTitle(),m.getSrs(),m.getProfile(),m.getHref());
                    nTileMap.setGroup(m.getGroup());
                    return nTileMap;
                })
                .collect(Collectors.toList());
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
            item.setServices(nService.getHref());
            if(StringUtils.isNotEmpty(u.getAbstract())){
                item.setAbstract(u.getAbstract());
            }
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
            List<Node_TileSet> nTileSetList=Lists.newArrayList();
            List<TileSet> tileSetList=metaDao.getTileSetList(u.getId());
            for(TileSet t:tileSetList){
                Node_TileSet nTileSet=new Node_TileSet(t.getHref(),t.getUnitsPerPixel(),t.getSortOrder());
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
        List<TileMap> mapList= Lists.newArrayList();
        List<TileMap> list1=metaDao.getTileMapList(1);
        mapList.addAll(list1);
        List<TileMap> list2=metaDao.getTileMapList(2);
        mapList.addAll(list2);
        return mapList;
    }

    public TileMap getTileMapById(long id){
        return metaDao.getTileMapById(id);
    }
    public List<TileSet> getTileSetList(long mapId){
        return metaDao.getTileSetList(mapId);
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
        Map<String,List<DataSetDO>> groupDict=list
                .stream()
                .collect(Collectors.groupingBy(DataSetDO::getName));

        List<DataSetDO> result=Lists.newArrayList();
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

package com.guangmushikong.lbi.service;

import com.guangmushikong.lbi.config.TileMapConfig;
import com.lbi.model.Tile;
import com.guangmushikong.lbi.model.*;
import com.lbi.util.ImageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

@Service("tmsService")
public class TMSService {
    @Resource(name="tileMapConfig")
    TileMapConfig tileMapConfig;

    @Value("${service.geoserver}")
    String geoserver;
    @Value("${service.tiledata}")
    String tiledata;

    public byte[] getTMS_Tile(
            String version,
            String layerName,
            String srs,
            String extension,
            Tile tile)throws Exception{
        String tileset=layerName+"@"+srs+"@"+extension;
        TileMap tileMap=tileMapConfig.getTileMap(tileset);
        if(tileMap==null)return null;

        if(tileMap.getKind()==1){
            //return getRemoteTile(tileMap,tile);
        }else if(tileMap.getKind()==2){
            //return getOSSTile(tileMap,tile);
        }else if(tileMap.getKind()==3){
            //return getOSSTimeTile(tileMap,tile);
        }else if(tileMap.getKind()==4){
            return getCacheTile(tileMap,tile);
        }else if(tileMap.getKind()==5){
            return getCacheTimeTile(tileMap,tile);
        }

        return null;
    }

    public byte[] getLayer(
            String version,
            String layerName,
            String srs,
            String extension){
        try{
            StringBuilder sb=new StringBuilder();
            sb.append(tiledata);
            sb.append(File.separator).append(layerName);
            sb.append(File.separator).append("layer.json");
            //System.out.println("【path】"+sb.toString());
            File file=new File(sb.toString());
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            return bos.toByteArray();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
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
            //System.out.println("【path】"+sb.toString());
            File file=new File(sb.toString());
            if(file.exists()){
                if(tileMap.getExtension().equalsIgnoreCase("tif")
                        || tileMap.getExtension().equalsIgnoreCase("terrain")){
                    FileInputStream fis = new FileInputStream(file);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
                    byte[] b = new byte[1000];
                    int n;
                    while ((n = fis.read(b)) != -1) {
                        bos.write(b, 0, n);
                    }
                    fis.close();
                    bos.close();
                    return bos.toByteArray();
                }else{
                    BufferedImage image=ImageIO.read(file);
                    if(image!=null)return ImageUtil.toByteArray(image);
                }
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
            //System.out.println("【path】"+sb.toString());
            File file=new File(sb.toString());
            if(file.exists()){
                if(tileMap.getExtension().equalsIgnoreCase("tif")){
                    FileInputStream fis = new FileInputStream(file);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
                    byte[] b = new byte[1000];
                    int n;
                    while ((n = fis.read(b)) != -1) {
                        bos.write(b, 0, n);
                    }
                    fis.close();
                    bos.close();
                    return bos.toByteArray();
                }else{
                    BufferedImage image=ImageIO.read(file);
                    if(image!=null)return ImageUtil.toByteArray(image);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}

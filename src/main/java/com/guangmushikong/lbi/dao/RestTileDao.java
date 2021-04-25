package com.guangmushikong.lbi.dao;

import com.google.common.collect.Sets;
import com.guangmushikong.lbi.model.Tile;
import com.guangmushikong.lbi.model.TileMap;
import com.guangmushikong.lbi.util.CommonUtil;
import com.guangmushikong.lbi.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Repository
@Slf4j
public class RestTileDao {
    @Autowired
    RestTemplate restTemplate;

    @Value("${service.tiledata}")
    String tiledata;

    /**
     * 获取本地瓦片
     * @param title     标题
     * @param subTile   二级标题
     * @param fileExtension 文件后缀
     * @param x     瓦片X坐标
     * @param y     瓦片Y坐标
     * @param z     瓦片Z坐标
     * @return
     * @throws Exception
     */
    public byte[] getLocalTmsTile(
            String title,
            String subTile,
            String fileExtension,
            long x,
            long y,
            int z)throws Exception{
        String path;
        if(StringUtils.isNotEmpty(subTile)){
            path=String.format("%s/%s/%s/%d/%d/%d.%s",
                    tiledata,
                    title,
                    subTile,
                    z,
                    x,
                    y,
                    fileExtension);
        }else {
            path=String.format("%s/%s/%d/%d/%d.%s",
                    tiledata,
                    title,
                    z,
                    x,
                    y,
                    fileExtension);
        }
        //log.info("path:{}",path);
        Set<String> extensionSet= Sets.newHashSet("tif","geojson","terrain");
        if(extensionSet.contains(fileExtension)){
            return CommonUtil.fileToByteArray(path);
        }else {
            return CommonUtil.imageToByteArray(path);
        }
    }

    public byte[] cacheTmsTile(
            String title,
            String fileExtension,
            String template,
            long x,
            long y,
            int z)throws Exception{
        String path=String.format("%s/%s/%d/%d/%d.%s",
                tiledata,
                title,
                z,
                x,
                y,
                fileExtension);
        Set<String> extensionSet= Sets.newHashSet("tif","geojson","terrain");
        byte[] bytes=null;
        if(extensionSet.contains(fileExtension)){
            bytes= CommonUtil.fileToByteArray(path);
        }else {
            bytes= CommonUtil.imageToByteArray(path);
        }

        if(bytes==null){
            bytes=getExtXyzTile(template,x,y,z);
        }
        if(bytes!=null){
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            File outFile=new File(path);
            FileUtils.copyInputStreamToFile(in,outFile);
        }

        return bytes;
    }

    public byte[] getExtXyzTile(String template, long x,long y,int level)throws Exception{
        int y2=new Double(Math.pow(2,level)-1-y).intValue();
        return getExtTmsTile(template,x,y2,level);
    }

    public byte[] getExtTmsTile(String template, long x,long y,int level){
        template= StringUtils.replace(template,"{x}",String.valueOf(x));
        template=StringUtils.replace(template,"{y}",String.valueOf(y));
        template=StringUtils.replace(template,"{z}",String.valueOf(level));
        return request(template);
    }

    public byte[] requestByRestTemplate(String url){
        RestTemplate restTemplate=new RestTemplate();
        byte[] bytes=restTemplate.getForObject(url,byte[].class);
        return bytes;
    }

    public byte[] request(String url){
        // 全局请求设置
        RequestConfig globalConfig = RequestConfig.custom().build();
        try{
            CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();
            Map<String, String> headerMap=new HashMap<>();
            headerMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            headerMap.put("Accept-Encoding", "gzip, deflate, br");
            headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
            headerMap.put("Cache-Control", "no-cache");
            headerMap.put("Connection", "keep-alive");
            headerMap.put("Pragma", "no-cache");
            headerMap.put("Upgrade-Insecure-Requests", "1");
            headerMap.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.83 Safari/537.36");
            HttpGet httpGet = new HttpGet(url);
            for(Map.Entry<String, String> entry : headerMap.entrySet()){
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
            CloseableHttpResponse res = httpClient.execute(httpGet);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = res.getEntity();
                return IOUtils.readFully(entity.getContent());
            }
            httpClient.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private void savePng(InputStream is, String filePath){
        try{
            //先读入内存
            ByteArrayOutputStream buf = new ByteArrayOutputStream(8192);
            byte[] b = new byte[1024];
            int len;
            while ((len = is.read(b)) != -1) {
                buf.write(b, 0, len);
            }
            //读图像
            // 读图像
            ByteArrayInputStream in = new ByteArrayInputStream(buf.toByteArray());
            BufferedImage image = ImageIO.read(in);
            in.close();
            File f = new File(filePath);
            ImageIO.write(image,"png", f);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void savePng(byte[] bytes, String filePath){
        try{
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            BufferedImage image = ImageIO.read(in);
            in.close();
            File f = new File(filePath);
            ImageIO.write(image,"png", f);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}

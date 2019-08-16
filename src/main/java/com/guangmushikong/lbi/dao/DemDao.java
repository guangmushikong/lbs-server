package com.guangmushikong.lbi.dao;

import com.guangmushikong.lbi.model.ContourPoint;
import lombok.extern.slf4j.Slf4j;
import org.geotools.coverage.grid.GridCoordinates2D;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.geometry.DirectPosition2D;
import org.opengis.geometry.DirectPosition;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.awt.image.Raster;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import static com.guangmushikong.lbi.model.LBSConstants.*;

/*************************************
 * Class Name: DemDao
 * Description:〈DemDao〉
 * @author deyi
 * @since 1.0.0
 ************************************/
@Repository
@Slf4j
public class DemDao {
    @Resource(name="coverageGujiao")
    GridCoverage2D coverageGujiao;
    @Resource(name="coverageJingzhuang")
    GridCoverage2D coverageJingzhuang;

    Raster grid_gujiao;
    Raster grid_jingzhuang;


    /**
     * 经纬度坐标转像素坐标
     * @param longitude 经度
     * @param latitude  纬度
     * @return 像素坐标
     */
    public GridCoordinates2D point2Pixel(double longitude, double latitude,String demName)throws Exception {
        DirectPosition2D position = new DirectPosition2D();
        position.setLocation(longitude,latitude);
        if(GUJIAO.equalsIgnoreCase(demName)){
            return coverageGujiao.getGridGeometry().worldToGrid(position);
        }else if(JINGZHUANG.equalsIgnoreCase(demName)){
            return coverageJingzhuang.getGridGeometry().worldToGrid(position);
        }else {
            return null;
        }
    }

    /**
     * 获取像素坐标高度
     * @param pixelX    像素坐标X值
     * @param pixelY    像素坐标Y值
     * @return 高度
     */
    public double getHeightByPixel(int pixelX,int pixelY,String demName){
        getGridData(demName);
        if(GUJIAO.equalsIgnoreCase(demName)){
            double[] data = grid_gujiao.getPixel(pixelX, pixelY, new double[1]);
            return data[0];
        }else if(JINGZHUANG.equalsIgnoreCase(demName)){
            double[] data = grid_jingzhuang.getPixel(pixelX, pixelY, new double[1]);
            return data[0];
        }else {
            return -1;
        }
    }


    /**
     * 清理插值点坐标数据
     * @param posGridList 插值点坐标列表
     * @return 清理后坐标列表
     */
    public List<ContourPoint> fixPoint(
            List<GridCoordinates2D> posGridList,
            ContourPoint fPoint,
            ContourPoint tPoint,
            String demName)throws Exception{
        double height;
        Queue<ContourPoint> queue=new LinkedList<>();
        for(GridCoordinates2D posGrid:posGridList){
            DirectPosition pt=pixel2Point(posGrid.x,posGrid.y,demName);
            double[] xys=pt.getCoordinate();
            height=getHeightByPixel(posGrid.x,posGrid.y,demName);
            ContourPoint point=new ContourPoint();
            point.setLongitude(xys[0]);
            point.setLatitude(xys[1]);
            point.setX(posGrid.x);
            point.setY(posGrid.y);
            point.setKind(2);
            point.setHeight((int)height);
            queue.add(point);
        }
        //寻找单调区间
        ContourPoint a=queue.poll();
        ContourPoint b;
        char fAttr,tAttr,attr='0';
        if(a.getHeight() > fPoint.getHeight())fAttr='+';
        else if(a.getHeight() == fPoint.getHeight())fAttr='=';
        else fAttr='-';

        List<ContourPoint> list=new ArrayList<>();
        while((b=queue.peek())!=null){
            if(b.getHeight() > a.getHeight())attr='+';
            else if(b.getHeight() == a.getHeight())attr='=';
            else attr='-';

            if(fAttr!=attr){
                list.add(a);
                fAttr=attr;
            }
            a=queue.poll();
        }
        //比较尾节点
        if(tPoint.getHeight() > a.getHeight())tAttr='+';
        else if(tPoint.getHeight() == a.getHeight())tAttr='=';
        else tAttr='-';
        if(attr!='0' && tAttr!=attr)list.add(a);

        return list;
    }

    /**
     * 获取线两个端点之间像素点
     * @param px1
     * @param py1
     * @param px2
     * @param py2
     * @return 像素点列表
     */
    public List<GridCoordinates2D> getPixelListByScanLine(int px1,int py1,int px2,int py2){
        List<GridCoordinates2D> pixelList=new ArrayList<>();
        if(px1==px2){
            if(py2<py1){
                for(int py=py1-1;py>py2;py--){
                    GridCoordinates2D pixel=new GridCoordinates2D(px1,py);
                    pixelList.add(pixel);
                }
            }else {
                for(int py=py1+1;py<py2;py++){
                    GridCoordinates2D pixel=new GridCoordinates2D(px1,py);
                    pixelList.add(pixel);
                }
            }
        }else if(py1 == py2){
            if(px2<px1){
                for(int px=px1-1;px>px2;px--){
                    GridCoordinates2D pixel=new GridCoordinates2D(px,py1);
                    pixelList.add(pixel);
                }
            }else {
                for(int px=px1+1;px<px2;px++){
                    GridCoordinates2D pixel=new GridCoordinates2D(px,py1);
                    pixelList.add(pixel);
                }
            }
        }else{
            double k=(py2-py1)*1.0/(px2-px1);
            int px=px1;int py=py1;
            int xOffset=1;int yOffset=1;
            if(px2<px1)xOffset=-1;
            if(py2<py1)yOffset=-1;

            if(Math.abs(py2-py1)>Math.abs(px2-px1)){
                while((py+yOffset)!=py2){
                    py=py+yOffset;
                    px=(int)Math.round(px1+(py-py1)/k);
                    GridCoordinates2D pixel=new GridCoordinates2D(px,py);
                    pixelList.add(pixel);
                }
            }else {
                while((px+xOffset)!=px2){
                    px=px+xOffset;
                    py=(int)Math.round(py1+(px-px1)*k);
                    GridCoordinates2D pixel=new GridCoordinates2D(px,py);
                    pixelList.add(pixel);
                }
            }
        }
        return pixelList;
    }

    /**
     * 像素点坐标转经纬度
     * @param pixelX 像素X坐标
     * @param pixelY 像素Y坐标
     * @return 经纬度
     */
    private DirectPosition pixel2Point(
            int pixelX,
            int pixelY,
            String demName)throws Exception {
        GridCoordinates2D posGrid=new GridCoordinates2D(pixelX,pixelY);
        if(GUJIAO.equalsIgnoreCase(demName)){
            return coverageGujiao.getGridGeometry().gridToWorld(posGrid);
        }else if(JINGZHUANG.equalsIgnoreCase(demName)){
            return coverageJingzhuang.getGridGeometry().gridToWorld(posGrid);
        }else {
            return null;
        }
    }


    /**
     * 获取DEM栅格
     */
    private void getGridData(String demName){
        if(GUJIAO.equalsIgnoreCase(demName)){
            if(grid_gujiao==null){
                grid_gujiao=coverageGujiao.getRenderedImage().getData();
                log.info("init grid_gujiao");
            }
        }else if(JINGZHUANG.equalsIgnoreCase(demName)){
            if(grid_jingzhuang==null){
                grid_jingzhuang=coverageJingzhuang.getRenderedImage().getData();
                log.info("init grid_jingzhuang");
            }
        }
    }
}

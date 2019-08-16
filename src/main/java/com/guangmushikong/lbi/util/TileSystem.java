package com.guangmushikong.lbi.util;



import com.guangmushikong.lbi.model.Pixel;
import com.guangmushikong.lbi.model.Tile;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;


/**
 *
 * 瓦片系统类。
 * <p>Bing Maps的地图数据采用墨卡托投影（Mercator Projection）进行存储和展现。</p>
 * <p>赤道半径为6378137米，则赤道周长为2*PI*r = 20037508.3427892</p>
 * <p>X轴的取值范围：[-20037508.3427892,20037508.3427892]。</p>
 * <p>Y轴的取值范围也限定在[-20037508.3427892,20037508.3427892]。</p>
 * <p>因此在墨卡托投影坐标系（米）下的坐标范围是：最小为(-20037508.3427892, -20037508.3427892)到最大坐标为(20037508.3427892, 20037508.3427892)。</p>
 * <p>对应地理坐标系经度取值范围是[-180,180]，纬度取值范围是[-85.05112877980659，85.05112877980659]。</p>
 * <p>地理坐标系（经纬度）对应的范围是：最小地理坐标(-180,-85.05112877980659)，最大地理坐标(180, 85.05112877980659)。</p>
 * <p></p>
 * <img src="http://i.msdn.microsoft.com/dynimg/IC19035.jpg"/>
 * <table border="1">
 * <tr><th>1</th><th>2</th><th>3</th></tr>
 * </table>
 * @version	1.0
 * @author deyi
 */
public class TileSystem {
    /**
     * 赤道半径
     */
    private final static double EarthRadius = 6378137;
    /**
     * 最小纬度
     */
    private final static double MinLatitude = -85.05112878;
    /**
     * 最大纬度
     */
    private final static double MaxLatitude = 85.05112878;
    /**
     * 最小经度
     */
    private final static double MinLongitude = -180;
    /**
     * 最大经度
     */
    private final static double MaxLongitude = 180;

    private static double Clip(double n, double minValue, double maxValue)
    {
        return Math.min(Math.max(n, minValue), maxValue);
    }
    /**
     * 地图宽度。单位：像素
     * @param levelOfDetail 级别
     */
    public static double MapSize(int levelOfDetail)
    {
        return 256*Math.pow(2,levelOfDetail);
    }

    /**
     * 地面分辨率。单位：米/像素
     * @param latitude 纬度
     * @param levelOfDetail 级别
     */
    public static double GroundResolution(double latitude, int levelOfDetail)
    {
        latitude = Clip(latitude, MinLatitude, MaxLatitude);
        return Math.cos(latitude * Math.PI / 180) * 2 * Math.PI * EarthRadius / MapSize(levelOfDetail);
    }
    /**
     * 地图比例尺
     * @param latitude 纬度
     * @param levelOfDetail 级别
     * @param screenDpi 屏幕分辨率
     */
    public static double MapScale(double latitude, int levelOfDetail, int screenDpi)
    {
        return GroundResolution(latitude, levelOfDetail) * screenDpi / 0.0254;
    }
    /**
     * 缩放级别换算地图分辨率
     * @param levelOfDetail 级别
     */
    public static double ZoomToResolution(int levelOfDetail){
        double resolution=0;
        switch(levelOfDetail){
            case 1:resolution=78271.5170;break;
            case 2:resolution=39135.7585;break;
            case 3:resolution=19567.8792;break;
            case 4:resolution=9783.9396;break;
            case 5:resolution=4891.9698;break;
            case 6:resolution=2445.9849;break;
            case 7:resolution=1222.9925;break;
            case 8:resolution=611.4962;break;
            case 9:resolution=305.7481;break;
            case 10:resolution=152.8741;break;
            case 11:resolution=76.4370;break;
            case 12:resolution=38.2185;break;
            case 13:resolution=19.1093;break;
            case 14:resolution=9.5546;break;
            case 15:resolution=4.7773;break;
            case 16:resolution=2.3887;break;
            case 17:resolution=1.1943;break;
            case 18:resolution=0.5972;break;
            case 19:resolution=0.2986;break;
            case 20:resolution=0.1493;break;
            case 21:resolution=0.0746;break;
            case 22:resolution=0.0373;break;
            case 23:resolution=0.0187;break;
        }
        return resolution;
    }
    /**
     * 地图分辨率换算缩放级别
     * @param resolution 分辨率
     */
    public static int ResolutionToZoom(double resolution){
        int levelOfDetail=0;
        if(resolution>=78271.5170)levelOfDetail=1;
        else if(resolution>=39135.7585)levelOfDetail=2;
        else if(resolution>=19567.8792)levelOfDetail=3;
        else if(resolution>=9783.9396)levelOfDetail=4;
        else if(resolution>=4891.9698)levelOfDetail=5;
        else if(resolution>=2445.9849)levelOfDetail=6;
        else if(resolution>=1222.9925)levelOfDetail=7;
        else if(resolution>=611.4962)levelOfDetail=8;
        else if(resolution>=305.7481)levelOfDetail=9;
        else if(resolution>=152.8741)levelOfDetail=10;
        else if(resolution>=76.4370)levelOfDetail=11;
        else if(resolution>=38.2185)levelOfDetail=12;
        else if(resolution>=19.1093)levelOfDetail=13;
        else if(resolution>=9.5546)levelOfDetail=14;
        else if(resolution>=4.7773)levelOfDetail=15;
        else if(resolution>=2.3887)levelOfDetail=16;
        else if(resolution>=1.1943)levelOfDetail=17;
        else if(resolution>=0.5972)levelOfDetail=18;
        else if(resolution>=0.2986)levelOfDetail=19;
        else if(resolution>=0.1493)levelOfDetail=20;

        return levelOfDetail;
    }
    public static Tile LatLongToTile(Coordinate pt, int levelOfDetail){
        Pixel pixel=LatLongToPixelXY(pt,levelOfDetail);
        Tile tile= PixelXYToTileXY(pixel);
        tile.setZ(levelOfDetail);
        return tile;
    }
    /**
     *
     * 经纬度坐标转换为屏幕坐标
     *
     * @param pt	经纬度坐标
     * @param levelOfDetail	缩放级别
     * @return Pixel 屏幕坐标
     */
    public static Pixel LatLongToPixelXY(Coordinate pt, int levelOfDetail){
        double latitude=pt.y;
        double longitude=pt.x;
        latitude = Clip(latitude, MinLatitude, MaxLatitude);
        longitude = Clip(longitude, MinLongitude, MaxLongitude);

        double x = (longitude + 180) / 360;
        double sinLatitude = Math.sin(latitude * Math.PI / 180);
        double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI);

        double mapSize = MapSize(levelOfDetail);
        int pixelX = (int) Clip(x * mapSize + 0.5, 0, mapSize - 1);
        int pixelY = (int) Clip(y * mapSize + 0.5, 0, mapSize - 1);
        return new Pixel(pixelX,pixelY);
    }
    /**
     *
     * 屏幕坐标转换为经纬度坐标
     *
     * @param pixel	屏幕坐标
     * @param levelOfDetail	缩放级别
     * @return LngLat 经纬度坐标
     */
    public static Coordinate PixelXYToLatLong(Pixel pixel, int levelOfDetail){
        long pixelX=pixel.getX();
        long pixelY=pixel.getY();
        double mapSize = MapSize(levelOfDetail);
        double x = (Clip(pixelX, 0, mapSize - 1) / mapSize) - 0.5;
        double y = 0.5 - (Clip(pixelY, 0, mapSize - 1) / mapSize);

        double latitude = 90 - 360 * Math.atan(Math.exp(-y * 2 * Math.PI)) / Math.PI;
        double longitude = 360 * x;
        return new Coordinate(longitude,latitude);
    }
    /**
     *
     * 像素坐标转换为所在的Tile的XY坐标值
     *
     * @param pixel	像素坐标
     * @return Tile 瓦片坐标
     */
    public static Tile PixelXYToTileXY(Pixel pixel){
        long tileX = pixel.getX() / 256;
        long tileY = pixel.getY() / 256;
        return new Tile(tileX,tileY);
    }
    /**
     *
     * 瓦片坐标转换为所在的Tile的XY坐标值
     *
     * @param tile	瓦片坐标
     * @return Pixel 像素坐标
     */
    public static Pixel TileXYToPixelXY(Tile tile){
        long pixelX= tile.getX() * 256;
        long pixelY= tile.getY() * 256;
        return new Pixel(pixelX,pixelY);
    }
    /**
     *
     * 瓦片坐标转换为QuadKey四叉树键值
     *
     * @param tile	瓦片坐标
     * @return String QuadKey四叉树键值
     */
    public static String TileXYToQuadKey(Tile tile){
        long tileX=tile.getX();
        long tileY=tile.getY();
        int levelOfDetail=tile.getZ();
        StringBuilder quadKey = new StringBuilder();
        for (int i = levelOfDetail; i > 0; i--){
            char digit = '0';
            int mask = 1 << (i - 1);
            if ((tileX & mask)!= 0){digit++;}
            if ((tileY & mask)!= 0){digit++;digit++;}
            quadKey.append(digit);
        }
        return quadKey.toString();
    }
    /**
     *
     * Tile坐标转换为所在的Tile的边框
     *
     * @param tile	瓦片坐标
     * @return Bounds 边框
     */
    public static Envelope TileXYToBounds(Tile tile){
        Pixel px= TileXYToPixelXY(tile);	//瓦片左上角坐标
        Pixel minPX=new Pixel(px.getX(),px.getY()+256);
        Pixel maxPX=new Pixel(px.getX()+256,px.getY());
        Coordinate sw= PixelXYToLatLong(minPX, tile.getZ());
        Coordinate ne= PixelXYToLatLong(maxPX, tile.getZ());
        return new Envelope(sw,ne);
    }
    /**
     *
     * Tile坐标转换为所在的Tile的墨卡托坐标边框
     *
     * @param tile	瓦片坐标
     */
    public static String TileXYToBBOX(Tile tile){
        double resoultion=ZoomToResolution(tile.getZ());
        double r = 20037508.342789244;
        double minX=tile.getX()*256*resoultion - r;
        double minY=(Math.pow(2,tile.getZ())-1-tile.getY())*256*resoultion - r;
        double maxX=(tile.getX()+1)*256*resoultion - r;
        double maxY=(Math.pow(2,tile.getZ())-tile.getY())*256*resoultion - r;
        return minX+","+minY+","+maxX+","+maxY;
    }
    /**
     *
     * QuadKey四叉树键值转换为瓦片坐标
     *
     * @param quadKey	QuadKey四叉树键值
     * @return Tile 瓦片坐标
     */
    public static Tile QuadKeyToTileXY(String quadKey){
        int tileX=0;
        int tileY=0;
        int levelOfDetail= quadKey.length();

        for (int i = levelOfDetail; i > 0; i--){
            int mask = 1 << (i - 1);
            switch (quadKey.charAt(levelOfDetail - i)){
                case '0':break;
                case '1':tileX |= mask;break;
                case '2':tileY |= mask;break;
                case '3':tileX |= mask;tileY |= mask;break;

                //default:throw new ArgumentException("Invalid QuadKey digit sequence.");
            }
        }
        return new Tile(tileX,tileY,levelOfDetail);
    }

    public static void main(String args[]){
        int z=ResolutionToZoom((5009377.085697312-2504688.542848654)/200);
        System.out.println(z);
    }

}

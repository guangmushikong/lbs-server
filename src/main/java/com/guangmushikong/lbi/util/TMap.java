package com.guangmushikong.lbi.util;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * 
 * 地图工具类
 * @version	1.0
 */
public class TMap {
	/**
	 * 
	 * 测距
	 * 
	 * @param a	经纬度坐标1
	 * @param b	经纬度坐标2
	 * @return 距离(米)
	 */
	public static double getDistance(Coordinate a, Coordinate b){
    	double c=a.y* Math.PI/180;
    	double d=b.y* Math.PI/180;
    	double dis=(Math.asin(Math.sqrt(Math.pow(Math.sin((c-d)/2),2)+ Math.cos(c)* Math.cos(d)* Math.pow(Math.sin((a.x-b.x)* Math.PI/180/2),2)))*12756274);
    	return dis;
    }
	/**
	 * 
	 * 根据偏移距离计算经纬度
	 * <p>根据原始点经纬度坐标、偏移量，计算偏移后的经纬度坐标	</p>
	 * @param pt	经纬度坐标
	 * @param w	东西方向的偏移量，向东为正，向西为负，单位：米
	 * @param s	南北方向的偏移量，向北为正，向南为负，单位：米
	 * @return 经纬度坐标
	 */
	public static Coordinate getLngLatByOffset(Coordinate pt,double w,double s){
    	double x,y;    	
    	x=pt.x+ Math.asin(Math.sin(Math.round(w)/12756274.0)/ Math.cos(pt.y* Math.PI/180))*360/ Math.PI;
    	y=pt.y+ Math.asin(Math.round(s)/12756274.0)*360/ Math.PI;
    	return  new Coordinate(x,y);
    }
	/**
	 * 
	 * 经纬度转墨卡托坐标
	 * @param pt	经纬度坐标
	 * @return 墨卡托坐标
	 */
	public static Coordinate lonLat2Mercator(Coordinate pt){
		double x = pt.x * 20037508.34/180;  ;
		double y = Math.log(Math.tan((90+pt.y)*Math.PI/360))/(Math.PI/180);
		y = y *20037508.34/180;
		return new Coordinate(x, y);
	}
	/**
	 * 
	 * 墨卡托坐标转经纬度
	 * @param pt 墨卡托坐标
	 * @return 经纬度
	 */
	public static Coordinate mercator2lonLat(Coordinate pt){
		double lng = pt.x / 20037508.34 * 180;
        double lat = pt.y / 20037508.34 * 180;
		lat = 180 / Math.PI * (2 * Math.atan(Math.exp(lat * Math.PI / 180)) - Math.PI / 2);
        return new Coordinate(lng, lat);
	}
}

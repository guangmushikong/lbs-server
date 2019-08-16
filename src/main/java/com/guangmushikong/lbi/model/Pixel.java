package com.guangmushikong.lbi.model;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * 屏幕像素坐标类，该类为基础类（单位：像素）。
 * <p>像素坐标系（Pixel Coordinates）单位。</p>
 * <p>以左上角为原点(0,0)，向右向下为正方向。</p>
 * @version	1.0
 * @author deyi
 */
@Getter
@Setter
public class Pixel {
    /**
     * 横向像素
     */
    long x;
    /**
     * 纵向像素
     */
    long y;

    public Pixel(){

    }

    /**
     * 根据给定参数构造Pixel的新实例
     * @param x 横向像素
     * @param y 纵向像素
     */
    public Pixel(long x, long y){
        this.x=x;
        this.y=y;
    }

    public String toString(){
        return "Pixel("+x+","+y+")";
    }
}

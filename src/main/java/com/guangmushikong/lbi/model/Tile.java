package com.guangmushikong.lbi.model;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * 瓦片类，该类为基础类（单位：块）。
 * <p>地图瓦片坐标系（Tile Coordinates）单位。</p>
 * <p>瓦片坐标系以左上角为原点(0, 0)，到右下角(2 ^ 图像级别 - 1, 2 ^ 图像级别 - 1)为止。</p>
 * @version	1.0
 * @author deyi
 */
@Getter
@Setter
public class Tile {
    /**
     * 横向瓦片数
     */
    long x;
    /**
     * 纵向瓦片数
     */
    long y;
    /**
     * 级别
     */
    int z;

    public Tile(){
    }

    /**
     * 根据给定参数构造Tile的新实例
     * @param x 横向瓦片数
     * @param y 纵向瓦片数
     */
    public Tile(long x, long y){
        this.x=x;
        this.y=y;
    }
    /**
     * 根据给定参数构造Tile的新实例
     * @param x 横向瓦片数
     * @param y 纵向瓦片数
     * @param z 级别
     */
    public Tile(long x, long y, int z){
        this.x=x;
        this.y=y;
        this.z=z;
    }
    @Override
    public String toString(){
        return "Tile("+x+","+y+","+z+")";
    }
}

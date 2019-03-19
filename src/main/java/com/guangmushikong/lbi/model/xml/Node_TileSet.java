package com.guangmushikong.lbi.model.xml;

import javax.xml.bind.annotation.XmlAttribute;

public class Node_TileSet {
    @XmlAttribute(name="href")
    String href;
    @XmlAttribute(name="units-per-pixel")
    String units_per_pixel;
    @XmlAttribute(name="order")
    String order;


    public Node_TileSet(String href, String units_per_pixel, String order){
        this.order=order;
        this.units_per_pixel=units_per_pixel;
        this.href=href;
    }
}

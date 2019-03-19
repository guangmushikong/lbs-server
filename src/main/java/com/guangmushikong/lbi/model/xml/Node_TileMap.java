package com.guangmushikong.lbi.model.xml;

import javax.xml.bind.annotation.XmlAttribute;

public class Node_TileMap {
    @XmlAttribute(name="title")
    String title;
    @XmlAttribute(name="srs")
    String srs;
    @XmlAttribute(name="group")
    String group;
    @XmlAttribute(name="profile")
    String profile;
    @XmlAttribute(name="href")
    String href;

    public Node_TileMap(String title, String srs, String profile, String href){
        this.title=title;
        this.srs=srs;
        this.profile=profile;
        this.href=href;
    }
    public void setGroup(String val){
        this.group=val;
    }
}

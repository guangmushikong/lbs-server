package com.guangmushikong.lbi.model.xml;

import javax.xml.bind.annotation.XmlAttribute;

public class Node_TileMapService {
    @XmlAttribute(name="title")
    String title;
    @XmlAttribute(name="version")
    String version;
    @XmlAttribute(name="href")
    String href;

    public Node_TileMapService(String title,String version, String href){
        this.title=title;
        this.version=version;
        this.href=href;
    }
}

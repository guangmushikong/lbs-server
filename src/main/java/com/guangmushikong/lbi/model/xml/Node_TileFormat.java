package com.guangmushikong.lbi.model.xml;

import javax.xml.bind.annotation.XmlAttribute;

public class Node_TileFormat {
    @XmlAttribute(name="width")
    int width;
    @XmlAttribute(name="height")
    int height;
    @XmlAttribute(name="mime-type")
    String mime_type;
    @XmlAttribute(name="extension")
    String extension;


    public void setWidth(int val) {
        this.width=val;
    }
    public void setHeight(int val) {
        this.height=val;
    }
    public void setMimeType(String val) {
        this.mime_type=val;
    }
    public void setExtension(String val) {
        this.extension=val;
    }

}

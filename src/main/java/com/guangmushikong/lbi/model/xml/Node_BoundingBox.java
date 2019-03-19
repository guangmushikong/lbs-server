package com.guangmushikong.lbi.model.xml;

import javax.xml.bind.annotation.XmlAttribute;

public class Node_BoundingBox {
    @XmlAttribute(name="minx")
    double minx;
    @XmlAttribute(name="miny")
    double miny;
    @XmlAttribute(name="maxx")
    double maxx;
    @XmlAttribute(name="maxy")
    double maxy;

    public void setMinX(double val) {
        this.minx=val;
    }
    public void setMinY(double val) {
        this.miny=val;
    }
    public void setMaxX(double val) {
        this.maxx=val;
    }
    public void setMaxY(double val) {
        this.maxy=val;
    }
}

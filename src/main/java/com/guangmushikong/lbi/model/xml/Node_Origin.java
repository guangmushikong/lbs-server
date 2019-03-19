package com.guangmushikong.lbi.model.xml;

import javax.xml.bind.annotation.XmlAttribute;

public class Node_Origin {
    @XmlAttribute(name="x")
    double x;
    @XmlAttribute(name="y")
    double y;

    public void setX(double val) {
        this.x=val;
    }
    public void setY(double val) {
        this.y=val;
    }

}

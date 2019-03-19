package com.guangmushikong.lbi.model.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class Node_TileSets {
    @XmlAttribute(name="profile")
    String profile;
    @XmlElement(name = "TileSet")
    List<Node_TileSet> tileSets;

    public void setProfile(String val) {
        this.profile = val;
    }
    public void setTileSets(List<Node_TileSet> val) {
        this.tileSets=val;
    }
}

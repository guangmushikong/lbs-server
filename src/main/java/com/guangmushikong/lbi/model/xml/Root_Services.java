package com.guangmushikong.lbi.model.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "Services")
public class Root_Services {
    @XmlElement(name = "TileMapService")
    List<Node_TileMapService> tileMapServiceList;

    public void setTileMapServices(List<Node_TileMapService> val) {
        this.tileMapServiceList = val;
    }
}

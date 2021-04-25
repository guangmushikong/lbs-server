package com.guangmushikong.lbi.model;

import com.vividsolutions.jts.geom.Geometry;
import lombok.Data;

@Data
public class KmlDO {
    String id;
    String name;
    String userName;
    String type;
    Geometry geometry;
}

package com.guangmushikong.lbi.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

/*************************************
 * Class Name: GeometryVO
 * Description:〈几何对象〉
 * @create 2018/5/14
 * @since 1.0.0
 ************************************/
@Getter
@Setter
public class GeometryVO {
    @JSONField(ordinal = 1)
    String type;
    @JSONField(ordinal = 2)
    JSONArray coordinates;
}

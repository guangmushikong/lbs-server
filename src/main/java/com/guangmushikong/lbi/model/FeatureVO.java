package com.guangmushikong.lbi.model;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/*************************************
 * Class Name: FeatureVO
 * Description:〈要素对象〉
 * @create 2018/5/14
 * @since 1.0.0
 ************************************/
@Data
public class FeatureVO {
    @JSONField(ordinal = 1)
    String type;
    @JSONField(ordinal = 2)
    GeometryVO geometry;
    @JSONField(ordinal = 3)
    JSONObject properties;
}

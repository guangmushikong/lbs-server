
package com.guangmushikong.lbi.model;

import com.vividsolutions.jts.geom.Geometry;
import lombok.Data;

/*************************************
 * Class Name: CustomDataSetDO
 * Description:〈自定义数据集〉
 * @author deyi
 * @since 1.0.0
 ************************************/
@Data
public class CustomDataSetDO {
    /**
     * 主键
     */
    long id;
    /**
     * 数据集名称
     */
    String name;
    /**
     * 项目ID
     */
    long projectId;
    /**
     * 用户ID
     */
    long userId;
    /**
     * 空间对象
     */
    Geometry geom;
}

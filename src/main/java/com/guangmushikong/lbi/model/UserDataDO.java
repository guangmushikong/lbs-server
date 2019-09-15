
package com.guangmushikong.lbi.model;

import lombok.Data;

/*************************************
 * Class Name: UserDataDO
 * Description:〈自定义数据VO〉
 * @author deyi
 * @since 1.0.0
 ************************************/
@Data
public class UserDataDO {
    /**
     * 数据ID
     */
    String uuid;
    /**
     * 数据集名称
     */
    String name;
    /**
     * 类型。0绘制数据，1轨迹数据
     */
    int type;
    /**
     * 项目ID
     */
    long projectId;
    /**
     * 用户ID
     */
    String userName;
    /**
     * 属性
     */
    String prop;
    /**
     * 空间对象（wkt格式hex）
     */
    String wkt;
}

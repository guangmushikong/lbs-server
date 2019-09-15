/* *************************************
 * Copyright (C), Navinfo
 * Package: com.guangmushikong.lbi.dao
 * Author: liumingkai
 * Date: Created in 2019/8/19 14:51
 **************************************/
package com.guangmushikong.lbi.dao;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;
import java.sql.PreparedStatement;
/*************************************
 * Class Name: UserDataDaoTest
 * Description:〈UserDataDaoTest〉
 * @author liumingkai
 * @since 1.0.0
 ************************************/
public class UserDataDaoTest {
    Connection conn;
    @Before
    public void init(){
        try{
            String url="jdbc:postgresql://111.202.109.210:5432/cateye";
            String user="cateye";
            String pwd="#Cateye@2019$";
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, user, pwd);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void 读数据()throws Exception{
        String sql="select * from data.liupanshui_point limit 10";
        Statement stat=conn.createStatement();
        Statement stat2=conn.createStatement();
        ResultSet rs=stat.executeQuery(sql);
        //String inSql="insert into t_custom_data(name,project_id,user_id,geom) values(?,?,?,?)";
        //PreparedStatement ps=conn.prepareStatement(inSql);
        while(rs.next()){
            int id=rs.getInt("id");
            String name=rs.getString("o_name");
            //byte[] bytes=rs.getBytes("geom");
            //WKBReader wkbReader=new WKBReader();
            //Geometry geom=wkbReader.read(bytes);
            String wkbStr=rs.getString("geom");
            Geometry geom=new WKBReader().read(WKBReader.hexToBytes(wkbStr));
            //geom.setSRID(4326);
            WKBWriter wkbWriter=new WKBWriter(2,true);
            byte[] wkb=wkbWriter.write(geom);
            String hexStr=bytesToHexString(wkb);
            System.out.println(id+"|"+name+"|"+geom.toText());
            /*ps.setString(1,name);
            ps.setLong(2,1L);
            ps.setLong(3,11L);
            ps.setString(4,hexStr);
            ps.addBatch();*/
            StringBuilder sb=new StringBuilder();
            sb.append("insert into t_custom_data(name,project_id,user_name,geom)");
            sb.append(" values('"+name+"',1,'admin','"+hexStr+"')");
            System.out.println("【sql】"+sb.toString());
            //String inSql="insert into t_custom_data(name,project_id,user_id,geom) values(";
            stat2.execute(sb.toString());

        }
        rs.close();
        stat.close();
        stat2.close();
        //ps.executeUpdate();
        //ps.close();

    }
    @After
    public void destroy()throws SQLException {
        if(conn!=null){
            conn.close();
        }
    }

    private static String bytesToHexString(byte[] bArr) {
        StringBuffer sb = new StringBuffer(bArr.length);
        String sTmp;

        for (int i = 0; i < bArr.length; i++) {
            sTmp = Integer.toHexString(0xFF & bArr[i]);
            if (sTmp.length() < 2)
                sb.append(0);
            sb.append(sTmp.toUpperCase());
        }

        return sb.toString();
    }
}

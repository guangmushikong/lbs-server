import com.lbi.model.Tile;
import com.lbi.util.TileSystem;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKBReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

public class testPG {
    Connection conn;
    @Before
    public void init(){
        try{
            Class.forName("org.postgresql.Driver");
            String url="jdbc:postgresql://111.202.109.210:5432/postgres";
            String user="postgres";
            String pwd="Cateye@2018";
            conn=DriverManager.getConnection(url,user,pwd);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void 获取瓦片坐标()throws Exception {
        Coordinate pt=new Coordinate(105.2378354,26.07574815);
        Tile tile= TileSystem.LatLongToTile(pt,13);
        System.out.println(tile.toString());
    }
    @Test
    public void test()throws Exception {
        String sql="select id,st_asewkb(geom) as wkb from liupanshui_point limit 10";
        Statement stat=conn.createStatement();
        ResultSet rs=stat.executeQuery(sql);
        while(rs.next()){
            int id=rs.getInt("id");
            //String name=rs.getString("o_name");
            byte[] wkb=rs.getBytes("wkb");

            WKBReader wkbReader=new WKBReader();
            Geometry geom=wkbReader.read(wkb);
            System.out.println(id+"|"+geom.toText());
        }
        rs.close();
        stat.close();

    }
    @After
    public void destroy()throws SQLException{
        if(conn!=null)conn.close();
    }
}

import com.guangmushikong.lbi.model.ServiceType;
import com.lbi.model.Tile;
import org.junit.Test;

public class testType {
    @Test
    public void test(){
        ServiceType type=ServiceType.TMS;
        System.out.println(type.name());
        System.out.println(type.toString());
        ServiceType type2=ServiceType.getByValue(2);
        ServiceType type3=ServiceType.valueOf("TMS");
        System.out.println(type2.name()+"|"+type2.getValue());
        System.out.println(type3.name()+"|"+type3.getValue());
    }
    @Test
    public void 瓦片计算(){
        int x=23;int y=20;int z=5;
        System.out.println(x+","+y+","+z);
        Tile tile=new Tile(x,y,z);
        int alterY=new Double(Math.pow(2,z)-1-y).intValue();
        System.out.println(x+","+alterY+","+z);
    }
}

import com.guangmushikong.lbi.model.ServiceType;

import com.guangmushikong.lbi.model.Tile;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.util.DigestUtils;

import java.security.MessageDigest;


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
    public void testMD5()throws Exception{
        String pwd="admin123";
        //pwd="123456";
        MessageDigestPasswordEncoder passEncoder = new MessageDigestPasswordEncoder("MD5");
        String encode=passEncoder.encode(pwd).trim();
        System.out.println("【MessageDigestPasswordEncoder】"+encode);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        encode=encoder.encode(pwd).trim();
        System.out.println("【BCryptPasswordEncoder】"+encode);
        MessageDigest md= MessageDigest.getInstance("MD5");
        byte[] input = pwd.getBytes();
        byte[] output = md.digest(input);
        String str = Base64.encodeBase64String(output);
        System.out.println("【md5】"+str);
        System.out.println("【base64】"+Base64.encodeBase64String(pwd.getBytes()));
        encode= DigestUtils.md5DigestAsHex(pwd.getBytes());
        System.out.println("【md5DigestAsHex】"+encode);

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

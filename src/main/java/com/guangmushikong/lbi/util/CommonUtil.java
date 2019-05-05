package com.guangmushikong.lbi.util;

import com.lbi.util.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CommonUtil {
    /**
     * 读取文件字节流
     * @param path 文件地址
     * @return
     * @throws IOException
     */
    public static byte[] fileToByteArray(String path)throws IOException {
        File file=new File(path);
        if(file.exists()){
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            return bos.toByteArray();
        }
        return null;
    }

    /**
     * 读取图片字节流
     * @param path
     * @return
     * @throws IOException
     */
    public static byte[] imageToByteArray(String path)throws IOException {
        File file=new File(path);
        if(file.exists()){
            BufferedImage image= ImageIO.read(file);
            if(image!=null){
                return ImageUtil.toByteArray(image);
            }
        }
        return null;
    }
}

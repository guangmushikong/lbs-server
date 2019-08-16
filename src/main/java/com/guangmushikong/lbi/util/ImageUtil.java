package com.guangmushikong.lbi.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class ImageUtil {
    public static byte[] toByteArray(BufferedImage image){
        try{
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", out);
            return out.toByteArray();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
    public static byte[] emptyImage() {
        try{
            BufferedImage image = new BufferedImage(256, 256,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            image = g2d.getDeviceConfiguration().createCompatibleImage(256, 256,
                    Transparency.TRANSLUCENT);
            g2d.dispose();
            g2d = image.createGraphics();
            g2d.dispose();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", out);
            return out.toByteArray();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}

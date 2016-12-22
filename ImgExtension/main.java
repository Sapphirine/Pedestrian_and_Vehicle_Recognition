package com.yuan;


import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.EnumSet;

/**
 * Created by park on 11/18/16.
 */
public class main {
    private static final int DEFAULT_START = 60091;
    private static final int DEFAULT_END = 60095;
    private static final String PATH_PREFIX = "/Users/park/Desktop/test/positive/img/";
    private static final String PATH_POSTFIX = ".jpg";

    public static void main(String[] args) throws IOException {
        for (int i = DEFAULT_START; i <= DEFAULT_END; i++) {
            //Image read
            BufferedImage image = imageRead(i);
            //do transform and save it
            for (MyTransformType type : EnumSet.range(MyTransformType.HorizontalZoomOut1, MyTransformType.GaussianNoise3)) {
                BufferedImage res = filer(image, type);
                imageSave(res, i, type.getNum());
            }
        }
    }
    public static BufferedImage imageRead(int i) throws IOException {
        String name = String.valueOf(i);
        String path = PATH_PREFIX + name + PATH_POSTFIX;
        File file = new File(path);
        return ImageIO.read(file);
    }
    public static void imageSave(BufferedImage res, int i, int idx) throws IOException {
        String name = String.valueOf(i) + "_" + String.valueOf(idx);
        String path = PATH_PREFIX + name + PATH_POSTFIX;
        File file = new File(path);
        ImageIO.write(res, "jpg", file);
    }
    public static BufferedImage filer(BufferedImage image, MyTransformType type) {
        MyTransform myTransform = new MyTransform();
        if (type == MyTransformType.GaussianNoise1) {
            return myTransform.addNoise(image, 1);
        }
        if (type == MyTransformType.GaussianNoise2) {
            return myTransform.addNoise(image, 2);
        }
        if (type == MyTransformType.GaussianNoise3) {
            return myTransform.addNoise(image, 3);
        }
        AffineTransform transform = myTransform.getTransform(type);
        AffineTransformOp op = new AffineTransformOp(transform, null);
        BufferedImage res = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        op.filter(image, res);
        res = CutImage.cutImage(res, type);
        return res;
    }
}

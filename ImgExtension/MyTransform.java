package com.yuan;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Created by park on 11/18/16.
 */
public class MyTransform {
    //I provide 9 types of transforms in this class,
    //which can be used by set the parameter of
    //getTransform method from 0 to 8
    //private static final int DEFAULT_HORIZONTAL = 100;
    //private static final int DEFAULT_VERTICAL = 100;
    //private static final double DEFAULT_DEGREE = 30;
    private static final double DEFAULT_MAG = 0.8;
    private static final double DEFAULT_FACTOR = 25;
    public AffineTransform getTransform(MyTransformType type) {
        switch(type) {
            case HorizontalZoomOut1:
                return horizontalZoomOut1();
            case HorizontalZoomOut2:
                return horizontalZoomOut2();
            case VerticalZoomOut1:
                return verticalZoomOut1();
            case VerticalZoomOut2:
                return verticalZoomOut2();
            case ZoomOut1:
                return ZoomOut1();
            case ZoomOut2:
                return ZoomOut2();
        }
        return null;
    }
    public BufferedImage addNoise(BufferedImage image, int para) {
        int width = image.getWidth();
        int height = image.getHeight();
        Random random = new Random();
        int[] inPixels = new int[width*height];
        int[] outPixels = new int[width*height];
        image.getRGB(0, 0, width, height, inPixels, 0, width);
        int index = 0;
        for(int row=0; row<height; row++) {
            int ta = 0, tr = 0, tg = 0, tb = 0;
            for(int col=0; col<width; col++) {
                index = row * width + col;
                ta = (inPixels[index] >>> 24) & 0xff;
                tr = (inPixels[index] >>> 16) & 0xff;
                tg = (inPixels[index] >>> 8) & 0xff;
                tb = inPixels[index] & 0xff;
                tr = addGNoise(tr, random, para);
                tg = addGNoise(tg, random, para);
                tb = addGNoise(tb, random, para);
                outPixels[index] = (ta << 24) | (tr << 16) | (tg << 8) | tb;
            }
        }
        BufferedImage res = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        res.setRGB(0, 0, width, height, outPixels, 0, width);
        return res;
    }
    private int addGNoise(int tr, Random random, int para) {
        int v, ran;
        boolean inRange = false;
        do {
            if (para == 1) {
                ran = (int) Math.round(random.nextGaussian() * DEFAULT_FACTOR / 2);
            } else if (para == 2) {
                ran = (int) Math.round(random.nextGaussian() * DEFAULT_FACTOR);
            } else {
                ran = (int) Math.round(random.nextGaussian() * DEFAULT_FACTOR * 2);
            }
            v = tr + ran;
            // check whether it is valid single channel value
            inRange = (v>=0 && v<=255);
            if (inRange) tr = v;
        } while (!inRange);
        return tr;
    }
    private AffineTransform horizontalZoomOut1() {
        double mag = DEFAULT_MAG / 2;
        return new AffineTransform(mag, 0, 0, 1, 0, 0);
    }
    private AffineTransform horizontalZoomOut2() {
        return new AffineTransform(DEFAULT_MAG, 0, 0, 1, 0, 0);
    }
    private AffineTransform verticalZoomOut1() {
        double mag = DEFAULT_MAG / 2;
        return new AffineTransform(1, 0, 0, mag, 0, 0);
//        double rad = Math.toRadians(DEFAULT_DEGREE);
//        return new AffineTransform(Math.cos(rad), -Math.sin(rad), Math.sin(rad), Math.cos(rad), 0, 0);
    }
    private AffineTransform verticalZoomOut2() {
        return new AffineTransform(1, 0, 0, DEFAULT_MAG, 0, 0);
    }
    private AffineTransform ZoomOut1() {
        double mag = DEFAULT_MAG / 2;
        return new AffineTransform(mag, 0, 0, mag, 0, 0);
    }
    private AffineTransform ZoomOut2() {
        return new AffineTransform(DEFAULT_MAG, 0, 0, DEFAULT_MAG, 0, 0);
    }
}

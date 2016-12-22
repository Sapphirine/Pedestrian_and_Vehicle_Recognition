package com.yuan;


import java.awt.image.BufferedImage;


/**
 * Created by park on 12/17/16.
 */
public class CutImage {
    public static BufferedImage cutImage(BufferedImage image, MyTransformType type) {
        int w = 640;
        int h = 360;
        switch(type) {
            case HorizontalZoomOut1:
                h = 360;
                w = 256;
                break;
            case HorizontalZoomOut2:
                h = 360;
                w = 512;
                break;
            case VerticalZoomOut1:
                h = 144;
                w = 640;
                break;
            case VerticalZoomOut2:
                h = 288;
                w = 640;
                break;
            case ZoomOut1:
                h = 144;
                w = 256;
                break;
            case ZoomOut2:
                h = 288;
                w = 512;
                break;
            default:
                break;
        }
        image = image.getSubimage(0, 0, w, h);
        return image;
    }
}

package com.yuan;

/**
 * Created by park on 11/18/16.
 */
public enum MyTransformType {
    HorizontalZoomOut1(0), HorizontalZoomOut2(1), VerticalZoomOut1(2), VerticalZoomOut2(3),
    ZoomOut1(4), ZoomOut2(5), GaussianNoise1(6), GaussianNoise2(7), GaussianNoise3(8);
    private final int num;
    MyTransformType(int num) {
        this.num = num;
    }
    public int getNum() {
        return num;
    }
}

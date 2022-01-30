package com.iratebear.apollo.imageutils;

import android.graphics.Bitmap;

public class ImageItem {
    private Bitmap bitmap;
    private int color;
    public ImageItem(Bitmap bitmap, int color) {
        this.color = color;
        this.bitmap = bitmap;
    }
    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getColor() {
        return color;
    }
}

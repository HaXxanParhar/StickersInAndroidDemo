package com.drudotstech.teststickers;

import android.graphics.Bitmap;
import android.graphics.Rect;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/2/2022 at 2:30 PM
 ******************************************************/


public class BitmapModel {

    private Bitmap bitmap;
    private int left, top, height, width;
    private Rect rect;

    public BitmapModel(Bitmap bitmap, Rect rect) {
        this.bitmap = bitmap;
        this.rect = rect;
        left = rect.left;
        top = rect.top;
        width = rect.right;
        height = rect.bottom;
    }

    public BitmapModel(Bitmap bitmap, int left, int top, int width, int height) {
        this.bitmap = bitmap;
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        this.rect = new Rect(left, top, width, height);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }
}

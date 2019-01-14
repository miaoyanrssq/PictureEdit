package cn.zgy.picture;

import android.graphics.Bitmap;

public class PictureUtils {

    static {
        System.loadLibrary("picture-edit");
    }

    public static native String stringFromJNI();

    /**
     * 美白
     */
    public static native void ndkMb(Bitmap bitmap, float brightness, float constraint);

    /**
     * 灰度
     */
    public static native void grayScale(Bitmap bitmap);

    /**
     * 高斯模糊 1:成功
     */
    public static native int gaussBlur(Bitmap bitmap);
}

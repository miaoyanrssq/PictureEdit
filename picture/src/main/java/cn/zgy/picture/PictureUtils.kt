package cn.zgy.picture

import android.graphics.Bitmap

object PictureUtils {

        init {
            System.loadLibrary("picture-edit")
        }

    external fun stringFromJNI() : String

    /**
     * 美白
     */
    external fun ndkMb(bitmap : Bitmap, brightness : Float, constraint : Float)

    /**
     * 灰度
     */
    external fun grayScale(bitmap : Bitmap)

    /**
     * 高斯模糊 1:成功
     */
    external fun gaussBlur(bitmap: Bitmap) : Int


}
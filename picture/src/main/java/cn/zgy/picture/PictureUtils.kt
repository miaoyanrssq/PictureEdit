package cn.zgy.picture

import android.graphics.Bitmap

class PictureUtils {

    companion object {
        init {
            System.loadLibrary("picture-edit")
            System.loadLibrary("compress")
        }
    }

    external fun stringFromJNI() : String

    /**
     * 美白
     */
    external fun ndkMb(bitmap : Bitmap, britness : Float, constrait : Float)

    /**
     * 灰度
     */
    external fun grayScale(bitmap : Bitmap)

    /**
     * 高斯模糊
     */
    external fun gaussBlur(bitmap: Bitmap)

    /**
     * 压缩
     */
    external fun compressBitmap(bitmap: Bitmap, quality: Int, filenameBytes: ByteArray, optimize: Boolean) : String
}
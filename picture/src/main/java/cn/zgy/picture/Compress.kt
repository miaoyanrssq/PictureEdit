package cn.zgy.picture

import android.graphics.Bitmap

class Compress {

    companion object {
        init {
            System.loadLibrary("compress")
        }
    }
    /**
     * 压缩
     */
    external fun compressBitmap(bitmap: Bitmap, quality: Int, filenameBytes: ByteArray, optimize: Boolean) : String
}
package cn.zgy.picture

class PictureUtils {

    companion object {
        init {
            System.loadLibrary("picture-edit")
        }
    }

    external fun stringFromJNI() : String

    external fun NdkMb(buffer : IntArray, width : Int, height : Int) : IntArray
}
package cn.zgy.picture

import android.graphics.Bitmap
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Message

class PictureEdit : Handler.Callback{

    private var mHandler: Handler = Handler(Looper.getMainLooper(), this)

    private var bitmap: Bitmap? = null
    private var curFile: String? = null
    private var outputFilename: String = Environment.getExternalStorageDirectory().path + "/Picture/"+ System.currentTimeMillis()/1000 + ".jpg"
    private var quality: Int = 50
    private var optimize: Boolean = true
    private var listener : OnCompressListener? = null
    private var maxSize: Int = 0

    companion object {

        private const val MSG_COMPRESS_SUCCESS = 0
        private const val MSG_COMPRESS_START = 1
        private const val MSG_COMPRESS_ERROR = 2

        fun create() : PictureEdit{
            return PictureEdit()
        }
    }


    /**
     * 待压缩bitmap，如果设置此值，则inputFile(curFile: String)设置无效
     */
    fun bitmap(bitmap: Bitmap): PictureEdit{
        this.bitmap = bitmap
        return this
    }

    /**
     * 待压缩图片路径，和bitmap(bitmap: Bitmap)不能同时设置，否则无效
     */
    fun inputFile(curFile: String):PictureEdit{
        this.curFile = curFile
        return this
    }

    /**
     * 输出图片路径
     */
    fun outputFile(filename : String): PictureEdit{
        outputFilename = filename
        return this
    }

    /**
     * 压缩质量，只针对未设置ignoreSize(maxSize : Int)的情况有效
     */
    fun quality(quality : Int): PictureEdit{
        this.quality = quality
        return this
    }

    /**
     * 是否采用哈夫曼算法，可在保持清晰度的情况下缩小5-10倍空间
     */
    fun optimize(optimize : Boolean): PictureEdit{
        this.optimize = optimize
        return this
    }

    /**
     * 监听回调
     */
    fun listener(listener: OnCompressListener): PictureEdit{
        this.listener = listener
        return this
    }

    /**
     * 最小可压缩size：kb
     */
    fun ignoreSize(maxSize : Int): PictureEdit{
        this.maxSize = maxSize
        return this
    }


    fun compress(){
        if(bitmap == null && curFile == null){
            return
        }
        mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_START))
        Thread(Runnable {
            var result: String
            if(bitmap != null) {
                if (maxSize == 0) {
                    result = Compress.compressBitmap(bitmap!!, quality, outputFilename.toByteArray(), optimize)
                } else {
                    result = Compress.compressBitmap(bitmap!!, outputFilename, maxSize, quality)
                }
            }else{
                result = Compress.compressBitmap(curFile!!, outputFilename, maxSize, quality)
            }
            if ("1".equals(result)){
                mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_SUCCESS, outputFilename))
            }else{
                mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_ERROR, RuntimeException("压缩失败")))
            }
        }).start()

    }


    override fun handleMessage(msg: Message?): Boolean {
        if (listener == null) return false
        when (msg?.what) {
            MSG_COMPRESS_START -> listener?.start()
            MSG_COMPRESS_SUCCESS -> listener?.onSuccess(msg.obj.toString() + "")
            MSG_COMPRESS_ERROR -> listener?.onError(msg.obj.toString())
        }
        return false
    }

    interface OnCompressListener {
        fun start()
        fun onSuccess(filePath: String)
        fun onError(e: String)
    }

    /**
     * 美白
     */
    fun ndkMb(bitmap : Bitmap, brightness : Float, constraint : Float){
        PictureUtils.ndkMb(bitmap, brightness, constraint)
    }

    /**
     * 灰度
     */
    fun grayScale(bitmap : Bitmap){
        PictureUtils.grayScale(bitmap)
    }

    /**
     * 高斯模糊
     */
    fun gaussBlur(bitmap: Bitmap): Int{
        return PictureUtils.gaussBlur(bitmap)
    }
}
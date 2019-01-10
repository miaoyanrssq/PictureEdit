package cn.zgy.picture

import android.graphics.Bitmap
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Message

class PictureEdit : Handler.Callback{

    private var mHandler: Handler = Handler(Looper.getMainLooper(), this)

    private var bitmap: Bitmap? = null
    private var outputFilename: String = Environment.getExternalStorageDirectory().path + "/Picture/"+ System.currentTimeMillis()/1000 + ".jpg"
    private var quality: Int = 50
    private var optimize: Boolean = true
    private var listener : OnCompressListener? = null;

    companion object {

        private const val MSG_COMPRESS_SUCCESS = 0
        private const val MSG_COMPRESS_START = 1
        private const val MSG_COMPRESS_ERROR = 2

        fun create() : PictureEdit{
            return PictureEdit()
        }
    }

    fun bitmap(bitmap: Bitmap): PictureEdit{
        this.bitmap = bitmap
        return this
    }

    fun outputFile(filename : String): PictureEdit{
        outputFilename = filename
        return this
    }
    fun quality(quality : Int): PictureEdit{
        this.quality = quality
        return this
    }
    fun optimize(optimize : Boolean): PictureEdit{
        this.optimize = optimize
        return this
    }
    fun listener(listener: OnCompressListener): PictureEdit{
        this.listener = listener
        return this
    }


    fun compress(){
        if(bitmap == null){
            return
        }
        mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_START))
        Thread(Runnable {
            var result = Compress().compressBitmap(bitmap!!,quality, outputFilename.toByteArray(), optimize)
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
}
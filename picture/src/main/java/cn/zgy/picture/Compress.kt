package cn.zgy.picture

import android.graphics.*
import android.media.ExifInterface
import java.io.*

object Compress {

        init {
            System.loadLibrary("compress")
        }

    /**
     * 压缩，native方法
     */
    external fun compressBitmap(bitmap: Bitmap, quality: Int, filenameBytes: ByteArray, optimize: Boolean): String

    /**
     * 通过JNI图片压缩把Bitmap保存到指定目录
     * @param image bitmap对象
     * @param filePath 要保存的指定目录
     * @param maxSize 最大图片大小
     * @author gaoyangzhen
     */
    fun compressBitmap(bitmap: Bitmap, targetFilePath: String, maxSize: Int, quality: Int): String {
        //获取尺寸压缩倍数
        var ratio = getRatioSize(bitmap.width, bitmap.height)
        //压缩bitmap到对应尺寸
        var result = Bitmap.createBitmap(bitmap.width / ratio, bitmap.height / ratio, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val rect = Rect(0, 0, bitmap.getWidth() / ratio, bitmap.getHeight() / ratio)
        canvas.drawBitmap(bitmap, null, rect, null)
        var stream: ByteArrayOutputStream = ByteArrayOutputStream()
        //用质量压缩法确定压缩比例
        var options = 100
        if (maxSize != 0) {
            result.compress(Bitmap.CompressFormat.JPEG, options, stream)
            // 循环判断如果压缩后图片是否大于maxSize,大于继续压缩
            while (stream.toByteArray().size / 1024 > maxSize) {
                stream.reset()
                options -= 10
                result.compress(Bitmap.CompressFormat.JPEG, options, stream)
            }
        }else{
            options = quality
        }
        var resultStr = compressBitmap(result, options, targetFilePath.toByteArray(), true)
        if (!result.isRecycled) {
            result.recycle()
        }
        return resultStr;
    }

    /**
     * 通过JNI图片压缩把Bitmap保存到指定目录
     * @param curFilePath 当前图片文件地址
     * @param targetFilePath 要保存的图片文件地址
     * @param maxSize 最大图片大小
     */
    fun compressBitmap(curFilePath: String, targetFilePath: String, maxSize: Int, quality: Int): String {
        //根据地址获取bitmap
        val result= getBitmapFromFile(curFilePath)
        val baos = ByteArrayOutputStream()
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var options = 100
        if (maxSize != 0) {
            result!!.compress(Bitmap.CompressFormat.JPEG, options, baos)
            // 循环判断如果压缩后图片是否大于maxSize,大于继续压缩
            while (baos.toByteArray().size / 1024 > maxSize) {
                // 重置baos即清空baos
                baos.reset()
                // 每次都减少10
                options -= 10
                // 这里压缩quality，把压缩后的数据存放到baos中
                result.compress(Bitmap.CompressFormat.JPEG, options, baos)
            }
        }
        else{
            options = quality
        }
        // JNI保存图片到SD卡 这个关键
        val resultStr = compressBitmap(result!!, options, targetFilePath.toByteArray(), true)
        // 释放Bitmap
        if (!result.isRecycled) {
            result.recycle()
        }
        return resultStr
    }


    /**
     * 通过文件路径读获取Bitmap防止OOM以及解决图片旋转问题
     * @param filePath 路径
     * @return bitmap
     */
    private fun getBitmapFromFile(filePath: String): Bitmap? {
        val newOpts = BitmapFactory.Options()
        newOpts.inJustDecodeBounds = true//只读边,不读内容
        BitmapFactory.decodeFile(filePath, newOpts)
        val w = newOpts.outWidth
        val h = newOpts.outHeight
        // 获取尺寸压缩倍数
        newOpts.inSampleSize = getRatioSize(w, h)
        newOpts.inJustDecodeBounds = false//读取所有内容
        newOpts.inDither = false
        newOpts.inPurgeable = true
        newOpts.inInputShareable = true
        newOpts.inTempStorage = ByteArray(32 * 1024)
        var bitmap: Bitmap? = null
        val file = File(filePath)
        var fs: FileInputStream? = null
        try {
            fs = FileInputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        try {
            if (fs != null) {
                bitmap = BitmapFactory.decodeFileDescriptor(fs.fd, null, newOpts)
                //旋转图片
                val photoDegree = readPictureDegree(filePath)
                if (photoDegree != 0) {
                    val matrix = Matrix()
                    matrix.postRotate(photoDegree.toFloat())
                    // 创建新的图片
                    bitmap = Bitmap.createBitmap(
                        bitmap!!, 0, 0,
                        bitmap.width, bitmap.height, matrix, true
                    )
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fs != null) {
                try {
                    fs.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return bitmap
    }

    /**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */

    private fun readPictureDegree(path: String): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path)
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return degree
    }

    /**
     * 计算缩放比
     * @param bitWidth 当前图片宽度
     * @param bitHeight 当前图片高度
     * @return int 缩放比
     */
    private fun getRatioSize(bitWidth: Int, bitHeight: Int): Int {
        // 图片最大分辨率
        val imageHeight = 1280
        val imageWidth = 960
        // 缩放比
        var ratio = 1
        // 缩放比,由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        if (bitWidth > bitHeight && bitWidth > imageWidth) {
            // 如果图片宽度比高度大,以宽度为基准
            ratio = bitWidth / imageWidth
        } else if (bitWidth < bitHeight && bitHeight > imageHeight) {
            // 如果图片高度比宽度大，以高度为基准
            ratio = bitHeight / imageHeight
        }
        // 最小比率为1
        if (ratio <= 0)
            ratio = 1
        return ratio
    }
}
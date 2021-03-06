package cn.zgy.pictureedit

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import cn.zgy.picture.Compress
import cn.zgy.picture.PictureEdit
import cn.zgy.picture.PictureEdit.OnCompressListener
import cn.zgy.picture.PictureUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity(), OnCompressListener {


    /** SD卡根目录  */
    private val externalStorageDirectory = Environment.getExternalStorageDirectory().path + "/Picture"+ "/"

    lateinit var bitmap : Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hello.text = PictureUtils.stringFromJNI()
        initView()
    }

    private fun initView() {
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.mv)
        yuantu.setOnClickListener {
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.mv)
            image.setImageBitmap(bitmap)
        }

        mb.setOnClickListener {
            ndkMB(bitmap)
        }

        grey.setOnClickListener {
            greyScale(bitmap)
//            PictureEdit.create().compress()
        }

        gauss.setOnClickListener {
            gaussBlur(bitmap)
        }

        compress.setOnClickListener {
//            compress(bitmap, externalStorageDirectory + System.currentTimeMillis()/1000 + ".jpg")
            var file = File(externalStorageDirectory)
            if(!file.exists()) {
                file.mkdirs()
            }
            checkPermission()
        }
    }

    /**
     * 6.0 权限申请
     */
    private fun checkPermission() {
        if (checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission
                        .WRITE_EXTERNAL_STORAGE
                ), 100
            )
        } else {
            PictureEdit.create()
                .bitmap(bitmap)
                .inputFile(externalStorageDirectory + 1547174972 + ".jpg")
                .outputFile(externalStorageDirectory + System.currentTimeMillis()/1000 + ".jpg")
                .quality(30)
                .optimize(true)
                .listener(this)
                .ignoreSize(100)
                .compress()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                compress(bitmap, externalStorageDirectory + System.currentTimeMillis()/1000 + ".jpg")
                PictureEdit.create()
                    .bitmap(bitmap)
                    .outputFile(externalStorageDirectory + System.currentTimeMillis()/1000 + ".jpg")
                    .quality(30)
                    .optimize(true)
                    .listener(this)
                    .ignoreSize(400)
                    .compress()
            }
        }
    }


    fun ndkMB(bitmap: Bitmap){
        PictureEdit.create().ndkMb(bitmap, 0.2f, 0.2f)
        image.setImageBitmap(bitmap)

    }

    fun greyScale(bitmap: Bitmap){
        PictureEdit.create().grayScale(bitmap)
        image.setImageBitmap(bitmap)
    }

    fun gaussBlur(bitmap: Bitmap){
        PictureEdit.create().gaussBlur(bitmap)
        image.setImageBitmap(bitmap)
    }

    fun compress(bitmap: Bitmap, outputFileName: String){
        var result = Compress.compressBitmap(bitmap,20, outputFileName.toByteArray(), true)
        if("1".equals(result)){
            image2.setImageBitmap(BitmapFactory.decodeFile(outputFileName))
        }
    }


    override fun start() {
    }

    override fun onSuccess(filePath: String) {
        image2.setImageBitmap(BitmapFactory.decodeFile(filePath))
    }

    override fun onError(e: String) {
    }
}

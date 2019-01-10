package cn.zgy.pictureedit

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import cn.zgy.picture.PictureUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    /** SD卡根目录  */
    private val externalStorageDirectory = Environment.getExternalStorageDirectory().path + "/Picture"+ "/"

    lateinit var bitmap : Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hello.text = PictureUtils().stringFromJNI()
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
        }

        gauss.setOnClickListener {
            gaussBlur(bitmap)
        }

        compress.setOnClickListener {
//            compress(bitmap, externalStorageDirectory + System.currentTimeMillis()/1000 + ".jpg")
            var file = File(externalStorageDirectory)
            file.mkdirs()
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
            compress(bitmap, externalStorageDirectory + System.currentTimeMillis()/1000 + ".jpg")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                compress(bitmap, externalStorageDirectory + System.currentTimeMillis()/1000 + ".jpg")
            }
        }
    }


    fun ndkMB(bitmap: Bitmap){
        PictureUtils().ndkMb(bitmap, 0.2f, 0.2f)
        image.setImageBitmap(bitmap)

    }

    fun greyScale(bitmap: Bitmap){
        PictureUtils().grayScale(bitmap)
        image.setImageBitmap(bitmap)
    }

    fun gaussBlur(bitmap: Bitmap){
        PictureUtils().gaussBlur(bitmap)
        image.setImageBitmap(bitmap)
    }

    fun compress(bitmap: Bitmap, outputFileName: String){
        var result = PictureUtils().compressBitmap(bitmap,20, outputFileName.toByteArray(), true)
        if("1".equals(result)){
            image2.setImageBitmap(BitmapFactory.decodeFile(outputFileName))
        }
    }
}

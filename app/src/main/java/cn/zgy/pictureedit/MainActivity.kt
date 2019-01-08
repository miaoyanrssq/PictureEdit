package cn.zgy.pictureedit

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import cn.zgy.picture.PictureUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hello.text = PictureUtils().stringFromJNI()
        initView()
    }

    private fun initView() {
        var bitmap : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.mv)
        yuantu.setOnClickListener {
            image.setImageBitmap(bitmap)
        }

        mb.setOnClickListener {
            NdkMB(bitmap)
        }
    }


    fun NdkMB(bitmap: Bitmap){
        var width = bitmap.width
        var height = bitmap.height
        var buffer = IntArray(width * height)
        bitmap.getPixels(buffer, 0, width, 1, 1, width-1, height-1)
        var ndkImage = PictureUtils().NdkMb(buffer, width, height)
        var ndkbitmap = Bitmap.createBitmap(ndkImage, width, height, Bitmap.Config.ARGB_8888)
        image.setImageBitmap(ndkbitmap)

    }
}

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
}

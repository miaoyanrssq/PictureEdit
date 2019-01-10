//
// Created by 高阳· 甄 on 2019/1/8.
//

#include <jni.h>
#include <string>
#include <stdlib.h>
#include <android/bitmap.h>
#include "gauss.h"

extern "C" JNIEXPORT jstring JNICALL
Java_cn_zgy_picture_PictureUtils_stringFromJNI(
        JNIEnv *env, jobject
) {
    std::string hello = "Hello from c++";
    return env->NewStringUTF(hello.c_str());
};


/**
 * 图片美白，增加亮度和对比度
 */
extern "C" JNIEXPORT void JNICALL
Java_cn_zgy_picture_PictureUtils_ndkMb
        (JNIEnv *env, jobject, jobject bitmap, jfloat brightness, jfloat constrait) {

    AndroidBitmapInfo info = {0};
    AndroidBitmap_getInfo(env, bitmap, &info);
    int *buf = NULL;
    AndroidBitmap_lockPixels(env, bitmap, (void **) &buf);

    int width = info.width;
    int height = info.height;
    int32_t *srcPixs = buf;
    int color;
    int a, r, g, b;
    int bri = (int) (brightness * 255);
    float cos = 1.f + constrait;

    for (int x = 0; x < height; x++) {
        for (int y = 0; y < width; y++) {
            color = srcPixs[width * x + y];
            a = color >> 24;
            r = (color >> 16) & 0xFF;
            g = (color >> 8) & 0xFF;
            b = color & 0xFF;

            int ri = r + bri;
            int gi = g + bri;
            int bi = b + bri;

            r = ri > 255 ? 255 : (ri < 0 ? 0 : ri);
            g = gi > 255 ? 255 : (gi < 0 ? 0 : gi);
            b = bi > 255 ? 255 : (bi < 0 ? 0 : bi);

            ri = r - 128;
            gi = g - 128;
            bi = b - 128;

            ri = (int) (ri * cos);
            gi = (int) (gi * cos);
            bi = (int) (bi * cos);

            ri = ri + 128;
            gi = gi + 128;
            bi = bi + 128;

            r = ri > 255 ? 255 : (ri < 0 ? 0 : ri);
            g = gi > 255 ? 255 : (gi < 0 ? 0 : gi);
            b = bi > 255 ? 255 : (bi < 0 ? 0 : bi);

            color = (a << 24) | (r << 16) | (g << 8) | b;
            srcPixs[width * x + y] = color;
        }
    }
    AndroidBitmap_unlockPixels(env, bitmap);
}

/**
 * 灰度
 */
extern "C" JNIEXPORT void JNICALL
Java_cn_zgy_picture_PictureUtils_grayScale(
        JNIEnv *env, jobject, jobject bitmap) {

    AndroidBitmapInfo info = {0};
    AndroidBitmap_getInfo(env, bitmap, &info);
    int *buf = NULL;
    AndroidBitmap_lockPixels(env, bitmap, (void **) &buf);

    int w = info.width;
    int h = info.height;
    int32_t *srcPixs = buf;
    int alpha = 0xFF << 24;
    int i, j;
    int color;
    int red, green, blue;
    for (i = 0; i < h; i++) {
        for (j = 0; j < w; j++) {
            color = srcPixs[w * i + j];
            red = (color >> 16) & 0xFF;
            green = (color >> 8) & 0xFF;
            blue = color & 0xFF;

            color = (red + green + blue) / 3;
            color = alpha | (color << 16) | (color << 8) | color;
            srcPixs[w * i + j] = color;
        }
    }
    AndroidBitmap_unlockPixels(env, bitmap);

}

/**
 * 高斯模糊
 */
extern "C" JNIEXPORT void JNICALL
Java_cn_zgy_picture_PictureUtils_gaussBlur(
        JNIEnv *env, jobject, jobject bitmap) {

    AndroidBitmapInfo info = {0};
    AndroidBitmap_getInfo(env, bitmap, &info);
    int *buf = NULL;
    AndroidBitmap_lockPixels(env, bitmap, (void **) &buf);
    gaussBlur1(buf, info.width, info.height, 50);
    AndroidBitmap_unlockPixels(env, bitmap);
}

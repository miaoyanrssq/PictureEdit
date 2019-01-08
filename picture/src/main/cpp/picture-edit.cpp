//
// Created by 高阳· 甄 on 2019/1/8.
//

#include <jni.h>
#include <string>
#include <stdlib.h>

extern "C" JNIEXPORT jstring JNICALL
Java_cn_zgy_picture_PictureUtils_stringFromJNI(
        JNIEnv *env, jobject
        ){
    std::string hello = "Hello from c++";
    return env ->NewStringUTF(hello.c_str());
};


/**
 * 图片美白，增加亮度和对比度
 */
extern "C" JNIEXPORT jintArray JNICALL
Java_cn_zgy_picture_PictureUtils_NdkMb
        (JNIEnv * env, jobject , jintArray buffer, jint width, jint height){
    jint* source = env->GetIntArrayElements(buffer, 0);
    float brightness = 0.4f;
    float constrait = 0.4f;
    int a, r, g, b;
    int bri = (int) (brightness * 255);
    float cos = 1.f + constrait;
    int newSize = width * height;

    for(int x=0 ; x< width ; x++){
        for(int y=0 ; y<height ; y++){
            int color = source[y*width + x];
            a = color >> 24;
            r = (color >> 16) & 0xFF;
            g = (color >> 8) & 0xFF;
            b = color & 0xFF;

            int ri = r + bri;
            int gi = g+ bri;
            int bi = b + bri;

            r = ri > 255 ? 255 : (ri < 0 ? 0 : ri);
            g = gi > 255 ? 255 : (gi < 0 ? 0 : gi);
            b = bi > 255 ? 255 : (bi < 0 ? 0 : bi);

            ri = r - 128;
            gi = g - 128;
            bi = b - 128;

            ri = (int)(ri * cos);
            gi = (int)(gi * cos);
            bi = (int)(bi * cos);

            ri = ri + 128;
            gi = gi + 128;
            bi = bi + 128;

            r = ri > 255 ? 255 : (ri < 0 ? 0 : ri);
            g = gi > 255 ? 255 : (gi < 0 ? 0 : gi);
            b = bi > 255 ? 255 : (bi < 0 ? 0 : bi);

            source[y*width + x] = (a << 24) | (r << 16) | (g << 8) | b;
        }
    }

    jintArray result = env -> NewIntArray(newSize);
    env->SetIntArrayRegion(result, 0, newSize, source);

    env-> ReleaseIntArrayElements(buffer, source, 0);

    return result;

}
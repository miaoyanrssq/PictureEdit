//
// Created by 高阳· 甄 on 2019/1/10.图片压缩算法
//
#include <string.h>
#include <android/bitmap.h>
#include <android/log.h>
#include <jni.h>
#include <stdio.h>
#include <setjmp.h>
#include <math.h>
#include <stdint.h>
#include <time.h>
#include <sys/stat.h>

extern "C" {
#include "jpeglib.h"
#include "cdjpeg.h"        /* Common decls for cjpeg/djpeg applications */
#include "jversion.h"        /* for version message */
#include "android/config.h"
}


#ifndef PICTUREEDIT_COMPRESS_H
#define PICTUREEDIT_COMPRESS_H

#endif //PICTUREEDIT_COMPRESS_H

#define LOG_TAG "jni"
#define LOGW(...)  __android_log_write(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#define true 1
#define false 0

typedef uint8_t BYTE;

typedef uint8_t BYTE;
char *error;
struct my_error_mgr {
    struct jpeg_error_mgr pub;
    jmp_buf setjmp_buffer;
};

typedef struct my_error_mgr *my_error_ptr;


METHODDEF(void)
my_error_exit(j_common_ptr cinfo) {
my_error_ptr myerr = (my_error_ptr) cinfo->err;
(*cinfo->err->output_message)(cinfo);
error = const_cast<char *>(myerr->pub.jpeg_message_table[myerr->pub.msg_code]);
LOGE("jpeg_message_table[%d]:%s", myerr->pub.msg_code, myerr->pub.jpeg_message_table[myerr->pub.msg_code]);
longjmp(myerr->setjmp_buffer, 1);
}

int generateJPEG(BYTE *data, int w, int h, int quality, const char *outfilename, jboolean optimize) {
    int nComponent = 3;

    struct jpeg_compress_struct jcs;
    struct my_error_mgr jem;

    jcs.err = jpeg_std_error(&jem.pub);
    jem.pub.error_exit = my_error_exit;
    if (setjmp(jem.setjmp_buffer)) {
        return 0;
    }
    jpeg_create_compress(&jcs);
    FILE *f = fopen(outfilename, "wb");
    if (f == NULL) {
        return 0;
    }
    jpeg_stdio_dest(&jcs, f);
    jcs.image_width = w;
    jcs.image_height = h;
    if (optimize) {
        LOGI("optimize==ture");
    } else {
        LOGI("optimize==false");
    }
    jcs.arith_code = false;
    jcs.input_components = nComponent;
    if (nComponent == 1) {
        jcs.in_color_space = JCS_GRAYSCALE;
    } else {
        jcs.in_color_space = JCS_RGB;
    }
    jpeg_set_defaults(&jcs);
    jcs.optimize_coding = optimize;
    jpeg_set_quality(&jcs, quality, true);
    jpeg_start_compress(&jcs, TRUE);
    JSAMPROW row_pointer[1];
    int row_stride;
    row_stride = jcs.image_width * nComponent;
    while (jcs.next_scanline < jcs.image_height) {
        row_pointer[0] = &data[jcs.next_scanline * row_stride];
        jpeg_write_scanlines(&jcs, row_pointer, 1);
    }
    jpeg_finish_compress(&jcs);
    jpeg_destroy_compress(&jcs);
    fclose(f);

    return 1;


}

char *jstrinTostring(JNIEnv *env, jbyteArray barr) {
    char *rtn = NULL;
    jsize alen = env->GetArrayLength(barr);
    jbyte *ba = env->GetByteArrayElements(barr, 0);
    if (alen > 0) {
        rtn = (char *) malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}

void jstringTostring(JNIEnv *env, jstring jstr, char *output, int *de_len) {
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("utf-8");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes",
                                     "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid,
                                                         strencode);
    jsize alen = env->GetArrayLength(barr);
    *de_len = alen;
    jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        output = (char *) malloc(alen + 1);
        memcpy(output, ba, alen);
        output[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
}



/**
 * 图片压缩
 */
extern "C" JNIEXPORT jstring JNICALL
Java_cn_zgy_picture_PictureUtils_compressBitmap(
        JNIEnv *env, jobject, jobject bitmap, jint quality, jbyteArray fileNameBytes, jboolean optimize) {

    AndroidBitmapInfo info;
    BYTE *pixels;
    int ret;
    BYTE *data;
    BYTE *temdata;
    char *fileName = jstrinTostring(env, fileNameBytes);
    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return env->NewStringUTF("0");;
    }
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, (void **) &pixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }
    int w = info.width;
    int h = info.height;

    BYTE r, g, b;
    data = static_cast<BYTE *>(malloc(w * h * 3));
    temdata = data;
    int i = 0, j = 0;
    int color;
    for (i = 0; i < h; i++) {
        for (j = 0; j < w; j++) {
            color = *((int *) pixels);
            r = ((color & 0x00FF0000) >> 16);
            g = ((color & 0x0000FF00) >> 8);
            b = color & 0x000000FF;
            *data = b;
            *(data + 1) = g;
            *(data + 2) = r;
            data = data + 3;
            pixels += 4;

        }

    }
    AndroidBitmap_unlockPixels(env, bitmap);
    int result = generateJPEG(temdata, w, h, quality, fileName, optimize);
    free(temdata);
    if (result == 0) {
        jstring resultCode = env->NewStringUTF(error);
        error = NULL;
        return resultCode;
    }

    return env->NewStringUTF("1");
}



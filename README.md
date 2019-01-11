# PictureEdit
jni图片处理


## 调用流程
```java
PictureEdit.create()
                .bitmap(bitmap)
                .outputFile(externalStorageDirectory + System.currentTimeMillis()/1000 + ".jpg")
                .quality(30)
                .optimize(true)
                .listener(this)
                .ignoreSize(100)
                .compress()
```
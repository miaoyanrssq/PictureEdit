# PictureEdit
jni图片处理

#使用
```groovy
implementation 'cn.zgy.picture:pictureEdit:0.0.1'
```

## 最新版本

(最新版本)[https://bintray.com/miaoyanrssq/maven/pictureEdit/_latestVersion]

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
# PictureEdit
jni图片处理，采用哈夫曼算法，按照苹果图片压缩的方式，保证清晰度的前提下压缩图片

#使用

java版

```groovy
implementation 'cn.zgy.picture:pictureEdit:0.0.3'
```

kotlin版

```groovy
implementation 'cn.zgy.picture:pictureEdit-kotlin:0.0.3'
```



## 最新版本

(最新版本)[https://bintray.com/miaoyanrssq/maven/pictureEdit/_latestVersion]

## 调用流程
```java
PictureEdit.create()
                .bitmap(bitmap)
                .outputFile(outputFile)
                .quality(30)
                .optimize(true)
                .listener(this)
                .ignoreSize(100)
                .compress()
```
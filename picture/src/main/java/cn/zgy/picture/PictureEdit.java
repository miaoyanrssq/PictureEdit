package cn.zgy.picture;


import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class PictureEdit implements Handler.Callback {

    private static final int MSG_COMPRESS_SUCCESS = 0;
    private static final int MSG_COMPRESS_START = 1;
    private static final int MSG_COMPRESS_ERROR = 2;

    private Handler mHandler = new Handler(Looper.getMainLooper(), this);

    private Bitmap bitmap = null;
    private String curFile = null;
    private String outputFilename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Picture/" + System.currentTimeMillis() / 1000 + ".jpg";
    private int quality = 50;
    private boolean optimize = true;
    private OnCompressListener listener = null;
    private int maxSize = 0;


    public static PictureEdit create(){
        return new PictureEdit();
    }

    /**
     * 待压缩bitmap，如果设置此值，则inputFile(curFile: String)设置无效
     */
    public PictureEdit bitmap(Bitmap bitmap){
        this.bitmap = bitmap;
        return this;
    }

    /**
     * 待压缩图片路径，和bitmap(bitmap: Bitmap)不能同时设置，否则无效
     */
    public PictureEdit inputFile(String curFile){
        this.curFile = curFile;
        return this;
    }

    /**
     * 输出图片路径
     */
    public PictureEdit outputFile(String outputFilename){
        this.outputFilename = outputFilename;
        return this;
    }
    /**
     * 压缩质量，只针对未设置ignoreSize(maxSize : Int)的情况有效
     */
    public PictureEdit quality(int quality){
        this.quality = quality;
        return this;
    }
    /**
     * 是否采用哈夫曼算法，可在保持清晰度的情况下缩小5-10倍空间
     */
    public PictureEdit optimize(boolean optimize){
        this.optimize = optimize;
        return this;
    }
    /**
     * 监听回调
     */
    public PictureEdit listener(OnCompressListener listener){
        this.listener = listener;
        return this;
    }
    /**
     * 最小可压缩size：kb
     */
    public PictureEdit ignoreSize(int maxSize ){
        this.maxSize = maxSize;
        return this;
    }

    public void compress(){
        if(bitmap == null && curFile == null){
            return;
        }
        mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_START));
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result;
                if(bitmap != null) {
                    if (maxSize == 0) {
                        result = Compress.compressBitmap(bitmap, quality, outputFilename.getBytes(), optimize);
                    } else {
                        result = Compress.compressBitmap(bitmap, outputFilename, maxSize, quality);
                    }
                }else{
                    result = Compress.compressBitmap(curFile, outputFilename, maxSize, quality);
                }
                if ("1".equals(result)){
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_SUCCESS, outputFilename));
                }else{
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_ERROR, new RuntimeException("压缩失败")));
                }
            }
        }).start();
    }

    @Override
    public boolean handleMessage(Message message) {
        if (listener == null) {
            return false;
        }
        switch (message.what) {
            case MSG_COMPRESS_START:
                listener.start();
                break;
            case MSG_COMPRESS_SUCCESS:
                listener.onSuccess(message.obj.toString() + "");
                break;
            case MSG_COMPRESS_ERROR:
                listener.onError(message.obj.toString());
                break;
            default:
                return false;
        }

        return false;
    }

    public interface OnCompressListener {
        void start();

        void onSuccess(String filePath);

        void onError(String e);
    }

    /**
     * 美白
     */
    public void ndkMb(Bitmap bitmap, Float brightness, Float constraint) {
        PictureUtils.ndkMb(bitmap, brightness, constraint);
    }

    /**
     * 灰度
     */
    public void grayScale(Bitmap bitmap) {
        PictureUtils.grayScale(bitmap);
    }

    /**
     * 高斯模糊
     */
    public int gaussBlur(Bitmap bitmap) {
        return PictureUtils.gaussBlur(bitmap);
    }
}

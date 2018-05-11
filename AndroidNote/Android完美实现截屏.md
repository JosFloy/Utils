# Android完美实现截屏

很多app都有截屏的需求，当你遇到产品经理给你提出这个需求时，你搜索了一下： 
1.取View的cacheDrawable 来实现截屏，这种方案，没有兼容性问题，但是缺点有两个：

> - 不能截状态栏
> - 遇到SurfaceView没辙，surfaceview需要用mediaplay手动取一帧buffer才行。
> - 不能在后台serivce中使用，因为主要依托于view。 

2.java代run一个 adb 命令截屏。

> - 需要root。

#  

我们之前也有这个需求，这个需求后来分给另外一个人做，当时他给我的答复就是这样子，这个我当时也是这么想的，毕竟以前调研过这个功能。 
然后，我想想，好像我记得5.0以后可以直接录制屏幕为视频的，觉得这个还是自己搜索一下吧。于是乎，我就搜索了一下，发现android 5.0以后开放了录屏API，那么所有的5.0以后的机器都应该取视频中的一帧数据，这样子我就可以实现截屏了。 

这种方式的优点：

> - 可以后台，不单单只能自己 的app里面的页面，
> - 可以截状态栏了。

缺点：

> - 无法兼容5.0之前机器，这点可以不用太在意。
> - 需要一个先弹窗让用户允许。

```
5.0以前的机器，我们全民TV的用户统计数据显示，有30%，但是从运营角度考虑 觉得这些用户都可以抛弃，就像多年之前我们做开发还从2.2开始兼容一样，那些用户的使用频率，话费欲望都很低，等于将是粉
```

那么，我们就用这个API实现吧！

Shotter.java

```java
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;


/**
 * Created by wei on 16-12-1.
 */
public class Shotter {

    private final SoftReference<Context> mRefContext;
    private ImageReader mImageReader;

    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;

    private String mLocalUrl = "";

    private OnShotListener mOnShotListener;


    public Shotter(Context context, Intent data) {
        this.mRefContext = new SoftReference<>(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


            mMediaProjection = getMediaProjectionManager().getMediaProjection(Activity.RESULT_OK,
                    data);

            mImageReader = ImageReader.newInstance(
                    getScreenWidth(),
                    getScreenHeight(),
                    PixelFormat.RGB_565,// a pixel两节省一些内存 个2个字节 此处RGB_565 必须和下面 buffer处理一致的格式
                    1);
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void virtualDisplay() {

        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                getScreenWidth(),
                getScreenHeight(),
                Resources.getSystem().getDisplayMetrics().densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);

    }

    public void startScreenShot(OnShotListener onShotListener, String loc_url) {
        mLocalUrl = loc_url;
        startScreenShot(onShotListener);
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void startScreenShot(OnShotListener onShotListener) {

        mOnShotListener = onShotListener;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            virtualDisplay();

            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        Image image = mImageReader.acquireLatestImage();

                                        AsyncTaskCompat.executeParallel(new SaveTask(), image);
                                    }
                                },
                    300);

        }

    }


    public class SaveTask extends AsyncTask<Image, Void, Bitmap> {

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected Bitmap doInBackground(Image... params) {

            if (params == null || params.length < 1 || params[0] == null) {

                return null;
            }

            Image image = params[0];

            int width = image.getWidth();
            int height = image.getHeight();
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            //每个像素的间距
            int pixelStride = planes[0].getPixelStride();
            //总的间距
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height,
                    Bitmap.Config.RGB_565);
            bitmap.copyPixelsFromBuffer(buffer);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
            image.close();
            File fileImage = null;
            if (bitmap != null) {
                try {

                    if (TextUtils.isEmpty(mLocalUrl)) {
                        mLocalUrl = getContext().getExternalFilesDir("screenshot").getAbsoluteFile()
                                +
                                "/"
                                +
                                SystemClock.currentThreadTimeMillis() + ".png";
                    }
                    fileImage = new File(mLocalUrl);

                    if (!fileImage.exists()) {
                        fileImage.createNewFile();
                    }
                    FileOutputStream out = new FileOutputStream(fileImage);
                    if (out != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    fileImage = null;
                } catch (IOException e) {
                    e.printStackTrace();
                    fileImage = null;
                }
            }

            if (fileImage != null) {
                return bitmap;
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }

            if (mVirtualDisplay != null) {
                mVirtualDisplay.release();
            }

            if (mOnShotListener != null) {
                mOnShotListener.onFinish();
            }

        }
    }


    private MediaProjectionManager getMediaProjectionManager() {

        return (MediaProjectionManager) getContext().getSystemService(
                Context.MEDIA_PROJECTION_SERVICE);
    }

    private Context getContext() {
        return mRefContext.get();
    }


    private int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


    // a  call back listener
    public interface OnShotListener {
        void onFinish();
    }
}
```

ScreenShotActivity.java

```java
/**
 * Created by wei on 16-9-18.
 * <p>
 * 完全透明 只是用于弹出权限申请的窗而已
 *
 */
public class ScreenShotActivity extends Activity {

    public static final int REQUEST_MEDIA_PROJECTION = 0x2893;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        setTheme(android.R.style.Theme_Dialog);//这个在这里设置 之后导致 的问题是 背景很黑
        super.onCreate(savedInstanceState);

        //如下代码 只是想 启动一个透明的Activity 而上一个activity又不被pause
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setDimAmount(0f);

        requestScreenShot();
    }


    public void requestScreenShot() {
        if (Build.VERSION.SDK_INT >= 21) {
            startActivityForResult(
                    ((MediaProjectionManager) getSystemService("media_projection")).createScreenCaptureIntent(),
                    REQUEST_MEDIA_PROJECTION
            );
        }
        else
        {
            toast("版本过低,无法截屏");
        }
    }

    private void toast(String str) {
        Toast.makeText(ScreenShotActivity.this,str,Toast.LENGTH_LONG).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_MEDIA_PROJECTION: {
                if (resultCode == -1 && data != null) {
                    Shotter shotter=new Shotter(ScreenShotActivity.this,data);
                    shotter.startScreenShot(new Shotter.OnShotListener() {
                        @Override
                        public void onFinish() {
                            toast("shot finish!");
                            finish(); // don't forget finish activity
                        }
                    });
                }
            }
        }
    }


}
```

原理比较简单，启动5.0的屏幕捕捉，使用`MediaProjection`创建一个虚拟桌面，将捕捉的数据传递到虚拟桌面，然后取虚拟桌面的一帧画面。 
`VirtualDisplay`这个类在android.hardware这个包下面，很明显这个是需要硬件支持。


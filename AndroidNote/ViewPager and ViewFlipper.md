ViewPager
ViewPager的概念
在前面的博文《Android开发笔记（十九）底部标签栏TabBar》中，我们提到可以在一个主页面里通过选项卡方式，切换到不同的子页面。那么在手机上还有另外一种切换页面的方式，就是通过手势左右滑动，ViewPager就是这么一个左右滑动来切换页面的控件。
ViewPager的基本思想跟适配视图差不多，都是定义一组元素，通过适配器来展示与响应不同元素的处理，适配视图的相关说明参见《Android开发笔记（三十八）列表类视图》。


ViewPager的常用方法
下面是ViewPager的常用方法：
setAdapter : 设置ViewPager的适配器
setCurrentItem : 设置当前的页码，即默认打开ViewPager时显示哪一页的内容。
setOnPageChangeListener : 设置ViewPager的页面变化监听器。


ViewPager的适配器
适配器的相关说明参见《Android开发笔记（三十八）列表类视图》。不过ViewPager并不使用Adapter类系列的适配器，而是使用PagerAdapter。如果ViewPager里面的视图是View与View的子类，那么适配器都用PagerAdapter；如果ViewPager里面的视图Fragment，那么适配器就要用FragmentStatePagerAdapter（其实该类也是来自于PagerAdapter）。废话少说，直接上PagerAdapter的例子代码：
[java] view plain copy
 在CODE上查看代码片派生到我的代码片

    private class ImageAdapater extends PagerAdapter {  

        @Override  
        public int getCount() {  
            return mViewList.size();  
        }  


        @Override  
        public boolean isViewFromObject(View arg0, Object arg1) {  
            return arg0 == arg1;  
        }  
          
        @Override  
        public void destroyItem(ViewGroup container, int position, Object object) {  
            container.removeView(mViewList.get(position));  
        }  
          
        @Override  
        public Object instantiateItem(ViewGroup container, int position) {  
            container.addView(mViewList.get(position));  
            return mViewList.get(position);  
        }  
          
    }  



ViewPager的监听器
ViewPager一般不监听每个页面项的点击事件，而是监听页面滑动的监听事件，对应的监听器类是OnPageChangeListener。该类的三个方法介绍如下：
onPageScrollStateChanged : 翻页状态改变时调用，状态参数取值说明为：0表示静止，1表示正在滑动，2表示滑动完毕。在翻页过程中，状态值变化依次为：正在滑动->滑动完毕->静止。
onPageScrolled : 在翻页过程中调用。该方法的三个参数取值说明为：第一个参数表示当前页面的序号；第二个参数表示当前页面偏移的百分比，最小值为0，最大值为1；第三个参数表示当前页面的偏移距离，单位px。
onPageSelected : 在页面选择时调用，该方法用得较多。位置参数表示当前页面的序号。


ViewPager的页码指示器
为了方便开发者处理ViewPager的页码显示与切换，Android附带了两个工具，分别是PagerTabStrip和PagerTitleStrip。二者都是在ViewPager的页面上方展示设定的页面标题，不同之处在于，PagerTabStrip类似Tab效果，文本下面有横线；而PagerTitleStrip只是单纯的文本标题效果。下面是这两个工具的使用说明：
1、在布局文件中，二者需要作为ViewPager的的下级节点；
2、在布局文件中，二者本身无需另外定义id，代码中一般也不需要取出它们的对象做什么处理；
3、二者除了展示标题之外，还可自动响应点击事件，点击左侧或右侧的标题，页面会自动切换到左侧或右侧的视图；
4、要设置每页的标题文本，可重写PagerAdapter的getPageTitle，在对应位置返回相应的标题文字；


ViewFlipper
ViewFlipper的概念
设计ViewFlipper的初衷就是**给视图翻转实现动画功能**，与ViewPager相比，ViewFlipper是自动翻页动画，而ViewPager是手工翻页动画。


ViewFlipper的常用方法
下面是ViewFlipper的常用方法：
setFlipInterval : 设置每次翻页的时间间隔。
setAutoStart : 设置是否自动开始翻页。
startFlipping : 开始翻页。
stopFlipping : 停止翻页。
isFlipping : 判断当前是否正在翻页。
showNext : 显示下一个视图。
showPrevious : 显示上一个视图。
setInAnimation : 设置视图的移入动画。
getInAnimation : 获取移入动画的对象。
setOutAnimation : 设置视图的移出动画。
getOutAnimation : 获取移出动画的对象。
setDisplayedChild : 设置当前展示第几个视图。
getDisplayedChild : 获取当前展示的是第几个视图。


ViewFlipper与ViewPager的区别
1、手势左右滑动产生翻页动作
ViewPager可自动响应左右滑动事件，但ViewFlipper不会自动处理，必须在页面上注册一个手势探测器GestureDetector以及对应的手势监听器OnGestureListener，并重写监听器的onFling方法，根据前后两个手势的位移变化，决定当前是往左翻页还是往右翻页还是不翻页。
2、自动翻页
ViewPager没有可以设置自动翻页的方法，得通过Handler机制来定时调用翻页动作。而ViewFlipper就是为自动翻页而生，startFlipping和stopFlipping两个方法分别用于开始翻页与停止翻页，同时setFlipInterval方法可设置每次翻页的间隔时间。
3、多个子页面视图的适配器
ViewPager可调用setAdapter方法统一设置子页面，但ViewFlipper来源自FrameLayout，所以没有适配器的说法。ViewFlipper本质是多个子视图共存于一个FrameLayout，只是在某个时刻只显示其中的一个子视图，因此ViewFlipper的子页面是一个个addView上去的。
4、页面滑动的监听器
ViewPager可调用setOnPageChangeListener方法设置页面滑动监听器，而ViewFlipper没有专门的滑动监听器，只能通过动画监听器AnimationListener来间接实现。具体说来，便是ViewFlipper先调用getInAnimation或者getOutAnimation获取移入或移除动画的Animation对象，然后再给这些动画对象注册动画监听器setAnimationListener。下面是动画监听器需重写的几个方法：
onPageScrollStateChanged : 翻页状态改变时调用，状态参数取值说明为：0表示静止，1表示正在滑动，2表示滑动完毕。在翻页过程中，状态值变化依次为：正在滑动->滑动完毕->静止。
onPageScrolled : 在翻页过程中调用。该方法的三个参数取值说明为：第一个参数表示当前页面的序号；第二个参数表示当前页面偏移的百分比，最小值为0，最大值为1；第三个参数表示当前页面的偏移距离，单位px。
onPageSelected : 在页面选择时调用，该方法用得较多。位置参数表示当前页面的序号。
5、多页面的的页码指示器
ViewPager有对应的PagerTabStrip和PagerTitleStrip，可以自动显示页码文字；ViewFlipper则没有相关的页码类，如果需要的话，只能自己定义一个新类。

Banner
如今我们打开电商类的APP，首页上方就有很炫的Banner（横幅轮播页），里面放了最新的商品和活动的介绍图片，还能自动轮播，也可左右翻动，着实是吸引眼球。
下面是一个Banner的截图，我们看看这个Banner是如何实现的。


首先看到Banner的界面由两部分组成，一部分是轮播的图片，另一部分是图片下方的几个图标用来表示当前播放的是第几张图片。所以一个Banner需要先初始化两个队列，一个是轮播图片队列，可考虑使用ArrayList<ImageView>；另一个则是下方图标队列，可考虑使用RadioGroup或者ArrayList<Button>。


然后图片队列需要按顺序进行轮播，这个轮播效果可考虑使用ViewPager或者ViewFlipper。至于自动滚动及轮播间隔的处理，ViewPager可采取Handler与Runnable结合；ViewFlipper就更简单了，设置flipInterval属性即可指定轮播的时间间隔。


另外，不要忘了在图片轮播时，下方图标也要跟着切换。为此需要给轮播事件加个监听器，以便每次轮播都能触发下方图标的变化。对于ViewPager，可加上OnPageChangeListener监听器，在onPageSelected方法中切换下方图标。对于ViewFlipper，可加上AnimationListener监听器，在onAnimationEnd方法中切换下方图标。


接着在Banner中增加处理手势事件，因为除了自动播放，我们还希望能够用手来控制即时播放。也就是说，用户的手向屏幕右边滑动时，Banner需要立即翻到上一张图片；用户的手向屏幕左边滑动时，Banner需要立即翻到下一张图片。对于ViewPager，我们无需关心左右滑动的手势，因为ViewPager已经自动实现了。对于ViewFlipper，既可以采用粗略手势事件GestureDetector里面的onFling方法（需要注册监听器OnGestureListener），也可以采用精确手势事件里面的onTouchEvent方法。


最后，当用户点击某张图片时，页面自然要跳转到该图片对应的商品页面或者活动页面。所以Banner还需要把一个页面点击的监听器接口开放出来，用于主页面响应这个页面点击事件。具体实现的话，就是Banner在内部点击事件中调用监听器的onBannerClick方法，而主页面需要实现监听器的onBannerClick方法。


代码示例
下面是ViewPager方式实现时候的代码
BannerPager.java
[java] view plain copy
 在CODE上查看代码片派生到我的代码片

    import java.util.ArrayList;  
    import java.util.List;  
      
    import com.example.exmbanner.R;  
    import com.example.exmbanner.util.Utils;  
      
    import android.annotation.SuppressLint;  
    import android.app.Activity;  
    import android.content.Context;  
    import android.os.Handler;  
    import android.os.Message;  
    import android.support.v4.view.PagerAdapter;  
    import android.support.v4.view.ViewPager;  
    import android.util.AttributeSet;  
    import android.util.Log;  
    import android.view.Gravity;  
    import android.view.LayoutInflater;  
    import android.view.MotionEvent;  
    import android.view.View;  
    import android.view.ViewGroup;  
    import android.widget.ImageView;  
    import android.widget.RadioButton;  
    import android.widget.RadioGroup;  
    import android.widget.RelativeLayout;  
      
    @SuppressLint({ "InflateParams", "ClickableViewAccessibility" })  
    public class BannerPager extends RelativeLayout implements View.OnClickListener {  
        private static final String TAG = "BannerPager";  
      
        private Context mContext;  
        private BannerClickListener mListener;  
        private ViewPager mPager;  
        private List<ImageView> mViewList = new ArrayList<ImageView>();  
        private RadioGroup mGroup;  
        private int mCount;  
        private LayoutInflater mInflater;  
        private BannerHandler mHandler;  
        private int dip_13;  
        private static int mInterval = 3000;  
      
        public BannerPager(Context context) {  
            super(context);  
            mContext = context;  
            init();  
        }  
      
        public BannerPager(Context context, AttributeSet attrs) {  
            super(context, attrs);  
            mContext = context;  
            init();  
        }  
      
        public void setOnBannerListener(BannerClickListener listener) {  
            mListener = listener;  
        }  
      
        public void start() {  
            mHandler.sendEmptyMessageDelayed(0, mInterval);  
        }  
      
        public void setImage(ArrayList<Integer> imageList) {  
            for (int i = 0; i < imageList.size(); i++) {  
                Integer imageID = ((Integer) imageList.get(i)).intValue();  
                ImageView ivNew = new ImageView(mContext);  
                ivNew.setLayoutParams(new LayoutParams(  
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));  
                ivNew.setScaleType(ImageView.ScaleType.FIT_XY);  
                ivNew.setImageResource(imageID);  
                ivNew.setOnClickListener(this);  
                mViewList.add(ivNew);  
                Log.d(TAG, "i=" + i + ",image_id=" + imageID);  
            }  
            mPager.setAdapter(new ImageAdapater());  
            mPager.setOnPageChangeListener(new BannerChangeListener(this));  
      
            mCount = imageList.size();  
            for (int i = 0; i < mCount; i++) {  
                RadioButton radio = new RadioButton(mContext);  
                radio.setLayoutParams(new RadioGroup.LayoutParams(dip_13, dip_13));  
                radio.setGravity(Gravity.CENTER);  
                radio.setButtonDrawable(R.drawable.rbt_selector);  
                mGroup.addView(radio);  
            }  
              
            mPager.setCurrentItem(0);  
            setButton(0);  
            mHandler = new BannerHandler();  
            start();  
        }  
      
        private void setButton(int position) {  
            ((RadioButton) mGroup.getChildAt(position)).setChecked(true);  
        }  
      
        private void init() {  
            mInflater = ((Activity) mContext).getLayoutInflater();  
            View view = mInflater.inflate(R.layout.banner_pager, null);  
            mPager = (ViewPager) view.findViewById(R.id.banner_pager);  
            mGroup = (RadioGroup) view.findViewById(R.id.banner_pager_group);  
            addView(view);  
            dip_13 = Utils.dip2px(mContext, 13);  
        }  
      
        public boolean dispatchTouchEvent(MotionEvent event) {  
            getParent().requestDisallowInterceptTouchEvent(true);  
            return super.dispatchTouchEvent(event);  
        }  
      
        @SuppressLint("HandlerLeak")  
        private class BannerHandler extends Handler {  
      
            @Override  
            public void handleMessage(Message msg) {  
                scrollToNext();  
                sendEmptyMessageDelayed(0, mInterval);  
            }  
        }  
      
        public void scrollToNext() {  
            int index = mPager.getCurrentItem() + 1;  
            if (mViewList.size() <= index) {  
                index = 0;  
            }  
            mPager.setCurrentItem(index);  
        }  
          
        private class ImageAdapater extends PagerAdapter {  
              
            @Override  
            public int getCount() {  
                return mViewList.size();  
            }  
      
            @Override  
            public boolean isViewFromObject(View arg0, Object arg1) {  
                return arg0 == arg1;  
            }  
              
            @Override  
            public void destroyItem(ViewGroup container, int position, Object object) {  
                container.removeView(mViewList.get(position));  
            }  
              
            @Override  
            public Object instantiateItem(ViewGroup container, int position) {  
                container.addView(mViewList.get(position));  
                return mViewList.get(position);  
            }  
              
        }  
      
        private class BannerChangeListener implements ViewPager.OnPageChangeListener {  
            private BannerChangeListener(BannerPager bannerPager) {  
            }  
      
            @Override  
            public void onPageScrollStateChanged(int arg0) {  
            }  
      
            @Override  
            public void onPageScrolled(int arg0, float arg1, int arg2) {  
            }  
      
            @Override  
            public void onPageSelected(int arg0) {  
                setButton(arg0);  
            }  
        }  
      
        @Override  
        public void onClick(View v) {  
            int position = mPager.getCurrentItem();  
            mListener.onBannerClick(position);  
        }  
      
    }  




调用页面代码
[java] view plain copy
 在CODE上查看代码片派生到我的代码片

    import java.util.ArrayList;  

    import android.annotation.SuppressLint;  
    import android.app.Activity;  
    import android.graphics.Point;  
    import android.os.Bundle;  
    import android.widget.TextView;  
    import android.widget.LinearLayout.LayoutParams;  
      
    import com.example.exmbanner.ui.BannerClickListener;  
    import com.example.exmbanner.ui.BannerPager;  
    import com.example.exmbanner.util.Utils;  
      
    public class BannerPagerActivity extends Activity implements BannerClickListener {  
        private static final String TAG = "BannerPagerActivity";  
      
        private TextView tv_pager;  
        private BannerPager mBanner;  
      
        @Override  
        protected void onCreate(Bundle savedInstanceState) {  
            super.onCreate(savedInstanceState);  
            setContentView(R.layout.activity_banner_pager);  
            tv_pager = (TextView) findViewById(R.id.tv_pager);  
      
            mBanner = (BannerPager) findViewById(R.id.banner_pager);  
            LayoutParams params = (LayoutParams) mBanner.getLayoutParams();  
            Point point = Utils.getSize(this);  
            params.height = (int) (point.x * 250f/ 640f);  
            mBanner.setLayoutParams(params);  
              
            ArrayList<Integer> bannerArray = new ArrayList<Integer>();  
            bannerArray.add(Integer.valueOf(R.drawable.banner_1));  
            bannerArray.add(Integer.valueOf(R.drawable.banner_2));  
            bannerArray.add(Integer.valueOf(R.drawable.banner_3));  
            bannerArray.add(Integer.valueOf(R.drawable.banner_4));  
            bannerArray.add(Integer.valueOf(R.drawable.banner_5));  
            mBanner.setImage(bannerArray);  
            mBanner.setOnBannerListener(this);  
        }  
      
        @SuppressLint("DefaultLocale")  
        @Override  
        public void onBannerClick(int position) {  
            String desc = String.format("您点击了第%d张图片", position+1);  
            tv_pager.setText(desc);  
        }  
      
    }  





下面是ViewFlipper方式实现时候的代码
BannerFlipper.java
[java] view plain copy
 在CODE上查看代码片派生到我的代码片

    import java.util.ArrayList;  
    import java.util.List;  
      
    import com.example.exmbanner.R;  
      
    import android.annotation.SuppressLint;  
    import android.app.Activity;  
    import android.content.Context;  
    import android.util.AttributeSet;  
    import android.util.Log;  
    import android.view.GestureDetector;  
    import android.view.LayoutInflater;  
    import android.view.MotionEvent;  
    import android.view.View;  
    import android.view.animation.Animation;  
    import android.view.animation.AnimationUtils;  
    import android.widget.Button;  
    import android.widget.ImageView;  
    import android.widget.LinearLayout;  
    import android.widget.RelativeLayout;  
    import android.widget.ViewFlipper;  
      
    @SuppressLint({ "InflateParams", "ClickableViewAccessibility" })  
    public class BannerFlipper extends RelativeLayout {  
        private static final String TAG = "BannerFlipper";  
      
        private Context mContext;  
        private BannerClickListener mListener;  
        private ViewFlipper mFlipper;  
        private LinearLayout mLayout;  
        private int mCount;  
        private List<Button> mButtonList = new ArrayList<Button>();  
        private LayoutInflater mInflater;  
        private GestureDetector mGesture;  
        private float mFlipGap = 20f;  
      
        public BannerFlipper(Context context) {  
            super(context);  
            mContext = context;  
            init();  
        }  
      
        public BannerFlipper(Context context, AttributeSet attrs) {  
            super(context, attrs);  
            mContext = context;  
            init();  
        }  
      
        public void setOnBannerListener(BannerClickListener listener) {  
            mListener = listener;  
        }  
      
         public void start() {  
             startFlip();  
         }  
      
        public void setImage(ArrayList<Integer> imageList) {  
            for (int i = 0; i < imageList.size(); i++) {  
                Integer imageID = ((Integer) imageList.get(i)).intValue();  
                ImageView ivNew = new ImageView(mContext);  
                ivNew.setLayoutParams(new LayoutParams(  
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));  
                ivNew.setScaleType(ImageView.ScaleType.FIT_XY);  
                ivNew.setImageResource(imageID);  
                mFlipper.addView(ivNew);  
                Log.d(TAG, "i=" + i + ",image_id=" + imageID);  
            }  
      
            mCount = imageList.size();  
            for (int i = 0; i < mCount; i++) {  
                View view = mInflater.inflate(R.layout.banner_flipper_button, null);  
                Button button = (Button) view.findViewById(R.id.banner_btn_num);  
                mLayout.addView(view);  
                mButtonList.add(button);  
            }  
            mFlipper.setDisplayedChild(mCount - 1);  
            startFlip();  
        }  
      
        private void setButton(int position) {  
            if (mCount > 1) {  
                for (int m = 0; m < mCount; m++) {  
                    if (m == position % mCount) {  
                        mButtonList.get(m).setBackgroundResource(R.drawable.icon_point_c);  
                    } else {  
                        mButtonList.get(m).setBackgroundResource(R.drawable.icon_point_n);  
                    }  
                }  
            }  
        }  
      
        @SuppressWarnings("deprecation")  
        private void init() {  
            mInflater = ((Activity) mContext).getLayoutInflater();  
            View view = mInflater.inflate(R.layout.banner_flipper, null);  
            mFlipper = (ViewFlipper) view.findViewById(R.id.banner_flipper);  
            mLayout = (LinearLayout) view.findViewById(R.id.banner_flipper_num);  
            addView(view);  
            // 该手势的onSingleTapUp事件是点击时进入广告页  
            mGesture = new GestureDetector(new BannerGestureListener(this));  
        }  
      
        public boolean dispatchTouchEvent(MotionEvent event) {  
            mGesture.onTouchEvent(event);  
            return true;  
        }  
      
        final class BannerGestureListener implements GestureDetector.OnGestureListener {  
            private BannerGestureListener(BannerFlipper bannerFlipper) {  
            }  
      
            @Override  
            public final boolean onDown(MotionEvent event) {  
                return true;  
            }  
      
            @Override  
            public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {  
                if (e1.getX() - e2.getX() > mFlipGap) {  
                    startFlip();  
                    return true;  
                }  
                if (e1.getX() - e2.getX() < -mFlipGap) {  
                    backFlip();  
                    return true;  
                }  
                return false;  
            }  
      
            @Override  
            public final void onLongPress(MotionEvent event) {  
            }  
      
            @Override  
            public final boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {  
                boolean result = false; // true表示要继续处理  
                if (Math.abs(distanceY) < Math.abs(distanceX)) {  
                    BannerFlipper.this.getParent().requestDisallowInterceptTouchEvent(false);  
                    result = true;  
                }  
                return result;  
            }  
      
            @Override  
            public final void onShowPress(MotionEvent event) {  
            }  
      
            @Override  
            public boolean onSingleTapUp(MotionEvent event) {  
                int position = mFlipper.getDisplayedChild();  
                mListener.onBannerClick(position);  
                return false;  
            }  
      
        }  
      
        private void startFlip() {  
            mFlipper.startFlipping();  
            mFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_left_in));  
            mFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_left_out));  
            mFlipper.getOutAnimation().setAnimationListener(new BannerAnimationListener(this));  
            mFlipper.showNext();  
        }  
      
        private void backFlip() {  
            mFlipper.startFlipping();  
            mFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_right_in));  
            mFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_right_out));  
            mFlipper.getOutAnimation().setAnimationListener(new BannerAnimationListener(this));  
            mFlipper.showPrevious();  
            mFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_left_in));  
            mFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_left_out));  
            mFlipper.getOutAnimation().setAnimationListener(new BannerAnimationListener(this));  
        }  
      
        private class BannerAnimationListener implements Animation.AnimationListener {  
            private BannerAnimationListener(BannerFlipper bannerFlipper) {  
            }  
      
            @Override  
            public final void onAnimationEnd(Animation paramAnimation) {  
                int position = mFlipper.getDisplayedChild();  
                setButton(position);  
            }  
      
            @Override  
            public final void onAnimationRepeat(Animation paramAnimation) {  
            }  
      
            @Override  
            public final void onAnimationStart(Animation paramAnimation) {  
            }  
        }  
      
    }  




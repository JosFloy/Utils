# Android手势详解

 手势操作在我们使用智能设备的过程中奉献了不一样的体验。Android开发中必然会进行手势操作方面的编程。那么它的原理是怎样的呢？我们如何进行手势操作编程呢？

**手势操作原理**

       首先，在Android系统中，每一次手势交互都会依照以下顺序执行。

       1. 接触接触屏一刹那，触发一个MotionEvent事件。

       2. 该事件被OnTouchListener监听，在其onTouch()方法里获得该MotionEvent对象。

       3. 通过GestureDetector（手势识别器）转发次MotionEvent对象至OnGestureListener。

       4. OnGestureListener获得该对象，听根据该对象封装的的信息，做出合适的反馈。

       这个顺序可以说就是手势操作的原理。

**手势操作类和接口**

       下面一同来了解一下MotionEvent、GestureDetector和OnGestureListener。

       MotionEvent: 这个类用于封装手势、触摸笔、轨迹球等等的动作事件。其内部封装了两个重要的属性X和Y，这两个属性分别用于记录横轴和纵轴的坐标。

       GestureDetector: 识别各种手势。

       OnGestureListener: 这是一个手势交互的监听接口，其中提供了多个抽象方法，并根据GestureDetector的手势识别结果调用相对应的方法。

** 手势操作实例**

       下面我再通过一个切换美女图片的代码示例，演示一下手势交互的实现，让大伙对上面的执行顺序，以及各手势动作的区分有一个更加深刻的了解和记忆。

       首先，提供一个只有ImageView的布局文件——main.xml。

```xml
<?xml version="1.0" encoding="utf-8"?>  
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android" android:orientation="vertical" android:layout_width="fill_parent" android:layout_height="fill_parent">  
   <ImageView android:id="@+id/image" android:layout_width="fill_parent" android:layout_height="fill_parent" android:layout_gravity="center"/>  
</LinearLayout>  
```

然后，完成我们的Activity，因为要监听触摸屏的触摸事件和手势时间，所以该Activity必须实现OnTouchListener和OnGestureListener两个接口，并重写其中的方法。具体代码如下：

```java
public class MainActivity extends Activity implements OnTouchListener, OnGestureListener {  
     
   //创建一个用于识别收拾的GestureDetector对象waiyuwu.blogcn.com  
   private GestureDetector detector = new GestureDetector(this);  
   //定义一个数组，用于放漂亮的女孩  
   int[] girls = new int[]{R.drawable.girl1, R.drawable.girl2, R.drawable.girl3};  
   //定义数组下标，以方便观看各个女孩  
   private int index;  
   private ImageView image;  
     
   @Override 
   public void onCreate(Bundle savedInstanceState) {  
    super.onCreate(savedInstanceState);  
    setContentView(R.layout.main);  
       
    image = (ImageView)findViewById(R.id.image);  
    //设置一个初始显示的girl吧  
    image.setImageResource(girls[index]);  
    //监听这个ImageView组件上的触摸屏时间  
    image.setOnTouchListener(this);  
    //下面两个要记得设哦，不然就没法处理轻触以外的事件了，例如抛掷动作。  
    image.setLongClickable(true);  
    detector.setIsLongpressEnabled(true);  
   }  
     
   //用于呼喊下一个女孩的方法  
   public void goNext(){  
     index++;  
     index = Math.abs(index % girls.length);  
     image.setImageResource(girls[index]);  
   }  
     
   //用户呼唤上一个女孩的方法  
   public void goPrevious(){  
     index--;  
     index = Math.abs(index % girls.length);  
     image.setImageResource(girls[index]);  
   }  
   
   //重写OnTouchListener的onTouch方法  
   //此方法在触摸屏被触摸，即发生触摸事件（接触和抚摸两个事件，挺形象）的时候被调用。  
   @Override 
   public boolean onTouch(View v, MotionEvent event) {  
     detector.onTouchEvent(event);  
     return true;  
   }  
   
   //在按下动作时被调用  
   @Override 
   public boolean onDown(MotionEvent e) {  
     return false;  
   }  
   
   //在抛掷动作时被调用  
   @Override 
   public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,  
       float velocityY) {  
     //velocityX表示横向的移动，根据手指移动的方向切换女孩  
     if(velocityX < 0){  
       goNext();  
     }else if(velocityX > 0){  
       goPrevious();  
     }  
     return false;  
   }  
   
   //在长按时被调用  
   @Override 
   public void onLongPress(MotionEvent e) {  
   }  
   
   //在滚动时调用  
   @Override 
   public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,  
       float distanceY) {  
     return false;  
   }  
   
   //在按住时被调用  
   @Override 
   public void onShowPress(MotionEvent e) {  
   }  
   
   //在抬起时被调用  
   @Override 
   public boolean onSingleTapUp(MotionEvent e) {  
     return false;  
   }  
 }  
```

**手势操作各个方法的含义**

       在刚开始学Android的时候，就觉得Google的文档不咋样，在研究手势时，更加的感觉Google的文档写得实在是太差了。很多常量， 属性和方法，居然连个描述都没有。没有描述也就罢了，但是OnGestureListener里手势这么多，它也没有一个介绍说明，在没有进行不断才尝试 之前，谁能搞懂onLongPress和onShowPress，onScroll和onFling的关系与差别吗？Google真的需要在文档方面做一次大手术了。不过好在经过鄙人不断反复的尝试。从个人的角度为这几个手势动作做出了定义。

1.        按下（onDown）： 刚刚手指接触到触摸屏的那一刹那，就是触的那一下。
2.        抛掷（onFling）： 手指在触摸屏上迅速移动，并松开的动作。
3.        长按（onLongPress）： 手指按在持续一段时间，并且没有松开。
4.        滚动（onScroll）： 手指在触摸屏上滑动。
5.        按住（onShowPress）： 手指按在触摸屏上，它的时间范围在按下起效，在长按之前。
6.        抬起（onSingleTapUp）：手指离开触摸屏的那一刹那。

       除了这些定义之外，鄙人也总结了一点算是经验的经验吧，在这里和大家分享一下。

       任何手势动作都会先执行一次按下（onDown）动作。

1.         长按（onLongPress）动作前一定会执行一次按住（onShowPress）动作。
2.        按住（onShowPress）动作和按下（onDown）动作之后都会执行一次抬起（onSingleTapUp）动作。
3.        长按（onLongPress）、滚动（onScroll）和抛掷（onFling）动作之后都不会执行抬起（onSingleTapUp）动作。


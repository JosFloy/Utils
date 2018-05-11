

# Android计时

目录：

1、借助Timer实现

2、调用handler.sendMessagedely(Message msg, long delayMillis)

3、借助布局Chronometer

**1、借助Timer实现**

（1） 布局文件

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
  android:layout_width="match_parent"
  android:layout_height="match_parent"
  android:orientation="vertical" >
 
  <TextView
   android:id="@+id/timerView"
   android:layout_width="wrap_content"
   android:layout_height="wrap_content"
   android:layout_gravity="center_horizontal"
   android:textSize="60sp" />
 
 </LinearLayout>
```

（2）Activity文件

```java
public class MyChronometer extends Activity {
   private TextView timerView;
   private long baseTimer;
   
   @Override
   protected void onCreate(Bundle savedInstanceState) {
     setContentView(R.layout.chrono);
     MyChronometer.this.baseTimer = SystemClock.elapsedRealtime();
     timerView = (TextView) this.findViewById(R.id.timerView);
     final Handler startTimehandler = new Handler(){
     public void handleMessage(android.os.Message msg) {
         if (null != timerView) {
           timerView.setText((String) msg.obj);
         }
       }
     };
     new Timer("开机计时器").scheduleAtFixedRate(new TimerTask() {
       @Override
       public void run() {
         int time = (int)((SystemClock.elapsedRealtime() - MyChronometer.this.baseTimer) / 1000);
         String hh = new DecimalFormat("00").format(time / 3600);
         String mm = new DecimalFormat("00").format(time % 3600 / 60);
         String ss = new DecimalFormat("00").format(time % 60);  
         String timeFormat = new String(hh + ":" + mm + ":" + ss);
         Message msg = new Message();
         msg.obj = timeFormat;
         startTimehandler.sendMessage(msg);
       }
       
     }, 0, 1000L);
     super.onCreate(savedInstanceState);
   }
```

新开一个定时器（Timer）, 在子线程中获取开机时间并转成字符串格式， 利用handler传回UI线程显示。

**2.调用handler.sendMessagedely(Message msg, long delayMillis)**

（1） 布局文件与方法1 相同，运行结果与方法1 相同

（2）Activity文件

```java
public class MyChronometer extends Activity {
   private TextView timerView;
   private long baseTimer;
   
   @Override
   protected void onCreate(Bundle savedInstanceState) {
     setContentView(R.layout.chrono);
     MyChronometer.this.baseTimer = SystemClock.elapsedRealtime();
     timerView = (TextView) this.findViewById(R.id.timerView);
     Handler myhandler = new Handler(){
       public void handleMessage(android.os.Message msg) {
         if (0 == MyChronometer.this.baseTimer) {
           MyChronometer.this.baseTimer = SystemClock.elapsedRealtime();
         }
         
         int time = (int)((SystemClock.elapsedRealtime() - MyChronometer.this.baseTimer) / 1000);
         String hh = new DecimalFormat("00").format(time / 3600);
         String mm = new DecimalFormat("00").format(time % 3600 / 60);
         String ss = new DecimalFormat("00").format(time % 60);  
         if (null != MyChronometer.this.timerView) {
           timerView.setText(hh + ":" + mm + ":" + ss);
         }
         sendMessageDelayed(Message.obtain(this, 0x0), 1000);
       }
     };
     myhandler.sendMessageDelayed(Message.obtain(myhandler, 0x0), 1000);
     super.onCreate(savedInstanceState);
   }
```

**sendMessageDelayed (Message msg, long delayMillis)：**在 delayMillis/1000 秒后发送消息 msg。

在Handler 的 handleMessage（）方法中调用sendMessageDelayed方法， 巧妙的实现了循环。需要注意的是，在Handler外要调用一次startTimehandler.sendMessageDelayed(Message.obtain(startTimehandler, 0x0), 1000);  以作为循环的入口。

**3.借助布局Chronometer**

（1） 布局文件

```xml
<?xml version="1.0" encoding="utf-8"?>
 <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
   android:layout_width="match_parent"
   android:layout_height="match_parent"
   android:orientation="vertical" >
   
   <Chronometer
     android:id="@+id/chronometer"
     android:layout_width="wrap_content"
     android:layout_height="wrap_content"
     android:layout_gravity="center_horizontal"
     android:textSize="60sp" />
   
</LinearLayout>
```

布局Chronometer继承自TextView

（2）Activity文件

```java
 public class MyChronometer extends Activity {
 
   Chronometer chronometer;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
     setContentView(R.layout.chrono);
     chronometer = (Chronometer) this.findViewById(R.id.chronometer);
     chronometer.setBase(SystemClock.elapsedRealtime());
     chronometer.start();
     super.onCreate(savedInstanceState);
   }
 }
```

逻辑代码很简单，调用chronometer.start()就可以开始计时。

chronometer.setBase（long base）：设置起始计时点，这里设置的是获取开机时间。

chronometer.start()：以上面setBase()设置的时间点为起始点，开始计时，看一下start()的源码就知道了：

```java
public void start() {
  mStarted = true;
  updateRunning();
 }
```

调用了updateRunning（）, 跟入updateRunning（）方法：

```java
  private void updateRunning() {
     boolean running = mVisible && mStarted;
     if (running != mRunning) {
       if (running) {
         updateText(SystemClock.elapsedRealtime());
         dispatchChronometerTick();
         mHandler.sendMessageDelayed(Message.obtain(mHandler, TICK_WHAT), 1000);
       } else {
         mHandler.removeMessages(TICK_WHAT);
       }
       mRunning = running;
     }
   }
   
   private Handler mHandler = new Handler() {
     public void handleMessage(Message m) {
       if (mRunning) {
         updateText(SystemClock.elapsedRealtime());
         dispatchChronometerTick();
         sendMessageDelayed(Message.obtain(this, TICK_WHAT), 1000);
       }
     }
   };
```

用updateText()方法设置时间显示。 至于计时循环机制，和方法二相同，同样是调用了handler的handMessageDelayed方法。

**注意：**最后说一个关于Chronometer类的常见问题，看到很多人都问用Chronometer类如何设置格式HH：MM：SS的时间。

问这个问题的童鞋先看一下官方文档的描述：

 If the format string is null, or if you never call setFormat(), the Chronometer will simply display the timer value in "MM:SS" or "H:MM:SS" form.

也就是说默认情况下，使用的格式是"MM:SS" 或者 "H:MM:SS"， 然后有童鞋又会问：那到底是"MM:SS" 还是 "H:MM:SS"。我们先看一下源码：

updateText()：

```java
private synchronized void updateText(long now) {
     long seconds = now - mBase;
     seconds /= 1000;
     String text = DateUtils.formatElapsedTime(mRecycle, seconds);
 
     if (mFormat != null) {
       Locale loc = Locale.getDefault();
       if (mFormatter == null || !loc.equals(mFormatterLocale)) {
         mFormatterLocale = loc;
         mFormatter = new Formatter(mFormatBuilder, loc);
       }
      mFormatBuilder.setLength(0);
       mFormatterArgs[0] = text;
       try {
         mFormatter.format(mFormat, mFormatterArgs);
         text = mFormatBuilder.toString();
       } catch (IllegalFormatException ex) {
         if (!mLogged) {
           Log.w(TAG, "Illegal format string: " + mFormat);
           mLogged = true;
         }
       }
     }    

  setText(text);
   }
```

调用了DateUtils.formatElapsedTime， 看一下DateUtils.formatElapsedTime里面都有啥：

```java
public static String formatElapsedTime(StringBuilder recycle, long elapsedSeconds) {
     Formatter f = new Formatter(sb, Locale.getDefault());
     initFormatStrings();
     if (hours > 0) {
       return f.format(sElapsedFormatHMMSS, hours, minutes, seconds).toString();
     } else {
       return f.format(sElapsedFormatMMSS, minutes, seconds).toString();
     }
   }
```

代码较多，我就挑重点截取了，仔细看看上面哪个if(){}else{}语句，你肯定就恍然大悟了吧？

为了我们理论的正确性，将方法三 Activity中的代码稍作修改：

chronometer.setBase(-18000000);


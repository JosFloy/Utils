# 自定义ChronometerView

**主要原理：**先设置一个基准倒计时时间mBaseSeconds，内置handler 每隔1s发送一个空消息，mRemainSeconds--，同时刷新界面视图，回调给外部调用者，只到为零。外部调用者可通过start()/pause()/stop()来控制计时器的工作状态。

```java
public class ChronometerView extends TextView { 
 
 /** 
 * A callback that notifies when the chronometer has decremented on its own. 
 * 
 * @author Fanjb 
 */ 
 public interface OnTickChangeListener { 
 
 /** 
  * remain seconds changed 
  * 
  * @param view 
  * @param remainSeconds 
  */ 
 public void onTickChanged(ChronometerView view, long remainSeconds); 
 } 
 
 private long mBase; 
 private long mRemainSeconds; 
 private boolean mStarted; 
 private boolean mReStart; 
 private boolean mVisible; 
 private boolean mIsEnable; 
 
 private OnTickChangeListener mTickListener; 
 
 public ChronometerView(Context context) { 
 this(context, null); 
 } 
 
 public ChronometerView(Context context, AttributeSet attrs) { 
 super(context, attrs, 0); 
 } 
 
 public ChronometerView(Context context, AttributeSet attrs, int defStyleAttr) { 
 super(context, attrs, defStyleAttr); 
 updateText(mRemainSeconds); 
 } 
 
 @Override 
 protected void onWindowVisibilityChanged(int visibility) { 
 super.onWindowVisibilityChanged(visibility); 
 mVisible = visibility == VISIBLE; 
 updateStatus(); 
 } 
 
 @Override 
 protected void onDetachedFromWindow() { 
 super.onDetachedFromWindow(); 
 mVisible = false; 
 updateStatus(); 
 } 
 
 /** 
 * 启动计时器 
 */ 
 public void start() { 
 if (mReStart && !mStarted) { 
  mRemainSeconds = mBase; 
 } 
 mStarted = true; 
 updateStatus(); 
 } 
 
 /** 
 * 暂停计时器 
 */ 
 public void pause() { 
 if (mStarted) { 
  mStarted = mReStart = false; 
  updateStatus(); 
 } 
 } 
 
 /** 
 * 停止计时器，再次调用 start()重新启动 
 */ 
 public void stop() { 
 mStarted = false; 
 mReStart = true; 
 updateStatus(); 
 updateText(mRemainSeconds = 0); 
 dispatchTickListener(); 
 } 
 
 /** 
 * 刷新内部状态 
 */ 
 private void updateStatus() { 
 boolean isEnable = mVisible && mStarted; 
 if (mIsEnable != isEnable) { 
  if (isEnable) { 
  mHandler.sendMessage(Message.obtain(mHandler, TICK_WHAT)); 
  } else { 
  mHandler.removeMessages(TICK_WHAT); 
  } 
  mIsEnable = isEnable; 
 } 
 } 
 
 private static final int TICK_WHAT = 1; 
 
 private Handler mHandler = new Handler() { 
 public void handleMessage(android.os.Message msg) { 
  if (mRemainSeconds > 0) { 
  updateText(--mRemainSeconds); 
  dispatchTickListener(); 
  sendMessageDelayed(Message.obtain(this, TICK_WHAT), 1000); 
  } 
 } 
 }; 
 
 private void updateText(long now) { 
 String text = DateUtils.formatElapsedTime(now); 
 setText(text); 
 } 
 
 /** 
 * 在未启动状态下设置开始倒计时时间 
 * 
 * @param baseSeconds 
 */ 
 public void setBaseSeconds(long baseSeconds) { 
 if (baseSeconds > 0 && baseSeconds != mBase && !mStarted) { 
  mBase = mRemainSeconds = baseSeconds; 
  updateText(mRemainSeconds); 
 } 
 } 
 
 /** 
 * 剩余时间 
 * 
 * @return 
 */ 
 public long getRemainSeconds() { 
 return mRemainSeconds; 
 } 
 
 public void setOnTickChangeListener(OnTickChangeListener listener) { 
 mTickListener = listener; 
 } 
 
 public OnTickChangeListener getTickListener() { 
 return mTickListener; 
 } 
 
 private void dispatchTickListener() { 
 if (mTickListener != null) { 
  mTickListener.onTickChanged(this, getRemainSeconds()); 
 } 
 } 
 
 @Override 
 public void onInitializeAccessibilityEvent(AccessibilityEvent event) { 
 super.onInitializeAccessibilityEvent(event); 
 event.setClassName(ChronometerView.class.getName()); 
 } 
 
 @Override 
 public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) { 
 super.onInitializeAccessibilityNodeInfo(info); 
 info.setClassName(Chronometer.class.getName()); 
 } 
}
```
# 自定义RatingBar

 ![2017330100254043](D:\我的文档\My Pictures\2017330100254043.gif)

这是一个类似于RatingBar的控件，然而配置RatingBar的样式难以实现这样的效果，如选中的图案和上面的文字对齐。因此，有必要写一个自定义View来实现。

思路如下：

- 新建一个TextRatingBar继承View类
- 在onDraw()方法中绘制元素：文字、横线、短竖线和圆形标记
- 重写onMeasure()方法，控制整体大小和边界
- 在onTouchEvent()方法中处理ACTION_DOWN和ACTION_MOVE事件，调用invalidate()方法引起View的重绘，以更新视图
- 定义一个用户选中某个字体的回调接口，以通知外部处理，比如去设置字体大小。

TextRatingBar类代码如下：

```java
public class TextRatingBar extends View{
 
 //paddingLeft
 private int mLeft;
 //paddingTop
 private int mTop;
 //当前rating
 private int mRating;
 //总raring数
 private int mCount;
 //rating文字
 private String[] texts = {"小","中","大","超大"};
 //相邻raring的距离
 private int mUnitSize;
 //bar到底部的距离
 private int mYOffset;
 //小竖条的一半长度
 private int mMarkSize;
 
 Paint paint = new Paint();
 
 public TextRatingBar(Context context) {
  this(context, null);
 }
 
 public TextRatingBar(Context context, AttributeSet attrs) {
  this(context, attrs, 0);
 }
 
 public TextRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
  super(context, attrs, defStyleAttr);
  mCount = 4;
  mRating = 0;
  mMarkSize = 3;
 }
 
 @Override
 protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
  super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  Log.i("test", getMeasuredWidth() + " " + getMeasuredHeight());
  mLeft = (getPaddingLeft()+getPaddingRight())/2;
  mTop = getPaddingTop();
  int barWidth = getMeasuredWidth() - 2 * mLeft;
  mUnitSize = barWidth/(mCount - 1);
  mYOffset = getMeasuredHeight() - getPaddingBottom();
 }
 
 @Override
 protected void onDraw(Canvas canvas) {
  paint.setStrokeWidth(2);
  paint.setColor(Color.RED);
  canvas.drawLine(mLeft,mYOffset,mLeft+mRating*mUnitSize,mYOffset,paint);
  for(int i=0;i<mCount;i++){
   paint.setColor(Color.RED);
   canvas.drawLine(mLeft+i*mUnitSize,mYOffset-mMarkSize,mLeft+i*mUnitSize,mYOffset+mMarkSize,paint);
   paint.setColor(mRating == i ? Color.RED : Color.BLACK);
   paint.setTextSize(30);
   paint.setTextAlign(Paint.Align.CENTER);
   canvas.drawText(texts[i],mLeft+i*mUnitSize,mTop,paint);
  }
  paint.setColor(Color.GRAY);
  canvas.drawLine(mLeft+mRating*mUnitSize,mYOffset,mLeft+(mCount-1)*mUnitSize,mYOffset,paint);
  canvas.drawCircle(mLeft+mRating*mUnitSize,mYOffset,10,paint);
 
 }
 
 @Override
 public boolean onTouchEvent(MotionEvent event) {
  if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE){
   float x = event.getX();
   for(int i=0;i<mCount;i++){
    float distance = mLeft+i*mUnitSize - x;
    if(Math.abs(distance) < 100){
     setRating(i);
     if(onRatingListener != null){
      onRatingListener.onRating(mRating);
     }
     break;
    }
   }
  }
  return true;
 }
 
 public void setRating(int rating) {
  mRating = rating;
  invalidate();
 }
 
 private OnRatingListener onRatingListener;
 
 public void setOnRatingListener(OnRatingListener onRatingListener) {
  this.onRatingListener = onRatingListener;
 }
 
 interface OnRatingListener{
  void onRating(int rating);
 }
}
```

几个要点： 

- onDraw()中的绘制要注意周围的预留空间，防止最左边的字体只显示一半，或滑动条下面没有一点空白的预留空间，导致用户不好划。因此在计算每一个绘制坐标时额外加上这些。
- 触摸事件是判断当前触摸点离哪个rating点最近，要加上左右临界值Math.abs(distance)，用户点击或划动在distance范围内就算发生了onRating()事件。

没有用到自定义属性，使用时直接放到布局中，周围加上padding就行了。

```java
<cc.rome753.demo.view.TextRatingBar
 android:paddingTop="20dp"
 android:paddingLeft="40dp"
 android:paddingRight="40dp"
 android:paddingBottom="35dp"
 android:layout_width="match_parent"
 android:layout_height="70dp" />
```


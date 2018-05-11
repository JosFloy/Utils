# Path类的lineTo和quadTo方法区别

当我们需要在屏幕上形成画线时，Path类的应用是必不可少的，而Path类的**lineTo**和**quadTo**方法实现的绘制线路形式也是不一样的，下面就以代码的实现来直观的探究这两个方法的功能实现区别；

**1. Path--->quadTo(float x1, float y1, float x2, float y2):**

     	该方法的实现是当我们不仅仅是画一条线甚至是画弧线时会形成平滑的曲线，该曲线又称为"**贝塞尔曲线**"(Bezier curve)，其中，x1，y1为控制点的坐标值，x2，y2为终点的坐标值；

    	贝塞尔曲线的形成，就比如我们把一条橡皮筋拉直，橡皮筋的头尾部对应起点和终点，然后从拉直的橡皮筋中选择任意一点（除头尾对应的点外）扯动橡皮筋形成的弯曲形状，而那个扯动橡皮筋的点就是控制点；

    	下就面以一个Demo来结合理解quadTo函数的应用，代码如下：

       1).自定义View：

```java
import android.content.Context;
import android.gesture.GestureStroke;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
 
public class DrawingWithBezier extends View
{
    private float mX;
    private float mY;
 
    private final Paint mGesturePaint = new Paint();
    private final Path mPath = new Path();
    
    public DrawingWithBezier(Context context)
    {
        super(context);
        mGesturePaint.setAntiAlias(true);
        mGesturePaint.setStyle(Style.STROKE);
        mGesturePaint.setStrokeWidth(5);
        mGesturePaint.setColor(Color.WHITE);
    }
 
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // TODO Auto-generated method stub
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                touchDown(event);
                 break;
            case MotionEvent.ACTION_MOVE:
                touchMove(event);
        }
        //更新绘制
        invalidate();
        return true;
    }
 
    @Override
    protected void onDraw(Canvas canvas)
    {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        //通过画布绘制多点形成的图形
        canvas.drawPath(mPath, mGesturePaint);
    }
 
    //手指点下屏幕时调用
    private void touchDown(MotionEvent event)
    {
      
        //mPath.rewind();
        //重置绘制路线，即隐藏之前绘制的轨迹
        mPath.reset();
        float x = event.getX();
        float y = event.getY();
        
        mX = x;
        mY = y;
        //mPath绘制的绘制起点
        mPath.moveTo(x, y);
    }
    
    //手指在屏幕上滑动时调用
    private void touchMove(MotionEvent event)
    {
        final float x = event.getX();
        final float y = event.getY();
 
        final float previousX = mX;
        final float previousY = mY;
 
        final float dx = Math.abs(x - previousX);
        final float dy = Math.abs(y - previousY);
        
        //两点之间的距离大于等于3时，生成贝塞尔绘制曲线
        if (dx >= 3 || dy >= 3)
        {
            //设置贝塞尔曲线的操作点为起点和终点的一半
            float cX = (x + previousX) / 2;
            float cY = (y + previousY) / 2;
 
            //二次贝塞尔，实现平滑曲线；previousX, previousY为操作点，cX, cY为终点
            mPath.quadTo(previousX, previousY, cX, cY);
 
            //第二次执行时，第一次结束调用的坐标值将作为第二次调用的初始坐标值
            mX = x;
            mY = y;
        }
    }
    
}
```

2).MainActivity:

```java
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
 
public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(new MySurfaceView(this));
        setContentView(new DrawingWithBezier(this));
        //setContentView(new DrawingWithoutBezier(this));      
    }
}
```

该Demo实现用户在手机屏幕上滑动手指时，可根据手指滑动的位置绘制出相应的线条，类似输入法手势的绘制，所以代码中的画笔Paint命名为mGesturePaint；

比如，我们在屏幕上绘制S这个图案，则形成的图案如下： 

 ![1425889731115114](D:\我的文档\My Pictures\1425889716201044.jpg)

**2. Path--->lineTo(float x, float y) :**

    	 该方法实现的仅仅是两点连成一线的绘制线路，这样，当我们用这个方法绘制曲线时，缺陷就出来了；下面的例子，同样还是和上面的Demo差不多，只不过Path调用的是lineTo方法，如下：

       1). 自定义View:

```java
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
 
public class DrawingWithoutBezier extends View
{
    private float mX;
    private float mY;
 
    private final Paint mGesturePaint = new Paint();
    private final Path mPath = new Path();
    
    public DrawingWithoutBezier(Context context)
    {
        super(context);
        mGesturePaint.setAntiAlias(true);
        mGesturePaint.setStyle(Style.STROKE);
        mGesturePaint.setStrokeWidth(5);
        mGesturePaint.setColor(Color.WHITE);
    }
 
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // TODO Auto-generated method stub
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                touchDown(event);
                 break;
            case MotionEvent.ACTION_MOVE:
                touchMove(event);
        }
        //更新绘制
        invalidate();
        return true;
    }
 
    @Override
    protected void onDraw(Canvas canvas)
    {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        canvas.drawPath(mPath, mGesturePaint);
    }
 
    //手指点下屏幕时调用
    private void touchDown(MotionEvent event)
    {
      
        //mPath.rewind();
        mPath.reset();
        float x = event.getX();
        float y = event.getY();
        
        mX = x;
        mY = y;
        
        //mPath绘制的绘制起点
        mPath.moveTo(x, y);
    }
    
    //手指在屏幕上滑动时调用
    private void touchMove(MotionEvent event)
    {
        final float x = event.getX();
        final float y = event.getY();
 
        final float previousX = mX;
        final float previousY = mY;
 
        final float dx = Math.abs(x - previousX);
        final float dy = Math.abs(y - previousY);
        
        //两点之间的距离大于等于3时，连接连接两点形成直线
        if (dx >= 3 || dy >= 3)
        {
            //两点连成直线
            mPath.lineTo(x, y);
            
            //第二次执行时，第一次结束调用的坐标值将作为第二次调用的初始坐标值
            mX = x;
            mY = y;
        }
    }
    
}
```

2).MainActivity:

```java
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
 
public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(new MySurfaceView(this));
        //setContentView(new DrawingWithBezier(this));
        setContentView(new DrawingWithoutBezier(this));      
    }
}
```

 ![1425889731115114](D:\我的文档\My Pictures\1425889731115114.jpg)


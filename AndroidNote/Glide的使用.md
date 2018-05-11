# Glide的使用

### 高斯模糊、加载监听、圆角图片

**1、引用**

`compile 'com.github.bumptech.glide:glide:3.7.0'`

**2、加载图片**

2.1. 基本加载

```java
Glide.with(context)
  .load(url)
  .into(imageView);
```

2.1设置加载中和加载失败的情况

```java
Glide.with(context)
  .load(url)
  .placeHolder(R.drawable.loading)
  .error(R.drawable.failed)
  .into(view);
```

2.3只加载动画

```java
Glide.with(context)
  .load(url)
  .asGif()//只能加载Gif文件
  .into(imageView)
```

2.4添加图片淡入淡出的效果

```java
Glide.with(context)
  .load(url)
  .placeHolder(R.drawable.loading)
  .error(R.drawable.failed)
  .crossFade(1000)//可设置时长，默认“300ms”
  .into(view);
```

2.5加载高斯模糊

```java
Glide.with(context)
  .load(url)
  .placeholder(R.drawable.loading)
  .error(R.drawable.failed)
  .crossFade(1000)
  // “23”：设置模糊度(在0.0到25.0之间)，默认”25";"4":图片缩放比例,默认“1”。
  .bitmapTransform(new BlurTransformation(context,23,4))
  .into(view);
```

2.6加载监听器RequestListener

```java
Glide.with(this)
  	 .load(internetUrl)
     .listener(new RequestListener<String,GlideDrawable>){
  @Override
  public boolean onException(Exception e, String model, Target<GlideDrawable> target, 					boolean isFirstResource) {
    Toast.makeText(getApplicationContext(),"资源加载异常",Toast.LENGTH_SHORT).show();
    return false;
   }
   //这个用于监听图片是否加载完成
   @Override
   public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
    Toast.makeText(getApplicationContext(),"图片加载完成",Toast.LENGTH_SHORT).show();
    return false;
   }
  })
  .into(imageView);

```

- [x] **注意：**如果需要加载完成后设置图片透明度为0，则不能设置.placeholder(R.drawable.url)，否则达不到你想要的效果。

2.7图片缓存机制

**Glide缓存策略**

Glide默认开启磁盘缓存和内存缓存，当然也可以对单张图片进行设置特定的缓存策略。 

设置图片不加入到内存缓存

```java
Glide.with(context)
  .load(eatFoodyImages[0])
  .skipMemoryCache(true)
  .into(imageViewInternet);
```

设置图片不加入到磁盘缓存

```java
Glide.with( context )
 .load( eatFoodyImages[0] )
 .diskCacheStrategy( DiskCacheStrategy.NONE )
 .into( imageViewInternet );
```

Glide支持多种磁盘缓存策略：

DiskCacheStrategy.NONE :不缓存图片 
DiskCacheStrategy.SOURCE :缓存图片源文件 
DiskCacheStrategy.RESULT:缓存修改过的图片 
DiskCacheStrategy.ALL:缓存所有的图片，默认

2.8 加载圆角图片

```java
public class GlideCircleTransform extends BitmapTransformation {
 public GlideCircleTransform(Context context) {
  super(context);
 }
 
 @Override
 protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
  return circleCrop(pool, toTransform);
 }
 
 private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
  if (source == null) return null;
  int size = Math.min(source.getWidth(), source.getHeight());
  int x = (source.getWidth() - size) / 2;
  int y = (source.getHeight() - size) / 2;
  // TODO this could be acquired from the pool too
  Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
  Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
  if (result == null) {
   result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
  }
  Canvas canvas = new Canvas(result);
  Paint paint = new Paint();
  paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
  paint.setAntiAlias(true);
  float r = size / 2f;
  canvas.drawCircle(r, r, r, paint);
  return result;
 }
 
 @Override
 public String getId() {
  return getClass().getName();
 }
}
```

使用：

```java
Glide.with(mContext)
 .load(imageUrl)
 .transform(new GlideCircleTransform(mContext))
 .into(holder.imageView);
```
2.9取消不在需要的负载

```java
Glide.with(fragment).clear(imageView);
```

尽管清除不在需要的负载是个好的做法，但我们不需要这么做。事实上，当我们通过Glide.with()方法传入的Activity或者Fragment被销毁时，Glide将会自动清除负载并且回收负载使用的任何资源。

---

### ListView和RecyclerView


# Kotlin和RxJava2.x实现短信验证码倒计时

```kotlin
val timer:TextView = findViewById(R.id.textView) //这里的 timer 就是你要控制显示倒计时效果的 TextView 
val mSubscription: Subscription? = null // Subscription 对象，用于取消订阅关系，防止内存泄露
//开始倒计时，用 RxJava2 实现
 private fun timer() {
  val count = 59L
  Flowable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
    .onBackpressureBuffer()//加上背压策略
    .take(count) //设置循环次数
    .map{ aLong ->
     count - aLong //
    }
    .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
    .subscribe(object : Subscriber<Long> {
     override fun onSubscribe(s: Subscription?) {
      timer.isEnabled = false//在发送数据的时候设置为不能点击
      timer.textColor = resources.getColor(Color.GRAY)//背景色设为灰色
      mSubscription = s
      s?.request(Long.MAX_VALUE)//设置请求事件的数量，重要，必须调用
     }
     override fun onNext(aLong: Long?) {
      timer.text = "${aLong}s后重发" //接受到一条就是会操作一次UI
     }
     override fun onComplete() {
      timer.text = "点击重发"
      timer.isEnabled = true
      timer.textColor = Color.WHITE
      mSubscription?.cancel()//取消订阅，防止内存泄漏
     }
     override fun onError(t: Throwable?) {
      t?.printStackTrace()
     }
    })
 }
```
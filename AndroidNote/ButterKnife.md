# ButterKnife

utterKnife的最新版本是8.4.0。

首先，需要导入`ButterKnife`的jar包。

在AndroidStudio中，`File->Project Structure->Dependencies->Library dependency `搜索butterknife即可，第一个就是.

另外一种就是直接在build:grade（app）dependencies里添加：

```groovy
compile 'com.jakewharton:butterknife:8.4.0'
annotationProcessor 'com.jakewharton:butterknife-compiler:8.4.0'
```

ok，现在正式开始使用吧，用法也很简单

在Activity子类的`onCreate()`方法里使用`ButterKnife.bind(this);`即可

```java
protected void onCreate(Bundle savedInstanceState) { 
    super.onCreate(savedInstanceState); 
    setContentView(R.layout.activity_main); 
    ButterKnife.bind(this); 
    tv1.setText("hi!sy")
```

注意：一定要在setContentView之后写。

再然后，把光标放在R.layout.activity_main上，鼠标右击，选中Generate...(Alt+Insert),点击会出现



选中的有TextView点击事件和findViewById的注解，点击Confirm就成功了！

什么，你说没有，别着急，你需要安装一个小插件（不要嫌麻烦，其实很简单，一劳永逸）

在`AndroidStudio->File->Settings->Plugins->`搜索Zelezny下载添加就行 ，可以快速生成对应组件的实例对象，不用手动写。

使用时，在要导入注解的Activity 或 Fragment 或 ViewHolder的layout资源代码上，右键——>Generate——Generate ButterKnife Injections。

```java
public class MainActivity extends Activity { 
   @BindView(R.id.tv_time) 
  TextView tvTime; 
  @BindView(R.id.activity_main) 
  RelativeLayout activityMain; 
  @BindView(R.id.tv_cal) 
  TextView tvCal; 
  @BindView(R.id.tv_date) 
  TextView tvDate; 
  Time time; 
  @Override
  protected void onCreate(Bundle savedInstanceState) { 
    super.onCreate(savedInstanceState); 
    setContentView(R.layout.activity_main); 
    ButterKnife.bind(this); 
    tvTime.setText("Time类"); 
    tvCal.setText("Calender类"); 
    tvDate.setText("Date类"); 
    initTime(); 
  } 
  private void initTime() { 
    time = new Time(); 
    time.setToNow(); 
  } 
  @OnClick({R.id.tv_cal, R.id.tv_date,R.id.tv_time}) 
  public void onClick(View view) { 
    switch (view.getId()) { 
      case R.id.tv_time://点击第一个 
        String times = time.year + "年" + time.month + "月" + time.monthDay 
            + "日" + time.hour + "时" + time.minute + "分" + time.second + "秒"
            + ":现在是一年中的第" + time.yearDay + "天"; 
        Toast.makeText(this, Time.getCurrentTimezone() + times, Toast.LENGTH_SHORT).show(); 
        tvTime.setText(times); 
        break; 
      case R.id.tv_cal: 
        break; 
      case R.id.tv_date: 
        break; 
    } 
  } 
  @Override
  protected void onDestroy() { 
    super.onDestroy(); 
//    Unbinder unbinder=ButterKnife.bind(this); 
//    unbinder.unbind(); 
    ButterKnife.bind(this).unbind(); 
  } 
}
```
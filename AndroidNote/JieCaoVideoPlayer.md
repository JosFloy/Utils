# JieCaoVideoPlayer

一行代码快速实现视频播放，Android视频播放，AndroidMP3播放，安卓视频播放一行代码搞定，真正实现Android的全屏功能，立志成为Android平台使用最广泛的视频播放控件

### 一，主要特点

- 全屏时启动新Activity实现播放器真正的全屏功能 
- 能在ListView、ViewPager和ListView、ViewPager和Fragment等多重嵌套模式下全屏工作
- ListView的拖拽和ViewPager的滑动时如果划出屏幕会自动重置视频
- 视频大小的屏幕适配，宽或长至少有两个对边是充满屏幕的，另外两个方向居中 
- 可以在加载、暂停、播放等各种状态中正常进入全屏和退出全屏
- 根据自己应用的颜色风格换肤 
- 播放MP3时显示缩略图片

### 二，使用步骤

导入到项目（建议使用第三方库导入的形式）

- 导入libiary

```
compile 'fm.jiecao:jiecaovideoplayer:5.5.2'1
```

- 在你的布局中申明JCVideoPlayer 组件

```xml
 <fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard
    android:id="@+id/videoplayer"
    android:layout_width="match_parent"
    android:layout_height="200dp"/>
```

- 在java代码中初始化JCVideoPlayer组件ID，并配置相对应的URL

  或者设置视频地址、缩略图地址、标题

```java
JCVideoPlayerStandard jcVideoPlayerStandard = (JCVideoPlayerStandard) 																findViewById(R.id.videoplayer);
jcVideoPlayerStandard.setUp("http://2449.vod.myqcloud.com/2449_22ca37a6ea9011e5acaaf51d105342e3.f20.mp4", JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "嫂子闭眼睛");
jcVideoPlayerStandard.thumbImageView.setImage("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640");
```

* 在包含播放器的Fragment或Activity的onPause()方法中调用JCVideoPlayer.releaseAllVideos()。相应的生命周期里面要做相对应的操作：

```java
@Override
public void onBackPressed() {
    if (JCVideoPlayer.backPress()) {
        return;
    }
    super.onBackPressed();
}
@Override
protected void onPause() {
    super.onPause();
    JCVideoPlayer.releaseAllVideos();
}
```

* 最后在 AndroidManifest.xml要进行相对应的配置

```xml
 <activity
    android:name=".MainActivity"
    android:configChanges="orientation|screenSize|keyboardHidden"
    android:screenOrientation="portrait" /> <!-- or 		android:screenOrientation="landscape"-->
```

##### 用JCVideoPlayer的优势

- 全屏时启动新Activity实现播放器真正的全屏功能
- 能在ListView、ViewPager和ListView、ViewPager和Fragment等多重嵌套模式下全屏工作
- ListView的拖拽和ViewPager的滑动时如果划出屏幕会自动重置视频
- 视频大小的屏幕适配，宽或长至少有两个对边是充满屏幕的，另外两个方向居中
- 可以在加载、暂停、播放等各种状态中正常进入全屏和退出全屏
- 根据自己应用的颜色风格换肤
- 播放MP3时显示缩略图片
- 占用空间非常小，不到50k


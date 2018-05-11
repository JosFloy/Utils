# EventBus 3.0使用详解

## 01 前言

> 当我们进行项目开发的时候，往往是需要应用程序的各组件、组件与后台线程间进行通信，比如在子线程中进行请求数据，当数据请求完毕后通过Handler或者是广播通知UI，而两个Fragment之间可以通过Listener进行通信等等。当我们的项目越来越复杂，使用Intent、Handler、Broadcast进行模块间通信、模块与后台线程进行通信时，代码量大，而且高度耦合。现在就让我们来学习一下EventBus 3.0吧。

## 02 什么是EventBus

[EventBus Github地址](https://link.jianshu.com?t=https%3A%2F%2Fgithub.com%2Fgreenrobot%2FEventBus)

[EventBus Github地址](https://link.jianshu.com?t=https%3A%2F%2Fgithub.com%2Fgreenrobot%2FEventBus)
进入官网，看看人家是怎么解释的：

> - simplifies the communication between components
>
> simplifies the communication between components decouples event senders and receivers
> performs well with Activities, Fragments, and background threads avoids complex and error-prone dependencies and life cycle issues
>
> - makes your code simpler
>
> - is fast
>
> - is tiny (~50k jar)
>
> - is proven in practice by apps with 100,000,000+ installs
>
> - has advanced features like delivery threads, subscriber priorities, etc.

大概的意思就是：**EventBus能够简化各组件间的通信，让我们的代码书写变得简单，能有效的分离事件发送方和接收方(也就是解耦的意思)，能避免复杂和容易出错的依赖性和生命周期问题。**

## 03 关于EventBus的概述

### 三要素

- Event  事件。它可以是任意类型。
- Subscriber 事件订阅者。在EventBus3.0之前我们必须定义以onEvent开头的那几个方法，分别是onEvent、onEventMainThread、onEventBackgroundThread和onEventAsync，而在3.0之后事件处理的方法名可以随意取，不过需要加上注解@subscribe()，并且指定线程模型，默认是POSTING。
- Publisher 事件的发布者。我们可以在任意线程里发布事件，一般情况下，使用EventBus.getDefault()就可以得到一个EventBus对象，然后再调用post(Object)方法即可。

### 四种线程模型

EventBus3.0有四种线程模型，分别是：

- POSTING (默认)  表示事件处理函数的线程跟发布事件的线程在同一个线程。
- MAIN 表示事件处理函数的线程在主线程(UI)线程，因此在这里不能进行耗时操作。
- BACKGROUND 表示事件处理函数的线程在后台线程，因此不能进行UI操作。如果发布事件的线程是主线程(UI线程)，那么事件处理函数将会开启一个后台线程，如果果发布事件的线程是在后台线程，那么事件处理函数就使用该线程。
- ASYNC 表示无论事件发布的线程是哪一个，事件处理函数始终会新建一个子线程运行，同样不能进行UI操作。

## 04 EventBus的基本用法

举个例子，我需要在一个Activity里注册EventBus事件，然后定义接收方法，这跟Android里的广播机制很像，你需要首先注册广播，然后需要编写内部类，实现接收广播，然后操作UI。所以，在EventBus中，你同样得这么做。

#### 自定义一个事件类

```
public class MessageEvent{
    private String message;
    public  MessageEvent(String message){
        this.message=message;
    }
    public String getMessage() {
        return message;
    }
 
    public void setMessage(String message) {
        this.message = message;
    }
}
```

这里有些同学，会有一些疑问，为什么要建立这样一个类，有什么用途。其实这个类就是一个Bean类，里面定义用来传输的数据的类型。

#### 注册事件

```
@Override
protected void onCreate(Bundle savedInstanceState) {           
     super.onCreate(savedInstanceState);
     setContentView(R.layout.activity_main)；
     EventBus.getDefault().register(this)；
} 
```

当我们需要在Activity或者Fragment里订阅事件时，我们需要注册EventBus。我们一般选择在Activity的onCreate()方法里去注册EventBus，在onDestory()方法里，去解除注册。

#### 解除注册

```
@Override
protected void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
}
```

#### 发送事件

```
EventBus.getDefault().post(messageEvent);
```

#### 处理事件

```
@Subscribe(threadMode = ThreadMode.MAIN)
public void XXX(MessageEvent messageEvent) {
    ...
}
```

前面我们说过，处理消息的方法名字可以随便取。但是需要加一个注解@Subscribe，并且要指定线程模型。

### 05 EventBus使用实战

以上我们讲了EventBus的基本用法，没有用过的同学也不要担心不会用，小编在这里举个小栗子。

#### 第一步:添加依赖

```
 compile 'org.greenrobot:eventbus:3.0.0'
```

#### 第二步:定义消息事件类

```
public class MessageEvent{
    private String message;
    public  MessageEvent(String message){
        this.message=message;
    }
 
    public String getMessage() {
        return message;
    }
 
    public void setMessage(String message) {
        this.message = message;
    }
}
```

#### 第三步:注册和解除注册

分别在FirstActivity的onCreate()方法和onDestory()方法里，进行注册EventBus和解除注册。

```
package com.example.lenovo.testapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lenovo.testapp.R;
import com.example.lenovo.testapp.event.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by ZZG on 2018/1/10.
 */

public class FirstActivity extends AppCompatActivity {
    private Button mButton;
    private TextView mText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity);
        mButton = (Button) findViewById(R.id.btn1);
        mText = (TextView) findViewById(R.id.tv1); 
        mText.setText("今天是星期三"); 
        EventBus.getDefault().register(this);
        jumpActivity();
    }

    private void jumpActivity() {

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FirstActivity.this,SecondActivity.class);
                startActivity(intent);
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        mText.setText(messageEvent.getMessage());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

}

```

#### 事件处理

在这里，事件的处理线程在主线程，是因为，我要让TextView去显示值。
在 SecondActivity里去进行事件的发送。

```
package com.example.lenovo.tezs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by ZZG on 2018/1/10.
 */

public class SecondActivity extends AppCompatActivity {
    private Button mButton2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        mButton2=(Button) findViewById(R.id.btn2);
        jumpActivity();
    }

    private void jumpActivity() {
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new MessageEvent("欢迎大家浏览我写的博客"));
                finish();
            }
        });
    }
}

```

很简单，当点击按钮的时候，发送了一个事件。

#### 运行程序。

在FirstActivity中，左边是一个按钮，点击之后可以跳转到SecondActivity，在按钮的右边是一个TextView，用来进行结果的验证。



这是SecondActivity，在页面的左上角，是一个按钮，当点击按钮，就会发送了一个事件，最后这个Activity就会销毁掉。



3.PNG

此时我们可以看到，FirstActivity里的文字已经变成了，我们在SecondActivity里设置的文字。

#### 总结

经过这个简单的例子，我们发现EventBus使用起来是如此的方便，当我们的代码量变得很多的时候，使用EventBus后你的逻辑非常的清晰，并且代码之间高度解耦，在进行组件、页面间通信的时候，EventBus是一个不错的选择。




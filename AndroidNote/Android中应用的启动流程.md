# Android中应用的启动流程

**前言**

在我们开始之前，希望您能最好已经满足以下条件：

     1、有一份编译后的Android源码（亲自动手实践才会有更深入的理解）

     2、对Binder机制有一定的了解

本文启动流程分析基于Android 5.1的源码。为什么是5.1的源码呢？因为手边编译完的代码只有这个版本…另外，用什么版本的源码并不重要，大体的流程并无本质上的区别，仅仅是实现细节的调整，找一个你熟悉的版本就好。

**1、启动时序图**

作为一个轻微强迫症的人，整理的时序图，相信大家按图索骥，一定能搞明白整个启动流程：

说明：为了让大家更清楚的理解整个过程，将时序图中划分为三个部分：**Launcher进程**、**System进程**、**App进程**，其中有涉及共用的类以L / A进行区分表示跟哪个进程有关，便于理解。

**2、关键类说明**

整个启动流程因为会涉及到多次Binder通信，这里先简要说明一下几个类的用途，方便大家理解整个交互流程：

      **1、ActivityManagerService：**AMS是Android中最核心的服务之一，主要负责系统中四大组件的启动、切换、调度及应用进程的管理和调度等工作，其职责与操作系统中的进程管理和调度模块相类似，因此它在Android中非常重要，它本身也是一个Binder的实现类。

      **2、Instrumentation：**顾名思义，它用来监控应用程序和系统的交互。

     ** 3、ActivityThread：**应用的入口类，系统通过调用main函数，开启消息循环队列。ActivityThread所在线程被称为应用的主线程（UI线程）。

      **4、ApplicationThread：**ApplicationThread提供Binder通讯接口，AMS则通过代理调用此App进程的本地方法。

      **5、ActivityManagerProxy：**AMS服务在当前进程的代理类，负责与AMS通信。

      **6、ApplicationThreadProxy：**ApplicationThread在AMS服务中的代理类，负责与ApplicationThread通信。

**3、流程分析**

首先交代下整个流程分析的场景：**用户点击Launcher上的应用图标到该应用主界面启动展示在用户眼前**。

这整个过程涉及到跨进程通信，所以我们将其划分为时序图中所展示三个进程：**Launcher进程**、**System进程**、**App进程**。为了不贴过长的代码又能说清楚进程间交互的流程，这里简述几个重要的交互点。

从时序图上大家也可以看到调用链相当长，对应的代码量也比较大，而且时序图只是分析了这个一个场景下的流程。道阻且长，行则将至！

**3.1 Launcher响应用户点击，通知AMS**

Launcher做为应用的入口，还是有必要交代一下的，我们来看看Launcher的代码片段，Launcher使用的是packages/apps/Launcher3的的源码。

```java
public class Launcher extends Activity
  implements View.OnClickListener, OnLongClickListener, LauncherModel.Callbacks,
     View.OnTouchListener, PageSwitchListener, LauncherProviderChangeListener {
 ...
 /**
  * Launches the intent referred by the clicked shortcut.
  *
  * @param v The view representing the clicked shortcut.
  */
 public void onClick(View v) {
  // Make sure that rogue clicks don't get through while allapps is launching, or after the
  // view has detached (it's possible for this to happen if the view is removed mid touch).
  if (v.getWindowToken() == null) {
   return;
  }
 
  ...
 
  Object tag = v.getTag();
  if (tag instanceof ShortcutInfo) {
   onClickAppShortcut(v);
  } else if (tag instanceof FolderInfo) {
   ...
  } else if (v == mAllAppsButton) {
   onClickAllAppsButton(v);
  } else if (tag instanceof AppInfo) {
   startAppShortcutOrInfoActivity(v);
  } else if (tag instanceof LauncherAppWidgetInfo) {
   ...
  }
 }
  
 private void startAppShortcutOrInfoActivity(View v) {
  ...
  boolean success = startActivitySafely(v, intent, tag);
  ...
 }
  
 boolean startActivitySafely(View v, Intent intent, Object tag) {
  ...
  try {
   success = startActivity(v, intent, tag);
  } catch (ActivityNotFoundException e) {
   ...
  }
  return success;
 }
  
 boolean startActivity(View v, Intent intent, Object tag) {
  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
  try {
   ...
 
   if (user == null || user.equals(UserHandleCompat.myUserHandle())) {
    // Could be launching some bookkeeping activity
    startActivity(intent, optsBundle);
   } else {
    ...
   }
   return true;
  } catch (SecurityException e) {
   ...
  }
  return false;
 }
}
```

通过starActicity辗转调用到`Activity:startActivityForResult`而后则调用至`Instrumentation:execStartActivity`，代码片段如下：

```java
public class Instrumentation {
 ...
 public ActivityResult execStartActivity(
   Context who, IBinder contextThread, IBinder token, Activity target,
   Intent intent, int requestCode, Bundle options) {
  IApplicationThread whoThread = (IApplicationThread) contextThread;
  ...
  try {
   ...
   int result = ActivityManagerNative.getDefault()
    .startActivity(whoThread, who.getBasePackageName(), intent,
      intent.resolveTypeIfNeeded(who.getContentResolver()),
      token, target != null ? target.mEmbeddedID : null,
      requestCode, 0, null, options);
   ...
  } catch (RemoteException e) {
  }
  return null;
 }
 ...
}
```

这里的`ActivityManagerNative.getDefault`返回`ActivityManagerService`的远程接口，即`ActivityManagerProxy`接口，有人可能会问了为什么会是`ActivityManagerProxy`，这就涉及到Binder通信了，这里不再展开。通过Binder驱动程序，`ActivityManagerProxy`与AMS服务通信，则实现了跨进程到System进程。

**3.2 AMS响应Launcher进程请求**

从上面的流程我们知道，此时AMS应该处理Launcher进程发来的请求，请参看时序图及源码，此时我们来看`ActivityStackSupervisor:startActivityUncheckedLocked`方法，目测这个方法已经超过600行代码，来看一些关键代码片段：

```java
public final class ActivityStackSupervisor implements DisplayListener {
 ...
 final int startActivityUncheckedLocked(ActivityRecord r, ActivityRecord sourceRecord,
   IVoiceInteractionSession voiceSession, IVoiceInteractor voiceInteractor, 
 int startFlags, boolean doResume, Bundle options, TaskRecord inTask) {
 final Intent intent = r.intent;
 final int callingUid = r.launchedFromUid;
 ...
 final boolean launchSingleTop = r.launchMode == ActivityInfo.LAUNCH_SINGLE_TOP;
 final boolean launchSingleInstance = r.launchMode == ActivityInfo.LAUNCH_SINGLE_INSTANCE;
 final boolean launchSingleTask = r.launchMode == ActivityInfo.LAUNCH_SINGLE_TASK; 
 
 int launchFlags = intent.getFlags();
 ...
 // We'll invoke onUserLeaving before onPause only if the launching
 // activity did not explicitly state that this is an automated launch.
 mUserLeaving = (launchFlags & Intent.FLAG_ACTIVITY_NO_USER_ACTION) == 0;
 ...
    
   ActivityRecord notTop =
    (launchFlags & Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP) != 0 ? r : null;
 
 // If the onlyIfNeeded flag is set, then we can do this if the activity
 // being launched is the same as the one making the call... or, as
 // a special case, if we do not know the caller then we count the
 // current top activity as the caller.
 if ((startFlags&ActivityManager.START_FLAG_ONLY_IF_NEEDED) != 0) {
 ...
 }
   ...
   // If the caller is not coming from another activity, but has given us an
 // explicit task into which they would like us to launch the new activity,
 // then let's see about doing that.
 if (sourceRecord == null && inTask != null && inTask.stack != null) {
 final Intent baseIntent = inTask.getBaseIntent();
 final ActivityRecord root = inTask.getRootActivity();
 ...
 // If this task is empty, then we are adding the first activity -- it
 // determines the root, and must be launching as a NEW_TASK.
 if (launchSingleInstance || launchSingleTask) {
 ...
 }
 ...
 }
   ...
   if (inTask == null) {
 if (sourceRecord == null) {
 // This activity is not being started from another... in this
 // case we -always- start a new task.
 if ((launchFlags & Intent.FLAG_ACTIVITY_NEW_TASK) == 0 && inTask == null) {
  Slog.w(TAG, "startActivity called from non-Activity context; forcing " +
       "Intent.FLAG_ACTIVITY_NEW_TASK for: " + intent);
  launchFlags |= Intent.FLAG_ACTIVITY_NEW_TASK;
 }
 } else if (sourceRecord.launchMode == ActivityInfo.LAUNCH_SINGLE_INSTANCE) {
 // The original activity who is starting us is running as a single
 // instance... this new activity it is starting must go on its
 // own task.
 launchFlags |= Intent.FLAG_ACTIVITY_NEW_TASK;
 } else if (launchSingleInstance || launchSingleTask) {
 // The activity being started is a single instance... it always
 // gets launched into its own task.
 launchFlags |= Intent.FLAG_ACTIVITY_NEW_TASK;
 }
 }
    
 ...
    
   // We may want to try to place the new activity in to an existing task. We always
 // do this if the target activity is singleTask or singleInstance; we will also do
 // this if NEW_TASK has been requested, and there is not an additional qualifier telling
 // us to still place it in a new task: multi task, always doc mode, or being asked to
 // launch this as a new task behind the current one.
 if (((launchFlags & Intent.FLAG_ACTIVITY_NEW_TASK) != 0 &&
    (launchFlags & Intent.FLAG_ACTIVITY_MULTIPLE_TASK) == 0)
    || launchSingleInstance || launchSingleTask) {
 // If bring to front is requested, and no result is requested and we have not
 // been given an explicit task to launch in to, and
 // we can find a task that was started with this same
 // component, then instead of launching bring that one to the front.
 if (inTask == null && r.resultTo == null) {
 // See if there is a task to bring to the front. If this is
 // a SINGLE_INSTANCE activity, there can be one and only one
 // instance of it in the history, and it is always in its own
 // unique task, so we do a special search.
 ActivityRecord intentActivity = !launchSingleInstance ?
  findTaskLocked(r) : findActivityLocked(intent, r.info);
 if (intentActivity != null) {
  ...
 }
 }
 }
   
 ...
    
   if (r.packageName != null) {
   // If the activity being launched is the same as the one currently
   // at the top, then we need to check if it should only be launched
   // once.
   ActivityStack topStack = getFocusedStack();
   ActivityRecord top = topStack.topRunningNonDelayedActivityLocked(notTop);
   if (top != null && r.resultTo == null) {
    if (top.realActivity.equals(r.realActivity) && top.userId == r.userId) {
     ...
    }
   }
   } else{
   ...
   }
   
 boolean newTask = false;
 boolean keepCurTransition = false;
 
 TaskRecord taskToAffiliate = launchTaskBehind && sourceRecord != null ?
    sourceRecord.task : null;
 
 // Should this be considered a new task?
 if (r.resultTo == null && inTask == null && !addingToTask
    && (launchFlags & Intent.FLAG_ACTIVITY_NEW_TASK) != 0) {
 ...
 if (reuseTask == null) {
    r.setTask(targetStack.createTaskRecord(getNextTaskId(),
      newTaskInfo != null ? newTaskInfo : r.info,
      newTaskIntent != null ? newTaskIntent : intent,
      voiceSession, voiceInteractor, !launchTaskBehind /* toTop */),
      taskToAffiliate);
    ...
   } else {
    r.setTask(reuseTask, taskToAffiliate);
   }
    ...
 } else if (sourceRecord != null) {
    
 } else if (!addingToTask &&
     (launchFlags&Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) != 0) {
       
 } else if (inTask != null){
    
 } else {
    
 }
   
 ...
   
 targetStack.startActivityLocked(r, newTask, doResume, keepCurTransition, options);
    
 ...
   return ActivityManager.START_SUCCESS;
 }
 ...
}
```

函数经过intent的标志值设置，通过`findTaskLocked`函数来查找存不存这样的Task，这里返回的结果是null，即`intentActivity`为null，因此，需要创建一个新的Task来启动这个`Activity`。现在处理堆栈顶端的`Activity`是`Launcher`，与我们即将要启动的`MainActivity`不是同一个`Activity`，创建了一个新的Task里面来启动这个`Activity`。

经过栈顶检测，则需要将Launcher推入Paused状态，才可以启动新的`Activity`。后续则调用至`ActivityStack:startPausingLocked`，我们来看一下这个函数：

```java
final class ActivityStack {
 ...
 final boolean startPausingLocked(boolean userLeaving, boolean uiSleeping, boolean resuming,
   boolean dontWait) {
 if (mPausingActivity != null) {
  ...
 } 
 ActivityRecord prev = mResumedActivity;
 if (prev == null) {
  ...
 }
 ...
 mResumedActivity = null;
 mPausingActivity = prev;
 mLastPausedActivity = prev;
 mLastNoHistoryActivity = (prev.intent.getFlags() & Intent.FLAG_ACTIVITY_NO_HISTORY) != 0
    || (prev.info.flags & ActivityInfo.FLAG_NO_HISTORY) != 0 ? prev : null;
 prev.state = ActivityState.PAUSING;
 ...
  
 if (prev.app != null && prev.app.thread != null) {
  try {
 ...
 prev.app.thread.schedulePauseActivity(prev.appToken, prev.finishing,
      userLeaving, prev.configChangeFlags, dontWait);
  } catch (Exception e) {
 ...
  }
 } else {
  ...
 }
 ...
 }
 ...
}
```

这里的`prev.app.thread`是一个`ApplicationThread`对象的远程接口，通过调用这个远程接口的`schedulePauseActivity`来通知Launcher进入Paused状态。至此，AMS对Launcher的请求已经响应，这是我们发现又通过Binder通信回调至Launcher进程。

**3.3 Launcher进程挂起Launcher，再次通知AMS**

这个流程相对会简单一些，我们来看`ActivityThread`:

```java
public final class ActivityThread {
 ...
 private void handlePauseActivity(IBinder token, boolean finished,
   boolean userLeaving, int configChanges, boolean dontReport) {
  ActivityClientRecord r = mActivities.get(token);
  if (r != null) {
   ...
   performPauseActivity(token, finished, r.isPreHoneycomb());
 
   // Make sure any pending writes are now committed.
   if (r.isPreHoneycomb()) {
    QueuedWork.waitToFinish();
   }
 
   // Tell the activity manager we have paused.
   if (!dontReport) {
    try {
     ActivityManagerNative.getDefault().activityPaused(token);
    } catch (RemoteException ex) {
    }
   }
   ...
  }
 }
 ...
}
```

这部分Launcher的`ActivityThread`处理页面Paused并且再次通过`ActivityManagerProxy`通知AMS。

**3.4 AMS创建新的进程**

创建新进程的时候，AMS会保存一个`ProcessRecord`信息，如果应用程序中的AndroidManifest.xml配置文件中，我们没有指定Application标签的process属性，系统就会默认使用package的名称。每一个应用程序都有自己的uid，因此，这里uid + process的组合就可以为每一个应用程序创建一个`ProcessRecord`。

```java
public final class ActivityManagerService extends ActivityManagerNative
  implements Watchdog.Monitor, BatteryStatsImpl.BatteryCallback {
 ...
 private final void startProcessLocked(ProcessRecord app, String hostingType, String hostingNameStr, String abiOverride, String entryPoint, String[] entryPointArgs) {
 ...
 try {
  ...
  // Start the process. It will either succeed and return a result containing
  // the PID of the new process, or else throw a RuntimeException.
  boolean isActivityProcess = (entryPoint == null);
  if (entryPoint == null) entryPoint = "android.app.ActivityThread";
  Process.ProcessStartResult startResult = Process.start(entryPoint,
     app.processName, uid, uid, gids, debugFlags, mountExternal,
     app.info.targetSdkVersion, app.info.seinfo, requiredAbi, instructionSet,
     app.info.dataDir, entryPointArgs);
  ...
 } catch () {
  ...
 }
 }
 ...
}
```

这里主要是调用`Process:start`接口来创建一个新的进程，新的进程会导入`android.app.ActivityThread`类，并且执行它的`main`函数，这就是每一个应用程序都有一个`ActivityThread`实例来对应的原因。

**3.5 应用进程初始化**

我们来看`Activity`的`main`函数，这里绑定了主线程的Looper，并进入消息循环，大家应该知道，整个Android系统是消息驱动的，这也是为什么主线程默认绑定Looper的原因：

```java
public final class ActivityThread {
 ...
 public static void main(String[] args) {
  ...
  Looper.prepareMainLooper();
 
  ActivityThread thread = new ActivityThread();
  thread.attach(false);
 
  ...
 
  Looper.loop();
   
  ...
 }
  
 private void attach(boolean system) {
  ...
  if (!system) {
   ...
   final IActivityManager mgr = ActivityManagerNative.getDefault();
   try {
    mgr.attachApplication(mAppThread);
   } catch (RemoteException ex) {
    // Ignore
   }
  } else {
   ...
  }
  ...
 }
 ...
}
```

attach函数最终调用了`ActivityManagerService`的远程接口`ActivityManagerProxy的attachApplication`函数，传入的参数是`mAppThread`，这是一个`ApplicationThread`类型的`Binder`对象，它的作用是AMS与应用进程进行进程间通信的。

**3.6 在AMS中注册应用进程，启动启动栈顶页面**

前面我们提到了AMS负责系统中四大组件的启动、切换、调度及应用进程的管理和调度等工作，通过上一个流程我们知道应用进程创建后通过Binder驱动与AMS产生交互，此时AMS则将应用进程创建后的信息进行了一次注册，如果拿Windows系统程序注册到的注册表来理解这个过程，可能会更形象一些。

`mMainStack.topRunningActivityLocked(null)`从堆栈顶端取出要启动的`Activity`，并在`realStartActivityLockedhan`函数中通过`ApplicationThreadProxy`调回App进程启动页面。

```java
public final class ActivityStackSupervisor implements DisplayListener {
 ...
 final boolean realStartActivityLocked(ActivityRecord r,
   ProcessRecord app, boolean andResume, boolean checkConfig)
   throws RemoteException {
  ...
  r.app = app;
  ...
    
  try {
  ...
  app.thread.scheduleLaunchActivity(new Intent(r.intent), r.appToken,
     System.identityHashCode(r), r.info, new Configuration(mService.mConfiguration),
     r.compat, r.launchedFromPackage, r.task.voiceInteractor, app.repProcState,
     r.icicle, r.persistentState, results, newIntents, !andResume,
     mService.isNextTransitionForward(), profilerInfo);
  ...
  } catch (RemoteException e) {
  ...
  }
  ...
 }
 ...
}
```

此时在App进程，我们可以看到，经过一些列的调用链最终调用至`MainActivity:onCreate`函数，之后会调用至`onResume`，而后会通知AMS该`MainActivity`已经处于`resume`状态。至此，整个启动流程告一段落。

**4、总结**

通过上述流程，相信大家可以有了一个基本的认知，这里我们忽略细节简化流程，单纯从进程角度来看下图： launch_app_sim

 ![2016822113838460](D:\我的文档\My Pictures\2016822113838460.png)
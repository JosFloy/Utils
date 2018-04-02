package com.mingrisoft.flowersdemo.ui.utils;

import android.util.Log;

/**
 * Log统一管理类-->上网下载现成的就可以
 * 一搜索一大把
 */
public class UtilLog
{

	private UtilLog()
	{
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	public static boolean isDebug = true;// 是否需要打印bug，可以在application的onCreate函数里面初始化
	private static final String TAG = "UtilLog";

	// 下面四个是默认tag的函数
	public static void i(String msg)
	{
		if (isDebug)
			Log.i(TAG, msg);
	}

	public static void d(String msg)
	{
		if (isDebug)
			Log.d(TAG, msg);
	}

	public static void e(String msg)
	{
		if (isDebug)
			Log.e(TAG, msg);
	}

	public static void v(String msg)
	{
		if (isDebug)
			Log.v(TAG, msg);
	}

	// 下面是传入自定义tag的函数
	public static void i(String tag, String msg)
	{
		if (isDebug)
			Log.i(tag, msg);
	}

	public static void d(String tag, String msg)
	{
		if (isDebug)
			Log.d(tag, msg);
	}

	public static void e(String tag, String msg)
	{
		if (isDebug)
			Log.e(tag, msg);
	}

	public static void v(String tag, String msg)
	{
		if (isDebug)
			Log.v(tag, msg);
	}

	/**
	* 把TAG 设置为默认的属性，如果用户输入tag就用输入的，如果没有就用默认的TAG
	**/
	public static void v(String tag,String msg){
		String ltag=tag;
		if(ltag==null){
			ltag=TAG;
		}else{
			ltag=tag;
		if(isDebug){
			Log.v(ltag,msg);
		}
	}
}
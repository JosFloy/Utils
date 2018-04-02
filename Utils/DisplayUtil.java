package com.mingrisoft.flowersdemo.ui.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 获取显示相关信息，
 */
public class DisplayUtil {
	
	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = getDisplayMetrics(context);
		return dm.widthPixels;
	}

	@NonNull
	private static DisplayMetrics getDisplayMetrics(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		assert wm != null;
		wm.getDefaultDisplay().getMetrics(dm);
		return dm;
	}

	public static int getScreenHeight(Context context) {
		DisplayMetrics dm = getDisplayMetrics(context);
		return dm.heightPixels;
	}

	public static float getScreenDensity(Context context) {
		DisplayMetrics dm = getDisplayMetrics(context);
		return dm.density;
	}
}

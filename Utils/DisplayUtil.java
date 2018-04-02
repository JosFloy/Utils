import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 获取显示相关信息，
 */
public class DisplayUtil {

	private static float scale;
	/**
	 * 获取手机屏幕宽度
	 * @param  context Context
	 * @return         宽度
	 */
	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = getDisplayMetrics(context);
		return dm.widthPixels;
	}

	/**
	 * 获取显示元数据
	 */
	@NonNull
	private static DisplayMetrics getDisplayMetrics(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		assert wm != null;
		wm.getDefaultDisplay().getMetrics(dm);
		return dm;
	}

	/**
	 * 获取屏幕高度
	 * @param  context Context
	 * @return         高度
	 */
	public static int getScreenHeight(Context context) {
		DisplayMetrics dm = getDisplayMetrics(context);
		return dm.heightPixels;
	}
	/**
	 * 获取屏幕密度
	 * @param  context Context
	 * @return         密度
	 */
	public static float getScreenDensity(Context context) {
		DisplayMetrics dm = getDisplayMetrics(context);
		return dm.density;
	}

  /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        if (scale == 0) {
            scale = getScreenDensity(context);
        }
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        if (scale == 0) {
            scale = getScreenDensity(context);
        }
        return (int) (pxValue / scale + 0.5f);
    }
}

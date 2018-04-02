import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 创建用于图片分享的文件工具类
 */
public class FileIOUtils {
    public static File  ExternalStorageDirectory= Environment.getExternalStorageDirectory();//根目录
    /**
     * 判断文件夹是否被创建
     * @param file 文件
     * @param name 文件名
     * @param bitmap 要压缩写入文件的图片
     * @return  创建成功则返回文件的路径，失败这返回null
     * @throws FileNotFoundException 向上抛出异常
     */
    public static String writeFile(File file, String name, Bitmap bitmap)
            throws FileNotFoundException {
        File save_bitmap = new File(file,name);
        boolean result = bitmap.compress(Bitmap.CompressFormat.JPEG,100,
                new BufferedOutputStream(new FileOutputStream(save_bitmap)));
        if (result){
            return save_bitmap.getPath();
        }
        return null;
    }

    public static Bitmap readFile(String path) throws IOException {
        return BitmapFactory.decodeFile(path);
    }
}

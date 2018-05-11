# android创建数据库(SQLite)保存图片示例

```java
/1.创建数据库
public class DBService extends SQLiteOpenHelper {

private final static int VERSION = 1;
private final static String DATABASE_NAME = "uniteqlauncher.db";

public DBService(Context context) {
    this(context, DATABASE_NAME, null, VERSION);
}

public DBService(Context context, String name, CursorFactory factory,
        int version) {
    super(context, name, factory, version);
}

@Override
public void onCreate(SQLiteDatabase db) {
    String sql = "CREATE TABLE [launcher]("
        + "[_id] INTEGER PRIMARY KEY AUTOINCREMENT,"
        + "[photo] BINARY)"; //保存为binary格式

    db.execSQL(sql);

}

@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    if(newVersion > oldVersion){
        db.execSQL("DROP TABLE IF EXISTS[launcher]");
    } else {
        return;
    }
    onCreate(db);
}
}
//保存图片到数据库
public void savePhoto(Drawable appIcon, Context mContext){
LayoutInflater mInflater = (LayoutInflater) mContext
.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
View v = inflater.inflate(R.layout.app_view, null);
ImageView iv = (ImageView) v.findViewById(R.id.appicon);
iv.setImageDrawable(appIcon);
String INSERT_SQL = "INSERT INTO launcher(photo) values(?)";
SQLiteDatabase db = mDBService.getWritableDatabase(); // 得到数据库
try {
ByteArrayOutputStream baos = new ByteArrayOutputStream();
((BitmapDrawable) iv.getDrawable()).getBitmap().compress(
CompressFormat.PNG, 100, baos);//压缩为PNG格式,100表示跟原图大小一样
Object[] args = new Object[] {baos.toByteArray() };
db.execSQL(INSERT_SQL, args);
baos.close();
db.close();
} catch (Exception e) {
e.printStackTrace();
}

}

//3.从数据库中取图片
public void getPhoto() {
String SELECT_SQL = "SELECT photo FROM launcher";
ImageView appIcon = (ImageView) v.findViewById(R.id.appicon);//v是我在类中定义的一个view对象，跟前面保存图片一样
byte[] photo = null;
mDBService = new DBService(getContext());
SQLiteDatabase db = mDBService.getReadableDatabase();
Cursor mCursor = db.rawQuery(SELECT_SQL, null);
if (mCursor != null) {
if (mCursor.moveToFirst()) {//just need to query one time
photo = mCursor.getBlob(mCursor.getColumnIndex("photo"));//取出图片
}
}
if (mCursor != null) {
mCursor.close();
}
db.close();
    ByteArrayInputStream bais = null;
if (photo != null) {
        bais = new ByteArrayInputStream(photo);
        appIcon.setImageDrawable(Drawable.createFromStream(bais, "photo"));//把图片设置到ImageView对象中
}
    //appIcon显示的就是之前保存到数据库中的图片
}
```
# RecyclerView

RecyclerView为新增的控件，要想使用需要在**项目**的`build.gradle`中添加相应的依赖库

打开`app/build.gradle`文件，在dependencies闭包中添加如下内容：

```groovy
dependencies{
  compile fileTree(dir:'libs',include:['*.jar'])
  compile 'com.android.support:recyclerview-v7:24.2.1'
  textCompile 'junit:junit:4.12'
}
```

添加完后记得点击一下Sync Now来进行同步，然后修改activity_main.xml中的代码：

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
              android:layout_width="match_parent"
              android:layout_height="match_parent">
	<android.support.v7.widget.RecyclerView
             android:id="@+id/recycler_view"
             android:layout_width="match_parent"
             android:layout_height="match_praent"/>
</LinearLayout>
```

**由于RecyclerView不是内置在系统SDK中，所有要把完整的包路径写出来**

---

### 基本用法

1、先构建一个用于显示的数据类Fruit.java

```java
public class Fruit {
    private String name;
    private int imageId;

    public Fruit(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public int getImageId() {
        return imageId;
    }
}
```

2、构建一个用来连接数据和View的Adapter(适配器)，继承自RecyclerView.Adapter，并将泛型指定为继承自RecyclerView.ViewHolder的内部类fruitAdapter.ViewHolder

```java
public class RecyFruitAdapter extends RecyclerView.Adapter<RecyFruitAdapter.ViewHolder> {
    private List<Fruit> mFruitList;

    public RecyFruitAdapter(List<Fruit> fruitList){
        mFruitList=fruitList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fruit_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.fruitView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Fruit fruit=mFruitList.get(position);
                Toast.makeText(v.getContext(), "You clicked view"+fruit.getName(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        holder.fruitImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int positon=holder.getAdapterPosition();
                Fruit fruit=mFruitList.get(positon);
                Toast.makeText(view.getContext(), "you clicked image"+fruit.getName(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Fruit fruit=mFruitList.get(position);
        holder.fruitImage.setImageResource(fruit.getImageId());
        holder.fruitName.setText(fruit.getName());
    }

    @Override
    public int getItemCount() {
        return mFruitList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        View fruitView;
        ImageView fruitImage;
        TextView fruitName;
        public ViewHolder(View view){
            super(view);
            fruitView=view;
            fruitImage=view.findViewById(R.id.fruit_image);
            fruitName=view.findViewById(R.id.fruit_name);
        }
    }
}
```

​	以上代码，首先定义了一个内部类ViewHolder，必须要继承自RecyclerView.ViewHolder，然后ViewHolder的构造函数中要传入一个View参数，这个参数通常就是RecyclerView子项的最外层布局，那么我们就可以通过findViewById()方法来获取布局中的ImageView和TextView的实例了

​	接下来FruitAdapter中也有**一个构造函数，用来把要展示的数据源传进来，并赋值给一个全局变量mFruitList，我们后续的操作都将在这个数据源的基础上进行。**

​	由于FruitAdapter是继承自RecyclerView.Adapter的，那么必须要重写onCreateViewHolder()、onBindViewHolder()和getItemCount()这个3个方法。

* **onCreateViewHolde()** 用于创建ViewHolder实例的，我们在这个方法中将fruit_item布局加载进来后创建一个ViewHolder实例，并把加载进来的布局传入**构造函数**当中，最后将ViewHolder的实例返回
* **onBindViewHolder()** 用于对RecyclerView子项的数据进行赋值的，会在每个子项被滚动到屏幕内的时候执行，这里我们通过position参数得到当前项的Fruit实例，然后再将数据设置到ViewHolder的ImageView和TextView当中即可
* **getItemCount()** 用于告诉RecyclerView一共有多少个子项，直接返回数据源的长度就可以了

3、适配器准备好了就可以使用RecyclerView了，修改MainActivity中的代码

```java
public class RecyclerViewDemo extends AppCompatActivity {
    private List<Fruit> mFruitList =new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_demo);
        initFruits();
        RecyclerView recyclerView=findViewById(R.id.recycler_view);
    /*    LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);*/
    /*    GridLayoutManager gridLayoutManager=new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(gridLayoutManager);*/
        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(3,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        RecyFruitAdapter adapter=new RecyFruitAdapter(mFruitList);
        recyclerView.setAdapter(adapter);
    }

    private void initFruits() {
        for (int i = 0; i < 2; i++) {
            Fruit apple = new Fruit(getRandomLengthName("Apple"), R.drawable.apple_pic);
            mFruitList.add(apple);
            Fruit banana = new Fruit(getRandomLengthName("Banana"), R.drawable.banana_pic);
            mFruitList.add(banana);
            Fruit orange = new Fruit(getRandomLengthName("Orange"), R.drawable.orange_pic);
            mFruitList.add(orange);
            Fruit watermelon = new Fruit(getRandomLengthName("Watermelon"), R.drawable.watermelon_pic);
            mFruitList.add(watermelon);
            Fruit pear = new Fruit(getRandomLengthName("Pear"), R.drawable.pear_pic);
            mFruitList.add(pear);
            Fruit grape = new Fruit(getRandomLengthName("Grape"), R.drawable.grape_pic);
            mFruitList.add(grape);
            Fruit pineapple = new Fruit(getRandomLengthName("Pineapple"), R.drawable.pineapple_pic);
            mFruitList.add(pineapple);
            Fruit strawberry = new Fruit(getRandomLengthName("Strawberry"), R.drawable.strawberry_pic);
            mFruitList.add(strawberry);
            Fruit cherry = new Fruit(getRandomLengthName("Cherry"), R.drawable.cherry_pic);
            mFruitList.add(cherry);
            Fruit mango = new Fruit(getRandomLengthName("Mango"), R.drawable.mango_pic);
            mFruitList.add(mango);
        }
    }
    private String getRandomLengthName(String name){
        Random random=new Random();
        int length=random.nextInt(20)+1;
        StringBuilder builder=new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(name);
        }
        return builder.toString();
    }
}
```

RecyclerView的布局排列不是由自身去管理，它把这个工作交个**了LayoutManager**来做，在LM中制定了一套可扩展的布局排列接口，子类只要按照接口的规范来实现，就能定制各种不同排列方式的布局

4、RecyclerView的点击事件

它并没有提供类似于setOnItecmClickListener()这样的注册监听器方法，而是需要我们自己给子项具体的View去注册点击事件，这样我们可以更灵活的去定制自己想要的点击事件，比如我们可以定于子项内具体的一个或者几个控件的监听方法

```java
static class ViewHolder extends RecyclerView.ViewHolder{
  View fruitView;
  .....
  public ViewHolder(View view){
  	super(view);
    fruitView=view;
    ....
  }
}
@Override
public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
  ......
  final ViewHolder=new ViewHolder(view);
  holder.fruitView.setOnClickListener(new View.OnClickListener(){
    @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Fruit fruit=mFruitList.get(position);
                Toast.makeText(v.getContext(), "You clicked view"+fruit.getName(),
                        Toast.LENGTH_SHORT).show();
            }
});
}
```

我们现实修改了ViewHolder，在ViewHolder中添加了fruitView变量来保存子项最外层布局的实例，然后在onCreateViewHolder()方法中注册点击事件就可以了
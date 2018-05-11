# RecyclerView添加头部和底部

如果只是想添加头部，可是使用GitHub里面这个项目，它可以为LinearLayoutManager，GridLayoutManager ，StaggeredGridLayoutManager布局的RecyclerView添加header。使用起来也十分简单； **只需将RecyclerViewHeader布局放在RecyclerView的上层**。

```xml
<FrameLayout
  android:layout_width="match_parent"
  android:layout_height="wrap_content">
 
  <android.support.v7.widget.RecyclerView
    android:id="@+id/recycler"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_gravity="center_horizontal|top" />
 
  <com.bartoszlipinski.recyclerviewheader.RecyclerViewHeader
    android:id="@+id/header"
    android:layout_width="match_parent"
    android:layout_height="100dp"
    android:layout_gravity="center_horizontal|top">
 
    <TextView
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"
      android:layout_centerInParent="true"
      android:text="header"/>
 
  </com.bartoszlipinski.recyclerviewheader.RecyclerViewHeader>
 
</FrameLayout>
```

然后获得RecyclerViewHeader对象：

```java
RecyclerViewHeader header = (RecyclerViewHeader) findViewById(R.id.header);
```

把RecyclerViewHeader赋予RecyclerView

```java
RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
// set LayoutManager for your RecyclerView
header.attachTo(recyclerView, true);
```

**注意事项** 

**RecyclerViewHeader必须在RecyclerView设置了LayoutManager之后调用**。

目前该库适用于LinearLayoutManager,StaggeredGridLayoutManager和GridLayoutManager布局的RecyclerViews。只支持垂直布局LayoutManager。如果你打算在RecyclerView中使用setOnScrollListener(…)方法，确保在setOnScrollListener(…)的attachTo(…)方法之前使用。

---

当然我们也可以自己写一个添加头部和底部的RecyclerView。它实现的基本原理也是

**通过getItemViewType方法返回不同的类型来添加头部和底部**。 

首先我们自定义一个RecyclerView：

```java
public class WrapRecyclerView extends RecyclerView {
  public ArrayList<View> mHeaderViews = new ArrayList<>();
  public ArrayList<View> mFooterViews = new ArrayList<>();
  //添加Adapter
  public Adapter mAdapter;
  public WrapRecyclerView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }
 
  public WrapRecyclerView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }
 
  public WrapRecyclerView(Context context) {
    super(context);
  }
  public void addHeaderView(View view){
    mHeaderViews.clear();
    mHeaderViews.add(view);
    if(mAdapter!=null){
      if(!(mAdapter instanceof RecyclerWrapAdapter)){
        mAdapter = new RecyclerWrapAdapter(mHeaderViews,mFooterViews,mAdapter);
      }
    }
  }
  public void addFooterView(View view){
    mFooterViews.clear();
    mFooterViews.add(view);
    if(mAdapter!=null){
      if(!(mAdapter instanceof RecyclerWrapAdapter)){
        mAdapter = new RecyclerWrapAdapter(mHeaderViews,mFooterViews,mAdapter);
      }
    }
  }
  public void setAdapter(Adapter adapter){
    if (mHeaderViews.isEmpty()&&mFooterViews.isEmpty()){
 
      super.setAdapter(adapter);
    }else {
      adapter = new RecyclerWrapAdapter(mHeaderViews,mFooterViews,adapter) ;
      super.setAdapter(adapter);
    }
    mAdapter = adapter ;
  }
}
```

我们会看到我们有一个RecyclerWrapAdapter没有实现，下面我们就来看下RecyclerWrapAdapter，这个也是实现添加头部和尾部的关键。

```java
ublic class RecyclerWrapAdapter extends RecyclerView.Adapter implements WrapperAdapter{
  private RecyclerView.Adapter mAdapter;
 
  private ArrayList<View> mHeaderViews;
 
  private ArrayList<View> mFootViews;
  static final ArrayList<View> EMPTY_INFO_LIST =
      new ArrayList<View>();
  private int mCurrentPosition;
  public RecyclerWrapAdapter(ArrayList<View> mHeaderViews, ArrayList<View> mFootViews, RecyclerView.Adapter mAdapter){
    this.mAdapter = mAdapter;
    if (mHeaderViews == null) {
      this.mHeaderViews = EMPTY_INFO_LIST;
    } else {
      this.mHeaderViews = mHeaderViews;
    }
    if (mFootViews == null) {
      this.mFootViews = EMPTY_INFO_LIST;
    } else {
      this.mFootViews = mFootViews;
    }
  }
 
  public int getHeadersCount() {
    return mHeaderViews.size();
  }
 
  public int getFootersCount() {
    return mFootViews.size();
  }
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == RecyclerView.INVALID_TYPE) {
      return new HeaderViewHolder(mHeaderViews.get(0));
    } else if (viewType == RecyclerView.INVALID_TYPE - 1) {
      return new HeaderViewHolder(mFootViews.get(0));
    }
    return mAdapter.onCreateViewHolder(parent, viewType);
  }
 
  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    //如果头部不为空，那么我们就要先添加头部，所以我们只要
    //把前面几个position给头部，当position小于头部总数的时候，
    //我们返回头部view。再判断原Adapter 的 count 与当前 position 
    // 的差值来比较，是调用原 Adapter 的 getView 方法，还是获取 footView
    // 的 view。
    int numHeaders = getHeadersCount();
    if (position < numHeaders) {
      return;
    }
    int adjPosition = position - numHeaders;
    int adapterCount = 0;
    if (mAdapter != null) {
      adapterCount = mAdapter.getItemCount();
      if (adjPosition < adapterCount) {
        mAdapter.onBindViewHolder(holder, adjPosition);
        return;
      }
    }
  }
 
  @Override
  public int getItemCount() {
    if (mAdapter != null) {
      return getHeadersCount() + getFootersCount() + mAdapter.getItemCount();
    } else {
      return getHeadersCount() + getFootersCount();
    }
  }
 
  @Override
  public RecyclerView.Adapter getWrappedAdapter() {
    return mAdapter;
  }
  @Override
  public int getItemViewType(int position) {
    //增加两个类型
    //RecyclerView.INVALID_TYPE 添加头部
    //RecyclerView.INVALID_TYPE-1 添加尾部
    //如果头部不为空，那么我们就要先添加头部，所以我们只要
    //把前面几个position给头部，当position小于头部总数的时候，
    //我们返回头部类型。再判断原Adapter 的 count 与当前 position 
    // 的差值来比较，是调用原 Adapter 的 类型，还是获取 footView
    // 的类型。
    mCurrentPosition = position ;
    int numHeaders = getHeadersCount();
    if(position<numHeaders){
      return RecyclerView.INVALID_TYPE ;
    }
    int adjPosition = position - numHeaders ;
    int adapterCount = 0 ;
    if(mAdapter!=null){
      adapterCount = mAdapter.getItemCount() ;
      if(adjPosition < adapterCount){
        return mAdapter.getItemViewType(adjPosition);
      }
    }
    return RecyclerView.INVALID_TYPE - 1;
  }
  private static class HeaderViewHolder extends RecyclerView.ViewHolder {
    public HeaderViewHolder(View itemView) {
      super(itemView);
    }
  }
}
```

我们还可以实现一个接口，来调用RecyclerWrapAdapter对象：

```java
public interface WrapperAdapter {
 
  public RecyclerView.Adapter getWrappedAdapter() ;
}
```

这样我们就可以把RecyclerView布局改成WrapRecyclerView就可以了，然后调用addHeaderView或者addFooterView就可以添加头部和尾部了。
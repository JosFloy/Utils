# TTS语音合成

一、基础知识

1.1 Android上的TTS介绍

​	TextToSpeech简称 TTS，称为语音合成，是Android 系统从1.6版本开始支持的新功能，能将所指定的文本转成不同语言音频输出。
​	首先来看下Adnroid系统带的TTS设置界面，点击Settings->Speech synthesis（这是1.6版本，如果是2.1版本的话是Settings->Text to Speech）,如下图1所示：

 ![图片2](D:\我的文档\My Pictures\图片2.png)

TTS功能需要有TTS Engine的支持，下面我们就来了解下android提供的TTS Engine。

​	Android使用了叫Pico的支持多种语言的语音合成引擎，Pico在后台负责分析把输入的文本，分成它能识别的各个片段，再把合成的各个语音片段以听起来比较自然的方式连接在一起，这个过程Android系统帮我们做，我们只把他当做一个神奇的过程就可以了。

​	TTS engine依托于当前Android Platform所支持的几种主要的语言：English、French、German、Italian和Spanish五大语言（暂时也是没有对中文提供支持）。TTS可以将文本随意的转换成以上任意五种语言的语音输出。与此同时，对于个别的语言版本将取决于不同的时区，例如：对于English，在 TTS中可以分别输出美式和英式两种不同的版本。

```xml
<?xml version="1.0" encoding="UTF8"?>
<LinearLayout 
	android:layout_height="fill_parent" 
	android:layout_width="fill_parent" 
	android:orientation="vertical" 
	xmlns:android="http://schemas.android.com/apk/res/android"> 
	<EditText 
		android:id="@+id/inputText" 
		android:layout_width="fill_parent"
		android:layout_height="wrap_content" 
		android:hint="Input the text here!"/> 
	<Button android:enabled="false" 
		android:id="@+id/speakBtn" 
		android:layout_gravity="center_horizontal" 
		android:layout_height="wrap_content" 
        android:layout_width="wrap_content" 
		android:text="Speak"/> 
	<Spinner android:id="@+id/langSelect" 
		android:layout_gravity="center_horizontal" 
		android:layout_height="wrap_content" 
		android:layout_width="wrap_content"/> 
</LinearLayout>  
```

2.2要使用TTS得实现`OnInitListener()`接口

```java
public class OPhoneTTSDemo extends Activity implements OnInitListener{ 
	/** Called when the activity is first created. */ 
	@Override 
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.main); 
	} 
	@Override 
	public void onInit(int status) { 
  } 
}     
```

`OnInitListener` 这个接口中只有一个抽象函数`void onInit(int status)`，在TextToSpeech引擎初始化完成后调用，可以根据状态status（为 `TextToSpeech.SUCCESS`或者`TextToSpeech.Error`）判断TTS初始化成功与否进行相应的操作。

​	接着定义好下面要用到的几个变量：

```java
private EditText inputText = null; 
private Button speakBtn = null; 
private static final int REQ_TTS_STATUS_CHECK = 0; 
private static final String TAG = "TTS Demo"; 
private TextToSpeech mTts; 
private Spinner langSelect = null; 
private String languages[]={"English","French","German","Italian","Spanish"};  
```

检查是否有安装TTS语言数据

```java
// 检查TTS数据是否已经安装并且可用 
 Intent checkIntent = new Intent(); 
 checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA); 
 startActivityForResult(checkIntent, REQ_TTS_STATUS_CHECK); 
```

这里启动一个新的Activity去检查TTS数据是否已经安装，
这个Activity会返回如下结果之一：

- CHECK_VOICE_DATA_PASS
- CHECK_VOICE_DATA_MISSING_VOLUME. , 
- CHECK_VOICE_DATA_FAIL,
- CHECK_VOICE_DATA_BAD_DATA, 
- CHECK_VOICE_DATA_MISSING_DATA,

只有第一个结果CHECK_VOICE_DATA_PASS表明TTS数据可用，其他都是数据不可用的结果，可以启动一个Activity去安装需要的TTS数据，
我们根据其返回的结果进行处理，如下所示：

```java
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if(requestCode == REQ_TTS_STATUS_CHECK) { 
		switch (resultCode) { 
			case TextToSpeech.Engine.CHECK_VOICE_DATA_PASS:{ // 这个返回结果表明TTS Engine可以用 
				mTts = new TextToSpeech(this, this); 
				Log.v(TAG, "TTS Engine is installed!"); 
			} 
			break; 
			case TextToSpeech.Engine.CHECK_VOICE_DATA_BAD_DATA: // 需要的语音数据已损坏 
			case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_DATA: //缺少需 要语言的语音数据 
			case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_VOLUME: //缺少需 要语言的发音数据 
			{ //这三种情况都表明数据有错,重新下载安装需要的数据 
				Log.v(TAG, "Need language stuff:"+resultCode); 
				Intent dataIntent = new Intent(); 
				dataIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA); 
				startActivity(dataIntent); 
			} 
			break; 
			case TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL: 
			// 检查失败 
			default: 
				Log.v(TAG, "Got a failure. TTS apparently not available"); 
			break; 
			} 
		} else{ 
		// 其他Intent返回的结果 
	} 
} 
```

如果返回CHECK_VOICE_DATA_PASS表示检查成功，可以新建一个TextToSpeech实例，


```java
public TextToSpeech (Context context, TextToSpeech.OnInitListener listener)
```

这里需要两个参数，一个是TTS实例运行的Context；另一个是初始化接口的实现，在实例的创建过程中，如果TTS引擎没有运行的话，则会初始化TTS引擎，并且在初始化完成后调用其第二个参数listener，在这里为函数public void onInit(int status)，如下所示：

```java
public void onInit(int status) { 
	//TTS Engine初始化完成 
	if(status == TextToSpeech.SUCCESS) {
		int result = mTts.setLanguage(Locale.US); 
		// 设置发音语 言 
		if(result == TextToSpeech.LANG_MISSING_DATA || 
				result == TextToSpeech.LANG_NOT_SUPPORTED){
				// 判断语言是否可用 
				Log.v(TAG, "Language is not available"); 
				speakBtn.setEnabled(false); 
		}else { 
			speakBtn.setEnabled(true); 
		} 
	} 
}  
```

在这个回调函数里，我们设置语言，然后就可以进行使用TTS引擎进行操作了。然后，在onCreate函数中设置EditText、Button和Spinner这3个控件的使用

```java
	inputText = (EditText)findViewById(R.id.inputText); 
	speakBtn = (Button)findViewById(R.id.speakBtn); 
	langSelect = (Spinner)findViewById(R.id.langSelect); 
	inputText.setText("I love you"); 
	speakBtn.setOnClickListener(new OnClickListener() {
	    public void onClick(View v) { 
		mTts.speak(inputText.getText().toString(), 
		TextToSpeech.QUEUE_ADD, null); 
		// 朗读输入框里的内容 
	 	} 
	 }); 
	 ArrayAdapter<String> adapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, languages); 
	 adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
	 langSelect.setAdapter(adapter); 
	 langSelect.setSelection(0); 
	 langSelect.setOnItemSelectedListener(new OnItemSelectedListener() { 
	 	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		int pos = langSelect.getSelectedItemPosition(); 
		//多余，根本没有用到result，所以所不用这样设置
        //int result = -1; 
		switch (pos) { 
			case 0: { 
				inputText.setText("I love you"); 
				// 设置发音语言 设置后报错，根本不需要这样设置，内置的语言会自动发音
				//result = mTts.setLanguage(Locale.US); 
			} 
			break; 
			case 1: { 
				inputText.setText("Je t'aime"); 
				//result = mTts.setLanguage(Locale.FRENCH); 
			} 
			break; 
			case 2: { 
				inputText.setText("Ich liebe dich"); 
				//result = mTts.setLanguage(Locale.GERMAN); 
			} 
			break; 
			case 3: { 
				inputText.setText("Ti amo"); 
				//result = mTts.setLanguage(Locale.ITALIAN); 
			} 
			break; 
			case 4: {
			    inputText.setText("Te quiero"); 
				//result = mTts.setLanguage(new Locale("spa", "ESP")); 
			} 
			break; 
			default: 
			break; 
			} 
	if(result == TextToSpeech.LANG_MISSING_DATA || 
			result == TextToSpeech.LANG_NOT_SUPPORTED){
		// 判断语言是否可用 
		Log.v(TAG, "Language is not available"); 
		speakBtn.setEnabled(false); 
	} else { 
		speakBtn.setEnabled(true); 
		} 
	} 
		public void onNothingSelected(AdapterView<?> arg) {
		} 
	});     
```

​	有了TextToSpeech实例后，就可以在Button的onClick事件里对文本进行语音合成并发音了，对应的API为：

```java
public int speak (String text, int queueMode, HashMap<String, String> params) 
```

这里需要三个参数，

- String text：要合成的文本
- int queueMode：使用TTS队列的方式，因为使用TTS合成语音时需要时间，TTS引擎就会把还没有轮到的部分放在其队列中排队，后来的内容有两种使用队列的方 式：`QUEUE_ADD`或者 `QUEUE_FLUSH`，QUEUE_ADD表示把当前需要合成的内容添加到TTS队列的后面，等前面的都完成了后再轮到他，QUEUE_FLUSH表示 清除队列中原有的内容，直接使TTS引擎对当前内容进行语音合成，一般来说适合比较紧急的情况使用，但因为在Android中可能不止你一个程序使用 TTS引擎，因此为了不破坏其他人的数据，建议使用QUEUE_ADD参数；
- HashMap<String,String> params:键值对表示的一个参数，可以使其为null。

**Spinner控件**中和TTS相关的部分是其item selected事件的处理，在选择不同的item时，为inputText设置不同语言“我爱你”对应的文本，并且为TTS实例设置语言。

```java
public int setLanguage(Local loc)
```

最后，创建一个TextToSpeech是需要占用资源的，因此我们要适时的释放这个资源：

```java
	@Override 
	protected void onPause() { 
		super.onPause(); 
		if(mTts != null) {
		//activity暂停时也停止 TTS  
		mTts.stop(); 
		} 
	} 
	@Override 
	protected void onDestroy() { 
		super.onDestroy(); 
		//释放TTS的资源 
		mTts.shutdown(); 
	}    
```






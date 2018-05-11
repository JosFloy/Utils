# Ftp上传和下载

Android中使用的FTP上传、下载，含有进度

代码部分主要分为三个文件：MainActivity,FTP,ProgressInputStream

1,MainActivity

```java
public class MainActivity extends Activity{
  private static final String TAG="MainActivity";
  
  public static final String FTP_CONNECT_SUCCESSS = "ftp连接成功"; 
  public static final String FTP_CONNECT_FAIL = "ftp连接失败"; 
  public static final String FTP_DISCONNECT_SUCCESS = "ftp断开连接"; 
  public static final String FTP_FILE_NOTEXISTS = "ftp上文件不存在"; 
    
  public static final String FTP_UPLOAD_SUCCESS = "ftp文件上传成功"; 
  public static final String FTP_UPLOAD_FAIL = "ftp文件上传失败"; 
  public static final String FTP_UPLOAD_LOADING = "ftp文件正在上传"; 
  
  public static final String FTP_DOWN_LOADING = "ftp文件正在下载"; 
  public static final String FTP_DOWN_SUCCESS = "ftp文件下载成功"; 
  public static final String FTP_DOWN_FAIL = "ftp文件下载失败"; 
    
  public static final String FTP_DELETEFILE_SUCCESS = "ftp文件删除成功"; 
  public static final String FTP_DELETEFILE_FAIL = "ftp文件删除失败"; 
  
  @Override
  protected void onCreate(Bundle savedInstanceState) { 
    super.onCreate(savedInstanceState); 
    setContentView(R.layout.main); 
      
    initView(); 
  } 
  
  private void initView(){
  	 //上传功能 
    //new FTP().uploadMultiFile为多文件上传 
    //new FTP().uploadSingleFile为单文件上传 
     Button buttonUpload = (Button) findViewById(R.id.button_upload);     
    buttonUpload.setOnClickListener(new OnClickListener() {       
      @Override
      public void onClick(View v) { 
  
          new Thread(new Runnable() {      
            @Override
            public void run() { 
                
              // 上传 
              File file = new File("/mnt/sdcard/ftpTest.docx"); 
              try { 
                //单文件上传 
                new FTP().uploadSingleFile(file, "/fff",new UploadProgressListener(){ 
                  @Override
                  public void onUploadProgress(String currentStep,long uploadSize,File file) { 
                    // TODO Auto-generated method stub 
                    Log.d(TAG, currentStep);                     
                    if(currentStep.equals(MainActivity.FTP_UPLOAD_SUCCESS)){ 
                      Log.d(TAG, "-----shanchuan--successful"); 
                    } else if(currentStep.equals(MainActivity.FTP_UPLOAD_LOADING)){ 
                      long fize = file.length(); 
                      float num = (float)uploadSize / (float)fize; 
                      int result = (int)(num * 100); 
                      Log.d(TAG, "-----shangchuan---"+result + "%"); 
                    } 
                  }               
                }); 
              } catch (IOException e) { 
                e.printStackTrace(); 
              } 
            } 
          }).start(); 
      } 
    });
    //下载功能 
    Button buttonDown = (Button)findViewById(R.id.button_down); 
    buttonDown.setOnClickListener(new View.OnClickListener() {      
      @Override
      public void onClick(View v) { 
          
        new Thread(new Runnable() {      
          @Override
          public void run() { 
              
            // 下载 
            try { 
                
              //单文件下载 
              new FTP().downloadSingleFile("/fff/ftpTest.docx","/mnt/sdcard/download/","ftpTest.docx",new DownLoadProgressListener(){ 
  
                @Override
                public void onDownLoadProgress(String currentStep, long downProcess, File file) { 
                  Log.d(TAG, currentStep);                     
                  if(currentStep.equals(MainActivity.FTP_DOWN_SUCCESS)){ 
                    Log.d(TAG, "-----xiazai--successful"); 
                  } else if(currentStep.equals(MainActivity.FTP_DOWN_LOADING)){ 
                    Log.d(TAG, "-----xiazai---"+downProcess + "%"); 
                  } 
                } 
                  
              });              
              
            } catch (Exception e) { 
              // TODO Auto-generated catch block 
              e.printStackTrace(); 
            } 
              
          } 
        }).start(); 
          
      } 
    }); 
     //删除功能 
    Button buttonDelete = (Button)findViewById(R.id.button_delete); 
    buttonDelete.setOnClickListener(new View.OnClickListener() {       
      @Override
      public void onClick(View v) { 
          
        new Thread(new Runnable() {      
          @Override
          public void run() { 
              
            // 删除 
            try { 
  
              new FTP().deleteSingleFile("/fff/ftpTest.docx",new DeleteFileProgressListener(){ 
  
                @Override
                public void onDeleteProgress(String currentStep) { 
                  Log.d(TAG, currentStep);                     
                  if(currentStep.equals(MainActivity.FTP_DELETEFILE_SUCCESS)){ 
                    Log.d(TAG, "-----shanchu--success"); 
                  } else if(currentStep.equals(MainActivity.FTP_DELETEFILE_FAIL)){ 
                    Log.d(TAG, "-----shanchu--fail"); 
                  } 
                } 
                  
              });              
              
            } catch (Exception e) { 
              // TODO Auto-generated catch block 
              e.printStackTrace(); 
            } 
              
          } 
        }).start(); 
          
      } 
    }); 
    
  }
}
```

2.FTP

```java
public class FTP { 
  /** 
   * 服务器名. 
   */
  private String hostName; 
  
  /** 
   * 端口号 
   */
  private int serverPort; 
  
  /** 
   * 用户名. 
   */
  private String userName; 
  
  /** 
   * 密码. 
   */
  private String password; 
  
  /** 
   * FTP连接. 
   */
  private FTPClient ftpClient; 
  
  public FTP() { 
    this.hostName = "192.168.1.101"; 
    this.serverPort = 21; 
    this.userName = "admin"; 
    this.password = "1234"; 
    this.ftpClient = new FTPClient(); 
  } 
  
  // -------------------------------------------------------文件上传方法------------------------------------------------ 
  
  /** 
   * 上传单个文件. 
   * 
   * @param localFile 
   *      本地文件 
   * @param remotePath 
   *      FTP目录 
   * @param listener 
   *      监听器 
   * @throws IOException 
   */
  public void uploadSingleFile(File singleFile, String remotePath, 
      UploadProgressListener listener) throws IOException { 
  
    // 上传之前初始化 
    this.uploadBeforeOperate(remotePath, listener); 
  
    boolean flag; 
    flag = uploadingSingle(singleFile, listener); 
    if (flag) { 
      listener.onUploadProgress(MainActivity.FTP_UPLOAD_SUCCESS, 0, 
          singleFile); 
    } else { 
      listener.onUploadProgress(MainActivity.FTP_UPLOAD_FAIL, 0, 
          singleFile); 
    } 
  
    // 上传完成之后关闭连接 
    this.uploadAfterOperate(listener); 
  } 
  
  /** 
   * 上传多个文件. 
   * 
   * @param localFile 
   *      本地文件 
   * @param remotePath 
   *      FTP目录 
   * @param listener 
   *      监听器 
   * @throws IOException 
   */
  public void uploadMultiFile(LinkedList<File> fileList, String remotePath, 
      UploadProgressListener listener) throws IOException { 
  
    // 上传之前初始化 
    this.uploadBeforeOperate(remotePath, listener); 
  
    boolean flag; 
  
    for (File singleFile : fileList) { 
      flag = uploadingSingle(singleFile, listener); 
      if (flag) { 
        listener.onUploadProgress(MainActivity.FTP_UPLOAD_SUCCESS, 0, 
            singleFile); 
      } else { 
        listener.onUploadProgress(MainActivity.FTP_UPLOAD_FAIL, 0, 
            singleFile); 
      } 
    } 
  
    // 上传完成之后关闭连接 
    this.uploadAfterOperate(listener); 
  } 
  
  /** 
   * 上传单个文件. 
   * 
   * @param localFile 
   *      本地文件 
   * @return true上传成功, false上传失败 
   * @throws IOException 
   */
  private boolean uploadingSingle(File localFile, 
      UploadProgressListener listener) throws IOException { 
    boolean flag = true; 
    // 不带进度的方式 
    // // 创建输入流 
    // InputStream inputStream = new FileInputStream(localFile); 
    // // 上传单个文件 
    // flag = ftpClient.storeFile(localFile.getName(), inputStream); 
    // // 关闭文件流 
    // inputStream.close(); 
  
    // 带有进度的方式 
    BufferedInputStream buffIn = new BufferedInputStream( 
        new FileInputStream(localFile)); 
    ProgressInputStream progressInput = new ProgressInputStream(buffIn, 
        listener, localFile); 
    flag = ftpClient.storeFile(localFile.getName(), progressInput); 
    buffIn.close(); 
  
    return flag; 
  } 
    
  /** 
   * 上传文件之前初始化相关参数 
   * 
   * @param remotePath 
   *      FTP目录 
   * @param listener 
   *      监听器 
   * @throws IOException 
   */
  private void uploadBeforeOperate(String remotePath, 
      UploadProgressListener listener) throws IOException { 
  
    // 打开FTP服务 
    try { 
      this.openConnect(); 
      listener.onUploadProgress(MainActivity.FTP_CONNECT_SUCCESSS, 0, 
          null); 
    } catch (IOException e1) { 
      e1.printStackTrace(); 
      listener.onUploadProgress(MainActivity.FTP_CONNECT_FAIL, 0, null); 
      return; 
    } 
  
    // 设置模式 
    ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE); 
    // FTP下创建文件夹 
    ftpClient.makeDirectory(remotePath); 
    // 改变FTP目录 
    ftpClient.changeWorkingDirectory(remotePath); 
    // 上传单个文件 
  
  } 
  
  /** 
   * 上传完成之后关闭连接 
   * 
   * @param listener 
   * @throws IOException 
   */
  private void uploadAfterOperate(UploadProgressListener listener) 
      throws IOException { 
    this.closeConnect(); 
    listener.onUploadProgress(MainActivity.FTP_DISCONNECT_SUCCESS, 0, null); 
  } 
  
  // -------------------------------------------------------文件下载方法------------------------------------------------ 
  
  /** 
   * 下载单个文件，可实现断点下载. 
   * 
   * @param serverPath 
   *      Ftp目录及文件路径 
   * @param localPath 
   *      本地目录 
   * @param fileName    
   *      下载之后的文件名称 
   * @param listener 
   *      监听器 
   * @throws IOException 
   */
  public void downloadSingleFile(String serverPath, String localPath, String fileName, DownLoadProgressListener listener) 
      throws Exception { 
  
    // 打开FTP服务 
    try { 
      this.openConnect(); 
      listener.onDownLoadProgress(MainActivity.FTP_CONNECT_SUCCESSS, 0, null); 
    } catch (IOException e1) { 
      e1.printStackTrace(); 
      listener.onDownLoadProgress(MainActivity.FTP_CONNECT_FAIL, 0, null); 
      return; 
    } 
  
    // 先判断服务器文件是否存在 
    FTPFile[] files = ftpClient.listFiles(serverPath); 
    if (files.length == 0) { 
      listener.onDownLoadProgress(MainActivity.FTP_FILE_NOTEXISTS, 0, null); 
      return; 
    } 
  
    //创建本地文件夹 
    File mkFile = new File(localPath); 
    if (!mkFile.exists()) { 
      mkFile.mkdirs(); 
    } 
  
    localPath = localPath + fileName; 
    // 接着判断下载的文件是否能断点下载 
    long serverSize = files[0].getSize(); // 获取远程文件的长度 
    File localFile = new File(localPath); 
    long localSize = 0; 
    if (localFile.exists()) { 
      localSize = localFile.length(); // 如果本地文件存在，获取本地文件的长度 
      if (localSize >= serverSize) { 
        File file = new File(localPath); 
        file.delete(); 
      } 
    } 
      
    // 进度 
    long step = serverSize / 100; 
    long process = 0; 
    long currentSize = 0; 
    // 开始准备下载文件 
    OutputStream out = new FileOutputStream(localFile, true); 
    ftpClient.setRestartOffset(localSize); 
    InputStream input = ftpClient.retrieveFileStream(serverPath); 
    byte[] b = new byte[1024]; 
    int length = 0; 
    while ((length = input.read(b)) != -1) { 
      out.write(b, 0, length); 
      currentSize = currentSize + length; 
      if (currentSize / step != process) { 
        process = currentSize / step; 
        if (process % 5 == 0) { //每隔%5的进度返回一次 
          listener.onDownLoadProgress(MainActivity.FTP_DOWN_LOADING, process, null); 
        } 
      } 
    } 
    out.flush(); 
    out.close(); 
    input.close(); 
      
    // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉 
    if (ftpClient.completePendingCommand()) { 
      listener.onDownLoadProgress(MainActivity.FTP_DOWN_SUCCESS, 0, new File(localPath)); 
    } else { 
      listener.onDownLoadProgress(MainActivity.FTP_DOWN_FAIL, 0, null); 
    } 
  
    // 下载完成之后关闭连接 
    this.closeConnect(); 
    listener.onDownLoadProgress(MainActivity.FTP_DISCONNECT_SUCCESS, 0, null); 
  
    return; 
  } 
  
  // -------------------------------------------------------文件删除方法------------------------------------------------ 
  
  /** 
   * 删除Ftp下的文件. 
   * 
   * @param serverPath 
   *      Ftp目录及文件路径 
   * @param listener 
   *      监听器 
   * @throws IOException 
   */
  public void deleteSingleFile(String serverPath, DeleteFileProgressListener listener) 
      throws Exception { 
  
    // 打开FTP服务 
    try { 
      this.openConnect(); 
      listener.onDeleteProgress(MainActivity.FTP_CONNECT_SUCCESSS); 
    } catch (IOException e1) { 
      e1.printStackTrace(); 
      listener.onDeleteProgress(MainActivity.FTP_CONNECT_FAIL); 
      return; 
    } 
  
    // 先判断服务器文件是否存在 
    FTPFile[] files = ftpClient.listFiles(serverPath); 
    if (files.length == 0) { 
      listener.onDeleteProgress(MainActivity.FTP_FILE_NOTEXISTS); 
      return; 
    } 
      
    //进行删除操作 
    boolean flag = true; 
    flag = ftpClient.deleteFile(serverPath); 
    if (flag) { 
      listener.onDeleteProgress(MainActivity.FTP_DELETEFILE_SUCCESS); 
    } else { 
      listener.onDeleteProgress(MainActivity.FTP_DELETEFILE_FAIL); 
    } 
      
    // 删除完成之后关闭连接 
    this.closeConnect(); 
    listener.onDeleteProgress(MainActivity.FTP_DISCONNECT_SUCCESS); 
      
    return; 
  } 
  
  // -------------------------------------------------------打开关闭连接------------------------------------------------ 
  
  /** 
   * 打开FTP服务. 
   * 
   * @throws IOException 
   */
  public void openConnect() throws IOException { 
    // 中文转码 
    ftpClient.setControlEncoding("UTF-8"); 
    int reply; // 服务器响应值 
    // 连接至服务器 
    ftpClient.connect(hostName, serverPort); 
    // 获取响应值 
    reply = ftpClient.getReplyCode(); 
    if (!FTPReply.isPositiveCompletion(reply)) { 
      // 断开连接 
      ftpClient.disconnect(); 
      throw new IOException("connect fail: " + reply); 
    } 
    // 登录到服务器 
    ftpClient.login(userName, password); 
    // 获取响应值 
    reply = ftpClient.getReplyCode(); 
    if (!FTPReply.isPositiveCompletion(reply)) { 
      // 断开连接 
      ftpClient.disconnect(); 
      throw new IOException("connect fail: " + reply); 
    } else { 
      // 获取登录信息 
      FTPClientConfig config = new FTPClientConfig(ftpClient 
          .getSystemType().split(" ")[0]); 
      config.setServerLanguageCode("zh"); 
      ftpClient.configure(config); 
      // 使用被动模式设为默认 
      ftpClient.enterLocalPassiveMode(); 
      // 二进制文件支持 
      ftpClient 
          .setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE); 
    } 
  } 
  
  /** 
   * 关闭FTP服务. 
   * 
   * @throws IOException 
   */
  public void closeConnect() throws IOException { 
    if (ftpClient != null) { 
      // 退出FTP 
      ftpClient.logout(); 
      // 断开连接 
      ftpClient.disconnect(); 
    } 
  } 
  
  // ---------------------------------------------------上传、下载、删除监听--------------------------------------------- 
    
  /* 
   * 上传进度监听 
   */ 
  public interface UploadProgressListener { 
    public void onUploadProgress(String currentStep, long uploadSize, File file); 
  } 
  
  /* 
   * 下载进度监听 
   */ 
  public interface DownLoadProgressListener { 
    public void onDownLoadProgress(String currentStep, long downProcess, File file); 
  } 
  
  /* 
   * 文件删除监听 
   */
  public interface DeleteFileProgressListener { 
    public void onDeleteProgress(String currentStep); 
  } 
  
} 
```

3.ProgressInputStream

```java
public class ProgressInputStream extends InputStream { 
  
  private static final int TEN_KILOBYTES = 1024 * 10; //每上传10K返回一次 
  
  private InputStream inputStream; 
  
  private long progress; 
  private long lastUpdate; 
  
  private boolean closed; 
    
  private UploadProgressListener listener; 
  private File localFile; 
  
  public ProgressInputStream(InputStream inputStream,UploadProgressListener listener,File localFile) { 
    this.inputStream = inputStream; 
    this.progress = 0; 
    this.lastUpdate = 0; 
    this.listener = listener; 
    this.localFile = localFile; 
      
    this.closed = false; 
  } 
  
  @Override
  public int read() throws IOException { 
    int count = inputStream.read(); 
    return incrementCounterAndUpdateDisplay(count); 
  } 
  
  @Override
  public int read(byte[] b, int off, int len) throws IOException { 
    int count = inputStream.read(b, off, len); 
    return incrementCounterAndUpdateDisplay(count); 
  } 
  
  @Override
  public void close() throws IOException { 
    super.close(); 
    if (closed) 
      throw new IOException("already closed"); 
    closed = true; 
  } 
  
  private int incrementCounterAndUpdateDisplay(int count) { 
    if (count > 0) 
      progress += count; 
    lastUpdate = maybeUpdateDisplay(progress, lastUpdate); 
    return count; 
  } 
  
  private long maybeUpdateDisplay(long progress, long lastUpdate) { 
    if (progress - lastUpdate > TEN_KILOBYTES) { 
      lastUpdate = progress; 
      this.listener.onUploadProgress(MainActivity.FTP_UPLOAD_LOADING, progress, this.localFile); 
    } 
    return lastUpdate; 
  }  
} 
```
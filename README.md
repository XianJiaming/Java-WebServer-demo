# 目录

- 要求

- 开发工具&环境

- 设计图

- 主要代码

- 实验结果

  

## 要求

**Requirements:**

1. The web server should response the http request from user agent correctly.

   实现结果：Web Server可以正常响应http请求，将请求报文输出在控制台，能够正确发回http响应报文与请求文件。

2. The web server should be capable to serve at least 2 http user agent simultaneously.

   实现结果：采用Java多线程，可以同时响应多个用户的访问

3. Users can set up the working port of this web server.

   实现结果：用户可以手动设置Web Server

### 开发工具&环境

- 操作系统：Window 10 家庭版 64Bit
- 处理器：Intel Core i7-7700HQ CPu @ 2.80GHz 2.80GHz
- 内存(RAM)：8.00GB
- 编辑器：IntellJ IDEA 2019.1.3 x64
- 开发语言：java
- Java SE 运行环境：1.8.0_221-b11
- Java JDK：1.8.0_221
- JVM 虚拟机：HotSpot 64-Bit Server VM(25.211-b11,mixed mode)

### 设计图

|      |                                                              |
| ---- | ------------------------------------------------------------ |
|      | ![img](file:///C:\Users\hasee\AppData\Local\Temp\ksohtml12460\wps1.jpg) |

### 主要代码

***\*server = new ServerSocket(port)\****;//以端口号作为参数，建立Server套接字的对象

***\*client = server.accept();\****//server监听等待连接，一旦有连接便创建Socket实例，client引用到该对象
***\*new communicateThread(client).start();\****//启动通信线程

***\*Private String getRequest(InputStream in):获取请求报文\****

调用了InputStream类的read(byte[],off,len)方法，将请求报文写入byte[] buffer。

创建了StringBuffer类对象，将buffer中的字符遍历并写入StringBuffer。使用StringBuffer既可以连续写入，获得更高的效率，同时可以保证线程安全。

最后调用toString()方法，以字符串形式返回请求报文

***\*private String getResourcePath(String s)：获取请求文件的名称\****

![img](file:///C:\Users\hasee\AppData\Local\Temp\ksohtml12460\wps2.png) 

根据HTTP请求报文的格式，可以看到，第一行为请求方法+请求URL+HTTP协议版本(以空格分隔)，故第一行第二个位置为请求URL。

在第一个空格和第二个空格之间(去除'/')，提取出请求文件的名称，同时默认加载主页

***\*private void sendResponse(String fileName,OutputStream out)：把响应报文以及请求的文件写进输出流\****

创建File类文件，参数为String fileName

调用OutputStream类write()方法，将响应报文头写进输出流。

若当文件存在，创建FileInputStream类对象，调用read()方法，将file写进buf中，再通过write()方法将buf写进输出流。

若当文件不存在，返回404 Not Found报文。

最后关闭FileInputStream对象。

***\*public void run()：重写Thread父类的run()方法\****

运行通信线程。按照流程，先获取http请求报文，获得请求的资源路径，再发送http响应报文，最后关闭IO流以及套接字client。依次调用getRequest()、getResourcePath()、sendResponce()、close()。

这里要加锁的原因：InputStream类read方法是阻塞方法，并且Socket的输入流没有明显的结束语句，所以第一个线程直到关闭IO流之前，后续线程会被阻塞。但是这时如果第二个线程阻塞在read语句上，由于访问的是InputStream类同一对象，第一个线程已经读到了末尾，第二个线程只能读到null，导致报文被吞。加上锁之后，保证从获取请求报文到返回响应报文这一系列操作具有原子性，针对小文件具有可行性；如果是较大的文件，可能导致访问过慢。更好的方法可能是使用NIO类。

### 实验结果

先运行java程序，打开浏览器在浏览器上输入localhost:12345/index.html

浏览器可以正确返回主页页面

![img](file:///C:\Users\hasee\AppData\Local\Temp\ksohtml12460\wps3.jpg) 

 控制台上打印出http请求报文

|      |                                                              |
| ---- | ------------------------------------------------------------ |
|      | ![img](file:///C:\Users\hasee\AppData\Local\Temp\ksohtml12460\wps4.jpg) |

 如果输入localhost:12345/，会默认访问主页

![img](file:///C:\Users\hasee\AppData\Local\Temp\ksohtml12460\wps5.jpg) 

如果输入不存在的地址 如localhost:12345/inhtml，会返回404 Not Found响应报文，同时显示错误页面（某些浏览器会请求favicon.ico，即地址栏左侧小图标，在这里因没有图标文件，所以应当返回404 Not Found）

![img](file:///C:\Users\hasee\AppData\Local\Temp\ksohtml12460\wps6.jpg) 

![img](file:///C:\Users\hasee\AppData\Local\Temp\ksohtml12460\wps7.jpg) 

可以实现多个用户同时访问Server，实现并发

![img](file:///C:\Users\hasee\AppData\Local\Temp\ksohtml12460\wps8.jpg) 
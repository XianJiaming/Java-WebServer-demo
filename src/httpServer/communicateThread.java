package httpServer;

import java.io.*;
import java.net.Socket;

/*
 * @author Xianjiaming
 * @StudentID 2017141491010
 * @version v20191123
 * */
public class communicateThread extends Thread {
    private static Object lock = new Object(); //锁
    private Socket client; //客户端的套接字
    //构造函数
    public communicateThread(Socket s){
        client = s;
    }
    //获取请求报文
    private String getRequest(InputStream in){
        StringBuffer request = new StringBuffer();
        byte[] buffer = new byte[1024];
        //将用户发送的Http请求报文写进buffer，返回值是buffer的有效长度
        int len;
        try{
            len = in.read(buffer);
        }
        catch(IOException e){
            e.printStackTrace();
            len = -1;
        }
        for(int i = 0; i < len; i++){
            request.append((char)buffer[i]);
        }
        System.out.print(request.toString());
        return request.toString();
    }
    //获取请求文件名称
    private String getResourcePath(String s){
        //结合HTTP请求报文来看，第一行为请求方法+请求URL+HTTP协议版本(以空格分隔)，故第一行第二个位置为请求URL
        int index1,index2;
        String resourcePath = null;
        index1 = s.indexOf(" ");
        if(index1!=-1){
            index2 = s.indexOf(" ",index1+1);
            if(index2>index1){
                //提取出请求文件的名称，在第一个空格和第二个空格之间(去除'/')
                resourcePath = s.substring(index1+2,index2);
                //默认加载主页
                if(resourcePath.equals("")){
                    resourcePath = "index.html";
                }
            }
        }
        return resourcePath;
    }
    //把响应报文以及请求的文件写进输出流
    private void sendResponse(String fileName,OutputStream out) throws IOException {
        File file = null;
        FileInputStream fis = null;
        byte buf[] = new byte[1024];
        try {
            file = new File(fileName);
            if (file.exists()) {
                fis = new FileInputStream(file);
                //将响应报文头写进输出流
                out.write("HTTP/1.1 200 OK\n".getBytes());
                out.write("Content-Type: text/html; charset=UTF-8\n\n".getBytes());
                //将请求的文件写进输出流
                int readLength = fis.read(buf);
                if(readLength > 0){
                    out.write(buf,0,readLength);
                }
            }
            else {
                System.out.println("404 Not Found");
                System.out.println("请检查路径是否正确!\n");
                String errMsg = "HTTP/1.1 404 Not Found\r\n"+
                        "Content-Type:text/html\r\n"+
                        "Content-Length:23\r\n"+
                        "\r\n"+
                        "<h1>File Not Found</h1>";
                out.write(errMsg.getBytes());
            }
        }
        catch(Exception e){
            e.getMessage();
        }
        finally {
            out.flush();
            out.close();
            //关闭FileInputStream
            if(fis!=null) {
                fis.close();
            }
        }
    }
    //运行通信线程
    public void run(){
        InputStream in = null;
        OutputStream out = null;
        try {
            //read方法是阻塞方法，后续线程会被阻塞，而Socket的输入流没有明显的结束语句，
            //直到输出流被关闭，后续进程才能继续通行
            synchronized (lock) {
                in = client.getInputStream();
                out = client.getOutputStream();
                //获取Http请求报文
                String request = getRequest(in);
                //获得请求的资源路径
                String fileName = getResourcePath(request);
                //发送Http响应报文
                sendResponse(fileName, out);
                //关闭输入输出流
                out.flush();
                out.close();
                in.close();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            try{
                client.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

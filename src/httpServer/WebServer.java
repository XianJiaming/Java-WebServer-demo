package httpServer;

import java.net.ServerSocket;
import java.net.Socket;

/*
 * @author Xianjiaming
 * @StudentID 2017141491010
 * @version v20191123
 * */
public class WebServer {
    //用户可以自己设定Server的端口号
    private static final int port = 12345;
    public static void main(String[] args){
        //Server的套接字
        ServerSocket server;
        //Client的套接字
        Socket client;
        try{
            //以端口号作为参数，建立Server套接字的对象
            server = new ServerSocket(port);
            System.out.println("The WebServer is listening on port "+server.getLocalPort());
            while(true){
                //server监听等待连接，一旦有连接便创建Socket实例，client引用到该对象
                client = server.accept();
                //启动通信线程
                new communicateThread(client).start();
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}

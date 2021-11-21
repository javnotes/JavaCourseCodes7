package nio_1;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @description: 单线程的socket程序，一次请求要新建、销毁一个连接
 * @author: luffyhub
 * @date: 2021/11/9
 **/

public class HttpServer01 {
    public static void main(String[] args) throws IOException {
        // 服务端监听端口
        final int port = 8801;
        // 服务端
        ServerSocket serverSocket = new ServerSocket(port);

        while (true) {
            // accept()监听要与此Socket建立的连接并接受它。该方法阻塞，直到建立连接;创建一个新的Socket并返回
            Socket socket = null;
            try {
                System.out.println("[8801]服务器监听端口中...");
                socket = serverSocket.accept();
                System.out.println("[8801]连接建立了...");

            } catch (IOException e) {
                e.printStackTrace();
            }
            service(socket);
            System.out.println("[8801]服务端响应完毕");
            System.out.println("[8801]--------------------------");
        }
    }

    private static void service(Socket socket) {
        Writer out;
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            printWriter.println("HTTP/1.1 200 OK");
            printWriter.println("Content-Type:text/html;charset=utf-8");

            String body = "hello,nio1";
            printWriter.println("Content-Length:" + body.getBytes().length);
            printWriter.println();
            printWriter.write(body);
        } catch (IOException e) {
            e.printStackTrace();
        }

        close(printWriter);
        close(socket);
    }

    // 关闭资源
    private static void close(Closeable res) {
        if (null != res) {
            try {
                res.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

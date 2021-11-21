package nio_1;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @description: 每个请求一个线程
 * @author: luffyhub
 * @date: 2021/11/9
 **/
public class HttpServer02 {
    public static void main(String[] args) throws IOException {
        final int port = 8802;
        ServerSocket serverSocket = new ServerSocket(port);

        while (true) {
            System.out.println("[8802]服务器监听端口中...");
            final Socket socket = serverSocket.accept();
            new Thread(() -> {
                System.out.println("[8802]服务器开始响应");
                service(socket);
                System.out.println("[8802]服务器完成响应");
                System.out.println("[8802]-----------------");
            }).start();
        }
    }

    private static void service(Socket socket) {
        try {
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            printWriter.println("HTTP/1.1 200 OK");
            printWriter.println("Content-Type:text/html;charset=utf-8");
            String body = "hello,nio2";
            printWriter.println("Content-Length:" + body.getBytes().length);
            printWriter.println();
            printWriter.write(body);

            close(printWriter);
            close(socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

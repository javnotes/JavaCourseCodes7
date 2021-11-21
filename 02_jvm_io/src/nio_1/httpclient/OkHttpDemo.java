package nio_1.httpclient;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

import java.io.IOException;

/**
 * @description: 使用 OkHttp 访问 http://localhost:8801
 * @author: luf
 * @create: 2021-11-21 14:03
 **/
public class OkHttpDemo {
    public static void main(String[] args) {
        String url = "http://localhost:8801";

        OkHttpClient okHttpClient = new OkHttpClient();
        // 创建一个请求
        Request request = new Request.Builder().url(url).build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                // 获取返回的数据，可通过response.body().string()获取，默认返回的是utf-8格式；
                // string() 适用于获取小数据信息，如果返回的数据超过1M，建议使用stream()获取返回的数据，因为string() 方法会将整个文档加载到内存中。
                System.out.println(response.body().string());
            } else {
                System.out.println("response fail");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



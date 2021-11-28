package concurrency;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @author: luf
 * @create: 2021-11-28 22:46
 **/
public class Demo2Join {
    public static void main(String[] args) throws InterruptedException {
        final Demo2Join demo = new Demo2Join();

        AtomicInteger value = new AtomicInteger();
        long start = System.currentTimeMillis();

        // 在这里创建一个线程或线程池，
        // 异步执行 下面方法
        Thread thread = new Thread(() -> {
            value.set(demo.sum());
        });
        thread.start();
        thread.join();

        // 确保  拿到result 并输出
        int result = value.get();
        System.out.println("异步计算结果为：" + result);
        System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");
        // 然后退出main线程
    }

    private int sum() {
        return fibo(36);
    }

    private int fibo(int a) {
        if (a < 2) {
            return 1;
        }
        return fibo(a - 1) + fibo(a - 2);
    }
}

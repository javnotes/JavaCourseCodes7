package concurrency;

import java.io.File;
import java.util.concurrent.CountDownLatch;

/**
 * @description: 阻塞主线程，N 个子线程满足条件时，主线程才继续。
 * 即：子线程赋值后，主线程才继续
 * @author: luf
 * @date: 2021/11/29
 **/
public class Demo6CountdownLatch {
    private Integer value = null;

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        ;
        Demo6CountdownLatch demo = new Demo6CountdownLatch();
        long start = System.currentTimeMillis();

        // 在这里创建一个线程或线程池，
        // 异步执行 下面方法
        Thread thread = new Thread(() -> {
            demo.setValue();
            latch.countDown();
        });
        thread.start();

        latch.await();
        int result = demo.value; //这是得到的返回值

        // 确保  拿到result 并输出
        System.out.println("异步计算结果为：" + result);
        System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");
        // 然后退出main线程
    }

    private void setValue() {
        value = sum();
    }

    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if (a < 2) {
            return 1;
        }
        return fibo(a - 1) + fibo(a - 2);
    }
}

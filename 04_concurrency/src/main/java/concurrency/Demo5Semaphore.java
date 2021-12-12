package concurrency;

import java.util.concurrent.Semaphore;

/**
 * @description:
 * Semaphore:可以用来控制同时访问特定资源的线程数量，通过协调各个线程，以保证合理的使用资源。
 * 通常用于那些资源有明确访问数量限制的场景，常用于限流
 * Semaphore 先在线程中使用，后在主线程中使用
 * @author: luf
 * @create: 2021-11-28 23:34
 **/
public class Demo5Semaphore {
    private volatile Integer value = null;
    private Semaphore semaphore = new Semaphore(1);

    public static void main(String[] args) throws InterruptedException {

        final Demo5Semaphore demo = new Demo5Semaphore();
        long start = System.currentTimeMillis();

        // 在这里创建一个线程或线程池，
        // 异步执行 下面方法
        Thread thread = new Thread(() -> {
            try {
                demo.setValue();
                System.out.println("set");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();

        // TODO:问题是如何让线程先于此处下一行代码执行
        Thread.sleep(10);

        int result = demo.getValue(); //这是得到的返回值

        // 确保  拿到result 并输出
        System.out.println("异步计算结果为：" + result);
        System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");
        // 然后退出main线程
    }

    private void setValue() throws InterruptedException {
        semaphore.acquire();
        value = sum();
        semaphore.release();
        return;
    }

    private int getValue() throws InterruptedException {
        int result;
        semaphore.acquire();
        result = this.value;
        semaphore.release();
        return result;
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

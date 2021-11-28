package concurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @description:
 * @author: luf
 * @create: 2021-11-28 23:22
 **/
public class Demo4LockAndCondition {
    private volatile Integer value = null;
    private Lock lock = new ReentrantLock();
    private Condition newCondition = lock.newCondition();

    public static void main(String[] args) throws InterruptedException {
        final Demo4LockAndCondition demo = new Demo4LockAndCondition();
        long start = System.currentTimeMillis();

        // 在这里创建一个线程或线程池，
        // 异步执行 下面方法
        Runnable target;
        Thread thread = new Thread(()->{
            demo.sum();
        });
        thread.start();

        int result = demo.getValue(); //这是得到的返回值

        // 确保  拿到result 并输出
        System.out.println("异步计算结果为：" + result);

        System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");

        // 然后退出main线程
    }

    private void sum() {
        lock.lock();
        try {
            value = fibo(36);
            newCondition.signal();
        } finally {
            lock.unlock();
        }
    }

    private int fibo(int a) {
        if (a < 2) {
            return 1;
        }
        return fibo(a - 1) + fibo(a - 2);
    }

    private int getValue() throws InterruptedException {
        lock.lock();
        try {
            while (value == null) {
                newCondition.await();
            }
        } finally {
            lock.unlock();
        }
        return value;
    }
}

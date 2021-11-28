package concurrency;

/**
 * @description:
 * @author: luf
 * @create: 2021-11-28 22:58
 **/
public class Demo3WaitAndNotify {
    private volatile Integer value = null;

    public static void main(String[] args) throws InterruptedException {
        final Demo3WaitAndNotify demo = new Demo3WaitAndNotify();

        long start = System.currentTimeMillis();

        // 在这里创建一个线程或线程池，
        // 异步执行 下面方法
        Thread thread = new Thread(() -> {
            demo.sum();
        });
        thread.start();

        // getValue调用wait()
        int result = demo.getValue(); //这是得到的返回值

        // 确保  拿到result 并输出
        System.out.println("异步计算结果为：" + result);
        System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");
        // 然后退出main线程
    }

    private synchronized void sum() {
        value = fibo(36); // 计算出结果
        notifyAll();
        return;
    }

    private int fibo(int a) {
        if (a < 2) {
            return 1;
        }
        return fibo(a - 1) + fibo(a - 2);
    }

    private synchronized int getValue() throws InterruptedException {
        while (true) {
            if (value == null) {
                wait();
            } else {
                break;
            }
        }
        return value;
    }
}

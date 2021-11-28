package concurrency;

/**
 * @description: 循环判断是否得到结果
 * @author: luf
 * @create: 2021-11-28 22:31
 **/
public class Demo1UseWhile {
    // 注意：没有volatile，程序不会终止
    private volatile Integer value = null;

    public static void main(String[] args) {
        final Demo1UseWhile demo = new Demo1UseWhile();

        long start = System.currentTimeMillis();

        // 在这里创建一个线程或线程池，
        // 异步执行 下面方法
        Thread thread = new Thread(()->{
            demo.setValue();
        });
        thread.start();

        // 任务是setValue
        // 主线程中有while循环等待
        int result = demo.getValue(); //这是得到的返回值

        // 确保  拿到result 并输出
        System.out.println("异步计算结果为：" + result);
        System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");

        // 然后退出main线程
    }

    private void setValue() {
        value = fibo(36);
        return;
    }

    private int fibo(int a) {
        if (a < 2) {
            return 1;
        }
        return fibo(a - 1) + fibo(a - 2);
    }

    private int getValue() {
        while (value == null) {
            // 空循环
        }
        // 得到结果
        return value;
    }
}

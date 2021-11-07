import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @description: 第一周作业 2.
 * 定义一个 Classloader，加载一个 Hello.xlass 文件，执行 hello 方法，此文件内容是一个 Hello.class 文件所有字节（x=255-x）处理后的文件。
 * 通过自定义ClassLoader，来加载指定class文件内容：
 * 1. 编写一个类继承自ClassLoader抽象类；
 * 2. 复写它的findClass()方法；
 * 3. 在findClass()方法中调用defineClass()；
 * @author: luf
 * @create: 2021-11-07 19:41
 **/
public class MyClassLoader extends ClassLoader {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        final String className = "Hello";
        final String methodMame = "hello";

        // 创建类加载器
        MyClassLoader myClassLoader = new MyClassLoader();

        // 加载指定类
        // 向上委托父加载器不会加载成功（自定义文件），则通过findClass(String)查找，故需重写方法findClass(String)
        Class<?> clazz = myClassLoader.loadClass(className);

        // 查看类中所有声明的方法
        for (Method m : clazz.getDeclaredMethods()) {
            // java.lang.Class.getSimpleName 返回底层类的简单名称
            System.out.println("查看类中所有声明的方法：");
            System.out.println(clazz.getSimpleName() + "." + m.getName());
        }
        System.out.println();

        // 创建实例
        Object instance = clazz.getDeclaredConstructor().newInstance();
        // 调用实例方法
        Method method = clazz.getMethod(methodMame);
        method.invoke(instance);
    }

    /**
     * 查找类
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
//        // 如果支持包名, 则需要进行路径转换
//        String resourcePath = name.replace(".", "/");
        // 文件后缀
        final String suffix = ".xlass";
        // 获取输入字节流
        // java.lang.ClassLoader.getResourceAsStream 返回用于读取指定资源的输入流
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(name + suffix);

        try {
            //在处理文件输入流时，通过调用available()方法来获取还有多少字节可以读取，根据该数值创建固定大小的byte数组，从而读取输入流的信息
            int length = inputStream.available();
            byte[] byteArray = new byte[length];
            inputStream.read(byteArray);

            // 解码
            byte[] classBytes = decode(byteArray);

            // 通知底层定义该类
            // defineClass()将class二进制内容转换成Class对象
            return defineClass(name, classBytes, 0, classBytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException(name, e);
        } finally {
            close(inputStream);
        }
    }

    /**
     * 解码
     */
    private static byte[] decode(byte[] byteArray) {
        int length = byteArray.length;
        byte[] targetArray = new byte[length];
        for (int i = 0; i < length; i++) {
            // 因为该文件所有字节经过（x=255-x）处理
            targetArray[i] = (byte) (255 - byteArray[i]);
        }
        return targetArray;
    }

    /**
     * 关闭资源，均实现了接口java.io.Closeable，多态性
     */
    private static void close(Closeable res) {
        if (null != res) { // 常量写在左边
            try {
                res.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
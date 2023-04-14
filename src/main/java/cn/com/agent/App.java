package cn.com.agent;

/**
 * @Author: wenguoxing
 * @Date: 2023/4/13 16:31
 * @Version 1.0
 */
public class App {
    public static void main(String[] args) throws InterruptedException {
        while (true) {
            System.out.println(getGreeting());
            Thread.sleep(1000L);
        }
    }

    public static String getGreeting() {
        return "hello world";
    }
}
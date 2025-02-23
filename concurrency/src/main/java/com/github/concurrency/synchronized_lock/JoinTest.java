package com.github.concurrency.synchronized_lock;

/**
 * 测试join()函数
 *
 * @author 杨中肖
 * @date 2025/02/12
 */
public class JoinTest {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.github.concurrency.synchronized_lock;

import org.openjdk.jol.info.ClassLayout;

/**
 * 轻量级锁
 *
 * @author 杨中肖
 * @date 2025/02/12
 */
public class ThinLock {
    public static void main(String[] args) {
        thinLock();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        thinLockUp();
    }

    /**
     * 轻量级锁
     */
    private static void thinLock() {
        Object lock = new Object();
        // 调用hashCode，无法使用偏向锁
        lock.hashCode();
        new Thread(() -> {
            synchronized (lock) {
                System.out.println("===== T1 Locked State =====");
                System.out.println(ClassLayout.parseInstance(lock).toPrintable());
            }
        }).start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        new Thread(() -> {
            synchronized (lock) {
                System.out.println("===== T2 Locked State =====");
                System.out.println(ClassLayout.parseInstance(lock).toPrintable());
            }
        }).start();
    }

    /**
     * 轻量级锁升级
     */
    public static void thinLockUp() {
        Object lock = new Object();
        lock.hashCode();
        new Thread(() -> {
            synchronized (lock) {
                System.out.println("===== T1 Locked State =====");
                System.out.println(ClassLayout.parseInstance(lock).toPrintable());
            }
        }).start();

        new Thread(() -> {
            synchronized (lock) {
                System.out.println("===== T2 Locked State =====");
                System.out.println(ClassLayout.parseInstance(lock).toPrintable());
            }
        }).start();
    }
}

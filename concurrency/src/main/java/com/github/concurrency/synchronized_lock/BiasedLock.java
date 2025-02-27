package com.github.concurrency.synchronized_lock;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

import java.util.concurrent.TimeUnit;

/**
 * 偏向锁
 * 关闭偏向锁延迟：-XX:BiasedLockingStartupDelay=0
 *
 * @author 杨中肖
 * @date 2025/02/27
 */
public class BiasedLock {
    public static void main(String[] args) {

        MyLock lock = new MyLock();
        System.out.println();
        new Thread(() -> {
            synchronized (lock) {
                System.out.println("===== T1 Locked State =====");
                System.out.println(ClassLayout.parseInstance(lock).toPrintable());
                System.out.println(VM.current().addressOf(Thread.currentThread()));
            }
        }).start();

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        new Thread(() -> {
            synchronized (lock) {
                System.out.println("===== T2 Locked State =====");
                System.out.println(ClassLayout.parseInstance(lock).toPrintable());
                System.out.println(VM.current().addressOf(Thread.currentThread()));
            }
        }).start();
    }

    /**
     * 锁
     * com.github.concurrency.synchronized_lock.BiasedLock$MyLock
     *
     * @author 杨中肖
     * @date 2025/02/27
     */
    public static class MyLock {
    }
}

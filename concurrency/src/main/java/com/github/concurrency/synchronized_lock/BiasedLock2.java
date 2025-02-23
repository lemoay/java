package com.github.concurrency.synchronized_lock;

import org.openjdk.jol.info.ClassLayout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BiasedLock2 {
    public static void main(String[] args) {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 创建锁
        Object lock = new Object();

        lock.hashCode();

        ExecutorService pool = Executors.newFixedThreadPool(5);

        pool.submit(() -> lock(lock));

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        pool.submit(() -> lock(lock));

        try {
            Thread.sleep(10000000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void lock(Object lock) {
        synchronized (lock) {
            System.out.println("===== T2 Locked State =====");
            System.out.println(ClassLayout.parseInstance(lock).toPrintable());
        }
    }
}

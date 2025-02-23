package com.github.concurrency.synchronized_lock;

import org.openjdk.jol.info.ClassLayout;

import java.util.concurrent.TimeUnit;

public class BiasedLock {
    public static void main(String[] args) {
        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Object lock = new Object();
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
            System.out.println(Thread.currentThread().getName());
        }).start();
    }
}

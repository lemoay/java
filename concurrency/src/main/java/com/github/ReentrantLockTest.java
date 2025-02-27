package com.github;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockTest {

    @SneakyThrows
    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        new Thread(() -> {
            try {
                lock.lock();
                System.out.println("===== T1 执行了 =====");
                Thread.sleep(5000);
                condition.signal();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }).start();

        try {
            lock.lock();
            System.out.println("===== Main 执行了 =====");
            Thread.sleep(5000);
            condition.await();
            System.out.println("===== Main 唤醒了 =====");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}

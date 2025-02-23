package com.github.concurrency;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ThreadPoolTest {


    static final ExecutorService pool = Executors.newFixedThreadPool(5);

    @Test
    public void test() {
        pool.submit(() -> {
            log.info("{}", Thread.currentThread().getName());
        });
    }
}

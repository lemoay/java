package com.github.concurrency;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

@Slf4j
public class CompletableFutureTest {


    static final ExecutorService pool = Executors.newFixedThreadPool(5);

    /**
     *         graph TB
     *         A[开始] --> B[CF1]
     *         A --> C[CF2]
     *         B --> D[CF3]
     *         B --> E[CF4]
     *         C --> E[CF4]
     *         C --> F[CF5]
     *         D --> G[CF6]
     *         E --> G[CF6]
     *         F --> G[CF6]
     *            开始
     *            /  \
     *           /    \
     *         CF1    CF2
     *        /  \    /  \
     *       /    \  /    \
     *     CF3    CF4     CF5
     *       \     |      /
     *        \    |     /
     *         \   |    /
     *          \  |   /
     *           \ |  /
     *            CF6
     */
    @Test
    public void test() {
        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(6);

        // CF1
        CompletableFuture<String> cf1 = CompletableFuture.supplyAsync(() -> {
            try {
                sleep(1000); // 模拟耗时任务
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("CF1 completed");
            return "CF1";
        }, executor);

        // CF2
        CompletableFuture<String> cf2 = CompletableFuture.supplyAsync(() -> {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("CF2 completed");
            return "CF2";
        }, executor);

        // CF3 依赖 CF1
        CompletableFuture<String> cf3 = cf1.thenApply(result -> {
            try {
                sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("CF3 completed with input: " + result);
            return "CF3";
        });

        // CF4 依赖 CF1 和 CF2
        CompletableFuture<String> cf4 = cf1.thenCombine(cf2, (r1, r2) -> {
            try {
                sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("CF4 completed with inputs: " + r1 + ", " + r2);
            return "CF4";
        });

        // CF5 依赖 CF2
        CompletableFuture<String> cf5 = cf2.thenApply(result -> {
            try {
                sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("CF5 completed with input: " + result);
            return "CF5";
        });

        // CF6 依赖 CF3、CF4 和 CF5
        CompletableFuture<Void> cf6 = CompletableFuture.allOf(cf3, cf4, cf5)
                .thenRun(() -> {
                    try {
                        String result3 = cf3.join();
                        String result4 = cf4.join();
                        String result5 = cf5.join();
                        sleep(500);
                        System.out.println("CF6 completed with inputs: " + result3 + ", " + result4 + ", " + result5);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        // 等待 CF6 完成
        cf6.join();

        // 关闭线程池
        executor.shutdown();
        System.out.println("All tasks completed");
    }

    @RepeatedTest(5)
    public void accept() {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread());
            return 1;
        }, pool).thenAccept(i -> {
            System.out.println(Thread.currentThread());
            System.out.println(++i);
        });
    }

    @RepeatedTest(5)
    public void acceptAsync() {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread());
            return 1;
        }, pool).thenAcceptAsync(i -> {
            System.out.println(Thread.currentThread());
            System.out.println(++i);
        }, pool);
    }

    @Test
    public void thenApply() {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread());
            return 1;
        }, pool).thenApply(i -> {
            System.out.println(Thread.currentThread());
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return ++i;
        });
        log.info("主线程开始执行");
        Integer join = future.join();
        log.info("主线程执行完毕");
    }

    @Test
    public void thenRun() {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 1;
        }, pool).thenRun(() -> {
            log.info(Thread.currentThread().getName());
        });
        future.join();
    }

    @Test
    public void thenCompose() {
        log.info("主线程开始执行");
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("future1: " + Thread.currentThread().getName());
            return 1;
        }, pool).thenCompose(x -> CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("future1: " + Thread.currentThread().getName());
            return x + 2;
        }, pool));

        System.out.println(future.join());
        log.info("主线程执行完毕");
    }

    @Test
    public void thenCombine() {
        log.info("主线程开始执行");
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("future1: " + Thread.currentThread().getName());
            return 1;
        }, pool);

        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("future2: " + Thread.currentThread().getName());
            return 2;
        }, pool);

        CompletableFuture<Integer> future = future1.thenCombine(future2, (x, y) -> {
            log.info("thenCombine: " + Thread.currentThread().getName());
            return x + y;
        });
        future.join();
        log.info("主线程执行完毕");
    }

    /**
     * 异常捕获测试
     */
    @Test
    public void exceptionCatch() {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            int i = 1 / 0;
            return "ok";
        }).exceptionally(i -> {
            log.error(i.getMessage());
            return "ok";
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            int i = 1 / 0;
            return "ok";
        }).handleAsync((x, y) -> {
            log.info(x);
            log.error(y.getMessage());
            return "ok";
        });

        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            int i = 1 / 0;
            return "ok";
        });

        CompletableFuture<Void> all = CompletableFuture.allOf(future1, future2, future3);

        try {
            all.get();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}

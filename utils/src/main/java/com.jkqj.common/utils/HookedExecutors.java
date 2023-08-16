package com.jkqj.common.utils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 创建线程池的工具类
 *
 * @author cb
 * @date 2020-10-25
 */
public final class HookedExecutors {

    private static final ThreadLocal<Long> TIMEOUT = ThreadLocal.withInitial(() -> TimeUnit.MINUTES.toMillis(1));

    /**
     * 设置当前线程池超时时间
     *
     * @param millis
     */
    public static void setTimeout(long millis) {
        TIMEOUT.set(millis);
    }


    /**
     * 设置当前线程池超时时间
     *
     * @param
     */
    public static long getTimeout() {
        return TIMEOUT.get();
    }

    /**
     * 程序退出时保证线程任务执行结束或者超时
     *
     * @param executorService
     * @param <T>
     * @return
     */
    public static <T extends ExecutorService> T hook(final ExecutorService executorService) {
        long timeout = getTimeout();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executorService.shutdown();
            try {
                // 等待超时
                executorService.awaitTermination(timeout, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                // ingnore
            }
            if (!executorService.isShutdown()) {
                executorService.shutdownNow();
            }
        }));
        return (T) executorService;
    }


    /**
     * 创建线程池
     *
     * @return
     * @see {@link Executors#newCachedThreadPool()}
     */
    public static ThreadPoolExecutor newCachedThreadPool() {
        return newCachedThreadPool(null);
    }

    /**
     * 创建指定名称线程池
     *
     * @param poolName
     * @return
     * @see {@link Executors#newCachedThreadPool()}
     */
    public static ThreadPoolExecutor newCachedThreadPool(String poolName) {
        return hook(Executors.newCachedThreadPool(threadFactory(poolName)));
    }


    /**
     * 创建线程池
     *
     * @return
     * @see {@link Executors#newSingleThreadExecutor()}
     */
    public static ExecutorService newSingleThreadExecutor() {
        return newSingleThreadExecutor(null);
    }

    /**
     * 创建指定名称线程池
     *
     * @param poolName
     * @return
     * @see {@link Executors#newSingleThreadExecutor()}
     */
    public static ExecutorService newSingleThreadExecutor(String poolName) {
        return hook(Executors.newSingleThreadExecutor(threadFactory(poolName)));
    }


    /**
     * 创建线程池
     *
     * @param nThreads
     * @return
     * @see {@link Executors#newFixedThreadPool(int)}
     */
    public static ThreadPoolExecutor newFixedThreadPool(int nThreads) {
        return newFixedThreadPool(null, nThreads);
    }

    /**
     * 创建指定名称线程池
     *
     * @param poolName
     * @param nThreads
     * @return
     * @see {@link Executors#newFixedThreadPool(int)}
     */
    public static ThreadPoolExecutor newFixedThreadPool(String poolName, int nThreads) {
        return hook(Executors.newFixedThreadPool(nThreads, threadFactory(poolName)));
    }


    /**
     * 创建调度线程池
     *
     * @param nThreads
     * @return {@link Executors#newScheduledThreadPool(int)}
     */
    public static ScheduledThreadPoolExecutor newScheduledThreadPool(int nThreads) {
        return newScheduledThreadPool(null, nThreads);
    }


    /**
     * 创建指定名称调度线程池
     *
     * @param poolName
     * @param nThreads
     * @return {@link Executors#newScheduledThreadPool(int)}
     */
    public static ScheduledThreadPoolExecutor newScheduledThreadPool(String poolName, int nThreads) {
        return hook(Executors.newScheduledThreadPool(nThreads, threadFactory(poolName)));
    }

    /**
     * 创建单线程调度线程池
     *
     * @return {@link Executors#newSingleThreadScheduledExecutor()}
     */
    public static ScheduledThreadPoolExecutor newSingleThreadScheduledExecutor() {
        return newSingleThreadScheduledExecutor(null);
    }


    /**
     * 创建指定名称单线程调度线程池
     *
     * @param poolName
     * @return {@link Executors#newSingleThreadScheduledExecutor()}
     */
    public static ScheduledThreadPoolExecutor newSingleThreadScheduledExecutor(String poolName) {
        return hook(Executors.newSingleThreadScheduledExecutor(threadFactory(poolName)));
    }


    /**
     * 当消费速度过慢时, 支持生产者线程阻塞<br/>
     *
     * @param nThreads 线程数
     * @param capacity 队列容量
     * @return
     */
    public static ThreadPoolExecutor blockingExecutor(int nThreads, int capacity) {
        return blockingExecutor(null, nThreads, capacity);
    }


    /**
     * 当消费速度过慢时, 支持生产者线程阻塞<br/>
     *
     * @param poolName 线程池名称
     * @param nThreads 线程数
     * @param capacity 队列容量
     * @return
     */
    public static ThreadPoolExecutor blockingExecutor(String poolName, int nThreads, int capacity) {
        return hook(new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.NANOSECONDS,
                new LinkedBlockingQueue<>(capacity), threadFactory(poolName),
                (r, executor) -> {
                    try {
                        executor.getQueue().put(r);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }));
    }

    static ThreadFactory threadFactory(String poolName) {
        return new DefaultThreadFactory(poolName);
    }

    static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory(String poolName) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" +
                    (poolName == null || poolName.isEmpty() ? poolNumber.getAndIncrement() : poolName) +
                    "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);

            if (t.isDaemon()) {

                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }

            return t;
        }
    }
}
package com.ly.core.utils;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * 线程池工具
 * 执行顺序：corePoolSize -> 任务队列 -> maximumPoolSize -> 拒绝策略
 */
public class ThreadPool {
    /**
     * 线程池长期维持的线程数，即使线程处于Idle状态，也不会回收。
     * 当前机器核数 * 2
     */
    public static final int corePoolSize = Runtime.getRuntime().availableProcessors() * 2 ;

    /**
     * 线程数的上限
     */
    public static final int maximumPoolSize = 50;

    /**
     * 超过corePoolSize的线程的idle时长，
     * 超过这个时间，多余的线程会被回收
     * 单位unit
     */
    public static final long keepAliveTime = 60;

    public static final TimeUnit unit = TimeUnit.SECONDS;

    /**
     * 任务的排队队列
     * ArrayBlockingQueue
     * 有界队列
     * 先进先出队列（队列头的是最先进队的元素；队列尾的是最后进队的元素）
     * 有界队列（即初始化时指定的容量，就是队列最大的容量，不会出现扩容，容量满，则阻塞进队操作；容量空，则阻塞出队操作）
     * 队列不支持空元素
     * <p>
     * LinkedBlockingQueue
     * 如果不指定容量，默认为Integer.MAX_VALUE，也就是无界队列
     * <p>
     * PriorityBlockingQueue
     * 是一个基于数组实现的线程安全的无界队列
     * <p>
     * SynchronizedQueue
     * 无界的FIFO同步队列
     */
    public static final BlockingQueue workQueue = new LinkedBlockingQueue<>(20);

    /**
     * 拒绝策略
     * AbortPolicy  抛出RejectedExecutionException
     * DiscardPolicy  什么也不做，直接忽略
     * DiscardOldestPolicy  丢弃执行队列中最老的任务，尝试为当前提交的任务腾出位置
     * CallerRunsPolicy  直接由提交任务者执行这个任务
     */
    public static final RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();

    public static ThreadPoolExecutor threadPool;

    /**
     * 无返回值直接执行
     *
     * @param runnable
     */
    public static void execute(Runnable runnable) {
        createThreadPool().execute(runnable);
    }

    /**
     * 返回值直接执行，获取返回值 Future.get()
     *
     * @param callable
     */
    public static <T> Future<T> submit(Callable<T> callable) {
        return createThreadPool().submit(callable);
    }

    /**
     * 多个task提交
     * @param callables
     * @param <T>
     * @return
     */
    public static <T> List<Future<T>> inVokeAll(Collection<? extends Callable<T>> callables){
        try {
            return createThreadPool().invokeAll(callables);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ThreadPoolExecutor createThreadPool() {
        if (threadPool != null && !threadPool.isShutdown()) {
            return threadPool;
        } else {
            synchronized (ThreadPool.class) {
                if (threadPool == null || threadPool.isShutdown()) {
                    threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit,
                            workQueue, r -> {
                                Thread thread = new Thread(r);
                                thread.setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());
                                return thread;
                            }, handler);
                }
                System.out.println(threadPool.getActiveCount() + ","+ threadPool.getCorePoolSize()+ ","+ threadPool.getMaximumPoolSize() + ","+ threadPool.getPoolSize() + ","+ threadPool.getTaskCount());
                return threadPool;
            }
        }
    }

    public static void shutdown() {
        if(threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdown();
        }
    }

    /**
     * 关闭后所有任务是否都已完成
     * while (!isTerminated())
     */
    public static void isTerminated(){
        threadPool.isTerminated();
    }



    public static void main(String[] args) {
        ThreadPool.execute(() -> System.out.println(Thread.currentThread().getName()));
        shutdown();
        System.out.println(ThreadPool.threadPool.isShutdown());
        ThreadPool.execute(() -> System.out.println(Thread.currentThread().getName()));
        System.out.println(ThreadPool.threadPool.isShutdown());
//        for (int i = 0; i < 10; i++) {
////            ThreadPool.execute(() -> System.out.println(Thread.currentThread().getName()));
//
//            List<Future<String>> list = new ArrayList<>();
////            list.add(ThreadPool.submit(() -> {
////                System.out.println(Thread.currentThread().getName());
////                return "ok";
////            }));
//
//            list.forEach(l -> {
//                try {
//                    System.out.println(l.get());
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//            });
//        }
    }
}
package com.lemon.core.util;

import com.google.common.collect.Maps;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author wangmin
 * 可控制线程最大并发数，超出的线程会在队列中等待
 */
public class ThreadPoolUtil {
    private static Logger logger = Logger.getLogger(ThreadPoolUtil.class);

    //参数初始化
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    //核心线程数量大小
    private static final int corePoolSize = Math.max(2, CPU_COUNT - 1);
    //线程池最大容纳线程数
    private static final int maximumPoolSize = CPU_COUNT * 2 + 1;
    //线程空闲后的存活时长
    private static final int keepAliveTime = 300;

    public static Map<String, ThreadPoolExecutor> threadExcutor = Maps.newConcurrentMap();


    public static ThreadPoolExecutor getAllThreadPool(String name) {
        ThreadPoolExecutor executor = threadExcutor.get(name);
        if (null == executor) {
            synchronized (ThreadPoolUtil.class){
                if(null == executor){
                    executor = ThreadPoolUtil.nameFixMaxThreadPool(name, 10, 20);
                    threadExcutor.put(name, executor);
                }
            }
        }

        return executor;
    }

    /**
     * 定时同步
     * @param name
     * @return
     */
    public static ThreadPoolExecutor getScheThreadPool(final String name) {
        ThreadPoolExecutor executor = threadExcutor.get(name);
        if (null == executor) {
            synchronized (ThreadPoolUtil.class){
                if(null == executor){
                    executor = ThreadPoolUtil.scheThreadPool(name);
                    threadExcutor.put(name, executor);
                }
            }
        }

        executor.execute(new Runnable() {
            @Override
            public void run() {
            }
        });
        return executor;
    }

    public static ThreadPoolExecutor scheThreadPool(final String groupName) {
        return new ScheduledThreadPoolExecutor(corePoolSize, new ThreadFactory() {
            private AtomicInteger num = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName(groupName + " - " + num.incrementAndGet());
                return thread;
            }
        }, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                logger.info(groupName + "-- rejected execution");
            }
        });
    }

    public static ThreadPoolExecutor nameFixMaxThreadPool(final String groupName, final int max, final int qsize) {
        return nameFixMaxThreadPool(groupName, maximumPoolSize, qsize, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                logger.info(groupName + "-- rejected execution");
            }
        });
    }

    public static ThreadPoolExecutor nameFixMaxThreadPool(final String groupName, final int max, final int qsize, RejectedExecutionHandler handler) {
        return new ThreadPoolExecutor(corePoolSize, max, keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(qsize), new ThreadFactory() {
            private AtomicInteger num = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName(groupName + " - " + num.incrementAndGet());
                return thread;
            }
        }, handler);
    }
}

package com.lemon.core.util.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemon.base.RestResult;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * https://www.jianshu.com/p/c5048208709c
 */
public class RedissonTest {
    public void testAtomic() {
        RedissonClient client = createClient();
        RAtomicLong rAtomicLong = client.getAtomicLong("RAtomicLong");

        Callable<Long> callable = () -> {
            String threadName = Thread.currentThread().getName();

            // test
            long count = rAtomicLong.addAndGet(1);


            System.out.printf("thread:{%s} >>> %d\n", threadName, count);
            return count;
        };

        try {
            ThreadPool.invoke(callable, 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            ThreadPool.shutdown();
        }

//        selfLock();
    }

//    public void testSet() {
//        RedissonClient client = createClient();
//        RSet<Object> rSet   = client.getSet("RSet");
//
//        Callable<CompletionStage<Void>> callable = () -> {
//            return rSet.addAllAsync(Set.of("swimming", "coding"))
//                    .thenAccept(v -> {
//                        if (v == Boolean.FALSE) {
//                            System.out.printf("[fail]thread:{%s} >>> %s\n", Thread.currentThread().getName(), v);
//                        }
//                    })
//                    .exceptionally(e -> {
//                        System.out.printf("[error]thread:{%s} >>> %s\n", Thread.currentThread().getName(), e);
//                        return null;
//                    });
//        };
//
//        try {
//            ThreadPool.invoke(callable, 100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            ThreadPool.shutdown();
//        }
//    }

//    public void testMap() {
//        RedissonClient       client = createClient();
//        RMap<Object, Object> rMap   = client.getMap("RMap", MapOptions.defaults());
//
//        Callable<CompletionStage<Void>> callable = () -> {
//            return rMap.putAllAsync(Map.of("chinese", 95, "math", 92, "english", 91))
//                    .exceptionally(e -> {
//                        System.out.printf("[error]thread:{%s} >>> %s\n", Thread.currentThread().getName(), e);
//                        return null;
//                    });
//        };
//
//        try {
//            ThreadPool.invoke(callable, 100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            ThreadPool.shutdown();
//        }
//    }

//    public void testLock() {
//        RedissonClient client = createClient();
//        RLock rLock = client.getLock("RLock");
//        Map<String, Integer> map = new HashMap<>() {{
//            put("number", 0);
//        }};
//        Callable<Void> callable = () -> {
//            rLock.lock();
//            try {
//                map.put("number", map.get("number") + 1);
//            } finally {
//                rLock.unlock();
//            }
//            return null;
//        };
//
//        try {
//            ThreadPool.invoke(callable, 100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            ThreadPool.shutdown();
//        }
//        System.out.println("number=" + map.get("number"));
//    }

    private void selfLock() {
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private RedissonClient createClient() {
        Config config = new Config();
        // EPOLL 只能在 Linux 下使用
        config.setTransportMode(TransportMode.NIO);
        config.useSingleServer()
                .setAddress("redis://localhost:6379")
                .setDatabase(0)
                .setClientName("redisson-test")
                .setConnectionPoolSize(128)
                .setConnectionMinimumIdleSize(128)
                .setIdleConnectionTimeout(60000)
                .setConnectTimeout(1000)
                .setTimeout(3000)
                .setRetryAttempts(1);
        config.setCodec(new JsonJacksonCodec(new ObjectMapper()));
        config.setNettyThreads(128).setThreads(128);
        return Redisson.create(config);
    }

//    public void testBloomFilter() {
//        RedissonClient client = createClient();
//        RBloomFilter<Object> bloomFilter = client.getBloomFilter("bloomFilter");
//
//        bloomFilter.tryInit(10, 0.01);
//
//        Map<String, String> map  = Map.of("a", "aa", "b", "b");
//        List<Integer> list = List.of(1, 11, 111);
//        bloomFilter.add(map);
//        bloomFilter.add(list);
//        bloomFilter.add(true);
//        bloomFilter.add("bloomFilter");
//
//        System.out.println("contains true=" + bloomFilter.contains(true));
//        System.out.println("contains false=" + bloomFilter.contains(false));
//        System.out.println("contains map=" + bloomFilter.contains(map));
//        System.out.println("contains list=" + bloomFilter.contains(list));
//
//        System.out.println("size=" + bloomFilter.getSize());
//        System.out.println("count=" + bloomFilter.count());
//    }

    abstract static class ThreadPool {
        private volatile static ThreadPoolExecutor executor;

        private static void initThreadPool() {
            executor = ThreadPoolHolder.INSTANCE;
        }

        public static <R> List<Future<R>> invoke(Callable<R> callable, int nRepeat) throws InterruptedException {
            return invoke(callable, nRepeat, 0, TimeUnit.SECONDS);
        }

        public static <R> List<Future<R>> invoke(Callable<R> callable, int nRepeat, long timeout, TimeUnit unit) throws InterruptedException {
            if (executor == null) {
                initThreadPool();
            }
            List<Callable<R>> callables = IntStream.range(0, nRepeat).boxed().map(i -> callable).collect(Collectors.toList());
            if (timeout <= 0) {
                return executor.invokeAll(callables);
            }
            return executor.invokeAll(callables, timeout, unit);
        }

        private static class ThreadPoolHolder {
            private final static ThreadPoolExecutor INSTANCE =
                    new ThreadPoolExecutor(128, 128, 0, TimeUnit.SECONDS,
                            new LinkedBlockingQueue<>());
        }

        public static void shutdown() {
            if (executor != null && !executor.isShutdown()) {
                executor.shutdown();
            }
            executor = null;
        }
    }

    public static void main(String[] args) {
        RedissonTest redissonTest = new RedissonTest();
        RedissonClient redisson = redissonTest.createClient();

        RLock lock = redisson.getLock("anyLock");
        // 最常见的使用方法
        lock.lock();
        lock.unlock();
    }

}

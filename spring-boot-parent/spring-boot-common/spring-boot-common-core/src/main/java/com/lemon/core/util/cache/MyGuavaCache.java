package com.lemon.core.util.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author ：lemon
 * @description：
 * @date ：Created in 2020/6/22 12:15
 */
public class MyGuavaCache {
    /**
     * 构建一个本地缓存
     */
    private final LoadingCache<String, Object> CACHE = CacheBuilder.newBuilder()
            //初始化100个
            .initialCapacity(100)
            //最大10000
            .maximumSize(10000)
            //30分钟没有读写操作数据就过期
            .expireAfterAccess(1, TimeUnit.MINUTES)
            //只有当内存不够的时候才会value才会被回收
            .softValues()
            //开启统计功能
            .recordStats()

            .build(new CacheLoader<String, Object>() {
                //如果get()没有拿到缓存，直接点用load()加载缓存
                @Override
                public Object load(String key) {
                    return "null";
                }

                /**
                 * 在调用getAll()的时候，如果没有找到缓存，就会调用loadAll()加载缓存
                 * @param keys
                 * @return
                 * @throws Exception
                 */
                @Override
                public Map<String, Object> loadAll(Iterable<? extends String> keys) throws Exception {
                    return super.loadAll(keys);
                }

                /**
                 * 调用refresh()的时候调用reload()，一般用于更新缓存
                 * @param key
                 * @param oldValue
                 * @return
                 * @throws Exception
                 */
                @Override
                public ListenableFuture<Object> reload(String key, Object oldValue) throws Exception {
                    //异步更新缓存
                    return Futures.immediateFuture("NEW_VALUE");
                }
            });

    private void test() {
        try {
            System.out.println(CACHE.get("hello", () -> {
                return "hello";
            }));
            System.out.println(CACHE.get("hello"));
            try {
                TimeUnit.SECONDS.sleep(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(CACHE.get("hello"));
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MyGuavaCache myGuavaCache = new MyGuavaCache();
        myGuavaCache.test();
    }
}

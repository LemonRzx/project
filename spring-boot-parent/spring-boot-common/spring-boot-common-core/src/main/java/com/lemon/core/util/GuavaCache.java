package com.lemon.core.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.lemon.base.RestResult;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author ：lemon
 * @description：
 * @date ：Created in 2020/6/22 10:53
 * https://www.jianshu.com/p/a08cedfe9772
 * https://www.cnblogs.com/fnlingnzb-learner/p/11022152.html
 */
public class GuavaCache {

    /**
     * 构建一个本地缓存
     */
    private static final LoadingCache<String, Object> CACHE = CacheBuilder.newBuilder()
            //初始化100个
            .initialCapacity(100)
            //最大10000
            .maximumSize(10000)
            //30分钟没有读写操作数据就过期
            .expireAfterAccess(30, TimeUnit.MINUTES)
            //只有当内存不够的时候才会value才会被回收
            .softValues()
            //开启统计功能
            .recordStats()

            .build(new CacheLoader<String, Object>() {
                //如果get()没有拿到缓存，直接点用load()加载缓存
                @Override
                public Object load(String key) {
                    System.out.println("key:" + key);
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
                    System.out.println(keys);
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
                    System.out.println("reload:[" + key + ":" + oldValue + "]");
                    //异步更新缓存
                    return Futures.immediateFuture("NEW_VALUE");
                }
            });

    /**
     * cache测试
     */
    private static void cacheTest() throws ExecutionException {
        final String value = "world";

        //查询hello对应的缓存，没有找到就返回null
        Object obj2 = CACHE.get("hello");
        System.out.println(obj2);

        Callable<Object> callable = () -> value;
        //如果没有找到hello对应的缓存数据，就调用callable获取，过程：调用，缓存，返回
        final Object obj1 = CACHE.get("hello", callable);

        //查询hello对应的缓存，没有找到就返回null
        obj2 = CACHE.getIfPresent("hello");

        //判断是否是从callable拿到的数据
        System.out.println(Objects.equals(value, obj1));

        //判断是否是从缓存中获取的数据
        System.out.println(Objects.equals(value, obj2));

        //显示插入
        CACHE.put("PUT_KEY", "PUT_VALUE");
        //判断是的插入成功
        System.out.println(CACHE.getIfPresent("PUT_KEY"));

        //显示清除
        CACHE.invalidate("hello");
        //判断是否清除成功
        System.out.println(CACHE.get("hello"));

        //获取缓存的状态
        CacheStats stats = CACHE.stats();
        /**
         * CacheStats{
         *   hitCount=3,             缓存命中次数
         *   missCount=2,            缓存未命中的次数
         *   loadSuccessCount=2,     缓存加载成功次数
         *   loadExceptionCount=0,   缓存加载异常次数
         *   totalLoadTime=4270620,  总共加载时间(单位:纳秒)
         *   evictionCount=0         缓存被回收次数，不包含显示清除
         * }
         */
        System.out.println(stats);
        //刷新指定的缓存
        CACHE.refresh("PUT_KEY");
        //测试是否刷新成功缓存
        System.out.println(CACHE.get("PUT_KEY"));

        //批量获取缓存
        List<String> keys = Lists.newArrayList("KEY_1", "KEY_2");
        Map<String, Object> result = CACHE.getAll(keys);
        System.out.println(result);

        //将cache转换成视图
        Map<String, Object> map = CACHE.asMap();
        System.out.println(map);

        //获取指定的缓存数据
        System.out.println(map.get("PUT_KEY"));
        //修改视图
        map.put("MAP_KEY", "MAP_VALUE");
        //修改视图也能修改缓存
        System.out.println(CACHE.get("MAP_KEY"));
    }

    /**
     * 主函数
     *
     * @param args
     * @throws ExecutionException
     */
    public static void main(String[] args) throws ExecutionException {
        cacheTest();
    }

    /**
     *
     * @param param
     * @return
     */
    public RestResult lemonTest(@RequestBody Map param) {
        List datas = null;
        try {
            datas = (List) CACHE.get("lemon", ()->{
                List result = null;
                if (true) {
                    result = new ArrayList<>();
                }
                return result;
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return RestResult.ok().data(datas);
    }
}
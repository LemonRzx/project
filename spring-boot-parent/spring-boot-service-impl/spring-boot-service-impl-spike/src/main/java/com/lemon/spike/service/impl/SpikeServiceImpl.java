package com.lemon.spike.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.lemon.base.RestResult;
import com.lemon.core.util.RedisUtil;
import com.lemon.core.util.redis.RedissLockUtil;
import com.lemon.spike.SpikeService;
import com.lemon.spike.mapper.SeckillMapper;
import com.lemon.spike.mapper.entity.SeckillEntity;
import com.lemon.spike.producer.SpikeCommodityProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author ：lemon
 * @description：秒杀服务接口实现
 * @date ：Created in 2020/4/30 17:05
 */
@RestController
public class SpikeServiceImpl implements SpikeService {
    private final String REDIS_LOCK_KEY_PREFIX = "SECKILL_LOCK_KEY_PREFIX";
    private final String GOODS_KEY_PREFIX = "SECKILL_GOODS_KEY_PREFIX";

    @Autowired
    private SpikeCommodityProducer spikeCommodityProducer;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SeckillMapper seckillMapper;

    private HashMap<String,Object> CACHE = new HashMap<>();
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Object PRESENT = null;

    @Override
    public RestResult addSpikeTokenBucket(String seckillId, Long tokenQuantity) {
        redisUtil.setString(GOODS_KEY_PREFIX + seckillId, tokenQuantity + "");
        return RestResult.ok();
    }

    @Override
    @Transactional
    public RestResult spike(String userId, long seckillId, int state) {
        lock.readLock().lock();
        try {
            if (CACHE.get(GOODS_KEY_PREFIX + seckillId) != null) {
                return RestResult.ok().msg("该商品已售空");
            }
        } finally {
            lock.readLock().unlock();
        }



        boolean res=false;
        try {
            res = RedissLockUtil.tryLock(REDIS_LOCK_KEY_PREFIX + seckillId, TimeUnit.SECONDS, 1, 30);
            if(res){
                long inventory = Long.valueOf(redisUtil.getString(GOODS_KEY_PREFIX + seckillId));
                if (inventory == 0) {
                    lock.writeLock().lock();
                    try {
                        if (PRESENT == null) {
                            PRESENT = new Object();
                        }
                        CACHE.put(GOODS_KEY_PREFIX + seckillId,PRESENT);
                    } finally {
                        lock.writeLock().unlock();
                    }
                    return RestResult.ok().msg("该商品已售空");
                }
                if (inventory < state) {
                    return RestResult.ok().msg("商品库存不足，当前库存数量：" + inventory);
                }
                redisUtil.setString(GOODS_KEY_PREFIX + seckillId, (inventory - state) + "");

                // 获取到秒杀token之后，异步放入mq中实现修改商品的库存
                sendSeckillMsg(seckillId, userId);
                return RestResult.ok().msg("正在排队中.......");
            }else{
                return RestResult.ok().msg("该商品已售空");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return RestResult.server_error();
        } finally{
            if(res){//释放锁
                RedissLockUtil.unlock(REDIS_LOCK_KEY_PREFIX + seckillId);
            }
        }
    }

    /**
     * 获取到秒杀token之后，异步放入mq中实现修改商品的库存
     */
    @Async
    void sendSeckillMsg(Long seckillId, String userId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("seckillId", seckillId);
        jsonObject.put("userId", userId);
        spikeCommodityProducer.send(jsonObject);
    }

    @Override
    @Transactional
    public RestResult addSpike(String name, Long inventory) {
        SeckillEntity seckillEntity = new SeckillEntity();
        seckillEntity.setName(name).setInventory(inventory).setVersion(0L);
        int result = seckillMapper.insertSeckill(seckillEntity);
        return RestResult.ok();
    }

    @Override
    @Transactional
    public RestResult startSeckilRedisLock(long seckillId, long userId) {
        boolean res=false;
        try {
            res = RedissLockUtil.tryLock(seckillId+"", TimeUnit.SECONDS, 3, 20);
            if(res){
//                Seckill seckill = seckillService.selectById(seckillId);
//                if(seckill.getNumber()>0){
//                    SuccessKilled killed = new SuccessKilled();
//                    killed.setSeckillId(seckillId);
//                    killed.setUserId(userId);
//                    killed.setState(0);
//                    killed.setCreateTime(new Timestamp(new Date().getTime()));
//                    successKilledService.save(killed);
//                    Seckill temp = new Seckill();
//                    temp.setId(seckillId);
//                    temp.setNumber(0);
//                    seckillMapper.updateNum(temp);
//                }else{
//                    return RestResult.server_error();
//                }
            }else{
                return RestResult.server_error();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(res){//释放锁
                RedissLockUtil.unlock(seckillId+"");
            }
        }
        return RestResult.ok();
    }
}

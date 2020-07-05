package com.lemon.spike;

import com.lemon.base.RestResult;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ：lemon
 * @description：秒杀接口
 * @date ：Created in 2020/4/30 16:50
 */
public interface SpikeService {

    /**
     * 根据为秒杀商品创建token桶
     *
     * @param seckillId 秒杀商品id
     * @param tokenQuantity token桶的容量
     * @return
     */
    @RequestMapping("addSpikeTokenBucket")
    RestResult addSpikeTokenBucket(String seckillId, Long tokenQuantity);

    @RequestMapping("addSpike")
    RestResult addSpike(String name, Long inventory);

    /**
     * 秒杀商品
     *
     * @param userId 用户id
     * @param seckillId 秒杀商品的id
     * @param state 秒杀商品的数量
     * @return
     */
    @RequestMapping("spike")
    RestResult spike(String userId, long seckillId, int state);

    RestResult startSeckilRedisLock(long seckillId, long userId);
}

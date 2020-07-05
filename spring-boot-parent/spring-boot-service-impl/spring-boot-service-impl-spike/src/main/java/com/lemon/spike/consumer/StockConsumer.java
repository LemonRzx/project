package com.lemon.spike.consumer;

import com.alibaba.fastjson.JSONObject;
import com.lemon.spike.mapper.OrderMapper;
import com.lemon.spike.mapper.SeckillMapper;
import com.lemon.spike.mapper.entity.OrderEntity;
import com.lemon.spike.mapper.entity.SeckillEntity;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

/**
 * 库存消费者
 *
 *
 */
@Component
@Slf4j
public class StockConsumer {
    @Autowired
    private SeckillMapper seckillMapper;
    @Autowired
    private OrderMapper orderMapper;

    @RabbitListener(queues = "modify_inventory_queue")
    @Transactional
    public void process(Message message, @Headers Map<String, Object> headers, Channel channel) throws IOException {
        String messageId = message.getMessageProperties().getMessageId();
        String msg = new String(message.getBody(), "UTF-8");
        log.info(">>>messageId:{},msg:{}", messageId, msg);
        JSONObject jsonObject = JSONObject.parseObject(msg);
        // 1.获取秒杀id
        Long seckillId = jsonObject.getLong("seckillId");
        SeckillEntity seckillEntity = seckillMapper.findBySeckillId(seckillId);
        if (seckillEntity == null) {
            log.warn("seckillId:{},商品信息不存在!", seckillId);
            return;
        }
//        Long version = seckillEntity.getVersion();
//        int inventoryDeduction = seckillMapper.inventoryDeduction(seckillId, version);
        int modifyInventory = seckillMapper.modifyInventory(seckillId);
        if (!toDaoResult(modifyInventory)) {
            log.info(">>>seckillId:{}修改库存失败>>>>inventoryDeduction返回为{} 秒杀失败！", seckillId, modifyInventory);
            // 此处省略补偿逻辑（将失败的订单信息添加到失败订单表）
            return;
        }
        // 2.添加秒杀订单
        OrderEntity orderEntity = new OrderEntity();
        String userId = jsonObject.getString("userId");
        orderEntity.setUserId(userId);
        orderEntity.setSeckillId(seckillId);
        orderEntity.setState(1L);
        int insertOrder = orderMapper.insertOrder(orderEntity);
        if (!toDaoResult(insertOrder)) {
            return;
        }
        log.info(">>>修改库存成功seckillId:{}>>>>inventoryDeduction返回为{} 秒杀成功", seckillId, modifyInventory);
    }

    // 调用数据库层判断
    public Boolean toDaoResult(int result) {
        return result > 0 ? true : false;
    }

}

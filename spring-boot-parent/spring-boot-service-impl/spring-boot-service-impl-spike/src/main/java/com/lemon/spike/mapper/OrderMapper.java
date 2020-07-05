package com.lemon.spike.mapper;

import com.lemon.spike.mapper.entity.OrderEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface OrderMapper {
    @Insert("INSERT INTO `order` VALUES (null,#{seckillId},#{userId},#{state}, now());")
    int insertOrder(OrderEntity orderEntity);

    @Select("SELECT id,seckill_id AS seckillid,user_id as userId,state FROM order WHERE userId=#{userId} and seckill_id=#{seckillId}  AND STATE='1';")
    OrderEntity findByOrder(@Param("phone") String phone, @Param("seckillId") Long seckillId);
}

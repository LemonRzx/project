package com.lemon.spike.mapper;


import com.lemon.spike.mapper.entity.SeckillEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface SeckillMapper {
    @Select("SELECT id,name,inventory,start_time as startTime,end_time as endTime,version,create_time as createTime from seckill where id=#{id}")
    SeckillEntity findBySeckillId(Long id);

    /**
     * 乐官锁
     *
     * @param seckillId
     * @return
     */
    @Update("update seckill set inventory=inventory-1, version=version+1 where  id=#{seckillId} and inventory>0  and version=#{version}")
    int inventoryDeduction(@Param("seckillId") Long seckillId, @Param("version") Long version);

    @Update("update seckill set inventory=inventory-1, version=version+1 where  id=#{seckillId}")
    int modifyInventory(@Param("seckillId") Long seckillId);


    @Insert("INSERT INTO `seckill` VALUES (null,#{name},#{inventory},#{startTime},#{endTime},#{version},now());")
    int insertSeckill(SeckillEntity seckillEntity);
}

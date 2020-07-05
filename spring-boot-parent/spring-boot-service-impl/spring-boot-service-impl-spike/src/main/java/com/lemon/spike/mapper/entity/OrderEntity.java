package com.lemon.spike.mapper.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class OrderEntity {
    private Long id;
    private Long seckillId;
    private String userId;
    private Long state;
    private Date createTime;
}

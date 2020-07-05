package com.lemon.spike.mapper.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class SeckillEntity {
    private Long id;
    private String name;
    private Long inventory;
    private Date startTime;
    private Date endTime;
    private Long version;
}

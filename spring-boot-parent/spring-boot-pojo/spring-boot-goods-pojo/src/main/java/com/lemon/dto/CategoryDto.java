package com.lemon.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CategoryDto implements Serializable {

    private Integer id;

    private Integer parentId;

    private String name;

    private Integer status;

    private Integer sortOrder;

    private Integer level;

    private List<CategoryDto> children;//父菜单下的子菜单
}
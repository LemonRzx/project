package com.lemon.core.util.tree;



import com.lemon.dto.CategoryDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TreeUtil {
//    private  int count = 0;
    private List<CategoryDto> nodes;

    public TreeUtil(List<CategoryDto> nodes){
        this.nodes = nodes;
    }

    public List<CategoryDto> buildTree(){
        List<CategoryDto> list = new ArrayList<CategoryDto>();
        for (CategoryDto node : nodes) {
            if (node.getParentId() == 0) {
                list.add(node);
            }
        }
        list = getSortChildren(list);
        for (CategoryDto node : list) {
            build(node);
        }
        return list;
    }

    /***
     * 构建权限树
     * @Title: build
     * @Description: TODO
     * @param node
     */
    private void build(CategoryDto node){
        List<CategoryDto> children = getChildren(node);
        if (!children.isEmpty()) {
            node.setChildren(children);
//            if(count <3){
//                //children.get(0).setChecked("true");//设置默认选中
//                count ++;
//            }
            for (CategoryDto child : children) {
                build(child);
            }
        }
    }
    /**
     *
     * @Title: getChildren
     * @Description: TODO 获取子节点
     * @param node
     * @return
     */
    private List<CategoryDto> getChildren(CategoryDto node){
        List<CategoryDto> children = new ArrayList<CategoryDto>();
        Integer id = node.getId();
        for (CategoryDto child : nodes) {
//            if (id==child.getParentId()) {
            /*
            【强制】所有的相同类型的包装类对象之间值的比较，全部使用equals方法比较。
            说明：对于Integer var = ? 在-128至127范围内的赋值，Integer对象是在IntegerCache.cache产生，会复用已有对象，
            这个区间内的Integer值可以直接使用==进行判断，但是这个区间之外的所有数据，都会在堆上产生，并不会复用已有对象，
            这是一个大坑，推荐使用equals方法进行判断。
             */
            if (id.equals(child.getParentId())) {
                children.add(child);
            }
        }
        return getSortChildren(children);
        // return children;
    }

    /**
     *
     * @Title: getChildren
     * @Description: TODO 获取排序子节点
     * @param children
     * @return
     */
    private List<CategoryDto> getSortChildren(List<CategoryDto> children){
        MyCompare my = new MyCompare();
        Collections.sort(children,my) ;
        return children;
    }
}

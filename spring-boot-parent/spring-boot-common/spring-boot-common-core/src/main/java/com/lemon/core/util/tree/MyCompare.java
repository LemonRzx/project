package com.lemon.core.util.tree;



import com.lemon.dto.CategoryDto;
import java.util.Comparator;

public class MyCompare implements Comparator<CategoryDto> {

    @Override
    public int compare(CategoryDto o1, CategoryDto o2) {
        if(o1.getSortOrder()<o2.getSortOrder()){
            return -1;
        }else{
            return 1;
        }
    }
}

package com.lemon.base;

import com.lemon.constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RestResult {
    //返回码
    private Integer code;
    //返回消息
    private String msg;
    //数据
    private Object data;

    public static RestResult ok() {
        return new RestResult(Constant.HTTP_RES_CODE_200, null, null);
    }

    public RestResult data(Object data) {
        this.data = data;
        return this;
    }

    public static RestResult server_error(){
        return new RestResult(Constant.HTTP_RES_CODE_500, null, null);
    }

    public RestResult msg(String msg) {
        this.msg = msg;
        return this;
    }
}

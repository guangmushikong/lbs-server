package com.guangmushikong.lbi.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultBody<T> {
    boolean success=false;
    /**
     * 返回错误代码。0表示成功，-1表示失败
     */
    int errcode = -1;
    /**
     * 返回错误消息
     */
    String errmsg = "";
    /**
     * 封装数据信息
     */
    T data;

    public ResultBody(){
        data = null;
    }

    /**
     * 成功返回
     *
     * @param data
     */
    public ResultBody(T data) {
        this.success=true;
        this.errcode = 0;
        this.errmsg = "success";
        this.data = data;
    }

    public ResultBody(int errcode, String errmsg) {
        if(errcode==0) this.success=true;
        this.errcode = errcode;
        this.errmsg = errmsg;
    }

    public ResultBody(int errcode, String errmsg, T data) {
        if(errcode==0) this.success=true;
        this.errcode = errcode;
        this.errmsg = errmsg;
        this.data = data;
    }

    public ResultBody(boolean success, int errcode, String errmsg, T data) {
        this.success=success;
        this.errcode = errcode;
        this.errmsg = errmsg;
        this.data = data;
    }
}

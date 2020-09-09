package com.miniproject.hotwords.common;
/**
* @Description: 同一返回数据格式工具
* @Author: huanghy
* @Date: 2020/9/7
*/
public class ResultUtil {
    private final static String SUCCESS = "success";
    public static <T> Result<T> makeSuccess() {//请求成功，无data数据返回
        return new Result<T>().setCode(ReturnCode.SUCCESS.code).setMsg(SUCCESS);
    }
    public static <T> Result<T> makeSuccess(T data) {//请求成功，有data数据返回
        return new Result<T>().setCode(ReturnCode.SUCCESS).setMsg(SUCCESS).setData(data);
    }
    public static <T> Result<T> makeErr(String message) {//请求失败，自定义错误msg
        return new Result<T>().setCode(ReturnCode.FAIL).setMsg(message);
    }
    public static <T> Result<T> makeRsp(int code, String msg) {//自定义访问状态，自定义msg
        return new Result<T>().setCode(code).setMsg(msg);
    }
    public static <T> Result<T> makeRsp(int code, String msg, T data) {//全都自定义
        return new Result<T>().setCode(code).setMsg(msg).setData(data);
    }
}

package com.miniproject.hotwords.common;
/**
* @Description:  请求状态的枚举类
* @Author: huanghy
* @Date: 2020/9/7
*/
public enum ReturnCode {

    // 请求成功
    SUCCESS(200),
    // 失败
    FAIL(400),
    // 未认证（签名错误）
    UNAUTHORIZED(401),
    // 接口不存在
    NOT_FOUND(404),
    // 服务器内部错误
    INTERNAL_SERVER_ERROR(500);
    public int code;
    ReturnCode(int code) {
        this.code = code;
    }
}

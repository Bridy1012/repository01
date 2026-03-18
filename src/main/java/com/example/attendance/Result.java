package com.example.attendance;
/**
 * 统一返回结果类
 * @param <T> 泛型，支持返回任意类型的数据
 */
public class Result<T> {
    private int code;
    private String message;
    private T data;

    // 成功返回的静态方法
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "操作成功";
        result.data = data;
        return result;
    }

    // 失败返回的静态方法
    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = message;
        return result;
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
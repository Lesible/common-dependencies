package com.sumwhy.model;

/**
 * <p> 返回结果体 </p>
 * <p> created at 2021-11-04 15:30 by lesible </p>
 *
 * @author 何嘉豪
 */
public class Result<T> {

    public static final Result<Void> SUCCESS_RESULT = new Result<>(0, "success", null);

    public static final Result<Void> FAIL_RESULT = new Result<>(500, "fail", null);

    private Integer code;

    private String message;

    private T data;

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result() {
    }

    public static <T> Result<T> successData(T data) {
        return new Result<>(0, "success", data);
    }

    public static Result<Void> errorMsg(String msg) {
        return new Result<>(500, msg, null);
    }

    public static Result<Void> errorMsg(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    public Integer getCode() {
        return code;
    }

    public Result<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Result<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}


package com.bit37.team6.response;

public class JSONResponse<T> {
    private int code; //응답 코드
    private String msg; //응답 메시지
    private T data; //응답 데이터

    //생성자
    public JSONResponse() {
    }

    public JSONResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

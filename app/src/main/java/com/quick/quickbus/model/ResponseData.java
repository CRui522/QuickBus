package com.quick.quickbus.model;

public class ResponseData {
    private int code;
    private String message;

    public ResponseData(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

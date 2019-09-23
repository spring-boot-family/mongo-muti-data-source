package com.xinyan.mongo.common;

/**
 * @author weimin_ruan
 * @date 2019/9/23
 */
public class InnerException extends RuntimeException {
    private StatusCode statusCode;

    public InnerException(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public StatusCode getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }
}

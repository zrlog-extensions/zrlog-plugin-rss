package com.zrlog.plugin.rss.vo;

public class RssApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    public static <T> RssApiResponse<T> success(T data) {
        RssApiResponse<T> response = new RssApiResponse<T>();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

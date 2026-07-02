package com.zrlog.plugin.rss.vo;

public class WebsiteKeyRequest {

    private String key;

    public static WebsiteKeyRequest of(String key) {
        WebsiteKeyRequest request = new WebsiteKeyRequest();
        request.setKey(key);
        return request;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

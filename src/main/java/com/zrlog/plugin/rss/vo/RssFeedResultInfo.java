package com.zrlog.plugin.rss.vo;

public class RssFeedResultInfo {

    private final String content;
    private final String version;

    public RssFeedResultInfo(String content, String version) {
        this.content = content;
        this.version = version;
    }

    public String getContent() {
        return content;
    }

    public String getVersion() {
        return version;
    }
}

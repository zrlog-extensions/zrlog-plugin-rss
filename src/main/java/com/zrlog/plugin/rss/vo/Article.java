package com.zrlog.plugin.rss.vo;

public class Article {
    private final String title;
    private final String link;
    private final String description;
    private final String pubDate;
    private final String guid;

    public Article(String title, String link, String description, String pubDate, String guid) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.pubDate = pubDate;
        this.guid = guid;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getGuid() {
        return guid;
    }
}
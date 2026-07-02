package com.zrlog.plugin.rss.vo;

import java.util.Collections;
import java.util.List;

public class ArticleFeedResponse {

    private ArticlePage data;

    public List<ArticleEntry> rows() {
        if (data == null || data.rows == null) {
            return Collections.emptyList();
        }
        return data.rows;
    }

    public ArticlePage getData() {
        return data;
    }

    public void setData(ArticlePage data) {
        this.data = data;
    }

    public static class ArticlePage {
        private List<ArticleEntry> rows = Collections.emptyList();

        public List<ArticleEntry> getRows() {
            return rows;
        }

        public void setRows(List<ArticleEntry> rows) {
            this.rows = rows;
        }
    }

    public static class ArticleEntry {
        private Number id;
        private String title;
        private String url;
        private String content;
        private String releaseTime;

        public Number getId() {
            return id;
        }

        public void setId(Number id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getReleaseTime() {
            return releaseTime;
        }

        public void setReleaseTime(String releaseTime) {
            this.releaseTime = releaseTime;
        }

        public String idText() {
            return id == null ? "" : String.valueOf(id.longValue());
        }
    }
}

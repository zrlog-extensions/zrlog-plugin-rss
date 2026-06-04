package com.zrlog.plugin.rss.service;

import com.zrlog.plugin.common.SecurityUtils;
import com.zrlog.plugin.rss.vo.Article;
import com.zrlog.plugin.rss.vo.RssFeedResultInfo;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.StringJoiner;

public class RSSFeedGenerator {

    public static RssFeedResultInfo generateRSSFeed(String title, String link, String description, List<Article> articles) {
        String language = "zh-cn";
        String lastBuildDate;
        if (articles.isEmpty()) {
            lastBuildDate = ZonedDateTime.of(2025, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME);
        } else {
            lastBuildDate = articles.get(0).getPubDate();
        }

        StringBuilder rssContent = new StringBuilder();
        rssContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        rssContent.append("<rss version=\"2.0\">\n");
        rssContent.append("  <channel>\n");
        rssContent.append("    <title><![CDATA[").append(title).append("]]></title>\n");
        rssContent.append("    <link><![CDATA[").append(link).append("]]></link>\n");
        rssContent.append("    <description><![CDATA[").append(description).append("]]></description>\n");
        rssContent.append("    <language>").append(language).append("</language>\n");
        rssContent.append("    <pubDate>").append(lastBuildDate).append("</pubDate>\n");
        rssContent.append("    <lastBuildDate>").append(lastBuildDate).append("</lastBuildDate>\n");
        rssContent.append("    <docs>https://www.rssboard.org/rss-specification</docs>\n");
        rssContent.append("    <generator>ZrLog rss Generator</generator>\n");

        StringJoiner rawContent = new StringJoiner("\n");
        rawContent.add(title).add(link).add(language).add(description);
        for (Article article : articles) {
            rssContent.append("    <item>\n");
            rssContent.append("      <title><![CDATA[").append(article.getTitle()).append("]]></title>\n");
            rssContent.append("      <link><![CDATA[").append(article.getLink()).append("]]></link>\n");
            rssContent.append("      <description><![CDATA[").append(article.getDescription()).append("]]></description>\n");
            rssContent.append("      <pubDate>").append(article.getPubDate()).append("</pubDate>\n");
            rssContent.append("      <guid isPermaLink=\"false\">").append(article.getGuid()).append("</guid>\n");
            rssContent.append("    </item>\n");
            rawContent.add(article.getTitle());
            rawContent.add(article.getLink());
            rawContent.add(article.getDescription());
            rawContent.add(article.getPubDate());
            rawContent.add(article.getGuid());
        }

        rssContent.append("  </channel>\n");
        rssContent.append("</rss>\n");

        return new RssFeedResultInfo(rssContent.toString(), SecurityUtils.md5(rawContent.toString()));
    }
}

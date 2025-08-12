package com.zrlog.plugin.rss.service;

import com.zrlog.plugin.common.SecurityUtils;
import com.zrlog.plugin.rss.vo.Article;
import com.zrlog.plugin.rss.vo.RssFeedResultInfo;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

public class RSSFeedGenerator {

    public static void main(String[] args) {
        // Example usage
        List<Article> articles = Arrays.asList(
                new Article("Latest Article Title 1", "http://www.example.com/article/1", "This is a description of the latest article 1", ZonedDateTime.now().minusDays(1).format(DateTimeFormatter.RFC_1123_DATE_TIME), "1"),
                new Article("Latest Article Title 2", "http://www.example.com/article/2", "This is a description of the latest article 2", ZonedDateTime.now().minusDays(2).format(DateTimeFormatter.RFC_1123_DATE_TIME), "2")
                // Add more articles as needed
        );

        String rss = generateRSSFeed("Example RSS Feed", "http://www.example.com", "This is an example of an RSS feed", articles).getContent();
        System.out.println("rss = " + rss);
    }

    public static RssFeedResultInfo generateRSSFeed(String title, String link, String description, List<Article> articles) {
        String language = "zh-cn";
        String lastBuildDate;
        if (articles.isEmpty()) {
            lastBuildDate = ZonedDateTime.parse("2025-01-01 00:00:00",DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).format(DateTimeFormatter.RFC_1123_DATE_TIME);;
        } else {
            lastBuildDate = articles.get(0).getPubDate();
        }

        // Create the RSS feed content
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

        // Add articles from the list
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
            //compare
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
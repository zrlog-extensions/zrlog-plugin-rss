package com.zrlog.plugin.rss.service;

import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.client.HttpClientUtils;
import com.zrlog.plugin.common.model.PublicInfo;
import com.zrlog.plugin.data.codec.ContentType;
import com.zrlog.plugin.rss.vo.Article;
import com.zrlog.plugin.rss.vo.ArticleFeedResponse;
import com.zrlog.plugin.rss.vo.RssFeedResultInfo;
import com.zrlog.plugin.type.ActionType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FeedService {

    private final IOSession session;

    public FeedService(IOSession session) {
        this.session = session;
    }

    private static String toGMTString(Date date) {
        Instant instant = date.toInstant();
        // 将Instant转换为ZonedDateTime，指定GMT时区
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("GMT"));
        // 创建一个DateTimeFormatter并指定为Cookie过期日期的格式
        DateTimeFormatter cookieExpireFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'", java.util.Locale.ENGLISH);
        // 使用DateTimeFormatter格式化日期时间为Cookie的过期日期格式
        return zonedDateTime.format(cookieExpireFormatter);
    }

    public RssFeedResultInfo feed() {
        PublicInfo publicInfo = session.getResponseSync(ContentType.JSON, new HashMap<>(), ActionType.LOAD_PUBLIC_INFO, PublicInfo.class);
        ArticleFeedResponse info = HttpClientUtils.sendGetRequest(publicInfo.getApiHomeUrl() + "/api/article?size=50000&feed=true", ArticleFeedResponse.class, session, Duration.ofSeconds(30));
        List<Article> articles = new ArrayList<>();
        info.rows().forEach(e -> {
            try {
                Date releaseTime = new SimpleDateFormat("yyyy-MM-dd").parse(e.getReleaseTime());
                String pubDate = toGMTString(releaseTime);
                articles.add(new Article(e.getTitle(), publicInfo.getHomeUrl() + e.getUrl(),
                        Objects.requireNonNullElse(e.getContent(), ""), pubDate, e.idText()));
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
        });
        return RSSFeedGenerator.generateRSSFeed(publicInfo.getTitle(), publicInfo.getHomeUrl(), "", articles);
    }
}

package com.zrlog.plugin.rss.service;

import com.google.gson.Gson;
import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.common.model.PublicInfo;
import com.zrlog.plugin.data.codec.ContentType;
import com.zrlog.plugin.rss.vo.Article;
import com.zrlog.plugin.rss.vo.RssFeedResultInfo;
import com.zrlog.plugin.type.ActionType;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        try {
            HttpClient httpClient = HttpClient.newBuilder().build();
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(publicInfo.getApiHomeUrl() + "/api/article?size=50000&feed=true")).build();
            HttpResponse<byte[]> send = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
            Map<String, Object> info = new Gson().fromJson(new String(send.body()), Map.class);
            Map<String, Object> data = (Map<String, Object>) info.get("data");
            List<Map<String, Object>> rows = (List<Map<String, Object>>) data.get("rows");
            List<Article> articles = new ArrayList<>();
            rows.forEach(e -> {
                try {
                    Date releaseTime = new SimpleDateFormat("yyyy-MM-dd").parse((String) e.get("releaseTime"));
                    String pubDate = toGMTString(releaseTime);
                    articles.add(new Article((String) e.get("title"), "https:" + e.get("url"),
                            Objects.requireNonNullElse((String) e.get("content"), ""), pubDate, ((Double) e.get("id")).longValue() + ""));
                } catch (ParseException ex) {
                    throw new RuntimeException(ex);
                }
            });
            httpClient.close();
            return RSSFeedGenerator.generateRSSFeed(publicInfo.getTitle(), publicInfo.getHomeUrl(), "", articles);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

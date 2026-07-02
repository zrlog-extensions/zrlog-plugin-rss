package com.zrlog.plugin.rss.controller;

import com.google.gson.Gson;
import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.common.IdUtil;
import com.zrlog.plugin.data.codec.ContentType;
import com.zrlog.plugin.data.codec.HttpRequestInfo;
import com.zrlog.plugin.data.codec.MsgPacket;
import com.zrlog.plugin.data.codec.MsgPacketStatus;
import com.zrlog.plugin.render.SimpleTemplateRender;
import com.zrlog.plugin.rss.Application;
import com.zrlog.plugin.rss.handle.AutoRefreshFeedFileRunnable;
import com.zrlog.plugin.rss.vo.RssApiResponse;
import com.zrlog.plugin.rss.vo.RssConfig;
import com.zrlog.plugin.rss.vo.RssPageData;
import com.zrlog.plugin.rss.vo.WebsiteKeyRequest;
import com.zrlog.plugin.type.ActionType;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class RssController {

    private final IOSession session;
    private final MsgPacket requestPacket;
    private final HttpRequestInfo requestInfo;
    private final Gson gson = new Gson();

    public RssController(IOSession session, MsgPacket requestPacket, HttpRequestInfo requestInfo) {
        this.session = session;
        this.requestPacket = requestPacket;
        this.requestInfo = requestInfo;
    }

    public void update() {
        session.sendMsg(new MsgPacket(requestConfig(), ContentType.JSON, MsgPacketStatus.SEND_REQUEST, IdUtil.getInt(), ActionType.SET_WEBSITE.name()), msgPacket -> {
            //更新缓存
            int msgId = IdUtil.getInt();
            session.sendJsonMsg(new HashMap<>(), ActionType.REFRESH_CACHE.name(), msgId, MsgPacketStatus.SEND_REQUEST);
            MsgPacket packetByMsgId = session.getResponseMsgPacketByMsgId(msgId);
            //response ok
            response(RssApiResponse.success(Objects.nonNull(packetByMsgId)));
            Application.getAutoRefreshFeedFile().doFeed();
        });
    }

    public void index() {
        session.sendJsonMsg(WebsiteKeyRequest.of("uriPath,rssText"), ActionType.GET_WEBSITE.name(), IdUtil.getInt(), MsgPacketStatus.SEND_REQUEST, msgPacket -> {
            RssConfig config = normalizeConfig(gson.fromJson(msgPacket.getDataStr(), RssConfig.class));
            Map<String, Object> data = new HashMap<>();
            data.put("theme", isDarkMode() ? "dark" : "light");
            data.put("data", gson.toJson(RssApiResponse.success(pageData(config))));
            session.responseHtmlStr(new SimpleTemplateRender().render("/templates/index", session.getPlugin(), data), requestPacket.getMethodStr(), requestPacket.getMsgId());
        });
    }

    public void json() {
        session.sendJsonMsg(WebsiteKeyRequest.of("uriPath,rssText"), ActionType.GET_WEBSITE.name(), IdUtil.getInt(), MsgPacketStatus.SEND_REQUEST, msgPacket -> {
            response(RssApiResponse.success(pageData(normalizeConfig(gson.fromJson(msgPacket.getDataStr(), RssConfig.class)))));
        });
    }

    public void widget() {
        session.sendJsonMsg(WebsiteKeyRequest.of("uriPath,rssText"), ActionType.GET_WEBSITE.name(), IdUtil.getInt(), MsgPacketStatus.SEND_REQUEST, msgPacket -> {
            Map<String, Object> data = configMap(normalizeConfig(gson.fromJson(msgPacket.getDataStr(), RssConfig.class)));
            data.put("target", hasParam("preview") ? "_blank" : "_top");
            session.responseHtmlStr(new SimpleTemplateRender().render("/widget", session.getPlugin(), data), requestPacket.getMethodStr(), requestPacket.getMsgId());
        });
    }

    public void feed() {
        session.responseXmlStr(Application.getAutoRefreshFeedFile().doFeed(), requestPacket.getMethodStr(), requestPacket.getMsgId());
    }

    private RssPageData pageData(RssConfig config) {
        RssPageData data = new RssPageData();
        data.setDark(isDarkMode());
        data.setColorPrimary(getAdminColorPrimary());
        data.setAdminColorPrimary(getAdminColorPrimary());
        data.setPlugin(session.getPlugin());
        data.setUriPath(config.getUriPath());
        data.setRssText(config.getRssText());
        return data;
    }

    private RssConfig requestConfig() {
        if (requestInfo.getRequestBody() != null && requestInfo.getRequestBody().length > 0) {
            String body = new String(requestInfo.getRequestBody(), StandardCharsets.UTF_8);
            if (body.trim().startsWith("{")) {
                return normalizeConfig(gson.fromJson(body, RssConfig.class));
            }
        }
        return normalizeConfig(configFromParams());
    }

    private RssConfig configFromParams() {
        RssConfig config = new RssConfig();
        config.setUriPath(paramValue("uriPath"));
        config.setRssText(paramValue("rssText"));
        return config;
    }

    private RssConfig normalizeConfig(RssConfig config) {
        if (config == null) {
            config = new RssConfig();
        }
        if (config.getUriPath() == null || config.getUriPath().trim().isEmpty()) {
            config.setUriPath(AutoRefreshFeedFileRunnable.DEFAULT_URI_PATH);
        }
        if (config.getRssText() == null) {
            config.setRssText("");
        }
        return config;
    }

    private Map<String, Object> configMap(RssConfig config) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("uriPath", config.getUriPath());
        map.put("rssText", config.getRssText());
        return map;
    }

    private void response(Object data) {
        session.sendMsg(new MsgPacket(data, ContentType.JSON, MsgPacketStatus.RESPONSE_SUCCESS, requestPacket.getMsgId(), requestPacket.getMethodStr()));
    }

    private boolean hasParam(String key) {
        return requestInfo.getParam() != null && requestInfo.getParam().containsKey(key);
    }

    private String paramValue(String key) {
        if (requestInfo.getParam() == null || requestInfo.getParam().get(key) == null || requestInfo.getParam().get(key).length == 0) {
            return "";
        }
        return requestInfo.getParam().get(key)[0];
    }

    private boolean isDarkMode() {
        return requestInfo.isDarkMode();
    }

    private String getAdminColorPrimary() {
        return requestInfo.getAdminColorPrimary();
    }
}

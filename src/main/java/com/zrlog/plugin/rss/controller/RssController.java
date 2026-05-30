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
import com.zrlog.plugin.type.ActionType;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
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
        session.sendMsg(new MsgPacket(params(), ContentType.JSON, MsgPacketStatus.SEND_REQUEST, IdUtil.getInt(), ActionType.SET_WEBSITE.name()), msgPacket -> {
            //更新缓存
            int msgId = IdUtil.getInt();
            session.sendJsonMsg(new HashMap<>(), ActionType.REFRESH_CACHE.name(), msgId, MsgPacketStatus.SEND_REQUEST);
            MsgPacket packetByMsgId = session.getResponseMsgPacketByMsgId(msgId);
            //response ok
            Map<String, Object> map = new HashMap<>();
            map.put("success", Objects.nonNull(packetByMsgId));
            session.sendMsg(new MsgPacket(map, ContentType.JSON, MsgPacketStatus.RESPONSE_SUCCESS, requestPacket.getMsgId(), requestPacket.getMethodStr()));
            Application.getAutoRefreshFeedFile().doFeed();
        });
    }

    public void index() {
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("key", "uriPath,rssText");
        session.sendJsonMsg(keyMap, ActionType.GET_WEBSITE.name(), IdUtil.getInt(), MsgPacketStatus.SEND_REQUEST, msgPacket -> {
            Map map = new Gson().fromJson(msgPacket.getDataStr(), Map.class);
            Map<String, Object> data = new HashMap<>();
            data.put("theme", isDarkMode() ? "dark" : "light");
            data.put("data", gson.toJson(successMap(pageData(map))));
            session.responseHtmlStr(new SimpleTemplateRender().render("/templates/index", session.getPlugin(), data), requestPacket.getMethodStr(), requestPacket.getMsgId());
        });
    }

    public void json() {
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("key", "uriPath,rssText");
        session.sendJsonMsg(keyMap, ActionType.GET_WEBSITE.name(), IdUtil.getInt(), MsgPacketStatus.SEND_REQUEST, msgPacket -> {
            Map map = new Gson().fromJson(msgPacket.getDataStr(), Map.class);
            response(successMap(pageData(map)));
        });
    }

    public void widget() {
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("key", "uriPath,rssText");
        session.sendJsonMsg(keyMap, ActionType.GET_WEBSITE.name(), IdUtil.getInt(), MsgPacketStatus.SEND_REQUEST, msgPacket -> {
            Map map = new Gson().fromJson(msgPacket.getDataStr(), Map.class);
            if (Objects.isNull(map.get("uriPath"))) {
                map.put("uriPath", AutoRefreshFeedFileRunnable.DEFAULT_URI_PATH);
            }
            if (Objects.isNull(map.get("rssText"))) {
                map.put("rssText", "");
            }
            map.put("target", requestInfo.simpleParam().containsKey("preview") ? "_blank" : "_top");
            session.responseHtmlStr(new SimpleTemplateRender().render("/widget", session.getPlugin(), map), requestPacket.getMethodStr(), requestPacket.getMsgId());
        });
    }

    public void feed() {
        session.responseXmlStr(Application.getAutoRefreshFeedFile().doFeed(), requestPacket.getMethodStr(), requestPacket.getMsgId());
    }

    private Map<String, Object> pageData(Map websiteMap) {
        Map<String, Object> data = new HashMap<>();
        data.put("dark", isDarkMode());
        data.put("adminColorPrimary", getAdminColorPrimary());
        data.put("plugin", session.getPlugin());
        

        String uriPath = websiteMap != null ? stringValue(websiteMap.get("uriPath")) : "";
        if (uriPath == null || uriPath.trim().isEmpty()) {
            uriPath = AutoRefreshFeedFileRunnable.DEFAULT_URI_PATH;
        }
        data.put("uriPath", uriPath);
        data.put("rssText", websiteMap != null ? stringValue(websiteMap.get("rssText")) : "");
        return data;
    }

    private Map<String, Object> params() {
        if (requestInfo.getRequestBody() != null && requestInfo.getRequestBody().length > 0) {
            String body = new String(requestInfo.getRequestBody(), StandardCharsets.UTF_8);
            if (body.trim().startsWith("{")) {
                return gson.fromJson(body, Map.class);
            }
        }
        if (requestInfo.getParam() == null) {
            return new HashMap<>();
        }
        return requestInfo.simpleParam();
    }

    private Map<String, Object> successMap(Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        map.put("data", data);
        return map;
    }

    private void response(Map<String, Object> map) {
        session.sendMsg(new MsgPacket(map, ContentType.JSON, MsgPacketStatus.RESPONSE_SUCCESS, requestPacket.getMsgId(), requestPacket.getMethodStr()));
    }

    private String stringValue(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof List && !((List) value).isEmpty()) {
            return String.valueOf(((List) value).get(0));
        }
        return String.valueOf(value);
    }

    private boolean isDarkMode() {
        return requestInfo.isDarkMode();
    }

    private String getAdminColorPrimary() {
        return requestInfo.getAdminColorPrimary();
    }
}
package com.zrlog.plugin.rss;

import com.google.gson.Gson;
import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.client.ClientActionHandler;
import com.zrlog.plugin.data.codec.HttpRequestInfo;
import com.zrlog.plugin.data.codec.MsgPacket;
import com.zrlog.plugin.data.codec.MsgPacketStatus;
import com.zrlog.plugin.rss.controller.RssController;

public class RssClientActionHandler extends ClientActionHandler {

    @Override
    public void httpMethod(IOSession session, MsgPacket msgPacket) {
        if (msgPacket.getStatus() == MsgPacketStatus.RESPONSE_ERROR || msgPacket.getStatus() == MsgPacketStatus.RESPONSE_SUCCESS) {
            return;
        }
        HttpRequestInfo httpRequestInfo = new Gson().fromJson(msgPacket.getDataStr(), HttpRequestInfo.class);
        if (httpRequestInfo.getUri().startsWith("/feed")) {
            new RssController(session, msgPacket, httpRequestInfo).feed();
        } else {
            super.httpMethod(session, msgPacket);
        }
    }

    @Override
    public void refreshCache(IOSession session, MsgPacket msgPacket) {
        Application.getAutoRefreshFeedFile().run();
    }
}

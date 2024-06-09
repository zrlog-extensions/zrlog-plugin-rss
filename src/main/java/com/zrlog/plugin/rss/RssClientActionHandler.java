package com.zrlog.plugin.rss;

import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.rss.controller.RssController;
import com.zrlog.plugin.client.ClientActionHandler;
import com.zrlog.plugin.data.codec.HttpRequestInfo;
import com.zrlog.plugin.data.codec.MsgPacket;
import com.google.gson.Gson;

public class RssClientActionHandler extends ClientActionHandler {

    @Override
    public void httpMethod(IOSession session, MsgPacket msgPacket) {
        HttpRequestInfo httpRequestInfo = new Gson().fromJson(msgPacket.getDataStr(),HttpRequestInfo.class);
        if (httpRequestInfo.getUri().startsWith("/feed")) {
            new RssController(session, msgPacket, httpRequestInfo).feed();
        } else {
            super.httpMethod(session, msgPacket);
        }
    }
}

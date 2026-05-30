package com.zrlog.plugin.rss.handle;

import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.api.IConnectHandler;
import com.zrlog.plugin.data.codec.MsgPacket;

public class ConnectHandler implements IConnectHandler {

    private AutoRefreshFeedFileRunnable autoRefreshFeedFileRunnable;

    @Override
    public void handler(IOSession ioSession, MsgPacket msgPacket) {
        this.autoRefreshFeedFileRunnable = new AutoRefreshFeedFileRunnable(ioSession);
    }

    public AutoRefreshFeedFileRunnable getAutoRefreshFeedFile() {
        return autoRefreshFeedFileRunnable;
    }
}

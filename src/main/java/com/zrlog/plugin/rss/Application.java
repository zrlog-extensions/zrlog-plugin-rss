package com.zrlog.plugin.rss;


import com.zrlog.plugin.client.NioClient;
import com.zrlog.plugin.render.SimpleTemplateRender;
import com.zrlog.plugin.rss.controller.RssController;
import com.zrlog.plugin.rss.handle.AutoRefreshFeedFileRunnable;
import com.zrlog.plugin.rss.handle.ConnectHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Application {

    private static final ConnectHandler rssConnectHandler = new ConnectHandler();

    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {
        List<Class> classList = new ArrayList<>();
        classList.add(RssController.class);
        new NioClient(rssConnectHandler, new SimpleTemplateRender(), new RssClientActionHandler()).connectServer(args, classList, RssPluginAction.class);
    }

    public static AutoRefreshFeedFileRunnable getAutoRefreshFeedFile() {
        return rssConnectHandler.getAutoRefreshFeedFile();
    }
}


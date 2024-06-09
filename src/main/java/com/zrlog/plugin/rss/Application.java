package com.zrlog.plugin.rss;


import com.zrlog.plugin.client.NioClient;
import com.zrlog.plugin.render.SimpleTemplateRender;
import com.zrlog.plugin.rss.controller.RssController;
import com.zrlog.plugin.rss.handle.ConnectHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Application {
    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {
        List<Class> classList = new ArrayList<>();
        classList.add(RssController.class);
        new NioClient(new ConnectHandler(), new SimpleTemplateRender(), new RssClientActionHandler()).connectServer(args, classList, RssPluginAction.class);
    }
}


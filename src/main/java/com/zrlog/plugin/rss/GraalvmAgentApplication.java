package com.zrlog.plugin.rss;

import com.zrlog.plugin.RunConstants;
import com.zrlog.plugin.common.PluginNativeImageUtils;
import com.zrlog.plugin.rss.controller.RssController;
import com.zrlog.plugin.rss.vo.ArticleFeedResponse;
import com.zrlog.plugin.rss.vo.RssApiResponse;
import com.zrlog.plugin.rss.vo.RssConfig;
import com.zrlog.plugin.rss.vo.RssPageData;
import com.zrlog.plugin.rss.vo.WebsiteKeyRequest;
import com.zrlog.plugin.type.RunType;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class GraalvmAgentApplication {


    public static void main(String[] args) throws IOException {
        RunConstants.runType = RunType.AGENT;
        String basePath = System.getProperty("user.dir").replace("\\target", "").replace("/target", "");
        //PathKit.setRootPath(basePath);
        File file = new File(basePath + "/src/main/resources");
        PluginNativeImageUtils.doLoopResourceLoad(file.listFiles(), file.getPath() + "/", "/");
        //Application.nativeAgent = true;
        PluginNativeImageUtils.exposeController(Collections.singletonList(RssController.class));
        PluginNativeImageUtils.usedGsonObject();
        PluginNativeImageUtils.gsonNativeAgentByClazz(Arrays.asList(
                ArticleFeedResponse.class,
                ArticleFeedResponse.ArticlePage.class,
                ArticleFeedResponse.ArticleEntry.class,
                RssApiResponse.class,
                RssConfig.class,
                RssPageData.class,
                WebsiteKeyRequest.class));
        Application.main(args);

    }
}

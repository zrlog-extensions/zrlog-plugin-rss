package com.zrlog.plugin.rss.handle;

import com.zrlog.plugin.IOSession;
import com.zrlog.plugin.common.IOUtil;
import com.zrlog.plugin.common.LoggerUtil;
import com.zrlog.plugin.common.model.BlogRunTime;
import com.zrlog.plugin.data.codec.ContentType;
import com.zrlog.plugin.rss.controller.RssController;
import com.zrlog.plugin.rss.service.FeedService;
import com.zrlog.plugin.rss.vo.RssFeedResultInfo;
import com.zrlog.plugin.type.ActionType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AutoRefreshFeedFileRunnable implements Runnable {

    public static final String DEFAULT_URI_PATH = "/rss.xml";

    private static final Logger LOGGER = LoggerUtil.getLogger(RssController.class);

    private final IOSession ioSession;
    private String uploadedFeedVersion;

    public AutoRefreshFeedFileRunnable(IOSession ioSession) {
        this.ioSession = ioSession;
    }

    @Override
    public void run() {
        try {
            RssFeedResultInfo feed = new FeedService(ioSession).feed();
            if (Objects.equals(uploadedFeedVersion, feed.getVersion())) {
                return;
            }
            doHandle(feed);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
    }

    private String doHandle(RssFeedResultInfo feed) {
        uploadedFeedVersion = feed.getVersion();
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("key", "uriPath");
        Map responseMap = ioSession.getResponseSync(ContentType.JSON, keyMap, ActionType.GET_WEBSITE, Map.class);
        String uriPath = Objects.requireNonNullElse((String) responseMap.get("uriPath"), DEFAULT_URI_PATH);
        String path = ioSession.getResponseSync(ContentType.JSON, new HashMap<>(), ActionType.BLOG_RUN_TIME, BlogRunTime.class).getPath();
        File rssFile = new File(path + uriPath);
        rssFile.getParentFile().mkdirs();
        IOUtil.writeBytesToFile(feed.getContent().getBytes(), rssFile);
        try {
            Map<String, String[]> map = new HashMap<>();
            map.put("fileInfo", new String[]{rssFile + ",/" + rssFile.getName() + ",true"});
            ioSession.requestService("uploadService", map);
        } catch (Exception e) {
            LOGGER.warning("upload to service failed " + e.getMessage());
        }
        return feed.getContent();
    }

    public String doFeed() {
        RssFeedResultInfo feed = new FeedService(ioSession).feed();
        return doHandle(feed);
    }
}

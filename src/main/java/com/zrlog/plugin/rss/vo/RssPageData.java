package com.zrlog.plugin.rss.vo;

import com.zrlog.plugin.message.Plugin;

public class RssPageData {

    private boolean dark;
    private String colorPrimary;
    private String adminColorPrimary;
    private Plugin plugin;
    private String uriPath;
    private String rssText;

    public boolean isDark() {
        return dark;
    }

    public void setDark(boolean dark) {
        this.dark = dark;
    }

    public String getColorPrimary() {
        return colorPrimary;
    }

    public void setColorPrimary(String colorPrimary) {
        this.colorPrimary = colorPrimary;
    }

    public String getAdminColorPrimary() {
        return adminColorPrimary;
    }

    public void setAdminColorPrimary(String adminColorPrimary) {
        this.adminColorPrimary = adminColorPrimary;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public String getUriPath() {
        return uriPath;
    }

    public void setUriPath(String uriPath) {
        this.uriPath = uriPath;
    }

    public String getRssText() {
        return rssText;
    }

    public void setRssText(String rssText) {
        this.rssText = rssText;
    }
}

package com.zrlog.plugin.rss;

import com.zrlog.plugin.RunConstants;
import com.zrlog.plugin.common.PluginNativeImageUtils;
import com.zrlog.plugin.rss.controller.RssController;
import com.zrlog.plugin.type.RunType;

import java.io.File;
import java.io.IOException;
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
        Application.main(args);

    }
}
package com.bai.app.model;

import com.bai.bridge.base.BridgeCoreConstants;

import java.io.File;

public class BridgeAppConstants {

    /**
     * 当前运行文件夹
     */
    public static String systemPath = System.getProperty("user.dir");

    /**
     * 存储插件文件夹
     */
    public static String pluginFoldPath = systemPath + File.separator + BridgeCoreConstants.PLUGIN_FILE;

    /**
     * 存储文件夹位置
     */
    public static String pluginRecordFilePath = pluginFoldPath + File.separator + "pluginRecords";



}

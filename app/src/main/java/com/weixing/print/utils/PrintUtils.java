package com.weixing.print.utils;

import android.util.Log;

import java.util.List;

public class PrintUtils {

    /**
     * 通过组合的设备信息 返回设备名称
     * @param btDeviceInfo
     * @return
     */
    public static String  getDeviceName(String btDeviceInfo){
        String btName = btDeviceInfo.split("\n")[0];
        return btName;
    }

    /**
     * 通过组合的设备信息 返回设备mac地址
     * @param btDeviceInfo
     * @return
     */
    public static String getDeviceAddress(String btDeviceInfo){
        String btAddress = btDeviceInfo.substring(btDeviceInfo.length() - 17);
        return btAddress;
    }

    public static void log(String str) {
        Log.e("susei", str);
    }

    /**
     * 将数组转换为str
     * @param alreadList
     */
    public static String arrayToStr(List<String> alreadList) {
        StringBuilder builder = new StringBuilder();
        for (String str : alreadList) {
            builder.append(str);
            builder.append(",");
        }
        if(builder.length()==0) {
            return "";
        }
        String substring = builder.substring(0, builder.length() - 1);
        PrintUtils.log("将数组转换为str-----" + substring.toString());
        return substring.toString();

    }
}

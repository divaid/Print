package com.weixing.print.manager;

/**
 * 跟蓝牙打印相关的状态
 * Created by wb-cuiweixing on 2015/10/14.
 */

public class PrintExceptionState {
    public static final int BATTERYLOW = 1;//打印机电池电压过低
    public static final int OVERHEAT = 2;//打印机机头过热
    public static final int NOWARKUP = 3;//打印机唤醒未成功
    public static final int COVEROPEN = 4;//打印机纸仓盖未关闭
    public static final int NOPAPER = 5;//打印机缺纸
    public static final int ISPRINTING = 6;//是否打印
    public static final int PRINTOVER = 7;//打印结束
    public static final int STARTPRINT = 8;//开始打印

    public static final int NOSUPPORTBLUETOOTH = 9; //不支持蓝牙
    public static final int PORTNOOPEN = 10;//连接未成功 --未打开蓝牙或者非打印设备
    public static final int NOSUPPORTJPL = 11;//不支持JPL
    public static final int NOJQDEVICE = 12;//非济强设备，可能存在乱码
    public static final int DISCONNECT = 13;//蓝牙失去连接
    public static final int ENABLEPRINT = 14; //现在可打印
    public static final int CANCELDISCOVERY = 15; //取消搜索
    public static final int CANCELPRINT = 16; //取消打印


}


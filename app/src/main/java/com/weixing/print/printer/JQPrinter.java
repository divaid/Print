package com.weixing.print.printer;

import com.weixing.print.printer.esc.ESC;
import com.weixing.print.printer.port.Port;

/**
 * 打印设备
 */
public class JQPrinter {
    private Port _port;
    private byte[] _cmd = {0, 0};

    public ESC esc;
    //	public JPL jpl;
    public boolean isOpen = false;
    private byte[] state = {0, 0};
    private PrinterParam param;

    public final static int STATE_NOPAPER_UNMASK = 0x01;
    public final static int STATE_OVERHEAT_UNMASK = 0x02;
    public final static int STATE_BATTERYLOW_UNMASK = 0x04;
    public final static int STATE_PRINTING_UNMASK = 0x08;
    public final static int STATE_COVEROPEN_UNMASK = 0x10;

    /*
     * 打印机缺纸
     * 0x01
     */
    public boolean isNoPaper;

    /*
     * 打印机打印头过热
     * 0x02
     */
    public boolean isOverHeat;

    /*
     * 打印机电池电压过低
     * 0x04
     */
    public boolean isBatteryLow;

    /*
     * 打印机正在打印
     * 0x08
     */
    public boolean isPrinting;

    /*
     * 打印机纸仓盖未关闭
     * 0x10
     */
    public boolean isCoverOpen;

    /*
     * 复位打印机相关状态标准
     */
    public void stateReset() {
        isNoPaper = false;
        isOverHeat = false;
        isBatteryLow = false;
        isPrinting = false;
        isCoverOpen = false;
    }

    /*
     * 构造函数
     */
    public JQPrinter(Printer_define.PRINTER_MODEL model) {
        _port = new Port();
        param = new PrinterParam(model, _port);
        esc = new ESC(param);
//		jpl = new JPL(param);
    }

    /**
     * 建立连接
     *
     * @param btDeviceString
     * @return
     */
    public boolean open(String btDeviceString)    //注意：最好不要将此函数放在Activity的onCreate函数中，因为bluetooth connect时会有定的延时，有时会造成页面显示很慢，而误认为没有点击按钮
    {
        if (isOpen) return true;

        if (btDeviceString == null) return false;

        if (!_port.open(btDeviceString, 3000)) return false;

        isOpen = true;
        return true;
    }

    public boolean close() {
        if (!isOpen)
            return false;

        isOpen = false;
        return _port.close();
    }

    /*
     * 等待蓝牙为开启状态
     */
    public boolean waitBluetoothOn(int timeout) {
        return _port.getBluetoothStateON(timeout);
    }

    /*
     * 唤醒打印机
     * 注意:部分手持蓝牙连接第一次不稳定，会造成开头字符乱码，可以通常这个方法来避免此问题
     */
    public boolean wakeUp() {
        if (!_port.writeNULL())
            return false;
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isOpen = true;
        return esc.text.init();
    }

    /*
     * 走纸到右黑标
     */
    public boolean feedRightMark() {
        switch (param.model) {
            case JLP351: //JLP351系列没有右黑标传感器，只能使用中间的标签缝传感器代替有右黑标传感器
            case JLP351_IC:
                _cmd[0] = 0x1D;
                _cmd[1] = 0x0C;
                return _port.write(_cmd, 0, 2);//指令0x1D 0x01C 等效于 0x0E
            default:
                _cmd[0] = 0x0C;
                return _port.write(_cmd, 0, 1);
        }
    }

    /*
     * 走纸到左黑标
     */
    public boolean feedLeftMark() {
        switch (param.model) {
            case JLP351: //JLP351系列没有右黑标传感器，只能使用中间的标签缝传感器代替有右黑标传感器
            case JLP351_IC:
                _cmd[0] = 0x0C;
                return _port.write(_cmd, 0, 1);//0x0C
            default:
                _cmd[0] = 0x0E;
                return _port.write(_cmd, 0, 1);
        }
    }

    /*
     * 获取打印机状态
     */
    public boolean getPrinterState(int timeout_read) {

        stateReset();
        if (!esc.getState(state, timeout_read))
            return false;
        if ((state[0] & STATE_NOPAPER_UNMASK) != 0) {
            isNoPaper = true;
        }
        if ((state[0] & STATE_BATTERYLOW_UNMASK) != 0) {
            isBatteryLow = true;
        }
        if ((state[0] & STATE_COVEROPEN_UNMASK) != 0) {
            isCoverOpen = true;
        }
        if ((state[0] & STATE_OVERHEAT_UNMASK) != 0) {
            isOverHeat = true;
        }
        if ((state[0] & STATE_PRINTING_UNMASK) != 0) {
            isPrinting = true;
        }
        return true;
    }

    /*
     * 获取是否支持JPL语言
     */
    public boolean getJPLsupport() {
        return param.jplSupport;
    }

    /*
     * 获取是否支持ESC语言
     */
    public boolean getESCsupport() {
        return param.escSupport;
    }

    /*
     * 获取是否支持CPCL语言
     */
    public boolean getCPCLsupport() {
        return param.cpclSupport;
    }

    /*
     * 获取打印机型号
     */
    public Printer_define.PRINTER_MODEL getModel() {
        return param.model;
    }
}

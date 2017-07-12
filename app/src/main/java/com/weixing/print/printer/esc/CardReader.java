package com.weixing.print.printer.esc;

import com.weixing.print.printer.PrinterParam;

public class CardReader extends BaseESC
{
    public CardReader(PrinterParam param) {
        super(param);
    }
    public enum CARD_ERROR
    {
        ERROR_OP_CMD(0xF3),//不支持的操作命令
        ERROR_ACK_DATA_ZERO(0xF4),
        ERROR_EXCUEE_FRAME(0xF5),
        ERROR_VERIFY_PASSWORD(0xF6),
        ERROR_OP_ERROR(0xF7),
        ERROR_NO_CARD(0xF8),
        ERROR_OP_READ(0xF9),
        ERROR_OP_WRITE(0xFA),
        ERROR_PARAM_LENGTH_MIN(0xFB),
        ERROR_PARAM_LENGTH_MAX(0xFC),
        ERROR_CHANNEL(0xFD), //通道错误
        ERROR_CARD_POWER_OFF(0xFE), //卡未上电
        ERROR_CARD_TYPE(0xFF);//卡类型错误
        private int _value;
        private CARD_ERROR(int mode)
        {
            _value = mode;
        }
        public int value()
        {
            return _value;
        }
    }

    public enum CARD_CMD
    {
        SET_CARD_TYPE(0xF3),
        RESET(0xF6),
        WRITE_READ(0xF7),
        CHECK_CARD(0xF8);
        private int _value;
        private CARD_CMD(int mode)
        {
            _value = mode;
        }
        public int value()
        {
            return _value;
        }
    }

    public enum CARD_TYPE_SUB_IC
    {
        CDT_CPU_T0,
        CDT_CPU_T1
    }

    private byte[] req = new byte[256 + 16];
    public byte[] rsp = new byte[256 + 16];
    public int rsp_len;

    /// 读卡模块上电
    /// 1)大卡上电不受此指令控制，大卡插入时自动上电，拔出时自动下电。
    public boolean powerOn()
    {
        int timeout = 3000;
        _port.flushReadBuffer();

        _cmd[0] = 0x1B;
        _cmd[1] = 0x17;
        if (!_port.write(_cmd, 0, 2))
        {
            return false;
        }
        if (!_port.read(rsp, 2, timeout))
        {
            return false;
        }
        if (rsp[0] == (byte)0xAA && rsp[1] == (byte)0xFE)
        {
            return true;
        }
        //Log.e("ali","error "+ (rsp[0])+" "+ (rsp[1]));
        return false;
    }
    /*
     * 读卡模块下电
     * 1)大卡上电不受此指令控制，大卡插入时自动上电，拔出时自动下电。
     */
    public boolean powerOff()
    {
        int timeout = 3000;
        _port.flushReadBuffer();

        _cmd[0] = 0x1B;
        _cmd[1] = 0x18;
        if (!_port.write(_cmd, 0, 2))
            return false;
        if (!_port.read(rsp, 2, timeout))
            return false;
        if ((rsp[0]&0xFF) == 0x55 && (rsp[1]&0xFF) == 0xFE)
        {
            return true;
        }
        return false;
    }

    private boolean send_and_wait(CARD_CMD cmd_flag,byte []req,int req_len,String from)
    {
        rsp_len = 0;
        int len = 1 + req_len;
        _port.flushReadBuffer();

        _cmd[0] = 0x1B;
        _cmd[1] = 0x1A;
        _cmd[2] = (byte)len;
        _cmd[3] = (byte)(len >> 8);
        _cmd[4] = (byte)cmd_flag.value();

        if (!_port.write(_cmd, 0, 5))
        {
            //Log.e("ali","cmd write error");
            return false;
        }
        if (req_len > 0)
        {
            if (!_port.write(req, 0, req_len))
            {
              //  Log.e("ali","req write error");
                return false;
            }
        }

        if (!_port.read(rsp, 1, 3000)) //read cmd_flag
        {
           // Log.e("ali","read cmd_flag error");
            return false;
        }
        if (rsp[0] != (byte)cmd_flag.value())
        {
            if (rsp[0] == (byte)0xFF)
            {
                //Log.e("ali","错误类型 " + rsp[0]);//MessageBox.Show();
                return false;
            }
            //Log.e("ali","cmd_flag error "+rsp[0] +" from:" +from);
            return false;
        }

        if (!_port.read(rsp, 1, 3000)) //read success flag
            return false;
        if (rsp[0] == 0x00) //命令成功，无返回数据
            return true;
        else if (rsp[0] == 0x01) //命令成功，有返回数据
        {
            if (!_port.read(rsp, 2, 3000)) //read data len
                return false;
            rsp_len = rsp[0] | (rsp[1] << 8);
            if (rsp_len > rsp.length)
            {
               // Log.e("ali","rsp 缓冲区太小 <" + rsp_len);//MessageBox.Show("rsp 缓冲区太小 <" + rsp_len.ToString());
                return false;
            }
            return _port.read(rsp, rsp_len, 3000); //read data
        }
        else if ((rsp[0]&0xFF) == 0xFF)//命令失败
        {
            if (!_port.read(rsp, 1, 3000)) //read success flag
                return false;
            return false;
        }
        return false;
    }

    /// 检测大卡座是否有卡插入
    public boolean checkBigCardInsert(byte[] state)
    {
        req[0] = 0;//暂时只支持channel 0
        if (!send_and_wait(CARD_CMD.CHECK_CARD, req, 1,"check card in"))
        {
           // Log.e("ali","send and wait error");
            return false;
        }
        if (rsp_len != 1)
            return false;
        state[0] = rsp[0];
        return true;
    }

    /// 设置卡类型
    /// 1)channel 1,2只支持CPU卡
    public boolean setCardType(int channel,ESC.CARD_TYPE_MAIN card_type_main,int card_type_sub)
    {
        req[0] = (byte)channel;
        req[1] = (byte)card_type_main.value();
        req[2] = (byte)card_type_sub;
        if (!send_and_wait(CARD_CMD.SET_CARD_TYPE, req, 3,"set card type"))
            return false;
        return true;
    }

    /// CPU卡复位
    public boolean cardCPU_Reset(int channel)
    {
        req[0] = (byte)channel;
        if (!send_and_wait(CARD_CMD.RESET, req, 1,"reset"))
            return false;
        return true;
    }

    /// 从CPU卡读数据
    /// 1)请求的命令需要放在ADPU中
    /// 2)返回的结果在read中
    public boolean cardCPU_Read(int channel, char[] ADPU, int adpu_len,
                                String from) {
        req[0] = (byte) channel;
        req[1] = 0;// read
        for (int i = 0; i < adpu_len; i++) {
            req[2 + i] = (byte) ADPU[i];
        }
        if (!send_and_wait(CARD_CMD.WRITE_READ, req, adpu_len + 2, from))
            return false;
        if (rsp_len == 2) {
            if ((rsp[0] & 0xFF) == 0x99 && rsp[1] == 0x61)
                return false;
        }
        return true;
    }


    /// 向CPU卡写数据
    /// 1)请求的命令及数据需要放在ADPU中
    /// 2)返回的结果在ret中
    /// 3)Select 等语句用此方法
    public boolean cardCPU_Write(int channel, char[] ADPU, int adpu_len,
                                 String from) {
        req[0] = (byte) channel;
        req[1] = 1;// write
        for (int i = 0; i < adpu_len; i++) {
            req[2 + i] = (byte) ADPU[i];
        }
        if (!send_and_wait(CARD_CMD.WRITE_READ, req, adpu_len + 2, from))
            return false;
        if (rsp_len == 0) {
            return false;
        } else if (rsp_len == 2) {
            if ((rsp[0] & 0xFF) == 0x99 && rsp[1] == 0x61)
                return false;
        }
        return true;
    }
}

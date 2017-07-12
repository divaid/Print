package com.weixing.print.printer.esc;

import com.weixing.print.printer.PrinterParam;
import com.weixing.print.printer.port.Port;

public class ESC
{
	public Text text;
	public Image image;
	public Graphic graphic;
	public Barcode barcode;
	public CardReader card_reader;

	private Port _port;
	private byte[] _cmd = { 0, 0, 0};
	private PrinterParam _param;

	public ESC(PrinterParam param) {
		_param = param;
		_port = _param.port;
		text = new Text(_param);
		image = new Image(_param);
		graphic = new Graphic(_param);
		barcode = new Barcode(_param);
		card_reader = new CardReader(_param);
	}

	public enum CARD_TYPE_MAIN {
		CDT_AT24Cxx(0x01), CDT_SLE44xx(0x11), CDT_CPU(0x21);
		private int _value;

		private CARD_TYPE_MAIN(int type) {
			_value = type;
		}

		public int value() {
			return _value;
		}
	};

	public  enum BAR_TEXT_POS
	{
		NONE,
		TOP,
		BOTTOM,
	}
	public  enum BAR_TEXT_SIZE
	{
		ASCII_12x24,
		ASCII_8x16,
	}

	public  enum BAR_UNIT
	{
		x1(1),
		x2(2),
		x3(3),
		x4(4);
		private int _value;
		private BAR_UNIT(int dots)
		{
			_value = dots;
		}
		public int value()
		{
			return _value;
		}
	}
	public static class LINE_POINT
	{
		public int startPoint;
		public int endPoint;
		public LINE_POINT(){};
		public LINE_POINT(int start_point, int end_point)
		{
			startPoint = (short)start_point;
			endPoint = (short)end_point;
		}
	}
	/*
	 * 枚举类型：文本放大方式
	 */
	public  enum 	TEXT_ENLARGE
	{
		NORMAL(0x00),                        //正常字符 
		HEIGHT_DOUBLE(0x01),                 //倍高字符
		WIDTH_DOUBLE(0x10),                  //倍宽字符
		HEIGHT_WIDTH_DOUBLE (0x11);           //倍高倍宽字符

		private int _value;
		private TEXT_ENLARGE(int mode)
		{
			_value = mode;
		}
		public int value()
		{
			return _value;
		}
	}
	/*
	 * 枚举类型：字体高度
	 */
	public  enum FONT_HEIGHT
	{
		x24,
		x16,
		x32,
		x48,
		x64,
	}

	public  enum IMAGE_MODE
	{
		SINGLE_WIDTH_8_HEIGHT(0x01),        //单倍宽8点高
		DOUBLE_WIDTH_8_HEIGHT(0x00),        //倍宽8点高
		SINGLE_WIDTH_24_HEIGHT(0x21),       //单倍宽24点高
		DOUBLE_WIDTH_24_HEIGHT(0x20);       //倍宽24点高
		private int _value;
		private IMAGE_MODE(final int mode)
		{
			_value = mode;
		}
		public int value()
		{
			return _value;
		}
	}
	public  enum IMAGE_ENLARGE
	{
		NORMAL,//正常
		HEIGHT_DOUBLE,//倍高 
		WIDTH_DOUBLE,//倍宽
		HEIGHT_WIDTH_DOUBLE	//倍高倍宽	
	}

	public boolean init()
	{
		_cmd[0] = 0x1B;
		_cmd[1] = 0x40;
		return _port.write(_cmd,0,2);
	}

	/*
	 * 唤醒打印机，并初始化
	 */
	public boolean wakeUp()
	{
		if(!_port.writeNULL())
			return false;
		try {Thread.sleep(50);} catch (InterruptedException e) {}
		return init();
	}

	public boolean getState(byte []ret,int timerout_read)
	{
		_port.flushReadBuffer();
		_cmd[0] = 0x10;
		_cmd[1] = 0x04;
		_cmd[2] = 0x05;
		if (!_port.write(_cmd,0,3))
			return false;
		if (!_port.read(ret, 2,timerout_read))
			return false;
		return true;
	}
	/*
	 * 换行回车
	 */
	public boolean feedEnter() {
		_cmd[0] = 0x0D;
		_cmd[1] = 0x0A;
		return _port.write(_cmd, 0, 2);
	}
	/*
	 * 走纸几行
	 * 输入参数:
	 * --int lines:几行
	 */
	public boolean feedLines(int lines) {
		_cmd[0] = 0x1B;
		_cmd[1] = 0x64;
		_cmd[2] = (byte) lines;
		return _port.write(_cmd, 0, 3);
	}
	/*
	 * 走纸几点
	 * 输入参数:
	 * --int dots:多少个点
	 */
	public boolean feedDots(int dots) {
		_cmd[0] = 0x1B;
		_cmd[1] = 0x4A;
		_cmd[2] = (byte) dots;
		return _port.write(_cmd, 0, 3);
	}
	public boolean lable_rignt_mark(){
		_cmd[0] = 0x0C;
		return _port.write(_cmd, 0, 1);
	}
	public boolean lable_left_mark(){
		_cmd[0] = 0x0E;
		return _port.write(_cmd, 0, 1);
	}
}



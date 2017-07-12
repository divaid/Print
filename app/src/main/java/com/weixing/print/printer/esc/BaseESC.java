package com.weixing.print.printer.esc;

import com.weixing.print.printer.PrinterParam;
import com.weixing.print.printer.Printer_define;
import com.weixing.print.printer.port.Port;


public class BaseESC {
	protected Port _port;
	protected byte[] _cmd = { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	protected Printer_define.PRINTER_MODEL _model;
	protected PrinterParam _param;

	public BaseESC(PrinterParam param)
	{
		_param = param;
		_model = _param.model;
		_port = _param.port;
	}

	/*
	 * 设置打印对象的x，y坐标
	 */
	public boolean setXY(int x, int y) {
		if (x < 0 || x >= _param.escPageWidth || x > 0x1FF) {
			return false;
		}

		if (y < 0 || y >= _param.escPageHeight || y > 0x7F) {
			return false;
		}

		int pos = ((x & 0x1FF) | ((y & 0x7F) << 9));
		_cmd[0] = 0x1B;
		_cmd[1] = 0x24;
		_cmd[2] = (byte) pos;
		_cmd[3] = (byte) (pos >> 8);
		_port.write(_cmd, 0, 4);
		return true;
	}
	/*
	 * 设置打印对象对齐方式
	 * 支持打印对象:文本(text),条码(barcode)
	 */
	public boolean setAlign(Printer_define.ALIGN align)
	{
		_cmd[0] = 0x1B;
		_cmd[1] = 0x61;
		_cmd[2] = (byte)align.ordinal();
		return _port.write(_cmd, 0, 3);
	}
	/*
	 * 设置行间距
	 */
	public boolean setLineSpace(int dots)
	{
		_cmd[0] = 0x1B;
		_cmd[1] = 0x33;
		_cmd[2] = (byte)dots;
		return _port.write(_cmd, 0, 3);
	}
	/*
	 * 初始化打印机
	 */
	public boolean init()
	{
		_cmd[0] = 0x1B;
		_cmd[1] = 0x40;
		return _port.write(_cmd,0,2);
	}

	/// <summary>
	/// 换行回车
	/// </summary>
	public boolean enter() {
		_cmd[0] = 0x0D;
		_cmd[1] = 0x0A;
		return _port.write(_cmd, 0, 2);
	}
	/**
	 *设置字符间距
	 *
	 * @return
	 */
	public boolean setCharSpace(int dots) {
		_cmd[0] = 0x1B;
		_cmd[1] = 0x20;
		_cmd[2] = (byte) dots;
		return _port.write(_cmd, 0, 3);
	}
}

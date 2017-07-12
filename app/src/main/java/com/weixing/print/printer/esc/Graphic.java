package com.weixing.print.printer.esc;

import com.weixing.print.printer.PrinterParam;


public class Graphic extends BaseESC {
	/*
	 * 构造函数
	 */
	public Graphic(PrinterParam param) {
		super(param);
	}

	/*
	 * 画线段
	 */
	public boolean linedrawOut(ESC.LINE_POINT[] line_points) {
		byte line_count = (byte) line_points.length;
		if (line_count > 8)
			return false;
		_cmd[0] = 0x1D;
		_cmd[1] = 0x27;
		_cmd[2] = line_count;
		if (!_port.write(_cmd, 0, 3))
			return false;
		byte[] data = { 0, 0, 0, 0 };
		for (int i = 0; i < line_count; i++) {
			if (line_points[i].startPoint < 0)
				line_points[i].startPoint = 0;
			if (line_points[i].startPoint >= _param.escPageWidth)
				line_points[i].startPoint = _param.escPageWidth - 1;
			data[0] = (byte) line_points[i].startPoint;
			data[1] = (byte) (line_points[i].startPoint >> 8);
			data[2] = (byte) line_points[i].endPoint;
			data[3] = (byte) (line_points[i].endPoint >> 8);
			if (!_port.write(data, 0, 4))
				return false;
		}
		return true;
	}

}

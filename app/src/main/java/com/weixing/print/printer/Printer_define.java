package com.weixing.print.printer;

public class Printer_define {
	/*
	 * 枚举类型：字体ID
	 */
	public  enum FONT_ID
	{
		ASCII_12x24(0x0000),
		ASCII_8x16(0x0001),
		ASCII_16x32(0x0003),
		ASCII_24x48(0x0004),
		ASCII_32x64(0x0005),
		GBK_24x24(0x0010),
		GBK_16x16(0x0011),
		GBK_32x32(0x0013),
		GB2312_48x48(0x0014);

		private int _value;
		private FONT_ID(int id)
		{
			_value = id;
		}
		public int value()
		{
			return _value;
		}
	}

	/*
	 * 枚举类型：打印机型号
	 */
	public  enum PRINTER_MODEL
	{
		VMP02,
		VMP02_P,
		JLP351,
		JLP351_IC,
		ULT113x,
		ULT1131_IC,
		EXP341,
	}

	/*
	 * 枚举类型：对齐方式
	 */
	public  enum ALIGN
	{
		LEFT,
		CENTER,
		RIGHT;
	}
}

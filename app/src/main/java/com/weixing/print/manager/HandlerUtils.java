package com.weixing.print.manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.weixing.application.PrintApplication;
import com.weixing.print.printer.JQPrinter;
import com.weixing.utils.ToastUtil;

public class HandlerUtils {

	public static boolean canShowDialog = true;
	public static void handle(final Context context, int type) {
		if(!canShowDialog){
			return;
		}
		switch (type) {
			case PrintExceptionState.PORTNOOPEN:
				showDialog(context,"连接未成功,请重新设置打印机！","connect failed");
				cancelPrint();
				break;
			case PrintExceptionState.BATTERYLOW:
				showDialog(context,"打印机电量过低！","batter low");
				break;
			case PrintExceptionState.OVERHEAT:
				showDialog(context,"打印机机头过热,请休息一会！","OVERHEAT");
				break;
			case PrintExceptionState.NOWARKUP:
				showDialog(context,"打印机唤醒未成功,请重新设置打印机！","warkup failed");
				cancelPrint();
				break;
			case PrintExceptionState.COVEROPEN:
				// 弹窗提示
				showDialog(context,"打印机纸仓盖未关闭！","COVEROPEN");
				break;
			case PrintExceptionState.NOPAPER:
				// 弹窗提示
				showDialog(context,"打印机缺纸！","NOPAPER");
				break;
			case PrintExceptionState.ISPRINTING:
				// mExpressPrintBtn.setText("打印机正在打印！");
				break;
			case PrintExceptionState.PRINTOVER:
				// mExpressPrintBtn.setText("打印");
				// mExpressPrintBtn.setClickable(true);
				break;
			case PrintExceptionState.NOSUPPORTJPL:
				showDialog(context,"不支持JLP，请设置正确的打印机型号！","NOSUPPORTJPL");
				break;
			case PrintExceptionState.STARTPRINT:
				showDialog(context, "正在打印中....","printing");
//				 mExpressPrintBtn.setText("正在打印");
				break;
			case PrintExceptionState.NOSUPPORTBLUETOOTH:
				showDialog(context,"该设备不支持蓝牙","NOSUPPORTBLUETOOTH");
				break;
			case PrintExceptionState.NOJQDEVICE:
				showDialog(context,"非指定的设备，可能存在乱码","NOJQDEVICE");
				break;
			case PrintExceptionState.DISCONNECT:
				ToastUtil.showLong("打印设备断开连接,请重新连接");
				cancelPrint();
				break;
			case PrintExceptionState.ENABLEPRINT:
				ToastUtil.showLong(PrintApplication.getInstance(), "连接成功，可打印了");
				break;
			case PrintExceptionState.CANCELDISCOVERY:
				break;
			case PrintExceptionState.CANCELPRINT:
				cancelPrint();
				break;
		}
	}

	private static void cancelPrint() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				JQPrinter printer = PrintApplication.getInstance().printer;
				if(printer != null){
					PrintApplication.getInstance().printer.close();
					PrintApplication.getInstance().printer = null;
//					RFApplication.getInstance().printer = new JQPrinter(Printer_define.PRINTER_MODEL.JLP351_IC);
				}
				PrintApplication.getInstance().btManager.cancelAllPairedDevices();
				PrintApplication.getInstance().currentPrinterAddress = null;
				PrintApplication.getInstance().currentPrinterInfo = null;
			}
		}).start();
	}

	public static void showDialog(Context context, String message,String mes2) {
		AlertDialog dialog = createDialog(context, "打印机", message, "确定",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				}, "", null);
		if (context instanceof Activity) {
			Activity activity = (Activity) context;
			if (!activity.isFinishing()) {
				dialog.show();
			}
		}
	}

	public static AlertDialog createDialog(Context context, String title,
										   String message, String confirmText,
										   DialogInterface.OnClickListener confirmClickListener,
										   String cancelText,
										   DialogInterface.OnClickListener cancelClickListener) {
		AlertDialog alertDialog = new AlertDialog.Builder(context)
				.setTitle(title).setMessage(message)
				.setPositiveButton(confirmText, confirmClickListener)
				.setNegativeButton(cancelText, cancelClickListener).create();
		return alertDialog;
	}
}

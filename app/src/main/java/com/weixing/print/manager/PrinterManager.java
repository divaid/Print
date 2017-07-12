package com.weixing.print.manager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import com.weixing.application.PrintApplication;
import com.weixing.print.printer.esc.ESC;
import com.weixing.utils.CreateCodeBitmap;
import com.weixing.utils.ToastUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * 打印管理类
 * Created by wb-cuiweixing on 2015/10/13.
 */
public class PrinterManager {

    private PrintFormater mPrintFormater;
    private ESC.LINE_POINT[] lines;
    //    private List<ParcelContainer> mModels;
    private Activity mActivity;

    public PrinterManager(Activity activity) {
        this.mPrintFormater = new PrintFormater();
        this.mActivity = activity;
    }

    public boolean init() {
        SimpleDateFormat formatter;
        if (!PrintApplication.getInstance().printer.wakeUp()) {
            HandlerUtils.handle(mActivity, PrintExceptionState.NOWARKUP);
            return false;
        }
        if (!getState()) {
            return false;
        }
        return true;
    }

    /**
     * final ParcelContainer mModel,
     *
     * @param pos
     * @param size
     * @throws IOException
     */
    public void printHeader(int pos, int size) throws IOException {
        //批次号  二维码
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

//                PrintApplication.getInstance().qrPic = createQR(mModel.batchCode + "");
                PrintApplication.getInstance().qrPic = createQR("hello world");
            }
        });
        thread.start();
        while (thread.isAlive()) ;
        mPrintFormater.feedDots(30);
        if (PrintApplication.getInstance().qrPic != null) {
            mPrintFormater.printImg_QR(PrintApplication.getInstance().qrPic);
        }
        mPrintFormater.feedDots(30);
//        mPrintFormater.headerFormat_48("批次号：" + mModel.batchCode);
        mPrintFormater.feedDots_20();
//        mPrintFormater.code128(mModel.batchCode + "");
//        mPrintFormater.feedDots(50);
//        mPrintFormater.headerFormat_48("批次名称：" + mModel.batchName);
        mPrintFormater.feedDots_20();
//        mPrintFormater.headerFormat_32("批次类型：" + mModel.batchProperty);
        mPrintFormater.feedDots_20();
//        mPrintFormater.headerFormat_32("包裹数量：" + mModel.parcelNum);
        mPrintFormater.feedDots_20();
//        mPrintFormater.headerFormat_32("包 裹 号: " + mModel.parcelCode);
        mPrintFormater.feedDots_20();
//        mPrintFormater.headerFormat_32("订单数量：" + mModel.orderNum);
        mPrintFormater.feedDots_25();
        mPrintFormater.drawLine();
        if (pos > 0) {
            mPrintFormater.feedDots(45);
        }

    }

    private Bitmap createQR(String str) {
        return CreateCodeBitmap.createQRImage(str, 230, 230);
    }

//    public void printOrder(ParcelSalesOrder parcelSalesOrder) throws IOException {
//        //订单头部信息
//        printOrderHeader(parcelSalesOrder);
//
//        //商品详情
//        List<ParcelSalesOrderDetail> salesOrderDetailResults = parcelSalesOrder.salesOrderDetailResults;
//        printOrderDetailTitle();
//        for (ParcelSalesOrderDetail mParcelSalesOrderDetail :salesOrderDetailResults) {
//            printOrderDetail(mParcelSalesOrderDetail);
//        }
//        mPrintFormater.feedDots_10();
//        //横线
//        mPrintFormater.drawLine();
//        //核算金额
//        printOrderMoney(parcelSalesOrder);
//    }

//    private void printOrderDetail(ParcelSalesOrderDetail parcelSalesOrderDetail) throws IOException {
//
//        mPrintFormater.feedDots_5();
//        mPrintFormater.orderDetail_24_InLine(0, 0, parcelSalesOrderDetail.itemName);
//        mPrintFormater.feedDots_5();
//        String buyNumber = parcelSalesOrderDetail.buyNumber;
//        String[] split = buyNumber.split(",");
//        int x = 0;
//        if(split[0].length() > 5){
//             x = 150 - (split[0].length() - 5) * 6;
//            mPrintFormater.orderDetail_24_InLine(x, 0,split[0]);
//        }else{
//            mPrintFormater.orderDetail_24_InLine(150, 0,split[0]);
//        }
//
//
//        mPrintFormater.orderDetail_24_InLine(320, 0, parcelSalesOrderDetail.itemUnitPrice);
//        mPrintFormater.orderDetail_24(480, 0, parcelSalesOrderDetail.amount + "");
//        if(split.length>1){
//            if(x > 0){
//                mPrintFormater.orderDetail_24(x, 0, split[1]);
//            }else{
//                mPrintFormater.orderDetail_24(150, 0, split[1]);
//            }
//        }
//
//    }

    private void printOrderDetailTitle() throws IOException {
        //商品
        mPrintFormater.drawLine();
        mPrintFormater.feedDots_10();
        mPrintFormater.orderDetail_24_Blod_InLine(0, 0, "商品名称");
        mPrintFormater.orderDetail_24_Blod_InLine(150, 0, "数量");
        mPrintFormater.orderDetail_24_Blod_InLine(320, 0, "单价");
        mPrintFormater.orderDetail_24_Blod(480, 0, "合计");
        mPrintFormater.feedDots_10();
        //横线
        mPrintFormater.drawLine();
    }

//    private void printOrderMoney(ParcelSalesOrder parcelSalesOrder) throws IOException {
//        mPrintFormater.feedDots_10();
//        mPrintFormater.orderMoney_32_Blod_InLine("订单应付金额：");
//        mPrintFormater.orderMoney_32(patchStr(parcelSalesOrder.totalOrderAmount + ""));
//        mPrintFormater.feedDots_5();
//        mPrintFormater.orderMoney_32_Blod_InLine("    优惠金额：");
//        mPrintFormater.orderMoney_32(patchStr(parcelSalesOrder.discountAmount + ""));
//        mPrintFormater.feedDots_5();
//        mPrintFormater.orderMoney_32_Blod_InLine("订单实付金额：");
//        mPrintFormater.orderMoney_32(patchStr(parcelSalesOrder.payOrderAmount + ""));
//        mPrintFormater.feedDots_5();
//        mPrintFormater.orderMoney_32_Blod_InLine("差额退款金额：");
//        mPrintFormater.orderMoney_32(patchStr(parcelSalesOrder.refundAmount + ""));
//        mPrintFormater.feedDots(17);
//        mPrintFormater.orderMoney_32_AlignCenter(" 如有差额退款，将会在签收后原路返回 支付宝账户，请注意查收。");
//        mPrintFormater.feedDots(13);
//        //横线
//        mPrintFormater.drawLine();
//    }
//
//    private void printOrderHeader(ParcelSalesOrder parcelSalesOrder) throws IOException {
//        mPrintFormater.feedDots_25();
//        //EasyLife
//        mPrintFormater.orderHeader_48_Blod_AlignCenter("盒马鲜生");
//        mPrintFormater.feedDots_25();
//        mPrintFormater.drawLine();
//        mPrintFormater.feedDots_15();
//        mPrintFormater.orderHeader_32_Blod(0, 0, "订单号： " + parcelSalesOrder.salesNumber);
//        mPrintFormater.feedDots_5();
//        //条形码
////        mPrintFormater.code128(parcelSalesOrder.salesNumber);
////        mPrintFormater.feedDots_25();
//        if (PrintApplication.getInstance().printer != null  //打包#1
//                && PrintApplication.getInstance().printer.esc != null
//                && PrintApplication.getInstance().printer.esc.text != null) {
//            PrintApplication.getInstance().printer.esc.text.printOutInLine(ALIGN.LEFT, ESC.FONT_HEIGHT.x32, true, ESC.TEXT_ENLARGE.NORMAL, "客  户：");
//        }
////        PrintApplication.getInstance().printer.esc.text.printOutInLine(ALIGN.LEFT, ESC.FONT_HEIGHT.x32, true, ESC.TEXT_ENLARGE.NORMAL, "客  户：");
//        mPrintFormater.orderHeader_24(150, 0, parcelSalesOrder.receiverName);
//        mPrintFormater.feedDots_5();
//        mPrintFormater.orderHeader_32_Blod_InLine(0, 0, "电  话：");
//        mPrintFormater.orderHeader_24(150, 0, parcelSalesOrder.receiverTel + "");
//        mPrintFormater.feedDots_5();
//        mPrintFormater.orderHeader_32_Blod_InLine(0, 0, "地  址：");
//        mPrintFormater.orderHeader_24(150, 0, parcelSalesOrder.postAddress + "");
//        mPrintFormater.feedDots_20();
//
//    }
//
//    public void printTail(ParcelSalesOrder parcelSalesOrder) throws IOException {
//        mPrintFormater.feedDots(18);
//        //放置客服电话或者门店广告语
//        mPrintFormater.orderHeader_32_Blod_AlignCenter("欢迎光临盒马鲜生体验店");
//        mPrintFormater.feedDots_10();
//
//        //虚线
//        mPrintFormater.drawDashLine();
//        mPrintFormater.feedDots_20();
//
//        //客户签字
//        mPrintFormater.headerFormat_48("订 单 号："+parcelSalesOrder.salesNumber);
//        mPrintFormater.headerFormat_48("客户签字：");
//        mPrintFormater.feedDots(175);
//
//        //虚线
//        mPrintFormater.drawDashLine();
//        mPrintFormater.feedDots(13);
//    }


    /**
     * 获取状态
     *
     * @return
     */
    public boolean getState() {
        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(500); //因为缓冲区怕数据接收不完，所以留一点时间去等待
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!PrintApplication.getInstance().printer.getPrinterState(2000))//此处的读超时需要算上打印内容的时间。请根据打印内容调整,如果你打印的内容更多，就需要设置更多的时间。
            {
                ToastUtil.showLong("获取打印机状态失败");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            if (PrintApplication.getInstance().printer.isBatteryLow) {
                HandlerUtils.handle(mActivity, PrintExceptionState.BATTERYLOW);
                //电池低，只提示，不禁止
//                return false;
            }
            if (PrintApplication.getInstance().printer.isOverHeat) {
                HandlerUtils.handle(mActivity, PrintExceptionState.OVERHEAT);
                return false;
            }
            if (PrintApplication.getInstance().printer.isCoverOpen) {
                HandlerUtils.handle(mActivity, PrintExceptionState.COVEROPEN);
                return false;
            } else if (PrintApplication.getInstance().printer.isNoPaper) {
                HandlerUtils.handle(mActivity, PrintExceptionState.NOPAPER);
                return false;
            }
            if (PrintApplication.getInstance().printer.isPrinting) {
                HandlerUtils.handle(mActivity, PrintExceptionState.STARTPRINT);
                return false;
            } else {
                break;
            }

        }
        return true;
    }

    // 解决左右对齐的方法  最多位数定位：12位（包括.00） 千万
    private String patchStr(String str) {
        StringBuffer buffer = new StringBuffer();
        int length = str.length();
        for (int i = length; i < 12; i++) {
            buffer.append(" ");
        }
        buffer.append(str);
        return buffer.toString();
    }

    public void printAll(String msg) throws IOException {
//            mModels = parcelContainers;
//            for(int i = 0; i < mModels.size(); i++){
//                ParcelContainer mModel = mModels.get(i);
//                //批次
//                printHeader(mModel,i,mModels.size());
//                //订单
//                List<ParcelSalesOrder> salesOrderResults = mModel.salesOrderResults;
//                for (ParcelSalesOrder parcelSalesOrder :salesOrderResults) {
//                    printOrder(parcelSalesOrder);
//                    printTail(parcelSalesOrder);
//                }
//            }
            /*for (ParcelContainer mModel :mModels) {
                //批次
                printHeader(mModel);
                //订单
                List<ParcelSalesOrder> salesOrderResults = mModel.salesOrderResults;
                for (ParcelSalesOrder parcelSalesOrder :salesOrderResults) {
                    printOrder(parcelSalesOrder);
                    printTail(parcelSalesOrder);
                }
            }*/


    }

    public void printImage(Bitmap image) throws IOException {
        PrintApplication.getInstance().qrPic = image;
        mPrintFormater.feedDots(30);
        if (PrintApplication.getInstance().qrPic != null) {
            mPrintFormater.printImg_QR(PrintApplication.getInstance().qrPic);
        }
        mPrintFormater.feedDots(45);
        mPrintFormater.drawLine();
        mPrintFormater.feedDots_20();
    }

    /**
     * 打印二维码
     *
     * @param msg 要打印的内容
     * @throws IOException
     */
    public void printQR(final String msg) throws IOException {
        Bitmap image = createQR(msg);
        PrintApplication.getInstance().qrPic = image;
        mPrintFormater.feedDots(30);
        if (PrintApplication.getInstance().qrPic != null) {
            mPrintFormater.printImg_QR(PrintApplication.getInstance().qrPic);
        }
        mPrintFormater.feedDots(45);
        mPrintFormater.feedDots_20();
    }

    public void printBarCode(final Context context, final String msg) throws IOException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                PrintApplication.getInstance().qrPic = createBarCode(context, msg);
            }
        });
        thread.start();
        while (thread.isAlive()) ;
        mPrintFormater.feedDots(30);
        if (PrintApplication.getInstance().qrPic != null) {
            mPrintFormater.printImg_QR(PrintApplication.getInstance().qrPic);
        }
        mPrintFormater.feedDots(45);
//        mPrintFormater.code128(msg);
        mPrintFormater.feedDots_20();
    }

    private Bitmap createBarCode(Context context, String str) {
        if (str.length() < 3) {
            return CreateCodeBitmap.creatBarcode(context, str, 270, 100, false);
        } else if (str.length() < 5) {
            return CreateCodeBitmap.creatBarcode(context, str, 320, 100, false);
        } else if (str.length() < 7) {
            return CreateCodeBitmap.creatBarcode(context, str, 350, 100, false);
        } else if (str.length() < 9) {
            return CreateCodeBitmap.creatBarcode(context, str, 380, 100, false);
        } else if (str.length() < 11) {
            return CreateCodeBitmap.creatBarcode(context, str, 410, 100, false);
        } else if (str.length() < 13) {
            return CreateCodeBitmap.creatBarcode(context, str, 435, 100, false);
        } else if (str.length() < 15) {
            return CreateCodeBitmap.creatBarcode(context, str, 470, 100, false);
        } else if (str.length() < 17) {
            return CreateCodeBitmap.creatBarcode(context, str, 500, 100, false);
        } else if (str.length() < 19) {
            return CreateCodeBitmap.creatBarcode(context, str, 520, 100, false);
        } else {
            return CreateCodeBitmap.creatBarcode(context, str, 550, 100, false);
        }
    }
}

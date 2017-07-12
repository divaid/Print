package com.weixing.print.manager;


import android.graphics.Bitmap;
import com.weixing.application.PrintApplication;
import com.weixing.print.printer.JQPrinter;
import com.weixing.print.printer.Printer_define.ALIGN;
import com.weixing.print.printer.esc.ESC;

import java.io.IOException;

/**
 * 适用当前模板的格式
 * Created by wb-cuiweixing on 2015/10/14.
 */
public class PrintFormater {
    private JQPrinter mPrinter = null;
    private ESC.LINE_POINT[] lines;

    public PrintFormater() {
        mPrinter = PrintApplication.getInstance().printer;
        lines = new ESC.LINE_POINT[1];
        lines[0] = new ESC.LINE_POINT(0, 575);
    }

    /**
     * 走几个点
     *
     * @param dots
     */
    public void feedDots(int dots) throws IOException {
        mayException(mPrinter.esc.feedDots(dots));
    }

    public void feedDots_5() throws IOException {
         feedDots(5);
    }

    public void feedDots_10() throws IOException {
         feedDots(10);
    }

    public void feedDots_20() throws IOException {
         feedDots(20);
    }

    public void feedDots_25() throws IOException {
         feedDots(25);
    }

    public void feedDots_15() throws IOException {
         feedDots(15);
    }


    /**
     * 条形码
     */
    public void code128(String str)throws IOException {
        mayException(!mPrinter.esc.barcode.code128_auto_drawOut(ALIGN.CENTER, ESC.BAR_UNIT.x3, 90, ESC.BAR_TEXT_POS.NONE, ESC.BAR_TEXT_SIZE.ASCII_8x16, str));

    }

    /**
     * 二维码
     * @param str
     * @throws IOException
     */
    public void bardCode2D_AlignRight(String str) throws IOException {
        mayException(mPrinter.esc.barcode.barcode2D_QRCode_Align(ALIGN.RIGHT, ESC.BAR_UNIT.x4, 0, 2, str));
    }
    public void bardCode2D_AlignCenter(String str) throws IOException {
        mayException(mPrinter.esc.barcode.barcode2D_QRCode_Align(ALIGN.CENTER, ESC.BAR_UNIT.x4, 0, 2, str));
    }

    public void bardCode2D(int x,int y,String str) throws IOException {
        mayException(mPrinter.esc.barcode.barcode2D_QRCode(x, y, ESC.BAR_UNIT.x4, 0, 2, str));
    }


    /**
     * 头部批次字号 32
     *
     * @param str
     */
    public void headerFormat_32(String str) throws IOException {

        mayException(mPrinter.esc.text.printOut(ALIGN.LEFT, ESC.FONT_HEIGHT.x32, true, ESC.TEXT_ENLARGE.NORMAL, str));
    }

  /**
     * 头部批次字号 48
     *
     * @param str
     */
    public void headerFormat_48(String str) throws IOException {

        mayException(mPrinter.esc.text.printOut(ALIGN.LEFT, ESC.FONT_HEIGHT.x48, true, ESC.TEXT_ENLARGE.NORMAL, str));
    }
    /**
     * 头部批次字号 48  行内
     *
     * @param str
     */
    public void headerFormat_48_InLine(String str) throws IOException {

        mayException(mPrinter.esc.text.printOutInLine(ALIGN.LEFT, ESC.FONT_HEIGHT.x48, true, ESC.TEXT_ENLARGE.NORMAL, str));
    }
/**
     * 头部批次字号 48  行内
     *
     * @param str
     */
    public void headerFormat_48_InLine(int x,int y,String str) throws IOException {

        mayException(mPrinter.esc.text.printOutInLine(x,y, ESC.FONT_HEIGHT.x48, true, ESC.TEXT_ENLARGE.NORMAL, str));
    }


    /**
     * 画横线
     */
    public void drawLine() throws IOException {
        for (int i = 0; i < 4; i++) {
            mayException(mPrinter.esc.graphic.linedrawOut(lines));
        }

    }

    /**
     * 画虚线
     * @throws IOException
     */
    public void drawDashLine() throws IOException {
        mayException(mPrinter.esc.text.printOut("-----------------------------------------------"));
    }

    public void printOut(String str) throws IOException {
        mayException(mPrinter.esc.text.printOut(str));
    }

    /**
     * 订单头部样式 32号粗体字 居中显示
     *
     * @param str
     */
    public void orderHeader_32_Blod_AlignCenter(String str)throws IOException {
        mayException(mPrinter.esc.text.printOut(ALIGN.CENTER, ESC.FONT_HEIGHT.x32, true, ESC.TEXT_ENLARGE.NORMAL, str));
    }
    /**
     * 订单头部样式 48号粗体字 居中显示
     *
     * @param str
     */
    public void orderHeader_48_Blod_AlignCenter(String str)throws IOException {
        mayException(mPrinter.esc.text.printOut(ALIGN.CENTER, ESC.FONT_HEIGHT.x48, true, ESC.TEXT_ENLARGE.NORMAL, str));
    }
    /**
     * 订单头部样式 32号粗体字
     *
     * @param x
     * @param y
     * @param str
     */
    public void orderHeader_32_Blod(int x, int y, String str)throws IOException {
        mayException(mPrinter.esc.text.printOut(x, y, ESC.FONT_HEIGHT.x32, true, ESC.TEXT_ENLARGE.NORMAL, str));
    }

    /**
     * 订单头部样式 32号粗体字 不换行
     *
     * @param x
     * @param y
     * @param str
     */
    public void orderHeader_32_Blod_InLine(int x, int y, String str)throws IOException {

         mayException(mPrinter.esc.text.printOutInLine(x, y, ESC.FONT_HEIGHT.x32, true, ESC.TEXT_ENLARGE.NORMAL, str));
    }

    /**
     * 订单头部样式 24号字
     *
     * @param x
     * @param y
     * @param str
     */
    public void orderHeader_24(int x, int y, String str)throws IOException {
         mayException(mPrinter.esc.text.printOut(x, y, ESC.FONT_HEIGHT.x24, false, ESC.TEXT_ENLARGE.NORMAL, str));
    }


    /**
     * 订单详情样式  24号粗体字 不换行
     *
     * @param x
     * @param y
     * @param str
     */
    public void orderDetail_24_Blod_InLine(int x, int y, String str)throws IOException {
        mayException(mPrinter.esc.text.printOutInLine(x, y, ESC.FONT_HEIGHT.x24, true, ESC.TEXT_ENLARGE.NORMAL, str));

    }

    /**
     * 订单详情样式 24号粗体字 换行
     *
     * @param x
     * @param y
     * @param str
     */
    public void orderDetail_24_Blod(int x, int y, String str)throws IOException {
        mayException(mPrinter.esc.text.printOut(x, y, ESC.FONT_HEIGHT.x24, true, ESC.TEXT_ENLARGE.NORMAL, str));
    }

    /**
     * 订单详情样式 24号 换行
     *
     * @param x
     * @param y
     * @param str
     */
    public void orderDetail_24(int x, int y, String str)throws IOException {
         mayException(mPrinter.esc.text.printOut(x, y, ESC.FONT_HEIGHT.x24, false, ESC.TEXT_ENLARGE.NORMAL, str));
    }

    /**
     * 订单详情样式 24号 不换行
     *
     * @param x
     * @param y
     * @param str
     */
    public void orderDetail_24_InLine(int x, int y, String str)throws IOException {
         mayException(mPrinter.esc.text.printOutInLine(x, y, ESC.FONT_HEIGHT.x24, false, ESC.TEXT_ENLARGE.NORMAL, str));
    }

    /**
     * 订单金额样式 32 粗体 不换行 （左边固定文字）
     *
     * @param str
     */
    public void orderMoney_32_Blod_InLine(String str)throws IOException {
        mayException(mPrinter.esc.text.printOutInLine(0, 0, ESC.FONT_HEIGHT.x32, true, ESC.TEXT_ENLARGE.NORMAL, str));
    }

    /**
     * 订单金额样式 32 粗体 换行 （右边具体金额）
     *
     * @param str
     */
    public void orderMoney_32(String str)throws IOException {
        mayException(mPrinter.esc.text.printOut(360, 0, ESC.FONT_HEIGHT.x32, false, ESC.TEXT_ENLARGE.NORMAL, str));
    }

    /**
     * 订单头部样式 32号粗体字 居中显示
     *
     * @param str
     */
    public void orderMoney_32_AlignCenter(String str)throws IOException {
        mayException(mPrinter.esc.text.printOut(ALIGN.CENTER, ESC.FONT_HEIGHT.x32, false, ESC.TEXT_ENLARGE.NORMAL, str));
    }


    /**
     * 打印二维码图片
     * @param qr
     */
    public void printImg_QR(Bitmap qr) {
        mPrinter.esc.image.drawOut_align(ALIGN.CENTER, qr);
    }

    /**
     * 该方法：添加异常,方便异步线程处理异常
     * @param bol
     * @throws IOException
     */
    public void mayException(boolean bol)throws IOException{
        /*if(!bol){
            throw new IOException();
        }*/
    }
}

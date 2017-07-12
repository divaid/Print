package com.weixing.print.printer.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

/// <summary>
/// 图像转换类
/// </summary>
public class ImageConvert {
    private int width;

    /// <summary>
    /// 图像宽度
    /// </summary>
    public int getWidth() {
        return width;
    }

    private int height;

    /// <summary>
    /// 图像高度
    /// </summary>
    public int getHeight() {
        return height;
    }

    /// <summary>
    /// 构造函数
    /// </summary>
    public ImageConvert() {
        width = 0;
        height = 0;
    }

    /// <summary>
    /// 根据阈值判断是否为黑点。
    /// 1)值由RGB分量获得
    /// </summary>
    /// <param name="dot_color">点的颜色@param
    /// <param name="gray_threshold">阈值@param
    /// <returns>true:为黑点；false:为黑点</returns>
    private boolean PixelIsBlack(int color, int gray_threshold) {
        int red = ((color & 0x00FF0000) >> 16);
        int green = ((color & 0x0000FF00) >> 8);
        int blue = color & 0x000000FF;
        int grey = (int) ((float) red * 0.299 + (float) green * 0.587 + (float) blue * 0.114);
        return (grey < gray_threshold);
    }

    /// <summary>
    /// 垂直方式转换图像为数组
    /// </summary>
    /// <param name="bitmap">图像@param
    /// <param name="gray_threshold">阈值@param
    /// <returns>null表示失败，否则返回数据数组</returns>
    public byte[] CovertImageVertical(Bitmap bitmap, int gray_threshold, int column_dots) {
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        int count = (height - 1) / column_dots + 1;//分成多少个小图片
        int column_bytes = column_dots / 8;
        byte[] data = new byte[width * column_bytes * count];

        int index = 0;
        //根据位图各点的灰度值，确定打印位图相应的点的黑白色
        int sx = 0, sy = 0;                                                                          //位图的x，y坐标值；
        for (int i = 0; i < count; i++)                                                     //8行像素行构成以小行，一幅位图需要LengthColumn个小行；
        {
            for (int j = 0; j < width; j++)                                     //一小行的位图数据有BmpWidth列，需要BmpWidth个字节存放；
            {
                sx = j;                                                                             //位图当前像素点的x坐标为列数x；
                for (int k = 0; k < 8; k++)                                                        //k，小行中8行像素行的当前行；
                {
                    sy = (i << 3) + k;                                                              //位图当前像素点的y坐标为(小行数×8)+k;
                    if (sy >= height)                                            //如果位图当前像素点的y坐标大于实际位图高度(位图实际高度可能不为8的整数倍)，不对该点颜色进行判断；
                    {
                        continue;
                    } else {
                        if (PixelIsBlack(bitmap.getPixel(sx, sy), gray_threshold))                    //判断当前点是否为黑色；
                        {
                            data[index] |= (byte) (0x01 << (7 - k));      //如果为黑色，当前点所对应字节的对应为赋值为1；
                        }
                    }
                }
                index++;                                                      //一小行的一列像素点判断完毕后，位图数据实际长度加1；
            }
        }
        return data;
    }

    /// <summary>
    /// 垂直方式转换图像为数组
    /// </summary>
    /// <param name="image_path">图像路径@param
    /// <param name="gray_threshold">阈值@param
    /// <returns>null表示失败，否则返回数据数组</returns>
    public byte[] CovertImageVertical(String image_path, int gray_threshold) {
        if (new File(image_path).exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(image_path);
            byte[] data = CovertImageVertical(bitmap, gray_threshold, 8);
            if (data == null) {
                //Log.e("ali", "转换文件失败");
            }
            return data;
        } else {
            //Log.e("ali", "文件路径错误:" + image_path);
            return null;
        }
    }

    /// <summary>
    /// 水平方式转换图像为数组
    /// </summary>
    /// <param name="bitmap">图像@param
    /// <param name="gray_threshold">阈值@param
    /// <returns>null表示失败，否则返回数据数组</returns>
    public byte[] CovertImageHorizontal(Bitmap bitmap, int gray_threshold) {
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        int BytesPerLine = (width - 1) / 8 + 1;

        byte[] data = new byte[BytesPerLine * height];

        int index = 0;

        //根据位图各点的灰度值，确定打印位图相应的点的黑白色
        int x = 0, y = 0;                                                                          //位图的x，y坐标值；
        for (int i = 0; i < height; i++)                                        //每次判断一像素行，需要判断BmpHeight次
        {
            for (int j = 0; j < BytesPerLine; j++)                                                    //每行需要LengthRow字节存放数据，
            {
                for (int k = 0; k < 8; k++)                                                        //每个字节存放8个点，即1点1位；
                {
                    x = (j << 3) + k;                                                              //x坐标为已计算字节×8+当前字节的位k；
                    y = i;                                                                         //当前行；
                    if (x >= width)                                             //如果位图当前像素点的y坐标大于实际位图宽度(位图实际宽度可能不为8的整数倍)，不对该点颜色进行判断；
                    {
                        continue;
                    } else {
                        if (PixelIsBlack(bitmap.getPixel(x, y), gray_threshold)) {
                            data[index] |= (byte) (0x01 << k);
                        }
                    }
                }
                index++;                                                       //一行像素行8点判断完毕后，位图数据实际长度加1；
            }
            x = 0;
        }

        return data;
    }

    /// <summary>
    /// 水平方式转换图像为数组
    /// </summary>
    /// <param name="image_path">图像路径@param
    /// <param name="gray_threshold">阈值@param
    /// <returns>null表示失败，否则返回数据数组</returns>
    public byte[] CovertImageHorizontal(String image_path, int gray_threshold) {
        if (new File(image_path).exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(image_path);
            byte[] data = CovertImageHorizontal(bitmap, gray_threshold);
            if (data == null) {
                // Log.e("ali","转换文件失败");
            }
            return data;
        } else {
            //Log.e("ali","文件路径错误:" + image_path);
            return null;
        }
    }
}
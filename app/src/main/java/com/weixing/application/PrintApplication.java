package com.weixing.application;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.weixing.application.inter.IInitializeComponent;
import com.weixing.print.manager.BluetoothDeviceManager;
import com.weixing.print.printer.JQPrinter;
import com.weixing.print.receiver.FoundBroadcastReceiver;
import com.weixing.print.receiver.LoseConnReceiver;
import com.weixing.utils.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by wb-cuiweixing on 2016/12/8.
 */
public class PrintApplication extends Application implements IInitializeComponent.IInitFinishListener {
    private static PrintApplication app;
    private FoundBroadcastReceiver mFoundBroadcastReceiver;
    private LoseConnReceiver mLoseConnReceiver;


    public static PrintApplication getInstance() {

        return app;

    }

    @Override
    public void onCreate() {
        super.onCreate();
//        instance = this;
        app = this;

    }

    public void initBluetooth() {
        BluetoothAdapter mbtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mbtAdapter == null) {
            ToastUtil.showLong(this, "本机没有找到蓝牙硬件或驱动！");
            return;
        }

        //搜索广播
        PrintApplication.getInstance().btAdapter = mbtAdapter;
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mFoundBroadcastReceiver = new FoundBroadcastReceiver();
        registerReceiver(mFoundBroadcastReceiver, filter);
        PrintApplication.getInstance().foundBroadcastReceiver = mFoundBroadcastReceiver;

        //断连广播
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);//蓝牙断开
        mLoseConnReceiver = new LoseConnReceiver(this);
        registerReceiver(mLoseConnReceiver, filter2);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


    //print
    public BluetoothAdapter btAdapter = null;
    public JQPrinter printer = null;
    public BluetoothDeviceManager btManager = null;
    public String currentPrinterAddress = null;
    public String currentPrinterInfo = null;
    public Bitmap qrPic = null;
    public int enterPackagingActivityNum = 0;
    public boolean isPackingActivityDestory = false;
    public FoundBroadcastReceiver foundBroadcastReceiver;

    public String new_url = "";
    public int downloadstate = 0;//0 空闲 1下载
    public DownloadApkThread downThread = new DownloadApkThread();

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError() {

    }

    /**
     * 下载文件线程
     *
     * @author Administrator
     */
    public class DownloadApkThread extends Thread {

        @Override
        public void run() {
            try {
                downloadstate = 1;
                //判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    // 获取SDCard的路径
                    String sdpath = Environment.getExternalStorageDirectory() + "/";
                    String mSavePath = sdpath + "download";
                    URL url = new URL(new_url);
                    // 创建连接
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    // 获取文件大小
                    int length = conn.getContentLength();
                    // 创建输入流
                    InputStream is = conn.getInputStream();

                    File file = new File(mSavePath);
                    // 如果文件不存在，新建目录
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, "RF.apk");
                    if (apkFile.exists())
                        apkFile.delete();
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    // 缓存
                    byte buf[] = new byte[1024];
                    // 写入到文件中
                    do {
                        int numread = is.read(buf);
                        count += numread;
                        if (numread <= 0) {
                            // 下载完成
                            File apkfile = new File(apkFile.getPath());
                            if (!apkfile.exists()) {
                                return;
                            }
                            checkUnInstallVersion(apkfile.toString());

                            break;

                        }
                        // 写入文件
                        fos.write(buf, 0, numread);
                    } while (true);
                    fos.close();
                    is.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    public PackageManager pm = null;
    public int apkVersion = 1;//机子现在有版本

    /*获取下载包的 版本号 如果高于服务器版本不下载*/
    private void checkUnInstallVersion(String apkpath) {
        PackageInfo info = pm.getPackageArchiveInfo(apkpath, 0);
        Log.e("Application", info.versionCode + "----" + info.versionName);
        if (apkVersion > info.versionCode) {
//            ToastUtil.showLong(this, "下载的版本低于现在版本,安装失败");
            Looper.prepare();
            Toast.makeText(getApplicationContext(),"下载的版本低于现在版本,安装失败",Toast.LENGTH_LONG).show();
            Looper.loop();
        } else {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.parse("file://" + apkpath.toString()), "application/vnd.android.package-archive");
            Log.e("Application", apkpath.toString());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            getInstance().startActivity(i);
        }
        downloadstate = 0;
        downThread = new DownloadApkThread();
    }
}

package com.weixing.print.printer.port;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * 本地蓝牙
 */
public class Port {

    private byte[] _cmd = {0};
    private BluetoothAdapter btAdapter;
    private BluetoothSocket mmBtSocket;
    private String btDeviceString;
    private OutputStream mmOutStream = null;
    private InputStream mmInStream = null;

    public boolean isOpen = false;

    public Port() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    //when the object is destroyed , free resources.
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    /*
     *  等待蓝牙状态直到STATE_ON。
     */
    public boolean getBluetoothStateON(int timeout) {
        for (int loop = timeout / 50; loop > 0; loop--) {
            int r = btAdapter.getState();
            if (r == BluetoothAdapter.STATE_ON)
                return true;
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
            }
        }
        return false;
    }

    public boolean open(String strBtAddr, int timeout) {
        isOpen = false;
        if (strBtAddr == null) return false;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        btDeviceString = strBtAddr;
        if (timeout < 1000)
            timeout = 1000;
        if (timeout > 6000)
            timeout = 6000;

        long start_time = SystemClock.elapsedRealtime();
        for (; ; ) {
            if (BluetoothAdapter.STATE_ON == btAdapter.getState()) {
                break;
            }
            if (SystemClock.elapsedRealtime() - start_time > timeout) {
                Log.e("ali", "adapter state on timeout");
                return false;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        BluetoothSocket TmpSock = null;
        try {
            BluetoothDevice btDevice = btAdapter.getRemoteDevice(btDeviceString);
            TmpSock = btDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            int deviceType = btDevice.getBluetoothClass().getMajorDeviceClass();
            Log.e("susei", "susei deviceType是：" + deviceType);
        } catch (Exception ex) {
            TmpSock = null;
            isOpen = false;
            return false;
        }
        mmBtSocket = TmpSock;

        start_time = SystemClock.elapsedRealtime();
        for (; ; ) {
            try {
                mmBtSocket.connect();
            } catch (Exception ex) {
                ex.printStackTrace();
                if (SystemClock.elapsedRealtime() - start_time > timeout) {
                        try {
                        mmBtSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    isOpen = false;
                    return false;
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            break;
        }

        try {
            mmOutStream = mmBtSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mmInStream = mmBtSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isOpen = true;
        return true;
    }

    public boolean close() {
        if (mmBtSocket == null) {
            isOpen = false;
            Log.e("ali", "mmBtSocket null");
            return false;
        }
        if (isOpen) {
            try {
                if (mmOutStream != null) {
                    mmOutStream.close();
                    mmOutStream = null;
                }
                if (mmInStream != null) {
                    mmInStream.close();
                    mmOutStream = null;
                }
                mmBtSocket.close(); //SB close会使Socket无效，必须下次使用必须再次createRfcommSocketToServiceRecord来创建
            } catch (Exception ex) {
                isOpen = false;
                return false;
            }
        }
        isOpen = false;
        mmBtSocket = null;
        return true;
    }

    public boolean flushReadBuffer() {
        byte[] buffer = new byte[64];
        if (!isOpen)
            return false;
        while (true) {
            int r = 0;
            try {
                //TODO  这里可能是流问题
              /*  String brand = Build.BRAND;
                if(!brand.equals("alps")){//非辰达设备
                    r= mmInStream.available();
                }*/
                r= mmInStream.available();
                if (r == 0) break;
                if (r > 0) {
                    if (r > 64) r = 64;
                    mmInStream.read(buffer, 0, r);
                }
            } catch (IOException e) {
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {

            }

        }
        isOpen = true;
        return true;
    }

    public boolean write(byte[] buffer, int offset, int length) {
        if (!isOpen)
            return false;
        if (mmBtSocket == null) {
            Log.e("ali", "mmBtSocket null");
            return false;
        }
        if (mmOutStream == null) {
            return false;
        }
        try {
            mmOutStream.write(buffer, offset, length);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        isOpen = true;
        return true;
    }

    public boolean writeNULL() {
        _cmd[0] = 0;
        return write(_cmd, 0, 1);
    }

    public boolean write(char[] data, int offset, int length, int size) {
        if (size == 1) {
            for (int i = offset; i < offset + length; i++) {
                _cmd[0] = (byte) data[i];
                write(_cmd, 0, 1);
            }
        } else {
            for (int i = offset; i < offset + length; i++) {
                _cmd[0] = (byte) data[i];
                _cmd[1] = (byte) (data[i] >> 8);
                write(_cmd, 0, 2);
            }
        }
        return true;
    }

    public boolean write(String text) {
        byte[] data = null;
        try {
            data = text.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            Log.e("ali", "Sting getBytes('GBK') failed");
            return false;
        }
        if (!write(data, 0, data.length))
            return false;
        return writeNULL();
    }

    public boolean read(byte[] buffer, int offset, int length, int timeout_read) {
        if (!isOpen)
            return false;
        if (timeout_read < 200) timeout_read = 200;
        if (timeout_read > 5000) timeout_read = 5000;

        try {
            long start_time = SystemClock.elapsedRealtime();
            long cur_time = 0;
            int need_read = length;
            int cur_readed = 0;
            for (; ; ) {
                if (mmInStream.available() > 0) {
                    cur_readed = mmInStream.read(buffer, offset, need_read);
                    offset += cur_readed;
                    need_read -= cur_readed;
                }
                if (need_read == 0) {
                    break;
                }
                cur_time = SystemClock.elapsedRealtime();
                if (cur_time - start_time > timeout_read) {
                    Log.e("ali", "read timeout");
                    return false;
                }
                Thread.sleep(20);
            }
        } catch (Exception ex) {
            Log.e("ali", "read exception");
            close();
            return false;
        }
        return true;
    }

    public boolean read(byte[] buffer, int length, int timeout_read) {
        if (length > buffer.length)
            return false;
        return read(buffer, 0, length, timeout_read);
    }
}

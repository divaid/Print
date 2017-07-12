package com.weixing.print.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.weixing.application.PrintApplication;
import com.weixing.print.R;
import com.weixing.print.utils.PrintUtils;

import java.util.ArrayList;

/**
 * Created by wb-cuiweixing on 2015/10/24.
 */
public class AvailableAdapter extends BaseAdapter {

    private ArrayList<String> list;
    private Activity activity;

    public AvailableAdapter(ArrayList list,Activity activity) {
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = View.inflate(activity, R.layout.list_item_print_paired, null);
        TextView tv_pairedDevice = (TextView) view.findViewById(R.id.tv_pairedDevice);
        ImageView iv_conn = (ImageView) view.findViewById(R.id.iv_conn);
        iv_conn.setVisibility(View.INVISIBLE);
        if (PrintApplication.getInstance().currentPrinterAddress != null && PrintApplication.getInstance().currentPrinterAddress.equals( PrintUtils.getDeviceAddress(list.get(position)))) {
            iv_conn.setVisibility(View.VISIBLE);
        }
        tv_pairedDevice.setText(list.get(position));
        return view;
    }
}
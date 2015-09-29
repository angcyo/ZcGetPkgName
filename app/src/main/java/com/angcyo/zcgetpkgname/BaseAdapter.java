package com.angcyo.zcgetpkgname;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angcyo on 15-09-29-029.
 */
public class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.ViewHolder> {

    Context context;
    List<CmdUtil.AppInfo> apps;

    public BaseAdapter(Context context, List<CmdUtil.AppInfo> apps) {
        this.context = context;
        this.apps = apps;
        filter();
    }

    public BaseAdapter(Context context) {
        this.context = context;
        apps = new ArrayList<>();
    }

    public void setApps(List<CmdUtil.AppInfo> apps) {
        this.apps = apps;
        filter();
        this.notifyItemRangeInserted(0, apps.size());
    }

    public void filter() {
        for (int i = 0; i < apps.size(); i++) {
            if (apps.get(i).packageName.trim().contains("com.android")) {
                apps.remove(i);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.adapter_app_item, null);
        return new ViewHolder(view);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final CmdUtil.AppInfo appInfo = apps.get(position);
        holder.appIco.setBackground(appInfo.appIcon);
        holder.appName.setText(appInfo.appName);
        holder.itemlayout.setFocusable(true);
        holder.itemlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                if (!Apk.isEmpty(appInfo.packageName)) {
                    manager.setPrimaryClip(ClipData.newPlainText("packagename", appInfo.packageName));
                    Toast.makeText(context, "包名 " + appInfo.packageName + " 已复制", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "包名获取失败", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIco;
        TextView appName;
        LinearLayout itemlayout;

        public ViewHolder(View itemView) {
            super(itemView);
            appIco = (ImageView) itemView.findViewById(R.id.app_ico);
            appName = (TextView) itemView.findViewById(R.id.app_name);
            itemlayout = (LinearLayout) itemView.findViewById(R.id.item);
        }
    }
}

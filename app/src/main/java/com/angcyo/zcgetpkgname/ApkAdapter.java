package com.angcyo.zcgetpkgname;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angcyo on 15-09-29-029.
 */
public class ApkAdapter extends RecyclerView.Adapter<ApkAdapter.ViewHolder> {

    Context context;
    List<CmdUtil.FileInfo> fileInfos;

    public ApkAdapter(Context context, List<CmdUtil.FileInfo> fileInfos) {
        this.context = context;
        this.fileInfos = fileInfos;
    }

    public ApkAdapter(Context context) {
        this.context = context;
        fileInfos = new ArrayList<>();
    }

    public void setFileInfos(List<CmdUtil.FileInfo> fileInfos) {
        this.fileInfos = fileInfos;
        this.notifyItemRangeInserted(0, fileInfos.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.adapter_apk_item, null);
        return new ViewHolder(view);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final CmdUtil.FileInfo appInfo = fileInfos.get(position);
        holder.appName.setText(appInfo.fileName.trim());
        holder.itemlayout.setFocusable(true);
        holder.itemlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).parseApk(appInfo.filePath);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileInfos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView appName;
        LinearLayout itemlayout;

        public ViewHolder(View itemView) {
            super(itemView);
            appName = (TextView) itemView.findViewById(R.id.app_name);
            itemlayout = (LinearLayout) itemView.findViewById(R.id.item);
        }
    }
}

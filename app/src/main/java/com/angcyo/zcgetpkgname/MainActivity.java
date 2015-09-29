package com.angcyo.zcgetpkgname;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    ProgressFragment progressFragment;
    RecyclerView recyclerView;
    boolean isApp = true;//isApk// 显示类型, APP, 还是APK

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isApp) {
                    scanApk();
                    isApp = false;
                } else {
                    scanApp();
                    isApp = true;
                }
            }
        });
        scanApp();
    }

    private void scanApp() {
        progressFragment = ProgressFragment.newInstance("扫描所有已安装APP...");
        progressFragment.show(getSupportFragmentManager(), "progress");

        new AsyncTask<Void, Void, List<CmdUtil.AppInfo>>() {
            @Override
            protected List<CmdUtil.AppInfo> doInBackground(Void... params) {
                List<CmdUtil.AppInfo> apps = CmdUtil.getAllAppInfo(MainActivity.this);
                return apps;
            }

            @Override
            protected void onPostExecute(List<CmdUtil.AppInfo> appInfos) {
                super.onPostExecute(appInfos);
                progressFragment.dismiss();
                recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 4));
                recyclerView.setAdapter(new BaseAdapter(MainActivity.this, appInfos));
            }
        }.execute();
    }

    private void scanApk() {
        Toast.makeText(this, "扫描APK文件...", Toast.LENGTH_LONG).show();
        progressFragment = ProgressFragment.newInstance("扫描APK文件...");
        progressFragment.show(getSupportFragmentManager(), "progress");

        new AsyncTask<Void, String, List<CmdUtil.FileInfo>>() {
            @Override
            protected List<CmdUtil.FileInfo> doInBackground(Void... params) {
                List<CmdUtil.FileInfo> files = CmdUtil.getAllFiles(Environment.getExternalStorageDirectory().getAbsolutePath(), ".apk", new CmdUtil.CallBack() {
                    @Override
                    public void onCall(String string) {
                        publishProgress(new String[]{string});
                    }
                });
                files.addAll(CmdUtil.getAllFiles("/mnt/extsd", ".apk",
                        new CmdUtil.CallBack() {
                            @Override
                            public void onCall(String string) {
                                publishProgress(new String[]{string});
                            }
                        }));
                return files;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                progressFragment.setTip(values[0]);
            }

            @Override
            protected void onPostExecute(List<CmdUtil.FileInfo> fileInfos) {
                super.onPostExecute(fileInfos);
                progressFragment.dismiss();
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
                recyclerView.setAdapter(new ApkAdapter(MainActivity.this, fileInfos));
            }
        }.execute();
    }

    public void parseApk(String apkPath) {
        progressFragment = ProgressFragment.newInstance("解析APK中... ");
        progressFragment.show(getSupportFragmentManager(), "progress");
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String packName = Apk.getPkgNameFromApk(params[0]);
                return packName;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressFragment.dismiss();
                ClipboardManager manager = (ClipboardManager) MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                manager.setPrimaryClip(ClipData.newPlainText("packagename", s));
                if (!Apk.isEmpty(s)) {
                    Toast.makeText(MainActivity.this, "包名 " + s + " 已复制", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "包名获取失败", Toast.LENGTH_LONG).show();
                }
            }
        }.execute(apkPath);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//            if (keyCode == 12 || keyCode == 20 || keyCode == 21 || keyCode == 22) {
//                recyclerView.setFocusable(true);
//                recyclerView.requestFocus();
//            }
//            Toast.makeText(this, "keyCode : " + keyCode, Toast.LENGTH_LONG).show();//19 20 21 22
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

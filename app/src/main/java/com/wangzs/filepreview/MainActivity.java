package com.wangzs.filepreview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;
/**
 * @description:
 * @autour: wangzs
 * @date: 2019/2/28 16:01
 * @version:
 *
*/
public class MainActivity extends AppCompatActivity implements TbsReaderView.ReaderCallback {

    private TbsReaderView tbsReaderView;
    private FrameLayout rootView;
    private Button otherOpen;
    private Button appOpen;
    private String filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "方案接口介绍.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appOpen = findViewById(R.id.app_open);
        otherOpen = findViewById(R.id.other_open);
        rootView = findViewById(R.id.root_view);

        appOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appOpen(filePath);
            }
        });
        otherOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doViewFilePreviewIntent(filePath);
            }
        });
        findViewById(R.id.down_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Main2Activity.class));
            }
        });
    }

    private void appOpen(String previewPath) {
        if (TextUtils.isEmpty(previewPath)) {
            Toast.makeText(this, "文件路径不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        tbsReaderView = new TbsReaderView(this, this);
        rootView.addView(tbsReaderView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        Bundle bundle = new Bundle();
        bundle.putString("filePath", previewPath);
        bundle.putString("tempPath", Environment.getExternalStorageDirectory().getPath());
        boolean result = tbsReaderView.preOpen(previewType(previewPath), false);
        if (result) {
            tbsReaderView.openFile(bundle);
        } else {
            otherOpen.setVisibility(View.VISIBLE);
        }
    }

    public void doViewFilePreviewIntent(String path) {
        if (TextUtils.isEmpty(path)) {
            Toast.makeText(this, "文件路径不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            String type = MimeTypesTools.getMimeType(this, path);
            File file = new File(path);
            if (!file.exists()) {
                Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
                return;
            }
            intent.setDataAndType(Uri.fromFile(file), type);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tbsReaderView != null) {
            tbsReaderView.onStop();
        }
    }

    private String previewType(String path) {
        if (!TextUtils.isEmpty(path)) {
            return path.substring(path.lastIndexOf(".") + 1).toLowerCase();
        }
        return "";
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }
}

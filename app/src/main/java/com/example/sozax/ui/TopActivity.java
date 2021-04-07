package com.example.sozax.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sozax.R;
import com.example.sozax.bl.com.LoginInfo;
import com.example.sozax.bl.models.version_info.VersionInfoModel;
import com.example.sozax.common.CommonActivity;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TopActivity extends CommonActivity {

    //region onCreate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);

        // アプリ内のバージョン情報を取得
        VersionInfoModel app_version_info = new VersionInfoModel();

        try
        {
            PackageInfo pckInfo = getPackageManager().getPackageInfo(getPackageName(),0);

            app_version_info.Versioncd = pckInfo.versionCode;
            app_version_info.Versionnm = pckInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException ex)
        {
            // エラー内容を出力
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("エラー");
            builder.setMessage(ex.getMessage());

            builder.show();
            return;
        }

        // 画面にバージョン名を出力
        ((TextView) findViewById(R.id.txtVersionName)).setText(app_version_info.Versionnm);

        VersionInfoModel db_version_info = new VersionInfoModel();

        GetVersionInfoTask getVersionInfoTask = new GetVersionInfoTask();

        try {
            db_version_info = getVersionInfoTask.execute().get();
        } catch (ExecutionException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("エラー");
            builder.setMessage(e.getMessage());

            builder.show();
            return;
        } catch (InterruptedException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("エラー");
            builder.setMessage(e.getMessage());

            builder.show();
            return;
        }

        // エラー発生
        if(db_version_info.Is_error)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("エラー");
            builder.setMessage(db_version_info.Message);

            builder.show();
            return;
        }

        // 該当データなし
        if(db_version_info.Versioncd == 0)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("エラー");
            builder.setMessage("最新のバージョン情報が見つかりませんでした。");

            builder.show();
            return;
        }

        // アプリ内とDBの、バージョンコードを比較して、
        // 最新のバージョンであるかチェックする
        if(db_version_info.Versioncd > app_version_info.Versioncd)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("お知らせ");
            builder.setMessage("最新のバージョンにアップデート可能です。");

            builder.show();
        }
    }

    //endregion

    //region 開始ボタンをクリック

    public void btnStart_Click(View view) {

        try
        {

            // ログイン情報を取得
            logininfo = new LoginInfo();
            SharedPreferences preferences = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);

            // 店所
            logininfo.Tensyocd = preferences.getInt("Tensyocd",0);
            logininfo.Tensyonm = preferences.getString("Tensyonm","");

            // 作業担当者
            logininfo.Sgytantocd = preferences.getInt("Sgytantocd",0);
            logininfo.Sgytantonm = preferences.getString("Sgytantonm","");

            // 倉庫
            logininfo.Soukocd = preferences.getInt("Soukocd",0);
            logininfo.Soukonm = preferences.getString("Soukonm","");

            // 作業日時
            long sgydate = preferences.getLong("Sgydate",0);
            logininfo.Sgydate = new Date(sgydate);

            // 更新日時
            long updatedate = preferences.getLong("Updatedate",0);
            logininfo.Updatedate = new Date(updatedate);

        }catch (Exception ex)
        {
            // エラー内容を出力
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("エラー");
            builder.setMessage(ex.getMessage());

            builder.show();

            // ログイン画面に遷移
            Intent intent = new Intent(getApplication(), LoginActivity.class);

            startActivity(intent);
            return;
        }

        // 現在日時を取得
        Date nowDate = new Date();

        if(logininfo.Updatedate.getYear() == nowDate.getYear() &&
           logininfo.Updatedate.getMonth() == nowDate.getMonth() &&
           logininfo.Updatedate.getDay() == nowDate.getDay())
        {
            // 更新日と現在日が一致する場合、メニュー画面に遷移
            Intent intent = new Intent(getApplication(), MenuActivity.class);
            intent.putExtra("LogionInfo", logininfo);

            startActivity(intent);
        }
        else
        {
            // 更新日と現在日が一致しない場合、ログイン画面に遷移
            Intent intent = new Intent(getApplication(), LoginActivity.class);
            intent.putExtra("LogionInfo", logininfo);

            startActivity(intent);
        }
    }

    //endregion

    //region DENSO固有ボタンの設定

    @Override
    public void onKeyRemapCreated() {
    }

    //endregion

    //region バージョン情報取得

    public class GetVersionInfoTask extends AsyncTask<URL, Void, VersionInfoModel> {

        // 非同期処理
        @Override
        protected VersionInfoModel doInBackground(URL... params) {

            VersionInfoModel ret = null;

            final okhttp3.MediaType mediaTypeJson = okhttp3.MediaType.parse("application/json; charset=UTF-8");
            final Request request = new Request.Builder()
                    .url("http://192.168.10.214:55500/api/versioninfo/get/")
                    .headers(Headers.of(new LinkedHashMap<String,String>()))
                    .build();

            final OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            Response response = null;

            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String s = "";
            try {
                s = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // JSONファイルからModelデータに変換
            Gson gson = new Gson();
            ret = gson.fromJson(s,VersionInfoModel.class);

            return  ret;
        }
    }

    //endregion
}
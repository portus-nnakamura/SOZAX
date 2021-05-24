package com.example.sozax.bl.controllers;

import android.os.AsyncTask;
import android.os.Environment;
import android.telephony.emergency.EmergencyNumber;

import com.densowave.apkinstallservice.IApkInstallAPI;
import com.example.sozax.bl.models.apk_download.ApkDownloadModel;
import com.example.sozax.common.CommonController;
import com.example.sozax.common.ResultClass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressWarnings("ALL")
public class ApkController extends CommonController {

    /**
     * apkファイルを取得するタスク
     */
    public static class GetApkTask extends AsyncTask<Void, Void, ApkDownloadModel> {

        // 非同期処理
        @Override
        protected ApkDownloadModel doInBackground(Void... void1) {

            ApkDownloadModel ret = null;

            try {

                // リクエストメッセージを作成
                final Request request = new Request.Builder()
                        .url(defaultRoute +"release/app-release.apk")
                        .headers(Headers.of(new LinkedHashMap<String, String>()))
                        .build();

                final OkHttpClient client = new OkHttpClient.Builder()
                        .build();

                // リクエストを実行して、レスポンスを取得
                Response response = client.newCall(request).execute();

                // 失敗した場合
                if (!response.isSuccessful()) {
                    ret = new ApkDownloadModel();
                    ret.Is_error = true;
                    ret.Message = response.message();
                    return ret;
                }

                // apkファイルの保存先を指定
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) .getPath() +"/release/";
                File file = new File(path);
                if(!file.mkdirs())
                {
                    ret = new ApkDownloadModel();
                    ret.Is_error = true;
                    ret.Message = "保存先ディレクトリの作成に失敗しました。";
                }

                // テンポラリファイルの設定
                File outputFile = new File(file,"tmp.apk");
                FileOutputStream fileOutputStream = new FileOutputStream(outputFile);

                // ダウンロード開始
                InputStream inputStream =response.body().byteStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = inputStream.read(buffer)) != -1)
                {
                    fileOutputStream.write(buffer,0,len);
                }
                fileOutputStream.close();
                inputStream.close();

                ret = new ApkDownloadModel();
                ret.Is_error = false;
                ret.Filepath = outputFile.getPath();

            } catch (Exception e) {
                ret = new ApkDownloadModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            return ret;
        }
    }
}

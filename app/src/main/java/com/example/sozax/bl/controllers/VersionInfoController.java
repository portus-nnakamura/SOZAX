package com.example.sozax.bl.controllers;

import android.os.AsyncTask;

import com.example.sozax.bl.models.version_info.VersionInfoModel;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VersionInfoController {

    public static class GetVersionInfoTask extends AsyncTask<Void, Void, VersionInfoModel> {

        // 非同期処理
        @Override
        protected VersionInfoModel doInBackground(Void... aVoid) {

            VersionInfoModel ret = null;

            final Request request = new Request.Builder()
                    .url("http://192.168.10.214:55500/api/versioninfo/get/")
                    .headers(Headers.of(new LinkedHashMap<String, String>()))
                    .build();

            final OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                ret = new VersionInfoModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return  ret;
            }

            if (response.isSuccessful() == false)
            {
                ret = new VersionInfoModel();
                ret.Is_error = true;
                ret.Message = response.message();
                return  ret;
            }

            String s = "";
            try {
                s = response.body().string();
            } catch (IOException e) {
                ret = new VersionInfoModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return  ret;
            }

            // JSONファイルからModelデータに変換
            Gson gson = new Gson();
            ret = gson.fromJson(s, VersionInfoModel.class);

            return ret;
        }
    }
}

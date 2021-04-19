package com.example.sozax.bl.controllers;

import android.os.AsyncTask;

import com.example.sozax.bl.models.version_info.VersionInfoModel;
import com.example.sozax.bl.models.zaiko_syokai.ZaikoSyokaiConditionModel;
import com.example.sozax.bl.models.zaiko_syokai.ZaikoSyokaiModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.LinkedHashMap;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ZaikoSyokaiController {

    public static class GetZaikoSyokaiTask extends AsyncTask<Long, Void, ZaikoSyokaiModel> {

        // 非同期処理
        @Override
        protected ZaikoSyokaiModel doInBackground(Long... syukeicd) {

            ZaikoSyokaiModel ret;

            final Request request = new Request.Builder()
                    .url("http://192.168.10.214:55500/api/zaikosyokai/get/" + String.valueOf(syukeicd[0]))
                    .headers(Headers.of(new LinkedHashMap<String, String>()))
                    .build();

            final OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            try {

                Response response = client.newCall(request).execute();

                if (response.isSuccessful() == false)
                {
                    ret = new ZaikoSyokaiModel();
                    ret.Is_error = true;
                    ret.Message = response.message();
                    return  ret;
                }

                String s = response.body().string();

                // JSONファイルからModelデータに変換
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                ret = gson.fromJson(s, ZaikoSyokaiModel.class);

            } catch (IOException e) {
                ret = new ZaikoSyokaiModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }
            catch (Exception e) {
                ret = new ZaikoSyokaiModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            return ret;
        }
    }
}

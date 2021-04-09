package com.example.sozax.bl.controllers;

import android.os.AsyncTask;

import com.example.sozax.bl.models.zaiko_syokai.ZaikoSyokaiConditionModel;
import com.example.sozax.bl.models.zaiko_syokai.ZaikoSyokaiModel;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.LinkedHashMap;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ZaikoSyokaiController {

    public static class GetZaikoSyokaiTask extends AsyncTask<ZaikoSyokaiConditionModel, Void, ZaikoSyokaiModel> {

        // 非同期処理
        @Override
        protected ZaikoSyokaiModel doInBackground(ZaikoSyokaiConditionModel... zaikoSyokaiConditionModel) {

            ZaikoSyokaiModel ret = null;

            final Request request = new Request.Builder()
                    .url("http://192.168.10.214:55500/api/zaikosyokai/get/" + zaikoSyokaiConditionModel[0].Syukeicd)
                    .headers(Headers.of(new LinkedHashMap<String, String>()))
                    .build();

            final OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                ret = new ZaikoSyokaiModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            String s = "";
            try {
                s = response.body().string();
            } catch (IOException e) {
                ret = new ZaikoSyokaiModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            // JSONファイルからModelデータに変換
            Gson gson = new Gson();
            ret = gson.fromJson(s, ZaikoSyokaiModel.class);

            return ret;
        }
    }
}
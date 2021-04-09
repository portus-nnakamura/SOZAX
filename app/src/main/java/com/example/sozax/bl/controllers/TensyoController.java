package com.example.sozax.bl.controllers;

import android.os.AsyncTask;

import com.example.sozax.bl.models.login_info.LoginInfoModel;
import com.example.sozax.bl.models.tensyo.TensyosModel;
import com.example.sozax.bl.models.version_info.VersionInfoModel;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.LinkedHashMap;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TensyoController {

    public static class GetTensyosTask extends AsyncTask<LoginInfoModel, Void, TensyosModel> {

        // 非同期処理
        @Override
        protected TensyosModel doInBackground(LoginInfoModel... loginInfoModel) {

            TensyosModel ret = null;

            final Request request = new Request.Builder()
                    .url("http://192.168.244.156:55500/api/tensyo/get/" + loginInfoModel[0].Kaicd)
                    .headers(Headers.of(new LinkedHashMap<String, String>()))
                    .build();

            final OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                ret = new TensyosModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            if (response.isSuccessful() == false)
            {
                ret = new TensyosModel();
                ret.Is_error = true;
                ret.Message = response.message();
                return  ret;
            }

            String s = "";
            try {
                s = response.body().string();
            } catch (IOException e) {
                ret = new TensyosModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            // JSONファイルからModelデータに変換
            Gson gson = new Gson();
            ret = gson.fromJson(s, TensyosModel.class);

            return ret;
        }
    }
}

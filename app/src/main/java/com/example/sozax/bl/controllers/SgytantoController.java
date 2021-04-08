package com.example.sozax.bl.controllers;

import android.os.AsyncTask;

import com.example.sozax.bl.models.login_info.LoginInfoModel;
import com.example.sozax.bl.models.sgytanto.SgytantosModel;
import com.example.sozax.bl.models.tensyo.TensyosModel;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.LinkedHashMap;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SgytantoController {

    public static class GetSgytantosTask extends AsyncTask<LoginInfoModel, Void, SgytantosModel> {

        // 非同期処理
        @Override
        protected SgytantosModel doInBackground(LoginInfoModel... loginInfoModel) {

            SgytantosModel ret = null;

            final Request request = new Request.Builder()
                    .url("http://192.168.10.214:55500/api/sgytanto/get/" + loginInfoModel[0].Kaicd + "/" + loginInfoModel[0].Tensyocd)
                    .headers(Headers.of(new LinkedHashMap<String, String>()))
                    .build();

            final OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                ret = new SgytantosModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            String s = "";
            try {
                s = response.body().string();
            } catch (IOException e) {
                ret = new SgytantosModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            // JSONファイルからModelデータに変換
            Gson gson = new Gson();
            ret = gson.fromJson(s, SgytantosModel.class);

            return ret;
        }
    }
}

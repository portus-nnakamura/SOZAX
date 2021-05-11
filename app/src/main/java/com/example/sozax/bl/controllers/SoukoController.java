package com.example.sozax.bl.controllers;

import android.os.AsyncTask;

import com.example.sozax.bl.models.login_info.LoginInfoModel;
import com.example.sozax.bl.models.sgytanto.SgytantosModel;
import com.example.sozax.bl.models.souko.SoukosModel;
import com.example.sozax.bl.models.syuko_denpyo.SyukoDenpyosModel;
import com.example.sozax.common.CommonController;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.LinkedHashMap;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SoukoController  extends CommonController {

    public static class GetSoukosTask extends AsyncTask<LoginInfoModel, Void, SoukosModel> {

        // 非同期処理
        @Override
        protected SoukosModel doInBackground(LoginInfoModel... loginInfoModel) {

            SoukosModel ret = null;

            final Request request = new Request.Builder()
                    .url(strURL + "souko/get/" + loginInfoModel[0].Kaicd + "/" + loginInfoModel[0].Tensyocd)
                    .headers(Headers.of(new LinkedHashMap<String, String>()))
                    .build();

            final OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                ret = new SoukosModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            if (!response.isSuccessful())
            {
                ret = new SoukosModel();
                ret.Is_error = true;
                ret.Message = response.message();
                return  ret;
            }

            String s = "";
            try {
                s = response.body().string();
            } catch (IOException e) {
                ret = new SoukosModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            // JSONファイルからModelデータに変換
            Gson gson = new Gson();
            ret = gson.fromJson(s, SoukosModel.class);

            return ret;
        }
    }
}

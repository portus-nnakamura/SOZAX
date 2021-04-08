package com.example.sozax.bl.controllers;

import android.os.AsyncTask;

import com.example.sozax.bl.models.bar_chk_komk.BarChkKomkConditionModel;
import com.example.sozax.bl.models.bar_chk_komk.BarChkKomkModel;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.LinkedHashMap;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BarChkKomkController {

    public static class GetBarChkKomkTask extends AsyncTask<BarChkKomkConditionModel, Void, BarChkKomkModel> {

        // 非同期処理
        @Override
        protected BarChkKomkModel doInBackground(BarChkKomkConditionModel... barChkKomkConditionModel) {

            BarChkKomkModel ret = null;

            final Request request = new Request.Builder()
                    .url("http://192.168.10.214:55500/api/barchkkomk/get/" + barChkKomkConditionModel[0].Hinbuncd + "/"
                            + barChkKomkConditionModel[0].Hinsyucd + "/" + barChkKomkConditionModel[0].Ninusicd)
                    .headers(Headers.of(new LinkedHashMap<String, String>()))
                    .build();

            final OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                ret = new BarChkKomkModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            String s = "";
            try {
                s = response.body().string();
            } catch (IOException e) {
                ret = new BarChkKomkModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            // JSONファイルからModelデータに変換
            Gson gson = new Gson();
            ret = gson.fromJson(s, BarChkKomkModel.class);

            return ret;
        }
    }
}

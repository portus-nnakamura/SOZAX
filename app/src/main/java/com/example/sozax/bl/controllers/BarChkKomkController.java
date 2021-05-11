package com.example.sozax.bl.controllers;

import android.app.Application;
import android.os.AsyncTask;

import com.example.sozax.R;
import com.example.sozax.bl.models.bar_chk_komk.BarChkKomkConditionModel;
import com.example.sozax.bl.models.bar_chk_komk.BarChkKomkModel;
import com.example.sozax.bl.models.syuko_denpyo.SyukoDenpyosModel;
import com.example.sozax.common.CommonController;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.LinkedHashMap;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BarChkKomkController extends CommonController {

    public static class GetBarChkKomkTask extends AsyncTask<BarChkKomkConditionModel, Void, BarChkKomkModel> {

        // 非同期処理
        @Override
        protected BarChkKomkModel doInBackground(BarChkKomkConditionModel... barChkKomkConditionModel) {

            BarChkKomkModel ret = null;
            final Request request = new Request.Builder()
                    .url(strURL + "barchkkomk/get/" + barChkKomkConditionModel[0].Hinbuncd + "/"
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

            if (!response.isSuccessful())
            {
                ret = new BarChkKomkModel();
                ret.Is_error = true;
                ret.Message = response.message();
                return  ret;
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

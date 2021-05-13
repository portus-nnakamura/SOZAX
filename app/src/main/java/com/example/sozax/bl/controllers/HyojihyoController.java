package com.example.sozax.bl.controllers;

import android.os.AsyncTask;

import com.example.sozax.bl.models.hyojihyo.HyojihyoConditionModel;
import com.example.sozax.bl.models.hyojihyo.HyojihyoModel;
import com.example.sozax.bl.models.syuko_denpyo.SyukoDenpyosModel;
import com.example.sozax.common.CommonController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.LinkedHashMap;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HyojihyoController  extends CommonController {

    public static class GetHyojihyoTask extends AsyncTask<HyojihyoConditionModel, Void, HyojihyoModel> {

        // 非同期処理
        @Override
        protected HyojihyoModel doInBackground(HyojihyoConditionModel... hyojihyoConditionModel) {

            HyojihyoModel ret = null;

            final Request request = new Request.Builder()
                    .url( strURL+ "hyojihyo/get/" + hyojihyoConditionModel[0].Kaicd + "/"
                            + hyojihyoConditionModel[0].Soukocd + "/" + hyojihyoConditionModel[0].Syukeicd)
                    .headers(Headers.of(new LinkedHashMap<String, String>()))
                    .build();

            final OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                ret = new HyojihyoModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            if (!response.isSuccessful())
            {
                ret = new HyojihyoModel();
                ret.Is_error = true;
                ret.Message = response.message();
                return  ret;
            }

            String s = "";
            try {
                s = response.body().string();
            } catch (IOException e) {
                ret = new HyojihyoModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            try{
                // JSONファイルからModelデータに変換
                Gson gson =  new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                ret = gson.fromJson(s, HyojihyoModel.class);
            }catch (Exception e){
                ret = new HyojihyoModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            return ret;
        }
    }

}

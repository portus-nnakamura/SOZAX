package com.example.sozax_app.bl.controllers;

import android.os.AsyncTask;

import com.example.sozax_app.bl.models.zaiko_syokai.ZaikoSyokaiModel;
import com.example.sozax_app.common.CommonController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Response;

@SuppressWarnings("ALL")
public class ZaikoSyokaiController extends CommonController {

    /**
     * 在庫照会データを取得するタスク
     */
    public static class GetZaikoSyokaiTask extends AsyncTask<Long, Void, ZaikoSyokaiModel> {

        // 非同期処理
        @Override
        protected ZaikoSyokaiModel doInBackground(Long... syukeicd) {

            ZaikoSyokaiModel ret = null;

            try {
                // リクエストを投げて、レスポンスを取得
                Response response = getResponse("zaikosyokai/get/" + syukeicd[0]);

                // 失敗した場合
                if (!response.isSuccessful()) {
                    ret = new ZaikoSyokaiModel();
                    ret.Is_error = true;
                    ret.Message = response.message();
                    return ret;
                }

                // レスポンスから在庫照会データを取得
                String s = response.body().string();

                // JSONファイルからModelデータに変換
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                ret = gson.fromJson(s, ZaikoSyokaiModel.class);
            } catch (Exception e) {
                ret = new ZaikoSyokaiModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            return ret;
        }
    }
}

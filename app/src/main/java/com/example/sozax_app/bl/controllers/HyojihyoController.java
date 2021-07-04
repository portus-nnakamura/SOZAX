package com.example.sozax_app.bl.controllers;

import android.os.AsyncTask;

import com.example.sozax_app.bl.models.hyojihyo.HyojihyoConditionModel;
import com.example.sozax_app.bl.models.hyojihyo.HyojihyoModel;
import com.example.sozax_app.common.CommonController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Response;

@SuppressWarnings("ALL")
public class HyojihyoController extends CommonController {

    /**
     * 表示票データを取得するタスク
     */
    public static class GetHyojihyoTask extends AsyncTask<HyojihyoConditionModel, Void, HyojihyoModel> {

        // 非同期処理
        @Override
        protected HyojihyoModel doInBackground(HyojihyoConditionModel... hyojihyoConditionModel) {

            HyojihyoModel ret = null;

            try {
                // リクエストを投げて、レスポンスを取得
                Response response = getResponse("hyojihyo/get/" + hyojihyoConditionModel[0].Kaicd + "/" + hyojihyoConditionModel[0].Soukocd + "/" + hyojihyoConditionModel[0].Syukeicd);

                // 失敗した場合
                if (!response.isSuccessful()) {
                    ret = new HyojihyoModel();
                    ret.Is_error = true;
                    ret.Message = response.message();
                    return ret;
                }

                // レスポンスから表示票データを取得
                String s = response.body().string();

                // JSONファイルからModelデータに変換
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                ret = gson.fromJson(s, HyojihyoModel.class);
            } catch (Exception e) {
                ret = new HyojihyoModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            return ret;
        }
    }

}

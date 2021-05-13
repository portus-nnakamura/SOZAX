package com.example.sozax.bl.controllers;

import android.os.AsyncTask;

import com.example.sozax.bl.models.login_info.LoginInfoModel;
import com.example.sozax.bl.models.tensyo.TensyosModel;
import com.example.sozax.common.CommonController;
import com.google.gson.Gson;

import okhttp3.Response;

@SuppressWarnings("ALL")
public class TensyoController extends CommonController {

    /**
     * 店所マスタ一覧を取得するタスク
     */
    public static class GetTensyosTask extends AsyncTask<LoginInfoModel, Void, TensyosModel> {

        // 非同期処理
        @Override
        protected TensyosModel doInBackground(LoginInfoModel... loginInfoModel) {

            TensyosModel ret = null;

            try {
                // リクエストを投げて、レスポンスを取得
                Response response = getResponse("tensyo/get/" + loginInfoModel[0].Kaicd);

                // 失敗した場合
                if (!response.isSuccessful()) {
                    ret = new TensyosModel();
                    ret.Is_error = true;
                    ret.Message = response.message();
                    return ret;
                }

                // レスポンスから店所マスタ一覧を取得
                String s = response.body().string();

                // JSONファイルからModelデータに変換
                Gson gson = new Gson();
                ret = gson.fromJson(s, TensyosModel.class);
            } catch (Exception e) {
                ret = new TensyosModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            return ret;
        }
    }
}

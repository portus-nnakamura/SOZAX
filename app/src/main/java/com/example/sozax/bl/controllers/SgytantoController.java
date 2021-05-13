package com.example.sozax.bl.controllers;

import android.os.AsyncTask;

import com.example.sozax.bl.models.login_info.LoginInfoModel;
import com.example.sozax.bl.models.sgytanto.SgytantosModel;
import com.example.sozax.common.CommonController;
import com.google.gson.Gson;

import okhttp3.Response;

@SuppressWarnings("ALL")
public class SgytantoController extends CommonController {

    /**
     * 作業担当者マスタ一覧を取得するタスク
     */
    public static class GetSgytantosTask extends AsyncTask<LoginInfoModel, Void, SgytantosModel> {

        // 非同期処理
        @Override
        protected SgytantosModel doInBackground(LoginInfoModel... loginInfoModel) {

            SgytantosModel ret = null;

            try {
                // リクエストを投げて、レスポンスを取得
                Response response = getResponse("sgytanto/get/" + loginInfoModel[0].Kaicd + "/" + loginInfoModel[0].Tensyocd);

                // 失敗した場合
                if (!response.isSuccessful()) {
                    ret = new SgytantosModel();
                    ret.Is_error = true;
                    ret.Message = response.message();
                    return ret;
                }

                // レスポンスから作業担当者マスタ一覧を取得
                String s = response.body().string();

                // JSONファイルからModelデータに変換
                Gson gson = new Gson();
                ret = gson.fromJson(s, SgytantosModel.class);
            } catch (Exception e) {
                ret = new SgytantosModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            return ret;
        }
    }
}

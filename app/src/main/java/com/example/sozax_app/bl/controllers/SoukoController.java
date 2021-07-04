package com.example.sozax_app.bl.controllers;

import android.os.AsyncTask;

import com.example.sozax_app.bl.models.login_info.LoginInfoModel;
import com.example.sozax_app.bl.models.souko.SoukosModel;
import com.example.sozax_app.common.CommonController;
import com.google.gson.Gson;

import okhttp3.Response;

@SuppressWarnings("ALL")
public class SoukoController extends CommonController {

    /**
     * 倉庫マスタ一覧を取得するタスク
     */
    public static class GetSoukosTask extends AsyncTask<LoginInfoModel, Void, SoukosModel> {

        // 非同期処理
        @Override
        protected SoukosModel doInBackground(LoginInfoModel... loginInfoModel) {

            SoukosModel ret = null;

            try {
                // リクエストを投げて、レスポンスを取得
                Response response = getResponse("souko/get/" + loginInfoModel[0].Kaicd + "/" + loginInfoModel[0].Tensyocd);

                // 失敗した場合
                if (!response.isSuccessful()) {
                    ret = new SoukosModel();
                    ret.Is_error = true;
                    ret.Message = response.message();
                    return ret;
                }

                // レスポンスから倉庫マスタ一覧を取得
                String s = response.body().string();

                // JSONファイルからModelデータに変換
                Gson gson = new Gson();
                ret = gson.fromJson(s, SoukosModel.class);
            } catch (Exception e) {
                ret = new SoukosModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            return ret;
        }
    }
}

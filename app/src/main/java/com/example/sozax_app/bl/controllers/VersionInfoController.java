package com.example.sozax_app.bl.controllers;

import android.os.AsyncTask;

import com.example.sozax_app.bl.models.version_info.VersionInfoModel;
import com.example.sozax_app.common.CommonController;
import com.google.gson.Gson;

import okhttp3.Response;

@SuppressWarnings("ALL")
public class VersionInfoController extends CommonController {

    /**
     * バージョン管理マスタを取得するタスク
     */
    public static class GetVersionInfoTask extends AsyncTask<Void, Void, VersionInfoModel> {

        // 非同期処理
        @Override
        protected VersionInfoModel doInBackground(Void... aVoid) {

            VersionInfoModel ret = null;

            try {
                // リクエストを投げて、レスポンスを取得
                Response response = getResponse("versioninfo/get/");

                // 失敗した場合
                if (!response.isSuccessful()) {
                    ret = new VersionInfoModel();
                    ret.Is_error = true;
                    ret.Message = response.message();
                    return ret;
                }

                // レスポンスからバージョン管理マスタを取得
                String s = response.body().string();

                // JSONファイルからModelデータに変換
                Gson gson = new Gson();
                ret = gson.fromJson(s, VersionInfoModel.class);
            } catch (Exception e) {
                ret = new VersionInfoModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            return ret;
        }
    }
}

package com.example.sozax_app.bl.controllers;

import android.os.AsyncTask;

import com.example.sozax_app.bl.models.syuko_denpyo.SyukoDenpyosModel;
import com.example.sozax_app.common.CommonController;
import com.example.sozax_app.common.ResultClass;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Response;

@SuppressWarnings("ALL")
public class SyukoSagyoController extends CommonController {

    //region 登録

    /**
     * 出庫作業を登録するタスク
     */
    public static class PostSyukoSagyosTask extends AsyncTask<SyukoDenpyosModel, Void, SyukoDenpyosModel> {

        // 非同期処理
        @Override
        protected SyukoDenpyosModel doInBackground(SyukoDenpyosModel... syukoDenpyosModel) {

            SyukoDenpyosModel ret = syukoDenpyosModel[0];

            try {

                // リクエストを投げて、レスポンスを取得
                Response response = executePost("syukosagyo/post/", ret);

                // 失敗した場合
                if (!response.isSuccessful()) {
                    ret.Is_error = true;
                    ret.Message = response.message();
                    return ret;
                }

                // レスポンスから出庫伝票データを取得
                String s = response.body().string();

                // JSONファイルからModelデータに変換
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                ResultClass resultClass = gson.fromJson(s, ResultClass.class);

                // 結果を格納
                ret.Is_error = resultClass.Is_error;
                ret.Message = resultClass.Message;

            } catch (Exception e) {
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            return ret;
        }
    }

    //endregion

    //region 更新

    /**
     * 出庫作業を更新するタスク
     */
    public static class PutSyukoSagyosTask extends AsyncTask<SyukoDenpyosModel, Void, SyukoDenpyosModel> {

        // 非同期処理
        @Override
        protected SyukoDenpyosModel doInBackground(SyukoDenpyosModel... syukoDenpyosModel) {

            SyukoDenpyosModel ret = syukoDenpyosModel[0];

            try {

                // リクエストを投げて、レスポンスを取得
                Response response = executePut("syukosagyo/put/", ret);

                // 失敗した場合
                if (!response.isSuccessful()) {
                    ret.Is_error = true;
                    ret.Message = response.message();
                    return ret;
                }

                // レスポンスから出庫伝票データを取得
                String s = response.body().string();

                // JSONファイルからModelデータに変換
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                ResultClass resultClass = gson.fromJson(s, ResultClass.class);

                // 結果を格納
                ret.Is_error = resultClass.Is_error;
                ret.Message = resultClass.Message;

            } catch (Exception e) {
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            return ret;
        }
    }

    //endregion

    //region 削除

    /**
     * 出庫作業を削除するタスク
     */
    public static class DeleteSyukoSagyosTask extends AsyncTask<SyukoDenpyosModel, Void, SyukoDenpyosModel> {

        // 非同期処理
        @Override
        protected SyukoDenpyosModel doInBackground(SyukoDenpyosModel... syukoDenpyosModel) {

            SyukoDenpyosModel ret = syukoDenpyosModel[0];

            try {

                // リクエストを投げて、レスポンスを取得
                Response response = executeDelete("syukosagyo/delete/", ret);

                // 失敗した場合
                if (!response.isSuccessful()) {
                    ret.Is_error = true;
                    ret.Message = response.message();
                    return ret;
                }

                // レスポンスから出庫伝票データを取得
                String s = response.body().string();

                // JSONファイルからModelデータに変換
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                ResultClass resultClass = gson.fromJson(s, ResultClass.class);

                // 結果を格納
                ret.Is_error = resultClass.Is_error;
                ret.Message = resultClass.Message;

            } catch (Exception e) {
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            return ret;
        }
    }

    //endregion

}

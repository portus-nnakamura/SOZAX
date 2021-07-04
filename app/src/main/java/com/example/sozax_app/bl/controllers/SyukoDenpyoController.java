package com.example.sozax_app.bl.controllers;

import android.os.AsyncTask;

import com.example.sozax_app.bl.models.syuko_denpyo.SyukoDenpyoConditionModel;
import com.example.sozax_app.bl.models.syuko_denpyo.SyukoDenpyoModel;
import com.example.sozax_app.bl.models.syuko_denpyo.SyukoDenpyosModel;
import com.example.sozax_app.common.CommonController;
import com.example.sozax_app.common.CommonFunction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.Response;

@SuppressWarnings("ALL")
public class SyukoDenpyoController extends CommonController {

    //region 1件取得

    /**
     * 出庫伝票データを1件取得するタスク
     */
    public static class GetSyukoDenpyoTask extends AsyncTask<SyukoDenpyoConditionModel, Void, SyukoDenpyoModel> {

        // 非同期処理
        @Override
        protected SyukoDenpyoModel doInBackground(SyukoDenpyoConditionModel... syukoDenpyoConditionModel) {

            SyukoDenpyoModel ret = null;

            try {
                // リクエストを投げて、レスポンスを取得
                Response response = getResponse("syukodenpyo/get/" + syukoDenpyoConditionModel[0].Syukono + "/"
                        + syukoDenpyoConditionModel[0].Kaicd + "/" + syukoDenpyoConditionModel[0].Soukocd + "/"
                        + CommonFunction.settingDateFormat(syukoDenpyoConditionModel[0].Sagyodate, "yyyy-MM-dd"));

                // 失敗した場合
                if (!response.isSuccessful()) {
                    ret = new SyukoDenpyoModel();
                    ret.Is_error = true;
                    ret.Message = response.message();
                    return ret;
                }

                // レスポンスから出庫伝票データを取得
                String s = response.body().string();

                // JSONファイルからModelデータに変換
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                ret = gson.fromJson(s, SyukoDenpyoModel.class);
            } catch (Exception e) {
                ret = new SyukoDenpyoModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            return ret;
        }
    }

    //endregion

    //region 複数件取得

    /**
     * 出庫伝票データを複数件取得するタスク
     */
    public static class GetSyukoDenpyosTask extends AsyncTask<SyukoDenpyoConditionModel, Void, SyukoDenpyosModel> {

        // 非同期処理
        @Override
        protected SyukoDenpyosModel doInBackground(SyukoDenpyoConditionModel... syukoDenpyoConditionModel) {

            SyukoDenpyosModel ret = null;

            try {
                // リクエストを投げて、レスポンスを取得
                Response response = getResponse("syukodenpyo/get/" + syukoDenpyoConditionModel[0].Kaicd + "/"
                        + syukoDenpyoConditionModel[0].Soukocd + "/"
                        + CommonFunction.settingDateFormat(syukoDenpyoConditionModel[0].Sagyodate, "yyyy-MM-dd"));

                // 失敗した場合
                if (!response.isSuccessful()) {
                    ret = new SyukoDenpyosModel();
                    ret.Is_error = true;
                    ret.Message = response.message();
                    return ret;
                }

                // レスポンスから出庫伝票データ一覧を取得
                String s = response.body().string();

                // JSONファイルからModelデータに変換
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                ret = gson.fromJson(s, SyukoDenpyosModel.class);
            } catch (Exception e) {
                ret = new SyukoDenpyosModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            return ret;
        }
    }

    //endregion

    //region 作業中を複数件取得

    /**
     * 作業中の出庫伝票データを複数件取得するタスク
     */
    public static class GetSyukoDenpyos_SagyochuTask extends AsyncTask<SyukoDenpyoConditionModel, Void, SyukoDenpyosModel> {

        // 非同期処理
        @Override
        protected SyukoDenpyosModel doInBackground(SyukoDenpyoConditionModel... syukoDenpyoConditionModel) {

            SyukoDenpyosModel ret = null;

            try {
                // リクエストを投げて、レスポンスを取得
                Response response = getResponse("syukodenpyo/get/sagyochu/" + syukoDenpyoConditionModel[0].Kaicd + "/"
                        + syukoDenpyoConditionModel[0].Soukocd + "/" + syukoDenpyoConditionModel[0].Sgytantocd + "/"
                        + CommonFunction.settingDateFormat(syukoDenpyoConditionModel[0].Sagyodate, "yyyy-MM-dd"));

                // 失敗した場合
                if (!response.isSuccessful()) {
                    ret = new SyukoDenpyosModel();
                    ret.Is_error = true;
                    ret.Message = response.message();
                    return ret;
                }

                // レスポンスから出庫伝票データ一覧を取得
                String s = response.body().string();

                // JSONファイルからModelデータに変換
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                ret = gson.fromJson(s, SyukoDenpyosModel.class);
            } catch (Exception e) {
                ret = new SyukoDenpyosModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            return ret;
        }
    }

    //endregion

    //region 未着手を複数件取得

    /**
     * 未着手の出庫伝票データを複数件取得するタスク
     */
    public static class GetSyukoDenpyos_SagyomichakusyuTask extends AsyncTask<SyukoDenpyoConditionModel, Void, SyukoDenpyosModel> {

        // 非同期処理
        @Override
        protected SyukoDenpyosModel doInBackground(SyukoDenpyoConditionModel... syukoDenpyoConditionModel) {

            SyukoDenpyosModel ret = null;

            try {
                // リクエストを投げて、レスポンスを取得
                Response response = getResponse("syukodenpyo/get/sagyomichakusyu/" + syukoDenpyoConditionModel[0].Kaicd + "/"
                        + syukoDenpyoConditionModel[0].Soukocd + "/"
                        + CommonFunction.settingDateFormat(syukoDenpyoConditionModel[0].Sagyodate, "yyyy-MM-dd"));

                // 失敗した場合
                if (!response.isSuccessful()) {
                    ret = new SyukoDenpyosModel();
                    ret.Is_error = true;
                    ret.Message = response.message();
                    return ret;
                }

                // レスポンスから出庫伝票データ一覧を取得
                String s = response.body().string();

                // JSONファイルからModelデータに変換
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                ret = gson.fromJson(s, SyukoDenpyosModel.class);
            } catch (Exception e) {
                ret = new SyukoDenpyosModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            return ret;
        }
    }

    //endregion
}
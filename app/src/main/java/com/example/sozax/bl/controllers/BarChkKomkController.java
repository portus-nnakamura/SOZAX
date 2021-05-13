package com.example.sozax.bl.controllers;

import android.os.AsyncTask;

import com.example.sozax.bl.models.bar_chk_komk.BarChkKomkConditionModel;
import com.example.sozax.bl.models.bar_chk_komk.BarChkKomkModel;
import com.example.sozax.common.CommonController;
import com.google.gson.Gson;

import okhttp3.Response;

@SuppressWarnings("ALL")
public class BarChkKomkController extends CommonController {

    /**
     * バーコードチェック項目マスタを取得するタスク
     */
    public static class GetBarChkKomkTask extends AsyncTask<BarChkKomkConditionModel, Void, BarChkKomkModel> {

        // 非同期処理
        @Override
        protected BarChkKomkModel doInBackground(BarChkKomkConditionModel... barChkKomkConditionModel) {

            BarChkKomkModel ret = null;

            try {
                // リクエストを投げて、レスポンスを取得
                Response response = getResponse("barchkkomk/get/" + barChkKomkConditionModel[0].Hinbuncd + "/" + barChkKomkConditionModel[0].Hinsyucd + "/" + barChkKomkConditionModel[0].Ninusicd);

                // 失敗した場合
                if (!response.isSuccessful()) {
                    ret = new BarChkKomkModel();
                    ret.Is_error = true;
                    ret.Message = response.message();
                    return ret;
                }

                // レスポンスからバーコードチェック項目マスタを取得
                String s = response.body().string();

                // JSONファイルからModelデータに変換
                Gson gson = new Gson();
                ret = gson.fromJson(s, BarChkKomkModel.class);
            } catch (Exception e) {
                ret = new BarChkKomkModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            return ret;
        }
    }
}

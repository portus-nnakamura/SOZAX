package com.example.sozax.bl.controllers;

import android.content.ContentResolver;
import android.os.AsyncTask;

import com.example.sozax.bl.models.hyojihyo.HyojiHyoModel;
import com.example.sozax.bl.models.login_info.LoginInfoModel;
import com.example.sozax.bl.models.souko.SoukosModel;
import com.example.sozax.bl.models.syuko_denpyo.SyukoDenpyoModel;
import com.example.sozax.bl.models.syuko_denpyo.SyukoDenpyosModel;
import com.example.sozax.bl.models.syuko_sagyo.SyukoSagyoModel;
import com.example.sozax.bl.models.tensyo.TensyosModel;
import com.example.sozax.common.ResultClass;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.LinkedHashMap;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SyukoSagyoController {

    public static class PostSyukoSagyosTask extends AsyncTask<SyukoDenpyosModel, Void, ResultClass> {

        // 非同期処理
        @Override
        protected ResultClass doInBackground(SyukoDenpyosModel... syukoDenpyosModel) {

            ResultClass ret = new ResultClass();

            // ModelデータをJSONファイルに変換
            Gson gson = new Gson();
            String s = gson.toJson(syukoDenpyosModel[0]);

            // リクエストボディを作成
            final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(mediaType,s);

            // リクエストを作成
            final Request request = new Request.Builder()
                    //.url("http://192.168.10.214:55500/api/syukosagyo/post/")
                    .url("http://192.168.244.181:55500/api/syukosagyo/post/")
                    .headers(Headers.of(new LinkedHashMap<String, String>()))
                    .post(requestBody)
                    .build();

            // リクエストを送信&レスポンス取得
            final OkHttpClient client = new OkHttpClient.Builder().build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            // エラー発生
            if(!response.isSuccessful())
            {
                ret.Is_error = true;
                ret.Message = response.message();
            }

            String responseBody = "";
            try {
                responseBody = response.body().string();
            } catch (IOException e) {
                ret = new TensyosModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            // JSONファイルからModelデータに変換
            ret = gson.fromJson(responseBody, ResultClass.class);

            return ret;
        }
    }
}

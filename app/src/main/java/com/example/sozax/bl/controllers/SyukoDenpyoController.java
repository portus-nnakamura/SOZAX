package com.example.sozax.bl.controllers;

import android.os.AsyncTask;

import com.example.sozax.bl.models.syuko_denpyo.SyukoDenpyoConditionModel;
import com.example.sozax.bl.models.syuko_denpyo.SyukoDenpyoModel;
import com.example.sozax.bl.models.syuko_denpyo.SyukoDenpyosModel;
import com.example.sozax.bl.models.version_info.VersionInfoModel;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.LinkedHashMap;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SyukoDenpyoController {

    //region 1件取得

    public static class GetSyukoDenpyoTask extends AsyncTask<SyukoDenpyoConditionModel, Void, SyukoDenpyoModel> {

        // 非同期処理
        @Override
        protected SyukoDenpyoModel doInBackground(SyukoDenpyoConditionModel... syukoDenpyoConditionModel) {

            SyukoDenpyoModel ret = null;

            final Request request = new Request.Builder()
                    .url("http://192.168.10.214:55500/api/syukodenpyo/get/" + syukoDenpyoConditionModel[0].Syukono + "/"
                            + syukoDenpyoConditionModel[0].Kaicd + "/" + syukoDenpyoConditionModel[0].Soukocd + "/"
                            + syukoDenpyoConditionModel[0].Sagyodate)
                    .headers(Headers.of(new LinkedHashMap<String, String>()))
                    .build();

            final OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                ret = new SyukoDenpyoModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            if (response.isSuccessful() == false)
            {
                ret = new SyukoDenpyoModel();
                ret.Is_error = true;
                ret.Message = response.message();
                return  ret;
            }

            String s = "";
            try {
                s = response.body().string();
            } catch (IOException e) {
                ret = new SyukoDenpyoModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            // JSONファイルからModelデータに変換
            Gson gson = new Gson();
            ret = gson.fromJson(s, SyukoDenpyoModel.class);

            return ret;
        }
    }

    //endregion

    //region 複数件取得

    public static class GetSyukoDenpyosTask extends AsyncTask<SyukoDenpyoConditionModel, Void, SyukoDenpyosModel> {

        // 非同期処理
        @Override
        protected SyukoDenpyosModel doInBackground(SyukoDenpyoConditionModel... syukoDenpyoConditionModel) {

            SyukoDenpyosModel ret = null;

            final Request request = new Request.Builder()
                    .url("http://192.168.10.214:55500/api/syukodenpyo/get/" + syukoDenpyoConditionModel[0].Kaicd + "/"
                            + syukoDenpyoConditionModel[0].Soukocd + "/" + syukoDenpyoConditionModel[0].Sagyodate)
                    .headers(Headers.of(new LinkedHashMap<String, String>()))
                    .build();

            final OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                ret = new SyukoDenpyosModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            if (response.isSuccessful() == false)
            {
                ret = new SyukoDenpyosModel();
                ret.Is_error = true;
                ret.Message = response.message();
                return  ret;
            }

            String s = "";
            try {
                s = response.body().string();
            } catch (IOException e) {
                ret = new SyukoDenpyosModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            // JSONファイルからModelデータに変換
            Gson gson = new Gson();
            ret = gson.fromJson(s, SyukoDenpyosModel.class);

            return ret;
        }
    }

    //endregion

    //region 複数権取得(作業中)

    public static class GetSyukoDenpyos_SagyochuTask extends AsyncTask<SyukoDenpyoConditionModel, Void, SyukoDenpyosModel> {

        // 非同期処理
        @Override
        protected SyukoDenpyosModel doInBackground(SyukoDenpyoConditionModel... syukoDenpyoConditionModel) {

            SyukoDenpyosModel ret = null;

            final Request request = new Request.Builder()
                    .url("http://192.168.10.214:55500/api/syukodenpyo/get/sagyochu/" + syukoDenpyoConditionModel[0].Kaicd + "/"
                            + syukoDenpyoConditionModel[0].Soukocd + "/" + syukoDenpyoConditionModel[0].Sgytantocd + "/"
                            + syukoDenpyoConditionModel[0].Sagyodate)
                    .headers(Headers.of(new LinkedHashMap<String, String>()))
                    .build();

            final OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                ret = new SyukoDenpyosModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            if (response.isSuccessful() == false)
            {
                ret = new SyukoDenpyosModel();
                ret.Is_error = true;
                ret.Message = response.message();
                return  ret;
            }

            String s = "";
            try {
                s = response.body().string();
            } catch (IOException e) {
                ret = new SyukoDenpyosModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            // JSONファイルからModelデータに変換
            Gson gson = new Gson();
            ret = gson.fromJson(s, SyukoDenpyosModel.class);

            return ret;
        }
    }

    //endregion

    //region
    public static class GetSyukos_SagyomichakusyuTask extends AsyncTask<SyukoDenpyoConditionModel, Void, SyukoDenpyosModel> {

        // 非同期処理
        @Override
        protected SyukoDenpyosModel doInBackground(SyukoDenpyoConditionModel... syukoDenpyoConditionModel) {

            SyukoDenpyosModel ret = null;

            final Request request = new Request.Builder()
                    .url("http://192.168.10.214:55500/api/syukodenpyo/get/sagyomichakusyu/" + syukoDenpyoConditionModel[0].Kaicd + "/"
                            + syukoDenpyoConditionModel[0].Soukocd + "/" + syukoDenpyoConditionModel[0].Sagyodate)
                    .headers(Headers.of(new LinkedHashMap<String, String>()))
                    .build();

            final OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                ret = new SyukoDenpyosModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            if (response.isSuccessful() == false)
            {
                ret = new SyukoDenpyosModel();
                ret.Is_error = true;
                ret.Message = response.message();
                return  ret;
            }

            String s = "";
            try {
                s = response.body().string();
            } catch (IOException e) {
                ret = new SyukoDenpyosModel();
                ret.Is_error = true;
                ret.Message = e.getMessage();
                return ret;
            }

            // JSONファイルからModelデータに変換
            Gson gson = new Gson();
            ret = gson.fromJson(s, SyukoDenpyosModel.class);

            return ret;
        }
    }

}

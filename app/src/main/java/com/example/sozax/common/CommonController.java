package com.example.sozax.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.LinkedHashMap;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommonController {

    //region インスタンス変数

    /**
     * デフォルトルート
     */
    public static final String defaultRoute = "http://192.168.10.108:55500/api/";
//        public static final String defaultRoute = "http://192.168.10.214:55500/api/";

    //endregion

    //region 取得

    /**
     * 引数のルートを元にリクエストメッセージを作成して、レスポンスを取得する
     *
     * @param route ルート
     * @return レスポンス
     * @throws IOException
     */
    @SuppressWarnings("JavaDoc")
    public static Response getResponse(final String route) throws IOException {

        // リクエストメッセージを作成
        final Request request = new Request.Builder()
                .url(defaultRoute + route)
                .headers(Headers.of(new LinkedHashMap<String, String>()))
                .build();

        final OkHttpClient client = new OkHttpClient.Builder()
                .build();

        // リクエストを実行して、レスポンスを取得
        return client.newCall(request).execute();

    }

    //endregion

    //region 登録

    /**
     * 引数のルートと登録データを元にリクエストメッセージを作成して、レスポンスを取得する
     *
     * @param route    ルート
     * @param postData 登録データ
     * @return レスポンス
     * @throws IOException
     */
    @SuppressWarnings("JavaDoc")
    public static <T> Response executePost(final String route, T postData) throws Exception {

        // 登録データをJSONファイルに変換
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String s = gson.toJson(postData);

        // リクエストボディを作成
        final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        //noinspection deprecation
        RequestBody requestBody = RequestBody.create(mediaType, s);

        // リクエストメッセージを作成
        final Request request = new Request.Builder()
                .url(defaultRoute + route)
                .headers(Headers.of(new LinkedHashMap<String, String>()))
                .post(requestBody)
                .build();

        final OkHttpClient client = new OkHttpClient.Builder().build();

        // リクエストを実行して、レスポンスを取得
        return client.newCall(request).execute();

    }

    //endregion

    //region 更新

    /**
     * 引数のルートと更新データを元にリクエストメッセージを作成して、レスポンスを取得する
     *
     * @param route   ルート
     * @param putData 更新データ
     * @return レスポンス
     * @throws IOException
     */
    @SuppressWarnings("JavaDoc")
    public static <T> Response executePut(final String route, T putData) throws Exception {

        // 登録データをJSONファイルに変換
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String s = gson.toJson(putData);

        // リクエストボディを作成
        final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        //noinspection deprecation
        RequestBody requestBody = RequestBody.create(mediaType, s);

        // リクエストメッセージを作成
        final Request request = new Request.Builder()
                .url(defaultRoute + route)
                .headers(Headers.of(new LinkedHashMap<String, String>()))
                .put(requestBody)
                .build();

        final OkHttpClient client = new OkHttpClient.Builder().build();

        // リクエストを実行して、レスポンスを取得
        return client.newCall(request).execute();

    }

    //endregion

    //region 削除

    /**
     * 引数のルートと削除データを元にリクエストメッセージを作成して、レスポンスを取得する
     *
     * @param route      ルート
     * @param deleteData 削除データ
     * @return レスポンス
     * @throws IOException
     */
    @SuppressWarnings("JavaDoc")
    public static <T> Response executeDelete(final String route, T deleteData) throws Exception {

        // 登録データをJSONファイルに変換
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
        String s = gson.toJson(deleteData);

        // リクエストボディを作成
        final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        //noinspection deprecation
        RequestBody requestBody = RequestBody.create(mediaType, s);

        // リクエストメッセージを作成
        final Request request = new Request.Builder()
                .url(defaultRoute + route)
                .headers(Headers.of(new LinkedHashMap<String, String>()))
                .delete(requestBody)
                .build();

        final OkHttpClient client = new OkHttpClient.Builder().build();

        // リクエストを実行して、レスポンスを取得
        return client.newCall(request).execute();

    }

    //endregion

}

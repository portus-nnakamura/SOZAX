package com.example.sozax.bl.models.tensyo;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;


class AsyncHttpRequest extends AsyncTask<Call, Void, Response> {

    private Activity mainActivity;

    public AsyncHttpRequest(Activity activity) {

        // 呼び出し元のアクティビティ
        this.mainActivity = activity;
    }

    @Override
    protected Response doInBackground(okhttp3.Call... call) {

        try {
            return call[0].execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  null;
    }
}
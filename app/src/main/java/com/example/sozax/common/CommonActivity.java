package com.example.sozax.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.densowave.bhtsdk.keyremap.KeyRemapLibrary;
import com.example.sozax.R;
import com.example.sozax.bl.models.login_info.LoginInfoModel;

public abstract class CommonActivity extends AppCompatActivity {

    //region メンバ変数

    // ログイン情報
    public LoginInfoModel loginInfo;

    //endregion

    //region onCreate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ログイン情報を取得
        Intent intent = getIntent();
        loginInfo = (LoginInfoModel) intent.getSerializableExtra(getResources().getString(R.string.intent_key_login_info));
    }

    //endregion

    //region ログイン情報を表示

    // ログイン情報を表示
    public void DisplayLoginInfo() {
//        TextView txtLoginInfo = findViewById(R.id.txtLoginInfo);
//        SimpleDateFormat sdf = new SimpleDateFormat("M/dd(E)", DateFormatSymbols.getInstance(Locale.JAPAN));
//        txtLoginInfo.setText(logininfo.getOfficeInfo().getName() + "　" + logininfo.getRepresentativeInfo().getName() + "\n" + sdf.format(logininfo.getWorkingday()) + "　" + logininfo.getWarehouseInfo().getName());
    }

    //endregion



}
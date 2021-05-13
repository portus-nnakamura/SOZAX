package com.example.sozax.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.densowave.bhtsdk.keyremap.KeyRemapLibrary;
import com.example.sozax.R;
import com.example.sozax.bl.models.login_info.LoginInfoModel;
import com.example.sozax.bl.models.syuko_denpyo.SyukoDenpyoModel;
import com.example.sozax.bl.models.syuko_denpyo.SyukoDenpyosModel;
import com.example.sozax.ui.GoodsIssuePage1Activity;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.example.sozax.common.CommonFunction.substringByBytes;

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
        TextView txtLoginTensyo = findViewById(R.id.txtLoginTensyo);
        txtLoginTensyo.setText(substringByBytes(loginInfo.Tensyonm, 10));

        TextView txtLoginSgytanto = findViewById(R.id.txtLoginSgytanto);
        txtLoginSgytanto.setText(substringByBytes(loginInfo.Sgytantonm, 10));

        TextView txtLoginSouko = findViewById(R.id.txtLoginSouko);
        txtLoginSouko.setText(substringByBytes(loginInfo.Soukornm, 10));

        SimpleDateFormat sdf = new SimpleDateFormat("M/dd(E)", DateFormatSymbols.getInstance(Locale.JAPAN));
        TextView txtLoginSgydate = findViewById(R.id.txtLoginSgydate);
        txtLoginSgydate.setText(sdf.format(loginInfo.Sgydate));
    }

    //endregion

    //region ログイン情報を長押しクリック

    public class clLoginInfo_LongClick implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {

            StringBuilder sb = new StringBuilder();
            sb.append("事業所:");
            sb.append(loginInfo.Tensyonm);
            sb.append("\r\n");
            sb.append("担当者:");
            sb.append(loginInfo.Sgytantonm);
            sb.append("\r\n");
            sb.append("倉　庫:");
            sb.append(loginInfo.Soukornm);
            sb.append("\r\n");
            sb.append("作業日:");
            SimpleDateFormat sdf = new SimpleDateFormat("M/dd(E)", DateFormatSymbols.getInstance(Locale.JAPAN));
            sb.append(sdf.format(loginInfo.Sgydate));

            Toast toast = Toast.makeText(getApplicationContext(),sb.toString(),Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();

            return false;
        }
    }

    //endregion

    //region アプリ終了ボタンの処理

    public class btnExit_Click implements View.OnClickListener {

        private final Activity activity;

        public btnExit_Click(Activity _activity) {
            activity = _activity;
        }

        @Override
        public void onClick(View v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("アプリを終了してもよろしいですか？")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAndRemoveTask();
                        }
                    })
                    .setNegativeButton("キャンセル", null)
                    .setCancelable(true);

            builder.show();
        }
    }

    //endregion

    //region 振動する

    public void Vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] vibratePattern = {0, 500, 100, 500};
        vibrator.vibrate(vibratePattern, -1); // ここの-1を1にするとリピートします。
        return;
    }

    //endregion
}
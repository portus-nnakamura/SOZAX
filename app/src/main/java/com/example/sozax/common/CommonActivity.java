package com.example.sozax.common;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;

import com.densowave.bhtsdk.keyremap.KeyRemapLibrary;
import com.example.sozax.R;
import com.example.sozax.bl.com.LoginInfo;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;

public abstract class CommonActivity extends AppCompatActivity implements KeyRemapLibrary.KeyRemapListener{

    //region メンバ変数

    // DENSO固有キーの割り当てライブラリ
    public KeyRemapLibrary mKeyRemapLibrary;

    // ログイン情報
    public LoginInfo logininfo;

    //endregion

    //region onCreate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);

        // ログイン情報を取得
        Intent intent = getIntent();
        logininfo = (LoginInfo) intent.getSerializableExtra("LOGININFO");
    }

    //endregion

    //region ログイン情報を表示

    // ログイン情報を表示
    public void DisplayLoginInfo()
    {
//        TextView txtLoginInfo = findViewById(R.id.txtLoginInfo);
//        SimpleDateFormat sdf = new SimpleDateFormat("M/dd(E)", DateFormatSymbols.getInstance(Locale.JAPAN));
//        txtLoginInfo.setText(logininfo.getOfficeInfo().getName() + "　" + logininfo.getRepresentativeInfo().getName() + "\n" + sdf.format(logininfo.getWorkingday()) + "　" + logininfo.getWarehouseInfo().getName());
    }

    //endregion

    //region 非同期処理

    public class Async extends AsyncTask<String, Void, String> {
        private Activity mActivity;

        public Async(Activity activity) {
            mActivity = activity;
        }

        @Override
        protected String doInBackground(String... string) {
            return null;
        }
    }

    //endregion

    //region アプリ終了ボタンの処理

    public class btnExit_Click implements View.OnClickListener {

        private Activity activity;

        public btnExit_Click(Activity _activity)
        {
            activity = _activity;
        }

        @Override
        public void onClick(View v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("アプリを終了してもよろしいですか？")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            moveTaskToBack(true);
                        }
                    })
                    .setNegativeButton("キャンセル", null)
                    .setCancelable(true);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            TextView msgTxt = alertDialog.findViewById(android.R.id.message);
            msgTxt.setTextSize((float) 14.0);

        }
    }

    //endregion

    //region 振動する

    public void Vibrate()
    {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] vibratePattern = {0, 500, 100, 500};
        vibrator.vibrate(vibratePattern, -1); // ここの-1を1にするとリピートします。
        return;
    }

    //endregion
}
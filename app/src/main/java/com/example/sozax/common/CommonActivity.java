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

    // DENSO固有キーの割り当てライブラリ
    public KeyRemapLibrary mKeyRemapLibrary;

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

    /**
     * @param text    対象となる文字列
     * @param index   切り出すバイト数
     * @param charset 文字コード
     * @return
     */
    public static String substringByBytes(String text, int index) {

        String ret = "";

        try {

            int textByteCnt = 0;
            for (int i = 0; i < text.length(); i++) {

                //対象となる文字列を先頭から1文字切り出し、その文字のバイト数を調べます。
                String tmpText = text.substring(i, i + 1);
                byte[] tmpTextByte = tmpText.getBytes("Shift_JIS");

                //切り出した文字を変数retに追加した際のバイト数が指定バイト数より大きければ、変数retを返します。
                if (textByteCnt + tmpTextByte.length > index) {
                    return ret;
                } else {
                    ret += tmpText;
                    textByteCnt = textByteCnt + tmpTextByte.length;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }
}
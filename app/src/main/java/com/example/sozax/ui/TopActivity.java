package com.example.sozax.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.sozax.R;
import com.example.sozax.bl.controllers.VersionInfoController;
import com.example.sozax.bl.models.login_info.LoginInfoModel;
import com.example.sozax.bl.models.version_info.VersionInfoModel;
import com.example.sozax.common.CommonActivity;

import java.util.Date;

public class TopActivity extends CommonActivity {

    //region インスタンス変数

    // アプリ内のバージョン情報
    VersionInfoModel app_version_info;
    // DB内のバージョン情報
    VersionInfoModel db_version_info;

    //endregion

    //region 初回起動

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);

        // アプリ内のバージョン情報を取得
        try {
            PackageInfo pckInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            app_version_info = new VersionInfoModel();
            app_version_info.Versioncd = pckInfo.versionCode;
            app_version_info.Versionnm = pckInfo.versionName;
        } catch (PackageManager.NameNotFoundException ex) {
            // エラー内容を出力
            OutputErrorMessage(java.text.MessageFormat.format(getResources().getString(R.string.top_activity_message1), ex.getMessage()));
            return;
        }

        // 画面にバージョン名を出力
        ((TextView) findViewById(R.id.txtVersionName)).setText(app_version_info.Versionnm);

        // DB内のバージョン情報を取得
        new GetVersionInfoTask().execute();
    }

    //endregion

    //region 開始ボタンをクリック

    public void btnStart_Click(View view) {

        // ログイン情報を取得
        loginInfo = new LoginInfoModel();
        SharedPreferences preferences = getSharedPreferences(getResources().getString(R.string.login_preferences_file_name), Context.MODE_PRIVATE);

        // 会社
        loginInfo.Kaicd = preferences.getInt(getResources().getString(R.string.login_preferences_key_kaicd), 40);

        // 店所
        loginInfo.Tensyocd = preferences.getInt(getResources().getString(R.string.login_preferences_key_tensyocd), 0);
        loginInfo.Tensyonm = preferences.getString(getResources().getString(R.string.login_preferences_key_tensyonm), "");

        // 作業担当者
        loginInfo.Sgytantocd = preferences.getInt(getResources().getString(R.string.login_preferences_key_sgytantocd), 0);
        loginInfo.Sgytantonm = preferences.getString(getResources().getString(R.string.login_preferences_key_sgytantonm), "");

        // 倉庫
        loginInfo.Soukocd = preferences.getInt(getResources().getString(R.string.login_preferences_key_soukocd), 0);
        loginInfo.Soukonm = preferences.getString(getResources().getString(R.string.login_preferences_key_soukonm), "");
        loginInfo.Soukornm = preferences.getString(getResources().getString(R.string.login_preferences_key_soukornm), "");

        // 作業日時
        long sgydate = preferences.getLong(getResources().getString(R.string.login_preferences_key_sgydate), new Date().getTime());
        loginInfo.Sgydate = new Date(sgydate);

        // 更新日時
        long updatedate = preferences.getLong(getResources().getString(R.string.login_preferences_key_updatedate), 0);
        loginInfo.Updatedate = new Date(updatedate);

        // 現在日時を取得
        Date nowDate = new Date();

        if (loginInfo.Updatedate.getYear() == nowDate.getYear() &&
                loginInfo.Updatedate.getMonth() == nowDate.getMonth() &&
                loginInfo.Updatedate.getDay() == nowDate.getDay()) {
            // 更新日と現在日が一致する場合、メニュー画面に遷移
            Intent intent = new Intent(getApplication(), MenuActivity.class);
            intent.putExtra(getResources().getString(R.string.intent_key_login_info), loginInfo);

            startActivity(intent);
            finish();
        } else {
            // 更新日と現在日が一致しない場合、ログイン画面に遷移
            Intent intent = new Intent(getApplication(), LoginActivity.class);
            intent.putExtra(getResources().getString(R.string.intent_key_login_info), loginInfo);
            intent.putExtra(getResources().getString(R.string.intent_key_previous_activity_name), this.getComponentName().getClassName());

            startActivity(intent);
            finish();
        }
    }

    //endregion

    //region バージョン情報取得

    @SuppressLint("StaticFieldLeak")
    public class GetVersionInfoTask extends VersionInfoController.GetVersionInfoTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // 操作を無効化
            setEnabledOperation(false);
        }

        /**
         * バックグランド処理が完了し、UIスレッドに反映する
         */
        @Override
        protected void onPostExecute(VersionInfoModel versionInfo) {

            try {

                // DBの取得結果をセット
                db_version_info = versionInfo;

                // エラー発生
                if (db_version_info.Is_error) {
                    OutputErrorMessage(java.text.MessageFormat.format(getResources().getString(R.string.top_activity_message2), db_version_info.Message));
                    return;
                }

                // 該当データなし
                if (db_version_info.Versioncd == 0) {
                    OutputErrorMessage(getResources().getString(R.string.top_activity_message3));
                    return;
                }

                // アプリ内とDBの、バージョンコードを比較して、
                // 最新のバージョンであるかチェックする
                if (db_version_info.Versioncd > app_version_info.Versioncd) {
                    OutputInformationMessage(getResources().getString(R.string.top_activity_message4));
                }

            } finally {

                // 操作を有効化
                setEnabledOperation(true);

            }
        }
    }

    //endregion
}
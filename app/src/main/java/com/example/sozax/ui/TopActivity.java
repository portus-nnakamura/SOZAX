package com.example.sozax.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sozax.R;
import com.example.sozax.bl.controllers.SyukoSagyoController;
import com.example.sozax.bl.controllers.VersionInfoController;
import com.example.sozax.bl.models.login_info.LoginInfoModel;
import com.example.sozax.bl.models.syuko_denpyo.SyukoDenpyoModel;
import com.example.sozax.bl.models.syuko_denpyo.SyukoDenpyosModel;
import com.example.sozax.bl.models.syuko_sagyo.SyukoSagyoModel;
import com.example.sozax.bl.models.version_info.VersionInfoModel;
import com.example.sozax.common.CommonActivity;
import com.example.sozax.common.ResultClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
            PackageInfo pckInfo = getPackageManager().getPackageInfo("dadsadsadsa", 0);
            app_version_info = new VersionInfoModel();
            app_version_info.Versioncd = pckInfo.versionCode;
            app_version_info.Versionnm = pckInfo.versionName;
        } catch (PackageManager.NameNotFoundException ex) {
            // エラー内容を出力
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("エラー");
            String template = "アプリ内のバージョン情報の取得に失敗しました\r\nパッケージ名：{0}が見つかりません";
            builder.setMessage(java.text.MessageFormat.format(template,ex.getMessage()));

            AlertDialog alertDialog = builder.create();

            // アラートダイアログを表示
            alertDialog.show();

            // メッセージのフォントサイズを変更
            TextView msgTxt = alertDialog.findViewById(android.R.id.message);
            //msgTxt.setTextSize((float) 11.2);

            return;
        }

        // 画面にバージョン名を出力
        ((TextView) findViewById(R.id.txtVersionName)).setText(app_version_info.Versionnm);

        // DB内のバージョン情報を取得
        new GetVersionInfoTask().execute();

        // お試し出庫作業登録
        SyukoDenpyoModel syukoDenpyoModel = new SyukoDenpyoModel();
        syukoDenpyoModel.Syukosgyjokyo = new SyukoSagyoModel();
        syukoDenpyoModel.Syukosgyjokyo.Syukono = 1;
        syukoDenpyoModel.Syukosgyjokyo.Kaicd = 66;
        syukoDenpyoModel.Syukosgyjokyo.Tencd = 2;
        syukoDenpyoModel.Syukosgyjokyo.Sgytantocd = 2;
        syukoDenpyoModel.Syukosgyjokyo.Soukocd = 777;
        String strDate = "2015-10-24";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        syukoDenpyoModel.Syukosgyjokyo.Sgydate = date;
        syukoDenpyoModel.Syukosgyjokyo.Sgyjokyokbn = 1;
        syukoDenpyoModel.Syukosgyjokyo.Syukeicd = 123;

        SyukoDenpyosModel syukoDenpyosModel = new SyukoDenpyosModel();
        syukoDenpyosModel.SyukoDenpyos = new SyukoDenpyoModel[1];
        syukoDenpyosModel.SyukoDenpyos[0] = syukoDenpyoModel;

        new DeleteSyukoSagyosTask().execute(syukoDenpyosModel);
    }

    //endregion

    //region 開始ボタンをクリック

    public void btnStart_Click(View view) {

        // ログイン情報を取得
        loginInfo = new LoginInfoModel();
        SharedPreferences preferences = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);

        // 会社
        loginInfo.Kaicd = preferences.getInt("Kaicd", 40);

        // 店所
        loginInfo.Tensyocd = preferences.getInt("Tensyocd", 0);
        loginInfo.Tensyonm = preferences.getString("Tensyonm", "");

        // 作業担当者
        loginInfo.Sgytantocd = preferences.getInt("Sgytantocd", 0);
        loginInfo.Sgytantonm = preferences.getString("Sgytantonm", "");

        // 倉庫
        loginInfo.Soukocd = preferences.getInt("Soukocd", 0);
        loginInfo.Soukonm = preferences.getString("Soukonm", "");

        // 作業日時
        long sgydate = preferences.getLong("Sgydate", new Date().getTime());
        loginInfo.Sgydate = new Date(sgydate);

        // 更新日時
        long updatedate = preferences.getLong("Updatedate", 0);
        loginInfo.Updatedate = new Date(updatedate);

        // 現在日時を取得
        Date nowDate = new Date();

        if (loginInfo.Updatedate.getYear() == nowDate.getYear() &&
                loginInfo.Updatedate.getMonth() == nowDate.getMonth() &&
                loginInfo.Updatedate.getDay() == nowDate.getDay()) {
            // 更新日と現在日が一致する場合、メニュー画面に遷移
            Intent intent = new Intent(getApplication(), MenuActivity.class);
            intent.putExtra("LoginInfo", loginInfo);

            startActivity(intent);
        } else {
            // 更新日と現在日が一致しない場合、ログイン画面に遷移
            Intent intent = new Intent(getApplication(), LoginActivity.class);
            intent.putExtra("LoginInfo", loginInfo);
            intent.putExtra("PreviousActivityName", this.getComponentName().getClassName());

            startActivity(intent);
        }
    }

    //endregion

    //region バージョン情報取得

    @SuppressLint("StaticFieldLeak")
    public class GetVersionInfoTask extends VersionInfoController.GetVersionInfoTask {

        /**
         * バックグランド処理が完了し、UIスレッドに反映する
         */
        @Override
        protected void onPostExecute(VersionInfoModel versionInfo) {

            // DBの取得結果をセット
            db_version_info = versionInfo;

            // エラー発生
            if (db_version_info.Is_error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TopActivity.this);
                builder.setTitle("エラー");
                builder.setMessage(db_version_info.Message);

                builder.show();
                return;
            }

            // 該当データなし
            if (db_version_info.Versioncd == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TopActivity.this);
                builder.setMessage("最新のバージョン情報が見つかりませんでした。");

                builder.show();
                return;
            }

            // アプリ内とDBの、バージョンコードを比較して、
            // 最新のバージョンであるかチェックする
            if (db_version_info.Versioncd > app_version_info.Versioncd) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TopActivity.this);
                builder.setTitle("お知らせ");
                builder.setMessage("最新のバージョンにアップデート可能です。");

                builder.show();
            }
        }
    }

    //endregion

    //region バージョン情報取得

    @SuppressLint("StaticFieldLeak")
    public class DeleteSyukoSagyosTask extends SyukoSagyoController.DeleteSyukoSagyosTask {

        /**
         * バックグランド処理が完了し、UIスレッドに反映する
         */
        @Override
        protected void onPostExecute(ResultClass resultClass) {

            if(resultClass.Is_error)
            {
                Toast.makeText(TopActivity.this,resultClass.Message,Toast.LENGTH_LONG).show();
                return;
            }

            Toast.makeText(TopActivity.this,"更新に成功しました。",Toast.LENGTH_LONG).show();
        }
    }

    //region DENSO固有ボタンの設定

    @Override
    public void onKeyRemapCreated() {
    }

    //endregion
}
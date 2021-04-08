package com.example.sozax.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.sozax.R;
import com.example.sozax.bl.controllers.SgytantoController;
import com.example.sozax.bl.controllers.SoukoController;
import com.example.sozax.bl.controllers.TensyoController;
import com.example.sozax.bl.models.login_info.LoginInfoModel;
import com.example.sozax.bl.models.sgytanto.SgytantoModel;
import com.example.sozax.bl.models.sgytanto.SgytantosModel;
import com.example.sozax.bl.models.souko.SoukoModel;
import com.example.sozax.bl.models.souko.SoukosModel;
import com.example.sozax.bl.models.tensyo.TensyoModel;
import com.example.sozax.bl.models.tensyo.TensyosModel;
import com.example.sozax.common.CommonActivity;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Date;

public class LoginActivity extends CommonActivity {

    //region インスタンス変数

    // 店所一覧
    private TensyosModel tensyosModel;
    // 作業担当者一覧
    private SgytantosModel sgytantosModel;
    // 倉庫一覧
    private SoukosModel soukosModel;

    //endregion

    //region 初回起動

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 前回ログイン情報があるか
        boolean exsistsPreviousLoginInfo = loginInfo != null;

        if (!exsistsPreviousLoginInfo) {
            // ログイン情報がない場合、インスタンス生成
            loginInfo = new LoginInfoModel();

            // 会社コード
            loginInfo.Kaicd = 40;
        }

        // 店所一覧を取得
        getTensyosTask.execute(loginInfo);
    }

    //endregion

    //region ログインボタンをクリック

    public void btnLogin_Click(View view) {

        final TextInputLayout tilTensyo = findViewById(R.id.tilTensyo);
        final TextInputLayout tilSgytanto = findViewById(R.id.tilSgytanto);
        final TextInputLayout tilSouko = findViewById(R.id.tilSouko);

        final AutoCompleteTextView txtTensyo = findViewById(R.id.txtTensyo);
        final AutoCompleteTextView txtSgytanto = findViewById(R.id.txtSgytanto);
        final AutoCompleteTextView txtSouko = findViewById(R.id.txtSouko);

        boolean isErr = false;
        if (txtTensyo.getText().toString().isEmpty()) {
            tilTensyo.setError("事業所を選択して下さい");
            isErr = true;
        } else {
            tilTensyo.setError("");
        }

        if (txtSgytanto.getText().toString().isEmpty()) {
            tilSgytanto.setError("担当者を選択して下さい");
            isErr = true;
        } else {
            tilSgytanto.setError("");
        }

        if (txtSouko.getText().toString().isEmpty()) {
            txtSouko.setError("倉庫を選択して下さい");
            isErr = true;
        } else {
            tilSouko.setError("");
        }

        if (isErr) {
            // エラー時に振動する
            Vibrate();
            return;
        }

        // ログイン情報セット

        // 会社
        loginInfo.Kaicd = 40;

        // 店所
        loginInfo.Tensyocd = tensyosModel.Tensyos[txtTensyo.getListSelection()].Tencd;
        loginInfo.Tensyonm = tensyosModel.Tensyos[txtTensyo.getListSelection()].Tennm;

        // 作業担当者
        loginInfo.Sgytantocd = sgytantosModel.Sgytantos[txtSgytanto.getListSelection()].Sgytantocd;
        loginInfo.Sgytantonm = sgytantosModel.Sgytantos[txtSgytanto.getListSelection()].Sgytantonm;

        // 倉庫
        loginInfo.Soukocd = soukosModel.Soukos[txtSouko.getListSelection()].Soukocd;
        loginInfo.Soukonm = soukosModel.Soukos[txtSouko.getListSelection()].Soukonm;

        // 作業日時
        if (loginInfo.Sgydate == null)
        {
            loginInfo.Sgydate = new Date();
        }

        // 更新日時
        loginInfo.Updatedate = new Date();

        // ログイン情報書き込み
        SharedPreferences preferences = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // 会社
        editor.putInt("Kaicd",loginInfo.Kaicd);

        // 店所
        editor.putInt("Tensyocd",loginInfo.Tensyocd);
        editor.putString("Tensyonm",loginInfo.Tensyonm);

        // 作業担当者
        editor.putInt("Sgytantocd",loginInfo.Sgytantocd);
        editor.putString("Sgytantonm",loginInfo.Sgytantonm);

        // 倉庫
        editor.putInt("Soukocd",loginInfo.Soukocd);
        editor.putString("Soukonm",loginInfo.Soukonm);

        // 作業日時
        editor.putLong("Sgydate",loginInfo.Sgydate.getTime());

        // 更新日時
        editor.putLong("Updatedate",loginInfo.Updatedate.getTime());

        // 反映
        editor.apply();

        // メニュー画面に遷移
        Intent intent = new Intent(getApplication(), MenuActivity.class);
        intent.putExtra("LOGININFO", loginInfo);

        startActivity(intent);
    }

    //endregion

    //region 店所一覧取得

    private final GetTensyosTask getTensyosTask = new GetTensyosTask(this);

    @SuppressLint("StaticFieldLeak")
    private  class GetTensyosTask extends TensyoController.GetTensyosTask {

        private final Activity mainActivity;

        public GetTensyosTask(Activity activity) {
            mainActivity = activity;
        }

        /**
         * バックグランド処理が完了し、UIスレッドに反映する
         */
        @Override
        protected void onPostExecute(TensyosModel tensyos) {

            // 取得結果をセット
            tensyosModel = tensyos;

            // エラー発生
            if (tensyosModel.Is_error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setTitle("エラー");
                builder.setMessage(tensyosModel.Message);

                builder.show();
                return;
            }

            // 該当データなし
            if (tensyosModel.Tensyos == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setTitle("エラー");
                builder.setMessage("事業所データがありません。");

                builder.show();
                return;
            }

            // コントロールにデータを追加
            ArrayList<String> items = new ArrayList<String>();

            for (TensyoModel tensyoModel : tensyosModel.Tensyos) {
                items.add(tensyoModel.Tennm);
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mainActivity, R.layout.dropdown_menu_popup_item, items);

            final AutoCompleteTextView txtTensyos = findViewById(R.id.txtTensyo);
            txtTensyos.setAdapter(arrayAdapter);

            // 前回ログイン情報がある場合、前回の店所を選択する
            if (loginInfo != null && loginInfo.Tensyocd > 0) {
                // 前回ログイン時の店所を取得
                int i = 0;
                int selectedIndex = -1;
                for (TensyoModel tensyoModel : tensyosModel.Tensyos) {

                    if (tensyoModel.Tencd == loginInfo.Tensyocd) {
                        selectedIndex = i;
                        break;
                    }

                    i++;
                }

                // 前回ログイン時の店所を選択
                if (selectedIndex > -1) {
                    txtTensyos.setSelection(selectedIndex);
                }

                // 作業担当者一覧を取得
                getSgytantosTask.execute(loginInfo);

                // 倉庫一覧を取得
                getSoukosTask.execute(loginInfo);
            }
        }
    }

    //endregion

    //region 作業担当者一覧取得

    private final GetSgytantosTask getSgytantosTask = new GetSgytantosTask(this);

    @SuppressLint("StaticFieldLeak")
    private class GetSgytantosTask extends SgytantoController.GetSgytantosTask {

        private final Activity mainActivity;

        public GetSgytantosTask(Activity activity) {
            mainActivity = activity;
        }

        /**
         * バックグランド処理が完了し、UIスレッドに反映する
         */
        @Override
        protected void onPostExecute(SgytantosModel sgytantos) {

            // 取得結果をセット
            sgytantosModel = sgytantos;

            // エラー発生
            if (sgytantosModel.Is_error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setTitle("エラー");
                builder.setMessage(sgytantosModel.Message);

                builder.show();
                return;
            }

            // 該当データなし
            if (sgytantosModel.Sgytantos == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setTitle("エラー");
                builder.setMessage("作業担当者データがありません。");

                builder.show();
                return;
            }

            // コントロールにデータを追加
            ArrayList<String> items = new ArrayList<String>();

            for (SgytantoModel SgytantoModel : sgytantosModel.Sgytantos) {
                items.add(SgytantoModel.Sgytantonm);
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mainActivity, R.layout.dropdown_menu_popup_item, items);

            final AutoCompleteTextView txtSgytantos = findViewById(R.id.txtSgytanto);
            txtSgytantos.setAdapter(arrayAdapter);

            // 前回ログイン情報がある場合、前回の作業担当者を選択する
            if (loginInfo != null && loginInfo.Sgytantocd > 0) {
                // 前回ログイン時の作業担当者を取得
                int i = 0;
                int selectedIndex = -1;
                for (SgytantoModel sgytantoModel : sgytantosModel.Sgytantos) {

                    if (sgytantoModel.Sgytantocd == loginInfo.Sgytantocd) {
                        selectedIndex = i;
                        break;
                    }

                    i++;
                }

                // 前回ログイン時の作業担当者を選択
                if (selectedIndex > -1) {
                    txtSgytantos.setSelection(selectedIndex);
                }
            }
        }
    }

    //endregion

    //region 倉庫一覧取得

    private final GetSoukosTask getSoukosTask = new GetSoukosTask(this);

    @SuppressLint("StaticFieldLeak")
    private class GetSoukosTask extends SoukoController.GetSoukosTask {

        private final Activity mainActivity;

        public GetSoukosTask(Activity activity) {
            mainActivity = activity;
        }

        /**
         * バックグランド処理が完了し、UIスレッドに反映する
         */
        @Override
        protected void onPostExecute(SoukosModel soukos) {

            // 取得結果をセット
            soukosModel = soukos;

            // エラー発生
            if (soukosModel.Is_error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setTitle("エラー");
                builder.setMessage(soukosModel.Message);

                builder.show();
                return;
            }

            // 該当データなし
            if (soukosModel.Soukos == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setTitle("エラー");
                builder.setMessage("倉庫データがありません。");

                builder.show();
                return;
            }

            // コントロールにデータを追加
            ArrayList<String> items = new ArrayList<String>();

            for (SoukoModel SoukoModel : soukosModel.Soukos) {
                items.add(SoukoModel.Soukonm);
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mainActivity, R.layout.dropdown_menu_popup_item, items);

            final AutoCompleteTextView txtSoukos = findViewById(R.id.txtSouko);
            txtSoukos.setAdapter(arrayAdapter);

            // 前回ログイン情報がある場合、前回の倉庫を選択する
            if (loginInfo != null && loginInfo.Soukocd > 0) {
                // 前回ログイン時の倉庫を取得
                int i = 0;
                int selectedIndex = -1;
                for (SoukoModel soukoModel : soukosModel.Soukos) {

                    if (soukoModel.Soukocd == loginInfo.Soukocd) {
                        selectedIndex = i;
                        break;
                    }

                    i++;
                }

                // 前回ログイン時の作業担当者を選択
                if (selectedIndex > -1) {
                    txtSoukos.setSelection(selectedIndex);
                }
            }
        }
    }

    //endregion

    //region DENSO固有ボタンの設定

    @Override
    public void onKeyRemapCreated() {
    }

    //endregion
}
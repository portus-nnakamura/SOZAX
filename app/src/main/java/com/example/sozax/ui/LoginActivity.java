package com.example.sozax.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.example.sozax.R;
import com.example.sozax.bl.controllers.SgytantoController;
import com.example.sozax.bl.controllers.SoukoController;
import com.example.sozax.bl.controllers.TensyoController;
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

    // 店所選択Index
    private int tensyoSelectedIndex = -1;
    // 作業担当者選択Index
    private int sgytantoSelectedIndex = -1;
    // 倉庫選択Index
    private int soukoSelectedIndex = -1;

    // 店所取得中フラグ
    private boolean isTensyoGetting = false;
    // 作業担当者取得中フラグ
    private boolean isSgytantoGetting = false;
    // 倉庫取得中フラグ
    private  boolean isSoukoGetting = false;

    //endregion

    //region 初回起動

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 選択イベント追加
        final AutoCompleteTextView txtTensyo = findViewById(R.id.txtTensyo);
        txtTensyo.setOnItemClickListener(new TensyoItemClickListener());
        final AutoCompleteTextView txtSgytanto = findViewById(R.id.txtSgytanto);
        txtSgytanto.setOnItemClickListener(new SgytantoItemClickListener());
        final AutoCompleteTextView txtSouko = findViewById(R.id.txtSouko);
        txtSouko.setOnItemClickListener(new SoukoItemClickListener());

        // 店所一覧を取得
        new GetTensyosTask().execute(loginInfo);
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
            tilTensyo.setError(getResources().getString(R.string.login_activity_message1));
            isErr = true;
        } else {
            tilTensyo.setError("");
        }

        if (txtSgytanto.getText().toString().isEmpty()) {
            tilSgytanto.setError(getResources().getString(R.string.login_activity_message2));
            isErr = true;
        } else {
            tilSgytanto.setError("");
        }

        if (txtSouko.getText().toString().isEmpty()) {
            tilSouko.setError(getResources().getString(R.string.login_activity_message3));
            isErr = true;
        } else {
            tilSouko.setError("");
        }

        if (isErr) {
            // エラー時に振動する
            Vibrate();
            return;
        }

        // ログイン情報書き込み
        SharedPreferences preferences = getSharedPreferences(getResources().getString(R.string.login_preferences_file_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // 店所
        editor.putInt(getResources().getString(R.string.login_preferences_key_tensyocd), loginInfo.Tensyocd);
        editor.putString(getResources().getString(R.string.login_preferences_key_tensyonm), loginInfo.Tensyonm);

        // 作業担当者
        editor.putInt(getResources().getString(R.string.login_preferences_key_sgytantocd), loginInfo.Sgytantocd);
        editor.putString(getResources().getString(R.string.login_preferences_key_sgytantonm), loginInfo.Sgytantonm);

        // 倉庫
        editor.putInt(getResources().getString(R.string.login_preferences_key_soukocd), loginInfo.Soukocd);
        editor.putString(getResources().getString(R.string.login_preferences_key_soukonm), loginInfo.Soukonm);

        // 作業日時
        editor.putLong(getResources().getString(R.string.login_preferences_key_sgydate), loginInfo.Sgydate.getTime());

        // 更新日時
        editor.putLong(getResources().getString(R.string.login_preferences_key_updatedate), new Date().getTime());

        // 反映
        editor.apply();

        // 前画面のアクティビティ名を取得
        String previousActivityName = getIntent().getStringExtra(getResources().getString(R.string.intent_key_previous_activity_name));

        if (previousActivityName.equals(TopActivity.class.getName())) {
            // 前画面がトップ画面の場合、メニュー画面に遷移
            Intent intent = new Intent(getApplication(), MenuActivity.class);
            intent.putExtra(getResources().getString(R.string.intent_key_login_info), loginInfo);

            startActivity(intent);
            finish();
        } else if (previousActivityName.equals(MenuActivity.class.getName())) {
            // 前画面がメニュー画面の場合、閉じる
            Intent intent = new Intent();
            intent.putExtra(getResources().getString(R.string.intent_key_login_info), loginInfo);

            setResult(RESULT_OK, intent);
            finish();
        }
    }

    //endregion

    //region 店所一覧を取得

    @SuppressLint("StaticFieldLeak")
    private class GetTensyosTask extends TensyoController.GetTensyosTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(!isTensyoGetting && !isSgytantoGetting && !isSoukoGetting)
            {
                // タッチ操作を無効化
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                // プログレスバーを表示
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            isTensyoGetting = true;
        }

        /**
         * バックグランド処理が完了し、UIスレッドに反映する
         */
        @Override
        protected void onPostExecute(TensyosModel tensyos) {

            try {

                // エラー発生
                if (tensyos.Is_error) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("エラー");
                    builder.setMessage(java.text.MessageFormat.format(getResources().getString(R.string.login_activity_message4), tensyos.Message));
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                            // すべての一覧をクリア
                            ClearListData();

                        }
                    });

                    builder.show();

                    return;
                }

                // 該当データなし
                if (tensyos.Tensyos == null) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage(getResources().getString(R.string.login_activity_message7));
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                            // すべての一覧をクリア
                            ClearListData();

                        }
                    });

                    builder.show();

                    return;
                }

                // 取得結果をセット
                tensyosModel = tensyos;

                // コントロールにデータを追加
                ArrayList<String> items = new ArrayList<String>();

                for (TensyoModel tensyoModel : tensyosModel.Tensyos) {
                    items.add(tensyoModel.Tennm);
                }

                UniqueArrayAdapter arrayAdapter = new UniqueArrayAdapter(LoginActivity.this, R.layout.dropdown_menu_popup_item, items);

                final AutoCompleteTextView txtTensyo = findViewById(R.id.txtTensyo);
                txtTensyo.setAdapter(arrayAdapter);

                // 前回ログイン情報がある場合、前回の店所を選択する
                tensyoSelectedIndex = -1;
                if (loginInfo != null && loginInfo.Tensyocd > 0) {
                    // 前回ログイン時の店所を取得
                    int i = 0;
                    for (TensyoModel tensyoModel : tensyosModel.Tensyos) {

                        if (tensyoModel.Tencd == loginInfo.Tensyocd) {
                            tensyoSelectedIndex = i;
                            break;
                        }

                        i++;
                    }

                    // 前回ログイン時の店所を選択
                    if (tensyoSelectedIndex > -1) {
                        txtTensyo.setText(tensyos.Tensyos[tensyoSelectedIndex].Tennm);
                    }

                    // 作業担当者一覧を取得
                    new GetSgytantosTask().execute(loginInfo);
                }

            } finally {

                isTensyoGetting = false;

                if(!isTensyoGetting && !isSgytantoGetting && !isSoukoGetting)
                {
                    // プログレスバーを非表示
                    ProgressBar progressBar = findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.INVISIBLE);

                    // タッチ操作を有効化
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }

            }

        }
    }

    //endregion

    //region 作業担当者一覧を取得

    @SuppressLint("StaticFieldLeak")
    private class GetSgytantosTask extends SgytantoController.GetSgytantosTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(!isTensyoGetting && !isSgytantoGetting && !isSoukoGetting)
            {
                // タッチ操作を無効化
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                // プログレスバーを表示
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            isSgytantoGetting = true;
        }

        /**
         * バックグランド処理が完了し、UIスレッドに反映する
         */
        @Override
        protected void onPostExecute(SgytantosModel sgytantos) {

            try {

                // コントロールを無効化
                final com.google.android.material.textfield.TextInputLayout tilSgytanto = findViewById(R.id.tilSgytanto);
                tilSgytanto.setEnabled(false);
                final AutoCompleteTextView txtSgytanto = findViewById(R.id.txtSgytanto);
                txtSgytanto.setEnabled(false);

                // エラー発生
                if (sgytantos.Is_error) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("エラー");
                    builder.setMessage(java.text.MessageFormat.format(getResources().getString(R.string.login_activity_message5), sgytantos.Message));
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                            // すべての一覧をクリア
                            ClearListData();

                        }
                    });

                    builder.show();

                    return;
                }

                // 該当データなし
                if (sgytantos.Sgytantos == null) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage(getResources().getString(R.string.login_activity_message8));
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                            // すべての一覧をクリア
                            ClearListData();

                        }
                    });

                    builder.show();

                    return;
                }

                // 取得結果をセット
                sgytantosModel = sgytantos;

                // コントロールにデータを追加
                ArrayList<String> items = new ArrayList<String>();

                for (SgytantoModel SgytantoModel : sgytantosModel.Sgytantos) {
                    items.add(SgytantoModel.Sgytantonm);
                }

                UniqueArrayAdapter arrayAdapter = new UniqueArrayAdapter(LoginActivity.this, R.layout.dropdown_menu_popup_item, items);
                txtSgytanto.setAdapter(arrayAdapter);

                // 前回ログイン情報がある場合、前回の作業担当者を選択する
                sgytantoSelectedIndex = -1;
                if (loginInfo != null && loginInfo.Sgytantocd > 0) {
                    // 前回ログイン時の作業担当者を取得
                    int i = 0;
                    for (SgytantoModel sgytantoModel : sgytantosModel.Sgytantos) {

                        if (sgytantoModel.Sgytantocd == loginInfo.Sgytantocd) {
                            sgytantoSelectedIndex = i;
                            break;
                        }

                        i++;
                    }

                    // 前回ログイン時の作業担当者を選択
                    if (sgytantoSelectedIndex > -1) {
                        txtSgytanto.setText(sgytantos.Sgytantos[sgytantoSelectedIndex].Sgytantonm);
                    }
                }

                // コントロールを有効化
                tilSgytanto.setEnabled(true);
                txtSgytanto.setEnabled(true);

                // 倉庫一覧を取得
                new GetSoukosTask().execute(loginInfo);

            } finally {

                isSgytantoGetting = false;

                if(!isTensyoGetting && !isSgytantoGetting && !isSoukoGetting)
                {
                    // プログレスバーを非表示
                    ProgressBar progressBar = findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.INVISIBLE);

                    // タッチ操作を有効化
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }

            }
        }
    }

    //endregion

    //region 倉庫一覧を取得

    @SuppressLint("StaticFieldLeak")
    private class GetSoukosTask extends SoukoController.GetSoukosTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(!isTensyoGetting && !isSgytantoGetting && !isSoukoGetting)
            {
                // タッチ操作を無効化
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                // プログレスバーを表示
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            isSoukoGetting = true;
        }

        /**
         * バックグランド処理が完了し、UIスレッドに反映する
         */
        @Override
        protected void onPostExecute(SoukosModel soukos) {

            try {

                // コントロールを無効化
                final com.google.android.material.textfield.TextInputLayout tilSouko = findViewById(R.id.tilSouko);
                tilSouko.setEnabled(false);
                final AutoCompleteTextView txtSouko = findViewById(R.id.txtSouko);
                txtSouko.setEnabled(false);

                // エラー発生
                if (soukos.Is_error) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("エラー");
                    builder.setMessage(java.text.MessageFormat.format(getResources().getString(R.string.login_activity_message6), soukos.Message));
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                            // すべての一覧をクリア
                            ClearListData();

                        }
                    });

                    builder.show();

                    return;
                }

                // 該当データなし
                if (soukos.Soukos == null) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage(getResources().getString(R.string.login_activity_message9));
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                            // すべての一覧をクリア
                            ClearListData();

                        }
                    });

                    builder.show();

                    return;
                }

                // 取得結果をセット
                soukosModel = soukos;

                // コントロールにデータを追加
                ArrayList<String> items = new ArrayList<String>();

                for (SoukoModel SoukoModel : soukosModel.Soukos) {
                    items.add(SoukoModel.Soukonm);
                }

                UniqueArrayAdapter arrayAdapter = new UniqueArrayAdapter(LoginActivity.this, R.layout.dropdown_menu_popup_item, items);
                txtSouko.setAdapter(arrayAdapter);

                // 前回ログイン情報がある場合、前回の倉庫を選択する
                soukoSelectedIndex = -1;
                if (loginInfo != null && loginInfo.Soukocd > 0) {
                    // 前回ログイン時の倉庫を取得
                    int i = 0;
                    for (SoukoModel soukoModel : soukosModel.Soukos) {

                        if (soukoModel.Soukocd == loginInfo.Soukocd) {
                            soukoSelectedIndex = i;
                            break;
                        }

                        i++;
                    }

                    // 前回ログイン時の作業担当者を選択
                    if (soukoSelectedIndex > -1) {
                        txtSouko.setText(soukos.Soukos[soukoSelectedIndex].Soukonm);
                    }
                }

                // コントロールを有効化
                tilSouko.setEnabled(true);
                txtSouko.setEnabled(true);

            } finally {

                isSoukoGetting = false;

                if(!isTensyoGetting && !isSgytantoGetting && !isSoukoGetting)
                {
                    // プログレスバーを非表示
                    ProgressBar progressBar = findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.INVISIBLE);

                    // タッチ操作を有効化
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }

            }
        }
    }

    //endregion

    //region 店所を選択

    private class TensyoItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            // ログイン情報に選択した店所をセット
            loginInfo.Tensyocd = tensyosModel.Tensyos[position].Tencd;
            loginInfo.Tensyonm = tensyosModel.Tensyos[position].Tennm;
            tensyoSelectedIndex = position;

            // 作業担当者をクリア
            ClearSgytanto();

            // 倉庫をクリア
            ClearSouko();

            // 作業担当者一覧を取得
            new GetSgytantosTask().execute(loginInfo);
        }
    }

    //endregion

    //region 作業担当者を選択

    private class SgytantoItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            // ログイン情報に選択した作業担当者をセット
            loginInfo.Sgytantocd = sgytantosModel.Sgytantos[position].Sgytantocd;
            loginInfo.Sgytantonm = sgytantosModel.Sgytantos[position].Sgytantonm;
            sgytantoSelectedIndex = position;

        }
    }

    //endregion

    //region 倉庫を選択

    private class SoukoItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            // ログイン情報に選択した倉庫をセット
            loginInfo.Soukocd = soukosModel.Soukos[position].Soukocd;
            loginInfo.Soukonm = soukosModel.Soukos[position].Soukonm;
            soukoSelectedIndex = position;

        }
    }

    //endregion

    //region クリア

    // 一覧クリア
    private void ClearListData()
    {
        ClearTensyo();
        ClearSgytanto();
        ClearSouko();
    }

    // 事業所をクリア
    private  void  ClearTensyo()
    {
        loginInfo.Tensyocd = 0;
        loginInfo.Tensyonm = "";
        tensyoSelectedIndex = -1;
        final AutoCompleteTextView txtTensyo = findViewById(R.id.txtTensyo);
        txtTensyo.setText("");
    }

    // 作業担当者をクリア
    private  void  ClearSgytanto()
    {
        loginInfo.Sgytantocd = 0;
        loginInfo.Sgytantonm = "";
        sgytantosModel = null;
        sgytantoSelectedIndex = -1;
        final com.google.android.material.textfield.TextInputLayout tilSgytanto = findViewById(R.id.tilSgytanto);
        tilSgytanto.setEnabled(false);
        final AutoCompleteTextView txtSgytanto = findViewById(R.id.txtSgytanto);
        txtSgytanto.setText("");
        txtSgytanto.setAdapter(null);
        txtSgytanto.setEnabled(false);
    }

    // 倉庫をクリア
    private  void  ClearSouko()
    {
        loginInfo.Soukocd = 0;
        loginInfo.Soukonm = "";
        soukosModel = null;
        soukoSelectedIndex = -1;
        final com.google.android.material.textfield.TextInputLayout tilSouko = findViewById(R.id.tilSouko);
        tilSouko.setEnabled(false);
        final AutoCompleteTextView txtSouko = findViewById(R.id.txtSouko);
        txtSouko.setText("");
        txtSouko.setAdapter(null);
        txtSouko.setEnabled(false);
    }

    //endregion

    //region 独自のアダプター

    private class UniqueArrayAdapter extends ArrayAdapter<String> {
        public UniqueArrayAdapter(@NonNull Context context, int resource, ArrayList<String> items) {
            super(context, resource, items);
        }

        @Override
        public Filter getFilter() {
            return new UniqueFilter();
        }
    }

    private class UniqueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            // 常に絞り込みを行わずに、全件表示する
            return new FilterResults();
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
        }
    }

    //endregion
}
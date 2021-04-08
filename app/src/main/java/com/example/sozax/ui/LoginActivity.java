package com.example.sozax.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Toast;

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

    //endregion

    //region 初回起動

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 選択イベント追加
        final AutoCompleteTextView txtTensyos = findViewById(R.id.txtTensyo);
        txtTensyos.setOnItemClickListener(new TensyoItemClickListener());
        final AutoCompleteTextView txtSgytantos = findViewById(R.id.txtSgytanto);
        txtSgytantos.setOnItemClickListener(new SgytantoItemClickListener());
        final AutoCompleteTextView txtSoukos = findViewById(R.id.txtSouko);
        txtSoukos.setOnItemClickListener(new SoukoItemClickListener());

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
            tilSouko.setError("倉庫を選択して下さい");
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
        SharedPreferences preferences = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // 会社
        editor.putInt("Kaicd", loginInfo.Kaicd);

        // 店所
        editor.putInt("Tensyocd", loginInfo.Tensyocd);
        editor.putString("Tensyonm", loginInfo.Tensyonm);

        // 作業担当者
        editor.putInt("Sgytantocd", loginInfo.Sgytantocd);
        editor.putString("Sgytantonm", loginInfo.Sgytantonm);

        // 倉庫
        editor.putInt("Soukocd", loginInfo.Soukocd);
        editor.putString("Soukonm", loginInfo.Soukonm);

        // 作業日時
        editor.putLong("Sgydate", loginInfo.Sgydate.getTime());

        // 更新日時
        editor.putLong("Updatedate", loginInfo.Updatedate.getTime());

        // 反映
        editor.apply();

        // メニュー画面に遷移
        Intent intent = new Intent(getApplication(), MenuActivity.class);
        intent.putExtra("LOGININFO", loginInfo);

        startActivity(intent);
    }

    //endregion

    //region 店所一覧を取得

    @SuppressLint("StaticFieldLeak")
    private class GetTensyosTask extends TensyoController.GetTensyosTask {

        /**
         * バックグランド処理が完了し、UIスレッドに反映する
         */
        @Override
        protected void onPostExecute(TensyosModel tensyos) {

            // 取得結果をセット
            tensyosModel = tensyos;

            // エラー発生
            if (tensyosModel.Is_error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("エラー");
                builder.setMessage(tensyosModel.Message);

                builder.show();
                return;
            }

            // 該当データなし
            if (tensyosModel.Tensyos == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage("事業所データがありません。");

                builder.show();
                return;
            }

            // コントロールにデータを追加
            ArrayList<String> items = new ArrayList<String>();

            for (TensyoModel tensyoModel : tensyosModel.Tensyos) {
                items.add(tensyoModel.Tennm);
            }

            UniqueArrayAdapter arrayAdapter = new UniqueArrayAdapter(LoginActivity.this, R.layout.dropdown_menu_popup_item, items);

            final AutoCompleteTextView txtTensyos = findViewById(R.id.txtTensyo);
            txtTensyos.setAdapter(arrayAdapter);

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
                    txtTensyos.setText(tensyos.Tensyos[tensyoSelectedIndex].Tennm);
                }

                // 倉庫一覧を取得
                new GetSoukosTask().execute(loginInfo);

                // 作業担当者一覧を取得
                new GetSgytantosTask().execute(loginInfo);
            }
        }
    }

    //endregion

    //region 作業担当者一覧を取得

    @SuppressLint("StaticFieldLeak")
    private class GetSgytantosTask extends SgytantoController.GetSgytantosTask {

        /**
         * バックグランド処理が完了し、UIスレッドに反映する
         */
        @Override
        protected void onPostExecute(SgytantosModel sgytantos) {

            // 取得結果をセット
            sgytantosModel = sgytantos;

            // エラー発生
            if (sgytantosModel.Is_error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("エラー");
                builder.setMessage(sgytantosModel.Message);

                builder.show();
                return;
            }

            // 該当データなし
            if (sgytantosModel.Sgytantos == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage("担当者データがありません。");

                builder.show();
                return;
            }

            // コントロールにデータを追加
            ArrayList<String> items = new ArrayList<String>();

            for (SgytantoModel SgytantoModel : sgytantosModel.Sgytantos) {
                items.add(SgytantoModel.Sgytantonm);
            }

            UniqueArrayAdapter arrayAdapter = new UniqueArrayAdapter(LoginActivity.this, R.layout.dropdown_menu_popup_item, items);

            final AutoCompleteTextView txtSgytantos = findViewById(R.id.txtSgytanto);
            txtSgytantos.setAdapter(arrayAdapter);

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
                    txtSgytantos.setText(sgytantos.Sgytantos[sgytantoSelectedIndex].Sgytantonm);
                }
            }
        }
    }

    //endregion

    //region 倉庫一覧を取得

    @SuppressLint("StaticFieldLeak")
    private class GetSoukosTask extends SoukoController.GetSoukosTask {

        /**
         * バックグランド処理が完了し、UIスレッドに反映する
         */
        @Override
        protected void onPostExecute(SoukosModel soukos) {

            // 取得結果をセット
            soukosModel = soukos;

            // エラー発生
            if (soukosModel.Is_error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("エラー");
                builder.setMessage(soukosModel.Message);

                builder.show();
                return;
            }

            // 該当データなし
            if (soukosModel.Soukos == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage("倉庫データがありません。");

                builder.show();
                return;
            }

            // コントロールにデータを追加
            ArrayList<String> items = new ArrayList<String>();

            for (SoukoModel SoukoModel : soukosModel.Soukos) {
                items.add(SoukoModel.Soukonm);
            }

            UniqueArrayAdapter arrayAdapter = new UniqueArrayAdapter(LoginActivity.this, R.layout.dropdown_menu_popup_item, items);

            final AutoCompleteTextView txtSoukos = findViewById(R.id.txtSouko);
            txtSoukos.setAdapter(arrayAdapter);

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
                    txtSoukos.setText(soukos.Soukos[soukoSelectedIndex].Soukonm);
                }
            }
        }
    }

    //endregion

    //region 店所を選択

    private class TensyoItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            try {
                // ログイン情報に選択した店所をセット
                loginInfo.Tensyocd = tensyosModel.Tensyos[position].Tencd;
                loginInfo.Tensyonm = tensyosModel.Tensyos[position].Tennm;
                tensyoSelectedIndex = position;

                // 作業担当者をクリア
                loginInfo.Sgytantocd = 0;
                loginInfo.Sgytantonm = "";
                sgytantosModel = null;
                sgytantoSelectedIndex = -1;
                final AutoCompleteTextView txtSgytantos = findViewById(R.id.txtSgytanto);
                txtSgytantos.setText("");
                txtSgytantos.setAdapter(null);

                // ログイン情報の倉庫をクリア
                loginInfo.Soukocd = 0;
                loginInfo.Soukonm = "";
                soukosModel = null;
                soukoSelectedIndex = -1;
                final AutoCompleteTextView txtSoukos = findViewById(R.id.txtSouko);
                txtSoukos.setText("");
                txtSoukos.setAdapter(null);

                // 作業担当者一覧を取得
                new GetSgytantosTask().execute(loginInfo);

                // 倉庫一覧を取得
                new GetSoukosTask().execute(loginInfo);

            } catch (Exception ex) {
                Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            }
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

    //region DENSO固有ボタンの設定

    @Override
    public void onKeyRemapCreated() {
    }

    //endregion

    //region 独自のアダプター

    private class UniqueArrayAdapter extends ArrayAdapter<String> {
        public UniqueArrayAdapter(@NonNull Context context, int resource , ArrayList<String> items) {
            super(context, resource,items);
        }
        @Override
        public Filter getFilter() {
            return new UniqueFilter();
        }
    }

    private class UniqueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            return new FilterResults();
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
        }
    }

    //endregion
}
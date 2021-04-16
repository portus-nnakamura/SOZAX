package com.example.sozax.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.densowave.bhtsdk.keyremap.KeyRemapLibrary;
import com.example.sozax.R;
import com.example.sozax.bl.controllers.SyukoDenpyoController;
import com.example.sozax.bl.controllers.SyukoSagyoController;
import com.example.sozax.bl.models.syuko_denpyo.SyukoDenpyoConditionModel;
import com.example.sozax.bl.models.syuko_denpyo.SyukoDenpyoModel;
import com.example.sozax.bl.models.syuko_denpyo.SyukoDenpyosModel;
import com.example.sozax.bl.models.syuko_sagyo.SyukoSagyoModel;
import com.example.sozax.common.CommonActivity;
import com.example.sozax.common.EnumClass;
import com.example.sozax.common.EnumClass.SgyjokyoKubun;
import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.sozax.ui.InventoryInquiryPage2Activity.toFullWidth;

public class GoodsIssuePage1Activity extends CommonActivity implements KeyRemapLibrary.KeyRemapListener {

    //region インスタンス変数

    // 作業中の伝票リスト
    private ArrayList<SyukoDenpyoModel> sagyochuSyukoDenpyos = null;

    // 現在作業中の出庫伝票のインデックス
    private int selectedSagyochuSyukoDenpyoIndex = -1;

    // キー割り当てライブラリ(DENSO製)
    public KeyRemapLibrary mKeyRemapLibrary;

    //endregion

    //region 初回起動

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_issue_page1);

        // イベントを追加
        // アプリ終了
        findViewById(R.id.btnExit).setOnClickListener(new btnExit_Click(GoodsIssuePage1Activity.this));
        // 伝票追加
        findViewById(R.id.btnSlipAdd).setOnClickListener(new btnSlipAdd_Click());
        // 伝票削除
        ((ListView) findViewById(R.id.lvGoodsIssueList)).setOnItemLongClickListener(new lvGoodsIssueList_LongClick());
        // 行選択
        ((ListView) findViewById(R.id.lvGoodsIssueList)).setOnItemClickListener(new lvGoodsIssueList_Click());

        // ハードウェアキーのマッピングクラスのインスタンスを生成
        mKeyRemapLibrary = new KeyRemapLibrary();
        mKeyRemapLibrary.createKeyRemap(this, this);

        // ログイン情報を表示
        DisplayLoginInfo();

        // 作業中の出庫伝票リストの取得条件を作成
        SyukoDenpyoConditionModel syukoDenpyoConditionModel = new SyukoDenpyoConditionModel();
        syukoDenpyoConditionModel.Kaicd = loginInfo.Kaicd;
        syukoDenpyoConditionModel.Sgytantocd = loginInfo.Sgytantocd;
        syukoDenpyoConditionModel.Soukocd = loginInfo.Soukocd;
        syukoDenpyoConditionModel.Sagyodate = loginInfo.Sgydate;

        // 作業中の出庫伝票リストを取得
        new GetSyukoDenpyos_SagyochuTask().execute(syukoDenpyoConditionModel);
    }

    //endregion

    //region アプリ終了アイコンをクリック

    // CommonActivity内に処理を記述済み

    //endregion

    //region 伝票追加アイコンをクリック

    private class btnSlipAdd_Click implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            // 未着手の出庫伝票リストの取得条件を作成
            SyukoDenpyoConditionModel syukoDenpyoConditionModel = new SyukoDenpyoConditionModel();
            syukoDenpyoConditionModel.Kaicd = loginInfo.Kaicd;
            syukoDenpyoConditionModel.Soukocd = loginInfo.Soukocd;
            syukoDenpyoConditionModel.Sagyodate = loginInfo.Sgydate;

            // 未着手の出庫伝票リストを取得
            new GetSyukoDenpyos_SagyomichakusyuTask().execute(syukoDenpyoConditionModel);

        }
    }

    //endregion


    //region TODO 出庫作業のQRをスキャン

    //endregion


    //region 出庫伝票をクリック

    class lvGoodsIssueList_Click implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if(selectedSagyochuSyukoDenpyoIndex != position)
            {
                view.setSelected(true);

                // 選択伝票Indexを更新
                selectedSagyochuSyukoDenpyoIndex = position;

                // 荷主・荷渡先・品名を表示
                DisplayNinuNiwaHin();

                // 数量・重量を表示
                DisplaySuryoJuryo();
            }

        }
    }

    //endregion

    //region 出庫伝票を長押しクリック

    class lvGoodsIssueList_LongClick implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {

            if(selectedSagyochuSyukoDenpyoIndex != position)
            {
                view.setSelected(true);

                // 選択伝票Indexを更新
                selectedSagyochuSyukoDenpyoIndex = position;

                // 荷主・荷渡先・品名を表示
                DisplayNinuNiwaHin();

                // 数量・重量を表示
                DisplaySuryoJuryo();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(GoodsIssuePage1Activity.this);
            builder.setMessage("選択行をクリアしても\nよろしいですか？")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // 出庫作業削除用のデータを作成
                            SyukoDenpyosModel deleteData = new SyukoDenpyosModel();
                            deleteData.SyukoDenpyos = new SyukoDenpyoModel[1];
                            deleteData.SyukoDenpyos[0] = sagyochuSyukoDenpyos.get(selectedSagyochuSyukoDenpyoIndex);

                            // 出庫作業削除
                            new DeleteSyukoSagyosTask().execute(deleteData);

                        }

                    })
                    .setNegativeButton("キャンセル", null)
                    .setCancelable(true);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            TextView msgTxt = alertDialog.findViewById(android.R.id.message);
            msgTxt.setTextSize((float) 14.0);
            return false;
        }
    }

    //endregion

    //region 出庫作業開始ボタンをクリック

    private final int REQUESTCODE = 1;

    public void btnGoodsIssuePage1Proceed_Click(View view) {

        Intent intent = new Intent(getApplicationContext(), GoodsIssuePage2Activity.class);
        intent.putExtra(getResources().getString(R.string.intent_key_login_info), loginInfo);
        intent.putExtra(getResources().getString(R.string.intent_key_sagyochu_syuko_denpyos), sagyochuSyukoDenpyos);
        intent.putExtra(getResources().getString(R.string.intent_key_selected_sagyochu_syuko_denpyo_index), selectedSagyochuSyukoDenpyoIndex);

        try {
            startActivityForResult(intent, REQUESTCODE);
        }catch (Exception exception)
        {
            Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_LONG).show();;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUESTCODE && resultCode == RESULT_OK) {
            // 作業中データと選択Indexを取得
            sagyochuSyukoDenpyos = (ArrayList<SyukoDenpyoModel>) data.getSerializableExtra(getResources().getString(R.string.intent_key_sagyochu_syuko_denpyos));
            selectedSagyochuSyukoDenpyoIndex = data.getIntExtra(getResources().getString(R.string.intent_key_selected_sagyochu_syuko_denpyo_index), -1);

            if (sagyochuSyukoDenpyos.size() == 0) {
                return;
            }

            // 出庫伝票を表示
            DisplaySyukoDenpyos();

            if (selectedSagyochuSyukoDenpyoIndex == -1) {
                return;
            }

            // 荷主・荷渡先・品名を表示
            DisplayNinuNiwaHin();

            // 数量・重量を表示
            DisplaySuryoJuryo();
        }
    }


    //endregion

    //region 進行ボタンの有効・無効化

    private void EnabledBtnGoodsIssuePage1Proceed() {
        ListView lvGoodsIssueList = findViewById(R.id.lvGoodsIssueList);
        SpannableStringBuilder sb = new SpannableStringBuilder("出庫作業開始");
        int start = sb.length();
        int color;
        boolean enabled;

        if (lvGoodsIssueList.getCount() == 0) {
            sb.append("\n(スキャンした伝票が無いので押せません)");
            color = getColor(R.color.darkgray);
            enabled = false;
        } else {
            sb.append("\n(全伝票スキャン後に押して下さい)");
            color = getColor(R.color.orientalblue);
            enabled = true;
        }

        sb.setSpan(new RelativeSizeSpan(0.5f), start, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        MaterialButton btnGoodsIssuePage1Proceed = findViewById(R.id.btnGoodsIssuePage1Proceed);
        btnGoodsIssuePage1Proceed.setText(sb);
        btnGoodsIssuePage1Proceed.setBackgroundColor(color);
        btnGoodsIssuePage1Proceed.setEnabled(enabled);
    }

    //endregion

    //region 出庫伝票の取得

    @SuppressLint("StaticFieldLeak")
    public class GetSyukoDenpyoTask extends SyukoDenpyoController.GetSyukoDenpyoTask {

        // 取得前処理
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // タッチ操作を無効化
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            // プログレスバーを表示
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);

        }

        // 取得後処理
        @Override
        protected void onPostExecute(SyukoDenpyoModel _syukoDenpyoModel) {

            try {

                // エラー発生
                if (_syukoDenpyoModel.Is_error) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GoodsIssuePage1Activity.this);
                    String template = "作業中の出庫伝票の削除に失敗しました。\r\n{0}";
                    builder.setMessage((java.text.MessageFormat.format(template, _syukoDenpyoModel.Message)));

                    builder.show();
                    return;
                }

                // 該当データなし
                if (_syukoDenpyoModel.Syukono == 0L) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GoodsIssuePage1Activity.this);
                    builder.setTitle("エラー");
                    builder.setMessage("該当する出庫伝票がありません");

                    builder.show();
                    return;
                }

                // 別のユーザーが作業中
                if (_syukoDenpyoModel.Syukosgyjokyo != null && _syukoDenpyoModel.Syukosgyjokyo.Sgytantocd != loginInfo.Sgytantocd) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GoodsIssuePage1Activity.this);
                    builder.setTitle("エラー");
                    String template = "{0}が作業中のため、追加できません";
                    builder.setMessage((java.text.MessageFormat.format(template, _syukoDenpyoModel.Syukosgyjokyo.Sgytantonm)));

                    builder.show();
                    return;
                }

                // 出庫作業登録用のデータを作成
                SyukoDenpyosModel postData = new SyukoDenpyosModel();
                postData.SyukoDenpyos = new SyukoDenpyoModel[0];
                postData.SyukoDenpyos[0] = _syukoDenpyoModel;

                // 出庫作業登録
                new PostSyukoSagyosTask().execute(postData);

            } finally {

                // プログレスバーを非表示
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.INVISIBLE);

                // タッチ操作を有効化
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            }
        }
    }

    //endregion

    //region 出庫伝票一覧（作業中）の取得

    @SuppressLint("StaticFieldLeak")
    public class GetSyukoDenpyos_SagyochuTask extends SyukoDenpyoController.GetSyukoDenpyos_SagyochuTask {

        // 取得前処理
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // タッチ操作を無効化
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            // プログレスバーを表示
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);

        }

        // 取得後処理
        @Override
        protected void onPostExecute(SyukoDenpyosModel _syukoDenpyosModel) {

            try {

                // エラー発生
                if (_syukoDenpyosModel.Is_error) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GoodsIssuePage1Activity.this);
                    String template = "作業中の出庫伝票の削除に失敗しました。\r\n{0}";
                    builder.setMessage((java.text.MessageFormat.format(template, _syukoDenpyosModel.Message)));

                    builder.show();
                    return;
                }

                // 該当データなし
                if (_syukoDenpyosModel.SyukoDenpyos == null) {
                    return;
                }

                if (sagyochuSyukoDenpyos == null) {
                    sagyochuSyukoDenpyos = new ArrayList<SyukoDenpyoModel>();
                }

                // 作業中出庫伝票リストに取得した出庫伝票リストを追加
                sagyochuSyukoDenpyos.addAll(Arrays.asList(_syukoDenpyosModel.SyukoDenpyos));

                // 先頭行を選択
                selectedSagyochuSyukoDenpyoIndex = 0;

                // 出庫伝票一覧を表示
                DisplaySyukoDenpyos();

            } finally {

                // プログレスバーを非表示
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.INVISIBLE);

                // タッチ操作を有効化
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            }
        }
    }

    //endregion

    //region 出庫伝票一覧（未着手）の取得

    @SuppressLint("StaticFieldLeak")
    public class GetSyukoDenpyos_SagyomichakusyuTask extends SyukoDenpyoController.GetSyukoDenpyos_SagyomichakusyuTask {

        // 取得前処理
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // タッチ操作を無効化
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            // プログレスバーを表示
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);

        }

        // 取得後処理
        @Override
        protected void onPostExecute(final SyukoDenpyosModel _syukoDenpyosModel) {

            try {

                // エラー発生
                if (_syukoDenpyosModel.Is_error) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GoodsIssuePage1Activity.this);
                    builder.setTitle("エラー");
                    String template = "未着手の出庫伝票の取得に失敗しました。\r\n{0}";
                    builder.setMessage((java.text.MessageFormat.format(template, _syukoDenpyosModel.Message)));

                    builder.show();
                    return;
                }

                // 該当データなし
                if (_syukoDenpyosModel.SyukoDenpyos == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GoodsIssuePage1Activity.this);
                    builder.setMessage("追加できる出庫伝票がありません");

                    builder.show();
                    return;
                }

                // 表示する「アイテムリスト」と「そのアイテムのチェック状態リスト」を作成
                final String[] items = new String[_syukoDenpyosModel.SyukoDenpyos.length];
                final boolean[] checkedItems = new boolean[_syukoDenpyosModel.SyukoDenpyos.length];

                for (int i = 0; i < _syukoDenpyosModel.SyukoDenpyos.length; i++) {
                    items[i] = (String.valueOf(_syukoDenpyosModel.SyukoDenpyos[i].Syukono));
                    checkedItems[i] = false;
                }

                // 出庫伝票を複数選択できるダイアログを作成
                AlertDialog.Builder builder = new AlertDialog.Builder(GoodsIssuePage1Activity.this);
                // タイトル
                builder.setTitle("出庫伝票を選択して下さい");
                // キャンセルボタン
                builder.setNegativeButton("キャンセル", null);
                // OKボタン
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // ✓がついている出庫伝票を取得
                        ArrayList<SyukoDenpyoModel> selectedData = new ArrayList<SyukoDenpyoModel>();
                        for (int i = 0; i < _syukoDenpyosModel.SyukoDenpyos.length; i++) {

                            if (checkedItems[i]) {

                                SyukoSagyoModel syukoSagyoModel = new SyukoSagyoModel();
                                syukoSagyoModel.Syukono = _syukoDenpyosModel.SyukoDenpyos[i].Syukono;
                                syukoSagyoModel.Kaicd = loginInfo.Kaicd;
                                syukoSagyoModel.Sgytencd = loginInfo.Tensyocd;
                                syukoSagyoModel.Sgytantocd = loginInfo.Sgytantocd;
                                syukoSagyoModel.Sgysoukocd = loginInfo.Soukocd;
                                syukoSagyoModel.Sgydate = loginInfo.Sgydate;
                                syukoSagyoModel.Sgyjokyokbn = SgyjokyoKubun.Uketuke.hashCode();

                                _syukoDenpyosModel.SyukoDenpyos[i].Syukosgyjokyo = syukoSagyoModel;
                                selectedData.add(_syukoDenpyosModel.SyukoDenpyos[i]);
                            }
                        }

                        // 出庫作業登録用データを作成
                        SyukoDenpyosModel postData = new SyukoDenpyosModel();
                        postData.SyukoDenpyos = new SyukoDenpyoModel[selectedData.size()];
                        selectedData.toArray(postData.SyukoDenpyos);

                        // 出庫作業を登録する
                        new PostSyukoSagyosTask().execute(postData);
                    }
                }).setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        // ✔状態を反転
                        checkedItems[which] = isChecked;

                        // 1件でも✔されているか
                        boolean existsChecked = false;
                        for (boolean checkedItem : checkedItems) {
                            if (checkedItem) {
                                existsChecked = true;
                                break;
                            }
                        }

                        // 1件でも✔されていれば、OKボタンを有効化
                        AlertDialog alertDialog = (AlertDialog) dialog;
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(existsChecked);
                    }
                });
                // 出庫伝票を選択

                // ダイアログ表示
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                // OKボタンを無効化
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

            } finally {

                // プログレスバーを非表示
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.INVISIBLE);

                // タッチ操作を有効化
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            }
        }
    }

    //endregion

    //region 出庫作業の登録

    @SuppressLint("StaticFieldLeak")
    public class PostSyukoSagyosTask extends SyukoSagyoController.PostSyukoSagyosTask {

        // 登録前処理
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // タッチ操作を無効化
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            // プログレスバーを表示
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);

        }

        // 登録後処理
        @Override
        protected void onPostExecute(SyukoDenpyosModel _syukoDenpyosModel) {

            try {

                // エラー発生
                if (_syukoDenpyosModel.Is_error) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GoodsIssuePage1Activity.this);
                    builder.setTitle("エラー");
                    String template = "出庫作業の登録に失敗しました。\r\n{0}";
                    builder.setMessage((java.text.MessageFormat.format(template, _syukoDenpyosModel.Message)));

                    builder.show();
                    return;
                }

                if (sagyochuSyukoDenpyos == null) {
                    sagyochuSyukoDenpyos = new ArrayList<SyukoDenpyoModel>();
                }

                // 出庫伝票追加前に、作業中出庫伝票リストから最後のIndexを取得
                int addBeforeLastIndex = sagyochuSyukoDenpyos.size() - 1;

                // 作業中出庫伝票リストに、登録した出庫伝票リストを追加する
                sagyochuSyukoDenpyos.addAll(Arrays.asList(_syukoDenpyosModel.SyukoDenpyos));

                // 現在作業中伝票Indexを今回追加した伝票リストの一番最初に設定する
                selectedSagyochuSyukoDenpyoIndex = (addBeforeLastIndex + 1);

                // 出庫伝票を表示する
                DisplaySyukoDenpyos();

            } finally {

                // プログレスバーを非表示
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.INVISIBLE);

                // タッチ操作を有効化
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            }
        }

    }

    //endregion

    //region 出庫作業の削除

    @SuppressLint("StaticFieldLeak")
    public class DeleteSyukoSagyosTask extends SyukoSagyoController.DeleteSyukoSagyosTask {

        // 削除前処理
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // タッチ操作を無効化
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            // プログレスバーを表示
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);

        }

        // 削除後処理
        @Override
        protected void onPostExecute(SyukoDenpyosModel _syukoDenpyosModel) {

            try {

                // エラー発生
                if (_syukoDenpyosModel.Is_error) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GoodsIssuePage1Activity.this);
                    builder.setTitle("エラー");
                    String template = "出庫作業の削除に失敗しました。\r\n{0}";
                    builder.setMessage((java.text.MessageFormat.format(template, _syukoDenpyosModel.Message)));

                    builder.show();
                    return;
                }

                // 作業中出庫伝票リストから削除した伝票を削除する
                for (SyukoDenpyoModel _syukoDenpyoModel : _syukoDenpyosModel.SyukoDenpyos) {

                    sagyochuSyukoDenpyos.remove(_syukoDenpyoModel);

                }

                // 現在作業中伝票Indexをクリアする
                selectedSagyochuSyukoDenpyoIndex = -1;

                // 出庫伝票を表示する
                DisplaySyukoDenpyos();

            } finally {

                // プログレスバーを非表示
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.INVISIBLE);

                // タッチ操作を有効化
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            }
        }

    }

    //endregion

    //region 出庫伝票一覧を表示

    private void DisplaySyukoDenpyos() {

        // ListView取得
        final ListView lvGoodsIssueList = findViewById(R.id.lvGoodsIssueList);

        // アダプターを作成
        final UniqueAdapter uniqueAdapter = new UniqueAdapter(this, sagyochuSyukoDenpyos);

        // アダプターをセット
        lvGoodsIssueList.setAdapter(uniqueAdapter);

        if (selectedSagyochuSyukoDenpyoIndex > -1) {
            // 行選択
            lvGoodsIssueList.deferNotifyDataSetChanged();
            lvGoodsIssueList.requestFocusFromTouch();
            lvGoodsIssueList.setSelection(selectedSagyochuSyukoDenpyoIndex);

            // 荷主・荷渡先・品名を表示
            DisplayNinuNiwaHin();

            // 数量・重量を表示
            DisplaySuryoJuryo();
        }
    }

    //endregion

    //region 荷主・荷渡先・品名を表示

    private void DisplayNinuNiwaHin() {
        DisplayNinuNiwaHin(selectedSagyochuSyukoDenpyoIndex);
    }

    private void DisplayNinuNiwaHin(int index) {
        TextView txtGoodsIssueSlipListDetailNinushi = findViewById(R.id.txtGoodsIssuePage1DetailNinushi);
        TextView txtGoodsIssueSlipListDetailNiwatashi = findViewById(R.id.txtGoodsIssuePage1DetailNiwatashi);
        TextView txtGoodsIssueSlipListDetailProductName = findViewById(R.id.txtGoodsIssuePage1DetailProductName);

        SyukoDenpyoModel currentSagyochuSyukoDenpyo = sagyochuSyukoDenpyos.get(index);

        // 荷主名
        txtGoodsIssueSlipListDetailNinushi.setText(currentSagyochuSyukoDenpyo.Ninusinm);
        // 荷渡名
        txtGoodsIssueSlipListDetailNiwatashi.setText(currentSagyochuSyukoDenpyo.Niwatanm);
        // 商品名
        StringBuilder hinmeinm = new StringBuilder(currentSagyochuSyukoDenpyo.Hinmeinm);
        if (!currentSagyochuSyukoDenpyo.Kikakunaiyo1.isEmpty()) {
            // 規格内容1
            hinmeinm.append(" ").append(currentSagyochuSyukoDenpyo.Kikakunaiyo1);
        }
        if (!currentSagyochuSyukoDenpyo.Kikakunaiyo2.isEmpty()) {
            // 規格内容2
            hinmeinm.append(" ").append(currentSagyochuSyukoDenpyo.Kikakunaiyo2);
        }
        if (!currentSagyochuSyukoDenpyo.Kikakunaiyo3.isEmpty()) {
            // 規格内容3
            hinmeinm.append(" ").append(currentSagyochuSyukoDenpyo.Kikakunaiyo3);
        }
        if (!currentSagyochuSyukoDenpyo.Kikakunaiyo4.isEmpty()) {
            // 規格内容4
            hinmeinm.append(" ").append(currentSagyochuSyukoDenpyo.Kikakunaiyo4);
        }
        if (!currentSagyochuSyukoDenpyo.Kikakunaiyo5.isEmpty()) {
            // 規格内容5
            hinmeinm.append(" ").append(currentSagyochuSyukoDenpyo.Kikakunaiyo5);
        }
        if (!currentSagyochuSyukoDenpyo.Kikakunaiyo6.isEmpty()) {
            // 規格内容6
            hinmeinm.append(" ").append(currentSagyochuSyukoDenpyo.Kikakunaiyo6);
        }
        if (!currentSagyochuSyukoDenpyo.Kikakunaiyo7.isEmpty()) {
            // 規格内容7
            hinmeinm.append(" ").append(currentSagyochuSyukoDenpyo.Kikakunaiyo7);
        }
        if (!currentSagyochuSyukoDenpyo.Kikakunaiyo8.isEmpty()) {
            // 規格内容8
            hinmeinm.append(" ").append(currentSagyochuSyukoDenpyo.Kikakunaiyo8);
        }
        if (!currentSagyochuSyukoDenpyo.Kikakunaiyo9.isEmpty()) {
            // 規格内容9
            hinmeinm.append(" ").append(currentSagyochuSyukoDenpyo.Kikakunaiyo9);
        }
        if (!currentSagyochuSyukoDenpyo.Kikakunaiyo10.isEmpty()) {
            // 規格内容10
            hinmeinm.append(" ").append(currentSagyochuSyukoDenpyo.Kikakunaiyo10);
        }
        if (!currentSagyochuSyukoDenpyo.Kikakunaiyo11.isEmpty()) {
            // 規格内容11
            hinmeinm.append(" ").append(currentSagyochuSyukoDenpyo.Kikakunaiyo11);
        }
        if (!currentSagyochuSyukoDenpyo.Kikakunaiyo12.isEmpty()) {
            // 規格内容12
            hinmeinm.append(" ").append(currentSagyochuSyukoDenpyo.Kikakunaiyo12);
        }
        if (!currentSagyochuSyukoDenpyo.Tanjuryo.equals(BigDecimal.ZERO)) {
            // 単重量
            hinmeinm.append(" ").append(currentSagyochuSyukoDenpyo.Tanjuryo);
        }

        txtGoodsIssueSlipListDetailProductName.setText(hinmeinm);
    }

    //endregion

    //region 数量・重量を表示

    private void DisplaySuryoJuryo() {
        DisplaySuryoJuryo(selectedSagyochuSyukoDenpyoIndex);
    }

    @SuppressLint("DefaultLocale")
    private void DisplaySuryoJuryo(int index) {
        TextView txtGoodsIssuePage1Quantity = findViewById(R.id.txtGoodsIssuePage1Quantity);
        TextView txtGoodsIssuePage1Weight = findViewById(R.id.txtGoodsIssuePage1Weight);

        SyukoDenpyoModel currentSagyochuSyukoDenpyo = sagyochuSyukoDenpyos.get(index);

        // 出庫個数
        txtGoodsIssuePage1Quantity.setText(toFullWidth(String.format("%,d", currentSagyochuSyukoDenpyo.Kosuu.intValue())));
        // 出庫重量
        txtGoodsIssuePage1Weight.setText(toFullWidth(String.format("%,d", currentSagyochuSyukoDenpyo.Juryo.intValue())));
    }


    //endregion

    //region 独自のAdapter

    private static class UniqueAdapter extends BaseAdapter {

        private final ArrayList<SyukoDenpyoModel> list;
        private final LayoutInflater inflater;

        public UniqueAdapter(Context context, ArrayList<SyukoDenpyoModel> list) {
            super();
            this.list = list;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public SyukoDenpyoModel getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {

            if (view == null) view = inflater.inflate(R.layout.goods_issue_page1_raw, null);

            // 表示するデータを取得
            final SyukoDenpyoModel syukoDenpyoModel = getItem(position);

            // 値をセットするテキストを取得
            final TextView txtGoodsIssueNo = view.findViewById(R.id.txtGoodsIssueNo);
            final TextView txtGoodsIssueSlipNo = view.findViewById(R.id.txtGoodsIssueSlipNo);
            final TextView txtGoodsIssueStatus = view.findViewById(R.id.txtGoodsIssueStatus);

            // 行番号
            txtGoodsIssueNo.setText(String.valueOf(position + 1));
            // 伝票番号
            txtGoodsIssueSlipNo.setText(String.valueOf(syukoDenpyoModel.Syukono));
            // 作業状況
            txtGoodsIssueStatus.setText(EnumClass.getSgyjokyoKubun(syukoDenpyoModel.Syukosgyjokyo.Sgyjokyokbn).getString());

            return view;
        }
    }

    //endregion

    //region DENSO固有ボタンの設定

    @Override
    public void onKeyRemapCreated() {

        // 左トリガーにバーコードスキャンを割り当て
        mKeyRemapLibrary.setRemapKey(KeyRemapLibrary.KeyCode.KEY_CODE_LT.getValue(),
                KeyRemapLibrary.ScanCode.SCAN_CODE_TRIGGER_BARCODE.getString());

        // 右トリガーにバーコードスキャンを割り当て
        mKeyRemapLibrary.setRemapKey(KeyRemapLibrary.KeyCode.KEY_CODE_RT.getValue(),
                KeyRemapLibrary.ScanCode.SCAN_CODE_TRIGGER_BARCODE.getString());

    }

    //endregion
}
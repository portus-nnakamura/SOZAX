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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.densowave.bhtsdk.barcode.BarcodeDataReceivedEvent;
import com.densowave.bhtsdk.keyremap.KeyRemapLibrary;
import com.example.sozax.R;
import com.example.sozax.bl.controllers.SyukoDenpyoController;
import com.example.sozax.bl.controllers.SyukoSagyoController;
import com.example.sozax.bl.models.syuko_denpyo.SyukoDenpyoConditionModel;
import com.example.sozax.bl.models.syuko_denpyo.SyukoDenpyoModel;
import com.example.sozax.bl.models.syuko_denpyo.SyukoDenpyosModel;
import com.example.sozax.bl.models.syuko_sagyo.SyukoSagyoModel;
import com.example.sozax.common.EnumClass;
import com.example.sozax.common.EnumClass.SgyjokyoKubun;
import com.example.sozax.common.ScannerActivity;
import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.sozax.common.CommonFunction.multiplyThousand;
import static com.example.sozax.common.CommonFunction.toFullWidth;

public class GoodsIssuePage1Activity extends ScannerActivity implements KeyRemapLibrary.KeyRemapListener {

    //region インスタンス変数

    // 作業中の伝票リスト
    private ArrayList<SyukoDenpyoModel> sagyochuSyukoDenpyos = null;

    // 現在作業中の出庫伝票のインデックス
    private int selectedSagyochuSyukoDenpyoIndex = -1;

    // キー割り当てライブラリ(DENSO製)
    private KeyRemapLibrary mKeyRemapLibrary;

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
        // ログイン情報長押し
        findViewById(R.id.clLoginInfo).setOnLongClickListener(new clLoginInfo_LongClick());

        // ハードウェアキーのマッピングクラスのインスタンスを生成
        mKeyRemapLibrary = new KeyRemapLibrary();
        mKeyRemapLibrary.createKeyRemap(this, this);

        // ログイン情報を表示
        DisplayLoginInfo();

        // 作業開始ボタンの無効化
        EnabledBtnGoodsIssuePage1Proceed(false);

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

    // region 画面終了時

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // KeyRemapLibrary クラスのインスタンスを解放
        mKeyRemapLibrary.disposeKeyRemap();
    }

    // endregion

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

    //region 出庫作業のQRをスキャン

    // 集計コードチェックパターン
    final private String chkPattern = "1:[0-9]{11}$";

    @Override
    public void onBarcodeDataReceived(BarcodeDataReceivedEvent event) {

        List<BarcodeDataReceivedEvent.BarcodeData> listBarcodeData = event.getBarcodeData();

        for (BarcodeDataReceivedEvent.BarcodeData data : listBarcodeData) {

            runOnUiThread(new Runnable() {

                        BarcodeDataReceivedEvent.BarcodeData readData = null;

                        Runnable setData(BarcodeDataReceivedEvent.BarcodeData _readData) {
                            readData = _readData;
                            return this;
                        }

                        @Override
                        public void run() {

                            // QRデータ取得
                            String qrData = readData.getData();

                            // 正規表現パターン
                            Pattern ptn = Pattern.compile(chkPattern);        // 先頭文字が2であるか、1文字目が:であるか、3桁目以降が数値であるか、13桁であるか
                            Matcher matcher = ptn.matcher(qrData);

                            // 正規表現でチェック
                            if (!matcher.lookingAt()) {
                                // 不正なQRデータの場合メッセージを表示して処理中断
                                OutputErrorMessage("出庫伝票のQRではありません。");
                            } else {

                                // QRデータから出庫Noを切り出す
                                long syukono = Long.parseLong(qrData.substring(2));

                                // 出庫伝票取得の条件を作成
                                SyukoDenpyoConditionModel syukoDenpyoConditionModel = new SyukoDenpyoConditionModel();
                                syukoDenpyoConditionModel.Syukono = syukono;
                                syukoDenpyoConditionModel.Kaicd = loginInfo.Kaicd;
                                syukoDenpyoConditionModel.Soukocd = loginInfo.Soukocd;
                                syukoDenpyoConditionModel.Sagyodate = loginInfo.Sgydate;

                                // 出庫伝票取得
                                new GetSyukoDenpyoTask().execute(syukoDenpyoConditionModel);
                            }
                        }
                    }.setData(data)
            );
        }
    }

    //endregion

    //region 出庫伝票をクリック

    class lvGoodsIssueList_Click implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            view.setActivated(true);

            if (selectedSagyochuSyukoDenpyoIndex != position) {

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

        private final onPositiveButtonClieckListener _onPositiveButtonClieckListener = new onPositiveButtonClieckListener();

        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {

            if (selectedSagyochuSyukoDenpyoIndex != position) {

                // 行選択
                final ListView lvGoodsIssueList = findViewById(R.id.lvGoodsIssueList);
                lvGoodsIssueList.setItemChecked(position, true);

                // 選択伝票Indexを更新
                selectedSagyochuSyukoDenpyoIndex = position;

                // 荷主・荷渡先・品名を表示
                DisplayNinuNiwaHin();

                // 数量・重量を表示
                DisplaySuryoJuryo();
            }

            OutputConfirmationMessage("選択行をクリアしてもよろしいですか？", _onPositiveButtonClieckListener);

            return false;
        }

        private class onPositiveButtonClieckListener implements DialogInterface.OnClickListener {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 出庫作業削除用のデータを作成
                SyukoDenpyosModel deleteData = new SyukoDenpyosModel();
                deleteData.SyukoDenpyos = new SyukoDenpyoModel[1];
                deleteData.SyukoDenpyos[0] = sagyochuSyukoDenpyos.get(selectedSagyochuSyukoDenpyoIndex);

                // 出庫作業削除
                new DeleteSyukoSagyosTask().execute(deleteData);
            }
        }
    }

    //endregion

    //region 出庫作業開始ボタンをクリック

    private final int REQUESTCODE = 1;

    public void btnGoodsIssuePage1Proceed_Click(View view) {

        if (sagyochuSyukoDenpyos.size() == 0) {
            return;
        }

        if (selectedSagyochuSyukoDenpyoIndex == -1) {
            return;
        }

        Intent intent = new Intent(getApplicationContext(), GoodsIssuePage2Activity.class);
        intent.putExtra(getResources().getString(R.string.intent_key_login_info), loginInfo);
        intent.putExtra(getResources().getString(R.string.intent_key_sagyochu_syuko_denpyos), sagyochuSyukoDenpyos);
        intent.putExtra(getResources().getString(R.string.intent_key_selected_sagyochu_syuko_denpyo_index), selectedSagyochuSyukoDenpyoIndex);

        try {
            startActivityForResult(intent, REQUESTCODE);
            // バーコード関連破棄
            DestroyBarcodeRelation();
        } catch (Exception exception) {
            OutputErrorMessage(exception.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {

            // バーコード関連作成
            CreateBarcodeRelation();

            if (requestCode == REQUESTCODE && resultCode == RESULT_OK) {
                // 作業中データと選択Indexを取得
                //noinspection unchecked
                sagyochuSyukoDenpyos = (ArrayList<SyukoDenpyoModel>) data.getSerializableExtra(getResources().getString(R.string.intent_key_sagyochu_syuko_denpyos));
                selectedSagyochuSyukoDenpyoIndex = data.getIntExtra(getResources().getString(R.string.intent_key_selected_sagyochu_syuko_denpyo_index), -1);

                if (sagyochuSyukoDenpyos == null || sagyochuSyukoDenpyos.size() == 0) {

                    // ListView取得
                    final ListView lvGoodsIssueList = findViewById(R.id.lvGoodsIssueList);
                    // アダプターをクリア
                    lvGoodsIssueList.setAdapter(null);

                    // 荷主・荷渡先・品名をクリア
                    ClearNinuNiwaHin();

                    // 数量・重量をクリア
                    ClearSuryoJuryo();

                    // 作業開始ボタンの無効化
                    EnabledBtnGoodsIssuePage1Proceed(sagyochuSyukoDenpyos != null && sagyochuSyukoDenpyos.size() != 0);
                    return;

                }

                // 出庫伝票を表示
                DisplaySyukoDenpyos();
            }
        } catch (Exception exception) {
            OutputErrorMessage(exception.getMessage());
        }
    }

    //endregion

    //region 進行ボタンの有効・無効化

    private void EnabledBtnGoodsIssuePage1Proceed(boolean isEnabled) {

        SpannableStringBuilder sb = new SpannableStringBuilder("出庫作業開始");
        int start = sb.length();
        int color;

        if (isEnabled) {
            sb.append("\n(全伝票スキャン後に押して下さい)");
            color = getColor(R.color.orientalblue);
        } else {
            sb.append("\n(スキャンした伝票が無いので押せません)");
            color = getColor(R.color.darkgray);
        }

        sb.setSpan(new RelativeSizeSpan(0.5f), start, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        MaterialButton btnGoodsIssuePage1Proceed = findViewById(R.id.btnGoodsIssuePage1Proceed);
        btnGoodsIssuePage1Proceed.setText(sb);
        btnGoodsIssuePage1Proceed.setBackgroundColor(color);
        btnGoodsIssuePage1Proceed.setEnabled(isEnabled);
    }

    //endregion

    //region 出庫伝票の取得

    @SuppressLint("StaticFieldLeak")
    public class GetSyukoDenpyoTask extends SyukoDenpyoController.GetSyukoDenpyoTask {

        // 取得前処理
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // 操作の無効化
            setEnabledOperation(false);
        }

        // 取得後処理
        @Override
        protected void onPostExecute(SyukoDenpyoModel _syukoDenpyoModel) {

            try {

                // エラー発生
                if (_syukoDenpyoModel.Is_error) {
                    String template = "作業中の出庫伝票の削除に失敗しました。\r\n{0}";
                    OutputErrorMessage((java.text.MessageFormat.format(template, _syukoDenpyoModel.Message)));
                    return;
                }

                // 該当データなし
                if (_syukoDenpyoModel.Syukono == 0L) {
                    Toast toast = Toast.makeText(GoodsIssuePage1Activity.this, "該当する出庫伝票がありません", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }

                // 別のユーザーが作業中
                if (_syukoDenpyoModel.Syukosgyjokyo != null && _syukoDenpyoModel.Syukosgyjokyo.Sgytantocd != loginInfo.Sgytantocd) {
                    String template = "{0}が作業中のため、追加できません";
                    OutputErrorMessage((java.text.MessageFormat.format(template, _syukoDenpyoModel.Syukosgyjokyo.Sgytantonm)));
                    return;
                }

                // 既に作業開始済み
                if (_syukoDenpyoModel.Syukosgyjokyo != null) {
                    if (_syukoDenpyoModel.Syukosgyjokyo.Sgyjokyokbn == SgyjokyoKubun.Juryokakunin.getInteger()) {
                        OutputNoTitleMessage("作業完了済みの出庫伝票のため、追加できません。");
                    } else {
                        OutputNoTitleMessage("作業中の出庫伝票のため、追加できません。");
                    }
                    return;
                }

                // 作業状況設定
                SyukoSagyoModel syukoSagyoModel = new SyukoSagyoModel();
                syukoSagyoModel.Sgysyukono = _syukoDenpyoModel.Syukono;
                syukoSagyoModel.Kaicd = loginInfo.Kaicd;
                syukoSagyoModel.Sgytencd = loginInfo.Tensyocd;
                syukoSagyoModel.Sgytantocd = loginInfo.Sgytantocd;
                syukoSagyoModel.Sgysoukocd = loginInfo.Soukocd;
                syukoSagyoModel.Sgydate = loginInfo.Sgydate;
                syukoSagyoModel.Sgyjokyokbn = SgyjokyoKubun.Uketuke.getInteger();

                _syukoDenpyoModel.Syukosgyjokyo = syukoSagyoModel;

                // 出庫作業登録用のデータを作成
                SyukoDenpyosModel postData = new SyukoDenpyosModel();
                postData.SyukoDenpyos = new SyukoDenpyoModel[1];
                postData.SyukoDenpyos[0] = _syukoDenpyoModel;

                // 出庫作業登録
                new PostSyukoSagyosTask().execute(postData);

            } finally {

                // 操作の有効化
                setEnabledOperation(true);

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

            // 操作の無効化
            setEnabledOperation(false);
        }

        // 取得後処理
        @Override
        protected void onPostExecute(SyukoDenpyosModel _syukoDenpyosModel) {

            try {

                // エラー発生
                if (_syukoDenpyosModel.Is_error) {
                    String template = "作業中の出庫伝票の取得に失敗しました。\r\n{0}";
                    OutputErrorMessage((java.text.MessageFormat.format(template, _syukoDenpyosModel.Message)));
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

                // 操作の有効化
                setEnabledOperation(true);

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

            // 操作の無効化
            setEnabledOperation(false);
        }

        // 取得後処理
        @Override
        protected void onPostExecute(final SyukoDenpyosModel _syukoDenpyosModel) {

            try {

                // エラー発生
                if (_syukoDenpyosModel.Is_error) {
                    String template = "未着手の出庫伝票の取得に失敗しました。\r\n{0}";
                    OutputErrorMessage((java.text.MessageFormat.format(template, _syukoDenpyosModel.Message)));
                    return;
                }

                // 該当データなし
                if (_syukoDenpyosModel.SyukoDenpyos == null) {
                    Toast toast = Toast.makeText(GoodsIssuePage1Activity.this, "追加できる出庫伝票がありません", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
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

                        dialog.dismiss();

                        // ✓がついている出庫伝票を取得
                        ArrayList<SyukoDenpyoModel> selectedData = new ArrayList<SyukoDenpyoModel>();
                        for (int i = 0; i < _syukoDenpyosModel.SyukoDenpyos.length; i++) {

                            if (checkedItems[i]) {

                                SyukoSagyoModel syukoSagyoModel = new SyukoSagyoModel();
                                syukoSagyoModel.Sgysyukono = _syukoDenpyosModel.SyukoDenpyos[i].Syukono;
                                syukoSagyoModel.Kaicd = loginInfo.Kaicd;
                                syukoSagyoModel.Sgytencd = loginInfo.Tensyocd;
                                syukoSagyoModel.Sgytantocd = loginInfo.Sgytantocd;
                                syukoSagyoModel.Sgysoukocd = loginInfo.Soukocd;
                                syukoSagyoModel.Sgydate = loginInfo.Sgydate;
                                syukoSagyoModel.Sgyjokyokbn = SgyjokyoKubun.Uketuke.getInteger();

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

                // 操作の有効化
                setEnabledOperation(true);

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

            // 操作の無効化
            setEnabledOperation(false);

        }

        // 登録後処理
        @Override
        protected void onPostExecute(SyukoDenpyosModel _syukoDenpyosModel) {

            try {

                // エラー発生
                if (_syukoDenpyosModel.Is_error) {
                    String template = "出庫作業の登録に失敗しました。\r\n{0}";
                    OutputErrorMessage((java.text.MessageFormat.format(template, _syukoDenpyosModel.Message)));
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

                // 操作の有効化
                setEnabledOperation(true);

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

            // 操作の無効化
            setEnabledOperation(false);
        }

        // 削除後処理
        @Override
        protected void onPostExecute(SyukoDenpyosModel _syukoDenpyosModel) {

            try {

                // エラー発生
                if (_syukoDenpyosModel.Is_error) {
                    String template = "出庫作業の削除に失敗しました。\r\n{0}";
                    OutputErrorMessage((java.text.MessageFormat.format(template, _syukoDenpyosModel.Message)));
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

                // 操作の有効化
                setEnabledOperation(true);

            }
        }

    }

    //endregion

    //region 出庫伝票一覧を表示

    private void DisplaySyukoDenpyos() {

        try {

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
                lvGoodsIssueList.setItemChecked(selectedSagyochuSyukoDenpyoIndex, true);

                // 荷主・荷渡先・品名を表示
                DisplayNinuNiwaHin();

                // 数量・重量を表示
                DisplaySuryoJuryo();
            } else {
                // 荷主・荷渡先・品名をクリア
                ClearNinuNiwaHin();

                // 数量・重量をクリア
                ClearSuryoJuryo();
            }

            // 作業開始ボタンの有効・無効化
            EnabledBtnGoodsIssuePage1Proceed(sagyochuSyukoDenpyos != null && sagyochuSyukoDenpyos.size() != 0);

        } catch (Exception exception) {
            OutputErrorMessage(exception.getMessage());
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

    private void ClearNinuNiwaHin() {
        TextView txtGoodsIssueSlipListDetailNinushi = findViewById(R.id.txtGoodsIssuePage1DetailNinushi);
        TextView txtGoodsIssueSlipListDetailNiwatashi = findViewById(R.id.txtGoodsIssuePage1DetailNiwatashi);
        TextView txtGoodsIssueSlipListDetailProductName = findViewById(R.id.txtGoodsIssuePage1DetailProductName);

        // 荷主名
        txtGoodsIssueSlipListDetailNinushi.setText("");
        // 荷渡名
        txtGoodsIssueSlipListDetailNiwatashi.setText("");
        // 商品名
        txtGoodsIssueSlipListDetailProductName.setText("");
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
        txtGoodsIssuePage1Weight.setText(toFullWidth(String.format("%,d", multiplyThousand(currentSagyochuSyukoDenpyo.Juryo).intValue())));
    }

    private void ClearSuryoJuryo() {
        TextView txtGoodsIssuePage1Quantity = findViewById(R.id.txtGoodsIssuePage1Quantity);
        TextView txtGoodsIssuePage1Weight = findViewById(R.id.txtGoodsIssuePage1Weight);

        // 出庫個数
        txtGoodsIssuePage1Quantity.setText("");
        // 出庫重量
        txtGoodsIssuePage1Weight.setText("");
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
            txtGoodsIssueStatus.setText(EnumClass.getNextSgyjokyoKubunName(syukoDenpyoModel.Syukosgyjokyo.Sgyjokyokbn));

            return view;
        }
    }

    //endregion

    //region ボタンの割り当て

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

    //region 戻るボタン押下時

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtra(getResources().getString(R.string.intent_key_login_info), loginInfo);
        startActivity(intent);
        finish();
    }

    //endregion
}
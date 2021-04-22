package com.example.sozax.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.densowave.bhtsdk.barcode.BarcodeDataReceivedEvent;
import com.densowave.bhtsdk.keyremap.KeyRemapLibrary;
import com.example.sozax.R;
import com.example.sozax.bl.controllers.ZaikoSyokaiController;
import com.example.sozax.bl.models.zaiko_syokai.ZaikoSyokaiModel;
import com.example.sozax.bl.models.zaiko_syokai.ZaikoSyokai_NyusyukkoRirekiModel;
import com.example.sozax.common.ScannerActivity;
import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.sozax.common.CommonFunction.multiplyThousand;
import static com.example.sozax.common.CommonFunction.settingDateFormat;
import static com.example.sozax.common.CommonFunction.toFullWidth;

public class InventoryInquiryPage1Activity extends ScannerActivity implements KeyRemapLibrary.KeyRemapListener {

    // region インスタンス変数
    // 在庫照会データ
    private ZaikoSyokaiModel dispData;

    // キー割り当てライブラリ(DENSO製)
    private KeyRemapLibrary mKeyRemapLibrary;

    // 集計コードチェックパターン
    final private String chkPattern = "2:[0-9]{15}$";

    // ハードウェアキー無効化フラグ
    private boolean isHardwareKeyDisabled = false;

    // 再立ち上げフラグ
    public static boolean isRerase = false;

    // endregion

    // region 初回起動
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_inquiry_page1);

        // ログイン情報を表示
        DisplayLoginInfo();

        // 履歴画面で戻るボタンが押された場合
        if (isRerase){
            dispData = (ZaikoSyokaiModel) getIntent().getSerializableExtra(getResources().getString(R.string.intent_key_zaiko_syokai));
            DisplayData();
            // 終わったら元に戻す
            isRerase = false;
        }
        else{
            // ボタンの無効化
            InvalidBtnInventoryInquiry();
        }



        // アプリ終了
        findViewById(R.id.btnExit).setOnClickListener(new btnExit_Click(InventoryInquiryPage1Activity.this));

        mKeyRemapLibrary = new KeyRemapLibrary();
        mKeyRemapLibrary.createKeyRemap(this, this); // create

    }
    // endregion

    //region 出庫作業のQRをスキャン

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
                            Pattern ptn = Pattern.compile(chkPattern);        // 先頭文字が2であるか、2文字目が:であるか、3桁目以降が数値であるか、17桁であるか
                            Matcher matcher = ptn.matcher(qrData);

                            // 正規表現でチェック
                            if (!matcher.lookingAt()) {
                                // 不正なQRデータの場合メッセージを表示して処理中断
                                AlertDialog.Builder builder = new AlertDialog.Builder(InventoryInquiryPage1Activity.this);
                                builder.setMessage(getResources().getString(R.string.inventory_inquiry_page1_activity_not_hyojihyosqr));
                                builder.show();
                                return;
                            } else {
                                // 正常なQRデータの場合集計コードを引数として取得処理を呼び出し処理続行
                                // QRデータから集計コードを切り出す
                                long syukeicd = Long.parseLong(qrData.substring(2));

                                // 取得
                                new GetZaikoSyokaiTask().execute(syukeicd);
                            }
                        }
                    }.setData(data)
            );
        }
    }

    //endregion

    // region アダプター作成
    private class ListAdapter extends BaseAdapter {

        private final ArrayList<String> list;
        private final LayoutInflater inflater;

        private ListAdapter(Context context, ArrayList<String> list) {
            this.list = list;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // 行のテンプレートを設定
            if (convertView == null)
                convertView = inflater.inflate(R.layout.inventory_inquiry_page1_raw, null);

            // position行目のデータを取得
            final String s = (String) getItem(position);

            // テキストボックスを取得
            TextView txtInventoryInquiryPage1Info = (TextView) convertView.findViewById(R.id.txtInventoryInquiryPage1Info);

            // 値をセット
            txtInventoryInquiryPage1Info.setText(s);

            return convertView;
        }
    }
    // endregion

    //region 在庫照会ボタンをクリックで、在庫履歴に遷移

    public void btnInventoryInquiryPage1Proceed_Click(View view) {

        // 在庫履歴に遷移
        Intent intent = new Intent(this, InventoryInquiryPage2Activity.class);
        intent.putExtra(getResources().getString(R.string.intent_key_login_info), loginInfo);
        intent.putExtra(getResources().getString(R.string.intent_key_zaiko_syokai), dispData);
        startActivity(intent);

        finish();
    }

    //endregion

    //region 進行ボタンの有効・無効化

    // 共通
    private void CommonEnabledBtnInventoryInquiry(SpannableStringBuilder sb, int color,  boolean enabled ) {
        MaterialButton btnInventoryInquiryPage1Proceed = findViewById(R.id.btnInventoryInquiryPage1Proceed);
        btnInventoryInquiryPage1Proceed.setText(sb);
        btnInventoryInquiryPage1Proceed.setBackgroundColor(color);
        btnInventoryInquiryPage1Proceed.setEnabled(enabled);
    }

    // 無効化
    private void InvalidBtnInventoryInquiry() {
        SpannableStringBuilder sb = new SpannableStringBuilder(getResources().getString(R.string.inventory_inquiry_page1_activity_zaikosyokaibtn));
        int start = sb.length();

        sb.append("\n" + getResources().getString(R.string.inventory_inquiry_page1_activity_cannot_press));
        sb.setSpan(new RelativeSizeSpan(0.5f), start, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        int color = getColor(R.color.darkgray);

        CommonEnabledBtnInventoryInquiry(sb,color,false);
    }

    // 有効化
    private void ActiveBtnInventoryInquiry(){
        SpannableStringBuilder sb = new SpannableStringBuilder(getResources().getString(R.string.inventory_inquiry_page1_activity_zaikosyokaibtn));
        int color = getColor(R.color.orientalblue);

        CommonEnabledBtnInventoryInquiry(sb,color,true );
    }

    //endregion

    //region 在庫照会データ取得

    @SuppressLint("StaticFieldLeak")
    private class GetZaikoSyokaiTask extends ZaikoSyokaiController.GetZaikoSyokaiTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // タッチ操作を無効化
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            // デバイスボタン押下時制御実装予定
            isHardwareKeyDisabled = true;

            // プログレスバーを表示
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }

        /**
         * バックグランド処理が完了し、UIスレッドに反映する
         */
        @Override
        protected void onPostExecute(ZaikoSyokaiModel zaikosyokai) {

            try {

                // エラー発生
                if (zaikosyokai.Is_error) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(InventoryInquiryPage1Activity.this);
                    builder.setTitle("エラー");
                    builder.setMessage(java.text.MessageFormat.format(getResources().getString(R.string.inventory_inquiry_page1_activity_failed_getdata_message), zaikosyokai.Message));

                    builder.show();

                    return;
                }

                // 該当データなし
                if (zaikosyokai.Syukeicd == 0) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(InventoryInquiryPage1Activity.this);
                    builder.setMessage(getResources().getString(R.string.inventory_inquiry_page1_activity_nodata_message));

                    builder.show();

                    return;
                }

                // 取得結果を格納
                dispData = zaikosyokai;

                // データ表示
                DisplayData();

            } finally {

                // プログレスバーを非表示
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.INVISIBLE);

                // ハードウェアキーを有効化
                isHardwareKeyDisabled = false;

                // タッチ操作を有効化
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }
    }

    //endregion

    // データ表示処理
    private void DisplayData() {
        // データを準備
        ArrayList<String> zaikoData = new ArrayList<String>();
        zaikoData.add(dispData.Ninusinm);
        zaikoData.add(settingDateFormat(dispData.Nyukodate, "yyyy年MM月dd"));
        zaikoData.add(dispData.Funenm);
        zaikoData.add(dispData.Hinmeinm);
        zaikoData.add(dispData.Kikaku);
        zaikoData.add(dispData.Tanjuryo + "Kg " + dispData.Nisunm);
        zaikoData.add(dispData.Sykdomenm);

        // レイアウトで定義している在庫品情報のリストビューを取得
        ListView lvInventoryInquiryProductInformation = (ListView) findViewById(R.id.lvInventoryInquiryProductInformation);

        // アダプタ－を作成
        ListAdapter adapter = new ListAdapter(InventoryInquiryPage1Activity.this, zaikoData);

        // 在庫品情報にアダプターをセット
        lvInventoryInquiryProductInformation.setAdapter(adapter);

        BigDecimal kosu = BigDecimal.ZERO;
        BigDecimal juryo = BigDecimal.ZERO;

        // 在庫個数、重量計算
        for (ZaikoSyokai_NyusyukkoRirekiModel rireki : dispData.Nyusyukkorireki) {
            // 個数計算
            BigDecimal kosucalc = rireki.Nyuko_kosuu.subtract(rireki.Syukko_kosuu);
            // 入出庫履歴　残表示用に保存
            rireki.Zan_kosu = kosu.add(kosucalc);
            kosu = rireki.Zan_kosu;

            // 重量計算
            BigDecimal juryocalc = rireki.Nyuko_Juryo.subtract(rireki.Syukko_Juryo);
            // 入出庫履歴　残表示用に保存
            rireki.Zan_juryo = juryo.add(juryocalc);
            juryo = rireki.Zan_juryo;
        }

        // 数量、重量合計保持
        dispData.Total_kosu = kosu;
        dispData.Total_juryo = juryo;

        // 在庫個数表示
        TextView txtInventoryInquiryPage1Quantity = findViewById(R.id.txtInventoryInquiryPage1Quantity);
        txtInventoryInquiryPage1Quantity.setText(toFullWidth(String.format("%,d", kosu.intValue())));

        // 在庫重量表示
        TextView txtInventoryInquiryPage1Weight = findViewById(R.id.txtInventoryInquiryPage1Weight);
        txtInventoryInquiryPage1Weight.setText(toFullWidth(String.format("%,d", multiplyThousand(juryo).intValue())));

        // ボタンの有効
        ActiveBtnInventoryInquiry();
    }

    //region ハードキークリック

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if(isHardwareKeyDisabled)
        {
            return  true;
        }

        return super.dispatchKeyEvent(e);
    }

    //endregion

    //region DENSO固有ボタンの設定

    @Override
    public void onKeyRemapCreated() {
        // onCreate処理内のcreateKeyRemap完了時の処理

        // 左トリガーにバーコードスキャンを割り当て
        mKeyRemapLibrary.setRemapKey(KeyRemapLibrary.KeyCode.KEY_CODE_LT.getValue(),
                KeyRemapLibrary.ScanCode.SCAN_CODE_TRIGGER_BARCODE.getString());

        // 右トリガーにバーコードスキャンを割り当て
        mKeyRemapLibrary.setRemapKey(KeyRemapLibrary.KeyCode.KEY_CODE_RT.getValue(),
                KeyRemapLibrary.ScanCode.SCAN_CODE_TRIGGER_BARCODE.getString());
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

    // region 戻るボタン押下時
    @Override
    public void onBackPressed(){

        // 本画面に遷移時メニュー画面は終了されている為、新たにメニュー画面を立ち上げる
        // メニューに遷移
        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtra(getResources().getString(R.string.intent_key_login_info), loginInfo);
        startActivity(intent);

        finish();
    }
    // endregion
}
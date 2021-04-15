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

import com.densowave.bhtsdk.keyremap.KeyRemapLibrary;
import com.example.sozax.R;
import com.example.sozax.bl.controllers.ZaikoSyokaiController;
import com.example.sozax.bl.models.zaiko_syokai.ZaikoSyokaiModel;
import com.example.sozax.bl.models.zaiko_syokai.ZaikoSyokai_NyusyukkoRirekiModel;
import com.example.sozax.common.CommonActivity;
import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InventoryInquiryPage1Activity extends CommonActivity implements KeyRemapLibrary.KeyRemapListener {

    // region インスタンス変数
    // 在庫照会データ
    private ZaikoSyokaiModel dispData;

    // キー割り当てライブラリ(DENSO製)
    public KeyRemapLibrary mKeyRemapLibrary;

    // endregion

    private ListAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_inquiry_page1);

        // ログイン情報を表示
//        TextView txtLoginInfo = findViewById(R.id.txtLoginInfo);
//        SimpleDateFormat sdf = new SimpleDateFormat("M/dd(E)", DateFormatSymbols.getInstance(Locale.JAPAN));
//        txtLoginInfo.setText(logininfo.getOfficeInfo().getName() + "　" + logininfo.getRepresentativeInfo().getName() + "\n" + sdf.format(logininfo.getWorkingday()) +"　"+ logininfo.getWarehouseInfo().getName());

        // ボタンの有効・無効化
        EnabledBtnInventoryInquiryPage1Proceed();

        // アプリ終了
        findViewById(R.id.btnExit).setOnClickListener(new btnExit_Click(InventoryInquiryPage1Activity.this));

        mKeyRemapLibrary = new KeyRemapLibrary();
        mKeyRemapLibrary.createKeyRemap(this, this); // create

    }



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

            // 値がある場合
            if (s != null && s.isEmpty() == false) {
                // 値をセット
                txtInventoryInquiryPage1Info.setText(s);
            } else {
                // 空文字セット
                txtInventoryInquiryPage1Info.setText("");
            }

            return convertView;
        }
    }


    //region 在庫照会ボタンをクリックで、在庫履歴に遷移

    public void btnInventoryInquiryPage1Proceed_Click(View view) {

        // 在庫履歴に遷移
        Intent intent = new Intent(this, InventoryInquiryPage2Activity.class);
        intent.putExtra("intent_key_login_info", loginInfo);
        intent.putExtra("intent_key_zaiko_syokai", dispData);
        startActivity(intent);
    }

    //endregion

    //region 進行ボタンの有効・無効化

    private void EnabledBtnInventoryInquiryPage1Proceed() {
        ListView lvInventoryInquiryProductInformation = findViewById(R.id.lvInventoryInquiryProductInformation);
        SpannableStringBuilder sb = new SpannableStringBuilder("在庫照会");
        int start = sb.length();
        int color;
        boolean enabled;

        if (lvInventoryInquiryProductInformation.getCount() == 0) {
            sb.append("\n(スキャンした伝票が無いので押せません)");
            sb.setSpan(new RelativeSizeSpan(0.5f), start, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            color = getColor(R.color.darkgray);
            enabled = false;
        } else {
            color = getColor(R.color.orientalblue);
            enabled = true;
        }

        MaterialButton btnInventoryInquiryPage1Proceed = findViewById(R.id.btnInventoryInquiryPage1Proceed);
        btnInventoryInquiryPage1Proceed.setText(sb);
        btnInventoryInquiryPage1Proceed.setBackgroundColor(color);
        btnInventoryInquiryPage1Proceed.setEnabled(enabled);
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
                if (zaikosyokai == null) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(InventoryInquiryPage1Activity.this);
                    builder.setMessage(getResources().getString(R.string.inventory_inquiry_page1_activity_nodata_message));

                    builder.show();

                    return;
                }

                // 取得結果をセット
                dispData = zaikosyokai;

                // サンプル用のデータを準備
                ArrayList<String> zaikoData = new ArrayList<String>();
                zaikoData.add(dispData.Ninusinm);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd");
                zaikoData.add(dateFormat.format(dispData.Nyukodate));
                zaikoData.add(dispData.Funenm);
                zaikoData.add(dispData.Hinmeinm);
                zaikoData.add(dispData.Kikaku);
                zaikoData.add(dispData.Tanjuryo + "Kg" + dispData.Nisunm);
                zaikoData.add(dispData.Sykdomenm);

                // レイアウトで定義している在庫品情報のリストビューを取得
                ListView lvInventoryInquiryProductInformation = (ListView) findViewById(R.id.lvInventoryInquiryProductInformation);

                // アダプタ－を作成
                adapter = new ListAdapter(InventoryInquiryPage1Activity.this, zaikoData);

                // 在庫品情報にアダプターをセット
                lvInventoryInquiryProductInformation.setAdapter(adapter);

                // 在庫個数、重量計算
                BigDecimal kosu = BigDecimal.ZERO;
                BigDecimal juryo = BigDecimal.ZERO;

                for (ZaikoSyokai_NyusyukkoRirekiModel rireki : dispData.Nyusyukkorireki) {
                    // 個数計算
                    BigDecimal kosucalc = rireki.Nyuko_kosuu.subtract(rireki.Syukko_kosuu);
                    kosu = kosu.add(kosucalc);

                    // 重量計算
                    BigDecimal juryocalc = rireki.Nyuko_Juryo.subtract(rireki.Syukko_Juryo);
                    juryo = juryo.add(juryocalc);
                }

                // 在庫個数表示
                TextView txtInventoryInquiryPage1Quantity = findViewById(R.id.txtInventoryInquiryPage1Quantity);
                txtInventoryInquiryPage1Quantity.setText(kosu.toString());

                // 在庫重量表示
                TextView txtInventoryInquiryPage1Weight = findViewById(R.id.txtInventoryInquiryPage1Weight);
                txtInventoryInquiryPage1Weight.setText(juryo.toString());

                // ボタンの有効・無効化
                EnabledBtnInventoryInquiryPage1Proceed();

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

    //region ハードキークリック

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.KEYCODE_1 && e.getAction() == KeyEvent.ACTION_DOWN) {

            // QRデータ作成
            String qrData = "2:012016011802411";

            int len = qrData.length();

            // 正規表現パターン
            Pattern pattern = Pattern.compile("2:[0-9]{15}$");        // 先頭文字が2であるか、2文字目が:であるか、3桁目以降が数値であるか、17桁であるか
            Matcher matcher = pattern.matcher(qrData);

            // 正規表現でチェック
            if (!matcher.lookingAt()){
                // 不正なQRデータの場合メッセージを表示して処理中断
                AlertDialog.Builder builder = new AlertDialog.Builder(InventoryInquiryPage1Activity.this);
                builder.setMessage(getResources().getString(R.string.inventory_inquiry_page1_activity_not_hyojihyosqr));
                builder.show();
                return super.dispatchKeyEvent(e);
            }
            else{
                // 正常なQRデータの場合集計コードを引数として取得処理を呼び出し処理続行
                // QRデータから集計コードを切り出す
                long syukeicd = Long.parseLong(qrData.substring(2));

                new GetZaikoSyokaiTask().execute(syukeicd);
            }
        }
        return super.dispatchKeyEvent(e);
    }

    //endregion

    //region DENSO固有ボタンの設定

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // KeyRemapLibrary クラスのインスタンスを解放
        mKeyRemapLibrary.disposeKeyRemap();
    }

    @Override
    public void onKeyRemapCreated() {
        // onCreate処理内のcreateKeyRemap完了時の処理

        // 左トリガーにバーコードスキャンを割り当て
        mKeyRemapLibrary.setRemapKey(KeyRemapLibrary.KeyCode.KEY_CODE_LT.getValue(),
                KeyRemapLibrary.ScanCode.SCAN_CODE_TRIGGER_BARCODE.getString());
        mKeyRemapLibrary.setRemapKey(KeyRemapLibrary.KeyCode.KEY_CODE_LT.getValue(),
                KeyRemapLibrary.ScanCode.SCAN_CODE_F5.getString());

        // 右トリガーにバーコードスキャンを割り当て
        mKeyRemapLibrary.setRemapKey(KeyRemapLibrary.KeyCode.KEY_CODE_RT.getValue(),
                KeyRemapLibrary.ScanCode.SCAN_CODE_TRIGGER_BARCODE.getString());
        mKeyRemapLibrary.setRemapKey(KeyRemapLibrary.KeyCode.KEY_CODE_RT.getValue(),
                KeyRemapLibrary.ScanCode.SCAN_CODE_F6.getString());
    }

    //endregion
}
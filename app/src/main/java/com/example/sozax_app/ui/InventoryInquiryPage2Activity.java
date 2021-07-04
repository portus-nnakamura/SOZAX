package com.example.sozax_app.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.densowave.bhtsdk.keyremap.KeyRemapLibrary;
import com.example.sozax_app.R;
import com.example.sozax_app.bl.models.zaiko_syokai.ZaikoSyokaiModel;
import com.example.sozax_app.bl.models.zaiko_syokai.ZaikoSyokai_NyusyukkoRirekiModel;
import com.example.sozax_app.common.CommonActivity;
import com.example.sozax_app.common.ConstClass;
import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import static com.example.sozax_app.common.CommonFunction.multiplyThousand;
import static com.example.sozax_app.common.CommonFunction.settingDateFormat;
import static com.example.sozax_app.common.CommonFunction.toFullWidth;
import static java.lang.String.format;

public class InventoryInquiryPage2Activity extends CommonActivity{

    // region インスタンス変数

    private ZaikoSyokaiModel dispData;

    // ソート判別用
    private SortMode sortMode = SortMode.Asc;

    // 表示判別用
    private DispMode dispMode = DispMode.Kosu;

    //endregion

    // region enum

    private enum SortMode {
        Asc, Desc
    }

    private enum DispMode {
        Kosu, Juryo
    }

    // endregion

    //region 初回起動
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_inquiry_page2);

        // アプリ終了
        findViewById(R.id.btnExit).setOnClickListener(new btnExit_Click(InventoryInquiryPage2Activity.this));

        // ログイン情報長押し
        findViewById(R.id.clLoginInfo).setOnLongClickListener(new clLoginInfo_LongClick());

        // ログイン情報を表示
        DisplayLoginInfo();

        // 1頁目から送られてきたデータを取得
        dispData = (ZaikoSyokaiModel) getIntent().getSerializableExtra(getResources().getString(R.string.intent_key_zaiko_syokai));

        if (dispData.Nisucd == ConstClass.NISUGATA_BARA) {
            MaterialButton btnInventoryInquiryPage2Weight = findViewById(R.id.btnInventoryInquiryPage2Weight);
            btnInventoryInquiryPage2Weight.setChecked(true);
            dispMode = DispMode.Juryo;
        } else {
            MaterialButton btnInventoryInquiryPage2Quantity = findViewById(R.id.btnInventoryInquiryPage2Quantity);
            btnInventoryInquiryPage2Quantity.setChecked(true);
            dispMode = DispMode.Kosu;
        }

        sortMode = SortMode.Asc;
        Arrays.sort(dispData.Nyusyukkorireki, new dateComparator());

        // データ表示
        DisplayData();
    }

    // endregion

    // region データ表示
    @SuppressLint("DefaultLocale")
    private void DisplayData() {

        // リスト表示
        ListView lvInventoryInquiryPage2Detail = findViewById(R.id.lvInventoryInquiryPage2Detail);

        // アダプター作成
        listAdapter = new ListAdapter(this, dispData.Nyusyukkorireki);
        lvInventoryInquiryPage2Detail.setAdapter(listAdapter);

        // 合計表示
        if (dispData.Nisucd != ConstClass.NISUGATA_BARA) {
            TextView txtInventoryInquiryPage2Quantity = findViewById(R.id.txtInventoryInquiryPage2Quantity);
            txtInventoryInquiryPage2Quantity.setText(toFullWidth(format("%,d", dispData.Total_kosu.intValue())));
        }
        // 重量表示(t→kg)
        TextView txtInventoryInquiryPage2Weight = findViewById(R.id.txtInventoryInquiryPage2Weight);
        txtInventoryInquiryPage2Weight.setText(toFullWidth(format("%,d", multiplyThousand(dispData.Total_juryo).intValue())));
    }

    // endregion

    // region アダプター作成

    ListAdapter listAdapter = null;

    private class ListAdapter extends ArrayAdapter<ZaikoSyokai_NyusyukkoRirekiModel> {

        private final ZaikoSyokai_NyusyukkoRirekiModel[] rirekis;
        private final LayoutInflater inflater;

        public ListAdapter(Context context, ZaikoSyokai_NyusyukkoRirekiModel[] rirekis) {
            super(context, 0, rirekis);
            this.rirekis = rirekis;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return rirekis.length;
        }

        @Override
        public ZaikoSyokai_NyusyukkoRirekiModel getItem(int position) {
            return rirekis[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("DefaultLocale")
        @Override
        public View getView(final int position, View view, ViewGroup parent) {

            if (view == null) view = inflater.inflate(R.layout.inventory_inquiry_page2_raw, null);

            final ZaikoSyokai_NyusyukkoRirekiModel datas = getItem(position);


            // TextView取得
            TextView txtInventoryInquiryPage2Date = view.findViewById(R.id.txtInventoryInquiryPage2Date);
            TextView txtInventoryInquiryPage2GoodsReceipt = view.findViewById(R.id.txtInventoryInquiryPage2GoodsReceipt);
            TextView txtInventoryInquiryPage2GoodsIssue = view.findViewById(R.id.txtInventoryInquiryPage2GoodsIssue);
            TextView txtInventoryInquiryPage2GoodsResidue = view.findViewById(R.id.txtInventoryInquiryPage2GoodsResidue);
            MaterialButton btnInventoryInquiryPage2Quantity = findViewById(R.id.btnInventoryInquiryPage2Quantity);
            MaterialButton btnInventoryInquiryPage2Weight = findViewById(R.id.btnInventoryInquiryPage2Weight);


            if (datas != null) {

                // 各TextViewの初期化
                txtInventoryInquiryPage2Date.setText("");
                txtInventoryInquiryPage2GoodsReceipt.setText("");
                txtInventoryInquiryPage2GoodsIssue.setText("");
                txtInventoryInquiryPage2GoodsResidue.setText("");

                txtInventoryInquiryPage2Date.setText(settingDateFormat(datas.Ukehridate, "yy/MM/dd"));

                // 個数表示
                if (btnInventoryInquiryPage2Quantity.isChecked()) {
                    if (datas.Nyuko_kosuu.compareTo(BigDecimal.ZERO) > 0) {
                        txtInventoryInquiryPage2GoodsReceipt.setText(format("%,d", datas.Nyuko_kosuu.intValue()));
                    }
                    if (datas.Syukko_kosuu.compareTo(BigDecimal.ZERO) > 0) {
                        txtInventoryInquiryPage2GoodsIssue.setText(format("%,d", datas.Syukko_kosuu.intValue()));
                    }

                    txtInventoryInquiryPage2GoodsResidue.setText(format("%,d", datas.Zan_kosu.intValue()));

                    // 重量表示(t→kg)
                } else if (btnInventoryInquiryPage2Weight.isChecked()) {

                    if (datas.Nyuko_Juryo.compareTo(BigDecimal.ZERO) > 0) {
                        txtInventoryInquiryPage2GoodsReceipt.setText(format("%,d", multiplyThousand(datas.Nyuko_Juryo).intValue()));
                    }
                    if (datas.Syukko_Juryo.compareTo(BigDecimal.ZERO) > 0) {
                        txtInventoryInquiryPage2GoodsIssue.setText(format("%,d", multiplyThousand(datas.Syukko_Juryo).intValue()));
                    }

                    txtInventoryInquiryPage2GoodsResidue.setText(format("%,d", multiplyThousand(datas.Zan_juryo).intValue()));
                }
            }
            return view;
        }
    }
    // endregion

    //region 重量切替
    public void btnInventoryInquiryPage2Weight_Click(View view) {

        if (dispMode == DispMode.Kosu) {
            dispMode = DispMode.Juryo;

            if (listAdapter != null) {
                // リスト更新
                listAdapter.notifyDataSetChanged();
            }
        }
    }
    //endregion

    //region 個数切替
    public void btnInventoryInquiryPage2Quantity_Click(View view) {

        if (dispMode == DispMode.Juryo) {
            dispMode = DispMode.Kosu;

            if (listAdapter != null) {
                // リスト更新
                listAdapter.notifyDataSetChanged();
            }
        }
    }
    //endregion

    // region ソート切替
    public void btnSort_Click(View view) {

        // ソート順区分反転
        if (sortMode == SortMode.Asc) {
            sortMode = SortMode.Desc;
        } else {
            sortMode = SortMode.Asc;
        }

        // ソート
        dateComparator dateComparator = new dateComparator();
        Arrays.sort(listAdapter.rirekis, dateComparator);
        Arrays.sort(listAdapter.rirekis, dateComparator);

        if (listAdapter != null) {
            // リスト更新
            listAdapter.notifyDataSetChanged();
        }
    }
    // endregion

    // region ソート条件
    private class dateComparator implements Comparator<ZaikoSyokai_NyusyukkoRirekiModel> {
        @Override
        public int compare(ZaikoSyokai_NyusyukkoRirekiModel zaiko1, ZaikoSyokai_NyusyukkoRirekiModel zaiko2) {
            Date date1 = zaiko1.Ukehridate;
            Date date2 = zaiko2.Ukehridate;

            if (date1.after(date2)) {
                switch (sortMode) {
                    case Asc:
                            return 1;
                    case Desc:
                            return -1;
                }
            }
            // 降順
            else if (date1.before(date2)) {
                switch (sortMode) {
                    case Asc:
                        return -1;
                    case Desc:
                        return 1;
                }
            } else {

                switch (sortMode) {
                    case Asc:
                        return zaiko1.Syukko_Juryo.compareTo(zaiko2.Syukko_Juryo);
                    case Desc:
                        return zaiko1.Nyuko_Juryo.compareTo(zaiko2.Nyuko_Juryo);
                }
            }

            return 0;
        }
    }
    // endregion

    // region 戻るボタン
    @Override
    public void onBackPressed() {

        // メニューに遷移
        Intent intent = new Intent(this, InventoryInquiryPage1Activity.class);
        intent.putExtra(getResources().getString(R.string.intent_key_login_info), loginInfo);
        intent.putExtra(getResources().getString(R.string.intent_key_zaiko_syokai), dispData);
        startActivity(intent);

        InventoryInquiryPage1Activity.isRerase = true;

        finish();
    }
    // endregion

    //region DENSO固有ボタンの設定

    @Override
    public void onKeyRemapCreated() {

        // SCANキーの割り当て削除
        mKeyRemapLibrary.setRemapKey(KeyRemapLibrary.KeyCode.KEY_CODE_CT.getValue(),
                KeyRemapLibrary.ScanCode.SCAN_CODE_NONE.getString());

        // 左トリガーの割り当て削除
        mKeyRemapLibrary.setRemapKey(KeyRemapLibrary.KeyCode.KEY_CODE_LT.getValue(),
                KeyRemapLibrary.ScanCode.SCAN_CODE_NONE.getString());

        // 右トリガーの割り当て削除
        mKeyRemapLibrary.setRemapKey(KeyRemapLibrary.KeyCode.KEY_CODE_RT.getValue(),
                KeyRemapLibrary.ScanCode.SCAN_CODE_NONE.getString());
    }

    //endregion
}
package com.example.sozax.ui;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sozax.R;
import com.example.sozax.bl.models.login_info.LoginInfoModel;
import com.example.sozax.bl.models.zaiko_syokai.ZaikoSyokaiModel;
import com.example.sozax.bl.models.zaiko_syokai.ZaikoSyokai_NyusyukkoRirekiModel;
import com.example.sozax.common.CommonActivity;
import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class InventoryInquiryPage2Activity extends CommonActivity {

    // region インスタンス変数

    private ZaikoSyokaiModel dispData;

    boolean isAsc = true;

    //endregion

    //region 初回起動
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_inquiry_page2);

        MaterialButton btnInventoryInquiryPage2Quantity = findViewById(R.id.btnInventoryInquiryPage2Quantity);
        btnInventoryInquiryPage2Quantity.setChecked(true);

        // ログイン情報取得
        LoginInfoModel loginInfo = (LoginInfoModel) getIntent().getSerializableExtra("intent_key_login_info");

        // ログイン情報を表示
        DisplayLoginInfo(loginInfo);

        // 1頁目から送られてきたデータを取得
        dispData = (ZaikoSyokaiModel) getIntent().getSerializableExtra("intent_key_zaiko_syokai");

        // データ表示
        DisplayData();

        // アプリ終了
        findViewById(R.id.btnExit).setOnClickListener(new btnExit_Click(InventoryInquiryPage2Activity.this));
    }

    // endregion

    // region ログイン情報表示

    public void DisplayLoginInfo(LoginInfoModel loginInfo) {

        TextView txtLoginTensyo = findViewById(R.id.txtLoginTensyo);
        txtLoginTensyo.setText(substringByBytes(loginInfo.Tensyonm, 10));

        TextView txtLoginSgytanto = findViewById(R.id.txtLoginSgytanto);
        txtLoginSgytanto.setText(substringByBytes(loginInfo.Sgytantonm, 10));

        TextView txtLoginSouko = findViewById(R.id.txtLoginSouko);
        txtLoginSouko.setText(substringByBytes(loginInfo.Soukonm, 10));

        SimpleDateFormat sdf = new SimpleDateFormat("M/dd(E)", DateFormatSymbols.getInstance(Locale.JAPAN));
        TextView txtLoginSgydate = findViewById(R.id.txtLoginSgydate);
        txtLoginSgydate.setText(sdf.format(loginInfo.Sgydate));
    }

    // endregion

    // region データ表示
    private void DisplayData() {

        // リスト表示
        ListView lvInventoryInquiryPage2Detail = (ListView) findViewById(R.id.lvInventoryInquiryPage2Detail);

        // アダプター作成
        ListAdapter adapter = new ListAdapter(this, dispData.Nyusyukkorireki);
        lvInventoryInquiryPage2Detail.setAdapter(adapter);
        lvInventoryInquiryPage2Detail.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // 合計表示
        TextView txtInventoryInquiryPage2Quantity = findViewById(R.id.txtInventoryInquiryPage2Quantity);
        txtInventoryInquiryPage2Quantity.setText(toFullWidth(String.format("%,d", dispData.Total_kosu.intValue())));
        TextView txtInventoryInquiryPage2Weight = findViewById(R.id.txtInventoryInquiryPage2Weight);
        // 重量表示(t→kg)
        txtInventoryInquiryPage2Weight.setText(toFullWidth(String.format("%,d", dispData.Total_juryo.multiply(BigDecimal.valueOf(1000)).intValue())));
    }

    // endregion

    // region アダプター作成
    // LIST表示制御用クラス
    // Ver1からの変更点：extends ArrayAdapter<Data> を extends BaseAdapterに変更
    private class ListAdapter extends BaseAdapter {

        private final ZaikoSyokai_NyusyukkoRirekiModel[] rirekis;
        private final LayoutInflater inflater;
        private final Resources r;

        public ListAdapter(Context context, ZaikoSyokai_NyusyukkoRirekiModel[] rirekis) {
            super();
            this.rirekis = rirekis;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            r = context.getResources();
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

        @Override
        public View getView(final int position, View view, ViewGroup parent) {

            if (view == null) view = inflater.inflate(R.layout.inventory_inquiry_page2_raw, null);

            // TextView取得
            TextView txtInventoryInquiryPage2Date = (TextView) view.findViewById(R.id.txtInventoryInquiryPage2Date);
            TextView txtInventoryInquiryPage2GoodsReceipt = (TextView) view.findViewById(R.id.txtInventoryInquiryPage2GoodsReceipt);
            TextView txtInventoryInquiryPage2GoodsIssue = (TextView) view.findViewById(R.id.txtInventoryInquiryPage2GoodsIssue);
            TextView txtInventoryInquiryPage2GoodsResidue = (TextView) view.findViewById(R.id.txtInventoryInquiryPage2GoodsResidue);
            MaterialButton btnInventoryInquiryPage2Quantity = findViewById(R.id.btnInventoryInquiryPage2Quantity);
            MaterialButton btnInventoryInquiryPage2Weight = findViewById(R.id.btnInventoryInquiryPage2Weight);

            final ZaikoSyokai_NyusyukkoRirekiModel datas = getItem(position);

            if (datas != null) {

                // 各TextViewの初期化
                txtInventoryInquiryPage2Date.setText("");
                txtInventoryInquiryPage2GoodsReceipt.setText("");
                txtInventoryInquiryPage2GoodsIssue.setText("");
                txtInventoryInquiryPage2GoodsResidue.setText("");

                SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
                txtInventoryInquiryPage2Date.setText(sdf.format(datas.Ukehridate));

                // 個数表示
                if (btnInventoryInquiryPage2Quantity.isChecked() == true) {
                    if (datas.Nyuko_kosuu.compareTo(BigDecimal.ZERO) == 1) {
                        txtInventoryInquiryPage2GoodsReceipt.setText(String.format("%,d", datas.Nyuko_kosuu.intValue()));
                    }
                    if (datas.Syukko_kosuu.compareTo(BigDecimal.ZERO) == 1) {
                        txtInventoryInquiryPage2GoodsIssue.setText(String.format("%,d", datas.Syukko_kosuu.intValue()));
                    }

                    txtInventoryInquiryPage2GoodsResidue.setText(String.format("%,d", datas.Zan_kosu.intValue()));

                    // 重量表示(t→kg)
                } else if (btnInventoryInquiryPage2Weight.isChecked() == true) {

                    if (datas.Nyuko_Juryo.compareTo(BigDecimal.ZERO) == 1) {
                        txtInventoryInquiryPage2GoodsReceipt.setText(String.format("%,d", datas.Nyuko_Juryo.multiply(BigDecimal.valueOf(1000)).intValue()));
                    }
                    if (datas.Syukko_Juryo.compareTo(BigDecimal.ZERO) == 1) {
                        txtInventoryInquiryPage2GoodsIssue.setText(String.format("%,d", datas.Syukko_Juryo.multiply(BigDecimal.valueOf(1000)).intValue()));
                    }

                    txtInventoryInquiryPage2GoodsResidue.setText(String.format("%,d", datas.Zan_juryo.multiply(BigDecimal.valueOf(1000)).intValue()));
                }
            }
            return view;
        }
    }
    // endregion

    //region 重量切替
    public void btnInventoryInquiryPage2Weight_Click(View view) {
        DisplayData();
    }
    //endregion

    //region 個数切替
    public void btnInventoryInquiryPage2Quantity_Click(View view) {
        DisplayData();
    }
    //endregion

    // region ソート切替
    public void btnSort_Click(View view) {
        isAsc = !isAsc;
        Arrays.sort(dispData.Nyusyukkorireki, new dateComparator());
        DisplayData();
    }
    // endregion

    // region ソート条件
    private class dateComparator implements Comparator<ZaikoSyokai_NyusyukkoRirekiModel> {
        @Override
        public int compare(ZaikoSyokai_NyusyukkoRirekiModel zaiko1, ZaikoSyokai_NyusyukkoRirekiModel zaiko2) {
            Date date1 = zaiko1.Ukehridate;
            Date date2 = zaiko2.Ukehridate;

            // ソート判定
            // 昇順
            if (isAsc && date1.after(date2)) {
                return 1;
            }
            // 降順
            else if (!isAsc && date1.before(date2)) {
                return 1;

            } else if (date1 == date2) {
                return 0;

            } else {
                return -1;
            }
        }
    }
    // endregion

}
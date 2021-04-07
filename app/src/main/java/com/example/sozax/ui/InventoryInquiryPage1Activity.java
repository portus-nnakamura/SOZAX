package com.example.sozax.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.densowave.bhtsdk.keyremap.KeyRemapLibrary;
import com.example.sozax.R;
import com.example.sozax.common.CommonActivity;
import com.google.android.material.button.MaterialButton;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class InventoryInquiryPage1Activity extends CommonActivity {

    private  ListAdapter adapter = null;

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

    //region サンプル

    // LIST表示制御用クラス
    //Ver1からの変更点：extends ArrayAdapter<Data> を extends BaseAdapterに変更
    private class ListAdapter extends BaseAdapter {

        private final ArrayList<String> list;
        private final LayoutInflater inflater;
        private final Resources r;

        public ListAdapter(Context context, ArrayList<String> list) {
            super();
            this.list = list;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            r = context.getResources();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {

            if (view == null) view = inflater.inflate(R.layout.inventory_inquiry_page1_raw, null);

            final String s = getItem(position);
            TextView txtInventoryInquiryPage1Info = (TextView) view.findViewById(R.id.txtInventoryInquiryPage1Info);

            if (s != null && s.isEmpty() == false) {
                txtInventoryInquiryPage1Info.setText(s);
            }
            else
            {
                txtInventoryInquiryPage1Info.setText("");
            }

            return view;
        }
    }

    //endregion

    //region 在庫照会ボタンをクリックで、在庫履歴に遷移

    public void btnInventoryInquiryPage1Proceed_Click(View view) {

        if (adapter == null)
        {

        }
        else
        {
            // 在庫履歴に遷移
            Intent intent = new Intent(this, InventoryInquiryPage2Activity.class);
            intent.putExtra("LOGININFO", logininfo);
            startActivity(intent);
        }
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

    //region ハードキークリック

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {

        if ((e.getKeyCode() == KeyEvent.KEYCODE_F5 ||e.getKeyCode() == KeyEvent.KEYCODE_F6) && e.getAction() == KeyEvent.ACTION_DOWN) {

            // サンプル用のデータを準備
            ArrayList<String> sampleData = new ArrayList<String>();
            sampleData.add("三井物産アグロビジネス㈱札幌");
            sampleData.add("K895 ﾆｭｰﾘｰﾀﾞｰ化成入り特NS785 500kg");
            sampleData.add("口付TB ツル袋");
            sampleData.add("君津 粉砕");
            sampleData.add("釧路丸");

            // リスト表示
            ListView lvInventoryInquiryProductInformation = (ListView) findViewById(R.id.lvInventoryInquiryProductInformation);
            adapter = new ListAdapter(this, sampleData);
            lvInventoryInquiryProductInformation.setAdapter(adapter);
            lvInventoryInquiryProductInformation.setChoiceMode(ListView.CHOICE_MODE_NONE);

            // 在庫個数
            TextView txtInventoryInquiryPage1Quantity = findViewById(R.id.txtInventoryInquiryPage1Quantity);
            txtInventoryInquiryPage1Quantity.setText("５００");

            // 在庫重量
            TextView txtInventoryInquiryPage1Weight = findViewById(R.id.txtInventoryInquiryPage1Weight);
            txtInventoryInquiryPage1Weight.setText("２５０,０００");

            // ボタンの有効・無効化
            EnabledBtnInventoryInquiryPage1Proceed();
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
//        mKeyRemapLibrary.setRemapKey(KeyRemapLibrary.KeyCode.KEY_CODE_LT.getValue(),
//                KeyRemapLibrary.ScanCode.SCAN_CODE_TRIGGER_BARCODE.getString());
        mKeyRemapLibrary.setRemapKey(KeyRemapLibrary.KeyCode.KEY_CODE_LT.getValue(),
                KeyRemapLibrary.ScanCode.SCAN_CODE_F5.getString());

        // 右トリガーにバーコードスキャンを割り当て
//        mKeyRemapLibrary.setRemapKey(KeyRemapLibrary.KeyCode.KEY_CODE_RT.getValue(),
//                KeyRemapLibrary.ScanCode.SCAN_CODE_TRIGGER_BARCODE.getString());
        mKeyRemapLibrary.setRemapKey(KeyRemapLibrary.KeyCode.KEY_CODE_RT.getValue(),
                KeyRemapLibrary.ScanCode.SCAN_CODE_F6.getString());
    }

    //endregion
}
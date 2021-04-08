package com.example.sozax.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sozax.R;
import com.example.sozax.bl.goods_issue.GoodsIssueSlip;
import com.example.sozax.common.CommonActivity;

import java.util.ArrayList;

import static com.example.sozax.ui.InventoryInquiryPage2Activity.toFullWidth;

public class GoodsIssueListActivity extends CommonActivity {

    //region Create

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_issue_list);

        // デモデータセット
        SetDemoData();

        // ログイン情報を表示
        DisplayLoginInfo();

        // 出庫伝票一覧を表示
        DisplayGoodsIssueSlipList();

        // アプリ終了
        findViewById(R.id.btnExit).setOnClickListener(new btnExit_Click(GoodsIssueListActivity.this));
        // 出庫
        findViewById(R.id.btnGoodsIssue).setOnClickListener(new btnGoodsIssue_Click());
        // 行選択
        ((ListView) findViewById(R.id.lvGoodsIssueSlipList)).setOnItemClickListener(new lvGoodsIssueSlipList_Click());
    }

    //endregion

    //region デモデータ

    private ArrayList<GoodsIssueSlip> demoData = null;

    private void SetDemoData() {
        Intent intent = getIntent();
        demoData = (ArrayList<GoodsIssueSlip>) intent.getSerializableExtra("DEMODATA");
    }

    //endregion



    //region 出庫する

    private static final int REQUEST_CODE = 1;

    class btnGoodsIssue_Click implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            ListView lvGoodsIssueSlipList =  ((ListView) findViewById(R.id.lvGoodsIssueSlipList));

            int count = lvGoodsIssueSlipList.getCount();
            boolean chkflg = false;
            for (int i = 0; i < count; i++) {
                CheckBox checkBox = lvGoodsIssueSlipList.getChildAt(i).findViewById(R.id.chkGoodsIssueListSelect);
                if (checkBox.isChecked()) {
                    demoData.get(i).setProgressState(GoodsIssueSlip.ProgressStateEnum.受付);
                }
            }

            Intent intent = new Intent(getApplication(), GoodsIssuePage1Activity.class);
            intent.putExtra("LOGININFO", loginInfo);
            intent.putExtra("DEMODATA",demoData);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE:

                demoData = (ArrayList<GoodsIssueSlip>) data.getSerializableExtra("DEMODATA");

                DisplayGoodsIssueSlipList();

                break;
            default:
                break;
        }
    }

    //endregion

    //region 出庫伝票一覧を表示

    private void DisplayGoodsIssueSlipList() {

        // 並び替え
        ArrayList<GoodsIssueSlip> tmp = new ArrayList<GoodsIssueSlip>();

        for (GoodsIssueSlip goodsIssueSlip:demoData)
        {
            if (goodsIssueSlip.getProgressState() != GoodsIssueSlip.ProgressStateEnum.完了)
            {
                tmp.add(goodsIssueSlip);
            }
        }

        for (GoodsIssueSlip goodsIssueSlip:demoData)
        {
            if (goodsIssueSlip.getProgressState() == GoodsIssueSlip.ProgressStateEnum.完了)
            {
                tmp.add(goodsIssueSlip);
            }
        }

        demoData = tmp;

        // リスト表示
        ListView lvGoodsIssueSlipList = (ListView) findViewById(R.id.lvGoodsIssueSlipList);
        ListAdapter adapter = new ListAdapter(this, demoData);
        lvGoodsIssueSlipList.setAdapter(adapter);
        lvGoodsIssueSlipList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // 詳細表示
        if (demoData.size() > 0) {
            DisplayDetail(demoData.get(0));
        }
    }

    //endregion

    //region 詳細表示

    private void DisplayDetail(GoodsIssueSlip dispData) {

        TextView txtGoodsIssueSlipListDetailNinushi = findViewById(R.id.txtGoodsIssueSlipListDetailNinushi);
        TextView txtGoodsIssueSlipListDetailNiwatashi = findViewById(R.id.txtGoodsIssueSlipListDetailNiwatashi);
        TextView txtGoodsIssueSlipListDetailProductName = findViewById(R.id.txtGoodsIssueSlipListDetailProductName);
        TextView txtGoodsIssueSlipListQuantity = findViewById(R.id.txtGoodsIssueSlipListQuantity);
        TextView txtGoodsIssueSlipListWeight = findViewById(R.id.txtGoodsIssueSlipListWeight);

        // 荷主名
        txtGoodsIssueSlipListDetailNinushi.setText(dispData.getNinushiName());
        // 荷渡名
        txtGoodsIssueSlipListDetailNiwatashi.setText(dispData.getNiwatashiName());
        // 商品名
        txtGoodsIssueSlipListDetailProductName.setText(dispData.getProductName());
        // 出庫個数
        txtGoodsIssueSlipListQuantity.setText(toFullWidth(String.format("%,d", dispData.getQuantity().intValue())));
        // 出庫重量
        txtGoodsIssueSlipListWeight.setText(toFullWidth(String.format("%,d", dispData.getWeight().intValue())));
    }

    //endregion

    //region 行選択

    class lvGoodsIssueSlipList_Click implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            view.setSelected(true);

            GoodsIssueSlip goodsIssueSlip = demoData.get(position);

            // 詳細表示
            DisplayDetail(goodsIssueSlip);
        }
    }

    //endregion

    //region 独自のAdapter

    private class ListAdapter extends BaseAdapter {

        private final ArrayList<GoodsIssueSlip> list;
        private final LayoutInflater inflater;
        private final Resources r;

        public ListAdapter(Context context, ArrayList<GoodsIssueSlip> list) {
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
        public GoodsIssueSlip getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {

            if (view == null) view = inflater.inflate(R.layout.goods_issue_list_raw, null);

            final GoodsIssueSlip tmp = getItem(position);

            TextView txtGoodsIssueListNo = (TextView) view.findViewById(R.id.txtGoodsIssueListNo);
            TextView txtGoodsIssueListSlipNo = (TextView) view.findViewById(R.id.txtGoodsIssueListSlipNo);
            TextView txtGoodsIssueListStatus = (TextView) view.findViewById(R.id.txtGoodsIssueListStatus);
            CheckBox chkGoodsIssueListSelect =  (CheckBox) view.findViewById(R.id.chkGoodsIssueListSelect);

            if (tmp != null) {

                txtGoodsIssueListNo.setText(String.valueOf(position + 1));
                txtGoodsIssueListSlipNo.setText(tmp.getSlipNo());

                if (tmp.getProgressState() != GoodsIssueSlip.ProgressStateEnum.完了) {
                    txtGoodsIssueListStatus.setText("未");

                    GradientDrawable borderDrawable = new GradientDrawable();
                    borderDrawable.setColor(getColor(R.color.frostygray));
                    //borderDrawable.setStroke(1, getColor(R.color.darkgray));
                    borderDrawable.setSize(txtGoodsIssueListNo.getWidth(),txtGoodsIssueListNo.getHeight());
                    LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{borderDrawable});
                    layerDrawable.setLayerInset(0, 0, -2, 0, -2);
                    txtGoodsIssueListNo.setBackground(borderDrawable);

                    borderDrawable = new GradientDrawable();
                    borderDrawable.setColor(getColor(R.color.frostygray));
                    //borderDrawable.setStroke(1, getColor(R.color.darkgray));
                    borderDrawable.setSize(txtGoodsIssueListSlipNo.getWidth(),txtGoodsIssueListSlipNo.getHeight());
                    layerDrawable = new LayerDrawable(new Drawable[]{borderDrawable});
                    layerDrawable.setLayerInset(0, 0, -2, 0, -2);
                    txtGoodsIssueListSlipNo.setBackground(borderDrawable);

                    borderDrawable = new GradientDrawable();
                    borderDrawable.setColor(getColor(R.color.frostygray));
                    //borderDrawable.setStroke(1, getColor(R.color.darkgray));
                    borderDrawable.setSize(txtGoodsIssueListStatus.getWidth(),txtGoodsIssueListStatus.getHeight());
                    layerDrawable = new LayerDrawable(new Drawable[]{borderDrawable});
                    layerDrawable.setLayerInset(0, 0, -2, 0, -2);
                    txtGoodsIssueListStatus.setBackground(borderDrawable);

                    borderDrawable = new GradientDrawable();
                    borderDrawable.setColor(getColor(R.color.frostygray));
                    //borderDrawable.setStroke(1, getColor(R.color.darkgray));
                    borderDrawable.setSize(chkGoodsIssueListSelect.getWidth(),chkGoodsIssueListSelect.getHeight());
                    layerDrawable = new LayerDrawable(new Drawable[]{borderDrawable});
                    layerDrawable.setLayerInset(0, 0, -2, 0, -2);
                    chkGoodsIssueListSelect.setBackground(borderDrawable);
                    chkGoodsIssueListSelect.setEnabled(true);
                    chkGoodsIssueListSelect.setButtonTintList(getColorStateList(R.color.dimgray));

                } else  {
                    txtGoodsIssueListStatus.setText("済");

                    GradientDrawable borderDrawable = new GradientDrawable();
                    borderDrawable.setColor(getColor(R.color.dimgray));
                    //borderDrawable.setStroke(1, getColor(R.color.darkgray));
                    borderDrawable.setSize(txtGoodsIssueListNo.getWidth(),txtGoodsIssueListNo.getHeight());
                    LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{borderDrawable});
                    layerDrawable.setLayerInset(0, 0, -2, 0, -2);
                    txtGoodsIssueListNo.setBackground(borderDrawable);

                    borderDrawable = new GradientDrawable();
                    borderDrawable.setColor(getColor(R.color.dimgray));
                    //borderDrawable.setStroke(1, getColor(R.color.darkgray));
                    borderDrawable.setSize(txtGoodsIssueListSlipNo.getWidth(),txtGoodsIssueListSlipNo.getHeight());
                    layerDrawable = new LayerDrawable(new Drawable[]{borderDrawable});
                    layerDrawable.setLayerInset(0, 0, -2, 0, -2);
                    txtGoodsIssueListSlipNo.setBackground(borderDrawable);

                    borderDrawable = new GradientDrawable();
                    borderDrawable.setColor(getColor(R.color.dimgray));
                    //borderDrawable.setStroke(1, getColor(R.color.darkgray));
                    borderDrawable.setSize(txtGoodsIssueListStatus.getWidth(),txtGoodsIssueListStatus.getHeight());
                    layerDrawable = new LayerDrawable(new Drawable[]{borderDrawable});
                    layerDrawable.setLayerInset(0, 0, -2, 0, -2);
                    txtGoodsIssueListStatus.setBackground(borderDrawable);

                    borderDrawable = new GradientDrawable();
                    borderDrawable.setColor(getColor(R.color.dimgray));
                    //borderDrawable.setStroke(1, getColor(R.color.darkgray));
                    borderDrawable.setSize(chkGoodsIssueListSelect.getWidth(),chkGoodsIssueListSelect.getHeight());
                    layerDrawable = new LayerDrawable(new Drawable[]{borderDrawable});
                    layerDrawable.setLayerInset(0, 0, -2, 0, -2);
                    chkGoodsIssueListSelect.setBackground(borderDrawable);
                    chkGoodsIssueListSelect.setEnabled(false);
                    chkGoodsIssueListSelect.setButtonTintList(getColorStateList(R.color.transparent));
                }
            }
            return view;
        }
    }

    //endregion

    //region 戻るボタン

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra("DEMODATA", demoData);
        setResult(RESULT_OK, intent);
        finish();

    }
    //endregion

    //region DENSO固有ボタンの設定

    @Override
    public void onKeyRemapCreated() {
    }

    //endregion
}
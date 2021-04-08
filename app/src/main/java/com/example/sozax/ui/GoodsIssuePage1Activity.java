package com.example.sozax.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.res.Resources;
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

import com.example.sozax.R;
import com.example.sozax.bl.goods_issue.GoodsIssueSlip;
import com.example.sozax.common.CommonActivity;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import static com.example.sozax.ui.InventoryInquiryPage2Activity.toFullWidth;

public class GoodsIssuePage1Activity extends CommonActivity {

    //region Create

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_issue_page1);

        // デモデータセット
        SetDemoData();

        // ログイン情報を表示
        DisplayLoginInfo();

        // 出庫伝票一覧を表示
        DisplayGoodsIssueSlipList();

        // アプリ終了
        findViewById(R.id.btnExit).setOnClickListener(new btnExit_Click(GoodsIssuePage1Activity.this));
        // 伝票追加
        findViewById(R.id.btnSlipAdd).setOnClickListener(new btnSlipAdd_Click());
        // 伝票削除
        ((ListView) findViewById(R.id.lvGoodsIssueList)).setOnItemLongClickListener(new lvGoodsIssueList_LongClick());
        // 行選択
        ((ListView) findViewById(R.id.lvGoodsIssueList)).setOnItemClickListener(new lvGoodsIssueList_Click());
    }

    //endregion

    //region デモデータ

    private ArrayList<GoodsIssueSlip> demoData = null;

    private void SetDemoData() {
        Intent intent = getIntent();
        demoData = (ArrayList<GoodsIssueSlip>) intent.getSerializableExtra("DEMODATA");
    }

    //endregion



    //region 伝票を追加

    String[] items = null;
    boolean[] itemsChecked = null;

    class btnSlipAdd_Click implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            // ダイアログで表示する[項目]と[✔状態]の配列のサイズを設定
            int len = 0;
            for (GoodsIssueSlip goodsIssueSlip : demoData) {
                // 未着手の伝票のみ、対象とする
                if (goodsIssueSlip.getProgressState() == GoodsIssueSlip.ProgressStateEnum.未着手) {
                    len++;
                }
            }

            if (len == 0) {
                // 追加できる出庫伝票が無い場合、その旨を通知して処理をキャンセルする
                Toast ts = Toast.makeText(GoodsIssuePage1Activity.this, "追加できる出庫伝票がありません", Toast.LENGTH_SHORT);
                ts.setGravity(Gravity.CENTER, 0, 0);
                ts.show();
                return;
            }

            items = new String[len];
            itemsChecked = new boolean[len];

            // ダイアログで表示する[項目]と[✔状態]の配列の中身を設定
            int i = 0;
            for (GoodsIssueSlip goodsIssueSlip : demoData) {

                if (goodsIssueSlip.getProgressState() != GoodsIssueSlip.ProgressStateEnum.未着手) {
                    continue;
                }

                items[i] = goodsIssueSlip.getSlipNo();
                itemsChecked[i] = false;
                i++;
            }

            // ダイアログ作成
            AlertDialog.Builder builder = new AlertDialog.Builder(GoodsIssuePage1Activity.this);

            // タイトル
            builder.setTitle("出庫伝票を選択して下さい");
            // キャンセルボタン
            builder.setNegativeButton("キャンセル", null);
            // OKボタン
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // 進行状況更新
                    int i = 0;
                    for (String item : items) {
                        if (itemsChecked[i] == true) {
                            for (GoodsIssueSlip goodsIssueSlip : demoData) {
                                if (goodsIssueSlip.getSlipNo() == item) {
                                    goodsIssueSlip.setProgressState(GoodsIssueSlip.ProgressStateEnum.受付);
                                    break;
                                }
                            }
                        }
                        i++;
                    }
                }
            });
            // 未着手の出庫伝票一覧
            builder.setMultiChoiceItems(items, itemsChecked, new OnMultiChoiceClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                    // ✔状態を反転
                    itemsChecked[which] = isChecked;

                    // 1件でも✔されているか
                    boolean existsChecked = false;
                    for (boolean b : itemsChecked) {
                        if (b == true) {
                            existsChecked = true;
                            break;
                        }
                    }

                    // 1件でも✔されていれば、OKボタンを有効化
                    AlertDialog alertDialog = (AlertDialog) dialog;
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(existsChecked);
                }
            });

            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {

                    // 1件でも✔されているか
                    boolean existsChecked = false;
                    for (boolean b : itemsChecked) {
                        if (b == true) {
                            existsChecked = true;
                            break;
                        }
                    }

                    if (existsChecked){
                        // 出庫伝票一覧を表示
                        DisplayGoodsIssueSlipList();
                    }
                }
            });

            // ダイアログ表示
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            // OKボタンを無効化
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        }
    }

    //endregion

    //region 伝票を削除

    class lvGoodsIssueList_LongClick implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {

            AlertDialog.Builder builder = new AlertDialog.Builder(GoodsIssuePage1Activity.this);
            builder.setMessage("選択行をクリアしても\nよろしいですか？")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            displayData.get(position).setProgressState(GoodsIssueSlip.ProgressStateEnum.未着手);
                            displayData.remove(position);
                            adapter.notifyDataSetChanged();

                            // 詳細情報をクリア
                            ClearDetail();
                            CurrentSlipIndex = -1;

                            Toast ts = Toast.makeText(GoodsIssuePage1Activity.this, "削除しました", Toast.LENGTH_SHORT);
                            ts.setGravity(Gravity.CENTER, 0, 0);
                            ts.show();
                            return;
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

    //region 進行ボタンをクリックして、作業画面に遷移

    // 現在の伝票Index
    private int CurrentSlipIndex = -1;
    private static final int REQUEST_CODE = 1;

    public void btnGoodsIssuePage1Proceed_Click(View view) {

        if (adapter == null || CurrentSlipIndex == -1) {


        } else {
            Intent intent = new Intent(this, GoodsIssuePage2Activity.class);
            intent.putExtra("LOGININFO", loginInfo);
            intent.putExtra("DISPLAYDATA", displayData);
            intent.putExtra("CURRENTSLIPINDEX", CurrentSlipIndex);

            // 遷移
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE:

                for (GoodsIssueSlip ret : (ArrayList<GoodsIssueSlip>) data.getSerializableExtra("DISPLAYDATA")) {
                    for (GoodsIssueSlip demo : demoData) {
                        if (ret.getSlipNo().equals(demo.getSlipNo())) {
                            // 進行状況を更新
                            demo.setProgressState(ret.getProgressState());
                            break;
                        }
                    }
                }

                // 再表示
                DisplayGoodsIssueSlipList();

                // 詳細クリア
                ClearDetail();

                break;
            default:
                break;
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

    //region 出庫伝票一覧を表示

    private ArrayList<GoodsIssueSlip> displayData = null;

    private void DisplayGoodsIssueSlipList() {
        displayData = new ArrayList<GoodsIssueSlip>();
        for (GoodsIssueSlip goodsIssueSlip : demoData) {
            if (goodsIssueSlip.getProgressState() != GoodsIssueSlip.ProgressStateEnum.未着手 && goodsIssueSlip.getProgressState() != GoodsIssueSlip.ProgressStateEnum.完了) {
                displayData.add(goodsIssueSlip);
            }
        }

        // リスト表示
        ListView lvGoodsIssueList = findViewById(R.id.lvGoodsIssueList);
        adapter = new ListAdapter(GoodsIssuePage1Activity.this, displayData);
        lvGoodsIssueList.setAdapter(adapter);
        lvGoodsIssueList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // 詳細表示
        if (displayData.size() > 0) {

            // 現在の伝票Index
            CurrentSlipIndex = 0;

            DisplayDetail(displayData.get(0));
        }

        // 進行ボタンの有効・無効化
        EnabledBtnGoodsIssuePage1Proceed();
    }

    //endregion

    //region 詳細表示

    private void DisplayDetail(GoodsIssueSlip dispData) {
        TextView txtGoodsIssueSlipListDetailNinushi = findViewById(R.id.txtGoodsIssuePage1DetailNinushi);
        TextView txtGoodsIssueSlipListDetailNiwatashi = findViewById(R.id.txtGoodsIssuePage1DetailNiwatashi);
        TextView txtGoodsIssueSlipListDetailProductName = findViewById(R.id.txtGoodsIssuePage1DetailProductName);
        TextView txtGoodsIssuePage1Quantity = findViewById(R.id.txtGoodsIssuePage1Quantity);
        TextView txtGoodsIssuePage1Weight = findViewById(R.id.txtGoodsIssuePage1Weight);

        // 荷主名
        txtGoodsIssueSlipListDetailNinushi.setText(dispData.getNinushiName());
        // 荷渡名
        txtGoodsIssueSlipListDetailNiwatashi.setText(dispData.getNiwatashiName());
        // 商品名
        txtGoodsIssueSlipListDetailProductName.setText(dispData.getProductName());
        // 出庫個数
        txtGoodsIssuePage1Quantity.setText(toFullWidth(String.format("%,d", dispData.getQuantity().intValue())));
        // 出庫重量
        txtGoodsIssuePage1Weight.setText(toFullWidth(String.format("%,d", dispData.getWeight().intValue())));
    }

    private void ClearDetail() {
        TextView txtGoodsIssueSlipListDetailNinushi = findViewById(R.id.txtGoodsIssuePage1DetailNinushi);
        TextView txtGoodsIssueSlipListDetailNiwatashi = findViewById(R.id.txtGoodsIssuePage1DetailNiwatashi);
        TextView txtGoodsIssueSlipListDetailProductName = findViewById(R.id.txtGoodsIssuePage1DetailProductName);
        TextView txtGoodsIssuePage1Quantity = findViewById(R.id.txtGoodsIssuePage1Quantity);
        TextView txtGoodsIssuePage1Weight = findViewById(R.id.txtGoodsIssuePage1Weight);

        // 荷主名
        txtGoodsIssueSlipListDetailNinushi.setText("");
        // 荷渡名
        txtGoodsIssueSlipListDetailNiwatashi.setText("");
        // 商品名
        txtGoodsIssueSlipListDetailProductName.setText("");
        // 出庫個数
        txtGoodsIssuePage1Quantity.setText("");
        // 出庫重量
        txtGoodsIssuePage1Weight.setText("");
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

    //region 行選択

    class lvGoodsIssueList_Click implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            view.setSelected(true);

            GoodsIssueSlip goodsIssueSlip = displayData.get(position);

            // 詳細表示
            DisplayDetail(goodsIssueSlip);

            // 現在の伝票Index
            CurrentSlipIndex = position;
        }
    }

    //endregion

    //region 独自のAdapter

    private ListAdapter adapter = null;

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

            if (view == null) view = inflater.inflate(R.layout.goods_issue_page1_raw, null);

            final GoodsIssueSlip slipData = getItem(position);
            TextView txtGoodsIssueNo = view.findViewById(R.id.txtGoodsIssueNo);
            TextView txtGoodsIssueSlipNo = view.findViewById(R.id.txtGoodsIssueSlipNo);
            TextView txtGoodsIssueStatus = view.findViewById(R.id.txtGoodsIssueStatus);

            if (slipData != null) {
                txtGoodsIssueNo.setText(String.valueOf(position + 1));
                txtGoodsIssueSlipNo.setText(slipData.getSlipNo());

                if (slipData.getProgressState() == GoodsIssueSlip.ProgressStateEnum.受付) {
                    txtGoodsIssueStatus.setText("受付");
                } else if (slipData.getProgressState() == GoodsIssueSlip.ProgressStateEnum.在庫確認) {
                    txtGoodsIssueStatus.setText("在庫確認");
                } else if (slipData.getProgressState() == GoodsIssueSlip.ProgressStateEnum.作業中) {
                    txtGoodsIssueStatus.setText("作業中");
                } else if (slipData.getProgressState() == GoodsIssueSlip.ProgressStateEnum.受領確認) {
                    txtGoodsIssueStatus.setText("受領確認");
                }
            } else {
                txtGoodsIssueNo.setText("");
                txtGoodsIssueSlipNo.setText("");
                txtGoodsIssueStatus.setText("");
            }

            if(position == 0)
            {
                view.setSelected(true);
            }
            return view;
        }
    }

    //endregion

    //region DENSO固有ボタンの設定

    @Override
    public void onKeyRemapCreated() {
    }

    //endregion
}
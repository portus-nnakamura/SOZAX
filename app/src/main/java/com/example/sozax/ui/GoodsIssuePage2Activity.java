package com.example.sozax.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.densowave.bhtsdk.keyremap.KeyRemapLibrary;
import com.example.sozax.R;
import com.example.sozax.bl.goods_issue.GoodsIssueSlip;
import com.example.sozax.common.CommonActivity;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class GoodsIssuePage2Activity extends CommonActivity {

    //region メンバ変数

    // F2押下中フラグ
    private boolean isF2Down = false;
    // F3押下中フラグ
    private boolean isF3Down = false;

    //endregion

    //region Create

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_issue_page2);

        // デモデータセット
        SetDemoData();

        // リフレッシュ
        Refresh(displayData.get(CurrentSlipIndex));

    }

    //endregion

    //region デモデータ

    private ArrayList<GoodsIssueSlip> demoData = null;
    private ArrayList<GoodsIssueSlip> displayData = null;

    // 現在の伝票Index
    private int CurrentSlipIndex = -1;

    private void SetDemoData() {
        Intent intent = getIntent();
        demoData = (ArrayList<GoodsIssueSlip>) intent.getSerializableExtra("DISPLAYDATA");
        displayData = (ArrayList<GoodsIssueSlip>) demoData.clone();
        CurrentSlipIndex = intent.getIntExtra("CURRENTSLIPINDEX", -1);
    }

    //endregion

    //region 画面をリフレッシュする

    private void Refresh(GoodsIssueSlip goodsIssueSlip) {
        TextView txtProgressPhase2 = findViewById(R.id.txtProgressPhase2);
        TextView txtProgressPhase3 = findViewById(R.id.txtProgressPhase3);
        TextView txtProgressPhase4 = findViewById(R.id.txtProgressPhase4);
        TextView txtGoodsIssuePage2Guidance = findViewById(R.id.txtGoodsIssuePage2Guidance);
        MaterialButton btnGoodsIssuePage2Proceed = findViewById(R.id.btnGoodsIssuePage2Proceed);

        if (goodsIssueSlip.getProgressState() == GoodsIssueSlip.ProgressStateEnum.受付) {
            goodsIssueSlip.setProgressState(GoodsIssueSlip.ProgressStateEnum.在庫確認);
        }

        if (goodsIssueSlip.getProgressState() == GoodsIssueSlip.ProgressStateEnum.在庫確認) {
            // 指示

            // 進行状況
            txtProgressPhase2.setBackgroundColor(getColor(R.color.signalred));
            txtProgressPhase3.setBackgroundColor(getColor(R.color.transparent));
            txtProgressPhase4.setBackgroundColor(getColor(R.color.transparent));
            txtProgressPhase2.setTextColor(getColor(R.color.white));
            txtProgressPhase3.setTextColor(getColor(R.color.black));
            txtProgressPhase4.setTextColor(getColor(R.color.black));

            // ガイダンス
            txtGoodsIssuePage2Guidance.setText(R.string.text_goods_issue_guidance_phase2);

            boolean existsNG = false;
            // 荷主名
        if (goodsIssueSlip.getNinushiName().isEmpty() == false && goodsIssueSlip.getNiwatashiCompareStateEnum() == GoodsIssueSlip.CompareStateEnum.NG) {
            existsNG = true;
        }
        // 荷渡名
        if (goodsIssueSlip.getNiwatashiName().isEmpty() == false && goodsIssueSlip.getNiwatashiCompareStateEnum() == GoodsIssueSlip.CompareStateEnum.NG) {
            existsNG = true;
        }
        // 商品名
        if (goodsIssueSlip.getProductName().isEmpty() == false && goodsIssueSlip.getProductCompareStateEnum() == GoodsIssueSlip.CompareStateEnum.NG) {
            existsNG = true;
        }
        // 荷姿名
        if (goodsIssueSlip.getNisugataName().isEmpty() == false&& goodsIssueSlip.getNisugataCompareStateEnum() == GoodsIssueSlip.CompareStateEnum.NG) {
            existsNG = true;
        }
        // 荷印名
        if (goodsIssueSlip.getNijirushiName().isEmpty() == false&& goodsIssueSlip.getNijirushiCompareStateEnum() == GoodsIssueSlip.CompareStateEnum.NG) {
            existsNG = true;
        }
        // 規格
        if (goodsIssueSlip.getKikaku().isEmpty() == false&& goodsIssueSlip.getKikakuCompareStateEnum() == GoodsIssueSlip.CompareStateEnum.NG) {
            existsNG = true;
        }
        // 船名
        if (goodsIssueSlip.getFuneName().isEmpty() == false&& goodsIssueSlip.getFuneCompareStateEnum() == GoodsIssueSlip.CompareStateEnum.NG) {
            existsNG = true;
        }

            boolean allOK = true;
            // 荷主名
        if (goodsIssueSlip.getNinushiName().isEmpty() == false && goodsIssueSlip.getNiwatashiCompareStateEnum() != GoodsIssueSlip.CompareStateEnum.OK) {
            allOK = false;
        }
        // 荷渡名
        if (goodsIssueSlip.getNiwatashiName().isEmpty() == false && goodsIssueSlip.getNiwatashiCompareStateEnum() != GoodsIssueSlip.CompareStateEnum.OK) {
            allOK = false;
        }
        // 商品名
        if (goodsIssueSlip.getProductName().isEmpty() == false && goodsIssueSlip.getProductCompareStateEnum() != GoodsIssueSlip.CompareStateEnum.OK) {
            allOK = false;
        }
        // 荷姿名
        if (goodsIssueSlip.getNisugataName().isEmpty() == false&& goodsIssueSlip.getNisugataCompareStateEnum() != GoodsIssueSlip.CompareStateEnum.OK) {
            allOK = false;
        }
        // 荷印名
        if (goodsIssueSlip.getNijirushiName().isEmpty() == false&& goodsIssueSlip.getNijirushiCompareStateEnum() != GoodsIssueSlip.CompareStateEnum.OK) {
            allOK = false;
        }
        // 規格
        if (goodsIssueSlip.getKikaku().isEmpty() == false&& goodsIssueSlip.getKikakuCompareStateEnum() != GoodsIssueSlip.CompareStateEnum.OK) {
            allOK = false;
        }
        // 船名
        if (goodsIssueSlip.getFuneName().isEmpty() == false&& goodsIssueSlip.getFuneCompareStateEnum() != GoodsIssueSlip.CompareStateEnum.OK) {
            allOK = false;
        }

            if(allOK)
            {
                // 進行ボタン
                SpannableStringBuilder sb = new SpannableStringBuilder();
                sb.append("出庫指示");
                int start = sb.length();
                sb.append("\n(出庫指示後に押して下さい)");
                sb.setSpan(new RelativeSizeSpan(0.5f), start, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                btnGoodsIssuePage2Proceed.setText(sb);
                btnGoodsIssuePage2Proceed.setBackgroundColor(getColor(R.color.coral));
                btnGoodsIssuePage2Proceed.setEnabled(true);
            }
            else if(existsNG)
            {
                // 進行ボタン
                SpannableStringBuilder sb = new SpannableStringBuilder("出庫指示");
                int start = sb.length();
                int color;
                boolean enabled;

                sb.append("\n(NGが含まれているため、押せません)");
                sb.setSpan(new RelativeSizeSpan(0.5f), start, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                color = getColor(R.color.darkgray);
                enabled = false;

                btnGoodsIssuePage2Proceed.setText(sb);
                btnGoodsIssuePage2Proceed.setBackgroundColor(color);
                btnGoodsIssuePage2Proceed.setEnabled(enabled);
            }
            else
            {
                SpannableStringBuilder sb = new SpannableStringBuilder("出庫指示");
                int start = sb.length();
                int color;
                boolean enabled;

                    sb.append("\n(在庫確認が未了のため、押せません)");
                    sb.setSpan(new RelativeSizeSpan(0.5f), start, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    color = getColor(R.color.darkgray);
                    enabled = false;

                btnGoodsIssuePage2Proceed.setText(sb);
                btnGoodsIssuePage2Proceed.setBackgroundColor(color);
                btnGoodsIssuePage2Proceed.setEnabled(enabled);
            }



        } else if (goodsIssueSlip.getProgressState() == GoodsIssueSlip.ProgressStateEnum.作業中) {
            // 完了

            // 進行状況
            txtProgressPhase2.setBackgroundColor(getColor(R.color.signalred));
            txtProgressPhase3.setBackgroundColor(getColor(R.color.signalred));
            txtProgressPhase4.setBackgroundColor(getColor(R.color.transparent));
            txtProgressPhase2.setTextColor(getColor(R.color.white));
            txtProgressPhase3.setTextColor(getColor(R.color.white));
            txtProgressPhase4.setTextColor(getColor(R.color.black));

            // ガイダンス
            txtGoodsIssuePage2Guidance.setText(R.string.text_goods_issue_guidance_phase3);

            // 進行ボタン
            SpannableStringBuilder sb = new SpannableStringBuilder();
            sb.append("出庫完了");
            int start = sb.length();
            sb.append("\n(出庫作業完了後に押して下さい)");
            sb.setSpan(new RelativeSizeSpan(0.5f), start, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            btnGoodsIssuePage2Proceed.setText(sb);
            btnGoodsIssuePage2Proceed.setBackgroundColor(getColor(R.color.coral));
            btnGoodsIssuePage2Proceed.setEnabled(true);
        } else if (goodsIssueSlip.getProgressState() == GoodsIssueSlip.ProgressStateEnum.受領確認) {
            // 確認

            // 進行状況
            txtProgressPhase2.setBackgroundColor(getColor(R.color.signalred));
            txtProgressPhase3.setBackgroundColor(getColor(R.color.signalred));
            txtProgressPhase4.setBackgroundColor(getColor(R.color.signalred));
            txtProgressPhase2.setTextColor(getColor(R.color.white));
            txtProgressPhase3.setTextColor(getColor(R.color.white));
            txtProgressPhase4.setTextColor(getColor(R.color.white));

            // ガイダンス
            txtGoodsIssuePage2Guidance.setText(R.string.text_goods_issue_guidance_phase4);

            // 進行ボタン
            SpannableStringBuilder sb = new SpannableStringBuilder();
            sb.append("受領確認");
            int start = sb.length();
            sb.append("\n(上記出庫品を正に受領いたしました)");
            sb.setSpan(new RelativeSizeSpan(0.5f), start, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            btnGoodsIssuePage2Proceed.setText(sb);
            btnGoodsIssuePage2Proceed.setBackgroundColor(getColor(R.color.signalred));
            btnGoodsIssuePage2Proceed.setEnabled(true);
        }

        // ヘッダの伝票情報
        TextView txtSlipInfo = findViewById(R.id.txtSlipInfo);
        // 総伝票数
        int totalSlipCount = displayData.size();
        // 現在のページ番号
        int CurrentPageNo = CurrentSlipIndex + 1;
        // ページ情報
        String pageInfo = "（" + CurrentPageNo + "/" + totalSlipCount + "）";
        // 伝票番号
        String slipNo = displayData.get(CurrentSlipIndex).getSlipNo();
        txtSlipInfo.setText(pageInfo + "\nNo." + slipNo);

        // 出庫品情報
        ListView lvGoodsIssueProductInformation = findViewById(R.id.lvGoodsIssueProductInformation);
        ArrayList<template> sampleData = new ArrayList<template>();

        template tmp = null;
        // 荷主名
        if (goodsIssueSlip.getNinushiName().isEmpty() == false) {
            tmp = new template();
            tmp.product_info = goodsIssueSlip.getNinushiName();
            tmp.product_info_compare_state = goodsIssueSlip.getNinushiCompareStateEnum();
            sampleData.add(tmp);
        }
        // 荷渡名
        if (goodsIssueSlip.getNiwatashiName().isEmpty() == false) {
            tmp = new template();
            tmp.product_info = goodsIssueSlip.getNiwatashiName();
            tmp.product_info_compare_state = goodsIssueSlip.getNiwatashiCompareStateEnum();
            sampleData.add(tmp);
        }
        // 商品名
        if (goodsIssueSlip.getProductName().isEmpty() == false) {
            tmp = new template();
            tmp.product_info = goodsIssueSlip.getProductName();
            tmp.product_info_compare_state = goodsIssueSlip.getProductCompareStateEnum();
            sampleData.add(tmp);
        }
        // 荷姿名
        if (goodsIssueSlip.getNisugataName().isEmpty() == false) {
            tmp = new template();
            tmp.product_info = goodsIssueSlip.getNisugataName();
            tmp.product_info_compare_state = goodsIssueSlip.getNisugataCompareStateEnum();
            sampleData.add(tmp);
        }
        // 荷印名
        if (goodsIssueSlip.getNijirushiName().isEmpty() == false) {
            tmp = new template();
            tmp.product_info = goodsIssueSlip.getNijirushiName();
            tmp.product_info_compare_state = goodsIssueSlip.getNijirushiCompareStateEnum();
            sampleData.add(tmp);
        }
        // 規格
        if (goodsIssueSlip.getKikaku().isEmpty() == false) {
            tmp = new template();
            tmp.product_info = goodsIssueSlip.getKikaku();
            tmp.product_info_compare_state = goodsIssueSlip.getKikakuCompareStateEnum();
            sampleData.add(tmp);
        }
        // 船名
        if (goodsIssueSlip.getFuneName().isEmpty() == false) {
            tmp = new template();
            tmp.product_info = goodsIssueSlip.getFuneName();
            tmp.product_info_compare_state = goodsIssueSlip.getFuneCompareStateEnum();
            sampleData.add(tmp);
        }

        // リスト表示
        ListAdapter adapter = new ListAdapter(this, sampleData);
        lvGoodsIssueProductInformation.setAdapter(adapter);
        lvGoodsIssueProductInformation.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // 出庫個数
        TextView txtGoodsIssuePage2Quantity = findViewById(R.id.txtGoodsIssuePage2Quantity);
        txtGoodsIssuePage2Quantity.setText(toFullWidth(String.format("%,d",goodsIssueSlip.getQuantity().intValue())));

        // 出庫重量
        TextView txtGoodsIssuePage2Weight = findViewById(R.id.txtGoodsIssuePage2Weight);
        txtGoodsIssuePage2Weight.setText(toFullWidth(String.format("%,d",goodsIssueSlip.getWeight().intValue())));
    }

    public void btnPreviousSlip_Click(View view) {

        if (CurrentSlipIndex > 0) {
            CurrentSlipIndex--;
        }

        Refresh(displayData.get(CurrentSlipIndex));
    }

    //endregion

    //region 前伝票を表示する

    public void btnNextSlip_Click(View view) {

        if (CurrentSlipIndex < displayData.size() - 1) {
            CurrentSlipIndex++;
        }

        Refresh(displayData.get(CurrentSlipIndex));
    }

    //endregion

    //region 次伝票を表示する



    public void btnGoodsIssuePage2Proceed(View view) {


            // 更新
            UpdateProgressState();

            if (displayData.size() > 0) {
                // 伝票データを再取得
                GoodsIssueSlip goodsIssueSlip = displayData.get(CurrentSlipIndex);

                // 画面をリフレッシュ
                Refresh(goodsIssueSlip);
            }

    }

    //endregion

    //region 現在の進行状況を一つ進める

    // 進行状況を更新する

    private void UpdateProgressState() {
        // 現在の進行状況を一つ進める
        if (displayData.get(CurrentSlipIndex).getProgressState() == GoodsIssueSlip.ProgressStateEnum.在庫確認) {
            // 指示
            displayData.get(CurrentSlipIndex).setProgressState(GoodsIssueSlip.ProgressStateEnum.作業中);

            for (GoodsIssueSlip goodsIssueSlip : demoData) {
                if (displayData.get(CurrentSlipIndex).getSlipNo().equals(goodsIssueSlip.getSlipNo())) {
                    goodsIssueSlip.setProgressState(displayData.get(CurrentSlipIndex).getProgressState());
                }
            }

        } else if (displayData.get(CurrentSlipIndex).getProgressState() == GoodsIssueSlip.ProgressStateEnum.作業中) {
            // 完了
            displayData.get(CurrentSlipIndex).setProgressState(GoodsIssueSlip.ProgressStateEnum.受領確認);

            for (GoodsIssueSlip goodsIssueSlip : demoData) {
                if (displayData.get(CurrentSlipIndex).getSlipNo().equals(goodsIssueSlip.getSlipNo())) {
                    goodsIssueSlip.setProgressState(displayData.get(CurrentSlipIndex).getProgressState());
                }
            }

        } else if (displayData.get(CurrentSlipIndex).getProgressState() == GoodsIssueSlip.ProgressStateEnum.受領確認) {
            // 確認
            displayData.get(CurrentSlipIndex).setProgressState(GoodsIssueSlip.ProgressStateEnum.完了);

            for (GoodsIssueSlip goodsIssueSlip : demoData) {
                if (displayData.get(CurrentSlipIndex).getSlipNo().equals(goodsIssueSlip.getSlipNo())) {
                    goodsIssueSlip.setProgressState(displayData.get(CurrentSlipIndex).getProgressState());
                }
            }

            displayData.remove(CurrentSlipIndex);

            if (displayData.size() > 0) {
                CurrentSlipIndex = 0;
            } else {
                Intent intent = new Intent();
                intent.putExtra("DISPLAYDATA", demoData);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    //endregion

    //region 独自のAdapter

    private class ListAdapter extends BaseAdapter {

        private final ArrayList<template> list;
        private final LayoutInflater inflater;
        private final Resources r;

        public ListAdapter(Context context, ArrayList<template> list) {
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
        public template getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {

            if (view == null) view = inflater.inflate(R.layout.goods_issue_page2_raw, null);

            final template tmpData = getItem(position);
            TextView txtGoodsIssuePage2ProductInformation = view.findViewById(R.id.txtGoodsIssuePage2ProductInformation);
            TextView txtGoodsIssuePage2Status = view.findViewById(R.id.txtGoodsIssuePage2Status);

            if (tmpData != null) {

                txtGoodsIssuePage2ProductInformation.setText(tmpData.product_info);

                if (tmpData.product_info_compare_state == GoodsIssueSlip.CompareStateEnum.対象外) {
                    txtGoodsIssuePage2Status.setText("");
                    txtGoodsIssuePage2Status.setTextColor(getColor(R.color.black));
                } else if (tmpData.product_info_compare_state == GoodsIssueSlip.CompareStateEnum.OK) {
                    txtGoodsIssuePage2Status.setText("OK");
                    txtGoodsIssuePage2Status.setTextColor(getColor(R.color.palegreen));
                } else if (tmpData.product_info_compare_state == GoodsIssueSlip.CompareStateEnum.NG) {
                    txtGoodsIssuePage2Status.setText("NG");
                    txtGoodsIssuePage2Status.setTextColor(getColor(R.color.signalred));
                }
            } else {
                txtGoodsIssuePage2ProductInformation.setText("");
                txtGoodsIssuePage2Status.setText("");
                txtGoodsIssuePage2Status.setTextColor(getColor(R.color.black));
            }

            return view;
        }
    }

    private class template {
        public String product_info = "";
        public GoodsIssueSlip.CompareStateEnum product_info_compare_state = GoodsIssueSlip.CompareStateEnum.対象外;
    }

    //endregion

    //region 戻るボタン

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra("DISPLAYDATA", demoData);
        setResult(RESULT_OK, intent);
        finish();

    }

    //endregion

    //region 全角変換

    /**
     * 「半角数字」を「全角数字」へ変換処理を実施する。
     *
     * @param s 対象文字列
     * @return 変換結果
     */
    public static String toFullWidth(String s) {
        StringBuilder sb = new StringBuilder(s);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (0x30 <= c && c <= 0x39) {
                sb.setCharAt(i, (char) (c + 0xFEE0));
            }
        }
        return sb.toString();
    }

    //endregion

    //region ハードキークリック

    final String[] items = {"目視で間違いないことを確認しました"};
    final boolean[] checkedItems = {false};

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {

        GoodsIssueSlip goodsIssueSlip = displayData.get(CurrentSlipIndex);

        if (goodsIssueSlip.getProgressState() != GoodsIssueSlip.ProgressStateEnum.在庫確認) {
            return super.dispatchKeyEvent(e);
        }

        if (e.getKeyCode() == KeyEvent.KEYCODE_F5) {

            // 荷主名
            goodsIssueSlip.setNinushiCompareStateEnum(GoodsIssueSlip.CompareStateEnum.OK);
            // 荷渡名
            goodsIssueSlip.setNiwatashiCompareStateEnum(GoodsIssueSlip.CompareStateEnum.OK);
            // 商品名
            goodsIssueSlip.setProductCompareStateEnum(GoodsIssueSlip.CompareStateEnum.OK);
            // 荷姿名
            goodsIssueSlip.setNisugataCompareStateEnum(GoodsIssueSlip.CompareStateEnum.OK);
            // 荷印名
            goodsIssueSlip.setNijirushiCompareStateEnum(GoodsIssueSlip.CompareStateEnum.OK);
            // 規格
            goodsIssueSlip.setKikakuCompareStateEnum(GoodsIssueSlip.CompareStateEnum.OK);
            // 船名
            goodsIssueSlip.setFuneCompareStateEnum(GoodsIssueSlip.CompareStateEnum.OK);

            // リフレッシュ
            Refresh(displayData.get(CurrentSlipIndex));
        }
        else if (e.getKeyCode() == KeyEvent.KEYCODE_F6) {

            // 荷主名
            goodsIssueSlip.setNinushiCompareStateEnum(GoodsIssueSlip.CompareStateEnum.OK);
            // 荷渡名
            goodsIssueSlip.setNiwatashiCompareStateEnum(GoodsIssueSlip.CompareStateEnum.OK);
            // 商品名
            goodsIssueSlip.setProductCompareStateEnum(GoodsIssueSlip.CompareStateEnum.OK);
            // 荷姿名
            goodsIssueSlip.setNisugataCompareStateEnum(GoodsIssueSlip.CompareStateEnum.NG);
            // 荷印名
            goodsIssueSlip.setNijirushiCompareStateEnum(GoodsIssueSlip.CompareStateEnum.OK);
            // 規格
            goodsIssueSlip.setKikakuCompareStateEnum(GoodsIssueSlip.CompareStateEnum.NG);
            // 船名
            goodsIssueSlip.setFuneCompareStateEnum(GoodsIssueSlip.CompareStateEnum.OK);

            Vibrate();

            // リフレッシュ
            Refresh(displayData.get(CurrentSlipIndex));
        }

        else if (e.getKeyCode() == KeyEvent.KEYCODE_F2) {

            if(e.getAction() == KeyEvent.ACTION_DOWN)
            {
                isF2Down = true;
            }
            else if(e.getAction() == KeyEvent.ACTION_UP)
            {
                isF2Down = false;
            }
        } else if (e.getKeyCode() == KeyEvent.KEYCODE_F3) {

            if(e.getAction() == KeyEvent.ACTION_DOWN)
            {
                isF3Down = true;
            }
            else if(e.getAction() == KeyEvent.ACTION_UP)
            {
                isF3Down = false;
            }
        }

        if(isF2Down == true && isF3Down == true)
        {
          boolean existsNG = false;
            // 荷主名
        if (goodsIssueSlip.getNinushiName().isEmpty() == false && goodsIssueSlip.getNiwatashiCompareStateEnum() == GoodsIssueSlip.CompareStateEnum.NG) {
            existsNG = true;
        }
        // 荷渡名
        if (goodsIssueSlip.getNiwatashiName().isEmpty() == false && goodsIssueSlip.getNiwatashiCompareStateEnum() == GoodsIssueSlip.CompareStateEnum.NG) {
            existsNG = true;
        }
        // 商品名
        if (goodsIssueSlip.getProductName().isEmpty() == false && goodsIssueSlip.getProductCompareStateEnum() == GoodsIssueSlip.CompareStateEnum.NG) {
            existsNG = true;
        }
        // 荷姿名
        if (goodsIssueSlip.getNisugataName().isEmpty() == false&& goodsIssueSlip.getNisugataCompareStateEnum() == GoodsIssueSlip.CompareStateEnum.NG) {
            existsNG = true;
        }
        // 荷印名
        if (goodsIssueSlip.getNijirushiName().isEmpty() == false&& goodsIssueSlip.getNijirushiCompareStateEnum() == GoodsIssueSlip.CompareStateEnum.NG) {
            existsNG = true;
        }
        // 規格
        if (goodsIssueSlip.getKikaku().isEmpty() == false&& goodsIssueSlip.getKikakuCompareStateEnum() == GoodsIssueSlip.CompareStateEnum.NG) {
            existsNG = true;
        }
        // 船名
        if (goodsIssueSlip.getFuneName().isEmpty() == false&& goodsIssueSlip.getFuneCompareStateEnum() == GoodsIssueSlip.CompareStateEnum.NG) {
            existsNG = true;
        }

            boolean allOK = true;
            // 荷主名
        if (goodsIssueSlip.getNinushiName().isEmpty() == false && goodsIssueSlip.getNiwatashiCompareStateEnum() != GoodsIssueSlip.CompareStateEnum.OK) {
            allOK = false;
        }
        // 荷渡名
        if (goodsIssueSlip.getNiwatashiName().isEmpty() == false && goodsIssueSlip.getNiwatashiCompareStateEnum() != GoodsIssueSlip.CompareStateEnum.OK) {
            allOK = false;
        }
        // 商品名
        if (goodsIssueSlip.getProductName().isEmpty() == false && goodsIssueSlip.getProductCompareStateEnum() != GoodsIssueSlip.CompareStateEnum.OK) {
            allOK = false;
        }
        // 荷姿名
        if (goodsIssueSlip.getNisugataName().isEmpty() == false&& goodsIssueSlip.getNisugataCompareStateEnum() != GoodsIssueSlip.CompareStateEnum.OK) {
            allOK = false;
        }
        // 荷印名
        if (goodsIssueSlip.getNijirushiName().isEmpty() == false&& goodsIssueSlip.getNijirushiCompareStateEnum() != GoodsIssueSlip.CompareStateEnum.OK) {
            allOK = false;
        }
        // 規格
        if (goodsIssueSlip.getKikaku().isEmpty() == false&& goodsIssueSlip.getKikakuCompareStateEnum() != GoodsIssueSlip.CompareStateEnum.OK) {
            allOK = false;
        }
        // 船名
        if (goodsIssueSlip.getFuneName().isEmpty() == false&& goodsIssueSlip.getFuneCompareStateEnum() != GoodsIssueSlip.CompareStateEnum.OK) {
            allOK = false;
        }

        if(allOK == false)
        {
            // F2&F3押下中であるなら、
            AlertDialog.Builder builder = new AlertDialog.Builder(GoodsIssuePage2Activity.this);

            TextView msgTxt = new TextView(this);
            msgTxt.setTextSize((float) 14.0);
            msgTxt.setTextColor(getColor(R.color.black));
            msgTxt.setPadding(20, 20, 0, 0);

            if(existsNG == true)
            {
                msgTxt.setText("NGが含まれていますが、よろしいですか？");
            }
            else
            {
                msgTxt.setText("表示票がスキャンされていませんが\nよろしいですか？");
            }

            msgTxt.setGravity(Gravity.LEFT);
            builder.setCustomTitle(msgTxt);

            checkedItems[0] = false;
            builder//.setTitle("表示票がスキャンされていませんが\nよろしいですか？")
                    .setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            checkedItems[which] = isChecked;
                            AlertDialog alertDialog = (AlertDialog) dialog;
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(isChecked);
                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (checkedItems[0] == false) {
                                return;
                            }

                            // 更新
                            UpdateProgressState();

                            if (displayData.size() > 0) {
                                // 伝票データを再取得
                                GoodsIssueSlip goodsIssueSlip = displayData.get(CurrentSlipIndex);

                                // 画面をリフレッシュ
                                Refresh(goodsIssueSlip);
                            }
                        }
                    })
                    .setNegativeButton("キャンセル", null)
                    .setCancelable(true);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        }
        }

       return super.dispatchKeyEvent(e);
    }

    //endregion


}
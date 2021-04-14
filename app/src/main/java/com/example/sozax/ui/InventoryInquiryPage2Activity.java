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
import com.example.sozax.common.CommonActivity;
import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import static java.math.BigDecimal.valueOf;

public class InventoryInquiryPage2Activity extends CommonActivity {

    //region Create

    boolean isAsc = true;

    //endregion

    //region テンプレ

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_inquiry_page2);

        MaterialButton btnInventoryInquiryPage2Quantity = findViewById(R.id.btnInventoryInquiryPage2Quantity);
        btnInventoryInquiryPage2Quantity.setChecked(true);

        DisplaySample();

        // ログイン情報を表示
//        TextView txtLoginInfo = findViewById(R.id.txtLoginInfo);
//        SimpleDateFormat sdf = new SimpleDateFormat("M/dd(E)", DateFormatSymbols.getInstance(Locale.JAPAN));
//        txtLoginInfo.setText(logininfo.getOfficeInfo().getName() + "　" + logininfo.getRepresentativeInfo().getName() + "\n" + sdf.format(logininfo.getWorkingday()) + "　" + logininfo.getWarehouseInfo().getName());

        // アプリ終了
        findViewById(R.id.btnExit).setOnClickListener(new btnExit_Click(InventoryInquiryPage2Activity.this));
    }

    private template_layer1 GetSample() {

        template_layer1 tmpData = new template_layer1();

        // 在庫
        tmpData.setStockQuantity(valueOf(500));
        tmpData.setStockWeight(valueOf(250000));

        for (int i = 0; i < 3; i++) {
            tmpData.l2.add(new template_layer2());
        }

        Date d = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(d);

        template_layer2 tmp;

        c.add(Calendar.DAY_OF_MONTH, 1);
        d = c.getTime();
        tmp = tmpData.l2.get(0);
        tmp.setDate(d);
        tmp.setGoodsReceiptQuantity(valueOf(750));
        tmp.setGoodsIssueQuantity(valueOf(0));
        tmp.setGoodsResidueQuantity(valueOf(750));
        tmp.setGoodsReceiptWeight(valueOf(375000));
        tmp.setGoodsIssueWeight(valueOf(0));
        tmp.setGoodsResidueWeight(valueOf(375000));

        tmp = new template_layer2();
        tmpData.l2.add(tmp);

        c.add(Calendar.DAY_OF_MONTH, 1);
        d = c.getTime();
        tmp = tmpData.l2.get(1);
        tmp.setDate(d);
        tmp.setGoodsReceiptQuantity(valueOf(0));
        tmp.setGoodsIssueQuantity(valueOf(150));
        tmp.setGoodsResidueQuantity(valueOf(600));
        tmp.setGoodsReceiptWeight(valueOf(0));
        tmp.setGoodsIssueWeight(valueOf(75000));
        tmp.setGoodsResidueWeight(valueOf(300000));

        tmp = new template_layer2();
        tmpData.l2.add(tmp);

        c.add(Calendar.DAY_OF_MONTH, 1);
        d = c.getTime();
        tmp = tmpData.l2.get(2);
        tmp.setDate(d);
        tmp.setGoodsReceiptQuantity(valueOf(0));
        tmp.setGoodsIssueQuantity(valueOf(100));
        tmp.setGoodsResidueQuantity(valueOf(500));
        tmp.setGoodsReceiptWeight(valueOf(0));
        tmp.setGoodsIssueWeight(valueOf(50000));
        tmp.setGoodsResidueWeight(valueOf(250000));

        Collections.sort(tmpData.l2, new template_layer2_comparator());

        for (int i = 0; i < 97; i++) {
            tmpData.l2.add(new template_layer2());
        }

        return tmpData;
    }

    private void DisplaySample() {
        // サンプル用のデータを準備
        template_layer1 sampleData = GetSample();

        // リスト表示
        ListView lvInventoryInquiryPage2Detail = (ListView) findViewById(R.id.lvInventoryInquiryPage2Detail);
        ListAdapter adapter = new ListAdapter(this, sampleData.l2);
        lvInventoryInquiryPage2Detail.setAdapter(adapter);
        lvInventoryInquiryPage2Detail.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // 在庫表示
        TextView txtInventoryInquiryPage2Quantity = findViewById(R.id.txtInventoryInquiryPage2Quantity);
        TextView txtInventoryInquiryPage2Weight = findViewById(R.id.txtInventoryInquiryPage2Weight);

        txtInventoryInquiryPage2Quantity.setText(toFullWidth(String.format("%,d", sampleData.getStockQuantity().intValue())));
        txtInventoryInquiryPage2Weight.setText(toFullWidth(String.format("%,d", sampleData.getStockWeight().intValue())));
    }

    public void btnInventoryInquiryPage2Quantity_Click(View view) {
        DisplaySample();
    }

    //region 重量切替
    public void btnInventoryInquiryPage2Weight_Click(View view) {
        DisplaySample();
    }

    //endregion

    //region 個数切替

    public void btnSort_Click(View view) {
        isAsc = !isAsc;
        DisplaySample();
    }

    //endregion

    private class template_layer1 {

        private final ArrayList<template_layer2> l2 = new ArrayList<template_layer2>();

        private BigDecimal stock_quantity = BigDecimal.ZERO;
        private BigDecimal stock_weight = BigDecimal.ZERO;

        public BigDecimal getStockQuantity() {
            return stock_quantity;
        }

        public void setStockQuantity(BigDecimal setStockQuantity) {
            this.stock_quantity = setStockQuantity;
        }

        public BigDecimal getStockWeight() {
            return stock_weight;
        }

        public void setStockWeight(BigDecimal setStockWeight) {
            this.stock_weight = setStockWeight;
        }
    }

    //endregion

    private class template_layer2 {

        private Date date = null;
        private BigDecimal goods_receipt_quantity = BigDecimal.ZERO;
        private BigDecimal goods_issue_quantity = BigDecimal.ZERO;
        private BigDecimal goods_residue_quantity = BigDecimal.ZERO;

        private BigDecimal goods_receipt_weight = BigDecimal.ZERO;
        private BigDecimal goods_issue_weight = BigDecimal.ZERO;
        private BigDecimal goods_residue_weight = BigDecimal.ZERO;

        private BigDecimal stock_quantity = BigDecimal.ZERO;
        private BigDecimal stock_weight = BigDecimal.ZERO;

        public Date getDate() {
            return date;
        }

        public void setDate(Date setDate) {
            this.date = setDate;
        }

        public BigDecimal getGoodsReceiptQuantity() {
            return goods_receipt_quantity;
        }

        public void setGoodsReceiptQuantity(BigDecimal setGoodsReceiptQuantity) {
            this.goods_receipt_quantity = setGoodsReceiptQuantity;
        }

        public BigDecimal getGoodsIssueQuantity() {
            return goods_issue_quantity;
        }

        public void setGoodsIssueQuantity(BigDecimal setGoodsIssueQuantity) {
            this.goods_issue_quantity = setGoodsIssueQuantity;
        }

        public BigDecimal getGoodsResidueQuantity() {
            return goods_residue_quantity;
        }

        public void setGoodsResidueQuantity(BigDecimal setGoodsResidueQuantity) {
            this.goods_residue_quantity = setGoodsResidueQuantity;
        }

        public BigDecimal getGoodsReceiptWeight() {
            return goods_receipt_weight;
        }

        public void setGoodsReceiptWeight(BigDecimal setGoodsReceiptWeight) {
            this.goods_receipt_weight = setGoodsReceiptWeight;
        }

        public BigDecimal getGoodsIssueWeight() {
            return goods_issue_weight;
        }

        public void setGoodsIssueWeight(BigDecimal setGoodsIssueWeight) {
            this.goods_issue_weight = setGoodsIssueWeight;
        }

        public BigDecimal getGoodsResidueWeight() {
            return goods_residue_weight;
        }

        public void setGoodsResidueWeight(BigDecimal setGoodsResidueWeight) {
            this.goods_residue_weight = setGoodsResidueWeight;
        }

        public BigDecimal getStockQuantity() {
            return stock_quantity;
        }

        public void setStockQuantity(BigDecimal setStockQuantity) {
            this.stock_quantity = setStockQuantity;
        }

        public BigDecimal getStockWeight() {
            return stock_weight;
        }

        public void setStockWeight(BigDecimal setStockWeight) {
            this.stock_weight = setStockWeight;
        }
    }

    public class template_layer2_comparator implements Comparator<template_layer2> {

        @Override
        public int compare(template_layer2 p1, template_layer2 p2) {

            if (isAsc) {
                if (p1.getDate() == null && p2.getDate() == null) {
                    return 0;
                } else if (p1.getDate() == null && p2.getDate() != null) {
                    return 1;
                } else if (p1.getDate() != null && p2.getDate() == null) {
                    return -1;
                }

                return p1.getDate().compareTo(p2.getDate());
            } else {
                if (p2.getDate() == null && p1.getDate() == null) {
                    return 0;
                } else if (p2.getDate() == null && p1.getDate() != null) {
                    return -1;
                } else if (p2.getDate() != null && p1.getDate() == null) {
                    return 1;
                }

                return p2.getDate().compareTo(p1.getDate());
            }
        }
    }

    //region 並び替え

    // LIST表示制御用クラス
    //Ver1からの変更点：extends ArrayAdapter<Data> を extends BaseAdapterに変更
    private class ListAdapter extends BaseAdapter {

        private final ArrayList<template_layer2> list;
        private final LayoutInflater inflater;
        private final Resources r;

        public ListAdapter(Context context, ArrayList<template_layer2> list) {
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
        public template_layer2 getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {

            if (view == null) view = inflater.inflate(R.layout.inventory_inquiry_page2_raw, null);

            final template_layer2 l2 = getItem(position);
            TextView txtInventoryInquiryPage2Date = (TextView) view.findViewById(R.id.txtInventoryInquiryPage2Date);
            TextView txtInventoryInquiryPage2GoodsReceipt = (TextView) view.findViewById(R.id.txtInventoryInquiryPage2GoodsReceipt);
            TextView txtInventoryInquiryPage2GoodsIssue = (TextView) view.findViewById(R.id.txtInventoryInquiryPage2GoodsIssue);
            TextView txtInventoryInquiryPage2GoodsResidue = (TextView) view.findViewById(R.id.txtInventoryInquiryPage2GoodsResidue);
            MaterialButton btnInventoryInquiryPage2Quantity = findViewById(R.id.btnInventoryInquiryPage2Quantity);
            MaterialButton btnInventoryInquiryPage2Weight = findViewById(R.id.btnInventoryInquiryPage2Weight);

            if (l2 != null) {

                if (l2.getDate() == null) {
                    txtInventoryInquiryPage2Date.setText("");
                    txtInventoryInquiryPage2GoodsReceipt.setText("");
                    txtInventoryInquiryPage2GoodsIssue.setText("");
                    txtInventoryInquiryPage2GoodsResidue.setText("");
                    return view;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
                txtInventoryInquiryPage2Date.setText(sdf.format(l2.getDate()));

                if (btnInventoryInquiryPage2Quantity.isChecked() == true) {
                    if (l2.getGoodsReceiptQuantity().compareTo(BigDecimal.ZERO) == 1) {
                        txtInventoryInquiryPage2GoodsReceipt.setText(String.format("%,d", l2.getGoodsReceiptQuantity().intValue()));
                    }
                    if (l2.getGoodsIssueQuantity().compareTo(BigDecimal.ZERO) == 1) {
                        txtInventoryInquiryPage2GoodsIssue.setText(String.format("%,d", l2.getGoodsIssueQuantity().intValue()));
                    }
                    if (l2.getGoodsResidueQuantity().compareTo(BigDecimal.ZERO) == 1) {
                        txtInventoryInquiryPage2GoodsResidue.setText(String.format("%,d", l2.getGoodsResidueQuantity().intValue()));
                    }
                } else if (btnInventoryInquiryPage2Weight.isChecked() == true) {

                    if (l2.getGoodsReceiptWeight().compareTo(BigDecimal.ZERO) == 1) {
                        txtInventoryInquiryPage2GoodsReceipt.setText(String.format("%,d", l2.getGoodsReceiptWeight().intValue()));
                    }
                    if (l2.getGoodsIssueWeight().compareTo(BigDecimal.ZERO) == 1) {
                        txtInventoryInquiryPage2GoodsIssue.setText(String.format("%,d", l2.getGoodsIssueWeight().intValue()));
                    }
                    if (l2.getGoodsResidueWeight().compareTo(BigDecimal.ZERO) == 1) {
                        txtInventoryInquiryPage2GoodsResidue.setText(String.format("%,d", l2.getGoodsResidueWeight().intValue()));
                    }
                } else {
                    txtInventoryInquiryPage2GoodsReceipt.setText("");
                    txtInventoryInquiryPage2GoodsIssue.setText("");
                    txtInventoryInquiryPage2GoodsResidue.setText("");
                }
            } else {
                txtInventoryInquiryPage2Date.setText("");
                txtInventoryInquiryPage2GoodsReceipt.setText("");
                txtInventoryInquiryPage2GoodsIssue.setText("");
                txtInventoryInquiryPage2GoodsResidue.setText("");
            }

            return view;
        }
    }

    //endregion
}
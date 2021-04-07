package com.example.sozax.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.sozax.R;
import com.example.sozax.bl.goods_issue.GoodsIssueSlip;
import com.example.sozax.common.CommonActivity;

import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MenuActivity extends CommonActivity {

    //region Create

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // ログイン情報を表示
//        TextView txtLoginInfo = findViewById(R.id.txtLoginInfo);
//        txtLoginInfo.setText(logininfo.getOfficeInfo().getName() + " " + logininfo.getRepresentativeInfo().getName() + "\n" + logininfo.getWarehouseInfo().getName());
//
//        TextView lblStatus = findViewById(R.id.lblDate);
//        SimpleDateFormat sdf = new SimpleDateFormat("M/dd (E)", DateFormatSymbols.getInstance(Locale.JAPAN));
//        lblStatus.setText(sdf.format(logininfo.getWorkingday()));

        // デモデータをセット
        SetDemoData();

        // アプリ終了
        findViewById(R.id.btnExit).setOnClickListener(new btnExit_Click(MenuActivity.this));
    }

    //endregion

    //region 出庫ボタンをクリック

    private static final int REQUEST_CODE = 1;

    public void btnGoodsIssue_Click(View view) {
        Intent intent = new Intent(getApplication(), GoodsIssuePage1Activity.class);
        intent.putExtra("LOGININFO", logininfo);
        intent.putExtra("DEMODATA",demoData);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE:

                demoData = (ArrayList<GoodsIssueSlip>) data.getSerializableExtra("DEMODATA");

                break;
            default:
                break;
        }
    }

    //endregion

    //region 出庫一覧ボタンをクリック

    public void btnGoodsIssueList_Click(View view) {
        Intent intent = new Intent(getApplication(), GoodsIssueListActivity.class);
        intent.putExtra("LOGININFO", logininfo);
        intent.putExtra("DEMODATA",demoData);
        startActivityForResult(intent, REQUEST_CODE);
    }

    //endregion

    //region 在庫照会ボタンをクリック

    public void btnInventoryInquiry_Click(View view) {
        Intent intent = new Intent(getApplication(), InventoryInquiryPage1Activity.class);
        intent.putExtra("LOGININFO", logininfo);
        startActivity(intent);
    }

    //endregion

    //region 日付変更

    public void btnDateChange_Click(View view) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, DateSetListener, year, month, day);
        datePickerDialog.show();
    }

    // 日付設定時のリスナ作成
    DatePickerDialog.OnDateSetListener DateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(android.widget.DatePicker datePicker, int year,
                              int monthOfYear, int dayOfMonth) {

//            logininfo.setWorkingday(new Date(year, monthOfYear, dayOfMonth));
//            TextView lblStatus = findViewById(R.id.lblDate);
//            SimpleDateFormat sdf = new SimpleDateFormat("M/dd (E)", DateFormatSymbols.getInstance(Locale.JAPAN));
//            lblStatus.setText(sdf.format(logininfo.getWorkingday()));
        }
    };

    //endregion

    //region ログイン

    public void btnLogin_Click(View view) {

    }

    //endregion

    //region 設定

    public void btnConfig_Click(View view) {
    }

    //endregion

    //region デモデータ

    private ArrayList<GoodsIssueSlip> demoData = null;

    private void SetDemoData()
    {
        demoData = new ArrayList<GoodsIssueSlip>();

        // その１
        GoodsIssueSlip tmp1 = new GoodsIssueSlip();
        tmp1.setSlipNo("20210115186");
        tmp1.setNinushiName("シーアイマテックス㈱");
        tmp1.setNiwatashiName("シーアイマテックス㈱");
        tmp1.setProductName("ＮＳ２０８Ｎａ");
        tmp1.setNisugataName("バラ");
        tmp1.setNijirushiName("");
        tmp1.setKikaku("中国");
        tmp1.setFuneName("CHANCE STAR");
        tmp1.setQuantity(BigDecimal.valueOf(0));
        tmp1.setWeight(BigDecimal.valueOf(130));
        tmp1.setProgressState(GoodsIssueSlip.ProgressStateEnum.未着手);
        demoData.add(tmp1);

        // その２
        GoodsIssueSlip tmp2 = new GoodsIssueSlip();
        tmp2.setSlipNo("20210212249");
        tmp2.setNinushiName("三井物産アグロビジネス㈱札幌");
        tmp2.setNiwatashiName("㈱扶相");
        tmp2.setProductName("ＰＫ化成肥料　20KG");
        tmp2.setNisugataName("ポリ袋");
        tmp2.setNijirushiName("ﾕﾆﾊﾞｰｻﾙ66");
        tmp2.setKikaku("66号");
        tmp2.setFuneName("");
        tmp2.setQuantity(BigDecimal.valueOf(1000));
        tmp2.setWeight(BigDecimal.valueOf(20000));
        tmp2.setProgressState(GoodsIssueSlip.ProgressStateEnum.未着手);
        demoData.add(tmp2);

        // その３
        GoodsIssueSlip tmp3 = new GoodsIssueSlip();
        tmp3.setSlipNo("20210215217");
        tmp3.setNinushiName("三菱商事アグリサービス㈱");
        tmp3.setNiwatashiName("㈱サンキョウ");
        tmp3.setProductName("硫酸加里　1,000KG");
        tmp3.setNisugataName("ＴＢ");
        tmp3.setNijirushiName("");
        tmp3.setKikaku("粒 台湾");
        tmp3.setFuneName("HAI XU");
        tmp3.setQuantity(BigDecimal.valueOf(20));
        tmp3.setWeight(BigDecimal.valueOf(20000));
        tmp3.setProgressState(GoodsIssueSlip.ProgressStateEnum.未着手);
        demoData.add(tmp3);
    }

    //endregion

    //region DENSO固有ボタンの設定

    @Override
    public void onKeyRemapCreated() {
    }

    public void btnReLogin_Click(View view) {
        finish();
    }

    //endregion
}
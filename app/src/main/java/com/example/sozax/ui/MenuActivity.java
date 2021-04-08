package com.example.sozax.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.sozax.R;
import com.example.sozax.bl.models.login_info.LoginInfoModel;
import com.example.sozax.common.CommonActivity;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MenuActivity extends CommonActivity {

    //region インスタンス変数

    //endregion

    //region 初回起動

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // アプリ終了イベントを追加
        findViewById(R.id.btnExit).setOnClickListener(new btnExit_Click(MenuActivity.this));

        // ログイン情報を表示
        TextView txtLoginInfo = findViewById(R.id.txtLoginInfo);
        txtLoginInfo.setText(loginInfo.Tensyonm + "　" + loginInfo.Sgytantonm + "\n" + loginInfo.Soukonm);

        // 作業日を表示
        TextView lblSgydate = findViewById(R.id.lblSgydate);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("M/d(E)", DateFormatSymbols.getInstance(Locale.JAPAN));
        lblSgydate.setText(sdf.format(loginInfo.Sgydate));
    }

    //endregion

    //region アプリ終了アイコンをクリック

    // CommonActivity内に処理を記述済み

    //endregion

    //region 再ログインアイコンをクリック

    public void btnReLogin_Click(View view) {
        Intent intent = new Intent(getApplication(), LoginActivity.class);
        intent.putExtra("LoginInfo", loginInfo);
        intent.putExtra("PreviousActivityName", this.getComponentName().getClassName());
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // ログイン情報を反映
            loginInfo = (LoginInfoModel) data.getSerializableExtra("LoginInfo");

            // ログイン情報を表示
            TextView txtLoginInfo = findViewById(R.id.txtLoginInfo);
            txtLoginInfo.setText(loginInfo.Tensyonm + "　" + loginInfo.Sgytantonm + "\n" + loginInfo.Soukonm);

        }
    }

    //endregion

    //region 作業日変更アイコンをクリック

    public void btnSgydateChange_Click(View view) {

        final Calendar c = Calendar.getInstance();
        c.setTime(loginInfo.Sgydate);

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

            // ログイン情報に作業日をセット
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            loginInfo.Sgydate = calendar.getTime();

            // ログイン情報を書き込み
            SharedPreferences preferences = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putLong("Sgydate", loginInfo.Sgydate.getTime());

            // 作業日を再表示
            TextView lblSgydate = findViewById(R.id.lblSgydate);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("M/d(E)", DateFormatSymbols.getInstance(Locale.JAPAN));
            lblSgydate.setText(sdf.format(loginInfo.Sgydate));
        }
    };


    //endregion

    //region 出庫ボタンをクリック

    public void btnGoodsIssue_Click(View view) {
        Intent intent = new Intent(getApplication(), GoodsIssuePage1Activity.class);
        intent.putExtra("LoginInfo", loginInfo);
        startActivity(intent);
    }

    //endregion

    //region 出庫一覧ボタンをクリック

    public void btnGoodsIssueList_Click(View view) {
        Intent intent = new Intent(getApplication(), GoodsIssueListActivity.class);
        intent.putExtra("LoginInfo", loginInfo);
        startActivity(intent);
    }

    //endregion

    //region 在庫照会ボタンをクリック

    public void btnInventoryInquiry_Click(View view) {
        Intent intent = new Intent(getApplication(), InventoryInquiryPage1Activity.class);
        intent.putExtra("LoginInfo", loginInfo);
        startActivity(intent);
    }

    //endregion

    //region DENSO固有ボタンの設定

    @Override
    public void onKeyRemapCreated() {
    }

    //endregion
}
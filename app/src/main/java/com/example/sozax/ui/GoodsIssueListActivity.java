package com.example.sozax.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import com.example.sozax.bl.controllers.SyukoDenpyoController;
import com.example.sozax.bl.controllers.SyukoSagyoController;
import com.example.sozax.bl.models.syuko_denpyo.SyukoDenpyoConditionModel;
import com.example.sozax.bl.models.syuko_denpyo.SyukoDenpyoModel;
import com.example.sozax.bl.models.syuko_denpyo.SyukoDenpyosModel;
import com.example.sozax.bl.models.syuko_sagyo.SyukoSagyoModel;
import com.example.sozax.common.CommonActivity;
import com.example.sozax.common.CommonFunction;
import com.example.sozax.common.EnumClass;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.example.sozax.common.CommonFunction.toFullWidth;

public class GoodsIssueListActivity extends CommonActivity {

    // region インスタンス変数

    private ArrayList<SyukoDenpyoModel> dispDatas = null;

    // チェック制御用
    private Map<Long, Boolean> syukoChk;

    // endregion

    //region 初回起動

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_issue_list);

        // アプリ終了
        findViewById(R.id.btnExit).setOnClickListener(new btnExit_Click(GoodsIssueListActivity.this));
        // 出庫
        findViewById(R.id.btnGoodsIssue).setOnClickListener(new btnGoodsIssue_Click());
        // 行選択
        ((ListView) findViewById(R.id.lvGoodsIssueSlipList)).setOnItemClickListener(new lvGoodsIssueSlipList_Click());
        // ログイン情報長押し
        findViewById(R.id.clLoginInfo).setOnLongClickListener(new clLoginInfo_LongClick());

        // ログイン情報を表示
        DisplayLoginInfo();

        // 作業中の出庫伝票リストの取得条件を作成
        SyukoDenpyoConditionModel syukoDenpyoConditionModel = new SyukoDenpyoConditionModel();
        syukoDenpyoConditionModel.Kaicd = loginInfo.Kaicd;
        syukoDenpyoConditionModel.Soukocd = loginInfo.Soukocd;
        syukoDenpyoConditionModel.Sagyodate = loginInfo.Sgydate;

        // 取得処理
        new GetSyukoDenpyosTask().execute(syukoDenpyoConditionModel);
    }

    //endregion

    //region 出庫ボタン押下時

    class btnGoodsIssue_Click implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            ListView lvGoodsIssueSlipList = findViewById(R.id.lvGoodsIssueSlipList);

            // チェックがついている伝票を取得し、出庫作業データ作成
            ArrayList<SyukoDenpyoModel> selectDatas = new ArrayList<SyukoDenpyoModel>();
            // ListViewの行数
            int count = lvGoodsIssueSlipList.getCount();
            for (int i = 0; i < count; i++) {
                CheckBox checkBox = lvGoodsIssueSlipList.getChildAt(i).findViewById(R.id.chkGoodsIssueListSelect);
                if (checkBox.isChecked()) {

                    SyukoSagyoModel syukoSagyoModel = new SyukoSagyoModel();
                    TextView txtSyukono = lvGoodsIssueSlipList.getChildAt(i).findViewById(R.id.txtGoodsIssueListSlipNo);
                    syukoSagyoModel.Sgysyukono = Long.parseLong(txtSyukono.getText().toString());
                    syukoSagyoModel.Kaicd = loginInfo.Kaicd;
                    syukoSagyoModel.Sgytencd = loginInfo.Tensyocd;
                    syukoSagyoModel.Sgytantocd = loginInfo.Sgytantocd;
                    syukoSagyoModel.Sgysoukocd = loginInfo.Soukocd;
                    syukoSagyoModel.Sgydate = loginInfo.Sgydate;

                    SyukoDenpyoModel tmp = new SyukoDenpyoModel();
                    tmp.Syukosgyjokyo = syukoSagyoModel;
                    selectDatas.add(tmp);
                }
            }

            // チェックされた明細がある場合
            if (selectDatas.size() > 0) {
                SyukoDenpyosModel postData = new SyukoDenpyosModel();
                postData.SyukoDenpyos = new SyukoDenpyoModel[selectDatas.size()];

                selectDatas.toArray(postData.SyukoDenpyos);

                // 出庫作業に登録
                new PostSyukoSagyosTask().execute(postData);
            } else {

                OutputNoTitleMessage("未着手の出庫伝票が選択されていません。");

            }
        }
    }

    //endregion

    // region 出庫一覧取得
    @SuppressLint("StaticFieldLeak")
    private class GetSyukoDenpyosTask extends SyukoDenpyoController.GetSyukoDenpyosTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // 操作の無効化
            setEnabledOperation(false);
        }

        /**
         * バックグランド処理が完了し、UIスレッドに反映する
         */
        @Override
        protected void onPostExecute(SyukoDenpyosModel _syukoDenpyosModel) {

            try {

                // エラー発生
                if (_syukoDenpyosModel.Is_error) {
                    OutputErrorMessage(java.text.MessageFormat.format(getResources().getString(R.string.goods_issue_list_activity_failed_get_syukosdata_message), _syukoDenpyosModel.Message));
                    return;
                }

                // 該当データなし
                if (_syukoDenpyosModel.SyukoDenpyos == null) {
                    OutputErrorMessage(getResources().getString(R.string.goods_issue_list_activity_nosyukosdata_message));
                    return;
                }

                if (dispDatas == null) {
                    dispDatas = new ArrayList<SyukoDenpyoModel>();
                }

                // 作業中出庫伝票リストに取得した出庫伝票リストを追加
                dispDatas.addAll(Arrays.asList(_syukoDenpyosModel.SyukoDenpyos));

                // データ表示
                DisplayDatas();

            } finally {

                // 操作の有効化
                setEnabledOperation(true);
            }
        }
    }

    // endregion

    // region 出庫作業登録

    @SuppressLint("StaticFieldLeak")
    public class PostSyukoSagyosTask extends SyukoSagyoController.PostSyukoSagyosTask {

        // 登録前処理
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // 操作の無効化
            setEnabledOperation(false);
        }

        // 登録後処理
        @Override
        protected void onPostExecute(SyukoDenpyosModel _syukoDenpyosModel) {

            try {

                // エラー発生
                if (_syukoDenpyosModel.Is_error) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GoodsIssueListActivity.this);
                    builder.setTitle("エラー");
                    builder.setMessage((java.text.MessageFormat.format(getResources().getString(R.string.goods_issue_list_activity_failed_insert_syukosgydata_message), _syukoDenpyosModel.Message)));

                    builder.show();
                    return;
                }

                // 出庫画面に遷移
                CreateIntent();

            } finally {

                // 操作の有効化
                setEnabledOperation(true);

            }
        }
    }

    // endregion

    // region 出庫画面に遷移
    private void CreateIntent() {
        Intent intent = new Intent(getApplication(), GoodsIssuePage1Activity.class);
        intent.putExtra(getResources().getString(R.string.intent_key_login_info), loginInfo);
        startActivity(intent);

        finish();
    }

    // endregion

    //region 出庫伝票一覧を表示

    private void DisplayDatas() {

        // リスト呼出
        ListView lvGoodsIssueSlipList = findViewById(R.id.lvGoodsIssueSlipList);

        // アダプター作成
        SyukoListAdapter adapter = new SyukoListAdapter(this, dispDatas);
        lvGoodsIssueSlipList.setAdapter(adapter);
        lvGoodsIssueSlipList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // 一行を目選択
        lvGoodsIssueSlipList.deferNotifyDataSetChanged();
        lvGoodsIssueSlipList.requestFocusFromTouch();
        lvGoodsIssueSlipList.setItemChecked(0,true);

        // 詳細表示
        if (dispDatas.size() > 0) {
            DisplayDetail(dispDatas.get(0));
        }
    }

    //endregion

    //region 詳細表示

    @SuppressLint("DefaultLocale")
    private void DisplayDetail(SyukoDenpyoModel dispData) {

        TextView txtGoodsIssueSlipListDetailNinushi = findViewById(R.id.txtGoodsIssueSlipListDetailNinushi);
        TextView txtGoodsIssueSlipListDetailNiwatashi = findViewById(R.id.txtGoodsIssueSlipListDetailNiwatashi);
        TextView txtGoodsIssueSlipListDetailProductName = findViewById(R.id.txtGoodsIssueSlipListDetailProductName);
        TextView txtGoodsIssueSlipListQuantity = findViewById(R.id.txtGoodsIssueSlipListQuantity);
        TextView txtGoodsIssueSlipListWeight = findViewById(R.id.txtGoodsIssueSlipListWeight);

        // 荷主名
        txtGoodsIssueSlipListDetailNinushi.setText(dispData.Ninusinm);
        // 荷渡名
        txtGoodsIssueSlipListDetailNiwatashi.setText(dispData.Niwatanm);

        // 品名
        StringBuilder hinmeinm = new StringBuilder(dispData.Hinmeinm);
        if (!dispData.Kikakunaiyo1.isEmpty()) {
            // 規格内容1
            hinmeinm.append(" ").append(dispData.Kikakunaiyo1);
        }
        if (!dispData.Kikakunaiyo2.isEmpty()) {
            // 規格内容2
            hinmeinm.append(" ").append(dispData.Kikakunaiyo2);
        }
        if (!dispData.Kikakunaiyo3.isEmpty()) {
            // 規格内容3
            hinmeinm.append(" ").append(dispData.Kikakunaiyo3);
        }
        if (!dispData.Kikakunaiyo4.isEmpty()) {
            // 規格内容4
            hinmeinm.append(" ").append(dispData.Kikakunaiyo4);
        }
        if (!dispData.Kikakunaiyo5.isEmpty()) {
            // 規格内容5
            hinmeinm.append(" ").append(dispData.Kikakunaiyo5);
        }
        if (!dispData.Kikakunaiyo6.isEmpty()) {
            // 規格内容6
            hinmeinm.append(" ").append(dispData.Kikakunaiyo6);
        }
        if (!dispData.Kikakunaiyo7.isEmpty()) {
            // 規格内容7
            hinmeinm.append(" ").append(dispData.Kikakunaiyo7);
        }
        if (!dispData.Kikakunaiyo8.isEmpty()) {
            // 規格内容8
            hinmeinm.append(" ").append(dispData.Kikakunaiyo8);
        }
        if (!dispData.Kikakunaiyo9.isEmpty()) {
            // 規格内容9
            hinmeinm.append(" ").append(dispData.Kikakunaiyo9);
        }
        if (!dispData.Kikakunaiyo10.isEmpty()) {
            // 規格内容10
            hinmeinm.append(" ").append(dispData.Kikakunaiyo10);
        }
        if (!dispData.Kikakunaiyo11.isEmpty()) {
            // 規格内容11
            hinmeinm.append(" ").append(dispData.Kikakunaiyo11);
        }
        if (!dispData.Kikakunaiyo12.isEmpty()) {
            // 規格内容12
            hinmeinm.append(" ").append(dispData.Kikakunaiyo12);
        }
        if (!dispData.Tanjuryo.equals(BigDecimal.ZERO)) {
            // 単重量
            hinmeinm.append(" ").append(dispData.Tanjuryo);
        }

        // 品名
        txtGoodsIssueSlipListDetailProductName.setText(hinmeinm);
        // 出庫個数
        txtGoodsIssueSlipListQuantity.setText(toFullWidth(String.format("%,d", dispData.Kosuu.intValue())));
        // 出庫重量
        txtGoodsIssueSlipListWeight.setText(toFullWidth(String.format("%,d", CommonFunction.multiplyThousand(dispData.Juryo).intValue())));
    }

    //endregion

    //region 行選択

    class lvGoodsIssueSlipList_Click implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            view.setActivated(true);

            SyukoDenpyoModel syukodenpyo = dispDatas.get(position);

            // 詳細表示
            DisplayDetail(syukodenpyo);
        }
    }

    //endregion

    //region Adapter作成

    private class SyukoListAdapter extends BaseAdapter {

        private final ArrayList<SyukoDenpyoModel> list;
        private final LayoutInflater inflater;

        public SyukoListAdapter(Context context, ArrayList<SyukoDenpyoModel> list) {
            super();
            this.list = list;
            syukoChk = new HashMap<Long, Boolean>();
            for (SyukoDenpyoModel syuko : list) {
                syukoChk.put(syuko.Syukono, false);
            }

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public SyukoDenpyoModel getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public View getView(final int position, View view, ViewGroup parent) {

            final SyukoDenpyoModel datas = getItem(position);

            if (view == null) {
                view = inflater.inflate(R.layout.goods_issue_list_raw, null);
            }

            view.setBackground(getDrawable(R.drawable.list_item_selecter_frostygray));

            TextView txtGoodsIssueListNo = view.findViewById(R.id.txtGoodsIssueListNo);
            TextView txtGoodsIssueListSlipNo = view.findViewById(R.id.txtGoodsIssueListSlipNo);
            TextView txtGoodsIssueListStatus = view.findViewById(R.id.txtGoodsIssueListStatus);
            CheckBox chkGoodsIssueListSelect = view.findViewById(R.id.chkGoodsIssueListSelect);

            chkGoodsIssueListSelect.setOnClickListener(new chkChkBox_Click(datas.Syukono));

            // チェックボックス有効
            chkGoodsIssueListSelect.setEnabled(true);

            // 出庫番号と紐づく場合チェックつける
            //noinspection ConstantConditions
            chkGoodsIssueListSelect.setChecked(syukoChk.get(datas.Syukono));

            // チェックボックスの色リセット
            chkGoodsIssueListSelect.setVisibility(View.VISIBLE);

            // 値を設定
            txtGoodsIssueListNo.setText(String.valueOf(position + 1));
            txtGoodsIssueListSlipNo.setText(String.valueOf(datas.Syukono));

            //　出庫作業が登録されている場合
            if (datas.Syukosgyjokyo != null) {

                // 受領確認の場合
                if (EnumClass.getSgyjokyoKubun(datas.Syukosgyjokyo.Sgyjokyokbn) == EnumClass.SgyjokyoKubun.Juryokakunin) {

                    // 背景色をグレーに設定
                    view.setBackground(getDrawable(R.drawable.list_item_selecter_darkgray));
                }

                //
                // 作業状況区分名表示
                txtGoodsIssueListStatus.setText(EnumClass.getNextSgyjokyoKubunName(datas.Syukosgyjokyo.Sgyjokyokbn));
                // チェックボックスを無効
                chkGoodsIssueListSelect.setEnabled(false);
                // チェックボックスを透明にする
                chkGoodsIssueListSelect.setVisibility(View.INVISIBLE);

            }
            //　出庫作業が登録されていない場合
            else {
                // 作業状況は未着手固定
                EnumClass.SgyjokyoKubun sgyjokyoKubun = EnumClass.getSgyjokyoKubun(EnumClass.SgyjokyoKubun.Michakusyu.getInteger());
                if (sgyjokyoKubun != null) {
                    txtGoodsIssueListStatus.setText(sgyjokyoKubun.getString());
                }
            }

            return view;
        }
    }

    //endregion

    // region チェックボックスクリック時

    class chkChkBox_Click implements View.OnClickListener {

        public Long syukono;

        public chkChkBox_Click(long l) {
            syukono = l;
        }

        @Override
        public void onClick(View view) {
            final boolean checked = ((CheckBox) view).isChecked();

            syukoChk.replace(syukono, checked);
        }
    }

    // endregion

    //region 戻るボタン押下時

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtra(getResources().getString(R.string.intent_key_login_info), loginInfo);
        startActivity(intent);
        finish();
    }

    //endregion
}
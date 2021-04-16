package com.example.sozax.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.densowave.bhtsdk.keyremap.KeyRemapLibrary;
import com.example.sozax.R;
import com.example.sozax.bl.controllers.BarChkKomkController;
import com.example.sozax.bl.controllers.HyojihyoController;
import com.example.sozax.bl.controllers.SyukoSagyoController;
import com.example.sozax.bl.goods_issue.GoodsIssueSlip;
import com.example.sozax.bl.models.bar_chk_komk.BarChkKomkConditionModel;
import com.example.sozax.bl.models.bar_chk_komk.BarChkKomkModel;
import com.example.sozax.bl.models.bar_chk_komk.BarChkKomk_KikakuModel;
import com.example.sozax.bl.models.hyojihyo.HyojihyoConditionModel;
import com.example.sozax.bl.models.hyojihyo.HyojihyoModel;
import com.example.sozax.bl.models.syuko_denpyo.SyukoDenpyoConditionModel;
import com.example.sozax.bl.models.syuko_denpyo.SyukoDenpyoModel;
import com.example.sozax.bl.models.syuko_denpyo.SyukoDenpyosModel;
import com.example.sozax.common.CommonActivity;
import com.example.sozax.common.EnumClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class GoodsIssuePage2Activity extends CommonActivity implements KeyRemapLibrary.KeyRemapListener {

    //region インスタンス変数

    // 作業中の伝票リスト
    private ArrayList<SyukoDenpyoModel> sagyochuSyukoDenpyos = null;

    // 現在作業中の出庫伝票のインデックス
    private int selectedSagyochuSyukoDenpyoIndex = -1;

    // キー割り当てライブラリ(DENSO製)
    public KeyRemapLibrary mKeyRemapLibrary;

    //endregion

    //region 初回起動

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_issue_page2);

        // イベントを追加
        // 前伝票を表示ボタン
        findViewById(R.id.btnPrevSlip).setOnClickListener(new btnPrevSlip_Click());
        // 次伝票を表示ボタン
        findViewById(R.id.btnNextSlip).setOnClickListener(new btnNextSlip_Click());

        // ハードウェアキーのマッピングクラスのインスタンスを生成
        mKeyRemapLibrary = new KeyRemapLibrary();
        mKeyRemapLibrary.createKeyRemap(this, this);

        // 出庫伝票情報を取得
        Intent intent = getIntent();
        sagyochuSyukoDenpyos = (ArrayList<SyukoDenpyoModel>) intent.getSerializableExtra(getResources().getString(R.string.intent_key_sagyochu_syuko_denpyos));
        selectedSagyochuSyukoDenpyoIndex = intent.getIntExtra(getResources().getString(R.string.intent_key_selected_sagyochu_syuko_denpyo_index), -1);

        // 画面全体の表示を更新
        RefreshScreenAll();
    }

    //endregion

    //region 前伝票を表示する

    private class btnPrevSlip_Click implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if(selectedSagyochuSyukoDenpyoIndex == 0)
            {
                return;
            }

            // デクリメント
            selectedSagyochuSyukoDenpyoIndex--;

            // 画面全体を更新する
            RefreshScreenAll();
        }
    }

    //endregion

    //region 次伝票を表示する

    private class btnNextSlip_Click implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if(selectedSagyochuSyukoDenpyoIndex == (sagyochuSyukoDenpyos.size() -1))
            {
                return;
            }

            // インクリメント
            selectedSagyochuSyukoDenpyoIndex--;

            // 画面全体を更新する
            RefreshScreenAll();
        }
    }

    //endregion

    //region TODO QRをスキャン

    //endregion

    //region TODO 進行ボタンをクリック

    //endregion

    //region バーコードチェック項目の取得

    @SuppressLint("StaticFieldLeak")
    public class GetBarChkKomkTask extends BarChkKomkController.GetBarChkKomkTask {

        // 取得前処理
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // タッチ操作を無効化
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            // プログレスバーを表示
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);

        }

        // 取得後処理
        @Override
        protected void onPostExecute(BarChkKomkModel _barChkKomkModel) {

            try {

                // エラー発生
                if (_barChkKomkModel.Is_error) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GoodsIssuePage2Activity.this);
                    String template = "バーコードチェック項目の取得に失敗しました。\r\n{0}";
                    builder.setMessage((java.text.MessageFormat.format(template, _barChkKomkModel.Message)));

                    builder.show();
                    return;
                }

                // 該当データなし
                if (_barChkKomkModel.Hinbuncd == 0 && _barChkKomkModel.Hinsyucd == 0 && _barChkKomkModel.Ninusicd == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GoodsIssuePage2Activity.this);
                    builder.setTitle("エラー");
                    builder.setMessage("該当するバーコードチェック項目がありません");

                    builder.show();
                    return;
                }

                // 取得したバーコードチェック項目を現在作業中の出庫伝票にセット
                sagyochuSyukoDenpyos.get(selectedSagyochuSyukoDenpyoIndex).BarChkKomk = _barChkKomkModel;

                // 画面全体を更新する
                RefreshScreenAll();

            } finally {

                // プログレスバーを非表示
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.INVISIBLE);

                // タッチ操作を有効化
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            }
        }
    }

    //endregion

    //region 表示票データの取得

    @SuppressLint("StaticFieldLeak")
    public class GetHyojihyoTask extends HyojihyoController.GetHyojihyoTask {

        // 取得前処理
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // タッチ操作を無効化
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            // プログレスバーを表示
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);

        }

        // 取得後処理
        @Override
        protected void onPostExecute(HyojihyoModel _hyojihyoModel) {

            try {

                // エラー発生
                if (_hyojihyoModel.Is_error) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GoodsIssuePage2Activity.this);
                    String template = "表示票データの取得に失敗しました。\r\n{0}";
                    builder.setMessage((java.text.MessageFormat.format(template, _hyojihyoModel.Message)));

                    builder.show();
                    return;
                }

                // 該当データなし
                if (_hyojihyoModel.Syukeicd == 0L) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GoodsIssuePage2Activity.this);
                    builder.setTitle("エラー");
                    builder.setMessage("該当する表示票データがありません");

                    builder.show();
                    return;
                }

                // 取得した表示票データを現在作業中の出庫伝票にセット
                sagyochuSyukoDenpyos.get(selectedSagyochuSyukoDenpyoIndex).Syukosgyjokyo.HyojiHyo = _hyojihyoModel;

                // 画面全体を更新する
                RefreshScreenAll();

            } finally {

                // プログレスバーを非表示
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.INVISIBLE);

                // タッチ操作を有効化
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            }
        }
    }

    //endregion

    //region 出庫作業の更新

    @SuppressLint("StaticFieldLeak")
    public class PutSyukoSagyosTask extends SyukoSagyoController.PutSyukoSagyosTask {

        // 登録前処理
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // タッチ操作を無効化
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            // プログレスバーを表示
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);

        }

        // 登録後処理
        @Override
        protected void onPostExecute(SyukoDenpyosModel _syukoDenpyosModel) {

            try {

                // エラー発生
                if (_syukoDenpyosModel.Is_error) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GoodsIssuePage2Activity.this);
                    builder.setTitle("エラー");
                    String template = "出庫作業の更新に失敗しました。\r\n{0}";
                    builder.setMessage((java.text.MessageFormat.format(template, _syukoDenpyosModel.Message)));

                    builder.show();
                    return;
                }

                // 更新したデータに上書き
                for (SyukoDenpyoModel _syukoDenpyoModel: _syukoDenpyosModel.SyukoDenpyos) {

                    for (SyukoDenpyoModel sagyochuSyukoDenpyo : sagyochuSyukoDenpyos) {

                        if(sagyochuSyukoDenpyo.Syukono == _syukoDenpyoModel.Syukono)
                        {
                            sagyochuSyukoDenpyo = _syukoDenpyoModel;
                            break;
                        }
                    }
                }

                // 画面全体を更新する
                RefreshScreenAll();

            } finally {

                // プログレスバーを非表示
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.INVISIBLE);

                // タッチ操作を有効化
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            }
        }

    }

    //endregion

    //region 画面全体の表示を更新

    ListData currentListData = null;

    private void RefreshScreenAll() {

        // 表示データを取得
        SyukoDenpyoModel syukoDenpyo = sagyochuSyukoDenpyos.get(selectedSagyochuSyukoDenpyoIndex);

        if (syukoDenpyo.BarChkKomk == null) {

            // バーコードチェック項目が未取得の場合、取得する
            BarChkKomkConditionModel barChkKomkConditionModel = new BarChkKomkConditionModel();
            barChkKomkConditionModel.Hinbuncd = syukoDenpyo.Hinbuncd;
            barChkKomkConditionModel.Hinsyucd = syukoDenpyo.Hinsyucd;
            barChkKomkConditionModel.Ninusicd = syukoDenpyo.Ninusicd;

            new GetBarChkKomkTask().execute(barChkKomkConditionModel);
            return;
        }

        if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L && syukoDenpyo.Syukosgyjokyo.HyojiHyo == null) {

            // 集計コードが設定済みだが、表示票データが未取得の場合、取得する
            HyojihyoConditionModel hyojihyoConditionModel = new HyojihyoConditionModel();
            hyojihyoConditionModel.Kaicd = loginInfo.Kaicd;
            hyojihyoConditionModel.Soukocd = loginInfo.Soukocd;
            hyojihyoConditionModel.Syukeicd = syukoDenpyo.Syukosgyjokyo.Syukeicd;

            new GetHyojihyoTask().execute(hyojihyoConditionModel);
            return;
        }

        // 表示する出庫伝票が変わった時のみ、リストビューの中身を更新する
        if (currentListData == null || currentListData.Syukono != syukoDenpyo.Syukono) {

            // リストビューのデータを作成
            currentListData = new ListData();
            currentListData.Syukono = syukoDenpyo.Syukono;
            currentListData.ListRowDatas = CreateListRowDatas(syukoDenpyo);

            // リストビューのアダプターを作成
            UniqueAdapter uniqueAdapter = new UniqueAdapter(this, currentListData);

            // アダプターをセット
            ListView lvGoodsIssueProductInformation = findViewById(R.id.lvGoodsIssueProductInformation);
            lvGoodsIssueProductInformation.setAdapter(uniqueAdapter);

        }

        // 出庫番号とページ数
        TextView txtSlipInfo = findViewById(R.id.txtSlipInfo);
        String templateSlipInfo = "({0}/{1})\r\nNo.{2}";
        txtSlipInfo.setText(java.text.MessageFormat.format(templateSlipInfo,(selectedSagyochuSyukoDenpyoIndex + 1),sagyochuSyukoDenpyos.size(),String.format("00000000000",syukoDenpyo.Syukono)));

        // 進行状況
        TextView  txtProgressPhase2 = findViewById(R.id.txtProgressPhase2);
        TextView  txtProgressPhase3 = findViewById(R.id.txtProgressPhase3);
        TextView  txtProgressPhase4 = findViewById(R.id.txtProgressPhase4);
        // ガイダンス
        TextView  txtGoodsIssuePage2Guidance = findViewById(R.id.txtGoodsIssuePage2Guidance);
        // 進行ボタン
        Button btnGoodsIssuePage2Proceed = findViewById(R.id.btnGoodsIssuePage2Proceed);

        switch (EnumClass.getSgyjokyoKubun(syukoDenpyo.Syukosgyjokyo.Sgyjokyokbn))
        {
            case Uketuke:

                txtProgressPhase2.setBackgroundColor(getColor(R.color.signalred));
                txtProgressPhase3.setBackgroundColor(getColor(R.color.white));
                txtProgressPhase4.setBackgroundColor(getColor(R.color.white));

                txtProgressPhase2.setTextColor(getColor(R.color.white));
                txtProgressPhase3.setTextColor(getColor(R.color.black));
                txtProgressPhase4.setTextColor(getColor(R.color.black));

                txtGoodsIssuePage2Guidance.setText(getString(R.string.text_goods_issue_guidance_phase2));

                boolean existsNG = false;
                boolean existsUNCHECK = false;
                for (ListRowData listRowData:currentListData.ListRowDatas)
                {
                    if(listRowData.Checkresult == EnumClass.CheckKubun.NG)
                    {
                        existsNG = true;
                        break;
                    }else if(listRowData.Checkresult == EnumClass.CheckKubun.UNCHECK)
                    {
                        existsUNCHECK = true;
                        break;
                    }
                }

                if(existsNG)
                {
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
                }else if(existsUNCHECK)
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
                else
                {
                    SpannableStringBuilder sb = new SpannableStringBuilder();
                    sb.append("出庫指示");
                    int start = sb.length();
                    sb.append("\n(出庫指示後に押して下さい)");
                    sb.setSpan(new RelativeSizeSpan(0.5f), start, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    btnGoodsIssuePage2Proceed.setText(sb);
                    btnGoodsIssuePage2Proceed.setBackgroundColor(getColor(R.color.coral));
                    btnGoodsIssuePage2Proceed.setEnabled(true);
                }



                break;

            case Zaikokakunin:

                txtProgressPhase2.setBackgroundColor(getColor(R.color.signalred));
                txtProgressPhase3.setBackgroundColor(getColor(R.color.signalred));
                txtProgressPhase4.setBackgroundColor(getColor(R.color.white));

                txtProgressPhase2.setTextColor(getColor(R.color.white));
                txtProgressPhase3.setTextColor(getColor(R.color.white));
                txtProgressPhase4.setTextColor(getColor(R.color.black));

                txtGoodsIssuePage2Guidance.setText(getString(R.string.text_goods_issue_guidance_phase3));

                SpannableStringBuilder sb = new SpannableStringBuilder();
                sb.append("出庫完了");
                int start = sb.length();
                sb.append("\n(出庫作業完了後に押して下さい)");
                sb.setSpan(new RelativeSizeSpan(0.5f), start, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                btnGoodsIssuePage2Proceed.setText(sb);
                btnGoodsIssuePage2Proceed.setBackgroundColor(getColor(R.color.coral));
                btnGoodsIssuePage2Proceed.setEnabled(true);

                break;

            case Syukosagyo:

                txtProgressPhase2.setBackgroundColor(getColor(R.color.signalred));
                txtProgressPhase3.setBackgroundColor(getColor(R.color.signalred));
                txtProgressPhase4.setBackgroundColor(getColor(R.color.signalred));

                txtProgressPhase2.setTextColor(getColor(R.color.white));
                txtProgressPhase3.setTextColor(getColor(R.color.white));
                txtProgressPhase4.setTextColor(getColor(R.color.white));

                txtGoodsIssuePage2Guidance.setText(getString(R.string.text_goods_issue_guidance_phase4));

                sb = new SpannableStringBuilder();
                sb.append("受領確認");
                start = sb.length();
                sb.append("\n(上記出庫品を正に受領いたしました)");
                sb.setSpan(new RelativeSizeSpan(0.5f), start, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                btnGoodsIssuePage2Proceed.setText(sb);
                btnGoodsIssuePage2Proceed.setBackgroundColor(getColor(R.color.signalred));
                btnGoodsIssuePage2Proceed.setEnabled(true);

                break;
        }
    }

    //endregion

    //region TODO ハードキークリック


    // F2押下中フラグ
    private boolean isF2Down = false;
    // F3押下中フラグ
    private boolean isF3Down = false;

    final String[] items = {"目視で間違いないことを確認しました"};
    final boolean[] checkedItems = {false};

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {

        GoodsIssueSlip goodsIssueSlip = new GoodsIssueSlip();

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

        } else if (e.getKeyCode() == KeyEvent.KEYCODE_F6) {

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

        } else if (e.getKeyCode() == KeyEvent.KEYCODE_F2) {

            if (e.getAction() == KeyEvent.ACTION_DOWN) {
                isF2Down = true;
            } else if (e.getAction() == KeyEvent.ACTION_UP) {
                isF2Down = false;
            }
        } else if (e.getKeyCode() == KeyEvent.KEYCODE_F3) {

            if (e.getAction() == KeyEvent.ACTION_DOWN) {
                isF3Down = true;
            } else if (e.getAction() == KeyEvent.ACTION_UP) {
                isF3Down = false;
            }
        }

        if (isF2Down == true && isF3Down == true) {
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
            if (goodsIssueSlip.getNisugataName().isEmpty() == false && goodsIssueSlip.getNisugataCompareStateEnum() == GoodsIssueSlip.CompareStateEnum.NG) {
                existsNG = true;
            }
            // 荷印名
            if (goodsIssueSlip.getNijirushiName().isEmpty() == false && goodsIssueSlip.getNijirushiCompareStateEnum() == GoodsIssueSlip.CompareStateEnum.NG) {
                existsNG = true;
            }
            // 規格
            if (goodsIssueSlip.getKikaku().isEmpty() == false && goodsIssueSlip.getKikakuCompareStateEnum() == GoodsIssueSlip.CompareStateEnum.NG) {
                existsNG = true;
            }
            // 船名
            if (goodsIssueSlip.getFuneName().isEmpty() == false && goodsIssueSlip.getFuneCompareStateEnum() == GoodsIssueSlip.CompareStateEnum.NG) {
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
            if (goodsIssueSlip.getNisugataName().isEmpty() == false && goodsIssueSlip.getNisugataCompareStateEnum() != GoodsIssueSlip.CompareStateEnum.OK) {
                allOK = false;
            }
            // 荷印名
            if (goodsIssueSlip.getNijirushiName().isEmpty() == false && goodsIssueSlip.getNijirushiCompareStateEnum() != GoodsIssueSlip.CompareStateEnum.OK) {
                allOK = false;
            }
            // 規格
            if (goodsIssueSlip.getKikaku().isEmpty() == false && goodsIssueSlip.getKikakuCompareStateEnum() != GoodsIssueSlip.CompareStateEnum.OK) {
                allOK = false;
            }
            // 船名
            if (goodsIssueSlip.getFuneName().isEmpty() == false && goodsIssueSlip.getFuneCompareStateEnum() != GoodsIssueSlip.CompareStateEnum.OK) {
                allOK = false;
            }

            if (allOK == false) {
                // F2&F3押下中であるなら、
                AlertDialog.Builder builder = new AlertDialog.Builder(GoodsIssuePage2Activity.this);

                TextView msgTxt = new TextView(this);
                msgTxt.setTextSize((float) 14.0);
                msgTxt.setTextColor(getColor(R.color.black));
                msgTxt.setPadding(20, 20, 0, 0);

                if (existsNG == true) {
                    msgTxt.setText("NGが含まれていますが、よろしいですか？");
                } else {
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

//                                if (checkedItems[0] == false) {
//                                    return;
//                                }
//
//                                // 更新
//                                UpdateProgressState();
//
//                                if (displayData.size() > 0) {
//                                    // 伝票データを再取得
//                                    GoodsIssueSlip goodsIssueSlip = displayData.get(CurrentSlipIndex);
//
//                                    // 画面をリフレッシュ
//                                    Refresh(goodsIssueSlip);
//                                }
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

    //region 独自のAdapter

    private class UniqueAdapter extends BaseAdapter {

        private final ListData listData;
        private final LayoutInflater layoutInflater;

        public UniqueAdapter(Context _context, ListData _listData) {
            super();
            listData = _listData;
            layoutInflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return listData.ListRowDatas.size();
        }

        @Override
        public ListRowData getItem(int position) {
            return listData.ListRowDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {

            if (view == null) {
                view = layoutInflater.inflate(R.layout.goods_issue_page2_raw, null);
            }

            // 表示する行データを取得
            final ListRowData dispData = getItem(position);

            // 表示するコンポーネントを取得
            TextView txtGoodsIssuePage2ProductInformation = view.findViewById(R.id.txtGoodsIssuePage2ProductInformation);
            TextView txtGoodsIssuePage2Status = view.findViewById(R.id.txtGoodsIssuePage2Status);

            txtGoodsIssuePage2ProductInformation.setText(dispData.Syukodenpyo_dispvalue);
            txtGoodsIssuePage2Status.setText(dispData.Checkresult.getString());

            switch (dispData.Checkresult) {

                case OK:

                    // OK の場合、文字色を緑色に変更
                    txtGoodsIssuePage2Status.setTextColor(getColor(R.color.palegreen));

                    break;
                case NG:

                    // NG の場合、文字色を赤色に変更
                    txtGoodsIssuePage2Status.setTextColor(getColor(R.color.signalred));

                    break;

            }

            return view;
        }
    }

    // リストビューのデータクラス
    private class ListData {

        public long Syukono = 0L;
        public ArrayList<ListRowData> ListRowDatas = new ArrayList<ListRowData>();

    }

    //　リストビューの行データクラス
    private class ListRowData {
        // 項目名
        public String Komokname = "";
        // 出庫伝票の表示値
        public String Syukodenpyo_dispvalue = "";
        // 表示票の表示値
        public String Hyojihyo_dispvalue = "";
        // チェック結果
        public EnumClass.CheckKubun Checkresult = EnumClass.CheckKubun.UNCHECK;
    }

    // リストビューの行データを作成
    private ArrayList<ListRowData> CreateListRowDatas(SyukoDenpyoModel syukoDenpyo) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");

        // バーコードチェック項目
        BarChkKomkModel barChkKomk = syukoDenpyo.BarChkKomk;
        // 表示票データ
        HyojihyoModel hyojihyo = syukoDenpyo.Syukosgyjokyo.HyojiHyo;

        ArrayList<ListRowData> ret = new ArrayList<ListRowData>();

        // 品種
        if (barChkKomk.Is_hinsyucd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "品種";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Hinsyunm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Hinsyunm;
                listRowData.Checkresult = syukoDenpyo.Hinsyucd == hyojihyo.Hinsyucd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // 品名
        if (barChkKomk.Is_hinmeicd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "品名";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Hinmeinm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Hinmeinm;
                listRowData.Checkresult = syukoDenpyo.Hinmeicd == hyojihyo.Hinmeicd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // 荷姿
        if (barChkKomk.Is_nisucd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "荷姿";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Nisunm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Nisunm;
                listRowData.Checkresult = syukoDenpyo.Nisucd == hyojihyo.Nisucd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // 荷印
        if (barChkKomk.Is_nijicd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "荷印";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Nijinm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Nijinm;
                listRowData.Checkresult = syukoDenpyo.Nijicd == hyojihyo.Nijicd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            } else

                ret.add(listRowData);
        }
        // 単位
        if (barChkKomk.Is_tanicd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "単位";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Taninm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Taninm;
                listRowData.Checkresult = syukoDenpyo.Tanicd == hyojihyo.Tanicd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // 倉庫
        if (barChkKomk.Is_soukocd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "倉庫";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Soukonm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Soukonm;
                listRowData.Checkresult = syukoDenpyo.Soukocd == hyojihyo.Soukocd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // エリア名
        if (barChkKomk.Is_eriacd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "エリア";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Erianm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Erianm;
                listRowData.Checkresult = syukoDenpyo.Eriacd == hyojihyo.Eriacd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // 船舶動静No
        if (barChkKomk.Is_sdosecd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "船舶動静No";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Funenm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Funenm;
                listRowData.Checkresult = syukoDenpyo.Sdosecd == hyojihyo.Sdosecd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // TODO 荷主
        if (barChkKomk.Is_ninusicd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "荷主";
            listRowData.Syukodenpyo_dispvalue = "TODO";
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = "TODO";
                listRowData.Checkresult = syukoDenpyo.Ninusicd == 0 ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // 貨物区分
        if (barChkKomk.Is_kamkbn == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "貨物区分";
            if (syukoDenpyo.Kamokbn > 0) {
                listRowData.Syukodenpyo_dispvalue = EnumClass.getKamotuKubun(syukoDenpyo.Kamokbn).getString();
            }
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                if (hyojihyo.Kamokbn > 0) {
                    listRowData.Hyojihyo_dispvalue = EnumClass.getKamotuKubun(hyojihyo.Kamokbn).getString();
                }

                listRowData.Checkresult = syukoDenpyo.Sdosecd == hyojihyo.Sdosecd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // 輸入年月
        if (barChkKomk.Is_yunyudate == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "輸入年月";
            listRowData.Syukodenpyo_dispvalue = simpleDateFormat.format(syukoDenpyo.Yunyudate);
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = simpleDateFormat.format(hyojihyo.Yunyudate);
                listRowData.Checkresult = syukoDenpyo.Yunyudate == hyojihyo.Yunyudate ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // 荷造作業区分
        if (barChkKomk.Is_niduksgyokbn == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "荷造作業区分";
            if (syukoDenpyo.Niduksgyokbn > 0) {
                listRowData.Syukodenpyo_dispvalue = EnumClass.getNidukuriKubun(syukoDenpyo.Niduksgyokbn).getString();
            }

            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                if (hyojihyo.Niduksgyokbn > 0) {
                    listRowData.Hyojihyo_dispvalue = EnumClass.getNidukuriKubun(hyojihyo.Niduksgyokbn).getString();
                }

                listRowData.Checkresult = syukoDenpyo.Niduksgyokbn == hyojihyo.Niduksgyokbn ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // 入庫手段区分
        if (barChkKomk.Is_nyukosyudankbn == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "入庫手段区分";
            if (syukoDenpyo.Nyukosyudankbn > 0) {
                listRowData.Syukodenpyo_dispvalue = EnumClass.getNyukosyudanKubun(syukoDenpyo.Nyukosyudankbn).getString();
            }
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                if (hyojihyo.Nyukosyudankbn > 0) {
                    listRowData.Hyojihyo_dispvalue = EnumClass.getNyukosyudanKubun(hyojihyo.Nyukosyudankbn).getString();
                }

                listRowData.Checkresult = syukoDenpyo.Nyukosyudankbn == hyojihyo.Nyukosyudankbn ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // 初期入庫日
        if (barChkKomk.Is_nyukodate == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "初期入庫日";
            listRowData.Syukodenpyo_dispvalue = simpleDateFormat.format(syukoDenpyo.Nyukodate);
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = simpleDateFormat.format(hyojihyo.Nyukodate);
                listRowData.Checkresult = syukoDenpyo.Nyukodate == hyojihyo.Nyukodate ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // 単重量
        if (barChkKomk.Is_tanjuryo == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "単重量";
            listRowData.Syukodenpyo_dispvalue = String.format("#,##0.0000", syukoDenpyo.Tanjuryo);
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = String.format("#,##0.0000", hyojihyo.Tanjuryo);
                listRowData.Checkresult = syukoDenpyo.Tanjuryo.equals(hyojihyo.Tanjuryo) ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // 承認日
        if (barChkKomk.Is_syonindate == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "承認日";
            listRowData.Syukodenpyo_dispvalue = simpleDateFormat.format(syukoDenpyo.Syonindate);
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = simpleDateFormat.format(hyojihyo.Syonindate);
                listRowData.Checkresult = syukoDenpyo.Syonindate == hyojihyo.Syonindate ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // 承認番号
        if (barChkKomk.Is_syoninno == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "承認番号";
            listRowData.Syukodenpyo_dispvalue = String.format("00000000000", syukoDenpyo.Syoninno);
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = String.format("00000000000", hyojihyo.Syoninno);
                listRowData.Checkresult = syukoDenpyo.Syoninno == hyojihyo.Syoninno ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // 通関申告日
        if (barChkKomk.Is_tukandate == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "通関申告日";
            listRowData.Syukodenpyo_dispvalue = simpleDateFormat.format(syukoDenpyo.Tukandate);
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = simpleDateFormat.format(hyojihyo.Tukandate);
                listRowData.Checkresult = syukoDenpyo.Tukandate == hyojihyo.Tukandate ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // 通関番号
        if (barChkKomk.Is_tukanno == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "通関番号";
            listRowData.Syukodenpyo_dispvalue = String.format("00000000000", syukoDenpyo.Tukanno);
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = String.format("00000000000", hyojihyo.Tukanno);
                listRowData.Checkresult = syukoDenpyo.Tukanno == hyojihyo.Tukanno ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // 出荷止め
        if (barChkKomk.Is_sykdomecd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "出荷止め";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Sykdomenm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Sykdomenm;
                listRowData.Checkresult = syukoDenpyo.Sykdomecd == hyojihyo.Sykdomecd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // 商社名
        if (barChkKomk.Is_syosyacd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "商社";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Syosyanm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Syosyanm;
                listRowData.Checkresult = syukoDenpyo.Syosyacd == hyojihyo.Syosyacd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // 寄託者名
        if (barChkKomk.Is_kitakucd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "寄託者";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Kitakunm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Kitakunm;
                listRowData.Checkresult = syukoDenpyo.Kitakucd == hyojihyo.Kitakucd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // BLNo
        if (barChkKomk.Is_blno == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "BLNo";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Blno;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Blno;
                listRowData.Checkresult = syukoDenpyo.Blno.equals(hyojihyo.Blno) ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // ID
        if (barChkKomk.Is_id == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "ID";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Id;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Id;
                listRowData.Checkresult = syukoDenpyo.Id.equals(hyojihyo.Id) ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // 保税輸送申告日
        if (barChkKomk.Is_oltdate == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "保税輸送申告日";
            listRowData.Syukodenpyo_dispvalue = simpleDateFormat.format(syukoDenpyo.Oltdate);
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = simpleDateFormat.format(hyojihyo.Oltdate);
                listRowData.Checkresult = syukoDenpyo.Oltdate.equals(hyojihyo.Oltdate) ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // 申告価格
        if (barChkKomk.Is_sinkokukkk == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "申告価格";
            listRowData.Syukodenpyo_dispvalue = String.format("#,###", syukoDenpyo.Sinkokukkk);
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = String.format("#,###", hyojihyo.Sinkokukkk);
                listRowData.Checkresult = syukoDenpyo.Sinkokukkk == hyojihyo.Sinkokukkk ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // 通貨単位
        if (barChkKomk.Is_tukatanicd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "通貨単位";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Tukataninm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Tukataninm;
                listRowData.Checkresult = syukoDenpyo.Tukatanicd == hyojihyo.Tukatanicd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // 在庫項目
        if (barChkKomk.Is_zaikokomkcd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "在庫項目";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Zaikokomknm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Zaikokomknm;
                listRowData.Checkresult = syukoDenpyo.Zaikokomkcd == hyojihyo.Zaikokomkcd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // DONO
        if (barChkKomk.Is_dono == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "DONO";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Dono;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Dono;
                listRowData.Checkresult = syukoDenpyo.Dono == hyojihyo.Dono ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        // 勝手取り
        if (barChkKomk.Is_katteflg == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "勝手取り";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Katteflg == 1 ? "勝手取り" : "";
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Katteflg == 1 ? "勝手取り" : "";
                listRowData.Checkresult = syukoDenpyo.Katteflg == hyojihyo.Katteflg ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            ret.add(listRowData);
        }
        for (BarChkKomk_KikakuModel kikaku : barChkKomk.Kikakus) {

            if (kikaku.Kkkkkmchkkbn == 1) {
                continue;
            }

            // 規格１
            if (kikaku.Kkkkmkcd == 1) {
                ListRowData listRowData = new ListRowData();
                listRowData.Komokname = "規格１";
                listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Kikakunaiyo1;
                if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                    listRowData.Hyojihyo_dispvalue = hyojihyo.Kikakunaiyo1;
                    listRowData.Checkresult = syukoDenpyo.Kikakucd1 == hyojihyo.Kikakucd1 ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
                }

                ret.add(listRowData);
            }
            // 規格２
            else if (kikaku.Kkkkmkcd == 2) {
                ListRowData listRowData = new ListRowData();
                listRowData.Komokname = "規格２";
                listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Kikakunaiyo2;
                if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                    listRowData.Hyojihyo_dispvalue = hyojihyo.Kikakunaiyo2;
                    listRowData.Checkresult = syukoDenpyo.Kikakucd2 == hyojihyo.Kikakucd2 ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
                }

                ret.add(listRowData);
            }
            // 規格３
            else if (kikaku.Kkkkmkcd == 3) {
                ListRowData listRowData = new ListRowData();
                listRowData.Komokname = "規格３";
                listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Kikakunaiyo3;
                if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                    listRowData.Hyojihyo_dispvalue = hyojihyo.Kikakunaiyo3;
                    listRowData.Checkresult = syukoDenpyo.Kikakucd3 == hyojihyo.Kikakucd3 ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
                }

                ret.add(listRowData);
            }
            // 規格４
            else if (kikaku.Kkkkmkcd == 4) {
                ListRowData listRowData = new ListRowData();
                listRowData.Komokname = "規格４";
                listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Kikakunaiyo4;
                if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                    listRowData.Hyojihyo_dispvalue = hyojihyo.Kikakunaiyo4;
                    listRowData.Checkresult = syukoDenpyo.Kikakucd4 == hyojihyo.Kikakucd4 ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
                }

                ret.add(listRowData);
            }
            // 規格５
            else if (kikaku.Kkkkmkcd == 5) {
                ListRowData listRowData = new ListRowData();
                listRowData.Komokname = "規格５";
                listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Kikakunaiyo5;
                if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                    listRowData.Hyojihyo_dispvalue = hyojihyo.Kikakunaiyo5;
                    listRowData.Checkresult = syukoDenpyo.Kikakucd5 == hyojihyo.Kikakucd5 ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
                }

                ret.add(listRowData);
            }
            // 規格６
            else if (kikaku.Kkkkmkcd == 6) {
                ListRowData listRowData = new ListRowData();
                listRowData.Komokname = "規格６";
                listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Kikakunaiyo6;
                if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                    listRowData.Hyojihyo_dispvalue = hyojihyo.Kikakunaiyo6;
                    listRowData.Checkresult = syukoDenpyo.Kikakucd6 == hyojihyo.Kikakucd6 ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
                }

                ret.add(listRowData);
            }
            // 規格７
            else if (kikaku.Kkkkmkcd == 7) {
                ListRowData listRowData = new ListRowData();
                listRowData.Komokname = "規格７";
                listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Kikakunaiyo7;
                if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                    listRowData.Hyojihyo_dispvalue = hyojihyo.Kikakunaiyo7;
                    listRowData.Checkresult = syukoDenpyo.Kikakucd7 == hyojihyo.Kikakucd7 ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
                }

                ret.add(listRowData);
            }
            // 規格８
            else if (kikaku.Kkkkmkcd == 8) {
                ListRowData listRowData = new ListRowData();
                listRowData.Komokname = "規格８";
                listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Kikakunaiyo8;
                if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                    listRowData.Hyojihyo_dispvalue = hyojihyo.Kikakunaiyo8;
                    listRowData.Checkresult = syukoDenpyo.Kikakucd8 == hyojihyo.Kikakucd8 ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
                }

                ret.add(listRowData);
            }
            // 規格９
            else if (kikaku.Kkkkmkcd == 9) {
                ListRowData listRowData = new ListRowData();
                listRowData.Komokname = "規格９";
                listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Kikakunaiyo9;
                if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                    listRowData.Hyojihyo_dispvalue = hyojihyo.Kikakunaiyo9;
                    listRowData.Checkresult = syukoDenpyo.Kikakucd9 == hyojihyo.Kikakucd9 ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
                }

                ret.add(listRowData);
            }
            // 規格１０
            else if (kikaku.Kkkkmkcd == 10) {
                ListRowData listRowData = new ListRowData();
                listRowData.Komokname = "規格１０";
                listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Kikakunaiyo10;
                if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                    listRowData.Hyojihyo_dispvalue = hyojihyo.Kikakunaiyo10;
                    listRowData.Checkresult = syukoDenpyo.Kikakucd10 == hyojihyo.Kikakucd10 ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
                }

                ret.add(listRowData);
            }
            // 規格１１
            else if (kikaku.Kkkkmkcd == 11) {
                ListRowData listRowData = new ListRowData();
                listRowData.Komokname = "規格１１";
                listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Kikakunaiyo11;
                if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                    listRowData.Hyojihyo_dispvalue = hyojihyo.Kikakunaiyo11;
                    listRowData.Checkresult = syukoDenpyo.Kikakucd11 == hyojihyo.Kikakucd11 ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
                }

                ret.add(listRowData);
            }
            // 規格１２
            else if (kikaku.Kkkkmkcd == 12) {
                ListRowData listRowData = new ListRowData();
                listRowData.Komokname = "規格１２";
                listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Kikakunaiyo12;
                if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                    listRowData.Hyojihyo_dispvalue = hyojihyo.Kikakunaiyo12;
                    listRowData.Checkresult = syukoDenpyo.Kikakucd12 == hyojihyo.Kikakucd12 ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
                }

                ret.add(listRowData);
            }
        }

        return ret;
    }

    //endregion

    //region TODO DENSO固有ボタンの設定

    @Override
    public void onKeyRemapCreated() {

        // 左トリガーにバーコードスキャンを割り当て
        mKeyRemapLibrary.setRemapKey(KeyRemapLibrary.KeyCode.KEY_CODE_LT.getValue(),
                KeyRemapLibrary.ScanCode.SCAN_CODE_TRIGGER_BARCODE.getString());

        // 右トリガーにバーコードスキャンを割り当て
        mKeyRemapLibrary.setRemapKey(KeyRemapLibrary.KeyCode.KEY_CODE_RT.getValue(),
                KeyRemapLibrary.ScanCode.SCAN_CODE_TRIGGER_BARCODE.getString());

    }

    //endregion
}
package com.example.sozax_app.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.densowave.bhtsdk.barcode.BarcodeDataReceivedEvent;
import com.densowave.bhtsdk.keyremap.KeyRemapLibrary;
import com.example.sozax_app.R;
import com.example.sozax_app.bl.controllers.BarChkKomkController;
import com.example.sozax_app.bl.controllers.HyojihyoController;
import com.example.sozax_app.bl.controllers.SyukoSagyoController;
import com.example.sozax_app.bl.models.bar_chk_komk.BarChkKomkConditionModel;
import com.example.sozax_app.bl.models.bar_chk_komk.BarChkKomkModel;
import com.example.sozax_app.bl.models.bar_chk_komk.BarChkKomk_KikakuModel;
import com.example.sozax_app.bl.models.hyojihyo.HyojihyoConditionModel;
import com.example.sozax_app.bl.models.hyojihyo.HyojihyoModel;
import com.example.sozax_app.bl.models.syuko_denpyo.SyukoDenpyoModel;
import com.example.sozax_app.bl.models.syuko_denpyo.SyukoDenpyosModel;
import com.example.sozax_app.common.ConstClass;
import com.example.sozax_app.common.EnumClass;
import com.example.sozax_app.common.ScannerActivity;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.sozax_app.common.CommonFunction.multiplyThousand;
import static com.example.sozax_app.common.CommonFunction.toFullWidth;
import static com.example.sozax_app.common.CommonFunction.toTanjuryoFormat;
import static java.lang.String.format;

public class GoodsIssuePage2Activity extends ScannerActivity {

    //region ????????????????????????

    // ???????????????????????????
    private ArrayList<SyukoDenpyoModel> sagyochuSyukoDenpyos = null;

    // ???????????????????????????????????????????????????
    private int selectedSagyochuSyukoDenpyoIndex = -1;

    //endregion

    //region ????????????

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_issue_page2);

        // ?????????????????????
        // ???????????????????????????
        findViewById(R.id.btnPrevSlip).setOnClickListener(new btnPrevSlip_Click());
        // ???????????????????????????
        findViewById(R.id.btnNextSlip).setOnClickListener(new btnNextSlip_Click());
        // ???????????????????????????
        ((ListView) findViewById(R.id.lvGoodsIssueProductInformation)).setOnItemClickListener(new lvGoodsIssueProductInformation_Click());

        // ???????????????????????????
        Intent intent = getIntent();
        try {
            //noinspection unchecked
            sagyochuSyukoDenpyos = (ArrayList<SyukoDenpyoModel>) intent.getSerializableExtra(getResources().getString(R.string.intent_key_sagyochu_syuko_denpyos));
        } catch (Exception exception) {
            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }
        selectedSagyochuSyukoDenpyoIndex = intent.getIntExtra(getResources().getString(R.string.intent_key_selected_sagyochu_syuko_denpyo_index), -1);

        // ??????????????????????????????
        RefreshScreenAll();
    }

    //endregion

    //region ????????????????????????

    private class btnPrevSlip_Click implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (selectedSagyochuSyukoDenpyoIndex == 0) {
                return;
            }

            // ??????????????????
            selectedSagyochuSyukoDenpyoIndex--;

            // ???????????????????????????
            RefreshScreenAll();
        }
    }

    //endregion

    //region ????????????????????????

    private class btnNextSlip_Click implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (selectedSagyochuSyukoDenpyoIndex == (sagyochuSyukoDenpyos.size() - 1)) {
                return;
            }

            // ?????????????????????
            selectedSagyochuSyukoDenpyoIndex++;

            // ???????????????????????????
            RefreshScreenAll();
        }
    }

    //endregion

    //region ????????????QR???????????????

    // ???????????????????????????????????????
    final private String chkPattern = "2:[0-9]{15}$";

    // ???????????????
    private Boolean isVibrate = false;

    @Override
    public void onBarcodeDataReceived(BarcodeDataReceivedEvent event) {

        List<BarcodeDataReceivedEvent.BarcodeData> listBarcodeData = event.getBarcodeData();

        for (BarcodeDataReceivedEvent.BarcodeData data : listBarcodeData) {

            runOnUiThread(new Runnable() {

                        BarcodeDataReceivedEvent.BarcodeData readData = null;

                        Runnable setData(BarcodeDataReceivedEvent.BarcodeData _readData) {
                            readData = _readData;
                            return this;
                        }

                        @Override
                        public void run() {

                            // QR???????????????
                            String qrData = readData.getData();

                            // ????????????????????????
                            Pattern ptn = Pattern.compile(chkPattern);        // ???????????????2???????????????2????????????:???????????????3????????????????????????????????????17???????????????
                            Matcher matcher = ptn.matcher(qrData);

                            // ???????????????????????????
                            if (!matcher.lookingAt()) {
                                // ?????????QR????????????????????????????????????????????????????????????
                                OutputErrorMessage(getResources().getString(R.string.inventory_inquiry_page1_activity_not_hyojihyosqr));
                            } else {
                                // ?????????QR??????????????????????????????????????????????????????????????????????????????????????????
                                // QR?????????????????????????????????????????????
                                long syukeicd = Long.parseLong(qrData.substring(2));

                                // ??????????????????????????????????????????;
                                SyukoDenpyoModel syukoDenpyoModel = sagyochuSyukoDenpyos.get(selectedSagyochuSyukoDenpyoIndex);
                                syukoDenpyoModel.Syukosgyjokyo.Syukeicd = syukeicd;
                                syukoDenpyoModel.Syukosgyjokyo.HyojiHyo = null;

                                // ?????????????????????
                                SyukoDenpyosModel putData = new SyukoDenpyosModel();
                                putData.SyukoDenpyos = new SyukoDenpyoModel[1];
                                putData.SyukoDenpyos[0] = syukoDenpyoModel;

                                // ????????????
                                isVibrate = true;

                                // ??????
                                new PutSyukoSagyosTask().execute(putData);
                            }
                        }
                    }.setData(data)
            );
        }
    }

    //endregion

    //region ?????????????????????????????????

    class lvGoodsIssueProductInformation_Click implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (currentListData == null) return;

            // ??????????????????????????????????????????
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment oldFragment = fragmentManager.findFragmentByTag(dialogTag);
            if (oldFragment != null) {
                fragmentManager.beginTransaction().remove(oldFragment).commit();
            }

            DetailDialogFragment detailDialogFragment = new DetailDialogFragment();
            detailDialogFragment.listRowData = currentListData.ListRowDatas.get(position);
            detailDialogFragment.showNow(fragmentManager, dialogTag);
        }
    }

    //region ?????????????????????

    public static class DetailDialogFragment extends DialogFragment {

        public ListRowData listRowData;

        //region ?????????????????????

        public DetailDialogFragment() {

        }

        //endregion

        //region ?????????????????????

        @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(listRowData.Komokname);
            builder.setMessage("????????????" + "\r\n" + listRowData.Syukodenpyo_dispvalue + "\r\n\n" + "?????????" + "\r\n" + listRowData.Hyojihyo_dispvalue);

            return builder.create();
        }

        //endregion

        //region ??????

        @Override
        public void onPause() {
            super.onPause();

            // onPause ????????????????????????????????????
            dismiss();
        }

        //endregion
    }

    //endregion

    //endregion

    //region ??????????????????????????????

    public void btnGoodsIssuePage2Proceed_Click(View view) {

        if (sagyochuSyukoDenpyos.size() == 0) {
            return;
        }

        if (selectedSagyochuSyukoDenpyoIndex == -1) {
            return;
        }

        // ????????????????????????????????????
        SyukoDenpyoModel syukoDenpyoModel = sagyochuSyukoDenpyos.get(selectedSagyochuSyukoDenpyoIndex);

        // ???????????????????????????????????????
        EnumClass.SgyjokyoKubun sgyjokyoKubun = EnumClass.getSgyjokyoKubun(syukoDenpyoModel.Syukosgyjokyo.Sgyjokyokbn);

        if (sgyjokyoKubun == null) {
            return;
        }

        switch (sgyjokyoKubun) {
            case Uketuke:
                syukoDenpyoModel.Syukosgyjokyo.Sgyjokyokbn = EnumClass.SgyjokyoKubun.Zaikokakunin.getInteger();
                break;
            case Zaikokakunin:
                syukoDenpyoModel.Syukosgyjokyo.Sgyjokyokbn = EnumClass.SgyjokyoKubun.Syukosagyo.getInteger();
                break;
            case Syukosagyo:
                syukoDenpyoModel.Syukosgyjokyo.Sgyjokyokbn = EnumClass.SgyjokyoKubun.Juryokakunin.getInteger();
                break;
        }

        // ?????????????????????
        SyukoDenpyosModel putData = new SyukoDenpyosModel();
        putData.SyukoDenpyos = new SyukoDenpyoModel[1];
        putData.SyukoDenpyos[0] = syukoDenpyoModel;

        // ??????
        new PutSyukoSagyosTask().execute(putData);
    }

    //endregion

    //region ??????????????????????????????????????????

    @SuppressLint("StaticFieldLeak")
    public class GetBarChkKomkTask extends BarChkKomkController.GetBarChkKomkTask {

        // ???????????????
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // ??????????????????
            setEnabledOperation(false);
        }

        // ???????????????
        @Override
        protected void onPostExecute(BarChkKomkModel _barChkKomkModel) {

            try {

                // ???????????????
                if (_barChkKomkModel.Is_error) {
                    String template = "??????????????????????????????????????????????????????????????????\r\n{0}";
                    OutputErrorMessage((java.text.MessageFormat.format(template, _barChkKomkModel.Message)));
                    return;
                }

                // ?????????????????????
                if (_barChkKomkModel.Hinbuncd == 0 && _barChkKomkModel.Hinsyucd == 0 && _barChkKomkModel.Ninusicd == 0) {
                    OutputErrorMessage("???????????????????????????????????????????????????????????????");
                    return;
                }

                // ??????????????????????????????????????????
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment oldFragment = fragmentManager.findFragmentByTag(dialogTag);
                if (oldFragment != null) {
                    fragmentManager.beginTransaction().remove(oldFragment).commit();
                }

                // ??????????????????????????????????????????????????????????????????????????????????????????
                sagyochuSyukoDenpyos.get(selectedSagyochuSyukoDenpyoIndex).BarChkKomk = _barChkKomkModel;

                // ???????????????????????????
                RefreshScreenAll();

            } finally {

                // ??????????????????
                setEnabledOperation(true);
            }
        }
    }

    //endregion

    //region ???????????????????????????

    @SuppressLint("StaticFieldLeak")
    public class GetHyojihyoTask extends HyojihyoController.GetHyojihyoTask {

        // ???????????????
        @Override
        protected void onPostExecute(HyojihyoModel _hyojihyoModel) {

            try {

                // ???????????????
                if (_hyojihyoModel.Is_error) {
                    String template = "???????????????????????????????????????????????????\r\n{0}";
                    OutputErrorMessage((java.text.MessageFormat.format(template, _hyojihyoModel.Message)));
                    return;
                }

                // ?????????????????????
                if (_hyojihyoModel.Syukeicd == 0L) {
                    OutputErrorMessage("????????????????????????????????????????????????");
                }

                // ??????????????????????????????????????????
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment oldFragment = fragmentManager.findFragmentByTag(dialogTag);
                if (oldFragment != null) {
                    fragmentManager.beginTransaction().remove(oldFragment).commit();
                }

                // ???????????????????????????????????????????????????????????????????????????
                sagyochuSyukoDenpyos.get(selectedSagyochuSyukoDenpyoIndex).Syukosgyjokyo.HyojiHyo = _hyojihyoModel;

                // ???????????????????????????
                RefreshScreenAll();

            } finally {

                // ??????????????????
                setEnabledOperation(true);

            }
        }
    }

    //endregion

    //region ?????????????????????

    @SuppressLint("StaticFieldLeak")
    public class PutSyukoSagyosTask extends SyukoSagyoController.PutSyukoSagyosTask {

        // ???????????????
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // ??????????????????
            setEnabledOperation(false);
        }

        // ???????????????
        @Override
        protected void onPostExecute(SyukoDenpyosModel _syukoDenpyosModel) {

            boolean isFinish = false;

            try {

                // ???????????????
                if (_syukoDenpyosModel.Is_error) {
                    String template = "?????????????????????????????????????????????\r\n{0}";
                    OutputErrorMessage((java.text.MessageFormat.format(template, _syukoDenpyosModel.Message)));
                    return;
                }

                // ??????????????????????????????????????????
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment oldFragment = fragmentManager.findFragmentByTag(dialogTag);
                if (oldFragment != null) {
                    fragmentManager.beginTransaction().remove(oldFragment).commit();
                }

                // ?????????????????????????????????
                for (SyukoDenpyoModel _syukoDenpyoModel : _syukoDenpyosModel.SyukoDenpyos) {

                    for (SyukoDenpyoModel sagyochuSyukoDenpyo : sagyochuSyukoDenpyos) {

                        if (sagyochuSyukoDenpyo.Syukono == _syukoDenpyoModel.Syukono) {
                            //noinspection UnusedAssignment
                            sagyochuSyukoDenpyo = _syukoDenpyoModel;
                            break;
                        }
                    }
                }

                // ?????????????????????4:????????????????????????????????????????????????
                ArrayList<SyukoDenpyoModel> removeSyukoDenpyoModelArrayList = new ArrayList<SyukoDenpyoModel>();
                for (SyukoDenpyoModel sagyochuSyukoDenpyo : sagyochuSyukoDenpyos) {

                    if (sagyochuSyukoDenpyo.Syukosgyjokyo.Sgyjokyokbn == EnumClass.SgyjokyoKubun.Juryokakunin.getInteger()) {
                        removeSyukoDenpyoModelArrayList.add(sagyochuSyukoDenpyo);
                    }
                }

                if (removeSyukoDenpyoModelArrayList.size() > 0) {
                    for (SyukoDenpyoModel removeSyukoDenpyoModel : removeSyukoDenpyoModelArrayList) {
                        sagyochuSyukoDenpyos.remove(removeSyukoDenpyoModel);
                    }

                    if (sagyochuSyukoDenpyos.size() == 0) {
                        isFinish = true;
                        return;
                    }

                    int lastIndex = sagyochuSyukoDenpyos.size() - 1;
                    if (selectedSagyochuSyukoDenpyoIndex > lastIndex) {
                        selectedSagyochuSyukoDenpyoIndex = lastIndex;
                    }
                }

                // ???????????????????????????
                RefreshScreenAll();

            } finally {

                // ??????????????????
                setEnabledOperation(true);

                if (isFinish) {
                    // ??????????????????????????????????????????????????????????????????????????????????????????
                    Intent intent = new Intent();
                    intent.putExtra(getResources().getString(R.string.intent_key_sagyochu_syuko_denpyos), sagyochuSyukoDenpyos);
                    intent.putExtra(getResources().getString(R.string.intent_key_selected_sagyochu_syuko_denpyo_index), selectedSagyochuSyukoDenpyoIndex);

                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }

    }

    //endregion

    //region ??????????????????????????????

    ListData currentListData = null;

    @SuppressLint("DefaultLocale")
    private void RefreshScreenAll() {

        // ???????????????
        Button btnGoodsIssuePage2Proceed = findViewById(R.id.btnGoodsIssuePage2Proceed);
        btnGoodsIssuePage2Proceed.setText("");
        btnGoodsIssuePage2Proceed.setBackgroundColor(getColor(R.color.white));
        btnGoodsIssuePage2Proceed.setEnabled(false);

        // ????????????????????????
        SyukoDenpyoModel syukoDenpyo = sagyochuSyukoDenpyos.get(selectedSagyochuSyukoDenpyoIndex);

        if (syukoDenpyo.BarChkKomk == null) {

            // ?????????????????????????????????????????????????????????????????????
            BarChkKomkConditionModel barChkKomkConditionModel = new BarChkKomkConditionModel();
            barChkKomkConditionModel.Hinbuncd = syukoDenpyo.Hinbuncd;
            barChkKomkConditionModel.Hinsyucd = syukoDenpyo.Hinsyucd;
            barChkKomkConditionModel.Ninusicd = syukoDenpyo.Ninusicd;

            new GetBarChkKomkTask().execute(barChkKomkConditionModel);
            return;
        }

        if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L && syukoDenpyo.Syukosgyjokyo.HyojiHyo == null) {

            // ?????????????????????????????????????????????????????????????????????????????????????????????
            HyojihyoConditionModel hyojihyoConditionModel = new HyojihyoConditionModel();
            hyojihyoConditionModel.Kaicd = loginInfo.Kaicd;
            hyojihyoConditionModel.Soukocd = loginInfo.Soukocd;
            hyojihyoConditionModel.Syukeicd = syukoDenpyo.Syukosgyjokyo.Syukeicd;

            new GetHyojihyoTask().execute(hyojihyoConditionModel);
            return;
        }

        // ???????????????????????????????????????
        currentListData = new ListData();
        currentListData.Syukono = syukoDenpyo.Syukono;
        currentListData.ListRowDatas = CreateListRowDatas(syukoDenpyo);

        // ?????????????????????????????????????????????
        UniqueAdapter uniqueAdapter = new UniqueAdapter(this, currentListData);

        // ???????????????????????????
        ListView lvGoodsIssueProductInformation = findViewById(R.id.lvGoodsIssueProductInformation);
        lvGoodsIssueProductInformation.setAdapter(uniqueAdapter);

        // ???????????????????????????
        TextView txtSlipInfo = findViewById(R.id.txtSlipInfo);
        String templateSlipInfo = "({0}/{1})\r\nNo.{2}";
        txtSlipInfo.setText(java.text.MessageFormat.format(templateSlipInfo, (selectedSagyochuSyukoDenpyoIndex + 1), sagyochuSyukoDenpyos.size(), format("%011d", syukoDenpyo.Syukono)));

        // ????????????
        TextView txtProgressPhase2 = findViewById(R.id.txtProgressPhase2);
        TextView txtProgressPhase3 = findViewById(R.id.txtProgressPhase3);
        TextView txtProgressPhase4 = findViewById(R.id.txtProgressPhase4);
        // ???????????????
        TextView txtGoodsIssuePage2Guidance = findViewById(R.id.txtGoodsIssuePage2Guidance);

        TextView textView = findViewById(R.id.lblGoodsIssuePage2Info);
        if (currentDispMode == DispMode.SyukoDenpyo) {
            textView.setText("???????????????");
        } else {
            textView.setText("???????????????");
        }

        // ????????????????????????
        DisplaySuryoJuryo();

        EnumClass.SgyjokyoKubun sgyjokyoKubun = EnumClass.getSgyjokyoKubun(syukoDenpyo.Syukosgyjokyo.Sgyjokyokbn);
        if (sgyjokyoKubun == null) {
            return;
        }

        switch (sgyjokyoKubun) {
            case Uketuke:

                txtProgressPhase2.setBackgroundColor(getColor(R.color.signalred));
                txtProgressPhase3.setBackgroundColor(getColor(R.color.transparent));
                txtProgressPhase4.setBackgroundColor(getColor(R.color.transparent));

                txtProgressPhase2.setTextColor(getColor(R.color.white));
                txtProgressPhase3.setTextColor(getColor(R.color.black));
                txtProgressPhase4.setTextColor(getColor(R.color.black));

                txtGoodsIssuePage2Guidance.setText(getString(R.string.text_goods_issue_guidance_phase2));

                boolean existsNG = false;
                boolean existsUNCHECK = false;
                for (ListRowData listRowData : currentListData.ListRowDatas) {
                    if (listRowData.Checkresult == EnumClass.CheckKubun.NG) {
                        existsNG = true;
                        break;
                    } else if (listRowData.Checkresult == EnumClass.CheckKubun.UNCHECK) {
                        existsUNCHECK = true;
                        break;
                    }
                }

                if (existsNG) {
                    SpannableStringBuilder sb = new SpannableStringBuilder("????????????");
                    int start = sb.length();
                    int color;

                    sb.append("\n(NG?????????????????????????????????????????????)");
                    sb.setSpan(new RelativeSizeSpan(0.5f), start, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    color = getColor(R.color.darkgray);

                    btnGoodsIssuePage2Proceed.setText(sb);
                    btnGoodsIssuePage2Proceed.setBackgroundColor(color);
                    btnGoodsIssuePage2Proceed.setEnabled(false);

                    if (isVibrate) {
                        // ????????????
                        Vibrate();
                        isVibrate = false;
                    }

                } else if (existsUNCHECK) {
                    SpannableStringBuilder sb = new SpannableStringBuilder("????????????");
                    int start = sb.length();
                    int color;

                    sb.append("\n(????????????????????????????????????????????????)");
                    sb.setSpan(new RelativeSizeSpan(0.5f), start, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    color = getColor(R.color.darkgray);

                    btnGoodsIssuePage2Proceed.setText(sb);
                    btnGoodsIssuePage2Proceed.setBackgroundColor(color);
                    btnGoodsIssuePage2Proceed.setEnabled(false);
                } else {
                    SpannableStringBuilder sb = new SpannableStringBuilder();
                    sb.append("????????????");
                    int start = sb.length();
                    sb.append("\n(????????????????????????????????????)");
                    sb.setSpan(new RelativeSizeSpan(0.5f), start, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    btnGoodsIssuePage2Proceed.setText(sb);
                    btnGoodsIssuePage2Proceed.setBackgroundColor(getColor(R.color.coral));
                    btnGoodsIssuePage2Proceed.setEnabled(true);
                }

                // ?????????????????????????????????????????????
                ScanClaim();

                break;

            case Zaikokakunin:

                txtProgressPhase2.setBackgroundColor(getColor(R.color.signalred));
                txtProgressPhase3.setBackgroundColor(getColor(R.color.signalred));
                txtProgressPhase4.setBackgroundColor(getColor(R.color.transparent));

                txtProgressPhase2.setTextColor(getColor(R.color.white));
                txtProgressPhase3.setTextColor(getColor(R.color.white));
                txtProgressPhase4.setTextColor(getColor(R.color.black));

                txtGoodsIssuePage2Guidance.setText(getString(R.string.text_goods_issue_guidance_phase3));

                SpannableStringBuilder sb = new SpannableStringBuilder();
                sb.append("????????????");
                int start = sb.length();
                sb.append("\n(??????????????????????????????????????????)");
                sb.setSpan(new RelativeSizeSpan(0.5f), start, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                btnGoodsIssuePage2Proceed.setText(sb);
                btnGoodsIssuePage2Proceed.setBackgroundColor(getColor(R.color.coral));
                btnGoodsIssuePage2Proceed.setEnabled(true);

                // ?????????????????????????????????????????????
                ScanClose();

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
                sb.append("????????????");
                start = sb.length();
                sb.append("\n(????????????????????????????????????????????????)");
                sb.setSpan(new RelativeSizeSpan(0.5f), start, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                btnGoodsIssuePage2Proceed.setText(sb);
                btnGoodsIssuePage2Proceed.setBackgroundColor(getColor(R.color.signalred));
                btnGoodsIssuePage2Proceed.setEnabled(true);

                // ?????????????????????????????????????????????
                ScanClose();

                break;
        }
    }

    private void DisplaySuryoJuryo() {
        DisplaySuryoJuryo(selectedSagyochuSyukoDenpyoIndex);
    }

    @SuppressLint("DefaultLocale")
    private void DisplaySuryoJuryo(int index) {
        TextView txtGoodsIssuePage1Quantity = findViewById(R.id.txtGoodsIssuePage2Quantity);
        TextView txtGoodsIssuePage1Weight = findViewById(R.id.txtGoodsIssuePage2Weight);

        SyukoDenpyoModel currentSagyochuSyukoDenpyo = sagyochuSyukoDenpyos.get(index);

        if (currentSagyochuSyukoDenpyo.Nisucd != ConstClass.NISUGATA_BARA) {
            // ????????????
            txtGoodsIssuePage1Quantity.setText(toFullWidth(String.format("%,d", currentSagyochuSyukoDenpyo.Kosuu.intValue())));
        }
        // ????????????
        txtGoodsIssuePage1Weight.setText(toFullWidth(String.format("%,d", multiplyThousand(currentSagyochuSyukoDenpyo.Juryo).intValue())));
    }

    //endregion

    //region ???????????????????????????

    // F2??????????????????
    private boolean isF2Down = false;
    // F3??????????????????
    private boolean isF3Down = false;
    final String[] items = {"???????????????????????????????????????????????????"};
    final boolean[] checkedItems = {false};

    @SuppressLint({"RtlHardcoded", "SetTextI18n"})
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {

        SyukoDenpyoModel syukoDenpyoModel = sagyochuSyukoDenpyos.get(selectedSagyochuSyukoDenpyoIndex);

        // ????????????????????????????????????
        if (syukoDenpyoModel.Syukosgyjokyo.Sgyjokyokbn == EnumClass.SgyjokyoKubun.Uketuke.getInteger()) {

            if (e.getKeyCode() == KeyEvent.KEYCODE_F2) {

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

            // F2&F3?????????????????????
            if (isF2Down && isF3Down) {

                boolean existsNG = false;
                boolean isAllOK = true;
                for (ListRowData listRowData : currentListData.ListRowDatas) {

                    if (!existsNG && listRowData.Checkresult == EnumClass.CheckKubun.NG) {
                        // NG???????????????
                        existsNG = true;
                    }

                    if (isAllOK && listRowData.Checkresult != EnumClass.CheckKubun.OK) {
                        // ??????OK
                        isAllOK = false;
                    }
                }

                if (!isAllOK) {

                    // F2&F3???????????????????????????
                    AlertDialog.Builder builder = new AlertDialog.Builder(GoodsIssuePage2Activity.this);

                    TextView msgTxt = new TextView(this);
                    msgTxt.setTextSize((float) 14.0);
                    msgTxt.setTextColor(getColor(R.color.black));
                    msgTxt.setPadding(20, 20, 0, 0);

                    if (existsNG) {
                        msgTxt.setText("NG??????????????????????????????????????????????????????");
                    } else {
                        msgTxt.setText("????????????????????????????????????????????????\n????????????????????????");
                    }

                    msgTxt.setGravity(Gravity.LEFT);
                    builder.setCustomTitle(msgTxt);

                    checkedItems[0] = false;
                    builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            checkedItems[which] = isChecked;
                            AlertDialog alertDialog = (AlertDialog) dialog;
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(isChecked);
                        }
                    })
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (!checkedItems[0]) {
                                        return;
                                    }

                                    // ???2:??????????????????????????????????????????
                                    SyukoDenpyoModel syukoDenpyoModel = sagyochuSyukoDenpyos.get(selectedSagyochuSyukoDenpyoIndex);
                                    syukoDenpyoModel.Syukosgyjokyo.Sgyjokyokbn = EnumClass.SgyjokyoKubun.Zaikokakunin.getInteger();

                                    // ?????????????????????
                                    SyukoDenpyosModel putData = new SyukoDenpyosModel();
                                    putData.SyukoDenpyos = new SyukoDenpyoModel[1];
                                    putData.SyukoDenpyos[0] = syukoDenpyoModel;

                                    // ??????
                                    new PutSyukoSagyosTask().execute(putData);
                                }
                            })
                            .setNegativeButton("???????????????", null)
                            .setCancelable(true);

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        }

        if (e.getKeyCode() == KeyEvent.KEYCODE_F4 && e.getAction() == KeyEvent.ACTION_DOWN) {
            switch (currentDispMode) {
                case SyukoDenpyo:
                    currentDispMode = DispMode.Hyojihyo;
                    break;
                case Hyojihyo:
                    currentDispMode = DispMode.SyukoDenpyo;
                    break;
            }

            RefreshScreenAll();
        }

        return super.dispatchKeyEvent(e);
    }

    //endregion

    //region ?????????Adapter

    private DispMode currentDispMode = DispMode.SyukoDenpyo;

    private enum DispMode {
        SyukoDenpyo,
        Hyojihyo
    }

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

            // ?????????????????????????????????
            final ListRowData dispData = getItem(position);

            // ??????????????????????????????????????????
            TextView txtGoodsIssuePage2ProductInformation = view.findViewById(R.id.txtGoodsIssuePage2ProductInformation);
            TextView txtGoodsIssuePage2Status = view.findViewById(R.id.txtGoodsIssuePage2Status);

            switch (currentDispMode) {
                case SyukoDenpyo:

                    txtGoodsIssuePage2ProductInformation.setText(dispData.Syukodenpyo_dispvalue);
                    break;
                case Hyojihyo:

                    txtGoodsIssuePage2ProductInformation.setText(dispData.Hyojihyo_dispvalue);
                    break;
            }

            txtGoodsIssuePage2Status.setText(dispData.Checkresult.getString());
            switch (dispData.Checkresult) {

                case OK:

                    // OK ???????????????????????????????????????
                    txtGoodsIssuePage2Status.setTextColor(getColor(R.color.palegreen));

                    break;
                case NG:

                    // NG ???????????????????????????????????????
                    txtGoodsIssuePage2Status.setTextColor(getColor(R.color.signalred));

                    break;

            }

            return view;
        }
    }

    // ???????????????????????????????????????
    private static class ListData {

        public long Syukono = 0L;
        public ArrayList<ListRowData> ListRowDatas = new ArrayList<ListRowData>();

    }

    //?????????????????????????????????????????????
    private static class ListRowData {
        // ?????????
        public String Komokname = "";
        // ????????????????????????
        public String Syukodenpyo_dispvalue = "";
        // ?????????????????????
        public String Hyojihyo_dispvalue = "";
        // ??????????????????
        public EnumClass.CheckKubun Checkresult = EnumClass.CheckKubun.UNCHECK;
    }

    // ??????????????????????????????????????????
    @SuppressLint("DefaultLocale")
    private ArrayList<ListRowData> CreateListRowDatas(SyukoDenpyoModel syukoDenpyo) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy???MM???dd???");

        // ?????????????????????????????????
        BarChkKomkModel barChkKomk = syukoDenpyo.BarChkKomk;
        // ??????????????????
        HyojihyoModel hyojihyo = syukoDenpyo.Syukosgyjokyo.HyojiHyo;

        ArrayList<ListRowData> ret = new ArrayList<ListRowData>();

        // ??????
        if (barChkKomk.Is_hinsyucd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "??????";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Hinsyunm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Hinsyunm;
                listRowData.Checkresult = syukoDenpyo.Hinsyucd == hyojihyo.Hinsyucd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ??????
        if (barChkKomk.Is_hinmeicd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "??????";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Hinmeinm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Hinmeinm;
                listRowData.Checkresult = syukoDenpyo.Hinmeicd == hyojihyo.Hinmeicd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ??????
        if (barChkKomk.Is_nisucd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "??????";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Nisunm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Nisunm;
                listRowData.Checkresult = syukoDenpyo.Nisucd == hyojihyo.Nisucd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ??????
        if (barChkKomk.Is_nijicd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "??????";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Nijinm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Nijinm;
                listRowData.Checkresult = syukoDenpyo.Nijicd == hyojihyo.Nijicd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            } else if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ??????
        if (barChkKomk.Is_tanicd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "??????";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Taninm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Taninm;
                listRowData.Checkresult = syukoDenpyo.Tanicd == hyojihyo.Tanicd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ??????
        if (barChkKomk.Is_soukocd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "??????";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Soukonm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Soukonm;
                listRowData.Checkresult = syukoDenpyo.Soukocd == hyojihyo.Soukocd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ????????????
        if (barChkKomk.Is_eriacd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "?????????";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Erianm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Erianm;
                listRowData.Checkresult = syukoDenpyo.Eriacd == hyojihyo.Eriacd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ????????????No
        if (barChkKomk.Is_sdosecd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "????????????No";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Funenm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Funenm;
                listRowData.Checkresult = syukoDenpyo.Sdosecd == hyojihyo.Sdosecd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ??????
        if (barChkKomk.Is_ninusicd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "??????";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Ninusinm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Ninusinm;
                listRowData.Checkresult = syukoDenpyo.Ninusicd == hyojihyo.Ninusicd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ????????????
        if (barChkKomk.Is_kamkbn == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "????????????";
            if (syukoDenpyo.Kamokbn > 0) {
                EnumClass.KamotuKubun kamotuKubun = EnumClass.getKamotuKubun(syukoDenpyo.Kamokbn);
                if (kamotuKubun != null) {
                    listRowData.Syukodenpyo_dispvalue = kamotuKubun.getString();
                }
            }
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                if (hyojihyo.Kamokbn > 0) {
                    EnumClass.KamotuKubun kamotuKubun = EnumClass.getKamotuKubun(hyojihyo.Kamokbn);
                    if (kamotuKubun != null) {
                        listRowData.Hyojihyo_dispvalue = kamotuKubun.getString();
                    }
                }

                listRowData.Checkresult = syukoDenpyo.Sdosecd == hyojihyo.Sdosecd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ????????????
        if (barChkKomk.Is_yunyudate == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "????????????";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Yunyudate != null ? simpleDateFormat.format(syukoDenpyo.Yunyudate) : "";
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Yunyudate != null ? simpleDateFormat.format(hyojihyo.Yunyudate) : "";
                listRowData.Checkresult = CompareToDate(syukoDenpyo.Yunyudate, hyojihyo.Yunyudate) == 0 ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ??????????????????
        if (barChkKomk.Is_niduksgyokbn == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "??????????????????";
            if (syukoDenpyo.Niduksgyokbn > 0) {
                EnumClass.NidukurisagyoKubun nidukurisagyoKubun = EnumClass.getNidukuriKubun(syukoDenpyo.Niduksgyokbn);
                if (nidukurisagyoKubun != null) {
                    listRowData.Syukodenpyo_dispvalue = nidukurisagyoKubun.getString();
                }
            }

            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                if (hyojihyo.Niduksgyokbn > 0) {
                    EnumClass.NidukurisagyoKubun nidukurisagyoKubun = EnumClass.getNidukuriKubun(hyojihyo.Niduksgyokbn);
                    if (nidukurisagyoKubun != null) {
                        listRowData.Hyojihyo_dispvalue = nidukurisagyoKubun.getString();
                    }
                }

                listRowData.Checkresult = syukoDenpyo.Niduksgyokbn == hyojihyo.Niduksgyokbn ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ??????????????????
        if (barChkKomk.Is_nyukosyudankbn == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "??????????????????";
            if (syukoDenpyo.Nyukosyudankbn > 0) {
                EnumClass.NyukosyudanKubun nyukosyudanKubun = EnumClass.getNyukosyudanKubun(syukoDenpyo.Nyukosyudankbn);
                if (nyukosyudanKubun != null) {
                    listRowData.Syukodenpyo_dispvalue = nyukosyudanKubun.getString();
                }
            }
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                if (hyojihyo.Nyukosyudankbn > 0) {
                    EnumClass.NyukosyudanKubun nyukosyudanKubun = EnumClass.getNyukosyudanKubun(hyojihyo.Nyukosyudankbn);
                    if (nyukosyudanKubun != null) {
                        listRowData.Hyojihyo_dispvalue = nyukosyudanKubun.getString();
                    }
                }

                listRowData.Checkresult = syukoDenpyo.Nyukosyudankbn == hyojihyo.Nyukosyudankbn ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ???????????????
        if (barChkKomk.Is_nyukodate == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "???????????????";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Nyukodate != null ? simpleDateFormat.format(syukoDenpyo.Nyukodate) : "";
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Nyukodate != null ? simpleDateFormat.format(hyojihyo.Nyukodate) : "";
                listRowData.Checkresult = CompareToDate(syukoDenpyo.Nyukodate, hyojihyo.Nyukodate) == 0 ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ?????????
        if (barChkKomk.Is_tanjuryo == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "?????????";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Nisucd != ConstClass.NISUGATA_BARA ? toTanjuryoFormat(syukoDenpyo.Tanjuryo) : "";

            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Nisucd != ConstClass.NISUGATA_BARA ? toTanjuryoFormat(syukoDenpyo.Tanjuryo) : "";
                listRowData.Hyojihyo_dispvalue = hyojihyo.Nisucd != ConstClass.NISUGATA_BARA ? toTanjuryoFormat(hyojihyo.Tanjuryo) : "";
                listRowData.Checkresult = syukoDenpyo.Tanjuryo.equals(hyojihyo.Tanjuryo) ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ?????????
        if (barChkKomk.Is_syonindate == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "?????????";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Syonindate != null ? simpleDateFormat.format(syukoDenpyo.Syonindate) : "";
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Syonindate != null ? simpleDateFormat.format(hyojihyo.Syonindate) : "";
                listRowData.Checkresult = CompareToDate(syukoDenpyo.Syonindate, hyojihyo.Syonindate) == 0 ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ????????????
        if (barChkKomk.Is_syoninno == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "????????????";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Syoninno > 0L ? format("%011d", syukoDenpyo.Syoninno) : "";
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Syoninno > 0L ? format("%011d", hyojihyo.Syoninno) : "";
                listRowData.Checkresult = syukoDenpyo.Syoninno == hyojihyo.Syoninno ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ???????????????
        if (barChkKomk.Is_tukandate == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "???????????????";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Tukandate != null ? simpleDateFormat.format(syukoDenpyo.Tukandate) : "";
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Tukandate != null ? simpleDateFormat.format(hyojihyo.Tukandate) : "";
                listRowData.Checkresult = CompareToDate(syukoDenpyo.Tukandate, hyojihyo.Tukandate) == 0 ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ????????????
        if (barChkKomk.Is_tukanno == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "????????????";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Tukanno > 0L ? format("%011d", syukoDenpyo.Tukanno) : "";
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Tukanno > 0L ? format("%011d", hyojihyo.Tukanno) : "";
                listRowData.Checkresult = syukoDenpyo.Tukanno == hyojihyo.Tukanno ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ????????????
        if (barChkKomk.Is_sykdomecd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "????????????";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Sykdomenm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Sykdomenm;
                listRowData.Checkresult = syukoDenpyo.Sykdomecd == hyojihyo.Sykdomecd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ?????????
        if (barChkKomk.Is_syosyacd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "??????";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Syosyanm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Syosyanm;
                listRowData.Checkresult = syukoDenpyo.Syosyacd == hyojihyo.Syosyacd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ????????????
        if (barChkKomk.Is_kitakucd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "?????????";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Kitakunm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Kitakunm;
                listRowData.Checkresult = syukoDenpyo.Kitakucd == hyojihyo.Kitakucd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
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

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
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

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ?????????????????????
        if (barChkKomk.Is_oltdate == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "?????????????????????";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Oltdate != null ? simpleDateFormat.format(syukoDenpyo.Oltdate) : "";
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Oltdate != null ? simpleDateFormat.format(hyojihyo.Oltdate) : "";
                listRowData.Checkresult = CompareToDate(syukoDenpyo.Oltdate, hyojihyo.Oltdate) == 0 ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ????????????
        if (barChkKomk.Is_sinkokukkk == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "????????????";
            listRowData.Syukodenpyo_dispvalue = format("%,d", syukoDenpyo.Sinkokukkk.intValue());
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = format("%,d", hyojihyo.Sinkokukkk.intValue());
                listRowData.Checkresult = syukoDenpyo.Sinkokukkk.equals(hyojihyo.Sinkokukkk) ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ????????????
        if (barChkKomk.Is_tukatanicd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "????????????";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Tukataninm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Tukataninm;
                listRowData.Checkresult = syukoDenpyo.Tukatanicd == hyojihyo.Tukatanicd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ????????????
        if (barChkKomk.Is_zaikokomkcd == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "????????????";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Zaikokomknm;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Zaikokomknm;
                listRowData.Checkresult = syukoDenpyo.Zaikokomkcd == hyojihyo.Zaikokomkcd ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // DONO
        if (barChkKomk.Is_dono == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "DONO";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Dono;
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Dono;
                listRowData.Checkresult = syukoDenpyo.Dono.equals(hyojihyo.Dono) ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        // ????????????
        if (barChkKomk.Is_katteflg == 1) {
            ListRowData listRowData = new ListRowData();
            listRowData.Komokname = "????????????";
            listRowData.Syukodenpyo_dispvalue = syukoDenpyo.Katteflg == 1 ? "????????????" : "";
            if (syukoDenpyo.Syukosgyjokyo.Syukeicd > 0L) {
                listRowData.Hyojihyo_dispvalue = hyojihyo.Katteflg == 1 ? "????????????" : "";
                listRowData.Checkresult = syukoDenpyo.Katteflg == hyojihyo.Katteflg ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
            }

            if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                ret.add(listRowData);
        }
        for (BarChkKomk_KikakuModel kikaku : barChkKomk.Kikakus) {

            if (kikaku.Kkkkkmchkkbn == 0) {
                continue;
            }

            // ?????????
            if (kikaku.Hyojijun == 1) {
                ListRowData listRowData = getKikakuListRowData(syukoDenpyo.Kkkkmnm1,
                        syukoDenpyo.Nyurkbn1,
                        syukoDenpyo.Syukosgyjokyo.Syukeicd,
                        syukoDenpyo.Kikakucd1,
                        syukoDenpyo.Kikakunaiyo1,
                        hyojihyo == null ? 0 : hyojihyo.Kikakucd1,
                        hyojihyo == null ? "" : hyojihyo.Kikakunaiyo1);

                if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                    ret.add(listRowData);
            }
            // ?????????
            else if (kikaku.Hyojijun == 2) {
                ListRowData listRowData = getKikakuListRowData(syukoDenpyo.Kkkkmnm2,
                        syukoDenpyo.Nyurkbn2,
                        syukoDenpyo.Syukosgyjokyo.Syukeicd,
                        syukoDenpyo.Kikakucd2,
                        syukoDenpyo.Kikakunaiyo2,
                        hyojihyo == null ? 0 : hyojihyo.Kikakucd2,
                        hyojihyo == null ? "" : hyojihyo.Kikakunaiyo2);

                if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                    ret.add(listRowData);
            }
            // ?????????
            else if (kikaku.Hyojijun == 3) {
                ListRowData listRowData = getKikakuListRowData(syukoDenpyo.Kkkkmnm3,
                        syukoDenpyo.Nyurkbn3,
                        syukoDenpyo.Syukosgyjokyo.Syukeicd,
                        syukoDenpyo.Kikakucd3,
                        syukoDenpyo.Kikakunaiyo3,
                        hyojihyo == null ? 0 : hyojihyo.Kikakucd3,
                        hyojihyo == null ? "" : hyojihyo.Kikakunaiyo3);

                if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                    ret.add(listRowData);
            }
            // ?????????
            else if (kikaku.Hyojijun == 4) {
                ListRowData listRowData = getKikakuListRowData(syukoDenpyo.Kkkkmnm4,
                        syukoDenpyo.Nyurkbn4,
                        syukoDenpyo.Syukosgyjokyo.Syukeicd,
                        syukoDenpyo.Kikakucd4,
                        syukoDenpyo.Kikakunaiyo4,
                        hyojihyo == null ? 0 : hyojihyo.Kikakucd4,
                        hyojihyo == null ? "" : hyojihyo.Kikakunaiyo4);

                if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                    ret.add(listRowData);
            }
            // ?????????
            else if (kikaku.Hyojijun == 5) {
                ListRowData listRowData = getKikakuListRowData(syukoDenpyo.Kkkkmnm5,
                        syukoDenpyo.Nyurkbn5,
                        syukoDenpyo.Syukosgyjokyo.Syukeicd,
                        syukoDenpyo.Kikakucd5,
                        syukoDenpyo.Kikakunaiyo5,
                        hyojihyo == null ? 0 : hyojihyo.Kikakucd5,
                        hyojihyo == null ? "" : hyojihyo.Kikakunaiyo5);

                if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                    ret.add(listRowData);
            }
            // ?????????
            else if (kikaku.Hyojijun == 6) {
                ListRowData listRowData = getKikakuListRowData(syukoDenpyo.Kkkkmnm6,
                        syukoDenpyo.Nyurkbn6,
                        syukoDenpyo.Syukosgyjokyo.Syukeicd,
                        syukoDenpyo.Kikakucd6,
                        syukoDenpyo.Kikakunaiyo6,
                        hyojihyo == null ? 0 : hyojihyo.Kikakucd6,
                        hyojihyo == null ? "" : hyojihyo.Kikakunaiyo6);

                if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                    ret.add(listRowData);
            }
            // ?????????
            else if (kikaku.Hyojijun == 7) {
                ListRowData listRowData = getKikakuListRowData(syukoDenpyo.Kkkkmnm7,
                        syukoDenpyo.Nyurkbn7,
                        syukoDenpyo.Syukosgyjokyo.Syukeicd,
                        syukoDenpyo.Kikakucd7,
                        syukoDenpyo.Kikakunaiyo7,
                        hyojihyo == null ? 0 : hyojihyo.Kikakucd7,
                        hyojihyo == null ? "" : hyojihyo.Kikakunaiyo7);

                if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                    ret.add(listRowData);
            }
            // ?????????
            else if (kikaku.Hyojijun == 8) {
                ListRowData listRowData = getKikakuListRowData(syukoDenpyo.Kkkkmnm8,
                        syukoDenpyo.Nyurkbn8,
                        syukoDenpyo.Syukosgyjokyo.Syukeicd,
                        syukoDenpyo.Kikakucd8,
                        syukoDenpyo.Kikakunaiyo8,
                        hyojihyo == null ? 0 : hyojihyo.Kikakucd8,
                        hyojihyo == null ? "" : hyojihyo.Kikakunaiyo8);

                if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                    ret.add(listRowData);
            }
            // ?????????
            else if (kikaku.Hyojijun == 9) {
                ListRowData listRowData = getKikakuListRowData(syukoDenpyo.Kkkkmnm9,
                        syukoDenpyo.Nyurkbn9,
                        syukoDenpyo.Syukosgyjokyo.Syukeicd,
                        syukoDenpyo.Kikakucd9,
                        syukoDenpyo.Kikakunaiyo9,
                        hyojihyo == null ? 0 : hyojihyo.Kikakucd9,
                        hyojihyo == null ? "" : hyojihyo.Kikakunaiyo9);

                if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                    ret.add(listRowData);
            }
            // ????????????
            else if (kikaku.Hyojijun == 10) {
                ListRowData listRowData = getKikakuListRowData(syukoDenpyo.Kkkkmnm10,
                        syukoDenpyo.Nyurkbn10,
                        syukoDenpyo.Syukosgyjokyo.Syukeicd,
                        syukoDenpyo.Kikakucd10,
                        syukoDenpyo.Kikakunaiyo10,
                        hyojihyo == null ? 0 : hyojihyo.Kikakucd10,
                        hyojihyo == null ? "" : hyojihyo.Kikakunaiyo10);

                if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                    ret.add(listRowData);
            }
            // ????????????
            else if (kikaku.Hyojijun == 11) {
                ListRowData listRowData = getKikakuListRowData(syukoDenpyo.Kkkkmnm11,
                        syukoDenpyo.Nyurkbn11,
                        syukoDenpyo.Syukosgyjokyo.Syukeicd,
                        syukoDenpyo.Kikakucd11,
                        syukoDenpyo.Kikakunaiyo11,
                        hyojihyo == null ? 0 : hyojihyo.Kikakucd11,
                        hyojihyo == null ? "" : hyojihyo.Kikakunaiyo11);

                if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                    ret.add(listRowData);
            }
            // ????????????
            else if (kikaku.Hyojijun == 12) {
                ListRowData listRowData = getKikakuListRowData(syukoDenpyo.Kkkkmnm12,
                        syukoDenpyo.Nyurkbn12,
                        syukoDenpyo.Syukosgyjokyo.Syukeicd,
                        syukoDenpyo.Kikakucd12,
                        syukoDenpyo.Kikakunaiyo12,
                        hyojihyo == null ? 0 : hyojihyo.Kikakucd12,
                        hyojihyo == null ? "" : hyojihyo.Kikakunaiyo12);

                if (currentDispMode == DispMode.SyukoDenpyo || listRowData.Checkresult == EnumClass.CheckKubun.NG)
                    ret.add(listRowData);
            }
        }

        return ret;
    }

    /**
     * ????????????
     *
     * @param date1 ?????????
     * @param date2 ?????????
     * @return ????????????????????????true, ???????????????false
     */
    private int CompareToDate(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            // ????????????null??????0
            return 0;
        }

        if (date1 == null || date2 == null) {
            // ????????????null??????1
            return 1;
        }

        //noinspection deprecation
        if (date1.getYear() == date2.getYear() && date1.getMonth() == date2.getMonth() && date1.getDay() == date2.getDay()) {
            // ?????????????????????????????????????????????0
            return 0;
        } else {
            // ???????????????1
            return 1;
        }
    }

    private ListRowData getKikakuListRowData(String komokName, int nyurKubun, long syukeicd, int syukoKikakuCode, String syukoKikakuNaiyo, int hyojihyoKikakuCode, String hyojihyoKikakuNaiyo) {
        ListRowData listRowData = new ListRowData();
        listRowData.Komokname = komokName;

        EnumClass.NyurKubun kubun = EnumClass.getNyurKubun(nyurKubun);

        if (kubun == null) return listRowData;

        final EnumClass.CheckKubun checkresult = syukoKikakuNaiyo.equals(hyojihyoKikakuNaiyo) ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
        switch (kubun) {
            case Code:

                listRowData.Syukodenpyo_dispvalue = syukoKikakuNaiyo;
                if (syukeicd > 0L) {
                    listRowData.Hyojihyo_dispvalue = hyojihyoKikakuNaiyo;
                    listRowData.Checkresult = syukoKikakuCode == hyojihyoKikakuCode ? EnumClass.CheckKubun.OK : EnumClass.CheckKubun.NG;
                }

                break;
            case Date:

                if (syukoKikakuNaiyo.length() == 8) {
                    listRowData.Syukodenpyo_dispvalue = String.format("%s???%s???%s???", syukoKikakuNaiyo.substring(0, 4), syukoKikakuNaiyo.substring(4, 6), syukoKikakuNaiyo.substring(6, 8));
                }

                if (syukeicd > 0L) {

                    if (hyojihyoKikakuNaiyo.length() == 8) {
                        listRowData.Syukodenpyo_dispvalue = String.format("%s???%s???%s???", hyojihyoKikakuNaiyo.substring(0, 4), hyojihyoKikakuNaiyo.substring(4, 6), hyojihyoKikakuNaiyo.substring(6, 8));
                    }

                    listRowData.Checkresult = checkresult;
                }

                break;
            case Free:

                listRowData.Syukodenpyo_dispvalue = syukoKikakuNaiyo;
                if (syukeicd > 0L) {
                    listRowData.Hyojihyo_dispvalue = hyojihyoKikakuNaiyo;
                    listRowData.Checkresult = checkresult;
                }

                break;
        }

        return listRowData;
    }


    //endregion

    //region ????????????????????????

    @Override
    public void onKeyRemapCreated() {

        // M1?????????F1?????????????????????
        mKeyRemapLibrary.setRemapKey(KeyRemapLibrary.KeyCode.KEY_CODE_M1.getValue(),
                KeyRemapLibrary.ScanCode.SCAN_CODE_F1.getString());

        // M2?????????F2?????????????????????
        mKeyRemapLibrary.setRemapKey(KeyRemapLibrary.KeyCode.KEY_CODE_M2.getValue(),
                KeyRemapLibrary.ScanCode.SCAN_CODE_F2.getString());

        // M3?????????F3?????????????????????
        mKeyRemapLibrary.setRemapKey(KeyRemapLibrary.KeyCode.KEY_CODE_M3.getValue(),
                KeyRemapLibrary.ScanCode.SCAN_CODE_F3.getString());

        // M4?????????F4?????????????????????
        mKeyRemapLibrary.setRemapKey(KeyRemapLibrary.KeyCode.KEY_CODE_M4.getValue(),
                KeyRemapLibrary.ScanCode.SCAN_CODE_F4.getString());

        // SCAN???????????????????????????????????????????????????
        mKeyRemapLibrary.setRemapKey(KeyRemapLibrary.KeyCode.KEY_CODE_CT.getValue(),
                KeyRemapLibrary.ScanCode.SCAN_CODE_TRIGGER_BARCODE.getString());

        // ????????????????????????????????????????????????????????????
        mKeyRemapLibrary.setRemapKey(KeyRemapLibrary.KeyCode.KEY_CODE_LT.getValue(),
                KeyRemapLibrary.ScanCode.SCAN_CODE_TRIGGER_BARCODE.getString());

        // ????????????????????????????????????????????????????????????
        mKeyRemapLibrary.setRemapKey(KeyRemapLibrary.KeyCode.KEY_CODE_RT.getValue(),
                KeyRemapLibrary.ScanCode.SCAN_CODE_TRIGGER_BARCODE.getString());

    }

    //endregion

    //region ????????????????????????

    @Override
    public void onBackPressed() {

        try {
            Intent intent = new Intent();
            intent.putExtra(getResources().getString(R.string.intent_key_sagyochu_syuko_denpyos), sagyochuSyukoDenpyos);
            intent.putExtra(getResources().getString(R.string.intent_key_selected_sagyochu_syuko_denpyo_index), selectedSagyochuSyukoDenpyoIndex);

            setResult(RESULT_OK, intent);
            finish();
        } catch (Exception exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //endregion
}
package com.example.sozax.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.sozax.R;
import com.example.sozax.bl.com.LoginInfo;
import com.example.sozax.bl.models.office.ROffice;
import com.example.sozax.bl.models.office.ROfficeMainte;
import com.example.sozax.bl.models.representative.RRepresentative;
import com.example.sozax.bl.models.representative.RRepresentativeMainte;
import com.example.sozax.bl.models.warehouse.RWarehouse;
import com.example.sozax.bl.models.warehouse.RWarehouseMainte;
import com.example.sozax.common.CommonActivity;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Date;

public class LoginActivity extends CommonActivity {

    //region Create

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        boolean b;
//        try {
//            String s = postWebAPI();
//            Gson gson = new Gson();
//            b = gson.fromJson(s,boolean.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        TensyoModel t;
//        try {
//            getWebAPI();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // バージョン名を表示
        PackageInfo pckInfo = null;

        try {
            pckInfo = getPackageManager().getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Controlsを初期化
        InitializeControls();

        final AutoCompleteTextView spnOffice = findViewById(R.id.txtOffice);
        final AutoCompleteTextView txtRepresentative = findViewById(R.id.txtRepresentative);
        final AutoCompleteTextView txtWarehouse = findViewById(R.id.txtWarehouse);

        // 事務所リストを取得
        ArrayList<ROffice> officeArrayList = new ROfficeMainte().GetOfficeArraySampleList();

        ArrayList<String> stringArrayList = new ArrayList<String>();
        for (ROffice rOffice: officeArrayList){
            stringArrayList.add(rOffice.getName());
        }

        // 事務所リストをスピナーにセット
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.dropdown_menu_popup_item,stringArrayList);
        spnOffice.setAdapter(arrayAdapter);



        // 担当者リストを取得
        ArrayList<RRepresentative> representativeArrayList = new RRepresentativeMainte().GetRepresentativeArraySampleList();

        ArrayList<String> stringArrayList2= new ArrayList<String>();
        for (RRepresentative representative: representativeArrayList){
            stringArrayList2.add(representative.getName());
        }

        // 担当者リストをスピナーにセット
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, R.layout.dropdown_menu_popup_item,stringArrayList2);
        txtRepresentative.setAdapter(arrayAdapter2);



        // 倉庫リストを取得
        ArrayList<RWarehouse> warehouseArrayList = new RWarehouseMainte().GetWarehouseArraySampleList();

        ArrayList<String> stringArrayList3= new ArrayList<String>();
        for (RWarehouse warehouse: warehouseArrayList){
            stringArrayList3.add(warehouse.getName());
        }

        // 倉庫リストをスピナーにセット
        ArrayAdapter<String> arrayAdapter3 = new ArrayAdapter<String>(this, R.layout.dropdown_menu_popup_item,stringArrayList3);
        txtWarehouse.setAdapter(arrayAdapter3);
    }

    //endregion

    //region Controlsを初期化

    private void InitializeControls()
    {
        final TextInputLayout spnOffice = findViewById(R.id.tilOffice);                   // 事務所
//        final Spinner spnRepresentative = findViewById(R.id.spnRepresentative);   // 担当者スピナー
//        final Spinner spnWarehouse = findViewById(R.id.spnWarehouse);             // 倉庫スピナー
        final Button btnStart = findViewById(R.id.btnStart);                      // 開始ボタン

//        spnOffice.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//
//            public void onItemClick(AdapterView parent,View view,int position,long id){
//
//                ROffice selectedOfficeValue = (ROffice) parent.getSelectedItem();
//
//                // ログイン情報が未登録の場合、
//                // 保存しているログイン情報と異なる場合、
//                // 前値と相違している場合は、事業所情報をログイン情報にセット＆担当者情報を取得
//                if (logininfo == null ||
//                        logininfo.getOfficeInfo() == null ||
//                        logininfo.getOfficeInfo().getCode() != selectedOfficeValue.getCode())
//                {
//                    // 今回選択した事業所をログイン情報にセット
//                    logininfo = new LoginInfo();
//                    logininfo.setOfficeInfo(selectedOfficeValue);
//
//                    // 担当者リストを取得
//                    ArrayList<RRepresentative> representativeArrayList = new RRepresentativeMainte().getRepresentativeArrayList(logininfo.getOfficeInfo().getCode());
//
//                    // 担当者リストをスピナーにセット
//                    ArrayAdapter<RRepresentative> arrayAdapter = new ArrayAdapter<RRepresentative>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item);
//                    arrayAdapter.addAll(representativeArrayList);
//                    //spnRepresentative.setAdapter(arrayAdapter);
//                }
//
//                // 担当者スピナーを 有効化
//                //spnRepresentative.setEnabled(true);
//
//                // 倉庫スピナーを 無効化
//                //spnWarehouse.setEnabled(false);
//
//                // 倉庫スピナーの 項目削除
//                //spnWarehouse.setAdapter(null);
//
//                // [開始]ボタンを無効化
//                btnStart.setEnabled(false);
//            }
//        });

        // 事務所スピナーの選択イベントを追加
//        spnOffice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//            //何も選択されなかった時の動作
//            @Override
//            public void onNothingSelected(AdapterView adapterView) {
//
//                // 担当者 , 倉庫 スピナーを 無効化
//                spnRepresentative.setEnabled(false);
//                spnWarehouse.setEnabled(false);
//
//                // 担当者 , 倉庫 スピナーの 項目削除
//                spnRepresentative.setAdapter(null);
//                spnWarehouse.setAdapter(null);
//
//                // [開始]ボタンを無効化
//                btnStart.setEnabled(false);
//            }
//
//            @Override
//            public void onItemSelected(AdapterView parent, View view, int position, long id) {
//
//                ROffice selectedOfficeValue = (ROffice) parent.getSelectedItem();
//
//                // ログイン情報が未登録の場合、
//                // 保存しているログイン情報と異なる場合、
//                // 前値と相違している場合は、事業所情報をログイン情報にセット＆担当者情報を取得
//                if (logininfo == null ||
//                    logininfo.getOfficeInfo() == null ||
//                    logininfo.getOfficeInfo().getCode() != selectedOfficeValue.getCode())
//                {
//                    // 今回選択した事業所をログイン情報にセット
//                    logininfo = new LoginInfo();
//                    logininfo.setOfficeInfo(selectedOfficeValue);
//
//                    // 担当者リストを取得
//                    ArrayList<RRepresentative> representativeArrayList = new RRepresentativeMainte().getRepresentativeArrayList(logininfo.getOfficeInfo().getCode());
//
//                    // 担当者リストをスピナーにセット
//                    ArrayAdapter<RRepresentative> arrayAdapter = new ArrayAdapter<RRepresentative>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item);
//                    arrayAdapter.addAll(representativeArrayList);
//                    spnRepresentative.setAdapter(arrayAdapter);
//                }
//
//                // 担当者スピナーを 有効化
//                spnRepresentative.setEnabled(true);
//
//                // 倉庫スピナーを 無効化
//                spnWarehouse.setEnabled(false);
//
//                // 倉庫スピナーの 項目削除
//                spnWarehouse.setAdapter(null);
//
//                // [開始]ボタンを無効化
//                btnStart.setEnabled(false);
//            }
//        });//

        // 担当者スピナーの選択イベントを追加

//        spnRepresentative.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//            public void onItemClick(AdapterView parent,View view,int position,long id){
//                RRepresentative selectedRepresentativeValue = (RRepresentative) parent.getSelectedItem();
//
//                // ログイン情報が未登録の場合、
//                // 保存しているログイン情報と異なる場合、
//                // 前値と相違している場合は、担当者情報をログイン情報にセット＆倉庫情報を取得
//                if (logininfo.getRepresentativeInfo() == null ||
//                        logininfo.getRepresentativeInfo().getCode() != selectedRepresentativeValue.getCode()) {
//
//                    // 今回選択した担当者をログイン情報にセット
//                    logininfo.setRepresentativeInfo(selectedRepresentativeValue);
//
//                    // 倉庫リストを取得
//                    ArrayList<RWarehouse> warehouseArrayList = new RWarehouseMainte().getWarehouseArrayList(logininfo.getOfficeInfo().getCode(),logininfo.getRepresentativeInfo().getCode());
//
//                    // 倉庫リストをスピナーにセット
//                    ArrayAdapter<RWarehouse> arrayAdapter = new ArrayAdapter<RWarehouse>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item);
//                    arrayAdapter.addAll(warehouseArrayList);
//                    spnWarehouse.setAdapter(arrayAdapter);
//                }
//
//                // 倉庫スピナーを 有効化
//                spnWarehouse.setEnabled(true);
//
//                // [開始]ボタンを無効化
//                btnStart.setEnabled(false);
//            }
//        });

//        spnRepresentative.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//            //何も選択されなかった時の動作
//            @Override
//            public void onNothingSelected(AdapterView adapterView) {
//
//                // 倉庫スピナーを 無効化
//                spnWarehouse.setEnabled(false);
//
//                // 倉庫スピナーの 項目削除
//                spnWarehouse.setAdapter(null);
//
//                // [開始]ボタンを無効化
//                btnStart.setEnabled(false);
//            }
//
//            @Override
//            public void onItemSelected(AdapterView parent, View view, int position, long id) {
//
//                if (spnRepresentative.isFocused() == false)
//                {
//                    return;
//                }
//
//                RRepresentative selectedRepresentativeValue = (RRepresentative) parent.getSelectedItem();
//
//                // ログイン情報が未登録の場合、
//                // 保存しているログイン情報と異なる場合、
//                // 前値と相違している場合は、担当者情報をログイン情報にセット＆倉庫情報を取得
//                if (logininfo.getRepresentativeInfo() == null ||
//                        logininfo.getRepresentativeInfo().getCode() != selectedRepresentativeValue.getCode()) {
//
//                    // 今回選択した担当者をログイン情報にセット
//                    logininfo.setRepresentativeInfo(selectedRepresentativeValue);
//
//                    // 倉庫リストを取得
//                    ArrayList<RWarehouse> warehouseArrayList = new RWarehouseMainte().getWarehouseArrayList(logininfo.getOfficeInfo().getCode(),logininfo.getRepresentativeInfo().getCode());
//
//                    // 倉庫リストをスピナーにセット
//                    ArrayAdapter<RWarehouse> arrayAdapter = new ArrayAdapter<RWarehouse>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item);
//                    arrayAdapter.addAll(warehouseArrayList);
//                    spnWarehouse.setAdapter(arrayAdapter);
//                }
//
//                // 倉庫スピナーを 有効化
//                spnWarehouse.setEnabled(true);
//
//                // [開始]ボタンを無効化
//                btnStart.setEnabled(false);
//            }
//        });

//        spnWarehouse.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//            public void onItemClick(AdapterView parent,View view,int position,long id){
//                RWarehouse selectedWarehouseValue = (RWarehouse)parent.getSelectedItem();
//
//                // ログイン情報が未登録の場合、
//                // 保存しているログイン情報と異なる場合、
//                // 前値と相違している場合は、倉庫情報をログイン情報にセット
//                if (logininfo.getWarehouseInfo() == null ||
//                        logininfo.getWarehouseInfo().getCode() != selectedWarehouseValue.getCode()) {
//
//                    // 今回選択した倉庫をログイン情報にセット
//                    logininfo.setWarehouseInfo(selectedWarehouseValue);
//                }
//
//                // [開始]ボタンを有効化
//                btnStart.setEnabled(true);
//            }
//        });

//        // 倉庫スピナーの選択イベントを追加
//        spnWarehouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//            //何も選択されなかった時の動作
//            @Override
//            public void onNothingSelected(AdapterView adapterView) { }
//
//            @Override
//            public void onItemSelected(AdapterView parent, View view, int position, long id) {
//
//                if (spnWarehouse.isFocused() == false)
//                {
//                    return;
//                }
//
//                RWarehouse selectedWarehouseValue = (RWarehouse)parent.getSelectedItem();
//
//                // ログイン情報が未登録の場合、
//                // 保存しているログイン情報と異なる場合、
//                // 前値と相違している場合は、倉庫情報をログイン情報にセット
//                if (logininfo.getWarehouseInfo() == null ||
//                        logininfo.getWarehouseInfo().getCode() != selectedWarehouseValue.getCode()) {
//
//                    // 今回選択した倉庫をログイン情報にセット
//                    logininfo.setWarehouseInfo(selectedWarehouseValue);
//                }
//
//                // [開始]ボタンを有効化
//                btnStart.setEnabled(true);
//            }
//        });
    }

    //endregion

    //region 開始ボタンをクリックして、メニュー画面に遷移

    public void btnLogin_Click(View view) {

        final AutoCompleteTextView spnOffice = findViewById(R.id.txtOffice);
        final AutoCompleteTextView txtRepresentative = findViewById(R.id.txtRepresentative);
        final AutoCompleteTextView txtWarehouse = findViewById(R.id.txtWarehouse);


                TextInputLayout tilOffice = findViewById(R.id.tilOffice);
                TextInputLayout tilRepresentative = findViewById(R.id.tilRepresentative);
                TextInputLayout tilWarehouse = findViewById(R.id.tilWarehouse);

                Boolean isErr = false;
                if(spnOffice.getText().toString().isEmpty())
                {
                    tilOffice.setError("事業所を選択して下さい");
                    isErr = true;
                }
                else
                {
                    tilOffice.setError("");
                }

                if(txtRepresentative.getText().toString().isEmpty())
                {
                    tilRepresentative.setError("担当者を選択して下さい");
                    isErr = true;
                }
                else
                {
                    tilRepresentative.setError("");
                }

                if(txtWarehouse.getText().toString().isEmpty())
                {
                    tilWarehouse.setError("倉庫を選択して下さい");
                    isErr = true;
                }
                else
                {
                    tilWarehouse.setError("");
                }

                if(isErr == true)
                {
                    Vibrate();
                    return;
                }
                
                logininfo = new LoginInfo();
                
//                ROffice tmpOffice = new ROffice();
//                tmpOffice.setCode(0);
//                tmpOffice.setName(spnOffice.getText().toString());
//                logininfo.setOfficeInfo(tmpOffice);
//
//                RRepresentative tmpRepresentative = new RRepresentative();
//                tmpRepresentative.setCode(0);
//                tmpRepresentative.setName(txtRepresentative.getText().toString());
//                logininfo.setRepresentativeInfo(tmpRepresentative);
//
//                RWarehouse tmpWarehouse = new RWarehouse();
//                tmpWarehouse.setCode(0);
//                tmpWarehouse.setName(txtWarehouse.getText().toString());
//                logininfo.setWarehouseInfo(tmpWarehouse);
//
//                logininfo.setWorkingday(new Date());

                // メニュー画面に遷移
                Intent intent = new Intent(getApplication(), MenuActivity.class);
                intent.putExtra("LOGININFO", logininfo);

                try {
                    startActivity(intent);
                }catch(Exception ex)
                {
                    ex.getMessage();
                }
    }

    //endregion

    //region お試し登録

//    private String CreateJSON()
//    {
//        TensyoModel tensyoModel = new TensyoModel();
//        tensyoModel.Tencd = 9999;
//        tensyoModel.Tennm = String.valueOf(9999) + "倉庫";
//
//        Gson gson = new Gson();
//        return gson.toJson(tensyoModel);
//    }
//
//    private  String postWebAPI() throws IOException {
//        final Map<String,String> httpHeaders = new LinkedHashMap<String,String>();
//        final String resultStr = doPost("http://192.168.244.181:52200/post/tensyo","UTF-8",httpHeaders,CreateJSON());
//
//        return resultStr;
//    }
//
//    private String CreateJSON2()
//    {
//        Gson gson = new Gson();
//        return gson.toJson(Integer.valueOf(1));
//    }
//
//    private  void getWebAPI() throws IOException {
//        final Map<String,String> httpHeaders = new LinkedHashMap<String,String>();
//        doPost2("http://192.168.244.181:52200/get/tensyo/1","UTF-8",httpHeaders);
//    }
//
//    public  String doPost(String url, String encoding,Map<String,String> headers, String json) throws IOException {
//        final okhttp3.MediaType mediaTypeJson = okhttp3.MediaType.parse("application/json; charset=" + encoding);
//
//        final RequestBody requestBody = RequestBody.create(mediaTypeJson, json);
//
//        final Request request = new Request.Builder()
//                .url(url)
//                .headers(Headers.of(headers))
//                .post(requestBody)
//                .build();
//
//        final OkHttpClient client = new OkHttpClient.Builder()
//                .build();
//
//        AsyncHttpRequest task = new AsyncHttpRequest(this);
//        final String resultStr = String.valueOf(task.execute(client.newCall(request)));
//        return resultStr;
//    }
//
//    public  void doPost2(String url, String encoding,Map<String,String> headers) throws IOException {
//        final okhttp3.MediaType mediaTypeJson = okhttp3.MediaType.parse("application/json; charset=" + encoding);
//
//        final Request request = new Request.Builder()
//                .url(url)
//                .headers(Headers.of(headers))
//                .build();
//
//        final OkHttpClient client = new OkHttpClient.Builder()
//                .build();
//
//        AsyncHttpRequest task = new AsyncHttpRequest(this);
//        Response response = null;
//
//        task.execute(client.newCall(request));
//    }
//
//    class AsyncHttpRequest extends AsyncTask<Call, Void, TensyoModel> {
//
//        private Activity mainActivity;
//
//        public AsyncHttpRequest(Activity activity) {
//
//            // 呼び出し元のアクティビティ
//            this.mainActivity = activity;
//        }
//
//        @Override
//        protected TensyoModel doInBackground(okhttp3.Call... call) {
//
//            TensyoModel ret = null;
//
//            try {
//                String s2 = call[0].execute().body().string();
//
//                TensyoModel tmp = new TensyoModel();
//                tmp.Tencd = 1;
//                tmp.Tennm = "あいう";
//
//                Gson gson = new Gson();
//
//                String s1 = gson.toJson(tmp,TensyoModel.class);
//                //String s2 =body.string();
//
//                        ret = gson.fromJson(s1,TensyoModel.class);
//                ret = gson.fromJson(s2,TensyoModel.class);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return  ret;
//        }
//    }

    //endregion

    //region お試し取得



    //endregion

    //region DENSO固有ボタンの設定

    @Override
    public void onKeyRemapCreated() {
    }

    //endregion
}
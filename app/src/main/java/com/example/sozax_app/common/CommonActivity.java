package com.example.sozax_app.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.densowave.bhtsdk.keyremap.KeyRemapLibrary;
import com.example.sozax_app.R;
import com.example.sozax_app.bl.models.login_info.LoginInfoModel;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.example.sozax_app.common.CommonFunction.substringByBytes;

public abstract class CommonActivity extends AppCompatActivity implements KeyRemapLibrary.KeyRemapListener{

    //region インスタンス変数

    /**
     * ログイン情報
     */
    public LoginInfoModel loginInfo;

    // キー割り当てライブラリ(DENSO製)
    public KeyRemapLibrary mKeyRemapLibrary;

    public static final String dialogTag  = "DIALOGTAG";

    //endregion

    //region 初回起動

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mKeyRemapLibrary = new KeyRemapLibrary();
        mKeyRemapLibrary.createKeyRemap(this, this);

        // ログイン情報を取得
        Intent intent = getIntent();
        loginInfo = (LoginInfoModel) intent.getSerializableExtra(getResources().getString(R.string.intent_key_login_info));
    }

    //endregion

    //region ログイン情報を表示

    // ログイン情報を表示
    public void DisplayLoginInfo() {
        TextView txtLoginTensyo = findViewById(R.id.txtLoginTensyo);
        txtLoginTensyo.setText(substringByBytes(loginInfo.Tensyonm, 10));

        TextView txtLoginSgytanto = findViewById(R.id.txtLoginSgytanto);
        txtLoginSgytanto.setText(substringByBytes(loginInfo.Sgytantonm, 10));

        TextView txtLoginSouko = findViewById(R.id.txtLoginSouko);
        txtLoginSouko.setText(substringByBytes(loginInfo.Soukornm, 10));

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("M/dd(E)", DateFormatSymbols.getInstance(Locale.JAPAN));
        TextView txtLoginSgydate = findViewById(R.id.txtLoginSgydate);
        txtLoginSgydate.setText(sdf.format(loginInfo.Sgydate));
    }

    //endregion

    //region ログイン情報を長押しクリック

    public class clLoginInfo_LongClick implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {

            StringBuilder sb = new StringBuilder();
            sb.append("事業所:");
            sb.append(loginInfo.Tensyonm);
            sb.append("\r\n");
            sb.append("担当者:");
            sb.append(loginInfo.Sgytantonm);
            sb.append("\r\n");
            sb.append("倉　庫:");
            sb.append(loginInfo.Soukornm);
            sb.append("\r\n");
            sb.append("作業日:");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("M/dd(E)", DateFormatSymbols.getInstance(Locale.JAPAN));
            sb.append(sdf.format(loginInfo.Sgydate));

            Toast toast = Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            return false;
        }
    }

    //endregionf

    //region アプリ終了ボタンの処理

    public class btnExit_Click implements View.OnClickListener {

        private final Activity activity;
        private final onPositiveButtonClieckListener _onPositiveButtonClieckListener = new onPositiveButtonClieckListener();

        public btnExit_Click(Activity _activity) {
            activity = _activity;
        }

        @Override
        public void onClick(View v) {

            OutputConfirmationMessage("アプリを終了してもよろしいですか？", _onPositiveButtonClieckListener);

        }
    }

    private class onPositiveButtonClieckListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            finishAndRemoveTask();
        }
    }

    //endregion

    //region 振動する

    public void Vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] vibratePattern = {0, 500, 100, 500};
        vibrator.vibrate(vibratePattern, -1); // ここの-1を1にするとリピートします。
    }

    //endregion

    //region エラーメッセージを出力する

    /**
     * エラーを出力する
     *
     * @param errorMessage エラーメッセージ
     */
    public void OutputErrorMessage(String errorMessage) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        // 古いダイアログフラグメントを削除
        Fragment oldFragment = fragmentManager.findFragmentByTag(dialogTag);
        if(oldFragment != null)
        {
            fragmentManager.beginTransaction().remove(oldFragment).commit();
        }

        // 新しいダイアログフラグメントを追加
        ErrorDialogFragment newFragment = new ErrorDialogFragment();

        // メッセージをセット
        newFragment.setMessage(errorMessage);

        // 出力
        newFragment.showNow(fragmentManager, dialogTag);
    }

    /**
     * エラーを出力する
     *
     * @param errorMessage エラーメッセージ
     */
    public void OutputErrorMessage(String errorMessage, DialogInterface.OnDismissListener dismissListener) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        // 古いダイアログフラグメントを削除
        Fragment oldFragment = fragmentManager.findFragmentByTag(dialogTag);
        if(oldFragment != null)
        {
            fragmentManager.beginTransaction().remove(oldFragment).commit();
        }

        // 新しいダイアログフラグメントを追加
        ErrorDialogFragment newFragment = new ErrorDialogFragment();

        // メッセージをセット
        newFragment.setMessage(errorMessage);

        // 出力
        newFragment.showNow(fragmentManager, dialogTag);

        // Dismissリスナーをセット
        newFragment.setOnDismissListener(dismissListener);

        // 出力
        newFragment.showNow(fragmentManager, dialogTag);
    }

    public static class ErrorDialogFragment extends DialogFragment {

        //region インスタンス変数

        /**
         * エラーメッセージ
         */
        private String ErrorMessage;

        /**
         * Dismissリスナー
         */
        private DialogInterface.OnDismissListener onDismissListener;

        //endregion

        //region コンストラクタ

        public ErrorDialogFragment() {

        }

        //endregion

        //region ダイアログ作成

        @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            TextView titleView = new TextView(getActivity());
            titleView.setText("エラー");
            titleView.setTextSize(20);
            titleView.setTypeface(Typeface.DEFAULT_BOLD);
            //noinspection deprecation
            titleView.setTextColor(getResources().getColor(R.color.white));
            //noinspection deprecation
            titleView.setBackgroundColor(getResources().getColor(R.color.black));
            titleView.setPadding(20, 20, 20, 20);
            titleView.setLeft(20);
            titleView.setGravity(Gravity.FILL_VERTICAL);
            titleView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_error_32, 0, 0, 0);
            titleView.setCompoundDrawablePadding(10);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCustomTitle(titleView);
            builder.setMessage(ErrorMessage);
            builder.setPositiveButton("OK", null);

            if (onDismissListener != null) {
                builder.setOnDismissListener(onDismissListener);
            }

            return builder.create();
        }

        //endregion

        //region 休止

        @Override
        public void onPause() {
            super.onPause();

            // onPause でダイアログを閉じる場合
            dismiss();
        }

        //endregion

        //region メッセージ追加

        public void setMessage(String message) {
            ErrorMessage = message;
        }

        //endregion

        //region Dissmissリスナーを追加

        public void setOnDismissListener(DialogInterface.OnDismissListener _onDismissListener) {
            onDismissListener = _onDismissListener;
        }

        //endregion
    }

    //endregion

    //region お知らせメッセージを出力する

    /**
     * お知らせを出力する
     *
     * @param informationMessage お知らせメッセージ
     */
    public void OutputInformationMessage(String informationMessage) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        // 古いダイアログフラグメントを削除
        Fragment oldFragment = fragmentManager.findFragmentByTag(dialogTag);
        if(oldFragment != null)
        {
            fragmentManager.beginTransaction().remove(oldFragment).commit();
        }

        // 新しいダイアログフラグメントを追加
        InformationDialogFragment newFragment = new InformationDialogFragment();

        // メッセージをセット
        newFragment.setMessage(informationMessage);

        // 出力
        newFragment.showNow(fragmentManager, dialogTag);
    }

    /**
     * お知らせダイアログ
     */
    public static class InformationDialogFragment extends DialogFragment {

        //region インスタンス変数

        /**
         * お知らせメッセージ
         */
        private String InformationMessage;

        //endregion

        //region コンストラクタ

        public InformationDialogFragment() {

        }

        //endregion

        //region ダイアログ作成

        @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            TextView titleView = new TextView(getActivity());
            titleView.setText("お知らせ");
            titleView.setTextSize(20);
            titleView.setTypeface(Typeface.DEFAULT_BOLD);
            //noinspection deprecation
            titleView.setTextColor(getResources().getColor(R.color.white));
            //noinspection deprecation
            titleView.setBackgroundColor(getResources().getColor(R.color.black));
            titleView.setPadding(20, 20, 20, 20);
            titleView.setLeft(20);
            titleView.setGravity(Gravity.FILL_VERTICAL);
            titleView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_info_32, 0, 0, 0);
            titleView.setCompoundDrawablePadding(10);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCustomTitle(titleView);
            builder.setMessage(InformationMessage);
            builder.setPositiveButton("OK", null);

            return builder.create();
        }

        //endregion

        //region 休止

        @Override
        public void onPause() {
            super.onPause();

            // onPause でダイアログを閉じる場合
            dismiss();
        }

        //endregion

        //region メッセージ追加

        public void setMessage(String message) {
            InformationMessage = message;
        }

        //endregion
    }

    //endregion

    //region 確認メッセージを出力する

    /**
     * 確認メッセージを出力する
     *
     * @param confirmationMessage 確認メッセージ
     */
    public void OutputConfirmationMessage(String confirmationMessage) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        // 古いダイアログフラグメントを削除
        Fragment oldFragment = fragmentManager.findFragmentByTag(dialogTag);
        if(oldFragment != null)
        {
            fragmentManager.beginTransaction().remove(oldFragment).commit();
        }

        // 新しいダイアログフラグメントを追加
        ConfirmationDialogFragment newFragment = new ConfirmationDialogFragment();

        // メッセージをセット
        newFragment.setMessage(confirmationMessage);

        // 出力
        newFragment.showNow(fragmentManager,dialogTag);
    }

    /**
     * 確認メッセージを出力する
     *
     * @param confirmationMessage 確認メッセージ
     */
    public void OutputConfirmationMessage(String confirmationMessage, DialogInterface.OnClickListener onPositiveButtonClieckListener) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        // 古いダイアログフラグメントを削除
        Fragment oldFragment = fragmentManager.findFragmentByTag(dialogTag);
        if(oldFragment != null)
        {
            fragmentManager.beginTransaction().remove(oldFragment).commit();
        }

        // 新しいダイアログフラグメントを追加
        ConfirmationDialogFragment newFragment = new ConfirmationDialogFragment();

        // メッセージをセット
        newFragment.setMessage(confirmationMessage);

        // ポジティブボタンクリックリスナーをセット
        if (onPositiveButtonClieckListener != null) {
            newFragment.setOnPositiveButtonClieckListener(onPositiveButtonClieckListener);
        }

        // 出力
        newFragment.showNow(fragmentManager, dialogTag);
    }

    /**
     * 確認ダイアログ
     */
    public static class ConfirmationDialogFragment extends DialogFragment {

        /**
         * 確認メッセージ
         */
        private String ConfirmationMessage;

        /**
         * Dismissリスナー
         */
        private DialogInterface.OnClickListener onPositiveButtonClieckListener;

        //region コンストラクタ

        public ConfirmationDialogFragment() {

        }

        //endregion

        //region ダイアログ作成

        @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            TextView titleView = new TextView(getActivity());
            titleView.setText("確認");
            titleView.setTextSize(20);
            titleView.setTypeface(Typeface.DEFAULT_BOLD);
            //noinspection deprecation
            titleView.setTextColor(getResources().getColor(R.color.white));
            //noinspection deprecation
            titleView.setBackgroundColor(getResources().getColor(R.color.black));
            titleView.setPadding(20, 20, 20, 20);
            titleView.setLeft(20);
            titleView.setGravity(Gravity.FILL_VERTICAL);
            titleView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_question_32, 0, 0, 0);
            titleView.setCompoundDrawablePadding(10);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCustomTitle(titleView);
            builder.setMessage(ConfirmationMessage);
            builder.setPositiveButton("OK", onPositiveButtonClieckListener);
            builder.setNegativeButton("CANCEL", null);

            return builder.create();
        }

        //endregion

        //region 休止

        @Override
        public void onPause() {
            super.onPause();

            // onPause でダイアログを閉じる場合
            dismiss();
        }

        //endregion

        //region メッセージ追加

        public void setMessage(String message) {
            ConfirmationMessage = message;
        }

        //endregion

        //region PositiveButtonClieckリスナーを追加

        public void setOnPositiveButtonClieckListener(DialogInterface.OnClickListener _onPositiveButtonClieckListener) {
            onPositiveButtonClieckListener = _onPositiveButtonClieckListener;
        }

        //endregion
    }

    //endregion

    //region タイトルなしメッセージを出力する

    /**
     * メッセージを出力する
     *
     * @param message メッセージ
     */
    public void OutputNoTitleMessage(String message) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        // 古いダイアログフラグメントを削除
        Fragment oldFragment = fragmentManager.findFragmentByTag(dialogTag);
        if(oldFragment != null)
        {
            fragmentManager.beginTransaction().remove(oldFragment).commit();
        }

        // 新しいダイアログフラグメントを追加
        NoTitleDialogFragment newFragment = new NoTitleDialogFragment();

        // メッセージをセット
        newFragment.setMessage(message);

        // 出力
        newFragment.showNow(fragmentManager, dialogTag);
    }

    /**
     * タイトルなしダイアログ
     */
    public static class NoTitleDialogFragment extends DialogFragment {

        /**
         * メッセージ
         */
        private String Message;

         //region コンストラクタ

        public NoTitleDialogFragment() {

        }

        //endregion

        //region ダイアログ作成

        @NotNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(Message);
            builder.setPositiveButton("OK", null);

            return builder.create();
        }

        //endregion

        //region 休止

        @Override
        public void onPause() {
            super.onPause();

            // onPause でダイアログを閉じる場合
            dismiss();
        }

        //endregion

        //region メッセージ追加

        public void setMessage(String message) {
            Message = message;
        }

        //endregion
    }

    //endregion

    //region 操作を有効・無効化する

    // ハードウェアキー無効化フラグ
    private boolean isHardwareKeyDisabled = false;

    /**
     * 操作を有効・無効化
     */
    public void setEnabledOperation(boolean isEnabled) {

        if (isEnabled) {

            // ハードウェアキーを有効化
            isHardwareKeyDisabled = false;

            // タッチ操作を有効化
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            // プログレスバーを非表示
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);

        } else {

            // ハードウェアキーを無効化
            isHardwareKeyDisabled = true;

            // タッチ操作を無効化
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            // プログレスバーを表示
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);

        }

    }

    // ハードウェアキー押下イベント
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {

        if (isHardwareKeyDisabled) {
            return true;
        }

        return super.dispatchKeyEvent(e);
    }

    //endregion

    // region 画面終了時

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FragmentManager fragmentManager = getSupportFragmentManager();

        // ダイアログフラグメントを破棄
        Fragment fragment = fragmentManager.findFragmentByTag(dialogTag);
        if(fragment != null) fragment.onDestroy();

        // KeyRemapLibrary クラスのインスタンスを解放
        mKeyRemapLibrary.disposeKeyRemap();
    }

    // endregion
}
package com.example.sozax.common;

import android.os.Bundle;
import android.util.Log;

import com.densowave.bhtsdk.barcode.BarcodeException;
import com.densowave.bhtsdk.barcode.BarcodeManager;
import com.densowave.bhtsdk.barcode.BarcodeScanner;
import com.densowave.bhtsdk.barcode.BarcodeScannerSettings;
import com.densowave.bhtsdk.barcode.DataEditingSettings;
import com.densowave.bhtsdk.barcode.DecodeSettings;
import com.densowave.bhtsdk.barcode.NotificationSettings;
import com.densowave.bhtsdk.barcode.ScanSettings;

import java.nio.charset.Charset;
import java.util.List;

public abstract class ScannerActivity extends CommonActivity implements BarcodeManager.BarcodeManagerListener, BarcodeScanner.BarcodeDataListener {

    //region インスタンス変数

    private final String TAG = "ScannerActivity";
    private boolean isClaimed = false;

    private BarcodeManager mBarcodeManager = null;
    public BarcodeScanner mBarcodeScanner = null;

    //endregion

    //region 初回起動時

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // マネージャーを作成
            BarcodeManager.create(this, this);
        } catch (BarcodeException e) {
            Log.e(TAG, "Error Code = " + e.getErrorCode(), e);
        }
    }

    //endregion

    //region 再開時

    @Override
    protected void onResume() {
        super.onResume();

        // 読み取り許可
        ScanClaim();
    }

    // 読み取り許可
    public void ScanClaim() {
        try {
            if (mBarcodeScanner != null && !isClaimed) {

                // 再開時に、バーコードスキャナの読取を許可
                mBarcodeScanner.claim();
                isClaimed = true;
            }
        } catch (BarcodeException e) {
            Log.e(TAG, "Error Code = " + e.getErrorCode(), e);
        }
    }

    //endregion

    //region 休止時

    @Override
    protected void onPause() {
        super.onPause();

        // 読み取り禁止
        ScanClose();
    }

    // 読み取り禁止
    public void ScanClose() {
        try {
            if (mBarcodeScanner != null && !isClaimed) {

                // 休止時に、バーコードスキャナの読取を禁止
                mBarcodeScanner.close();
                isClaimed = false;
            }
        } catch (BarcodeException e) {
            Log.e(TAG, "Error Code = " + e.getErrorCode(), e);
        }
    }

    //endregion

    //region 終了時

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 終了時に、スキャナのインスタンスを破棄する
        if (mBarcodeScanner != null) {
            try {
                mBarcodeScanner.destroy();
                isClaimed = false;
            } catch (BarcodeException e) {
                Log.e(TAG, "Error Code = " + e.getErrorCode(), e);
            }
        }

        // 終了時に、マネージャーのインスタンスを破棄する
        if (mBarcodeManager != null) {
            // Remove Scanner Manager
            mBarcodeManager.destroy();
        }
    }

    //endregion

    //region バーコードマネージャー作成後

    @Override
    public void onBarcodeManagerCreated(BarcodeManager barcodeManager) {

        mBarcodeManager = barcodeManager;

        try {
            List<BarcodeScanner> listScanner = mBarcodeManager.getBarcodeScanners();
            if (listScanner.size() > 0) {

                // スキャナのインスタンスを取得
                mBarcodeScanner = listScanner.get(0);

                // バーコードデータ受取後のイベントを登録
                mBarcodeScanner.addDataListener(this);

                // スキャナの設定を行う
                setScanner();

                // 設定した内容に従って、バーコードスキャナの読取を許可
                ScanClaim();

            }
        } catch (BarcodeException e) {
            Log.e(TAG, "Error Code = " + e.getErrorCode(), e);
        }

    }

    //endregion

    //region スキャナーの設定

    private void setScanner() {

        BarcodeScannerSettings settings = new BarcodeScannerSettings();

        //region scan(読み取り動作関連)

        // トリガーモード：オートオフモード
        settings.scan.triggerMode = ScanSettings.TriggerMode.AUTO_OFF;

        // 照明モード：自動
        settings.scan.lightMode = ScanSettings.LightMode.AUTO;

        // マーカモード：ノーマル
        settings.scan.markerMode = ScanSettings.MarkerMode.NORMAL;

        /// 補助照明：オフ（２次元バーコードスキャナでは、サポート対象外）
        //settings.scan.sideLightMode = ScanSettings.SideLightMode.OFF;

        //endregion

        //region notification(読み取り通知関連)

        //region notification.sound(読み取り通知音関連)

        // 読取通知音：有効
        settings.notification.sound.enabled = true;

        // オーディオ属性：着信音
        settings.notification.sound.usageType = NotificationSettings.UsageType.RINGTONE;

        // 読取成功音：デフォルト音（ブザー音）
        settings.notification.sound.goodDecodeFilePath = "";

        //endregion

        //region notification.vibrate(読み取り通知バイブレータ関連)

        // 読取通知バイブレータ：有効
        settings.notification.vibrate.enabled = true;

        //endregion

        //region notification.led(読み取り通知LED関連)

        // 読取通知LED：有効
        settings.notification.led.enabled = true;

        //endregion

        //endregion

        //region decode(デコード関連)

        // 二度読み防止解除時間：10ms
        settings.decode.sameBarcodeIntervalTime = 10;

        // デコードレベル：4
        settings.decode.decodeLevel = 4;

        // 白黒反転読取：禁止
        settings.decode.invertMode = DecodeSettings.InvertMode.DISABLED;

        // ポイントスキャンモード：ノーマル
        settings.decode.pointScanMode = DecodeSettings.PointScanMode.DISABLED;

        // 表裏反転読取：禁止
        settings.decode.reverseMode = DecodeSettings.ReverseMode.DISABLED;

        // バーコードのエンコーディング方式
        settings.decode.charset = Charset.forName("UTF-8");

        //region decode.multiLineMode(多段コード読み取り関連)

        //endregion

        //region decode.multiLineMode.symbologyy1st/2nd/3rd(１段目/２段目/３段目の読取コード情報)

        //endregion

        //endregion

        //region editing(読み取ったデータの編集関連)

        //region editing.ean13(読み取ったEAN-13コードの編集関連)

        // チェックデジット付加：付加しない
        settings.editing.ean13.reportCheckDigit = false;

        //endregion

        //region editing.upcA(読み取ったUPC-Aコードの編集関連)

        // チェックデジット付加：付加しない
        settings.editing.upcA.reportCheckDigit = false;

        // 転送桁数調整用先頭キャラクタ付加
        settings.editing.upcA.addLeadingZero = false;

        //endregion

        //region editing.ean8(読み取ったEAN-8コードの編集関連)

        // チェックデジット付加：付加しない
        settings.editing.ean8.reportCheckDigit = false;

        // EAN-13への変換
        settings.editing.ean8.convertToEan13 = false;

        //endregion

        //region editing.upcE(読み取ったUPC-Eコードの編集関連)

        // チェックデジット付加：付加しない
        settings.editing.upcE.reportCheckDigit = false;

        // 転送桁数調整用先頭キャラクタ付加
        settings.editing.upcE.addLeadingZero = false;

        // UPC-Aへの変換
        settings.editing.upcE.convertToUpcA = false;

        // UPC-Aへ変換した場合のナンバーシステム付加
        settings.editing.upcE.reportNumberSystemCharacterOfConvertedUpcA = false;

        //endregion

        //region editing.code39(読み取ったCode39コードの編集関連)

        // チェックデジット付加：付加しない
        settings.editing.code39.reportCheckDigit = false;

        // スタートストップ付加
        settings.editing.code39.reportStartStopCharacter = false;

        //endregion

        //region editing.codabar(読み取ったCodabarコードの編集関連)

        // チェックデジット付加：付加しない
        settings.editing.codabar.reportCheckDigit = false;

        // スタートストップ付加
        settings.editing.codabar.reportStartStopCharacter = false;

        // スタートストップの大文字変換
        settings.editing.codabar.convertToUpperCase = false;

        //endregion

        //region editing.itf(読み取ったITFコードの編集関連)

        // チェックデジット付加：付加しない
        settings.editing.itf.reportCheckDigit = false;

        //endregion

        //region editing.stf(読み取ったSTFコードの編集関連)

        // チェックデジット付加：付加しない
        settings.editing.stf.reportCheckDigit = false;

        //endregion

        //region editing.sqrc(読み取ったSQRCコードの編集関連)

        // SQRC 出力データ部：公開部と非公開部
        settings.editing.sqrc.correctKeyDecode = DataEditingSettings.CorrectKeyDecode.PUBLIC_AND_PRIVATE_DATA;

        // SQRC 暗号キー不一致時の出力データ部：出力無し
        settings.editing.sqrc.incorrectKeyDecode = DataEditingSettings.IncorrectKeyDecode.NONE;

        //endregion

        //endregion

        //region output.intent(Intent通知設定関連)

        // 通知設定の有無：通知しない
        settings.output.intent.enabled = false;

        // パッケージ名：指定なし
        settings.output.intent.packageName = "";

        // クラス名：指定なし
        settings.output.intent.className = "";

        // アクション名：指定なし
        settings.output.intent.actionName = "";

        //endregion

        // 読取関連の設定を設定
        mBarcodeScanner.setSettings(settings);

    }

    //endregion

    //region 操作を有効・無効化する

    /**
     * 操作を有効・無効化
     */
    @Override
    public void setEnabledOperation(boolean isEnabled) {

        super.setEnabledOperation(isEnabled);

        try {

            if (isEnabled) {

                // バーコードスキャナの読取を禁止
                ScanClose();

            } else {

                // バーコードスキャナの読取を許可
                ScanClaim();

            }

        } catch (Exception exception) {
            OutputErrorMessage(exception.getMessage());
        }
    }

    //endregion
}
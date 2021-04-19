package com.example.sozax.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.densowave.bhtsdk.keyremap.KeyRemapLibrary;
import com.example.sozax.R;
import com.example.sozax.bl.models.login_info.LoginInfoModel;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.BiFunction;

public abstract class CommonFunction {
    /**
     * @param text    対象となる文字列
     * @param index   切り出すバイト数
    // * @param charset 文字コード
     * @return
     */
    public static String substringByBytes(String text, int index) {

        String ret = "";

        try {

            int textByteCnt = 0;
            for (int i = 0; i < text.length(); i++) {

                //対象となる文字列を先頭から1文字切り出し、その文字のバイト数を調べます。
                String tmpText = text.substring(i, i + 1);
                byte[] tmpTextByte = tmpText.getBytes("Shift_JIS");

                //切り出した文字を変数retに追加した際のバイト数が指定バイト数より大きければ、変数retを返します。
                if (textByteCnt + tmpTextByte.length > index) {
                    return ret;
                } else {
                    ret += tmpText;
                    textByteCnt = textByteCnt + tmpTextByte.length;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }

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

    /**
     *  重量をトン単位からキロ単位に変換する。(1000掛ける)
     *
     * @param j 重量
     * @return r 計算結果
     */
    public static BigDecimal multiplyThousand (BigDecimal j)
    {
        // 戻り値
        BigDecimal r;

        BigDecimal thousand = BigDecimal.valueOf(1000);

        // 計算
        r = j.multiply(thousand);

        return r;
    }

    /**
     *  日付のフォーマットを設定する。
     *
     * @param d 日付
     * @param f フォーマット
     * @return r フォーマット設定した日付
     */
    public static String settingDateFormat(Date d, String f)
    {
        // 戻り値
        String r;
        // フォーマット
        SimpleDateFormat dateFormat = new SimpleDateFormat(f);

        r = dateFormat.format(d);

        return r;
    }
}

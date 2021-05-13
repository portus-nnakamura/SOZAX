package com.example.sozax.bl.models.login_info;

import java.io.Serializable;
import java.util.Date;

public class LoginInfoModel implements Serializable {

    // 会社コード(固定)
    public int Kaicd = 40;

    // 店所コード
    public int Tensyocd = 0;
    // 店所名
    public String Tensyonm = "";

    // 作業担当者コード
    public int Sgytantocd = 0;
    // 作業担当者名
    public String Sgytantonm = "";

    // 倉庫コード
    public int Soukocd = 0;
    // 倉庫名
    public  String Soukonm = "";
    // 倉庫略名
    public  String Soukornm = "";

    // 作業日
    public Date Sgydate = null;

    // 更新日時
    public  Date Updatedate  = null;

}

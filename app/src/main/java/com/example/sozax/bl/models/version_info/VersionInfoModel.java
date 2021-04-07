package com.example.sozax.bl.models.version_info;

import com.example.sozax.common.ResultClass;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VersionInfoModel extends ResultClass {

    // バージョンコード
    //@SerializedName("versioncd")
    @Expose
    public int Versioncd = 0;

    // バージョン名
    //@SerializedName("versionnm")
    @Expose
    public String Versionnm = "";

}
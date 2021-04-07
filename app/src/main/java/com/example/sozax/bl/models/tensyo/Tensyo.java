package com.example.sozax.bl.models.tensyo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tensyo {

    // 店所コード
    @SerializedName("tencd")
    @Expose
    public int Tencd = 0;

    // 店所名
    @SerializedName("tennm")
    @Expose
    public String Tennm = "";
}
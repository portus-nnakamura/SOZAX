package com.example.sozax.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultClass {

    // エラーフラグ
    //@SerializedName("is_error")
    @Expose
    public boolean Is_error = false;

    // メッセージ
    //@SerializedName("message")
    @Expose
    public String Message = "";

}
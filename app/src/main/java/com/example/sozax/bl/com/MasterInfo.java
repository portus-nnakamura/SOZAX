package com.example.sozax.bl.com;

import java.io.Serializable;

public class MasterInfo implements Serializable {

    // コード
    private int Code = 0;
    public int getCode(){return Code;}
    public void setCode(int setCode) {
        this.Code = setCode;
    }

    // 名称
    private String Name = "";
    public String getName(){return Name;}
    public void setName(String setName) { this.Name = setName; }
}

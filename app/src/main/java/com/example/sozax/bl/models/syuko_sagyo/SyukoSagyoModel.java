package com.example.sozax.bl.models.syuko_sagyo;

import com.example.sozax.bl.models.hyojihyo.HyojihyoModel;
import com.example.sozax.common.ResultClass;

import java.io.Serializable;
import java.util.Date;

public class SyukoSagyoModel implements Serializable {

    public long Sgysyukono = 0L;

    public int Kaicd = 0;

    public int Sgytencd = 0;

    public int Sgytantocd = 0;

    public String Sgytantonm = "";

    public int Sgysoukocd = 0;

    public Date Sgydate = null;

    public int Sgyjokyokbn = 0;

    public long Syukeicd = 0L;

    public HyojihyoModel HyojiHyo = null;
}

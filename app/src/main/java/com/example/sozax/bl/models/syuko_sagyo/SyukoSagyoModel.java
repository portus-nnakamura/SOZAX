package com.example.sozax.bl.models.syuko_sagyo;

import com.example.sozax.bl.models.hyojihyo.HyojiHyoModel;
import com.example.sozax.common.ResultClass;

import java.util.Date;

public class SyukoSagyoModel  {

    public long Syukono = 0L;

    public int Kaicd = 0;

    public int Tencd = 0;

    public int Sgytantocd = 0;

    public String Sgytantonm = "";

    public int Soukocd = 0;

    public Date Sgydate = new Date(Long.MIN_VALUE);

    public int Sgyjyokyokbn = 0;

    public long Syukeicd = 0;

    public HyojiHyoModel HyojiHyo = null;
}

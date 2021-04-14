package com.example.sozax.bl.models.zaiko_syokai;

import com.example.sozax.common.ResultClass;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ZaikoSyokaiModel extends ResultClass implements Serializable {

    public long Syukeicd = 0L;

    public int Sykdomecd = 0;

    public String Sykdomenm = "";

    public int Ninusicd = 0;

    public String Ninusinm = "";

    public Date Nyukodate = null;

    public int Nyukosyudankbn = 0;

    public int Sdosecd = 0;

    public Date Sdosenyuymd = null;

    public int Funecd = 0;

    public String Funenm = "";

    public int Hinbuncd = 0;

    public String Hinbunnm = "";

    public int Hinsyucd = 0;

    public String Hinsyunm = "";

    public int Hinmeicd = 0;

    public String Hinmeinm = "";

    public String Kikaku = "";

    public BigDecimal Tanjuryo = BigDecimal.ZERO;

    public int Nisucd = 0;

    public String Nisunm = "";

    public int Nijicd = 0;

    public String Nijinm = "";

    public ZaikoSyokai_NyusyukkoRirekiModel[] Nyusyukkorireki = null;


}

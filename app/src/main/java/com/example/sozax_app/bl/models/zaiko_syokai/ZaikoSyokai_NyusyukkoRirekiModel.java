package com.example.sozax_app.bl.models.zaiko_syokai;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ZaikoSyokai_NyusyukkoRirekiModel implements Serializable {

    public Date Ukehridate = new Date(Long.MIN_VALUE);

    public BigDecimal Nyuko_kosuu = BigDecimal.ZERO;

    public BigDecimal Nyuko_Juryo = BigDecimal.ZERO;

    public BigDecimal Syukko_kosuu = BigDecimal.ZERO;

    public BigDecimal Syukko_Juryo = BigDecimal.ZERO;

    public BigDecimal Zan_kosu = BigDecimal.ZERO;

    public BigDecimal Zan_juryo = BigDecimal.ZERO;
}

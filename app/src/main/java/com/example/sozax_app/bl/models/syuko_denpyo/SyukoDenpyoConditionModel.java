package com.example.sozax_app.bl.models.syuko_denpyo;

import java.io.Serializable;
import java.util.Date;

public class SyukoDenpyoConditionModel implements Serializable {

    public long Syukono = 0L;

    public int Kaicd = 0;

    public int Soukocd = 0;

    public Date Sagyodate = new Date(Long.MIN_VALUE);

    public int Sgytantocd = 0;
}

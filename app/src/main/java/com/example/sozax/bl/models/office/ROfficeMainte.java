package com.example.sozax.bl.models.office;

import java.util.ArrayList;

public class ROfficeMainte {

    public ArrayList<ROffice> GetOfficeArrayList()
    {
        ArrayList<ROffice> ret = new ArrayList<ROffice>();

        final int n = 10;
        for (int i = 0; i < n; i++){

            ROffice tmp = new ROffice();
            tmp.setCode(i);
            tmp.setName(String.valueOf(i) + "事業所");

            ret.add(tmp);
        }

        return ret;
    }

    public ArrayList<ROffice> GetOfficeArraySampleList()
    {
        ArrayList<ROffice> ret = new ArrayList<ROffice>();

        ROffice tmp = null;

        tmp = new ROffice();
        ret.add(tmp);
        tmp.setCode(1);
        tmp.setName("事業所A");

        tmp = new ROffice();
        ret.add(tmp);
        tmp.setCode(2);
        tmp.setName("事業所B");

        tmp = new ROffice();
        ret.add(tmp);
        tmp.setCode(3);
        tmp.setName("事業所C");

        tmp = new ROffice();
        ret.add(tmp);
        tmp.setCode(4);
        tmp.setName("事業所D");

        tmp = new ROffice();
        ret.add(tmp);
        tmp.setCode(5);
        tmp.setName("事業所E");

        tmp = new ROffice();
        ret.add(tmp);
        tmp.setCode(6);
        tmp.setName("事業所F");

        tmp = new ROffice();
        ret.add(tmp);
        tmp.setCode(7);
        tmp.setName("事業所G");

        tmp = new ROffice();
        ret.add(tmp);
        tmp.setCode(8);
        tmp.setName("事業所H");

        tmp = new ROffice();
        ret.add(tmp);
        tmp.setCode(9);
        tmp.setName("事業所I");

        return ret;
    }
}

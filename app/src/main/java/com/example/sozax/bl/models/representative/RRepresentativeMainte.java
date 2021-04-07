package com.example.sozax.bl.models.representative;

import java.util.ArrayList;

public class RRepresentativeMainte {

    public ArrayList<RRepresentative> getRepresentativeArrayList(int officeCode)
    {
        ArrayList<RRepresentative> ret = new ArrayList<RRepresentative>();

        final String officeValue = String.valueOf(officeCode);

        final int n = 10;
        for (int i = 0; i < n; i++){

            RRepresentative tmp = new RRepresentative();
            tmp.setCode(i);
            tmp.setName(officeValue + String.valueOf(i) + "さん");

            ret.add(tmp);

        }

        return ret;
    }

    public ArrayList<RRepresentative> GetRepresentativeArraySampleList()
    {
        ArrayList<RRepresentative> ret = new ArrayList<RRepresentative>();

        RRepresentative tmp = null;

        tmp = new RRepresentative();
        ret.add(tmp);
        tmp.setCode(1);
        tmp.setName("担当者A");

        tmp = new RRepresentative();
        ret.add(tmp);
        tmp.setCode(2);
        tmp.setName("担当者B");

        tmp = new RRepresentative();
        ret.add(tmp);
        tmp.setCode(3);
        tmp.setName("担当者C");

        tmp = new RRepresentative();
        ret.add(tmp);
        tmp.setCode(4);
        tmp.setName("担当者D");

        tmp = new RRepresentative();
        ret.add(tmp);
        tmp.setCode(5);
        tmp.setName("担当者E");

        tmp = new RRepresentative();
        ret.add(tmp);
        tmp.setCode(6);
        tmp.setName("担当者F");

        tmp = new RRepresentative();
        ret.add(tmp);
        tmp.setCode(7);
        tmp.setName("担当者G");

        tmp = new RRepresentative();
        ret.add(tmp);
        tmp.setCode(8);
        tmp.setName("担当者H");

        tmp = new RRepresentative();
        ret.add(tmp);
        tmp.setCode(9);
        tmp.setName("担当者I");

        return ret;
    }
}

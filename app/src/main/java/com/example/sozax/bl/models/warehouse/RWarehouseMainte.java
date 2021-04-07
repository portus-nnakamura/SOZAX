package com.example.sozax.bl.models.warehouse;

import java.util.ArrayList;

public class RWarehouseMainte {

    public ArrayList<RWarehouse> getWarehouseArrayList(int officeCode,int representativeCode)
    {
        ArrayList<RWarehouse> ret = new ArrayList<RWarehouse>();

        final String officeValue = String.valueOf(officeCode);
        final String representativeValue = String.valueOf(representativeCode);

        final int n = 10;
        for (int i = 0; i < n; i++){

            RWarehouse tmp = new RWarehouse();
            tmp.setCode(i);
            tmp.setName(officeValue + representativeValue + String.valueOf(i) + "倉庫");

            ret.add(tmp);

        }

        return ret;
    }

    public ArrayList<RWarehouse> GetWarehouseArraySampleList()
    {
        ArrayList<RWarehouse> ret = new ArrayList<RWarehouse>();

        RWarehouse tmp = null;

        tmp = new RWarehouse();
        ret.add(tmp);
        tmp.setCode(1);
        tmp.setName("倉庫A");

        tmp = new RWarehouse();
        ret.add(tmp);
        tmp.setCode(2);
        tmp.setName("倉庫B");

        tmp = new RWarehouse();
        ret.add(tmp);
        tmp.setCode(3);
        tmp.setName("倉庫C");

        tmp = new RWarehouse();
        ret.add(tmp);
        tmp.setCode(4);
        tmp.setName("倉庫D");

        tmp = new RWarehouse();
        ret.add(tmp);
        tmp.setCode(5);
        tmp.setName("倉庫E");

        tmp = new RWarehouse();
        ret.add(tmp);
        tmp.setCode(6);
        tmp.setName("倉庫F");

        tmp = new RWarehouse();
        ret.add(tmp);
        tmp.setCode(7);
        tmp.setName("倉庫G");

        tmp = new RWarehouse();
        ret.add(tmp);
        tmp.setCode(8);
        tmp.setName("倉庫H");

        tmp = new RWarehouse();
        ret.add(tmp);
        tmp.setCode(9);
        tmp.setName("倉庫I");

        return ret;
    }
}

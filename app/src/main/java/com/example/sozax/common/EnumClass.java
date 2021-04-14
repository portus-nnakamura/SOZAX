package com.example.sozax.common;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class EnumClass {

    public static class SgyjokyoEnum {

        // 受付
        public static final int Uketuke = 1;
        // 在庫確認
        public static final int Zaikokakunin = 2;
        // 出庫作業
        public static final int Syukosagyo = 3;
        // 受領確認
        public static final int Juryokakunin = 4;

        public static Map<Integer,String> SgyjokyoName;
        {
            SgyjokyoName = new HashMap<Integer, String>() {{
                put(Uketuke, "受付");
                put(Zaikokakunin, "在庫確認");
                put(Syukosagyo, "出庫作業");
                put(Juryokakunin, "受領確認");
            }};
        }
    }
}

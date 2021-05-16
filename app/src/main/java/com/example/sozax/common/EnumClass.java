package com.example.sozax.common;

public class EnumClass {

    //region 作業状況区分

    public enum SgyjokyoKubun {

        Michakusyu("未着手", 0),
        Uketuke("受付", 1),
        Zaikokakunin("在庫確認", 2),
        Syukosagyo("出庫作業", 3),
        Juryokakunin("受領確認", 4);

        private final String s;
        private final Integer i;


        SgyjokyoKubun(final String name, final int code) {
            this.s = name;
            this.i = code;
        }

        public String getString() {
            return this.s;
        }

        public int getInteger() {
            return this.i;
        }
    }

    public static SgyjokyoKubun getSgyjokyoKubun(final int code) {
        SgyjokyoKubun[] sgyjokyoKubuns = SgyjokyoKubun.values();
        for (SgyjokyoKubun sgyjokyoKubun : sgyjokyoKubuns) {
            if (sgyjokyoKubun.getInteger() == code) {
                return sgyjokyoKubun;
            }
        }

        return null;
    }

    //endregion

    //region チェック区分

    public enum CheckKubun {

        UNCHECK("", 0),
        OK("OK", 1),
        NG("NG", 2);

        private final String s;
        private final Integer i;

        CheckKubun(final String name, final int code) {
            this.s = name;
            this.i = code;
        }

        public String getString() {
            return this.s;
        }

        public int getInteger() {
            return this.i;
        }
    }

    @SuppressWarnings("unused")
    public static CheckKubun getCheckKubun(final int code) {
        CheckKubun[] checkKubuns = CheckKubun.values();
        for (CheckKubun checkKubun : checkKubuns) {
            if (checkKubun.getInteger() == code) {
                return checkKubun;
            }
        }

        return null;
    }

    //endregion

    //region 貨物区分

    public enum KamotuKubun {

        Naika("内貨", 1),
        Gaika("外貨", 2);

        private final String s;
        private final Integer i;

        KamotuKubun(final String name, final int code) {
            this.s = name;
            this.i = code;
        }

        public String getString() {
            return this.s;
        }

        public int getInteger() {
            return this.i;
        }
    }

    public static KamotuKubun getKamotuKubun(final int code) {
        KamotuKubun[] kamotuKubuns = KamotuKubun.values();
        for (KamotuKubun kamotuKubun : kamotuKubuns) {
            if (kamotuKubun.getInteger() == code) {
                return kamotuKubun;
            }
        }

        return null;
    }

    //endregion

    //region 荷造作業区分

    public enum NidukurisagyoKubun {

        Misettei("", 0),
        Nidukuri("荷造", 1),
        Tukurioki("作り置き", 2),
        Ninusinidukuri("荷主荷造", 5);

        private final String s;
        private final Integer i;

        NidukurisagyoKubun(final String name, final int code) {
            this.s = name;
            this.i = code;
        }

        public String getString() {
            return this.s;
        }

        public int getInteger() {
            return this.i;
        }
    }

    public static NidukurisagyoKubun getNidukuriKubun(final int code) {
        NidukurisagyoKubun[] nidukuriKubuns = NidukurisagyoKubun.values();
        for (NidukurisagyoKubun nidukuriKubun : nidukuriKubuns) {
            if (nidukuriKubun.getInteger() == code) {
                return nidukuriKubun;
            }
        }

        return null;
    }

    //endregion

    //region 入庫手段区分

    public enum NyukosyudanKubun {

        Funenyuko("船入庫", 1),
        Truck("トラック", 2),
        Container("コンテナ", 3);

        private final String s;
        private final Integer i;

        NyukosyudanKubun(final String name, final int code) {
            this.s = name;
            this.i = code;
        }

        public String getString() {
            return this.s;
        }

        public int getInteger() {
            return this.i;
        }
    }

    public static NyukosyudanKubun getNyukosyudanKubun(final int code) {
        NyukosyudanKubun[] nyukosyudanKubuns = NyukosyudanKubun.values();
        for (NyukosyudanKubun nyukosyudanKubun : nyukosyudanKubuns) {
            if (nyukosyudanKubun.getInteger() == code) {
                return nyukosyudanKubun;
            }
        }

        return null;
    }

    //endregion

}

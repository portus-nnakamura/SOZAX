package com.example.sozax.bl.goods_issue;

import java.io.Serializable;
import java.math.BigDecimal;

public class GoodsIssueSlip implements Serializable {

    // 伝票番号
    private String SlipNo = "";
    public String getSlipNo(){return SlipNo;}
    public void setSlipNo(String setSlipNo) {
        this.SlipNo = setSlipNo;
    }

    // 荷主コード
    private int NinushiCode = 0;
    public int getNinushiCode(){return NinushiCode;}
    public void setNinushiCode(int setNinushiCode) { this.NinushiCode = setNinushiCode; }

    // 荷主名
    private String NinushiName = "";
    public String getNinushiName(){return NinushiName;}
    public void setNinushiName(String setNinushiName) { this.NinushiName = setNinushiName; }

    // 荷主比較
    private CompareStateEnum NinushiCompareStateEnum = CompareStateEnum.対象外;
    public CompareStateEnum getNinushiCompareStateEnum(){return NinushiCompareStateEnum;}
    public void setNinushiCompareStateEnum(CompareStateEnum setNinushiCompareStateEnum) { this.NinushiCompareStateEnum = setNinushiCompareStateEnum; }

    // 荷渡コード
    private int NiwatashiCode = 0;
    public int getNiwatashiCode(){return NiwatashiCode;}
    public void setNiwatashiCode(int setNiwatashiCode) { this.NiwatashiCode = setNiwatashiCode; }

    // 荷渡名
    private String NiwatashiName = "";
    public String getNiwatashiName(){return NiwatashiName;}
    public void setNiwatashiName(String setNiwatashiName) { this.NiwatashiName = setNiwatashiName; }

    // 荷渡比較
    private CompareStateEnum NiwatashiCompareStateEnum = CompareStateEnum.対象外;
    public CompareStateEnum getNiwatashiCompareStateEnum(){return NiwatashiCompareStateEnum;}
    public void setNiwatashiCompareStateEnum(CompareStateEnum setNiwatashiCompareStateEnum) { this.NiwatashiCompareStateEnum = setNiwatashiCompareStateEnum; }

    // 商品コード
    private int ProductCode = 0;
    public int getProductCode(){return ProductCode;}
    public void setProductCode(int setProductCode) { this.ProductCode = setProductCode; }

    // 商品名
    private String ProductName = "";
    public String getProductName(){return ProductName;}
    public void setProductName(String setProductName) { this.ProductName = setProductName; }

    // 商品比較
    private CompareStateEnum ProductCompareStateEnum = CompareStateEnum.対象外;
    public CompareStateEnum getProductCompareStateEnum(){return ProductCompareStateEnum;}
    public void setProductCompareStateEnum(CompareStateEnum setProductCompareStateEnum) { this.ProductCompareStateEnum = setProductCompareStateEnum; }

    // 荷姿コード
    private int NisugataCode = 0;
    public int getNisugataCode(){return NisugataCode;}
    public void setNisugataCode(int setNisugataCode) { this.NisugataCode = setNisugataCode; }

    // 荷姿名
    private String NisugataName = "";
    public String getNisugataName(){return NisugataName;}
    public void setNisugataName(String setNisugataName) { this.NisugataName = setNisugataName; }

    // 荷姿比較
    private CompareStateEnum NisugataCompareStateEnum = CompareStateEnum.対象外;
    public CompareStateEnum getNisugataCompareStateEnum(){return NisugataCompareStateEnum;}
    public void setNisugataCompareStateEnum(CompareStateEnum setNisugataCompareStateEnum) { this.NisugataCompareStateEnum = setNisugataCompareStateEnum; }

    // 荷印コード
    private int NijirushiCode = 0;
    public int getNijirushiCode(){return NijirushiCode;}
    public void setNijirushiCode(int setNijirushiCode) { this.NijirushiCode = setNijirushiCode; }

    // 荷印名
    private String NijirushiName = "";
    public String getNijirushiName(){return NijirushiName;}
    public void setNijirushiName(String setNijirushiName) { this.NijirushiName = setNijirushiName; }

    // 荷印比較
    private CompareStateEnum NijirushiCompareStateEnum = CompareStateEnum.対象外;
    public CompareStateEnum getNijirushiCompareStateEnum(){return NijirushiCompareStateEnum;}
    public void setNijirushiCompareStateEnum(CompareStateEnum setNijirushiCompareStateEnum) { this.NijirushiCompareStateEnum = setNijirushiCompareStateEnum; }

    // 規格
    private String Kikaku = "";
    public String getKikaku(){return Kikaku;}
    public void setKikaku(String setKikaku) { this.Kikaku = setKikaku; }

    // 規格比較
    private CompareStateEnum KikakuCompareStateEnum = CompareStateEnum.対象外;
    public CompareStateEnum getKikakuCompareStateEnum(){return KikakuCompareStateEnum;}
    public void setKikakuCompareStateEnum(CompareStateEnum setKikakuCompareStateEnum) { this.KikakuCompareStateEnum = setKikakuCompareStateEnum; }
    
    // 船コード
    private int FuneCode = 0;
    public int getFuneCode(){return FuneCode;}
    public void setFuneCode(int setFuneCode) { this.FuneCode = setFuneCode; }

    // 船名
    private String FuneName = "";
    public String getFuneName(){return FuneName;}
    public void setFuneName(String setFuneName) { this.FuneName = setFuneName; }

    // 船比較
    private CompareStateEnum FuneCompareStateEnum = CompareStateEnum.対象外;
    public CompareStateEnum getFuneCompareStateEnum(){return FuneCompareStateEnum;}
    public void setFuneCompareStateEnum(CompareStateEnum setFuneCompareStateEnum) { this.FuneCompareStateEnum = setFuneCompareStateEnum; }
    
    // 数量
    private BigDecimal Quantity = BigDecimal.ZERO;
    public BigDecimal getQuantity(){return Quantity;}
    public void setQuantity(BigDecimal setQuantity) { this.Quantity = setQuantity; }
    
    // 重量
    private BigDecimal Weight = BigDecimal.ZERO;
    public BigDecimal getWeight(){return Weight;}
    public void setWeight(BigDecimal setWeight) { this.Weight = setWeight; }

    // 進行状況
    private ProgressStateEnum ProgressState = ProgressStateEnum.未着手;
    public ProgressStateEnum getProgressState(){return ProgressState;}
    public void setProgressState(ProgressStateEnum setProgressState) { this.ProgressState = setProgressState; }

    public enum ProgressStateEnum {
        未着手,
        受付,
        在庫確認,
        作業中,
        受領確認,
        完了
    }

    public enum CompareStateEnum{
        OK,
        NG,
        対象外
    }

}

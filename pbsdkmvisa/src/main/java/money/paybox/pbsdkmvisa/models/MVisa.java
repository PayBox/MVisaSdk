package money.paybox.pbsdkmvisa.models;

import java.io.Serializable;

public class MVisa implements Serializable {

    private String currencyCode;
    private String mVisaMerchantId;
    private String merchantName;
    private float amount;

    public MVisa(String mVisaMerchantId, String merchantName, String currencyCode, float amount) {
        this.mVisaMerchantId = mVisaMerchantId;
        this.merchantName = merchantName;
        this.currencyCode = currencyCode;
        this.amount = amount;
    }

    public String getmVisaMerchantId() {
        return mVisaMerchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public float getAmount() {
        return amount;
    }
}

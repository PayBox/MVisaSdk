package money.paybox.pbsdkmvisa.utils;

import java.util.HashMap;

public class MParser {


    public static final String MVISA_AMOUNT_TAG = "54";
    public static final String MVISA_MERCHANT_ID_TAG = "02";
    public static final String MVISA_MERCHANT_NAME_TAG = "59";
    public static final String MVISA_CURRENCY_CODE_TAG = "53";

    public static HashMap<String, String> decode(String code){
        HashMap<String, String> hm = new HashMap<>();
        try {
            StringBuffer tag = new StringBuffer();
            StringBuffer valLength = new StringBuffer();
            StringBuffer value = new StringBuffer();
            int length = 0;
            for(int i=0; i<code.length(); i++){
                if(tag.length()==2){
                    if(valLength.length()<2) {
                        valLength.append(code.charAt(i));
                    } else {
                        if(valLength.length()==2) {
                            if(length == 0){
                                length = Integer.parseInt(valLength.toString());
                            }
                            if(length!=0){
                                value.append(code.charAt(i));
                                length--;
                                if(length==0){
                                    hm.put(tag.toString(), value.toString());
                                    tag.setLength(0);
                                    valLength.setLength(0);
                                    value.setLength(0);
                                }
                            }
                        }
                    }
                } else {
                    tag.append(code.charAt(i));
                }
            }

        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return hm;
    }


    public static String getCurrencyByCodeISO4217(String code) {
        for(Iso4217Code currency : Iso4217Code.values()){
            if(currency.getValue().equals(code)){
                return currency.name();
            }
        }
        return null;
    }

    enum Iso4217Code {
        EUR("978"),
        USD("840"),
        AED("784"),
        KGS("417"),
        RUB("643"),
        UZS("860"),
        KZT("398"),
        THB("764"),
        CNY("156"),
        TRY("949"),
        UAH("980"),
        BYN("933"),
        HKD("344"),
        ILS("376"),
        GBP("826");

        private final String code;
        Iso4217Code(String code){
            this.code = code;
        }
        public String getValue() {
            return code;
        }
    }
}

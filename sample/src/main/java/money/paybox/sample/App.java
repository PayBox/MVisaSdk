package money.paybox.sample;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import money.paybox.payboxsdk.PBHelper;
import money.paybox.payboxsdk.Utils.Constants;



public class App extends Application {
    public static App instance;
    public PBHelper helper;
    SharedPreferences preferences;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

    }

    public void initBuilder(int merchant, String secretKey) {
        helper = new PBHelper.Builder(getApplicationContext(), secretKey, merchant)
                .setPaymentSystem(Constants.PBPAYMENT_SYSTEM.EPAYWEBKZT)
                .enabledAutoClearing(false)
                .enabledTestMode(true)
                .build();
    }

    public boolean isLoggedIn(){
        if (preferences.contains(getString(R.string.secretKeyIndex)) && preferences.contains(getString(R.string.merchantIdIndex))) {
            return true;
        }
        return false;
    }
    public String getSecretKey(){
        return preferences.getString(getString(R.string.secretKeyIndex), null);
    }



    public int getMerchandID() {
        return Integer.parseInt(preferences.getString(getString(R.string.merchantIdIndex), null));
    }

    public boolean saveSettings(String merchantId, String secretKey) {
        return preferences.edit().putString(getString(R.string.secretKeyIndex), secretKey).putString(getString(R.string.merchantIdIndex), merchantId).commit();
    }
}

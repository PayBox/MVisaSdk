package money.paybox.sample.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import money.paybox.sample.App;
import money.paybox.sample.R;


public class LaunchActivity extends AppCompatActivity {

    TextView merchnatId;
    TextView secretKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        merchnatId = (TextView)findViewById(R.id.authMerchantId);
        secretKey = (TextView)findViewById(R.id.authSecretKey);
        if(App.instance.isLoggedIn()){
            HomeActivity.show(getApplicationContext());
        }
    }



    public void signIn(View v) {
        if(!TextUtils.isEmpty(merchnatId.getText().toString()) && !TextUtils.isEmpty(secretKey.getText().toString())) {
            if(App.instance.saveSettings(merchnatId.getText().toString(), secretKey.getText().toString())) {
                HomeActivity.show(getApplicationContext());
            }
        }
    }

}

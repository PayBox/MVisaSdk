package money.paybox.sample.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import money.paybox.payboxsdk.Model.Card;
import money.paybox.payboxsdk.Model.Error;
import money.paybox.payboxsdk.Model.Response;
import money.paybox.pbsdkmvisa.MVisaHelper;
import money.paybox.pbsdkmvisa.interfaces.MVisaScanListener;
import money.paybox.pbsdkmvisa.models.MVisa;
import money.paybox.sample.App;
import money.paybox.sample.R;
import money.paybox.sample.ui.fragments.ConfirmFragment;
import money.paybox.sample.ui.fragments.MainFragment;


public class HomeActivity extends AppCompatActivity implements MVisaScanListener, ConfirmFragment.OnFragmentInteractionListener, MainFragment.OnFragmentInteractionListener {



    public static void show(Context context){
        Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    private LinearLayout progressView;
    private TextView result;
    private FragmentTransaction transaction;
    private ConfirmFragment confirmFragment;
    private MainFragment mainFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        progressView = (LinearLayout)findViewById(R.id.progressView);
        result = (TextView)findViewById(R.id.result);
        this.transaction = getSupportFragmentManager().beginTransaction();
        this.mainFragment = new MainFragment();
        this.confirmFragment = new ConfirmFragment();
        loadFragment(mainFragment);
    }




    private void loadFragment(Fragment fragment){
        try {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            if (getSupportFragmentManager().findFragmentById(R.id.fragmentContainer) == null) {
                ft.add(R.id.fragmentContainer, fragment);
            } else {
                ft.replace(R.id.fragmentContainer, fragment);
            }
            ft.addToBackStack(null);
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.instance.initBuilder(App.instance.getMerchandID(), App.instance.getSecretKey());
        MVisaHelper.getInstance().setListener(this);
        progressView.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MVisaHelper.getInstance().removeListener();
    }

    @Override
    public void onQrDetected(MVisa mVisa, ArrayList<Card> cards) {
        confirmFragment.setData(cards, mVisa);
        loadFragment(confirmFragment);
        progressView.setVisibility(View.GONE);
    }

    @Override
    public void onQrError(Error error) {
        result.setText("Error: "+error.getErrorDesription());
        progressView.setVisibility(View.GONE);
    }

    @Override
    public void onQrPaymentPaid(Response response) {
        result.setText("Payment: "+response.getPaymentId()+", status: "+response.getStatus());
        progressView.setVisibility(View.GONE);
    }

    @Override
    public void onPaymentInited() {
        result.setText("");
        progressView.setVisibility(View.VISIBLE);
        onBackPressed();
    }

    @Override
    public void onScanInited() {
        progressView.setVisibility(View.VISIBLE);
        result.setText("");
    }

}

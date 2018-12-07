package money.paybox.pbsdkmvisa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;

import money.paybox.payboxsdk.Model.Error;
import money.paybox.pbsdkmvisa.interfaces.MVisaScanListener;
import money.paybox.pbsdkmvisa.ui.CameraActivity;
import money.paybox.payboxsdk.Model.Card;
import money.paybox.payboxsdk.Model.RecurringPaid;
import money.paybox.payboxsdk.Model.Response;
import money.paybox.payboxsdk.PBHelper;

public class MVisaHelper extends MVisa {


    public static MVisaHelper instance;

    public static MVisaHelper getInstance(){
        if(instance==null){
            instance = new MVisaHelper();
        }
        return instance;
    }
    public void setListener(MVisaScanListener mVisaScanListener) {
        this.mVisaScanListener = mVisaScanListener;
    }

    public void removeListener() {
        this.mVisaScanListener = null;
    }

    public void initPayment(String orderId, String cardId, String description, HashMap<String, String> extraParams) {
        if(pbListenerIsAdded()) {
            String recurringProfile = getRecurringProfile(cardId);
            if(recurringProfile!=null){
                pbHelper.enableRecurring(36);
                pbHelper.makeRecurringPayment(mvisa.getAmount(), orderId, recurringProfile, description, extraParams);
            } else {
                pbHelper.initNewPayment(orderId, userId, mvisa.getAmount(), description, extraParams);
            }

        } else {
            throwPbHelperError();
        }
    }


    public void initScan(@NonNull Context context, @NonNull String userId) {
        this.userId = userId;
        this.isUsed = true;
        this.pbHelper = PBHelper.getSdk();
        if(this.mVisaScanListener!=null) {
            Receiver receiver = new Receiver();
            Intent scanIntent = new Intent(context, CameraActivity.class);
            scanIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(scanIntent);
            context.registerReceiver(receiver, new IntentFilter(CameraActivity.QR_ACTION));
        } else {
            throwListenerError();
        }
    }

    @Override
    public void onCardList(ArrayList<Card> arrayList) {
        if(arrayList!=null && !arrayList.isEmpty()) {
            cardList = new ArrayList<>();
            for(Card card : arrayList) {
                if(!TextUtils.isEmpty(card.getRecurringProfile())){
                    cardList.add(card);
                }
            }
            if(cardList.isEmpty()){
                cardList = null;
            }
        } else {
            cardList = null;
        }
        mVisaScanListener.onQrDetected(mvisa, cardList);
        removePbListener();
    }


    @Override
    public void onPaymentPaid(Response response) {
        mVisaScanListener.onQrPaymentPaid(response);
        removePbListener();
    }

    @Override
    public void onError(Error error) {
        cardList = null;
        mVisaScanListener.onQrError(error);
        removePbListener();
    }

    @Override
    public void onRecurringPaid(RecurringPaid recurringPaid) {
        mVisaScanListener.onQrPaymentPaid(new Response(
                recurringPaid.getStatus(),
                recurringPaid.getPaymentId(),
                null
        ));
        removePbListener();
    }

    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() == CameraActivity.QR_ACTION) {
                if (intent.hasExtra(CameraActivity.MVISA_EXTRA)) {
                    mvisa = (money.paybox.pbsdkmvisa.models.MVisa) intent.getSerializableExtra(CameraActivity.MVISA_EXTRA);
                    showAddedCard();
                }
            }
        }
    }
}

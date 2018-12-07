package money.paybox.pbsdkmvisa;

import android.text.TextUtils;

import java.util.ArrayList;

import money.paybox.pbsdkmvisa.interfaces.MVisaScanListener;
import money.paybox.payboxsdk.Interfaces.PBListener;
import money.paybox.payboxsdk.Model.Capture;
import money.paybox.payboxsdk.Model.Card;
import money.paybox.payboxsdk.Model.PStatus;
import money.paybox.payboxsdk.Model.Response;
import money.paybox.payboxsdk.PBHelper;

public abstract class MVisa implements PBListener {

    protected MVisaScanListener mVisaScanListener;
    protected money.paybox.pbsdkmvisa.models.MVisa mvisa;
    protected PBHelper pbHelper;
    protected String userId;
    protected ArrayList<Card> cardList;
    protected boolean isUsed = false;


    protected String getRecurringProfile(String cardId){
        if(cardList!=null && !TextUtils.isEmpty(cardId)){
            if(!cardList.isEmpty()){
                for(Card card:cardList){
                    if(card.getCardId().equals(cardId)){
                        return card.getRecurringProfile();
                    }
                }
            }
        }
        return null;
    }
    protected void removePbListener(){
        if(pbHelper!=null){
            pbHelper.removePbListener(this);
        }
    }

    protected boolean pbListenerIsAdded(){
        if(pbHelper!=null) {
            pbHelper.removePbListener(this);
            pbHelper.registerPbListener(this);
            return true;
        }
        return false;
    }

    protected void showAddedCard() {
        if(isUsed) {
            isUsed = false;
            if (pbListenerIsAdded()) {
                pbHelper.getCards(userId);
            } else {
                throwPbHelperError();
            }
        }
    }

    protected void throwPbHelperError() {
        throw new NullPointerException("PBHelper is null! Please call the setPbHelper void");
    }

    protected void throwListenerError() {
        throw new NullPointerException("MVisaScanListener is null! Please call the setListener void");
    }


    @Override
    public void onPaymentCanceled(Response response) { }
    @Override
    public void onCardPayInited(Response response) { }
    @Override
    public void onCardAdded(Response response) { }
    @Override
    public void onPaymentRevoke(Response response) { }
    @Override
    public void onPaymentStatus(PStatus pStatus) { }
    @Override
    public void onCardRemoved(Card card) { }
    @Override
    public void onCardPaid(Response response) { }
    @Override
    public void onPaymentCaptured(Capture capture) { }
}

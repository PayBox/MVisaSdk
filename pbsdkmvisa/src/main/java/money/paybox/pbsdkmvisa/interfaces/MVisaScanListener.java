package money.paybox.pbsdkmvisa.interfaces;

import java.util.ArrayList;

import money.paybox.payboxsdk.Model.Error;
import money.paybox.payboxsdk.Model.Response;
import money.paybox.pbsdkmvisa.models.MVisa;
import money.paybox.payboxsdk.Model.Card;

public interface MVisaScanListener {

    void onQrDetected(MVisa mVisa, ArrayList<Card> cards);
    void onQrError(Error error);
    void onQrPaymentPaid(Response response);
}

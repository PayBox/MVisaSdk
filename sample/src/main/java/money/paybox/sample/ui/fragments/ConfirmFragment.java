package money.paybox.sample.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import money.paybox.payboxsdk.Model.Card;
import money.paybox.pbsdkmvisa.MVisaHelper;
import money.paybox.pbsdkmvisa.models.MVisa;
import money.paybox.sample.R;
import money.paybox.sample.adapter.CardAdapter;


public class ConfirmFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private TextView mvMerchantId;
    private TextView mvMerchantName;
    private TextView mvAmount;
    private TextView mvCurrency;
    private Spinner cardList;
    private TextView cardWarn;
    private LinearLayout cardView;
    private Button initPayment;
    private ArrayList<Card> cards;
    private MVisa mVisa;


    public void setData(ArrayList<Card> cards, MVisa mVisa){
        this.cards = cards;
        this.mVisa = mVisa;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_confirm, container, false);
        mvMerchantName = (TextView)view.findViewById(R.id.mvMerchantName);
        mvMerchantId = (TextView)view.findViewById(R.id.mvMerchantId);
        mvAmount = (TextView)view.findViewById(R.id.mvAmount);
        mvCurrency = (TextView)view.findViewById(R.id.mvCurrency);
        cardList = (Spinner)view.findViewById(R.id.card_list);
        cardWarn = (TextView)view.findViewById(R.id.cardWarn);
        cardView = (LinearLayout)view.findViewById(R.id.cardView);
        initPayment = (Button)view.findViewById(R.id.confirmPayment);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MVisaHelper.getInstance().initPayment("123",
                        cardList.getCount()!=0 ? ((Card)cardList.getSelectedItem()).getCardId():null,
                        "test",null);
                mListener.onPaymentInited();

            }
        });
        initData();
    }


    private void initData(){
        if(mVisa!=null){
            mvMerchantId.setText(mVisa.getmVisaMerchantId());
            mvMerchantName.setText(mVisa.getMerchantName());
            mvAmount.setText(String.valueOf(mVisa.getAmount()));
            mvCurrency.setText(mVisa.getCurrencyCode());
        }
        if(cards!=null){
            initAdapter(cards);
        } else {
            cardWarn.setVisibility(View.VISIBLE);
            cardView.setVisibility(View.GONE);
        }
    }
    private void initAdapter(ArrayList<Card> cards){
        CardAdapter cardAdapter = new CardAdapter(getContext(), cards);
        cardList.setAdapter(cardAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onPaymentInited();
    }
}

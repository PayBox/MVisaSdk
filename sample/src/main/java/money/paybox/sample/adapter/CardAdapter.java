package money.paybox.sample.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import money.paybox.payboxsdk.Model.Card;
import money.paybox.sample.R;

public class CardAdapter extends ArrayAdapter<Card> {

    private LayoutInflater inflater;
    private ArrayList<Card> cards;
    public CardAdapter(Context context, ArrayList<Card> cards) {
        super(context, R.layout.card_item);
        this.cards = cards;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public Card getItem(int position) {
        return cards.get(position);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.card_item, parent, false);
        TextView cardNum = (TextView) view.findViewById(R.id.cardNum);
        cardNum.setText(getItem(position).getCardhash());
        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.card_item, parent, false);
        TextView cardNum = (TextView) view.findViewById(R.id.cardNum);
        cardNum.setText(getItem(position).getCardhash());
        return view;
    }
}

package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class TransactionListAdapter extends ArrayAdapter<Transaction> {

    private int resource;
    public TextView titleView;
    public TextView iznosView;
    public ImageView imageViewTransaction;

    public TransactionListAdapter(@NonNull Context context, int _resource, ArrayList<Transaction> items) {
        super(context, _resource,items);
        resource = _resource;
    }

    public void setTransaction(ArrayList<Transaction> transactions) {
        clear();
        addAll(transactions);
    }
    public Transaction getTransaction(int position) {return this.getItem(position);}

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LinearLayout newView;
        if (convertView == null) {
            newView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li;
            li = (LayoutInflater)getContext().
                    getSystemService(inflater);
            li.inflate(resource, newView, true);
        } else {
            newView = (LinearLayout)convertView;
        }

        Transaction transaction = getItem(position);

        titleView = newView.findViewById(R.id.title);
        iznosView = newView.findViewById(R.id.iznos);
        imageViewTransaction = newView.findViewById(R.id.icon);
        titleView.setText(transaction.getTitle());
        iznosView.setText(String.valueOf(transaction.getAmount()));

//        String typeMatch = String.valueOf(transaction.getType()).toLowerCase();
        String typeMatch = transaction.getType().getName().toLowerCase().replaceAll("\\s","");
        try {
            Class res = R.drawable.class;
            Field field = res.getField(typeMatch);
            int drawableId = field.getInt(null);
            imageViewTransaction.setImageResource(drawableId);
        }
        catch (Exception e) {
            imageViewTransaction.setImageResource(R.drawable.picture1);
        }

        return newView;
    }
}

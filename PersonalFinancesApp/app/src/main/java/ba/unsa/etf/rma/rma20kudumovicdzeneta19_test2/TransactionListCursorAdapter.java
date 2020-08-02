package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import java.lang.reflect.Field;

public class TransactionListCursorAdapter extends ResourceCursorAdapter {

    public TextView titleView;
    public TextView iznosView;
    public ImageView imageViewTransaction;

    public TransactionListCursorAdapter(Context context, int layout, Cursor c, boolean autoRequery) {
        super(context, layout, c, autoRequery);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        titleView = view.findViewById(R.id.title);
        iznosView = view.findViewById(R.id.iznos);
        imageViewTransaction = view.findViewById(R.id.icon);
        titleView.setText(cursor.getString(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TITLE)));
        iznosView.setText(cursor.getString(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_AMOUNT)));
        Integer typeId = Integer.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TYPE)));
        Transaction temp = new Transaction();
        Type type = temp.findTypeById(typeId);
        String typeMatch = type.getName().toLowerCase().replaceAll("\\s","");

        try {
            Class res = R.drawable.class;
            Field field = res.getField(typeMatch);
            int drawableId = field.getInt(null);
            imageViewTransaction.setImageResource(drawableId);
        }
        catch (Exception e) {
            imageViewTransaction.setImageResource(R.drawable.picture1);
        }

    }
}


package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;

import android.content.Context;
import android.os.Parcelable;

import java.time.LocalDate;

class TransactionDetailPresenter implements ITransactionDetailPresenter {
    private Context context;
    private Transaction transaction;

    private TransactionType transactionType;

    public TransactionDetailPresenter(Context context) {
        this.context = context;
        if(transactionType == null) transactionType = new TransactionType();
    }


    @Override
    public void create(LocalDate date, double amount, String title, String type, String itemDescription, int transactionInterval, LocalDate endDate) {
        this.transaction = new Transaction(date,amount,title,findType(type),itemDescription,transactionInterval,endDate);
    }

    @Override
    public Transaction getTransaction() {
        return transaction;
    }

    @Override
    public void setTransaction(Parcelable transaction) {
        this.transaction = (Transaction) transaction;
    }

    public Context getContext() {
        return context;
    }

    public Type findType(String name) {
        Type type = null;
        for(Type t:transactionType.getTransactionType()) {
            if(t.getName().equals(name)) type = t;
        }
        return type;
    }

}





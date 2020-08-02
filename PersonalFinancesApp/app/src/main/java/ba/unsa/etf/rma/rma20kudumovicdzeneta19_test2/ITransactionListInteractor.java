package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

public interface ITransactionListInteractor {
     Cursor getTransactionCursor(Context context);
     Cursor getTransactionCursorWODeleteTransactions(Context context);
     Cursor getTransactionCursorADD(Context context);
     Cursor getTransactionCursorEDIT(Context context);
     Cursor getTransactionCursorDELETE(Context context);
     void saveTransactionDB(Transaction transaction, Context context, String transactionAction, Boolean duplicate);
     void deleteTransactionDB(Transaction transaction, Context context);
     Transaction getTransaction(Context context, Integer id);
//    ArrayList<Transaction> get();
//    ArrayList<Transaction> getTransactionsForMonth();
//    void add(Transaction t);
//    void addTransactionsForMonth(Transaction t);
//    void set(ArrayList<Transaction> transactions);
//    void setTransactionsForMonth (ArrayList<Transaction> transactions);
//    void removeFromTransactionsForMonth(int position);
//    void changeTransactionInTransactionsForMonth(int position,Transaction t);
}

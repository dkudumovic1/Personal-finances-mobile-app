package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;

import android.os.Parcelable;

import java.time.LocalDate;

public interface ITransactionDetailPresenter {
    void create(LocalDate date, double amount, String title, String type, String itemDescription, int transactionInterval, LocalDate endDate);
    Transaction getTransaction();
    void setTransaction(Parcelable transaction);

}

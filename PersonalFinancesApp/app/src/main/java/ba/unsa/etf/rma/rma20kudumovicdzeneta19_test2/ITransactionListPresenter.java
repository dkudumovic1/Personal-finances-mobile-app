package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;

import com.github.mikephil.charting.data.BarEntry;

import java.time.LocalDate;
import java.util.ArrayList;

public interface ITransactionListPresenter {
    void getTransactions();

    void getFilteredSortedTransactions(Integer page, Integer typeId, String sort, String month, String year);

    void addTransaction(Transaction transaction);

    void editTransaction(Transaction transaction);

    void deleteTransaction(Integer id);

    ArrayList<Transaction> getAll();

    void onLeftButtonClicked();

    void onRightButtonClicked();

    ArrayList<Transaction> getTransactions1();

    ArrayList<Transaction> getTransactions2();

    void getRegularTransactions();

    ArrayList<Transaction> getFilteredandSortedTransactions(ArrayList<Transaction> transactions, Integer filter, Integer sort, LocalDate date);

    Double getMonthBudget(LocalDate date);

    void getTransactionsCursor();

    void saveTransaction(Transaction transaction, String transactionAction);

    void getTransactionsCursorWODelete();

    void deleteTransaction(Transaction transaction);

    boolean isTransactionInDB(Transaction transaction);

    ArrayList<BarEntry> potrosnjaPoMjesecima();

    ArrayList<BarEntry> zaradaPoMjesecima();

    ArrayList<BarEntry> ukupnoStanjePoMjesecima();

    ArrayList<BarEntry> potrosnjaPoDanima();

    ArrayList<BarEntry> zaradaPoDanima();

    ArrayList<BarEntry> ukupnoStanjePoDanima();

    ArrayList<BarEntry> potrosnjaPoSedmicama();

    ArrayList<BarEntry> zaradaPoSedmicama();

    ArrayList<BarEntry> ukupnoStanjePoSedmicama();

    void transferFromDB();
}

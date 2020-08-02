package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;

import android.database.Cursor;

import java.time.LocalDate;
import java.util.ArrayList;

public interface ITransactionListView {
    public void setTransactions(ArrayList<Transaction> movies);
    public void notifyTransactionListDataSetChanged();

    void setDate(LocalDate date);

    void setMonthText(LocalDate date);

    LocalDate getDate();

    void setAccount(Account account);

    Account getAccount();

    void refreshBudgetInView(double amount);

    void refreshMonthlyBudgetsInView(ArrayList<Integer> budgets);

    void refreshList();

    String getSort();

    Integer getFilter();

    public void setCursor(Cursor cursor);

    void setAccountCursor(Cursor cursor);
}

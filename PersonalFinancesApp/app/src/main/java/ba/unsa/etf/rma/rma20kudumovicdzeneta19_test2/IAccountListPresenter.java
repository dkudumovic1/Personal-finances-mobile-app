package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;

import android.database.Cursor;

import java.util.ArrayList;

public interface IAccountListPresenter {
    void getAccountFromWeb();

    Account getAccount();

    void editAccount(Account account);

    void getAccountCursor();

    boolean isAccountTableEmpty();

    void editAccountDB(Account account);

    Account getAccountDB();

    void transferFromDB();
//    void refreshAccounts();
//    void setAccounts(ArrayList<Account> accounts);
//    IAccountListInteractor getInteractor();
}

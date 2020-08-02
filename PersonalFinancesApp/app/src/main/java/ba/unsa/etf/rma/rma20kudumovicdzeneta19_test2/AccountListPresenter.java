package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.fragment.app.FragmentActivity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class AccountListPresenter implements IAccountListPresenter,AccountListInteractor.OnAccountGetDone{
    private Context context;
    private static ITransactionListView view;
    private static Account account;
    private IAccountListInteractor accountListInteractor;
//    private TransactionDetailFragment transactionDetailFragment;


    public AccountListPresenter(ITransactionListView view, Context context) {
        this.view = view;
        this.context = context;
        this.accountListInteractor = new AccountListInteractor();
    }

//    public AccountListPresenter(TransactionDetailFragment transactionDetailFragment, FragmentActivity activity) {
//        this.transactionDetailFragment = transactionDetailFragment;
//        this.context = activity;
//    }


    public AccountListPresenter(Context context) {
        this.context = context;
        this.accountListInteractor = new AccountListInteractor();
    }

    public AccountListPresenter(FragmentActivity activity) {
//        this.transactionDetailFragment = transactionDetailFragment;
        this.accountListInteractor = new AccountListInteractor();
//        this.context = activity;
    }

    public void getAccountFromWeb() {
        try {
            account = new AccountListInteractor((AccountListInteractor.OnAccountGetDone)this).execute("Get").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void getAccountCursor() {
        view.setAccountCursor(accountListInteractor.getAccountCursor(context.getApplicationContext()));
    }
    @Override
    public boolean isAccountTableEmpty() {
//        return accountListInteractor.getAccountCursor(context.getApplicationContext())==null;
        TransactionDBOpenHelper transactionDBOpenHelper = new TransactionDBOpenHelper(context.getApplicationContext());
        SQLiteDatabase db = transactionDBOpenHelper.getWritableDatabase();
        String count = "SELECT count(*) FROM "+TransactionDBOpenHelper.ACCOUNT_TABLE;
        Cursor cursor = db.rawQuery(count,null);
        cursor.moveToNext();
        int icount = cursor.getInt(0);
        return icount<=0;
    }

    @Override
    public Account getAccountDB() {
        Cursor cursor = accountListInteractor.getAccountCursor(context.getApplicationContext());
        Account account = null;
        if (cursor != null) {
            cursor.moveToFirst();
            int idPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_ID);
            int internalId = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_INTERNAL_ID);
            int monthLimitPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_MONTH_LIMIT);
            int totalLimitPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_TOTAL_LIMIT);
            int budgetId = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_BUDGET);
            account = new Account(Integer.parseInt(cursor.getString(idPos)),Double.parseDouble(cursor.getString(budgetId)),
                    Double.parseDouble(cursor.getString(totalLimitPos)),Double.parseDouble(cursor.getString(monthLimitPos)),Integer.parseInt(cursor.getString(internalId)));
        }
        cursor.close();
        return account;
    }

    @Override
    public void transferFromDB() {
        Account account = getAccountFromCursor(accountListInteractor.getAccountCursor(context.getApplicationContext()));
        editAccount(account);
    }

    private Account getAccountFromCursor(Cursor cursor) {
        Account account = null;
        if(cursor!=null) {
            cursor.moveToFirst();
            int idPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_ID);
            int internalId = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_INTERNAL_ID);
            int budgetPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_BUDGET);
            int totalLimitPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_TOTAL_LIMIT);
            int monthLimitPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_MONTH_LIMIT);
            account = new Account(Integer.parseInt(cursor.getString(idPos)),Double.parseDouble(cursor.getString(budgetPos)),
                    Double.parseDouble(cursor.getString(totalLimitPos)),Double.parseDouble(cursor.getString(monthLimitPos)),
                    Integer.parseInt(cursor.getString(internalId)));
            cursor.close();
        }
        return account;
    }


    public void editAccount(Account account) {
        try {
            account = new AccountListInteractor(this,account).execute("Edit").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void editAccountDB(Account account) {
        accountListInteractor.editAccountDB(account,context.getApplicationContext());
    }


    @Override
    public void onDone(Account account) {
//        if(transactionDetailFragment!=null) {
//            transactionDetailFragment.setAccount(account);
//        }else {
//            view.setAccount(account);
//        }
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }
    //    public AccountListPresenter(FragmentActivity activity) {
//        this.view = view;
//        this.interactor = new AccountListInteractor();
//    }

//    @Override
//    public void refreshAccounts() {
//        view.setAccount(interactor.get());
//    }
//
//    @Override
//    public void setAccounts(ArrayList<Account> accounts) {
//        interactor.set(accounts);
//    }

//    @Override
//    public IAccountListInteractor getInteractor() {
//        return interactor;
//    }

}

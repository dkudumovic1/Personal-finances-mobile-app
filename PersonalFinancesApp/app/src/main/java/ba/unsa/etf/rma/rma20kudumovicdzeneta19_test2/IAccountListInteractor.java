package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;

import android.content.Context;
import android.database.Cursor;

public interface IAccountListInteractor {
    Cursor getAccountCursor(Context context);
    void editAccountDB(Account account, Context context);
}

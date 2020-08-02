package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

import static ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2.MainActivity.SWIPE_THRESHOLD;
import static ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2.MainActivity.SWIPE_VELOCITY_THRESHOLD;

public class TransactionListFragment extends Fragment implements ITransactionListView, GestureDetector.OnGestureListener {

    private static Integer brojac = 0;
    private OnItemClick onItemClick;
    private OnAddClick onAddClick;
    private SwipeListFragment swipeListFragment;

    private ImageButton imageButtonRight;
    private ImageButton imageButtonLeft;
    private TextView textViewMonth;


    private ListView listView;

    private TextView textView1;
    private TextView textView2;

    private Spinner spinnerFilter;
    private Spinner spinnerSort;

    private Button buttonAddTransaction;

    private TransactionType transactionType;


    private ITransactionListPresenter transactionListPresenter;

    private IAccountListPresenter accountListPresenter;

    private  Account account;

    private  ArrayList<Integer> monthlyBudgets = new ArrayList<Integer>(){{add(0);
    add(0); add(0); add(0); add(0); add(0); add(0);
        add(0); add(0); add(0); add(0); add(0);}};

    private TransactionListAdapter transactionListAdapter;
    private TransactionListCursorAdapter transactionListCursorAdapter;

    private LocalDate date = LocalDate.parse("2020-03-01");
    private GestureDetector gestureDetector;


    public ITransactionListPresenter getPresenter() {
        if (transactionListPresenter == null) {
            transactionListPresenter = new TransactionListPresenter(this,getActivity());
        }
        return transactionListPresenter;
    }

//    private ITypePresenter typePresenter;
//    public ITypePresenter getTypePresenter() {
//        if (typePresenter == null) {
//            typePresenter = new TypePresenter();
//        }
//        return typePresenter;
//    }

    public IAccountListPresenter getAccountListPresenter() {
        if (accountListPresenter == null) {
            accountListPresenter = new AccountListPresenter(this,getActivity());
        }
        return accountListPresenter;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        brojac++;
        View fragmentView = inflater.inflate(R.layout.fragment_list, container, false);
        //getAccountListPresenter().getAccountFromWeb();

        spinnerFilter = fragmentView.findViewById(R.id.spinnerFilter);
        imageButtonLeft = fragmentView.findViewById(R.id.imageButtonLeft);
        imageButtonRight = fragmentView.findViewById(R.id.imageButtonRight);
        buttonAddTransaction = fragmentView.findViewById(R.id.buttonAddTransaction);
        textViewMonth = fragmentView.findViewById(R.id.textViewMonth);
        textView1 = fragmentView.findViewById(R.id.textView1);
        textView2 = fragmentView.findViewById(R.id.textView2);
        //account = getAccountListPresenter().getAccount();
        textViewMonth.setText(String.valueOf(date.getMonth())+date.getYear());

        transactionListAdapter=new TransactionListAdapter(getActivity(), R.layout.list_element, new ArrayList<Transaction>());
        listView= fragmentView.findViewById(R.id.listView);
        listView.setAdapter(transactionListAdapter);
        listView.setOnItemClickListener(listItemClickListener);
        transactionListCursorAdapter= new TransactionListCursorAdapter(getActivity(), R.layout.list_element,null,false);



        onItemClick= (OnItemClick) getActivity();
        onAddClick = (OnAddClick) getActivity();
        swipeListFragment = (SwipeListFragment) getActivity();



        ArrayList<CustomItems> customList = new ArrayList<>();
        customList.add(new CustomItems("All", R.drawable.picture1));
        customList.add(new CustomItems("Regular payment", R.drawable.regularpayment));
        customList.add(new CustomItems("Regular income", R.drawable.regularincome));
        customList.add(new CustomItems("Purchase", R.drawable.purchase));
        customList.add(new CustomItems("Individual income", R.drawable.individualincome));
        customList.add(new CustomItems("Individual payment", R.drawable.individualpayment));

        CustomAdapter customAdapter = new CustomAdapter(getActivity(), customList);

        if (spinnerFilter != null) {
            spinnerFilter.setAdapter(customAdapter);

        }


        ArrayList<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("Price - Ascending");
        spinnerArray.add("Price - Descending");
        spinnerArray.add("Title - Ascending");
        spinnerArray.add("Title - Descending");
        spinnerArray.add("Date - Ascending");
        spinnerArray.add("Date - Descending");

        spinnerSort = fragmentView.findViewById(R.id.spinnerSort);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(spinnerArrayAdapter);


        if(Conn.isConnected(getActivity())) {
            getAccountListPresenter().getAccountFromWeb();
            account = getAccountListPresenter().getAccount();
            AccountModel.setTempAccount(account);
            if(getAccountListPresenter().isAccountTableEmpty()) {
                writeAccountInDB(account);
            }else {
                editAccountInDB(account);
            }
//            Transaction transaction = new Transaction(LocalDate.of(2019, 01,02),2500.00,"Salary",2,"",30,LocalDate.of(2020, Month.DECEMBER,30),555);
//            writeTransactionInDB(transaction);
            getPresenter().getTransactions();
            getAll(null,null,"amount.asc",String.valueOf(date.getMonthValue()),String.valueOf(date.getYear()));
//        }else if (!Conn.isConnected(getActivity()) && TransactionModel.tempTransactions.size()!=0 ){
//            setTransactions(TransactionModel.tempTransactions);
//            setAccount(AccountModel.tempAccount);
        }else {
            setAccount(AccountModel.tempAccount);
            getAccountListPresenter().getAccountCursor();
            getPresenter().getTransactionsCursorWODelete();
        }

        textView1.setText("Global amount: "+(account.getBudget()));
        textView2.setText("Limit: "+account.getTotalLimit());

        imageButtonLeft.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                transactionListPresenter.onLeftButtonClicked();
                //todo popravi mjesece za bazu i pomocnu listu
                if(Conn.isConnected(getContext()))
                getAll(null,spinnerFilter.getSelectedItemPosition(),getQueryForSort(spinnerSort.getSelectedItemPosition()),String.valueOf(date.getMonthValue()),String.valueOf(date.getYear()));

            }
        });

        imageButtonRight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                transactionListPresenter.onRightButtonClicked();
                if(Conn.isConnected(getContext()))
                getAll(null,spinnerFilter.getSelectedItemPosition(),getQueryForSort(spinnerSort.getSelectedItemPosition()),String.valueOf(date.getMonthValue()),String.valueOf(date.getYear()));

            }
        });
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int filterCriterion = spinnerFilter.getSelectedItemPosition();
                if(Conn.isConnected(getContext()))
                getAll(null,spinnerFilter.getSelectedItemPosition(),getQueryForSort(spinnerSort.getSelectedItemPosition()),String.valueOf(date.getMonthValue()),String.valueOf(date.getYear()));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(Conn.isConnected(getContext()))
                getAll(null,spinnerFilter.getSelectedItemPosition(),getQueryForSort(spinnerSort.getSelectedItemPosition()),String.valueOf(date.getMonthValue()),String.valueOf(date.getYear()));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        //TODO
        if(getArguments()!=null && getArguments().containsKey("transactionForDeleting")) {
            Transaction transactionForDeleting = getArguments().getParcelable("transactionForDeleting");
            int resultPosition = getArguments().getInt("positionForDeleting");
            //getPresenter().deleteTransaction(resultPosition,transactionForDeleting);
            this.refreshList();
        }

        buttonAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddClick.onAddClicked(account.getTotalLimit(),account.getMonthLimit(),
                        account.getBudget(),monthlyBudgets);
            }
        });

        gestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
                boolean result;
                float diffX = moveEvent.getX() - downEvent.getX();
                float diffY = moveEvent.getY() - downEvent.getY();

                if(Math.abs(diffX)>Math.abs(diffY)) {
                    if(Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if(diffX > 0) {
                            onSwipeRight();
                        }else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                }else {
                    //swipe up and down
                    result = true;
                }

                return false;
            }
        });
        fragmentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        Button button =fragmentView.findViewById(R.id.bazaID);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent dbmanager = new Intent(getActivity(),AndroidDatabaseManager.class);
                startActivity(dbmanager);
            }
        });

        return fragmentView;
    }

    private void editAccountInDB(Account account) {
        ContentValues values = new ContentValues();
        values.put(TransactionDBOpenHelper.ACCOUNT_ID,account.getId());
        values.put(TransactionDBOpenHelper.ACCOUNT_BUDGET,account.getBudget());
        values.put(TransactionDBOpenHelper.ACCOUNT_TOTAL_LIMIT,account.getTotalLimit());
        values.put(TransactionDBOpenHelper.ACCOUNT_MONTH_LIMIT,account.getMonthLimit());
        SQLiteOpenHelper transactionDBOpenHelper = new TransactionDBOpenHelper(getActivity());
        SQLiteDatabase db = transactionDBOpenHelper.getWritableDatabase();
        String where = TransactionDBOpenHelper.ACCOUNT_INTERNAL_ID + " = " + account.getInternalId();
        db.update(TransactionDBOpenHelper.ACCOUNT_TABLE,values,where,null);
    }

    private void writeAccountInDB(Account account) {
        ContentValues values = new ContentValues();
        values.put(TransactionDBOpenHelper.ACCOUNT_ID,account.getId());
        values.put(TransactionDBOpenHelper.ACCOUNT_BUDGET,account.getBudget());
        values.put(TransactionDBOpenHelper.ACCOUNT_TOTAL_LIMIT,account.getTotalLimit());
        values.put(TransactionDBOpenHelper.ACCOUNT_MONTH_LIMIT,account.getMonthLimit());
        SQLiteOpenHelper transactionDBOpenHelper = new TransactionDBOpenHelper(getActivity());
        SQLiteDatabase db = transactionDBOpenHelper.getWritableDatabase();
        db.insert(TransactionDBOpenHelper.ACCOUNT_TABLE,null,values);
    }

    private void writeTransactionInDB(Transaction transaction) {
        ContentValues values = new ContentValues();
        values.put(TransactionDBOpenHelper.TRANSACTION_ID,transaction.getId());
        values.put(TransactionDBOpenHelper.TRANSACTION_TITLE,transaction.getTitle());
        values.put(TransactionDBOpenHelper.TRANSACTION_AMOUNT,transaction.getAmount());
        values.put(TransactionDBOpenHelper.TRANSACTION_DATE,transaction.getDate().toString());
        values.put(TransactionDBOpenHelper.TRANSACTION_END_DATE,transaction.getEndDate().toString());
        values.put(TransactionDBOpenHelper.TRANSACTION_INTERVAL,transaction.getTransactionInterval());
        values.put(TransactionDBOpenHelper.TRANSACTION_ITEM_DESCRIPTION,transaction.getItemDescription());
        values.put(TransactionDBOpenHelper.TRANSACTION_TYPE,transaction.getType().getId());
        values.put(TransactionDBOpenHelper.TRANSACTION_ACTION,"ADD");

        SQLiteOpenHelper transactionDBOpenHelper = new TransactionDBOpenHelper(getActivity());
        SQLiteDatabase db = transactionDBOpenHelper.getWritableDatabase();
        db.insert(TransactionDBOpenHelper.TRANSACTION_TABLE,null,values);
    }

    private void getAll(Integer page,Integer typeId,String sort,String month,String year) {
        ArrayList<Transaction> tmp =getPresenter().getTransactions1();
        ArrayList<Transaction> result;
        result = getPresenter().getFilteredandSortedTransactions(tmp,spinnerFilter.getSelectedItemPosition(),spinnerSort.getSelectedItemPosition(),date);
        setTransactions(result);
        notifyTransactionListDataSetChanged();
    }
    public Account refreshAccount() {
        getAccountListPresenter().getAccountFromWeb();
        return getAccountListPresenter().getAccount();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
        boolean result;
        float diffX = moveEvent.getX() - downEvent.getX();
        float diffY = moveEvent.getY() - downEvent.getY();

        if(Math.abs(diffX)>Math.abs(diffY)) {
            if(Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if(diffX > 0) {
                    onSwipeRight();
                }else {
                    onSwipeLeft();
                }
                result = true;
            }
        }else {
            //swipe up and down
            result = true;
        }

        return false;
    }

    private void onSwipeLeft() {
        swipeListFragment.OnSwipeLFromList();
    }

    private void onSwipeRight() {
        swipeListFragment.onSwipeRFromList();
    }

    public interface SwipeListFragment {
        void OnSwipeLFromList();
        void onSwipeRFromList();
    }
    public interface OnAddClick {
        void onAddClicked(double totalLimit,double monthLimit,double budget,
                          ArrayList<Integer> monthlyBudgets);
    }
    public interface OnItemClick {
        void onItemClicked(Transaction transaction,double totalLimit,double monthLimit,double budget,
                           ArrayList<Integer> monthlyBudgets,int positionOfElement);
        void onItemClicked(Boolean inDatabase, int id);
        void onItemClicked(Integer pos,double totalLimit,double monthLimit,double budget,
                           ArrayList<Integer> monthlyBudgets,int positionOfElement);
    }
    private AdapterView.OnItemClickListener listItemClickListener =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Transaction transaction = transactionListAdapter.getTransaction(position);
                    //todo otkomentarisi kad zavrsis account
                    onItemClick.onItemClicked(transaction,account.getTotalLimit(),account.getMonthLimit(),
                    account.getBudget(),monthlyBudgets,position);
                }
            };
    private AdapterView.OnItemClickListener listCursorItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            if(cursor != null) {
                //onItemClick.onItemClicked(true, cursor.getInt(cursor.getColumnIndex(TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID)));
                onItemClick.onItemClicked(cursor.getInt(cursor.getColumnIndex(TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID)),account.getTotalLimit(),account.getMonthLimit(),
                        account.getBudget(),monthlyBudgets,position);
//                Transaction transaction = transactionListCursorAdapter.get
//                onItemClick.onItemClicked(transaction,account.getTotalLimit(),account.getMonthLimit(),
//                        account.getBudget(),monthlyBudgets,position);
            }
        }
    };

    @Override
    public void setTransactions(ArrayList<Transaction> transactions) {
//        transactionListAdapter.setTransaction(transactions);
//
//        notifyTransactionListDataSetChanged();

        listView.setAdapter(transactionListAdapter);
        listView.setOnItemClickListener(listItemClickListener);
        transactionListAdapter.setTransaction(transactions);
        notifyTransactionListDataSetChanged();
        //todo provjeriti da li treba ostati samo lista zadnjih trans filtriranih i sort ili sve
        TransactionModel.tempTransactions.clear();
        TransactionModel.tempTransactions.addAll(transactions);

    }

    @Override
    public void setCursor(Cursor cursor) {
        listView.setAdapter(transactionListCursorAdapter);
        listView.setOnItemClickListener(listCursorItemClickListener);
        transactionListCursorAdapter.changeCursor(cursor);
    }

    @Override
    public void setAccountCursor(Cursor cursor) {
        Account newAccount = new Account();
       while(cursor.moveToNext()) {
           newAccount.setId(cursor.getInt(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_ID)));
           newAccount.setInternalId(cursor.getInt(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_INTERNAL_ID)));
           newAccount.setBudget(cursor.getDouble(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_BUDGET)));
           newAccount.setTotalLimit(cursor.getDouble(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_TOTAL_LIMIT)));
           newAccount.setMonthLimit(cursor.getDouble(cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.ACCOUNT_MONTH_LIMIT)));
           setAccount(newAccount);
       }
    }


    @Override
    public void notifyTransactionListDataSetChanged() {
        transactionListAdapter.notifyDataSetChanged();
    }

    @Override
    public void setDate(LocalDate date) {
        this.date = date;
    }


    @Override
    public void setMonthText(LocalDate date) {
        textViewMonth.setText(String.valueOf(date.getMonth())+date.getYear());
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public void setAccount(Account account) {
        this.account = account;
    }

    public void setAccountWithText(Account account) {
        this.account = account;
        textView1.setText("Global amount: "+(account.getBudget()));
        textView2.setText("Limit: "+account.getTotalLimit());
    }

    @Override
    public void refreshBudgetInView(double amount) {
        account.setBudget(amount);
        textView1.setText("Global amount: "+String.valueOf(account.getBudget()));
    }

    @Override
    public void refreshMonthlyBudgetsInView(ArrayList<Integer> budgets) {
        for(int i=0;i<monthlyBudgets.size();i++) {
            int x=monthlyBudgets.get(i);
            monthlyBudgets.set(i,budgets.get(i)+x);
        }
    }

    @Override
    public void refreshList() {
        //getPresenter().refreshList();
        //getPresenter().getFilteredSortedTransactions(null,spinnerFilter.getSelectedItemPosition(),getQueryForSort(spinnerSort.getSelectedItemPosition()),String.valueOf(date.getMonthValue()),String.valueOf(date.getYear()));
        if(Conn.isConnected(getContext())) {
            getPresenter().getTransactions();
//        setTransactions(getPresenter().getTransactions1());
            getAll(null,null,"amount.asc",String.valueOf(date.getMonthValue()),String.valueOf(date.getYear()));
            //TODO
        }
    }

    public Account getAccount() {
        return account;
    }

    public String getQueryForSort(Integer position) {
        switch(position) {
            case 0:
                return "amount.asc";
            case 1:
                return "amount.desc";
            case 2:
                return "title.asc";
            case 3:
                return "title.desc";
            case 4:
                return "date.asc";
            case 5:
                return "date.desc";
            default:
                return "amount.asc";
        }
    }
  public String getSort() {
        return getQueryForSort(spinnerSort.getSelectedItemPosition());
  }
  public Integer getFilter() {
        return spinnerFilter.getSelectedItemPosition();
  }

}
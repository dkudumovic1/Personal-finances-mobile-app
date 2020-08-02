package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TransactionListFragment.OnItemClick,TransactionDetailFragment.OnDeleteClick,
        TransactionDetailFragment.OnSaveClick,TransactionListFragment.OnAddClick,TransactionListFragment.SwipeListFragment,
        BudgetFragment.OnSwipe,GraphsFragment.SwipeGraphsFragment/*implements ITransactionListView*/{
    public static final int SWIPE_THRESHOLD = 100;
    public static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private boolean twoPaneMode=false;
    private TransactionDetailFragment detailFragment;
    private GestureDetector gestureDetector;
    // private Fragment listFragment;
    private Account account;
    private IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    private ITransactionListPresenter transactionListPresenter;
    private IAccountListPresenter accountListPresenter;

    public ITransactionListPresenter getPresenter() {
        if (transactionListPresenter == null) {
            transactionListPresenter = new TransactionListPresenter(getApplicationContext());
        }
        return transactionListPresenter;
    }

    public IAccountListPresenter getAccountPresenter() {
        if (accountListPresenter == null) {
            accountListPresenter = new AccountListPresenter(getApplicationContext());
        }
        return accountListPresenter;
    }

    @Override
    public void onStart() {

        super.onStart();
        registerReceiver(broadcastReceiver,filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() == null) {
                Toast toast = Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT);
                toast.show();
                if(BudgetFragment.textViewOfflineAcc!=null)
                BudgetFragment.textViewOfflineAcc.setText("Offline izmjena");
                if(TransactionDetailFragment.textViewOffline!=null)
                TransactionDetailFragment.textViewOffline.setText("Offline");
            }
            else {
                Toast toast = Toast.makeText(context, "Connected", Toast.LENGTH_SHORT);
                toast.show();
                getPresenter().transferFromDB();
                getAccountPresenter().transferFromDB();

                if(BudgetFragment.textViewOfflineAcc!=null)
                BudgetFragment.textViewOfflineAcc.setText("");
                if(TransactionDetailFragment.textViewOffline!=null)
                    TransactionDetailFragment.textViewOffline.setText("");

                FragmentManager fragmentManager = getSupportFragmentManager();
                TransactionListFragment listFragment =
                        (TransactionListFragment) fragmentManager.findFragmentByTag("list");
                if(listFragment!=null)
                listFragment.refreshList();
            }
        }
    };


    public TransactionDetailFragment getDetailFragment() {
        return detailFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//dohvatanje FragmentManager-a
        FragmentManager fragmentManager = getSupportFragmentManager();
        FrameLayout details = findViewById(R.id.transaction_detail);
//slucaj layouta za ˇsiroke ekrane
        if (details != null) {
            twoPaneMode = true;
            detailFragment = (TransactionDetailFragment)
                    fragmentManager.findFragmentById(R.id.transaction_detail);
//provjerimo da li je fragment detalji ve´c kreiran
            if (detailFragment == null) {
//kreiramo novi fragment FragmentDetalji ukoliko ve´c nije kreiran
                detailFragment = new TransactionDetailFragment();
                fragmentManager.beginTransaction().
                        replace(R.id.transaction_detail, detailFragment)
                        .commit();
            }
        } else {
            twoPaneMode = false;
        }
//Dodjeljivanje fragmenta MovieListFragment
        Fragment listFragment =
                fragmentManager.findFragmentByTag("list");
//provjerimo da li je ve´c kreiran navedeni fragment
        if (listFragment == null) {
//ukoliko nije, kreiramo
            listFragment = new TransactionListFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.transactions_list,listFragment,"list")
                    .commit();

        }else{
//sluˇcaj kada mijenjamo orijentaciju uredaja
//iz portrait (uspravna) u landscape (vodoravna)
//a u aktivnosti je bio otvoren fragment MovieDetailFragment
//tada je potrebno skinuti MovieDetailFragment sa steka
//kako ne bi bio dodan na mjesto fragmenta MovieListFragment
            fragmentManager.popBackStack(null,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

    }

    @Override
    public void onItemClicked(Transaction transaction,double totalLimit,double monthLimit,double budget,
                              ArrayList<Integer> monthlyBudgets,int positionOfElement) {
        //Priprema novog fragmenta FragmentDetalji
        Bundle arguments = new Bundle();
        arguments.putParcelable("transaction", transaction);
        arguments.putDouble("totalLimit", totalLimit);
        arguments.putDouble("monthLimit",monthLimit);
        arguments.putDouble("budget",budget);
        arguments.putIntegerArrayList("monthlyBudgets",monthlyBudgets);
        arguments.putInt("position",positionOfElement);
        TransactionDetailFragment detailFragment = new TransactionDetailFragment();
        detailFragment.setArguments(arguments);
        if (twoPaneMode){
//Sluˇcaj za ekrane sa ˇsirom dijagonalom
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.transaction_detail, detailFragment)
                    .commit();
        }
        else{
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.transactions_list,detailFragment)
                    .addToBackStack(null).commit();
        }
    }

    @Override
    public void onItemClicked(Integer pos,double totalLimit,double monthLimit,double budget,
                              ArrayList<Integer> monthlyBudgets,int positionOfElement) {
        //Priprema novog fragmenta FragmentDetalji
        Transaction transaction = (new TransactionListInteractor()).getTransaction(getBaseContext(),pos);
        Bundle arguments = new Bundle();
        arguments.putParcelable("transaction", transaction);
        arguments.putDouble("totalLimit", totalLimit);
        arguments.putDouble("monthLimit",monthLimit);
        arguments.putDouble("budget",budget);
        arguments.putIntegerArrayList("monthlyBudgets",monthlyBudgets);
        arguments.putInt("position",positionOfElement);
        TransactionDetailFragment detailFragment = new TransactionDetailFragment();
        detailFragment.setArguments(arguments);
        if (twoPaneMode){
//Sluˇcaj za ekrane sa ˇsirom dijagonalom
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.transaction_detail, detailFragment)
                    .commit();
        }
        else{
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.transactions_list,detailFragment)
                    .addToBackStack(null).commit();
        }
    }

    @Override
    public void onItemClicked(Boolean inDatabase, int id) {
        Bundle arguments = new Bundle();
        if (!inDatabase)
            arguments.putInt("id", id);
        else
            arguments.putInt("internal_id",id);
        TransactionDetailFragment detailFragment = new TransactionDetailFragment();
        detailFragment.setArguments(arguments);
        if (twoPaneMode){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.transaction_detail, detailFragment)
                    .commit();
        }
        else{
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.transactions_list,detailFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }


    @Override
    public void onDeleteClicked(Transaction transaction, int positionOfElement) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        TransactionListFragment listFragment =
                (TransactionListFragment) fragmentManager.findFragmentByTag("list");

        if(!twoPaneMode) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.transactions_list,listFragment)
                    .addToBackStack(null).commit();
        }else {
            assert listFragment != null;
            listFragment.refreshList();
            listFragment.setAccountWithText(listFragment.refreshAccount());
            TransactionDetailFragment detailFragment = new TransactionDetailFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.transactions_list,listFragment).commit();
            getSupportFragmentManager().beginTransaction().replace(R.id.transaction_detail,detailFragment).commit();

        }
    }

    @Override
    public void onSaveClicked(Transaction transaction, int positionOfElement) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        TransactionListFragment listFragment =
                (TransactionListFragment) fragmentManager.findFragmentByTag("list");
        //assert listFragment != null;
        if(listFragment!=null)
        listFragment.refreshList();
        if(!twoPaneMode) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.transactions_list,listFragment)
                    .addToBackStack(null).commit();
        }
    }

    @Override
    public void onAddClicked(double totalLimit, double monthLimit, double budget, ArrayList<Integer> monthlyBudgets) {
        Bundle arguments = new Bundle();
        arguments.putDouble("totalLimit", totalLimit);
        arguments.putDouble("monthLimit",monthLimit);
        arguments.putDouble("budget",budget);
        arguments.putIntegerArrayList("monthlyBudgets",monthlyBudgets);
        TransactionDetailFragment detailFragment = new TransactionDetailFragment();
        detailFragment.setArguments(arguments);
        if (twoPaneMode){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.transaction_detail, detailFragment)
                    .commit();
        }
        else{
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.transactions_list,detailFragment)
                    .addToBackStack(null).commit();
        }
    }

    @Override
    public void OnSwipeLFromList() {
        BudgetFragment budgetFragment = new BudgetFragment();
        if (!twoPaneMode){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.transactions_list, budgetFragment)
                    .commit();
        }
    }

    @Override
    public void onSwipeRFromList() {
        GraphsFragment graphsFragment = new GraphsFragment();
        if (!twoPaneMode){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.transactions_list, graphsFragment)
                    .commit();
        }
    }

    @Override
    public void onSwipeLFromBudget() {
        GraphsFragment graphsFragment = new GraphsFragment();
        if (!twoPaneMode){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.transactions_list, graphsFragment)
                    .commit();
        }
    }

    @Override
    public void onSwipeRFromBudget() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        TransactionListFragment listFragment =
                (TransactionListFragment) fragmentManager.findFragmentByTag("list");
        if(listFragment==null) listFragment = new TransactionListFragment();
        //dodati refresh?
        if(!twoPaneMode) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.transactions_list,listFragment)
                    .addToBackStack(null).commit();
        }
        //provjeri ovo
//        fragmentManager.popBackStack(null,
//                FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onSwipeLFromGraphs() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        TransactionListFragment listFragment =
                (TransactionListFragment) fragmentManager.findFragmentByTag("list");
        if(listFragment==null) listFragment = new TransactionListFragment();
        if(!twoPaneMode) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.transactions_list,listFragment)
                    .addToBackStack(null).commit();
        }
    }

    @Override
    public void onSwipeRFromGraphs() {
        BudgetFragment budgetFragment = new BudgetFragment();
        if (!twoPaneMode){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.transactions_list, budgetFragment)
                    .commit();
        }
    }


    //        for(int i=1;i<=12;i++) monthlyBudgets.add(0);
//
//        spinnerFilter = findViewById(R.id.spinnerFilter);
//        imageButtonLeft = findViewById(R.id.imageButtonLeft);
//        imageButtonRight = findViewById(R.id.imageButtonRight);
//        buttonAddTransaction = findViewById(R.id.buttonAddTransaction);
//        textViewMonth = findViewById(R.id.textViewMonth);
//        textView1 = findViewById(R.id.textView1);
//        textView2 = findViewById(R.id.textView2);
//
//
//
//        textViewMonth.setText(String.valueOf(date.getMonth())+date.getYear());
//
//        transactionListAdapter=new TransactionListAdapter(getApplicationContext(), R.layout.list_element, new ArrayList<Transaction>());
//        listView= (ListView) findViewById(R.id.listView);
//        listView.setAdapter(transactionListAdapter);
//        listView.setOnItemClickListener(listItemClickListener);
//        getPresenter().refresh();
////        spinnerFilter.setSelection(5);
//        getPresenter().refreshForMonth();
//
//        getAccountListPresenter().refreshAccounts();
//        getPresenter().refreshBudget();
//        getPresenter().refreshMonthlyBudgets();
//
//
//        textView1.setText("Global amount: "+String.valueOf(account.getBudget()));
//        textView2.setText("Limit: "+account.getTotalLimit());
//        imageButtonLeft.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                transactionListPresenter.onLeftButtonClicked();
//                getPresenter().refreshForMonth();
//                spinnerFilter.setSelection(0);
//                spinnerSort.setSelection(0);
//            }
//        });
//
//        imageButtonRight.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                transactionListPresenter.onRightButtonClicked();
//                getPresenter().refreshForMonth();
//                spinnerFilter.setSelection(0);
//                spinnerSort.setSelection(0);
//            }
//        });
//
//
//
//        ArrayList<CustomItems> customList = new ArrayList<>();
//        customList.add(new CustomItems("All", R.drawable.picture1));
//        customList.add(new CustomItems("Individual payment", R.drawable.individualpayment));
//        customList.add(new CustomItems("Regular payment", R.drawable.regularpayment));
//        customList.add(new CustomItems("Purchase", R.drawable.purchase));
//        customList.add(new CustomItems("Individual income", R.drawable.individualincome));
//        customList.add(new CustomItems("Regular income", R.drawable.regularincome));
//
//        CustomAdapter customAdapter = new CustomAdapter(this, customList);
//
//        if (spinnerFilter != null) {
//            spinnerFilter.setAdapter(customAdapter);
//
//        }
//
//
//        ArrayList<String> spinnerArray = new ArrayList<>();
//        spinnerArray.add("Price - Ascending");
//        spinnerArray.add("Price - Descending");
//        spinnerArray.add("Title - Ascending");
//        spinnerArray.add("Title - Descending");
//        spinnerArray.add("Date - Ascending");
//        spinnerArray.add("Date - Descending");
//
//        spinnerSort = findViewById(R.id.spinnerSort);
//        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
//                (this, android.R.layout.simple_spinner_item, spinnerArray);
//        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerSort.setAdapter(spinnerArrayAdapter);
//
//
//        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                int filterCriterion = spinnerFilter.getSelectedItemPosition();
//                getPresenter().filterBy(filterCriterion);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//
//            }
//
//        });
//
//        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//                int sortCriterion = spinnerSort.getSelectedItemPosition();
//                getPresenter().sortBy(sortCriterion);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//
//            }
//        });
//        buttonAddTransaction.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent transactionDetailIntent = new Intent(MainActivity.this, TransactionDetailActivity.class);
//                transactionDetailIntent.putExtra("requestCode", 2);
//                transactionDetailIntent.putExtra("totalLimit",account.getTotalLimit());
//                transactionDetailIntent.putExtra("monthLimit",account.getMonthLimit());
//                transactionDetailIntent.putExtra("budget",account.getBudget());
//                transactionDetailIntent.putExtra("monthlyBudgets",monthlyBudgets);
//                MainActivity.this.startActivityForResult(transactionDetailIntent,2);
//            }
//        });
//    }
//    private AdapterView.OnItemClickListener listItemClickListener = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Intent transactionDetailIntent = new Intent(MainActivity.this, TransactionDetailActivity.class);
//            Transaction transaction = transactionListAdapter.getTransaction(position);
//            transactionDetailIntent.putExtra("date", transaction.getDate());
//            transactionDetailIntent.putExtra("amount", transaction.getAmount());
//            transactionDetailIntent.putExtra("title", transaction.getTitle());
//            transactionDetailIntent.putExtra("type", transaction.getType().toString());
//            transactionDetailIntent.putExtra("itemDescription", transaction.getItemDescription());
//            transactionDetailIntent.putExtra("transactionInterval", transaction.getTransactionInterval());
//            transactionDetailIntent.putExtra("endDate", transaction.getEndDate());
//            transactionDetailIntent.putExtra("positionOfElement",position);
//            transactionDetailIntent.putExtra("totalLimit",account.getTotalLimit());
//            transactionDetailIntent.putExtra("monthLimit",account.getMonthLimit());
//            transactionDetailIntent.putExtra("budget",account.getBudget());
//            transactionDetailIntent.putExtra("requestCode", 1);
//            transactionDetailIntent.putExtra("monthlyBudgets",monthlyBudgets);
//            MainActivity.this.startActivityForResult(transactionDetailIntent,1);
//        }
//    };
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 1) {
//            if(resultCode == Activity.RESULT_OK){
//                Bundle extras = data.getExtras();
//                Transaction transactionForSaving = (Transaction) extras.getSerializable("CHANGEDTRANSACTION");
//                int resultPosition = extras.getInt("POSITION");
//                getPresenter().changeTransaction(resultPosition,transactionForSaving);
//                getPresenter().refreshBudget();
//                getPresenter().refreshMonthlyBudgets();
//            }
//            if (resultCode == Activity.RESULT_CANCELED) {
//
//                Bundle extras = data.getExtras();
//                Transaction transactionForDeleting = (Transaction) extras.getSerializable("DELETEDTRANSACTION");
//                int resultPosition = extras.getInt("POSITION");
//                getPresenter().deleteTransaction(resultPosition,transactionForDeleting);
//            }
//        }
//        if (requestCode == 2) {
//            if( resultCode == Activity.RESULT_OK) {
//                Bundle bundle = data.getExtras();
//                Transaction transactionForAdding = (Transaction) bundle.getSerializable("TRANSACTIONFORADDING");
//                getPresenter().addTransaction(transactionForAdding);
//                getPresenter().refreshBudget();
//                getPresenter().refreshMonthlyBudgets();
//            }
//            if(resultCode == Activity.RESULT_CANCELED) {
//
//            }
//        }
//    }
//    @Override
//    public void setTransactions(ArrayList<Transaction> transactions) {
//        transactionListAdapter.setTransaction(transactions);
//    }
//
//    @Override
//    public void notifyTransactionListDataSetChanged() {
//        transactionListAdapter.notifyDataSetChanged();
//    }
//
//    @Override
//    public void setDate(LocalDate date) {
//        this.date = date;
//    }
//
//
//    @Override
//    public void setMonthText(LocalDate date) {
//        textViewMonth.setText(String.valueOf(date.getMonth())+date.getYear());
//    }
//
//    public LocalDate getDate() {
//        return date;
//    }
//
//    @Override
//    public void setAccount(ArrayList<Account> accounts) {
//        this.account = accounts.get(0);
//    }
//
//    @Override
//    public void refreshBudgetInView(double amount) {
//        account.setBudget(amount);
//        textView1.setText("Global amount: "+String.valueOf(account.getBudget()));
//    }
//
//    @Override
//    public void refreshMonthlyBudgetsInView(ArrayList<Integer> budgets) {
//        for(int i=0;i<monthlyBudgets.size();i++) {
//            int x=monthlyBudgets.get(i);
//            monthlyBudgets.set(i,budgets.get(i)+x);
//        }
//    }


}

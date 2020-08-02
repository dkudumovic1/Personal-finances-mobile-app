package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TransactionDetailFragment extends Fragment {
    private EditText date;
    private EditText amount;
    private EditText title;
    private EditText itemDescription;
    private EditText transactionInterval;
    private EditText endDate;
    private Spinner spinnerType;
    private Button buttonSave;
    private Button buttonDelete;
    public static TextView textViewOffline;

    private OnDeleteClick onDeleteClick;
    private OnSaveClick onSaveClick;


    private Account account;
    private  ArrayList<Integer> monthlyBudgets = new ArrayList<Integer>() {{add(0); add(0); add(0); add(0); add(0); add(0); add(0); add(0); add(0); add(0); add(0); add(0);}};


    private String newTitle,newitemDescription,oldTitle,oldItemDescription;
    private LocalDate newDate,newEndDate,oldDate,oldEndDate;
    private double newAmount,oldAmount;
    private int newTransactionInterval,oldTransactionInterval;

    private TransactionType transactionType;

    private Type newType;
    private int type=0;



    private ITransactionDetailPresenter presenter;

    private ITransactionDetailPresenter getPresenter() {
        if (presenter == null) {
            presenter = new TransactionDetailPresenter(getContext());
        }
        return presenter;
    }

    private ITransactionListPresenter listPresenter;
    private ITransactionListPresenter getListPresenter() {
        if (listPresenter == null) {
            listPresenter = new TransactionListPresenter(getContext());
        }
        return listPresenter;
    }

//    private ITypePresenter typePresenter;
//    public ITypePresenter getTypePresenter() {
//        if (typePresenter == null) {
//            typePresenter = new TypePresenter();
//        }
//        return typePresenter;
//    }

    private IAccountListPresenter accountPresenter;
    private IAccountListPresenter getAccountPresenter() {
        if (accountPresenter == null) {
            accountPresenter = new AccountListPresenter( getContext());
        }
        return accountPresenter;
    }
    Double amountBefore;
    Integer typeBefore = -1;

@Override
public View onCreateView(
        LayoutInflater inflater,
        ViewGroup container,
        Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_detail, container, false);


    if(Conn.isConnected(getContext())) {
        getAccountPresenter().getAccountFromWeb();
        account = getAccountPresenter().getAccount();
    }else {
        account = getAccountPresenter().getAccountDB();
    }
    onDeleteClick= (OnDeleteClick) getActivity();
    onSaveClick = (OnSaveClick) getActivity();

    //getAccountPresenter().refreshAccounts();
//    account = getAccountPresenter().getInteractor().get().get(0);
    //if(account == null) getAccountPresenter().getAccountFromWeb();

//    getTypePresenter().getTypes();

//    monthlyBudgets=getListPresenter().getMonthlyBudgets();
    date = view.findViewById(R.id.date);
    amount = view.findViewById(R.id.amount);
    title = view.findViewById(R.id.title);
    itemDescription = view.findViewById(R.id.itemDescription);
    transactionInterval = view.findViewById(R.id.transactionInterval);
    endDate = view.findViewById(R.id.endDate);
    buttonSave = view.findViewById(R.id.buttonSave);
    buttonDelete = view.findViewById(R.id.buttonDelete);
    textViewOffline = view.findViewById(R.id.textViewOffline);

    ArrayList<String> spinnerArray = new ArrayList<>();
    spinnerArray.add("Regular payment");
    spinnerArray.add("Regular income");
    spinnerArray.add("Purchase");
    spinnerArray.add("Individual income");
    spinnerArray.add("Individual payment");

    spinnerType = view.findViewById(R.id.spinnerType);
    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
            (getActivity(), android.R.layout.simple_spinner_item, spinnerArray);
    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinnerType.setAdapter(spinnerArrayAdapter);


    final int position;
    final Transaction transaction;
    final double budget;
    final double monthLimit;
    final double totalLimit;
    final ArrayList<Integer> monthlyBudgets;
    if (getArguments() != null && getArguments().containsKey("transaction")) {
        getPresenter().setTransaction(getArguments().getParcelable("transaction"));
        transaction = getPresenter().getTransaction();

        position = getArguments().getInt("position", 0);
        budget = getArguments().getDouble("budget", 0);
        monthLimit = getArguments().getDouble("monthLimit", -500);
        totalLimit = getArguments().getDouble("totalLimit", -1000);
        monthlyBudgets = getArguments().getIntegerArrayList("monthlyBudgets");

        date.setText(transaction.getDate().toString());
        amount.setText(String.valueOf(transaction.getAmount()));
        title.setText(transaction.getTitle());
        itemDescription.setText(transaction.getItemDescription());
        transactionInterval.setText(String.valueOf(transaction.getTransactionInterval()));
        endDate.setText(String.valueOf(transaction.getEndDate()));

        type = transaction.getType().getId()-1;

        spinnerType.setSelection(type);

        oldDate = transaction.getDate();
        oldEndDate = transaction.getEndDate();
        oldTitle = transaction.getTitle();
        oldItemDescription = transaction.getItemDescription();
        oldAmount = transaction.getAmount();
        oldTransactionInterval = transaction.getTransactionInterval();
    }else if(getArguments() != null){
        transaction = null;
        position = 0;
        buttonDelete.setEnabled(false);
        budget = getArguments().getDouble("budget", 0);
        monthLimit = getArguments().getDouble("monthLimit", -500);
        totalLimit = getArguments().getDouble("totalLimit", -1000);
        //monthlyBudgets = getArguments().getIntegerArrayList("monthlyBudgets");

    }else {
        buttonDelete.setEnabled(false);
        transaction = null;
        position = 0;
        buttonDelete.setEnabled(false);
        budget = account.getBudget();
        monthLimit = account.getMonthLimit();
        totalLimit = account.getTotalLimit();
   }
    if(transaction!=null) {
        amountBefore = transaction.getAmount();
        typeBefore = transaction.getType().getId();
    }

        final DateValidatorUsingLocalDate dateValidatorUsingLocalDate1 = new DateValidatorUsingLocalDate(DateTimeFormatter.BASIC_ISO_DATE);
        final DateValidatorUsingLocalDate dateValidatorUsingLocalDate2 = new DateValidatorUsingLocalDate(DateTimeFormatter.ISO_LOCAL_DATE);
        final NumberValidator numberValidator = new NumberValidator();


        if(!Conn.isConnected(getContext()) && transaction == null) {
            textViewOffline.setText("Offline dodavanje");
        }
        else if (!Conn.isConnected(getContext()) && transaction!=null) {
            textViewOffline.setText("Offline izmjena");
        }
        else {
            textViewOffline.setText("");
        }

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0 || position==1) {
                    transactionInterval.setEnabled(true);
                    if(typeBefore == 1 || typeBefore == 2) {
                        endDate.setBackgroundColor(Color.TRANSPARENT);
                    }else {
                        endDate.setBackgroundColor(Color.RED);
                        buttonSave.setEnabled(false);
                    }

                    endDate.setEnabled(true);
                }
                else {
                    buttonSave.setEnabled(true);
                    transactionInterval.setEnabled(false);
                    if(transaction!=null)transaction.setTransactionInterval(0);
                    newTransactionInterval=0;
                    endDate.setBackgroundColor(Color.TRANSPARENT);
                    endDate.setEnabled(false);
                    if(transaction!=null)
                        transaction.setEndDate(null);
                    newEndDate=null;
                }
                if(position==1 || position ==3) {
                    if(transaction!=null)transaction.setItemDescription(null);
                    newitemDescription=null;
                    //itemDescription.setText("/");
                    itemDescription.setEnabled(false);
                }
                else {
                    itemDescription.setEnabled(true);

                }
                if(transaction!=null && type!=position){
                    spinnerType.setBackgroundResource(R.drawable.spinner_background);
                    transaction.setType(findType(position+1));
                }
                if(transaction!=null && type==position) {
                    spinnerType.setBackgroundResource(R.drawable.spinner_transparent_background);
                }
                newType = findType(position+1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(/*!dateValidatorUsingLocalDate1.isValid(s.toString()) &&*/ !dateValidatorUsingLocalDate2.isValid(s.toString())) {
                    date.setBackgroundColor(Color.RED);
                    buttonSave.setEnabled(false);
                }
                else  {
                    if(transaction!= null && oldDate!=LocalDate.parse(s.toString()))
                        date.setBackgroundColor(Color.GREEN);
                    else date.setBackgroundColor(Color.TRANSPARENT);
                    if(transaction!=null)
                        transaction.setDate(LocalDate.parse(s.toString()));
                    newDate=LocalDate.parse(s.toString());
                    buttonSave.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        endDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(/*!dateValidatorUsingLocalDate1.isValid(s.toString()) &&*/ !dateValidatorUsingLocalDate2.isValid(s.toString())) {
                    endDate.setBackgroundColor(Color.RED);
                    buttonSave.setEnabled(false);
                }
                else  {
                    if(transaction!=null && oldEndDate!=LocalDate.parse(s.toString()))
                        endDate.setBackgroundColor(Color.GREEN);
                    else endDate.setBackgroundColor(Color.TRANSPARENT);
                    if(transaction!=null)
                        transaction.setEndDate(LocalDate.parse(s.toString()));
                    newEndDate=LocalDate.parse(s.toString());
                    buttonSave.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()<4 || s.length()>14) {
                    title.setBackgroundColor(Color.RED);
                    buttonSave.setEnabled(false);
                }
                else {
                    if(transaction!=null && !oldTitle.equals(s.toString()))
                        title.setBackgroundColor(Color.GREEN);
                    else title.setBackgroundColor(Color.TRANSPARENT);
                    if(transaction!=null)
                        transaction.setTitle(s.toString());
                    newTitle =s.toString();
                    buttonSave.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        itemDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>100) {
                    title.setBackgroundColor(Color.RED);
                    buttonSave.setEnabled(false);
                }
                else {
                    if(oldItemDescription!=null  && transaction!=null && !oldItemDescription.equals(s.toString()) && !s.toString().equals("/"))
                        itemDescription.setBackgroundColor(Color.GREEN);
                    else itemDescription.setBackgroundColor(Color.TRANSPARENT);
                    if(transaction!=null)
                        transaction.setItemDescription(s.toString());
                    newitemDescription=s.toString();
                    buttonSave.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!numberValidator.isDouble(s.toString())) {
                    amount.setBackgroundColor(Color.RED);
                    buttonSave.setEnabled(false);
                }
                else {
                    if(transaction!=null && oldAmount!=Double.parseDouble(s.toString()))
                        amount.setBackgroundColor(Color.GREEN);
                    else amount.setBackgroundColor(Color.TRANSPARENT);
                    if(transaction!=null)
                        transaction.setAmount(Double.parseDouble(s.toString()));
                    newAmount=Double.parseDouble(s.toString());
                    buttonSave.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        transactionInterval.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!numberValidator.isInteger(s.toString())) {
                    transactionInterval.setBackgroundColor(Color.RED);
                    buttonSave.setEnabled(false);
                }
                else {
                    if(transaction!=null && oldTransactionInterval!=Integer.parseInt(s.toString()))
                        transactionInterval.setBackgroundColor(Color.GREEN);
                    else transactionInterval.setBackgroundColor(Color.TRANSPARENT);
                    if(transaction!=null)
                        transaction.setTransactionInterval(Integer.parseInt(s.toString()));
                    newTransactionInterval=Integer.parseInt(s.toString());
                    buttonSave.setEnabled(true);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


//        buttonSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(transaction!=null || (transaction==null && newTitle!=null && newDate!=null))
//                {
//                    buttonSave.setEnabled(true);if(transaction!=null && (transaction.getType()==TransactionType.REGULARPAYMENT ||
//                        transaction.getType()==TransactionType.INDIVIDUALPAYMENT ||
//                        transaction.getType()==TransactionType.PURCHASE) &&
//                        ((budget-transaction.getAmount())<totalLimit || monthlyBudgets.get(transaction.getDate().getMonthValue())- transaction.getAmount()<monthLimit)){
//                    new AlertDialog.Builder(getActivity())
//                            .setTitle("Save Transaction")
//                            .setMessage("Limit Exceeded! Are you sure you want to save this transaction?")
//                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    getListPresenter().changeTransaction(position,getPresenter().getTransaction());
//                                    removeColors();
//                                }
//                            })
//
//                            .setNegativeButton(android.R.string.no, null)
//                            .setIcon(android.R.drawable.ic_dialog_alert)
//                            .show();
//                }
//
//                else if(transaction==null && (newType==TransactionType.REGULARPAYMENT ||
//                        newType==TransactionType.INDIVIDUALPAYMENT ||
//                        newType==TransactionType.PURCHASE) &&
//                        ((budget-newAmount)<totalLimit || monthlyBudgets.get(newDate.getMonthValue())- newAmount<monthLimit)){
//                    new AlertDialog.Builder(getActivity())
//                            .setTitle("Save Transaction")
//                            .setMessage("Limit Exceeded! Are you sure you want to save this transaction?")
//                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Intent returnIntent = new Intent();
//                                    Bundle bundle = new Bundle();
//                                    bundle.putSerializable("CHANGEDTRANSACTION", (Serializable) transaction);
//                                    Transaction transactionForAdding = new Transaction(newDate,newAmount,newTitle,
//                                            newType,newitemDescription,newTransactionInterval,newEndDate);
//                                    bundle.putSerializable("TRANSACTIONFORADDING",transactionForAdding);
//                                    bundle.putInt("POSITION",position);
//                                    returnIntent.putExtras(bundle);
//                                    //prepravi
//                                    //setResult(Activity.RESULT_OK,returnIntent);
//                                    removeColors();
//                                }
//                            })
//
//                            .setNegativeButton(android.R.string.no, null)
//                            .setIcon(android.R.drawable.ic_dialog_alert)
//                            .show();
//
//                }else {
//                    Intent returnIntent = new Intent();
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("CHANGEDTRANSACTION", (Serializable) transaction);
//                    Transaction transactionForAdding = new Transaction(newDate,newAmount,newTitle,
//                            newType,newitemDescription,newTransactionInterval,newEndDate);
//                    bundle.putSerializable("TRANSACTIONFORADDING",transactionForAdding);
//                    bundle.putInt("POSITION",position);
//                    returnIntent.putExtras(bundle);
//                    //prepraviti
//                    //setResult(Activity.RESULT_OK,returnIntent);
//                    removeColors();
//                }
//
//
//
//                }
//            }
//
//        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(transaction!=null || (transaction==null && newTitle!=null && newDate!=null))
                {
                    buttonSave.setEnabled(true);
                    if(transaction!=null && (transaction.getType().getId()==1 ||
                        transaction.getType().getId()==5 ||
                        transaction.getType().getId()==3) &&
                        ((budget-transaction.getAmount())<-totalLimit  || transaction.getAmount()>monthLimit/*|| getListPresenter().getMonthBudget(transaction.getDate())-transaction.getAmount()<-monthLimit*/)){
                        //todo monthLimit
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Save Transaction")
                            .setMessage("Limit Exceeded! Are you sure you want to save this transaction?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                   // getListPresenter().editTransaction(transaction);
                                    if(Conn.isConnected(getContext())) {
                                        getListPresenter().editTransaction(transaction);
                                        textViewOffline.setText("");
                                        if(typeBefore!=transaction.getType().getId()) {
                                            updateForChangingType(transaction,amountBefore,transaction.getAmount());
                                            getAccountPresenter().editAccount(account);
                                        } else if(amountBefore != transaction.getAmount()) {
                                            updateAccountEdit(transaction,transaction.getAmount()-amountBefore);
                                            getAccountPresenter().editAccount(account);
                                        }
                                    }else {
                                        textViewOffline.setText("Offline izmjena");
                                        getListPresenter().saveTransaction(transaction,"EDIT");
                                        if(typeBefore!=transaction.getType().getId()) {
                                            updateForChangingType(transaction,amountBefore,transaction.getAmount());
                                            getAccountPresenter().editAccountDB(account);
                                        } else if(amountBefore != transaction.getAmount()) {
                                            updateAccountEdit(transaction,transaction.getAmount()-amountBefore);
                                            getAccountPresenter().editAccountDB(account);
                                        }
                                    }
                                    onSaveClick.onSaveClicked(transaction,position);
                                    removeColors();
                                }
                            })

                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }

                else if(transaction==null && (newType==findType(1) ||
                        newType==findType(5) ||
                        newType==findType(3)) &&
                        ((budget-newAmount)<-totalLimit || newAmount>monthLimit /*|| getListPresenter().getMonthBudget(newDate)-newAmount<-monthLimit)*/)){
                    //todo monthlimit
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Save Transaction")
                            .setMessage("Limit Exceeded! Are you sure you want to save this transaction?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent returnIntent = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("CHANGEDTRANSACTION", (Serializable) transaction);
                                    Transaction transactionForAdding = new Transaction(newDate,newAmount,newTitle,
                                            newType,newitemDescription,newTransactionInterval,newEndDate);
                                    updateAccount(transactionForAdding);
                                    if(Conn.isConnected(getContext())) {
                                        getListPresenter().addTransaction(transactionForAdding);
                                        getAccountPresenter().editAccount(account);
                                        textViewOffline.setText("");
                                    }else {
                                        textViewOffline.setText("Offline dodavanje");
                                        getListPresenter().saveTransaction(transactionForAdding,"ADD");
                                        getAccountPresenter().editAccountDB(account);

                                    }
                                    onSaveClick.onSaveClicked(transactionForAdding,0);
                                    removeColors();
                                }
                            })

                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }else if(transaction==null){
                    Intent returnIntent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("CHANGEDTRANSACTION", (Serializable) transaction);
                    Transaction transactionForAdding = new Transaction(newDate,newAmount,newTitle,
                            newType,newitemDescription,newTransactionInterval,newEndDate);
                    updateAccount(transactionForAdding);
                    if(Conn.isConnected(getContext())) {
                        getListPresenter().addTransaction(transactionForAdding);
                        getAccountPresenter().editAccount(account);
                        textViewOffline.setText("");
                    }else {
                        getListPresenter().saveTransaction(transactionForAdding,"ADD");
                        textViewOffline.setText("Offline dodavanje");
                        getAccountPresenter().editAccountDB(account);

                    }
                    onSaveClick.onSaveClicked(transactionForAdding,0);
                    removeColors();
                }else {
                    if(Conn.isConnected(getContext())) {
                        getListPresenter().editTransaction(transaction);
                        if(typeBefore!=transaction.getType().getId()) {
                            updateForChangingType(transaction,amountBefore,transaction.getAmount());
                            getAccountPresenter().editAccount(account);
                        } else if(amountBefore != transaction.getAmount()) {
                            updateAccountEdit(transaction,transaction.getAmount()-amountBefore);
                            getAccountPresenter().editAccount(account);
                        }
                    }else {
                        getListPresenter().saveTransaction(transaction,"EDIT");
                        textViewOffline.setText("Offline izmjena");
                        if(typeBefore!=transaction.getType().getId()) {
                            updateForChangingType(transaction,amountBefore,transaction.getAmount());
                            getAccountPresenter().editAccountDB(account);
                        } else if(amountBefore != transaction.getAmount()) {
                            updateAccountEdit(transaction,transaction.getAmount()-amountBefore);
                            getAccountPresenter().editAccountDB(account);
                        }
                    }
                        onSaveClick.onSaveClicked(transaction,position);
                        removeColors();
                    }



                }
            }
        });
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Conn.isConnected(getContext())) {
                    getListPresenter().deleteTransaction(transaction.getId());
                    updateAccountDelete(transaction);
                    getAccountPresenter().editAccount(account);
                    textViewOffline.setText("");
                } else {
                    getListPresenter().saveTransaction(transaction,"DELETE");
                    textViewOffline.setText("Offline brisanje");
                    updateAccountDelete(transaction);
                    getAccountPresenter().editAccountDB(account);
                }
                ////getListPresenter().deleteTransaction(position,getPresenter().getTransaction());
                onDeleteClick.onDeleteClicked(getPresenter().getTransaction(),position);
                //TODO moze preko onDelete pa refresh da se ucitvaju trans
            }
        });

    return  view;
}

    private void updateForChangingType(Transaction transaction, Double amountBefore, double amount) {
    Double budget = account.getBudget();
        boolean income1 = false;
        boolean income2 = false;
        boolean payment1 = false;
        boolean payment2 = false;
        if(typeBefore%2==0) income1 = true;
        else payment1 = true;
        if(transaction.getType().getId()%2==0) income2 = true;
        else payment2 = true;
        if(payment1 && payment2) updateAccountEdit(transaction,transaction.getAmount()-amountBefore);
        else if(income1 && income2) updateAccountEdit(transaction,transaction.getAmount()-amountBefore);
        else if(payment1 && income2) {
            budget+=amountBefore;
            budget+=amount;
            account.setBudget(budget);
        }
        else  {
            budget-=amountBefore;
            budget-=amount;
            account.setBudget(budget);
        }
    }

    public interface OnDeleteClick {
        void onDeleteClicked(Transaction transaction,int positionOfElement);
    }
    public interface OnSaveClick {
        void onSaveClicked(Transaction transaction,int positionOfElement);
    }
    private void removeColors() {
        title.setBackgroundColor(Color.TRANSPARENT);
        date.setBackgroundColor(Color.TRANSPARENT);
        amount.setBackgroundColor(Color.TRANSPARENT);
        itemDescription.setBackgroundColor(Color.TRANSPARENT);
        endDate.setBackgroundColor(Color.TRANSPARENT);
        transactionInterval.setBackgroundColor(Color.TRANSPARENT);
        spinnerType.setBackgroundResource(R.drawable.spinner_transparent_background);
    }
    private Type findType(Integer id) {
        Transaction transaction = new Transaction();
        return transaction.findTypeById(id);
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    private void updateAccount(Transaction transaction) {
        Double budget = account.getBudget();
        if(transaction.getType().getId()%2==0) {
            account.setBudget(budget+transaction.getAmount());
        }else {
            account.setBudget(budget-transaction.getAmount());
        }
    }

    private void updateAccountEdit(Transaction transaction,Double diff) {
    Double budget = account.getBudget();
        if(transaction.getType().getId()%2==0) {
            account.setBudget(budget+diff);
        }else {
            account.setBudget(budget-diff);
        }
    }

    private void updateAccountDelete(Transaction transaction) {
    Double budget = account.getBudget();
    if(transaction.getType().getId()%2==0) {
        account.setBudget(budget-transaction.getAmount());
    }else {
        account.setBudget(budget+transaction.getAmount());
    }
    }
}
//package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.graphics.drawable.Drawable;
//import android.os.Build;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Spinner;
//import android.widget.TextView;
//
//import java.io.Serializable;
//import java.lang.reflect.Field;
//import java.sql.SQLOutput;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//
//public class TransactionDetailActivity extends AppCompatActivity {
//
//    private EditText date;
//    private EditText amount;
//    private EditText title;
//    private EditText itemDescription;
//    private EditText transactionInterval;
//    private EditText endDate;
//    private Spinner spinnerType;
//    private Button buttonSave;
//    private Button buttonDelete;
//
//
//    String newTitle,newitemDescription,oldTitle,oldItemDescription;
//    LocalDate newDate,newEndDate,oldDate,oldEndDate;
//    double newAmount,oldAmount;
//    int newTransactionInterval,oldTransactionInterval;
//    TransactionType newType;
//    int type=0;
//
//
//
//    private ITransactionDetailPresenter presenter;
//
//    public ITransactionDetailPresenter getPresenter() {
//        if (presenter == null) {
//            presenter = new TransactionDetailPresenter(this);
//        }
//        return presenter;
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_transaction_detail2);
//
//        date = findViewById(R.id.date);
//        amount = findViewById(R.id.amount);
//        title = findViewById(R.id.title);
//        itemDescription = findViewById(R.id.itemDescription);
//        transactionInterval = findViewById(R.id.transactionInterval);
//        endDate = findViewById(R.id.endDate);
//        buttonSave = findViewById(R.id.buttonSave);
//        buttonDelete = findViewById(R.id.buttonDelete);
//
//        ArrayList<String> spinnerArray = new ArrayList<>();
//        spinnerArray.add("Individual payment");
//        spinnerArray.add("Regular payment");
//        spinnerArray.add("Purchase");
//        spinnerArray.add("Individual income");
//        spinnerArray.add("Regular income");
//
//        spinnerType = findViewById(R.id.spinnerType);
//        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
//                (this, android.R.layout.simple_spinner_item, spinnerArray);
//        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerType.setAdapter(spinnerArrayAdapter);
//
//        final Transaction transaction;
//        final int position;
//        if(getIntent().getIntExtra("requestCode",0) == 1) {
//            System.out.println("REQUEEST JEDAN");
//            getPresenter().create((LocalDate) getIntent().getSerializableExtra("date"), getIntent().getDoubleExtra("amount", 0),
//                    getIntent().getStringExtra("title"), getIntent().getStringExtra("type"),
//                    getIntent().getStringExtra("itemDescription"), getIntent().getIntExtra("transactionInterval", 0), (LocalDate) getIntent().getSerializableExtra("endDate"));
//            transaction = getPresenter().getTransaction();
//            position = getIntent().getIntExtra("positionOfElement", 0);
//
//            date.setText(transaction.getDate().toString());
//            amount.setText(String.valueOf(transaction.getAmount()));
//            title.setText(transaction.getTitle());
//            itemDescription.setText(transaction.getItemDescription());
//            transactionInterval.setText(String.valueOf(transaction.getTransactionInterval()));
//            endDate.setText(String.valueOf(transaction.getEndDate()));
//
//            type = 0;
//            TransactionType typeEnum = transaction.getType();
//
//            if (typeEnum == TransactionType.INDIVIDUALPAYMENT)
//                type = 0;
//            else if (typeEnum == TransactionType.REGULARPAYMENT)
//                type = 1;
//            else if (typeEnum == TransactionType.PURCHASE)
//                type = 2;
//            else if (typeEnum == TransactionType.INDIVIDUALINCOME)
//                type = 3;
//            else
//                type = 4;
//            spinnerType.setSelection(type);
//
//            oldDate=transaction.getDate();oldEndDate=transaction.getEndDate();
//            oldTitle =transaction.getTitle();oldItemDescription=transaction.getItemDescription();
//            oldAmount =transaction.getAmount();
//            oldTransactionInterval = transaction.getTransactionInterval();
//
//        }else {
//            transaction = null;
//            position = 0;
//            buttonDelete.setEnabled(false);
//        }
//        final double budget = getIntent().getDoubleExtra("budget",0);
//        final double monthLimit = getIntent().getDoubleExtra("monthLimit",-500);
//        final double totalLimit = getIntent().getDoubleExtra("totalLimit",-1000);
//        final ArrayList<Integer> monthlyBudgets = getIntent().getIntegerArrayListExtra("monthlyBudgets");
//        final DateValidatorUsingLocalDate dateValidatorUsingLocalDate1 = new DateValidatorUsingLocalDate(DateTimeFormatter.BASIC_ISO_DATE);
//        final DateValidatorUsingLocalDate dateValidatorUsingLocalDate2 = new DateValidatorUsingLocalDate(DateTimeFormatter.ISO_LOCAL_DATE);
//        final NumberValidator numberValidator = new NumberValidator();
//
//
//
//        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if(position==1 || position==4) {
//                    transactionInterval.setEnabled(true);
//                    endDate.setEnabled(true);
//                }
//                else {
//                    transactionInterval.setEnabled(false);
//                    if(transaction!=null)transaction.setTransactionInterval(0);
//                    newTransactionInterval=0;
//                    endDate.setEnabled(false);
//                    if(transaction!=null)
//                    transaction.setEndDate(null);
//                    newEndDate=null;
//                }
//                if(position==3 || position ==4) {
//                    transaction.setItemDescription(null);
//                    newitemDescription=null;
//                    itemDescription.setText("/");
//                    itemDescription.setEnabled(false);
//                }
//                else {
//                    itemDescription.setEnabled(true);
//
//                }
//                if(transaction!=null && type!=position){
//                    spinnerType.setBackgroundResource(R.drawable.spinner_background);
//                    transaction.setType(TransactionType.values()[position]);
//                }
//                if(transaction!=null && type==position) {
//                    spinnerType.setBackgroundResource(R.drawable.spinner_transparent_background);
//                }
//                newType = TransactionType.values()[position];
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//        date.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//           @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(/*!dateValidatorUsingLocalDate1.isValid(s.toString()) &&*/ !dateValidatorUsingLocalDate2.isValid(s.toString())) {
//                    date.setBackgroundColor(Color.RED);
//                    buttonSave.setEnabled(false);
//                }
//                else  {
//                    if(transaction!= null && oldDate!=LocalDate.parse(s.toString()))
//                    date.setBackgroundColor(Color.GREEN);
//                    else date.setBackgroundColor(Color.TRANSPARENT);
//                    if(transaction!=null)
//                    transaction.setDate(LocalDate.parse(s.toString()));
//                    newDate=LocalDate.parse(s.toString());
//                    buttonSave.setEnabled(true);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//        endDate.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(!dateValidatorUsingLocalDate1.isValid(s.toString()) && !dateValidatorUsingLocalDate2.isValid(s.toString())) {
//                    endDate.setBackgroundColor(Color.RED);
//                    buttonSave.setEnabled(false);
//                }
//                else  {
//                    if(transaction!=null && oldEndDate!=LocalDate.parse(s.toString()))
//                    endDate.setBackgroundColor(Color.GREEN);
//                    else endDate.setBackgroundColor(Color.TRANSPARENT);
//                    if(transaction!=null)
//                    transaction.setEndDate(LocalDate.parse(s.toString()));
//                    newEndDate=LocalDate.parse(s.toString());
//                    buttonSave.setEnabled(true);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//        title.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(s.length()<4 || s.length()>14) {
//                    title.setBackgroundColor(Color.RED);
//                    buttonSave.setEnabled(false);
//                }
//                else {
//                    if(transaction!=null && !oldTitle.equals(s.toString()))
//                    title.setBackgroundColor(Color.GREEN);
//                    else title.setBackgroundColor(Color.TRANSPARENT);
//                    if(transaction!=null)
//                    transaction.setTitle(s.toString());
//                    newTitle =s.toString();
//                    buttonSave.setEnabled(true);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//
//        itemDescription.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(s.length()>100) {
//                    title.setBackgroundColor(Color.RED);
//                    buttonSave.setEnabled(false);
//                }
//                else {
//                    if(transaction!=null && !oldItemDescription.equals(s.toString()) && !s.toString().equals("/"))
//                    itemDescription.setBackgroundColor(Color.GREEN);
//                    else itemDescription.setBackgroundColor(Color.TRANSPARENT);
//                    if(transaction!=null)
//                    transaction.setItemDescription(s.toString());
//                    newitemDescription=s.toString();
//                    buttonSave.setEnabled(true);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//        amount.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(!numberValidator.isDouble(s.toString())) {
//                    amount.setBackgroundColor(Color.RED);
//                    buttonSave.setEnabled(false);
//                }
//                else {
//                    if(transaction!=null && oldAmount!=Double.parseDouble(s.toString()))
//                    amount.setBackgroundColor(Color.GREEN);
//                    else amount.setBackgroundColor(Color.TRANSPARENT);
//                    if(transaction!=null)
//                    transaction.setAmount(Double.parseDouble(s.toString()));
//                    newAmount=Double.parseDouble(s.toString());
//                    buttonSave.setEnabled(true);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//        transactionInterval.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(!numberValidator.isInteger(s.toString())) {
//                    transactionInterval.setBackgroundColor(Color.RED);
//                    buttonSave.setEnabled(false);
//                }
//                else {
//                    if(transaction!=null && oldTransactionInterval!=Integer.parseInt(s.toString()))
//                    transactionInterval.setBackgroundColor(Color.GREEN);
//                    else transactionInterval.setBackgroundColor(Color.TRANSPARENT);
//                    if(transaction!=null)
//                    transaction.setTransactionInterval(Integer.parseInt(s.toString()));
//                    newTransactionInterval=Integer.parseInt(s.toString());
//                    buttonSave.setEnabled(true);
//
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//        //TODO onemoguciti odredjena polja u zavisnosti od tipa
//        buttonSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(transaction!=null || (transaction==null && newTitle!=null && newDate!=null))
//                    {
//                        buttonSave.setEnabled(true);if(transaction!=null && (transaction.getType()==TransactionType.REGULARPAYMENT ||
//                            transaction.getType()==TransactionType.INDIVIDUALPAYMENT ||
//                            transaction.getType()==TransactionType.PURCHASE) &&
//                            ((budget-transaction.getAmount())<totalLimit || monthlyBudgets.get(transaction.getDate().getMonthValue())- transaction.getAmount()<monthLimit)){
//                        new AlertDialog.Builder(TransactionDetailActivity.this)
//                                .setTitle("Save Transaction")
//                                .setMessage("Limit Exceeded! Are you sure you want to save this transaction?")
//                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        Intent returnIntent = new Intent();
//                                        Bundle bundle = new Bundle();
//                                        bundle.putSerializable("CHANGEDTRANSACTION", (Serializable) transaction);
//                                        Transaction transactionForAdding = new Transaction(newDate,newAmount,newTitle,
//                                                newType,newitemDescription,newTransactionInterval,newEndDate);
//                                        bundle.putSerializable("TRANSACTIONFORADDING",transactionForAdding);
//                                        bundle.putInt("POSITION",position);
//                                        returnIntent.putExtras(bundle);
//                                        setResult(Activity.RESULT_OK,returnIntent);
//                                        removeColors();
//                                    }
//                                })
//
//                                .setNegativeButton(android.R.string.no, null)
//                                .setIcon(android.R.drawable.ic_dialog_alert)
//                                .show();
//                    }
//
//                    else if(transaction==null && (newType==TransactionType.REGULARPAYMENT ||
//                            newType==TransactionType.INDIVIDUALPAYMENT ||
//                            newType==TransactionType.PURCHASE) &&
//                            ((budget-newAmount)<totalLimit || monthlyBudgets.get(newDate.getMonthValue())- newAmount<monthLimit)){
//                        new AlertDialog.Builder(TransactionDetailActivity.this)
//                                .setTitle("Save Transaction")
//                                .setMessage("Limit Exceeded! Are you sure you want to save this transaction?")
//                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        Intent returnIntent = new Intent();
//                                        Bundle bundle = new Bundle();
//                                        bundle.putSerializable("CHANGEDTRANSACTION", (Serializable) transaction);
//                                        Transaction transactionForAdding = new Transaction(newDate,newAmount,newTitle,
//                                                newType,newitemDescription,newTransactionInterval,newEndDate);
//                                        bundle.putSerializable("TRANSACTIONFORADDING",transactionForAdding);
//                                        bundle.putInt("POSITION",position);
//                                        returnIntent.putExtras(bundle);
//                                        setResult(Activity.RESULT_OK,returnIntent);
//                                        removeColors();
//                                    }
//                                })
//
//                                .setNegativeButton(android.R.string.no, null)
//                                .setIcon(android.R.drawable.ic_dialog_alert)
//                                .show();
//
//                    }else {
//                        Intent returnIntent = new Intent();
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("CHANGEDTRANSACTION", (Serializable) transaction);
//                        Transaction transactionForAdding = new Transaction(newDate,newAmount,newTitle,
//                                newType,newitemDescription,newTransactionInterval,newEndDate);
//                        bundle.putSerializable("TRANSACTIONFORADDING",transactionForAdding);
//                        bundle.putInt("POSITION",position);
//                        returnIntent.putExtras(bundle);
//                        setResult(Activity.RESULT_OK,returnIntent);
//                        removeColors();
//                    }
//
//
//
//                }
//                }
//
//        });
//
//        buttonDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent returnIntent = new Intent();
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("DELETEDTRANSACTION", (Serializable) transaction);
//                bundle.putInt("POSITION",position);
//                returnIntent.putExtras(bundle);
//                setResult(Activity.RESULT_CANCELED,returnIntent);
//                finish();
//            }
//        });
//    }
//    private void SetActivity(Transaction transaction) {
//        date.setText(transaction.getDate().toString());
//        amount.setText(String.valueOf(transaction.getAmount()));
//        title.setText(transaction.getTitle());
//        itemDescription.setText(transaction.getItemDescription());
//        transactionInterval.setText(String.valueOf(transaction.getTransactionInterval()));
//        endDate.setText(String.valueOf(transaction.getEndDate()));
//
//        int type=0;
//        TransactionType typeEnum=transaction.getType();
//
//        if(typeEnum==TransactionType.INDIVIDUALPAYMENT)
//            type = 0;
//        else if(typeEnum==TransactionType.REGULARPAYMENT)
//            type = 1;
//        else if(typeEnum==TransactionType.PURCHASE)
//            type = 2;
//        else if(typeEnum==TransactionType.INDIVIDUALINCOME)
//            type = 3;
//        else
//            type = 4;
//        spinnerType.setSelection(type);
//    }
//    private void removeColors() {
//        title.setBackgroundColor(Color.TRANSPARENT);
//        date.setBackgroundColor(Color.TRANSPARENT);
//        amount.setBackgroundColor(Color.TRANSPARENT);
//        itemDescription.setBackgroundColor(Color.TRANSPARENT);
//        endDate.setBackgroundColor(Color.TRANSPARENT);
//        transactionInterval.setBackgroundColor(Color.TRANSPARENT);
//        spinnerType.setBackgroundResource(R.drawable.spinner_transparent_background);
//    }
//}

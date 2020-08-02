package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;

import android.content.Context;
import android.database.Cursor;

import androidx.fragment.app.FragmentActivity;

import com.github.mikephil.charting.data.BarEntry;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

public class TransactionListPresenter implements ITransactionListPresenter, TransactionListInteractor.OnTransactionsGetDone, TransactionListInteractor.OnFilterSortDone {
    private static ITransactionListView view;
    private Context context;
    private static ArrayList<Transaction> transactions;
    private static ArrayList<Transaction> transactionsWithRegular;
    private ITransactionListInteractor transactionListInteractor;


    public TransactionListPresenter(ITransactionListView view, Context context) {
        this.view       = view;
        this.context    = context;
        this.transactionListInteractor = new TransactionListInteractor();
    }

    public TransactionListPresenter(Context context) {
        this.context = context;
        this.transactionListInteractor = new TransactionListInteractor();
    }
    public TransactionListPresenter(FragmentActivity activity) {

    }

    public TransactionListPresenter() {
        this.transactionListInteractor = new TransactionListInteractor();
    }

    @Override
    public void saveTransaction(Transaction transaction, String transactionAction) {
        Boolean duplicate = false;
        if((transactionAction.equals("EDIT") || transactionAction.equals("DELETE")) && isTransactionInDB(transaction)) duplicate = true;
        transactionListInteractor.saveTransactionDB(transaction,context.getApplicationContext(),transactionAction,duplicate);
    }

    @Override
    public void deleteTransaction(Transaction transaction) {
        transactionListInteractor.deleteTransactionDB(transaction,context.getApplicationContext());
    }

    @Override
    public boolean isTransactionInDB(Transaction transaction) {
        Cursor cr = transactionListInteractor.getTransactionCursor(context.getApplicationContext());
        Boolean here = false;
        //todo ako ne bude radilo onda kroz cr petljom
        if(transaction.getInternalId()==null || transaction.getInternalId().equals("null")) return false;
        Transaction tr = transactionListInteractor.getTransaction(context.getApplicationContext(),transaction.getInternalId());
        if(tr!=null) return true;
        else return false;
    }
    @Override
    public void getTransactions(){
        try {
            transactions = new TransactionListInteractor((TransactionListInteractor.OnTransactionsGetDone)this).execute("/account/baa446d6-dbc1-4975-968f-2efde5ba4eaa/transactions?page=","Get").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getTransactionsCursor() {
        view.setCursor(transactionListInteractor.getTransactionCursor(context.getApplicationContext()));
    }

    @Override
    public void getTransactionsCursorWODelete() {
        view.setCursor(transactionListInteractor.getTransactionCursorWODeleteTransactions(context.getApplicationContext()));
    }

    @Override
    public void transferFromDB() {
        ArrayList<Transaction> transForAdding = getTransactionFromCursor(transactionListInteractor.getTransactionCursorADD(context.getApplicationContext()));
        for(Transaction t: transForAdding) {
            addTransaction(t);
            deleteTransaction(t);
        }
        ArrayList<Transaction> transForEdit = getTransactionFromCursor(transactionListInteractor.getTransactionCursorEDIT(context.getApplicationContext()));
        for(Transaction t: transForEdit) {
            if(t.getId().equals("null") || t.getId()==null) {
                addTransaction(t);
            }else {
                editTransaction(t);
            }
            deleteTransaction(t);
        }

        ArrayList<Transaction> transForDeleting = getTransactionFromCursor(transactionListInteractor.getTransactionCursorDELETE(context.getApplicationContext()));
        for(Transaction t: transForDeleting) {
            if(t.getId()==null || t.getId().equals("null")) {
                deleteTransaction(t);
            }else {
                deleteTransaction(t.getId());
                deleteTransaction(t);
            }
        }
    }


    public  ArrayList<Transaction> getTransactionFromCursor(Cursor cursor) {
        ArrayList<Transaction> result = new ArrayList<>();
        Transaction tr = null;
        if(cursor!=null) {
            while(cursor.moveToNext()) {
                int idPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_ID);
                int internalId = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID);
                int amountPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_AMOUNT);
                int titlePos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TITLE);
                int typePos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_TYPE);
                int datePos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_DATE);
                int endDatePos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_END_DATE);
                int itemDescriptionPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_ITEM_DESCRIPTION);
                int intervalPos = cursor.getColumnIndexOrThrow(TransactionDBOpenHelper.TRANSACTION_INTERVAL);
                LocalDate endDate = null;
                if(!cursor.getString(endDatePos).equals("null")) {
                    endDate = LocalDate.parse(cursor.getString(endDatePos));
                }
                String itemDescription ="";
                if(!cursor.getString(itemDescriptionPos).equals("null")) {
                    itemDescription = cursor.getString(itemDescriptionPos);
                }
                Integer idNullable = null;
                if(!cursor.getString(idPos).equals("null")) {
                    idNullable = Integer.parseInt(cursor.getString(idPos));
                }

                tr = new Transaction(LocalDate.parse(cursor.getString(datePos)), Double.parseDouble(cursor.getString(amountPos)),
                        cursor.getString(titlePos), Integer.parseInt(cursor.getString(typePos)), itemDescription,
                        Integer.parseInt(cursor.getString(intervalPos)), endDate,
                        idNullable, Integer.parseInt(cursor.getString(internalId)));
                result.add(tr);
            }
            cursor.close();
        }
        return result;
    }

    @Override
    public void addTransaction(Transaction transaction) {
        new TransactionListInteractor((TransactionListInteractor.OnTransactionsGetDone)this,transaction).execute("","Post");
    }

    @Override
    public void editTransaction(Transaction transaction) {
        new TransactionListInteractor((TransactionListInteractor.OnTransactionsGetDone) this,transaction).execute(transaction.getId().toString(),"Edit");
    }

    @Override
    public void deleteTransaction(Integer id) {
        new TransactionListInteractor((TransactionListInteractor.OnTransactionsGetDone) this).execute(id.toString(),"Delete");
    }

    @Override
    public ArrayList<Transaction> getAll() {
        return null;
    }

    @Override
    public void getFilteredSortedTransactions(Integer page,Integer typeId,String sort,String month,String year) {
//        if(typeId!=null && (typeId==1 || typeId==2)) {
//            transactions = new ArrayList<>();
//            return;
//        }
        if(month != null && month.length()==1) month = "0"+month;
        if(typeId!=null && typeId == 0) typeId = null;
        if(page == null && typeId == null) {
            try {
                transactions = new TransactionListInteractor(this).execute("/account/baa446d6-dbc1-4975-968f-2efde5ba4eaa/transactions/filter?sort="
                        +sort+"&month="+month+"&year="+year,"Sort").get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else if(page == null) {
            try {
                transactions = new TransactionListInteractor(this).execute("/account/baa446d6-dbc1-4975-968f-2efde5ba4eaa/transactions/filter?typeId="+typeId+"&sort="
                        +sort+"&month="+month+"&year="+year,"Sort").get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else if (typeId == null) {
            try {
                transactions = new TransactionListInteractor(this).execute("/account/baa446d6-dbc1-4975-968f-2efde5ba4eaa/transactions/filter?page="+page+"&sort="
                        +sort+"&month="+month+"&year="+year,"Sort").get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            try {
                transactions = new TransactionListInteractor(this).execute("/account/baa446d6-dbc1-4975-968f-2efde5ba4eaa/transactions/filter?page="+page+"&typeId="+typeId+"&sort="
                        +sort+"&month="+month+"&year="+year, "Sort").get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void getRegularTransactions() {
            try {
                transactionsWithRegular = new TransactionListInteractor(this)
                        .execute("/account/baa446d6-dbc1-4975-968f-2efde5ba4eaa/transactions/filter?"+"typeId=1&"+"sort="+view.getSort(),"Sort").get();
                ArrayList<Transaction> transactionsIncome = new TransactionListInteractor(this)
                        .execute("/account/baa446d6-dbc1-4975-968f-2efde5ba4eaa/transactions/filter?"+"typeId=2&"+"sort="+view.getSort(),"Sort").get();
                transactionsWithRegular.addAll(transactionsIncome);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

    }

    @Override
    public void onDone(ArrayList<Transaction> results) {
//        if(view!=null) {
//            transactions = results;
//            view.setTransactions(results);
//            view.notifyTransactionListDataSetChanged();
//        }
        if(view!=null) {
            transactions = results;
        }
    }

    @Override
    public void onFilterSortDone(ArrayList<Transaction> results) {
//        view.setTransactions(results);
//        view.notifyTransactionListDataSetChanged();
        transactions = results;
    }


    //    private static ITransactionListView view;
//    private ITransactionListInteractor interactor;
//    private static Context context;
//
//
//    public TransactionListPresenter(ITransactionListView view, Context context) {
//        this.view = view;
//        this.interactor = new TransactionListInteractor();
//        this.context = context;
//        getInteractor().setTransactionsForMonth(interactor.get());
//        System.out.println("INTERACTOOOR");
//        System.out.println(interactor.get().size());
//        System.out.println(getInteractor().getTransactionsForMonth().size());
//
//    }
//
//    TransactionListPresenter(FragmentActivity activity) {
//        this.interactor = new TransactionListInteractor();
//        getInteractor().setTransactionsForMonth(interactor.get());
//    }
//
//    @Override
//    public void refresh() {
//        view.setTransactions(interactor.get());
//        view.notifyTransactionListDataSetChanged();
//    }
//
//
//
    @Override
    public void onLeftButtonClicked() {
        view.setDate(view.getDate().plusMonths(-1));
        view.setMonthText(view.getDate());
    }
//
    @Override
    public void onRightButtonClicked() {
        view.setDate(view.getDate().plusMonths(1));
        view.setMonthText(view.getDate());
    }

//    public ArrayList<Transaction> getAll() {
//
//        return transactionsWithRegular;
//    }
    public ArrayList<Transaction> getTransactions1() {
        return transactions;
    }

    public ArrayList<Transaction> getTransactions2() {
        return transactionsWithRegular;
    }
    @Override
    public ArrayList<Transaction> getFilteredandSortedTransactions(ArrayList<Transaction> transactions, Integer filter, Integer sort, LocalDate date) {
        ArrayList<Transaction> results = new ArrayList<>();
        for(Transaction t: transactions) {
            if(t.getEndDate()!=null && (filter==0 || filter.equals(t.getType().getId())) && (t.getType().getName().equals("Regular payment") || t.getType().getName().equals("Regular income"))) {
                LocalDate tmp =t.getDate();
                while(tmp.isBefore(t.getEndDate())) {
                    if(tmp.getMonth() == date.getMonth() && tmp.getYear() == date.getYear()) {
                        results.add(t);
                    }
                    tmp = tmp.plusDays(t.getTransactionInterval());
                }
            }
            else if((filter==0 || filter.equals(t.getType().getId())) && t.getDate().getMonth()==date.getMonth() && t.getDate().getYear()==date.getYear()) {
                results.add(t);
            }
        }
//        ArrayList<Transaction> result2 = new ArrayList<>();
//        for(Transaction t: results) {
//            if(filter==1 && t.getType().getName().equals("Regular payment")) result2.add(t);
//            if(filter==2 && t.getType().getName().equals("Regular income")) result2.add(t);
//            if(filter==3 && t.getType().getName().equals("Purchase")) result2.add(t);
//            if(filter==4 && t.getType().getName().equals("Individual income")) result2.add(t);
//            if(filter==5 && t.getType().getName().equals("Individual payment")) result2.add(t);
//            if(filter==0) results.add(t);
//        }
        results = sortBy(sort,results);
        return results;

//        for(Transaction t: result) {
//            if( (t.getType().getName().equals("Regular payment") || t.getType().getName().equals("Regular income")) && t.getEndDate()!=null
//            && (t.getDate().isBefore(view.getDate()) || t.getDate().isEqual(view.getDate())) &&
//                    ((t.getEndDate().isAfter(view.getDate()) || t.getEndDate().isEqual(view.getDate())) )) {
//                //TODO napravi fju da tacno izracuna
////                if(t.getTransactionInterval()!=0) {
////                    Integer frequency = 30/t.getTransactionInterval();
////                    for(int i=0;i<frequency;i++) result2.add(t);
////                }
//
//            }else if( !(t.getType().getName().equals("Regular payment") || t.getType().getName().equals("Regular income")) &&
//            t.getDate().getYear()==date.getYear() && t.getDate().getMonth()== date.getMonth()) result2.add(t);
    }
//
//    @Override
//    public void filterBy(int filterCriterion) {
//        refreshForMonth();
//        ArrayList<Transaction> temp = new ArrayList<>();
//        for(Transaction t: getInteractor().getTransactionsForMonth()) {
//            if(filterCriterion==1 && t.getType()==TransactionType.INDIVIDUALPAYMENT)  {
//                temp.add(t);
//            }
//            if(filterCriterion==2 && t.getType()==TransactionType.REGULARPAYMENT) temp.add(t);
//            if(filterCriterion==3 && t.getType()==TransactionType.PURCHASE) temp.add(t);
//            if(filterCriterion==4 && t.getType()==TransactionType.INDIVIDUALINCOME) temp.add(t);
//            if(filterCriterion==5 && t.getType()==TransactionType.REGULARINCOME) temp.add(t);
//            if(filterCriterion==0) temp.add(t);
//
//        }
//        getInteractor().setTransactionsForMonth(temp);
//        view.setTransactions(getInteractor().getTransactionsForMonth());
//        view.notifyTransactionListDataSetChanged();
//    }
//
//    @Override
//    public void refreshForMonth() {
//        view.setTransactions(new ArrayList<>());
//        getInteractor().setTransactionsForMonth(new ArrayList<>());
//        System.out.println("OVOOO");
//        System.out.println(interactor.get().size());
//
//        for(Transaction t: interactor.get()) {
//            System.out.println("OVDJEEEEEEEEEEE");
//            System.out.println(getInteractor().get().size());
//            System.out.println(t.getDate().toString());
//            if((t.getType()== TransactionType.REGULARINCOME || t.getType()== TransactionType.REGULARPAYMENT)
//                    && (t.getDate().isBefore(view.getDate()) || t.getDate().isEqual(view.getDate())) &&
//                    (t.getEndDate().isAfter(view.getDate()) || t.getEndDate().isEqual(view.getDate()))) {
//                getInteractor().addTransactionsForMonth(t);
//            }
//            else if(t.getDate().getMonth().equals(view.getDate().getMonth()) && t.getDate().getYear()==(view.getDate().getYear())) {
//                getInteractor().addTransactionsForMonth(t);
//            }
//        }
//        Collections.sort(getInteractor().getTransactionsForMonth(), new Comparator<Transaction>() {
//            @Override
//            public int compare(Transaction a, Transaction b) {
//                return (int) (a.getAmount()-b.getAmount());
//            }
//        });
//        view.setTransactions(getInteractor().getTransactionsForMonth());
//        view.notifyTransactionListDataSetChanged();
//    }
//
    public ArrayList<Transaction> sortBy(int sortCriterion, ArrayList<Transaction> transactions) {
        if(sortCriterion==0)
        Collections.sort(transactions, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction a, Transaction b) {
                return (int) (a.getAmount()-b.getAmount());
            }
        });
        //TODO Da li moze direktno sortirati odavdje
        if(sortCriterion ==1)
            Collections.sort(transactions, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction a, Transaction b) {
                    return (int) (b.getAmount()-a.getAmount());
                }
            });
        if(sortCriterion == 2)
            Collections.sort(transactions, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction a, Transaction b) {
                    return a.getTitle().compareTo(b.getTitle());
                }
            });
        if(sortCriterion ==3)
            Collections.sort(transactions, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction a, Transaction b) {
                    return b.getTitle().compareTo(a.getTitle());
                }
            });
        if(sortCriterion == 4)
            Collections.sort(transactions, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction a, Transaction b) {
                    return a.getDate().compareTo(b.getDate());
                }
            });
        if(sortCriterion == 5)
            Collections.sort(transactions, new Comparator<Transaction>() {
                @Override
                public int compare(Transaction a, Transaction b) {
                    return b.getDate().compareTo(a.getDate());
                }
            });
        return transactions;
    }
    @Override
    public Double getMonthBudget(LocalDate date) {
        ArrayList<Transaction> tmp = getTransactions1();
        ArrayList<Transaction> transactions = getFilteredandSortedTransactions(tmp,0,0,date);
        Double sum = 0.0;
        for(Transaction t: transactions) {
            if(t.getType().getName().equals("Regular income") || t.getType().getName().equals("Individual income")) {
                sum+=t.getAmount();
            }else {
                sum-=t.getAmount();
            }
        }
        return sum;
    }
//
//    @Override
//    public void changeTransaction(int resultPosition, Transaction transactionForSaving) {
//        getInteractor().changeTransactionInTransactionsForMonth(resultPosition,transactionForSaving);
//        view.setTransactions(getInteractor().getTransactionsForMonth());
//        view.notifyTransactionListDataSetChanged();
//        refreshForMonth();
//    }
//
//    @Override
//    public void deleteTransaction(final int resultPosition, Transaction transactionForDeleting) {
//        new AlertDialog.Builder(context)
//                .setTitle("Delete Transaction")
//                .setMessage("Are you sure you want to delete this transaction?")
//
//
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        getInteractor().removeFromTransactionsForMonth(resultPosition);
//                        view.setTransactions(getInteractor().getTransactionsForMonth());
//                        view.notifyTransactionListDataSetChanged();
//                    }
//                })
//
//                .setNegativeButton(android.R.string.no, null)
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .show();
//
//    }
//
//    @Override
//    public void addTransaction(Transaction transactionForAdding) {
//        getInteractor().addTransactionsForMonth(transactionForAdding);
//        getInteractor().add(transactionForAdding);
//        view.setTransactions(getInteractor().getTransactionsForMonth());
//        view.notifyTransactionListDataSetChanged();
//    }
//
//    @Override
//    public void refreshBudget() {
//        double amount =0;
//        for(Transaction t: getInteractor().getTransactionsForMonth()) {
//            if(t.getType()==TransactionType.REGULARINCOME || t.getType()==TransactionType.INDIVIDUALINCOME)
//                amount+=t.getAmount();
//            else amount-=t.getAmount();
//        }
//        view.refreshBudgetInView(amount);
//    }
//
//    @Override
//    public void refreshMonthlyBudgets() {
//        view.refreshMonthlyBudgetsInView(getMonthlyBudgets());
//    }
//    @Override
//    public ArrayList<Integer> getMonthlyBudgets() {
//        ArrayList<Integer> budgets= new ArrayList<>();
//        for(int i=1;i<=12;i++) budgets.add(0);
//        for(Transaction t: getInteractor().getTransactionsForMonth()) {
//            if(t.getType()==TransactionType.REGULARINCOME || t.getType()==TransactionType.INDIVIDUALINCOME) {
//                double temp = budgets.get(t.getDate().getMonthValue());
//                budgets.set(t.getDate().getMonthValue(), (int) (temp+t.getAmount()));
//            }
//            else {
//                double temp = budgets.get(t.getDate().getMonthValue());
//                budgets.set(t.getDate().getMonthValue(), (int) (temp-t.getAmount()));
//            }
//        }
//        return budgets;
//    }
//    @Override
//    public void refreshList() {
//        view.setTransactions(getInteractor().getTransactionsForMonth());
//        view.notifyTransactionListDataSetChanged();
//    }
    @Override
    public ArrayList<BarEntry> potrosnjaPoMjesecima() {
        LocalDate date = LocalDate.of(2020, 1, 01);
        ArrayList<BarEntry> result = new ArrayList<>();
        ArrayList<Double> potrosnja = new ArrayList<>();
        for (int i = 0; i < 12; i++) potrosnja.add(0.0);
        for (int month = 1; month <= 12; month++) {
            date = date.withMonth(month);
            ArrayList<Transaction> transactions = getFilteredandSortedTransactions(getTransactions1(), 0, 0, date);
            double suma = 0;
            for (Transaction t : transactions) {
                if (t.getType().getId() == 5 || t.getType().getId() == 1
                        || t.getType().getId() == 3) {
                    suma += t.getAmount();
                }
            }
            potrosnja.set(month - 1, suma);
        }
        for (int i = 1; i <= 12; i++) {
            result.add(new BarEntry(i, potrosnja.get(i - 1).floatValue()));
        }
        return result;
    }

    @Override
    public ArrayList<BarEntry> zaradaPoMjesecima() {
        LocalDate date = LocalDate.of(2020, 1, 01);
        ArrayList<BarEntry> result = new ArrayList<>();
        ArrayList<Double> zarada = new ArrayList<>();
        for (int i = 0; i < 12; i++) zarada.add(0.0);
        for (int month = 1; month <= 12; month++) {
            date = date.withMonth(month);
            ArrayList<Transaction> transactions = getFilteredandSortedTransactions(getTransactions1(), 0, 0, date);
            double suma = 0;
            for (Transaction t : transactions) {
                if (t.getType().getId() == 4 || t.getType().getId() == 2) {
                    suma += t.getAmount();
                }
            }
            zarada.set(month - 1, suma);
        }
        for (int i = 1; i <= 12; i++) {
            result.add(new BarEntry(i, zarada.get(i - 1).floatValue()));
        }
        return result;
    }

    @Override
    public ArrayList<BarEntry> ukupnoStanjePoMjesecima() {
        LocalDate date = LocalDate.of(2020, 1, 01);
        ArrayList<BarEntry> result = new ArrayList<>();
        ArrayList<Double> ukupnoStanje = new ArrayList<>();
        for (int i = 0; i < 12; i++) ukupnoStanje.add(0.0);
        for (int month = 1; month <= 12; month++) {
            date = date.withMonth(month);
            ArrayList<Transaction> transactions = getFilteredandSortedTransactions(getTransactions1(), 0, 0, date);
            double suma = 0;
            for (Transaction t : transactions) {
                if (t.getType().getId() == 4 || t.getType().getId() == 2) {
                    suma += t.getAmount();
                }
                if (t.getType().getId() == 5 || t.getType().getId() == 1
                        || t.getType().getId() == 3) {
                    suma -= t.getAmount();
                }
            }
            ukupnoStanje.set(month - 1, suma);
        }
        for (int i = 1; i <= 12; i++) {
            result.add(new BarEntry(i, ukupnoStanje.get(i - 1).floatValue()));
        }
        return result;
    }

    @Override
    public ArrayList<BarEntry> potrosnjaPoDanima() {
        ArrayList<BarEntry> result = new ArrayList<>();
        ArrayList<Double> potrosnja = new ArrayList<>();
        Integer month = view.getDate().getMonthValue();
        if(view.getDate().getMonth()== Month.FEBRUARY && view.getDate().isLeapYear()) {
            for(int i=1;i<=29;i++) potrosnja.add(0.);
        }else if(view.getDate().getMonth()==Month.FEBRUARY){
            for(int i=1;i<=28;i++) potrosnja.add(0.);
        }
        else if((month <8 && month%2==1) || (month>=8 && month%2==0)) {
            for(int i=1;i<=31;i++) potrosnja.add(0.);
        }else {
            for(int i=1;i<=30;i++) potrosnja.add(0.);
        }
        LocalDate date = LocalDate.of(2020,1,1);
        date = date.withMonth(month);
        for(Transaction t: getTransactions1()) {
            if(t.getEndDate()!=null &&  (t.getType().getName().equals("Regular payment"))) {
                LocalDate tmp =t.getDate();
                while(tmp.isBefore(t.getEndDate())) {
                    double suma = potrosnja.get(tmp.getDayOfMonth()-1);
                    if(tmp.getMonth() == date.getMonth() && tmp.getYear() == date.getYear()) {
                        potrosnja.set(tmp.getDayOfMonth()-1,suma+t.getAmount());
                    }
                    tmp = tmp.plusDays(t.getTransactionInterval());
                }
            }
            else if(t.getDate().getMonth()==view.getDate().getMonth() && t.getDate().getYear()==view.getDate().getYear() && (t.getType().getId()==5
                    || t.getType().getId()==3)) {
                double suma = potrosnja.get(t.getDate().getDayOfMonth()-1);
                potrosnja.set(t.getDate().getDayOfMonth()-1,suma+t.getAmount());
            }
        }
        for(int i=0;i<potrosnja.size();i++) {
            result.add(new BarEntry(i+1,potrosnja.get(i).floatValue()));
        }
        return result;
    }

    @Override
    public ArrayList<BarEntry> zaradaPoDanima() {
        ArrayList<BarEntry> result = new ArrayList<>();
        ArrayList<Double> potrosnja = new ArrayList<>();
        Integer month = view.getDate().getMonthValue();
        if(view.getDate().getMonth()== Month.FEBRUARY && view.getDate().isLeapYear()) {
            for(int i=1;i<=29;i++) potrosnja.add(0.);
        }else if(view.getDate().getMonth()==Month.FEBRUARY){
            for(int i=1;i<=28;i++) potrosnja.add(0.);
        }
        else if((month <8 && month%2==1) || (month>=8 && month%2==0)) {
            for(int i=1;i<=31;i++) potrosnja.add(0.);
        }else {
            for(int i=1;i<=30;i++) potrosnja.add(0.);
        }
        LocalDate date = LocalDate.of(2020,1,1);
        date = date.withMonth(month);
        for(Transaction t: getTransactions1()) {
            if(t.getEndDate()!=null &&  (t.getType().getName().equals("Regular income"))) {
                LocalDate tmp =t.getDate();
                while(tmp.isBefore(t.getEndDate())) {
                    double suma = potrosnja.get(tmp.getDayOfMonth()-1);
                    if(tmp.getMonth() == date.getMonth() && tmp.getYear() == date.getYear()) {
                        potrosnja.set(tmp.getDayOfMonth()-1,suma+t.getAmount());
                    }
                    tmp = tmp.plusDays(t.getTransactionInterval());
                }
            }
            else if(t.getDate().getMonth()==view.getDate().getMonth() && t.getDate().getYear()==view.getDate().getYear() && (t.getType().getId()==4)) {
                double suma = potrosnja.get(t.getDate().getDayOfMonth()-1);
                potrosnja.set(t.getDate().getDayOfMonth()-1,suma+t.getAmount());
            }
        }
        for(int i=0;i<potrosnja.size();i++) {
            result.add(new BarEntry(i+1,potrosnja.get(i).floatValue()));
        }
        return result;
//        ArrayList<BarEntry> result = new ArrayList<>();
//        ArrayList<Double> zarada = new ArrayList<>();
//        Integer month = view.getDate().getMonthValue();
//        if(view.getDate().getMonth()== Month.FEBRUARY && view.getDate().isLeapYear()) {
//            for(int i=1;i<=29;i++) zarada.add(0.);
//        }else if(view.getDate().getMonth()==Month.FEBRUARY){
//            for(int i=1;i<=28;i++) zarada.add(0.);
//        }
//        else if((month <8 && month%2==1) || (month>=8 && month%2==0)) {
//            for(int i=1;i<=31;i++) zarada.add(0.);
//        }else {
//            for(int i=1;i<=30;i++) zarada.add(0.);
//        }
//        LocalDate date = LocalDate.of(2020,1,1);
//        date = date.withMonth(month);
//        ArrayList<Transaction> transactions = getFilteredandSortedTransactions(getTransactions1(),0,0,date);
//        for(Transaction t: transactions) {
//            double suma = zarada.get(t.getDate().getDayOfMonth()-1);
//            if((t.getType().getId()==4 || t.getType().getId()==2)) {
//                zarada.set(t.getDate().getDayOfMonth()-1,suma+t.getAmount());
//            }
//        }
//        for(int i=0;i<zarada.size();i++) {
//            result.add(new BarEntry(i+1,zarada.get(i).floatValue()));
//        }
//        return result;
//        ArrayList<BarEntry> result = new ArrayList<>();
//        ArrayList<Double> zarada = new ArrayList<>();
//        Integer month = view.getDate().getDayOfMonth();
//        if(view.getDate().getMonth()== Month.FEBRUARY && view.getDate().isLeapYear()) {
//            for(int i=1;i<=29;i++) zarada.add(0.);
//        }else if(view.getDate().getMonth()==Month.FEBRUARY){
//            for(int i=1;i<=28;i++) zarada.add(0.);
//        }
//        else if((month <8 && month%2==1) || (month>=8 && month%2==0)) {
//            for(int i=1;i<=31;i++) zarada.add(0.);
//        }else {
//            for(int i=1;i<=30;i++) zarada.add(0.);
//        }
//        for(Transaction t: transactions) {
//            double suma = zarada.get(t.getDate().getDayOfMonth());
//            if(t.getDate().getMonth()==view.getDate().getMonth() && t.getDate().getYear()==view.getDate().getYear() &&
//                    (t.getType().getId()==4 || t.getType().getId()==2)) {
//                zarada.set(t.getDate().getDayOfMonth(),suma+t.getAmount());
//            }
//        }
//        for(int i=0;i<zarada.size();i++) {
//            result.add(new BarEntry(i+1,zarada.get(i).floatValue()));
//        }
//        return result;
    }

    @Override
    public ArrayList<BarEntry> ukupnoStanjePoDanima() {
//        ArrayList<BarEntry> result = new ArrayList<>();
//        ArrayList<Double> ukupnoStanje = new ArrayList<>();
//        Integer month = view.getDate().getDayOfMonth();
//        if(view.getDate().getMonth()== Month.FEBRUARY && view.getDate().isLeapYear()) {
//            for(int i=1;i<=29;i++) ukupnoStanje.add(0.);
//        }else if(view.getDate().getMonth()==Month.FEBRUARY){
//            for(int i=1;i<=28;i++) ukupnoStanje.add(0.);
//        }
//        else if((month <8 && month%2==1) || (month>=8 && month%2==0)) {
//            for(int i=1;i<=31;i++) ukupnoStanje.add(0.);
//        }else {
//            for(int i=1;i<=30;i++) ukupnoStanje.add(0.);
//        }
//        for(Transaction t: transactions) {
//            double suma = ukupnoStanje.get(t.getDate().getDayOfMonth());
//            if(t.getDate().getMonth()==view.getDate().getMonth() && t.getDate().getYear()==view.getDate().getYear() &&
//                    (t.getType().getId()==5 || t.getType().getId()==1
//                            || t.getType().getId()==3)) {
//                ukupnoStanje.set(t.getDate().getDayOfMonth(),suma-t.getAmount());
//            }
//            if(t.getDate().getMonth()==view.getDate().getMonth() && t.getDate().getYear()==view.getDate().getYear() &&
//                    (t.getType().getId()==4 || t.getType().getId()==2)) {
//                ukupnoStanje.set(t.getDate().getDayOfMonth(),suma+t.getAmount());
//            }
//
//        }
//        for(int i=0;i<ukupnoStanje.size();i++) {
//            result.add(new BarEntry(i+1,ukupnoStanje.get(i).floatValue()));
//        }
//        return result;
//        ArrayList<BarEntry> result = new ArrayList<>();
//        ArrayList<Double> ukupnoStanje = new ArrayList<>();
//        Integer month = view.getDate().getMonthValue();
//        if(view.getDate().getMonth()== Month.FEBRUARY && view.getDate().isLeapYear()) {
//            for(int i=1;i<=29;i++) ukupnoStanje.add(0.);
//        }else if(view.getDate().getMonth()==Month.FEBRUARY){
//            for(int i=1;i<=28;i++) ukupnoStanje.add(0.);
//        }
//        else if((month <8 && month%2==1) || (month>=8 && month%2==0)) {
//            for(int i=1;i<=31;i++) ukupnoStanje.add(0.);
//        }else {
//            for(int i=1;i<=30;i++) ukupnoStanje.add(0.);
//        }
//        LocalDate date = LocalDate.of(2020,1,1);
//        date = date.withMonth(month);
//        ArrayList<Transaction> transactions = getFilteredandSortedTransactions(getTransactions1(),0,0,date);
//        for(Transaction t: transactions) {
//            double suma = ukupnoStanje.get(t.getDate().getDayOfMonth()-1);
//            if((t.getType().getId()==5 || t.getType().getId()==1
//                    || t.getType().getId()==3)) {
//                ukupnoStanje.set(t.getDate().getDayOfMonth()-1,suma+t.getAmount());
//            }
//            if((t.getType().getId()==4 || t.getType().getId()==2)) {
//                ukupnoStanje.set(t.getDate().getDayOfMonth(),suma+t.getAmount());
//            }
//
//        }
//        for(int i=0;i<ukupnoStanje.size();i++) {
//            result.add(new BarEntry(i+1,ukupnoStanje.get(i).floatValue()));
//        }
//        return result;
        ArrayList<BarEntry> result = new ArrayList<>();
        ArrayList<Double> potrosnja = new ArrayList<>();
        Integer month = view.getDate().getMonthValue();
        if(view.getDate().getMonth()== Month.FEBRUARY && view.getDate().isLeapYear()) {
            for(int i=1;i<=29;i++) potrosnja.add(0.);
        }else if(view.getDate().getMonth()==Month.FEBRUARY){
            for(int i=1;i<=28;i++) potrosnja.add(0.);
        }
        else if((month <8 && month%2==1) || (month>=8 && month%2==0)) {
            for(int i=1;i<=31;i++) potrosnja.add(0.);
        }else {
            for(int i=1;i<=30;i++) potrosnja.add(0.);
        }
        LocalDate date = LocalDate.of(2020,1,1);
        date = date.withMonth(month);
        for(Transaction t: getTransactions1()) {
            if(t.getEndDate()!=null &&  (t.getType().getName().equals("Regular payment") || t.getType().getName().equals("Regular income"))) {
                LocalDate tmp =t.getDate();
                while(tmp.isBefore(t.getEndDate())) {
                    double suma = potrosnja.get(tmp.getDayOfMonth()-1);
                    if(tmp.getMonth() == date.getMonth() && tmp.getYear() == date.getYear()) {
                        if(t.getType().getName().equals("Regular income")) potrosnja.set(tmp.getDayOfMonth()-1,suma+t.getAmount());
                        else if(t.getType().getName().equals("Regular payment")) potrosnja.set(tmp.getDayOfMonth()-1,suma-t.getAmount());
                    }
                    tmp = tmp.plusDays(t.getTransactionInterval());
                }
            }
            else if(t.getDate().getMonth()==view.getDate().getMonth() && t.getDate().getYear()==view.getDate().getYear() && (t.getType().getId()==4
                    || t.getType().getId()==2)) {
                double suma = potrosnja.get(t.getDate().getDayOfMonth()-1);
                potrosnja.set(t.getDate().getDayOfMonth()-1,suma+t.getAmount());
            }
            else if(t.getDate().getMonth()==view.getDate().getMonth() && t.getDate().getYear()==view.getDate().getYear() && (t.getType().getId()==3)) {
                double suma = potrosnja.get(t.getDate().getDayOfMonth()-1);
                potrosnja.set(t.getDate().getDayOfMonth()-1,suma-t.getAmount());
            }

        }
        for(int i=0;i<potrosnja.size();i++) {
            result.add(new BarEntry(i+1,potrosnja.get(i).floatValue()));
        }
        return result;
    }

    @Override
    public ArrayList<BarEntry> potrosnjaPoSedmicama() {
        ArrayList<BarEntry> result = new ArrayList<>();
        ArrayList<Double> potrosnja= new ArrayList<>();
        for(int i=0;i<4;i++) potrosnja.add(0.0);
        LocalDate date = view.getDate();
        ArrayList<Transaction> transactions = getFilteredandSortedTransactions(getTransactions1(),0,0,date);
        for(Transaction t: transactions) {
            int redniBroj = t.getDate().getDayOfMonth();
            int pozicija;
            if(redniBroj<=7) pozicija = 0;
            else if(redniBroj<=14) pozicija = 1;
            else if(redniBroj<=21) pozicija = 2;
            else pozicija = 3;

            double suma = potrosnja.get(pozicija);
            if((t.getType().getId()==5 || t.getType().getId()==1
                    || t.getType().getId()==3)) {
                potrosnja.set(pozicija,suma+t.getAmount());
            }
        }
        for(int i=1;i<=4;i++) {
            result.add(new BarEntry(i,potrosnja.get(i-1).floatValue()));
        }
        return result;
    }

    @Override
    public ArrayList<BarEntry> zaradaPoSedmicama() {
        ArrayList<BarEntry> result = new ArrayList<>();
        ArrayList<Double> zarada= new ArrayList<>();
        for(int i=0;i<4;i++) zarada.add(0.0);
        LocalDate date = view.getDate();
        ArrayList<Transaction> transactions = getFilteredandSortedTransactions(getTransactions1(),0,0,date);
        for(Transaction t: transactions) {
            int redniBroj = t.getDate().getDayOfMonth();
            int pozicija;
            if(redniBroj<=7) pozicija = 0;
            else if(redniBroj<=14) pozicija = 1;
            else if(redniBroj<=21) pozicija = 2;
            else pozicija = 3;

            double suma = zarada.get(pozicija);
            if( (t.getType().getId()==4 || t.getType().getId()==2)) {
                zarada.set(pozicija,suma+t.getAmount());
            }
        }
        for(int i=1;i<=4;i++) {
            result.add(new BarEntry(i,zarada.get(i-1).floatValue()));
        }
        return result;
    }

    @Override
    public ArrayList<BarEntry> ukupnoStanjePoSedmicama() {
        ArrayList<BarEntry> result = new ArrayList<>();
        ArrayList<Double> ukupnoStanje= new ArrayList<>();
        for(int i=0;i<4;i++) ukupnoStanje.add(0.0);
        LocalDate date = view.getDate();
        ArrayList<Transaction> transactions = getFilteredandSortedTransactions(getTransactions1(),0,0,date);
        for(Transaction t: transactions) {
            int redniBroj = t.getDate().getDayOfMonth();
            int pozicija;
            if(redniBroj<=7) pozicija = 0;
            else if(redniBroj<=14) pozicija = 1;
            else if(redniBroj<=21) pozicija = 2;
            else pozicija = 3;

            double suma = ukupnoStanje.get(pozicija);
            if(t.getType().getId()==4 || t.getType().getId()==2) {
                ukupnoStanje.set(pozicija,suma+t.getAmount());
            }
            if((t.getType().getId()==5 || t.getType().getId()==1
                            || t.getType().getId()==3)) {
                ukupnoStanje.set(pozicija,suma-t.getAmount());
            }

        }
        for(int i=1;i<=4;i++) {
            result.add(new BarEntry(i,ukupnoStanje.get(i-1).floatValue()));
        }
        return result;
    }

}
package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class TransactionListInteractor extends AsyncTask<String, Integer, ArrayList<Transaction>> implements ITransactionListInteractor{
    private String api_key="";
    ArrayList<Transaction> transactions;
    Transaction transaction;
    private OnTransactionsGetDone caller;
    private OnFilterSortDone caller2;
    private TransactionDBOpenHelper transactionDBOpenHelper;
    SQLiteDatabase database;

    public TransactionListInteractor(OnTransactionsGetDone caller) {
        this.caller = caller;
        transactions = new ArrayList<>();
        transaction = new Transaction();
    }

    public TransactionListInteractor(OnTransactionsGetDone caller,Transaction transaction) {
        this.transaction = transaction;
        this.caller = caller;
        transactions = new ArrayList<>();
    }

    public TransactionListInteractor() {
        //todo super
    }

    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
        return sb.toString();
    }

    @Override
    protected ArrayList<Transaction> doInBackground(String... strings) {
        if(strings[1].equals("Get")) {
            String query = null;
            try {
                query = URLEncoder.encode(strings[0], "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Integer page = 0;
            while(true){
            String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com" + strings[0] + page;
            try {
                URL url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String result = convertStreamToString(in);
                JSONObject jo = new JSONObject(result);
                JSONArray results = jo.getJSONArray("transactions");
                if(results.length()==0) break;
                for (int i = 0; i < results.length(); i++) {
                    JSONObject transaction = results.getJSONObject(i);
                    Integer id = transaction.getInt("id");
                    String date = transaction.optString("date");
                    String title = transaction.getString("title");
                    Double amount = transaction.getDouble("amount");
                    String itemDescription = transaction.optString("itemDescription");
                    Integer transactionInterval = transaction.optInt("transactionInterval");
                    String endDate = transaction.optString("endDate");
                    Integer transactionTypeId = transaction.optInt("TransactionTypeId");
//                    Type type  = transactionType.getTransactionType().get(0);
//                    for(Type t: transactionType.getTransactionType()) {
//                        if(t.getId()==transactionTypeId)  {
//                            type = t;
//                            Integer test =t.getId();
//                        }
//                    }
                    LocalDate date1 = null, date2 = null;
                    if (!date.equals("null")) {
                        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
                        date1 = LocalDate.parse(date, inputFormatter);
                    }
                    if (!endDate.equals("null")) {
                        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
                        date2 = LocalDate.parse(endDate, inputFormatter);
                    }
                    transactions.add(new Transaction(date1, amount, title, transactionTypeId, itemDescription, transactionInterval, date2, id));
                    if (i == 4) break; //TODO page parametar
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
                page++;

            }
        }else if(strings[1].equals("Sort")) {
            String query = null;
            try {
                query = URLEncoder.encode(strings[0], "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com" + strings[0];
            try {
                URL url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String result = convertStreamToString(in);
                JSONObject jo = new JSONObject(result);
                JSONArray results = jo.getJSONArray("transactions");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject transaction = results.getJSONObject(i);
                    Integer id = transaction.getInt("id");
                    String date = transaction.optString("date");
                    String title = transaction.getString("title");
                    Double amount = transaction.getDouble("amount");
                    String itemDescription = transaction.optString("itemDescription");
                    Integer transactionInterval = transaction.optInt("transactionInterval");
                    String endDate = transaction.optString("endDate");
                    Integer transactionTypeId = transaction.optInt("TransactionTypeId");
//                    Type type  = transactionType.getTransactionType().get(0);
//                    for(Type t: transactionType.getTransactionType()) {
//                        if(t.getId()==transactionTypeId)  {
//                            type = t;
//                            Integer test =t.getId();
//                        }
//                    }
                    LocalDate date1 = null, date2 = null;
                    if (!date.equals("null")) {
                        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
                        date1 = LocalDate.parse(date, inputFormatter);
                    }
                    if (!endDate.equals("null")) {
                        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
                        date2 = LocalDate.parse(endDate, inputFormatter);
                    }
                    transactions.add(new Transaction(date1, amount, title, transactionTypeId, itemDescription, transactionInterval, date2, id));
                    if (i == 4) break; //TODO page parametar
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(strings[1].equals("Post")) {
//            String jsonInputString = makeJsonInputString(transaction);
            try {
                URL url = new URL ("http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/baa446d6-dbc1-4975-968f-2efde5ba4eaa/transactions");
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                JSONObject object = new JSONObject();
                object.put("date", transaction.getDate());
                object.put("title",transaction.getTitle());
                object.put("amount",transaction.getAmount());
                object.put("itemDescription",transaction.getItemDescription());
                object.put("transactionInterval",transaction.getTransactionInterval());
                object.put("endDate",transaction.getEndDate());
                object.put("TransactionTypeId",transaction.getType().getId());
                String jsonInputString = object.toString();

                try(OutputStream os = con.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if(strings[1].equals("Edit")) {
            //String jsonInputString = jsonInputString(strings);
            try {
                URL url = new URL ("http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/baa446d6-dbc1-4975-968f-2efde5ba4eaa/transactions/"+strings[0]);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);
//                String jsonInputString = "{\"date\":\"2020-08-02T14:13:00.000Z\",\"title\":\"Test 691\",\"amount\":135,\"itemDescription\":null,\"transactionInterval\":null,\"endDate\":null,\"TransactionTypeId\":3}";
                JSONObject object = new JSONObject();
                object.put("date", transaction.getDate());
                object.put("title",transaction.getTitle());
                object.put("amount",transaction.getAmount());
                object.put("itemDescription",transaction.getItemDescription());
                object.put("transactionInterval",transaction.getTransactionInterval());
                object.put("endDate",transaction.getEndDate());
                object.put("TransactionTypeId",transaction.getType().getId());
                String jsonInputString = object.toString();

                try(OutputStream os = con.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(strings[1].equals("Delete")) {
            //String jsonInputString = jsonInputString(strings);
            try {
                URL url = new URL ("http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/baa446d6-dbc1-4975-968f-2efde5ba4eaa/transactions/"+strings[0]);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("DELETE");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return transactions;
    }

//    @Override
//    protected void onPostExecute(Void aVoid){
//        super.onPostExecute(aVoid);
//        if(caller != null) {
//            caller.onDone(transactions);
//        }
//        else if(caller2 != null) {
//            caller2.onFilterSortDone(transactions);
//        }
//
//    }
    @Override
    public void deleteTransactionDB (Transaction transaction, Context context) {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        Uri transactionsURI = Uri.parse("content://rma.provider.transactions/elements");
        String where = TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID + " = " + transaction.getInternalId();
        cr.delete(transactionsURI, where, null);
    }

    @Override
    public void saveTransactionDB(Transaction transaction, Context context, String transactionAction, Boolean duplicate) {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        Uri transactionsURI = Uri.parse("content://rma.provider.transactions/elements");
        ContentValues values = new ContentValues();
        if(transaction.getId()!=null) values.put(TransactionDBOpenHelper.TRANSACTION_ID,transaction.getId());
        else values.put(TransactionDBOpenHelper.TRANSACTION_ID,"null");
        values.put(TransactionDBOpenHelper.TRANSACTION_TYPE,transaction.getType().getId());
        if(transaction.getItemDescription()!=null) values.put((TransactionDBOpenHelper.TRANSACTION_ITEM_DESCRIPTION),transaction.getItemDescription());
        else values.put(TransactionDBOpenHelper.TRANSACTION_ITEM_DESCRIPTION,"null");
        values.put(TransactionDBOpenHelper.TRANSACTION_INTERVAL,transaction.getTransactionInterval());
        values.put(TransactionDBOpenHelper.TRANSACTION_AMOUNT,transaction.getAmount());
        values.put(TransactionDBOpenHelper.TRANSACTION_TITLE,transaction.getTitle());
        values.put(TransactionDBOpenHelper.TRANSACTION_DATE,transaction.getDate().toString());
        if(transaction.getEndDate()!=null)  values.put(TransactionDBOpenHelper.TRANSACTION_END_DATE,transaction.getEndDate().toString());
        else values.put(TransactionDBOpenHelper.TRANSACTION_END_DATE,"null");
        values.put(TransactionDBOpenHelper.TRANSACTION_ACTION,transactionAction);
        if(transactionAction.equals("ADD")) {
            cr.insert(transactionsURI,values);
        }else if(transactionAction.equals("EDIT") && duplicate){
            String where = TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID + " = " + transaction.getInternalId();
            cr.update(transactionsURI,values,where,null);
        }else if(transactionAction.equals("EDIT")) {
            cr.insert(transactionsURI,values);
        }else if (transactionAction.equals("DELETE") && duplicate) {
            String where = TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID + " = " + transaction.getInternalId();
            cr.update(transactionsURI,values,where,null);
        }else if(transactionAction.equals("DELETE")) {
            cr.insert(transactionsURI,values);
        }
    }

    @Override
    public Transaction getTransaction(Context context, Integer id) {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        String[] kolone = null;
        Uri adresa = ContentUris.withAppendedId(Uri.parse("content://rma.provider.transactions/elements"), id);
        String where = null;
        String whereArgs[] = null;
        String order = null;
        Cursor cursor = cr.query(adresa, kolone, where, whereArgs, order);
        Transaction tr = null;
        if (cursor != null) {
            cursor.moveToFirst();
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
        }
        cursor.close();
        return tr;
    }

    @Override
    public Cursor getTransactionCursor(Context context) {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        String[] kolone = new String[]{
                TransactionDBOpenHelper.TRANSACTION_DATE,
                TransactionDBOpenHelper.TRANSACTION_AMOUNT,
                TransactionDBOpenHelper.TRANSACTION_TITLE,
                TransactionDBOpenHelper.TRANSACTION_TYPE,
                TransactionDBOpenHelper.TRANSACTION_ITEM_DESCRIPTION,
                TransactionDBOpenHelper.TRANSACTION_INTERVAL,
                TransactionDBOpenHelper.TRANSACTION_END_DATE,
                TransactionDBOpenHelper.TRANSACTION_ID,
                TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID

        };
        Uri adresa = Uri.parse("content://rma.provider.transactions/elements");
        String where = null;
        String whereArgs[] = null;
        String order = null;
        Cursor cur = cr.query(adresa,kolone,where,whereArgs,order);
        return cur;
    }

    @Override
    public Cursor getTransactionCursorWODeleteTransactions(Context context) {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        String[] kolone = new String[]{
                TransactionDBOpenHelper.TRANSACTION_DATE,
                TransactionDBOpenHelper.TRANSACTION_AMOUNT,
                TransactionDBOpenHelper.TRANSACTION_TITLE,
                TransactionDBOpenHelper.TRANSACTION_TYPE,
                TransactionDBOpenHelper.TRANSACTION_ITEM_DESCRIPTION,
                TransactionDBOpenHelper.TRANSACTION_INTERVAL,
                TransactionDBOpenHelper.TRANSACTION_END_DATE,
                TransactionDBOpenHelper.TRANSACTION_ID,
                TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID

        };
        Uri adresa = Uri.parse("content://rma.provider.transactions/elements");
        String where = TransactionDBOpenHelper.TRANSACTION_ACTION + " != " + "'DELETE'";
        String whereArgs[] = null;
        String order = null;
        Cursor cur = cr.query(adresa,kolone,where,whereArgs,order);
        return cur;
    }

    @Override
    public Cursor getTransactionCursorADD(Context context) {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        String[] kolone = new String[]{
                TransactionDBOpenHelper.TRANSACTION_DATE,
                TransactionDBOpenHelper.TRANSACTION_AMOUNT,
                TransactionDBOpenHelper.TRANSACTION_TITLE,
                TransactionDBOpenHelper.TRANSACTION_TYPE,
                TransactionDBOpenHelper.TRANSACTION_ITEM_DESCRIPTION,
                TransactionDBOpenHelper.TRANSACTION_INTERVAL,
                TransactionDBOpenHelper.TRANSACTION_END_DATE,
                TransactionDBOpenHelper.TRANSACTION_ID,
                TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID

        };
        Uri adresa = Uri.parse("content://rma.provider.transactions/elements");
        String where = TransactionDBOpenHelper.TRANSACTION_ACTION + " == " + "'ADD'";
        String whereArgs[] = null;
        String order = null;
        Cursor cur = cr.query(adresa,kolone,where,whereArgs,order);
        return cur;
    }

    @Override
    public Cursor getTransactionCursorEDIT(Context context) {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        String[] kolone = new String[]{
                TransactionDBOpenHelper.TRANSACTION_DATE,
                TransactionDBOpenHelper.TRANSACTION_AMOUNT,
                TransactionDBOpenHelper.TRANSACTION_TITLE,
                TransactionDBOpenHelper.TRANSACTION_TYPE,
                TransactionDBOpenHelper.TRANSACTION_ITEM_DESCRIPTION,
                TransactionDBOpenHelper.TRANSACTION_INTERVAL,
                TransactionDBOpenHelper.TRANSACTION_END_DATE,
                TransactionDBOpenHelper.TRANSACTION_ID,
                TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID

        };
        Uri adresa = Uri.parse("content://rma.provider.transactions/elements");
        String where = TransactionDBOpenHelper.TRANSACTION_ACTION + " == " + "'EDIT'";
        String whereArgs[] = null;
        String order = null;
        Cursor cur = cr.query(adresa,kolone,where,whereArgs,order);
        return cur;
    }

    @Override
    public Cursor getTransactionCursorDELETE(Context context) {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        String[] kolone = new String[]{
                TransactionDBOpenHelper.TRANSACTION_DATE,
                TransactionDBOpenHelper.TRANSACTION_AMOUNT,
                TransactionDBOpenHelper.TRANSACTION_TITLE,
                TransactionDBOpenHelper.TRANSACTION_TYPE,
                TransactionDBOpenHelper.TRANSACTION_ITEM_DESCRIPTION,
                TransactionDBOpenHelper.TRANSACTION_INTERVAL,
                TransactionDBOpenHelper.TRANSACTION_END_DATE,
                TransactionDBOpenHelper.TRANSACTION_ID,
                TransactionDBOpenHelper.TRANSACTION_INTERNAL_ID

        };
        Uri adresa = Uri.parse("content://rma.provider.transactions/elements");
        String where = TransactionDBOpenHelper.TRANSACTION_ACTION + " == " + "'DELETE'";
        String whereArgs[] = null;
        String order = null;
        Cursor cur = cr.query(adresa,kolone,where,whereArgs,order);
        return cur;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public interface OnTransactionsGetDone {
        public void onDone(ArrayList<Transaction> results);
    }

    public interface OnFilterSortDone {
        public void onFilterSortDone(ArrayList<Transaction> results);
    }

    private String makeJsonInputString(Transaction t) {
        String result = new String();
        //TODO dodaj navodnike kada itemDesc nije null
//        String jsonInputString = "{\"date\":\"2020-08-02T14:13:00.000Z\",\"title\":\"Test 691\",\"amount\":135,\"itemDescription\":null,\"transactionInterval\":null,\"endDate\":null,\"TransactionTypeId\":3}";
        result+="{\"date\":\""+t.getDate()+"\",\"title\":\""+t.getTitle()+"\",\"amount\":"+t.getAmount()+
                ",\"itemDescription\":"+t.getItemDescription()+",\"transactionInterval\":"+t.getTransactionInterval()+
                ",\"endDate\":"+t.getEndDate()+",\"TransactionTypeId\":"+t.getType().getId()+"}";


        return result;
    }

//    @Override
//    public ArrayList<Transaction> get() {
//        return TransactionModel.transactions;
//    }
//
//    @Override
//    public ArrayList<Transaction> getTransactionsForMonth() { return TransactionModel.transactionsForMonth; }
//
//    @Override
//    public void add(Transaction t) {
//        TransactionModel.transactions.add(t);
//    }
//
//    @Override
//    public void addTransactionsForMonth(Transaction t) { TransactionModel.transactionsForMonth.add(t);}
//
//    @Override
//    public void set(ArrayList<Transaction> transactions) { TransactionModel.transactions = transactions;}
//
//    @Override
//    public void setTransactionsForMonth(ArrayList<Transaction> transactions) { TransactionModel.transactionsForMonth = transactions; }
//
//    @Override
//    public void removeFromTransactionsForMonth(int position) { TransactionModel.transactionsForMonth.remove(position); }
//
//    @Override
//    public void changeTransactionInTransactionsForMonth(int position, Transaction t) {
//        System.out.println("U CHANGEEE");
//        System.out.println(get().size());
//        System.out.println(getTransactionsForMonth().size());
//        TransactionModel.transactionsForMonth.get(position).setAmount(t.getAmount());
//        TransactionModel.transactionsForMonth.get(position).setTitle(t.getTitle());
//        TransactionModel.transactionsForMonth.get(position).setDate(t.getDate());
//        TransactionModel.transactionsForMonth.get(position).setEndDate(t.getEndDate());
//        TransactionModel.transactionsForMonth.get(position).setItemDescription(t.getItemDescription());
//        TransactionModel.transactionsForMonth.get(position).setTransactionInterval(t.getTransactionInterval());
//        TransactionModel.transactionsForMonth.get(position).setType(t.getType());
//    }
}

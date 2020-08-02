package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;



import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.SurfaceControl;

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


public class AccountListInteractor extends AsyncTask<String, Integer, Account> implements IAccountListInteractor {
    private Account account;
    private Account accountForEdit;
    private OnAccountGetDone caller;
    private TransactionDBOpenHelper transactionDBOpenHelper;
    SQLiteDatabase database;

    public AccountListInteractor(OnAccountGetDone caller) {
        this.caller = caller;
        account = new Account();
        accountForEdit = new Account();
    }

    public AccountListInteractor(OnAccountGetDone caller, Account account) {
        this.caller = caller;
        this.account = new Account();
        accountForEdit = account;
    }

    public AccountListInteractor() {

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
    protected Account doInBackground(String... strings) {
        String query = null;
        try {
            query = URLEncoder.encode(strings[0], "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(strings[0]=="Get") {
            String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/baa446d6-dbc1-4975-968f-2efde5ba4eaa";
            try {
                URL url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String result = convertStreamToString(in);
                JSONObject jo = new JSONObject(result);
                Integer id = jo.getInt("id");
                Double budget = jo.getDouble("budget");
                Double totalLimit = jo.getDouble("totalLimit");
                Double monthLimit = jo.getDouble("monthLimit");
//            account = new Account(id,budget,totalLimit,monthLimit);
                account.setId(id);
                account.setBudget(budget);
                account.setTotalLimit(totalLimit);
                account.setMonthLimit(monthLimit);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (strings[0]=="Edit"){
            String url1 = "http://rma20-app-rmaws.apps.us-west-1.starter.openshift-online.com/account/baa446d6-dbc1-4975-968f-2efde5ba4eaa";
            try {
                URL url = new URL(url1);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                JSONObject object = new JSONObject();
                object.put("budget", accountForEdit.getBudget());
                object.put("totalLimit",accountForEdit.getTotalLimit());
                object.put("monthLimit",accountForEdit.getMonthLimit());
                String jsonInputString = object.toString();
                account.setBudget(accountForEdit.getBudget());
                account.setTotalLimit(accountForEdit.getTotalLimit());
                account.setMonthLimit(accountForEdit.getMonthLimit());

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

        }
        return account;
    }

//    @Override
//    protected void onPostExecute(Void aVoid){
//        super.onPostExecute(aVoid);
//        caller.onDone(account);
//
//    }

    @Override
    public void editAccountDB(Account account, Context context) {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        Uri transactionsURI = Uri.parse("content://rma.provider.account/elements");
        ContentValues values = new ContentValues();
        values.put(TransactionDBOpenHelper.ACCOUNT_ID,account.getId());
        values.put(TransactionDBOpenHelper.ACCOUNT_BUDGET,account.getBudget());
        values.put(TransactionDBOpenHelper.ACCOUNT_TOTAL_LIMIT,account.getTotalLimit());
        values.put(TransactionDBOpenHelper.ACCOUNT_MONTH_LIMIT,account.getMonthLimit());
        String where = TransactionDBOpenHelper.ACCOUNT_ID + " = "+ account.getId();
        cr.update(transactionsURI,values,where,null);
    }

    @Override
    public Cursor getAccountCursor(Context context) {
        ContentResolver cr = context.getApplicationContext().getContentResolver();
        String[] kolone = new String[]{
                TransactionDBOpenHelper.ACCOUNT_ID,
                TransactionDBOpenHelper.ACCOUNT_INTERNAL_ID,
                TransactionDBOpenHelper.ACCOUNT_BUDGET,
                TransactionDBOpenHelper.ACCOUNT_TOTAL_LIMIT,
                TransactionDBOpenHelper.ACCOUNT_MONTH_LIMIT
        };
        Uri adresa = Uri.parse("content://rma.provider.account/elements");
        String where = null;
        String whereArgs[] = null;
        String order = null;
        Cursor cur = cr.query(adresa,kolone,where,whereArgs,order);
        return cur;
    }

    public Account getAccount() {
        return account;
    }

    public interface OnAccountGetDone {
        public void onDone(Account result);
    }
//    @Override
//    public ArrayList<Account> get() {
//        return AccountModel.accounts;
//    }
//    @Override
//    public void set(ArrayList<Account> accounts) {
//        AccountModel.accounts = accounts;
//    }


}

package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class Transaction implements Serializable, Parcelable {
    private LocalDate date;
    private double amount;
    private String title;
    private Type type;
    private String itemDescription;
    private int transactionInterval;
    private LocalDate endDate;
    private Integer id;
    private Integer internalId;

    private ITypePresenter typePresenter;


    public Transaction(LocalDate date, double amount, String title, Type type, String itemDescription, int transactionInterval, LocalDate endDate, Integer id, Integer internalId) {
        this.date = date;
        this.amount = amount;
        this.title = title;
        this.type = type;
        this.itemDescription = itemDescription;
        this.transactionInterval = transactionInterval;
        this.endDate = endDate;
        this.id = id;
        this.internalId = internalId;
    }

    public Transaction(LocalDate date1, Double amount, String title, Integer transactionTypeId, String itemDescription, Integer transactionInterval, LocalDate date2, Integer id, Integer internalId) {


        this.date = date1;
        this.amount = amount;
        this.title = title;
        this.type = findTypeById(transactionTypeId);
        this.itemDescription = itemDescription;
        this.transactionInterval = transactionInterval;
        this.endDate = date2;
        this.id = id;
        this.internalId = internalId;
    }

    public Transaction(LocalDate date1, Double amount, String title, Integer transactionTypeId, String itemDescription, Integer transactionInterval, LocalDate date2, Integer id) {

        this.date = date1;
        this.amount = amount;
        this.title = title;
        this.type = findTypeById(transactionTypeId);
        this.itemDescription = itemDescription;
        this.transactionInterval = transactionInterval;
        this.endDate = date2;
        this.id = id;
    }


    public Type findTypeById(Integer transactionTypeId) {
        ArrayList<Type> tempList = TransactionType.transactionType;

        Type tmp = TransactionType.transactionType.get(0);
        for(Type t: TransactionType.transactionType) {
            if(t.getId()==transactionTypeId) {
                tmp = t;
            }
        }
        return tmp;
    }
//transactionType.getTransactionType()
//    public ITypePresenter getTypePresenter() {
//        if (typePresenter == null) {
//            typePresenter = new TypePresenter();
//        }
//        return typePresenter;
//    }

    public Transaction() {
    }

    public Transaction(LocalDate date, double amount, String title, Type type, String itemDescription, int transactionInterval, LocalDate endDate, Integer id) {
        this.date = date;
        this.amount = amount;
        this.title = title;
        this.type = type;
        this.itemDescription = itemDescription;
        this.transactionInterval = transactionInterval;
        this.endDate = endDate;
        this.id = id;

    }

    public Transaction(LocalDate date, double amount, String title, Type type, String itemDescription, int transactionInterval, LocalDate endDate) {
        this.date = date;
        this.amount = amount;
        this.title = title;
        this.type = type;
        this.itemDescription = itemDescription;
        this.transactionInterval = transactionInterval;
        this.endDate = endDate;



    }

    public Transaction(LocalDate date, double amount, String title, Type type, String itemDescription, Integer id) {
        this.date = date;
        this.amount = amount;
        this.title = title;
        this.type = type;
        this.itemDescription = itemDescription;
        this.id = id;


    }

    public Transaction(LocalDate date, double amount, String title, Type type, String itemDescription) {
        this.date = date;
        this.amount = amount;
        this.title = title;
        this.type = type;
        this.itemDescription = itemDescription;



    }
    protected Transaction(Parcel in) {
        date = (LocalDate) in.readSerializable();
        amount = in.readDouble();
        title = in.readString();
        type = (Type) in.readSerializable();//TODO provjeri da li radi
        itemDescription = in.readString();
        transactionInterval = in.readInt();
        endDate = (LocalDate) in.readSerializable();

        if (in.readByte() == 0) {
            internalId = null;
        } else {
            internalId = in.readInt();
        }



    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    public Integer getInternalId() {
        return internalId;
    }

    public void setInternalId(Integer internalId) {
        this.internalId = internalId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public int getTransactionInterval() {
        return transactionInterval;
    }

    public void setTransactionInterval(int transactionInterval) {
        this.transactionInterval = transactionInterval;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(date);
        dest.writeDouble(amount);
        dest.writeString(title);
        dest.writeSerializable(type);
        dest.writeString(itemDescription);
        dest.writeInt(transactionInterval);
        dest.writeSerializable(endDate);
        //todo id
        if (internalId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(internalId);
        }
    }
}

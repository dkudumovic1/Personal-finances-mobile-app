package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;

import java.util.ArrayList;

public class TransactionType {
    public static ArrayList<Type> transactionType = new ArrayList<Type>(){{
        add(new Type(1,"Regular payment"));
        add(new Type(2,"Regular income"));
        add(new Type(3,"Purchase"));
        add(new Type(4,"Individual income"));
        add(new Type(5,"Individual payment"));
    }
    };

//    public TransactionType() {
//       getPresenter().getTypes();
//    }

//    public ITypePresenter getPresenter() {
//        if (typePresenter == null) {
//            typePresenter = new TypePresenter();
//        }
//        return typePresenter;
//    }

    public void setTypes(ArrayList<Type> results) {
        transactionType =  results;
    }

    public ArrayList<Type> getTransactionType() {
        return transactionType;
    }

    public boolean typeDone () { return transactionType.size() != 0;}
}

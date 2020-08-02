//package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;
//
//import java.util.ArrayList;
//
//public class TypePresenter implements ITypePresenter,TypeInteractor.OnTypeGetDone{
//    public static TransactionType transactionType = new TransactionType();
//
//
//    @Override
//    public void getTypes(){
//        new TypeInteractor((TypeInteractor.OnTypeGetDone) this).execute();
//    }
////OnDone vrati listu u presenter a meni treba u inetarctoru,popravi!
//
//    @Override
//    public void onDone(ArrayList<Type> results) {
//        transactionType.setTypes(results);
//    }
//
//    public TransactionType getTransactionType() {
//        return transactionType;
//    }
//}

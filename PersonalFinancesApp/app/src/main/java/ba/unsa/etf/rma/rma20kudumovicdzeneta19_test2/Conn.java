package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;

import android.content.Context;
import android.net.ConnectivityManager;

public class Conn {
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null) {
            return false;
//            Toast toast = Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT);
//            toast.show();
        }
        else {
            return  true;
//            Toast toast = Toast.makeText(context, "Connected", Toast.LENGTH_SHORT);
//            toast.show();
        }
    }
}

package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import static ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2.MainActivity.SWIPE_THRESHOLD;
import static ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2.MainActivity.SWIPE_VELOCITY_THRESHOLD;

public class BudgetFragment extends Fragment implements GestureDetector.OnGestureListener {
    private TextView BudgetTextViewForNumber;
    private EditText monthLimitEditText;
    private EditText totalLimitEditText;
    public static TextView textViewOfflineAcc;

    private Button buttonSave;

    private double NewMonthLimit;
    private double NewTotalLimit;

    private OnSwipe onSwipe;

    private Account account;

    private IAccountListPresenter accountPresenter;
    private GestureDetector gestureDetector;

    public IAccountListPresenter getAccountPresenter() {
        if (accountPresenter == null) {
            accountPresenter = new AccountListPresenter(getContext());
        }
        return accountPresenter;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_fragment, container, false);


        BudgetTextViewForNumber = view.findViewById(R.id.BudgetTextViewForNumber);
        monthLimitEditText = view.findViewById(R.id.monthLimitEditText);
        totalLimitEditText = view.findViewById(R.id.totalLimitEditText);
        buttonSave = view.findViewById(R.id.buttonSave);
        textViewOfflineAcc = view.findViewById(R.id.textViewOfflineAcc);

        if(Conn.isConnected(getContext())) {
            if(account == null) getAccountPresenter().getAccountFromWeb();
            account = getAccountPresenter().getAccount();
            textViewOfflineAcc.setText("");
        }else {
            account = getAccountPresenter().getAccountDB();
            textViewOfflineAcc.setText("Offline izmjena");
        }
        BudgetTextViewForNumber.setText(String.valueOf(account.getBudget()));
        monthLimitEditText.setText(String.valueOf(account.getMonthLimit()));
        totalLimitEditText.setText(String.valueOf(account.getTotalLimit()));
        NewMonthLimit = account.getMonthLimit();
        NewTotalLimit = account.getTotalLimit();
        onSwipe = (OnSwipe) getActivity();

        final NumberValidator numberValidator = new NumberValidator();
        monthLimitEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!numberValidator.isDouble(s.toString())) {
                    monthLimitEditText.setBackgroundColor(Color.RED);
                    buttonSave.setEnabled(false);
                }
                else {
                    monthLimitEditText.setBackgroundColor(Color.TRANSPARENT);
                    buttonSave.setEnabled(true);
                    NewMonthLimit=Double.parseDouble(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        totalLimitEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!numberValidator.isDouble(s.toString())) {
                    totalLimitEditText.setBackgroundColor(Color.RED);
                    buttonSave.setEnabled(false);
                }
                else {
                    totalLimitEditText.setBackgroundColor(Color.TRANSPARENT);
                    buttonSave.setEnabled(true);
                    NewTotalLimit=Double.parseDouble(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Conn.isConnected(getContext())) {
                    getAccountPresenter().editAccount(new Account(account.getId(),account.getBudget(),NewTotalLimit,NewMonthLimit));
                }else {
                    getAccountPresenter().editAccountDB(new Account(account.getId(),account.getBudget(),NewTotalLimit,NewMonthLimit,account.getInternalId()));
                }
//                account = new Account(account.getBudget(),NewTotalLimit,NewMonthLimit);
//                getAccountPresenter().getInteractor().set(new ArrayList<Account>(){{add(account);}});
            }
        });
        gestureDetector = new GestureDetector(getActivity(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
                boolean result;
                float diffX = moveEvent.getX() - downEvent.getX();
                float diffY = moveEvent.getY() - downEvent.getY();

                if(Math.abs(diffX)>Math.abs(diffY)) {
                    if(Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if(diffX > 0) {
                            onSwipeRight();
                        }else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                }else {
                    //swipe up and down
                    result = true;
                }

                return false;
            }
        });
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
        return view;
    }


    public interface OnSwipe {
        void onSwipeLFromBudget();
        void onSwipeRFromBudget();
    }

    private void onSwipeLeft() {
        onSwipe.onSwipeLFromBudget();
    }

    private void onSwipeRight() {
        onSwipe.onSwipeRFromBudget();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
        System.out.println("DRUGI ONFLING");
        boolean result;
        float diffX = moveEvent.getX() - downEvent.getX();
        float diffY = moveEvent.getY() - downEvent.getY();

        if(Math.abs(diffX)>Math.abs(diffY)) {
            if(Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if(diffX > 0) {
                    onSwipeRight();
                }else {
                    onSwipeLeft();
                }
                result = true;
            }
        }else {
            //swipe up and down
            result = true;
        }

        return false;
    }

}

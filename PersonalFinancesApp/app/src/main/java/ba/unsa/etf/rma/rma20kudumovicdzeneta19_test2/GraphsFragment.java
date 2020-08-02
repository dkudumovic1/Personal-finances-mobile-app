package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;

import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.LocalDate;
import java.util.ArrayList;

import static ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2.MainActivity.SWIPE_THRESHOLD;
import static ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2.MainActivity.SWIPE_VELOCITY_THRESHOLD;

public class GraphsFragment extends Fragment implements GestureDetector.OnGestureListener{
    private BarChart barChart1;
    private BarChart barChart2;
    private BarChart barChart3;

    private Spinner spinnerUnitOfTime;

    private GestureDetector gestureDetector;

    private SwipeGraphsFragment swipeGraphsFragment;

    private ITransactionListPresenter listPresenter;
    public ITransactionListPresenter getListPresenter() {
        if (listPresenter == null) {
            listPresenter = new TransactionListPresenter(getActivity());
        }
        return listPresenter;
    }
    private int choice = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graphs_fragment, container, false);

        barChart1 = view.findViewById(R.id.barChart1);
        barChart2 = view.findViewById(R.id.barChart2);
        barChart3 = view.findViewById(R.id.barChart3);

        spinnerUnitOfTime = view.findViewById(R.id.spinnerUnitOfTime);
        ArrayList<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("Monthly Charts");
        spinnerArray.add("Daily Charts");
        spinnerArray.add("Weekly Charts");

        spinnerUnitOfTime= view.findViewById(R.id.spinnerUnitOfTime);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnitOfTime.setAdapter(spinnerArrayAdapter);


        spinnerUnitOfTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int choice = position;
                if(choice == 0) {
                    setBarChart1(getListPresenter().potrosnjaPoMjesecima());
                    setBarchart2(getListPresenter().zaradaPoMjesecima());
                    setBarChart3(getListPresenter().ukupnoStanjePoMjesecima());
                }
                if(choice == 1) {
                    setBarChart1(getListPresenter().potrosnjaPoDanima());
                    setBarchart2(getListPresenter().zaradaPoDanima());
                    setBarChart3(getListPresenter().ukupnoStanjePoDanima());
                }
                if(choice == 2) {
                   setBarChart1(getListPresenter().potrosnjaPoSedmicama());
                    setBarchart2(getListPresenter().zaradaPoSedmicama());
                    setBarChart3(getListPresenter().ukupnoStanjePoSedmicama());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                setBarChart1(getListPresenter().potrosnjaPoMjesecima());
                setBarchart2(getListPresenter().zaradaPoMjesecima());
                setBarChart3(getListPresenter().ukupnoStanjePoMjesecima());
            }

        });



//        ArrayList<String> labels = new ArrayList<String>();
//        labels.add("Mon");
//        labels.add("Tue");
//        labels.add("Wed");
//        labels.add("Thus");
//        labels.add("Fri");
//        labels.add("Sat");
//        labels.add("Sun");
//
//        BarData data = new BarData();
//        data.addDataSet(bardataset);
//        //barChart.setData(data); // set the data and list of lables into chart
//
//        // set the description
//
//        //barChart.animateY(5000);


        swipeGraphsFragment = (SwipeGraphsFragment) getActivity();
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


    public interface SwipeGraphsFragment {
        void onSwipeLFromGraphs();
        void onSwipeRFromGraphs();
    }

    private void onSwipeLeft() {
        swipeGraphsFragment.onSwipeLFromGraphs();
    }

    private void onSwipeRight() {
        swipeGraphsFragment.onSwipeRFromGraphs();
    }

    private void setBarChart1 (ArrayList<BarEntry> entries) {
        ArrayList<BarEntry> barEntries = entries;
        BarDataSet barDataSet = new BarDataSet(barEntries, "Potrošnja");
        BarData barData = new BarData(barDataSet);
        barChart1.setData(barData);
       // barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(12f);
        barChart1.getDescription().setText("");
        barChart1.invalidate();
    }
    private void setBarchart2 (ArrayList<BarEntry> entries) {
        ArrayList<BarEntry> barEntries2 = entries;
        BarDataSet barDataSet2 = new BarDataSet(barEntries2, "Zarada");
        BarData barData2 = new BarData(barDataSet2);
        barChart2.setData(barData2);
        //barDataSet2.setColors(Color.blue(1));
        barDataSet2.setValueTextColor(Color.BLACK);
        barDataSet2.setValueTextSize(12f);
        barChart2.getDescription().setText("");
        barChart2.invalidate();
    }
    private void setBarChart3 (ArrayList<BarEntry> entries) {
        ArrayList<BarEntry> barEntries3 = entries;
        BarDataSet barDataSet3 = new BarDataSet(barEntries3, "Ukupno stanje");
        BarData barData3 = new BarData(barDataSet3);
        barChart3.setData(barData3);
       // barDataSet3.setColors(Color.blue(2));
        barDataSet3.setValueTextColor(Color.BLACK);
        barDataSet3.setValueTextSize(12f);
        barChart3.getDescription().setText("");
        barChart3.invalidate();
    }


}

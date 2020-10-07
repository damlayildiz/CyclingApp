package my.app.cyclingapp.ui.dashboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import my.app.cyclingapp.R;
import nl.dionsegijn.konfetti.models.Size;
import nl.dionsegijn.konfetti.KonfettiView;
import android.content.DialogInterface;
import java.util.TreeMap;
import java.util.SortedMap;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    private CalendarView calender;
    private RadioButton day, week, month, year;
    private DashboardViewModel dashboardViewModel;
    private TextView stats, goals;
    private int selected_year, selected_month, selected_day = 0;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context cx;
    private KonfettiView konfettiView;
    private SortedMap<String, Integer> km_book = new TreeMap<String, Integer>();
    private String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onViewCreated(View root, Bundle savedInstanceState){
        calender = getView().findViewById(R.id.calendarView2);
        day =  getView().findViewById(R.id.radioButton4);
        week =  getView().findViewById(R.id.radioButton);
        month =  getView().findViewById(R.id.radioButton2);
        year =  getView().findViewById(R.id.radioButton3);
        stats =  getView().findViewById(R.id.textView6);
        goals = getView().findViewById(R.id.textView7);
        konfettiView =  getView().findViewById(R.id.viewKonfetti);
        day.setOnClickListener(this);
        week.setOnClickListener(this);
        month.setOnClickListener(this);
        year.setOnClickListener(this);
        calender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                Log.i("tag", year+" "+month+" "+day);
                selected_year = year;
                selected_month = month+1;
                selected_day = day;
            }
        });
        cx = getActivity();
        preferences = PreferenceManager.getDefaultSharedPreferences(cx);
        editor = preferences.edit();
        writeTheGoals();
        readTheKmBook();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void writeTheGoals() {
        int w = 100;
        int m = 100;
        int y = 100;
        int weekly_goal = preferences.getInt("weekly", 0);
        int monthly_goal = preferences.getInt("monthly", 0);
        int yearly_goal = preferences.getInt("yearly", 0);
        String s = "current goals status: (Completed / Goal)\n\n" ;
        int weekly_completed = preferences.getInt("week", 0);
        int monthly_completed = preferences.getInt("month", 0);
        int yearly_completed = preferences.getInt("year", 0);
        if (weekly_goal!=0){
            w = weekly_completed/weekly_goal*100;
        }
        if (monthly_goal!=0){
            m = monthly_completed/monthly_goal*100;
        }
        if (yearly_goal!=0){
            y = yearly_completed/yearly_goal*100;
        }
        s+="*weekly status: " + weekly_completed + "/" + weekly_goal + "   % " + w + " completed " +"\n";
        s+="*monthly status: " + monthly_completed + "/" + monthly_goal + "   % " + m + " completed " +"\n";
        s+="*yearly status: " + yearly_completed + "/" + yearly_goal + "   % " + y + " completed " +"\n";
        goals.setText(s);

        if (weekly_goal>0 && weekly_completed>=weekly_goal && !preferences.getBoolean("congratsWeek", false)){
            editor.putBoolean("congratsWeek", true);
            editor.apply();
            new AlertDialog.Builder(cx)
                    .setTitle("Congratulations")
                    .setMessage("You completed your weekly goal!!")
                    .setPositiveButton("Thanks", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            konfettiView.build()
                                    .addColors(Color.BLUE, Color.CYAN, Color.WHITE)
                                    .setDirection(0.0, 200.0)
                                    .setSpeed(1f, 6f)
                                    .setFadeOutEnabled(true)
                                    .setTimeToLive(2000L)
                                    .addSizes(new Size(12, 5f))
                                    .setPosition(50f, konfettiView.getWidth()
                                            + 200f, -50f, 600f)
                                    .streamFor(300, 5000L);
                        }
                    })
                    .show();
        }
        if (monthly_goal>0 && monthly_completed>=monthly_goal && !preferences.getBoolean("congratsMonth", false)){
            editor.putBoolean("congratsMonth", true);
            editor.apply();
            new AlertDialog.Builder(cx)
                    .setTitle("Congratulations")
                    .setMessage("You completed your monthly goal!!")
                    .setPositiveButton("Thanks", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            konfettiView.build()
                                    .addColors(Color.BLUE, Color.CYAN, Color.WHITE)
                                    .setDirection(0.0, 200.0)
                                    .setSpeed(1f, 6f)
                                    .setFadeOutEnabled(true)
                                    .setTimeToLive(2000L)
                                    .addSizes(new Size(12, 5f))
                                    .setPosition(50f, konfettiView.getWidth()
                                            + 200f, -50f, 600f)
                                    .streamFor(300, 5000L);
                        }
                    })
                    .show();
        }
        if (yearly_goal>0 && yearly_completed>=yearly_goal && !preferences.getBoolean("congratsYear", false)) {
            editor.putBoolean("congratsYear", true);
            editor.apply();
            new AlertDialog.Builder(cx)
                    .setTitle("Congratulations")
                    .setMessage("You completed your yearly goal!!")
                    .setPositiveButton("Thanks", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            konfettiView.build()
                                    .addColors(Color.BLUE, Color.CYAN, Color.WHITE)
                                    .setDirection(0.0, 200.0)
                                    .setSpeed(1f, 6f)
                                    .setFadeOutEnabled(true)
                                    .setTimeToLive(2000L)
                                    .addSizes(new Size(12, 5f))
                                    .setPosition(50f, konfettiView.getWidth()
                                            + 200f, -50f, 600f)
                                    .streamFor(300, 5000L);
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onClick(View view) {
        String s = "total km: \n";

        switch(view.getId()){
            case R.id.radioButton4:
                String day = selected_year + " "+ selected_month+" "+selected_day;
                if (km_book.keySet().contains(day)){
                    s+="\n";
                    s+=(day + ": " +km_book.get(day).toString())+ " km";
                }
                break;
            case R.id.radioButton:
                s+="\n:)";
                break;
            case R.id.radioButton2:
                int sum = 0;
                for (Map.Entry<String, Integer> e:km_book.entrySet()){
                    String[] dayArray = e.getKey().split(" ");
                    if (Integer.parseInt(dayArray[1]) == selected_month
                            && Integer.parseInt(dayArray[0]) == selected_year){
                        sum+=e.getValue();
                    }
                }
                s+="\n";
                if (selected_month!=0){
                    s+=(months[selected_month-1] + ": " +sum+ " km");
                }
                break;
            case R.id.radioButton3:
                sum = 0;
                for (Map.Entry<String, Integer> e:km_book.entrySet()){
                    String[] dayArray = e.getKey().split(" ");
                    if (Integer.parseInt(dayArray[0]) == selected_year){
                        sum+=e.getValue();
                    }
                }
                s+="\n";
                if (selected_year!=0){
                    s+=(selected_year + ": " +sum+ " km");
                }
                break;
            default:
                break;
        }
        stats.setText(s);
        week.setChecked(false);
        day.setChecked(false);
        month.setChecked(false);
        year.setChecked(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void readTheKmBook() {
        km_book = new TreeMap<String, Integer>();
        try {
            FileInputStream fIn = cx.openFileInput("KmBookFile");
            InputStreamReader isr = new InputStreamReader(fIn);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(fIn))) {
                String line = "";
                while((line = br.readLine()) != null) {
                    Log.i("line", line);
                    String[] words = line.split(" ");
                    String date  = words[0]+" "+words[1]+" "+words[2];
                    int km = Integer.parseInt(words[3]);
                    km_book.put(date, km);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
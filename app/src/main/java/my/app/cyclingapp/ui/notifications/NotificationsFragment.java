package my.app.cyclingapp.ui.notifications;

import my.app.cyclingapp.AlertReciever;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import my.app.cyclingapp.R;
import android.widget.Toast;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NotificationsFragment extends Fragment implements View.OnClickListener {

    private NotificationsViewModel notificationsViewModel;
    private Switch Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday;
    private TextView plan, goals;
    private Button save_plan, create_goal;
    private CheckBox weekly, monthly, yearly;
    private EditText goal_km, alarm_hour;
    private Set<String> days =  new HashSet<String>();
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context cx;
    private String[] daysString = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        return root;
    }

    public void onViewCreated(View root, Bundle savedInstanceState){
        Monday = getView().findViewById(R.id.switch1);
        Tuesday = getView().findViewById(R.id.switch2);
        Wednesday = getView().findViewById(R.id.switch3);
        Thursday = getView().findViewById(R.id.switch4);
        Friday = getView().findViewById(R.id.switch5);
        Saturday =  getView().findViewById(R.id.switch6);
        Sunday = getView().findViewById(R.id.switch7);
        plan = getView().findViewById(R.id.textView3);
        goals = getView().findViewById(R.id.textView5);
        weekly = getView().findViewById(R.id.checkBox10);
        monthly = getView().findViewById(R.id.checkBox11);
        yearly = getView().findViewById(R.id.checkBox13);
        goal_km = getView().findViewById(R.id.editTextNumber2);
        save_plan = getView().findViewById(R.id.button3);
        create_goal = getView().findViewById(R.id.button4);
        alarm_hour = getView().findViewById(R.id.editTextTime);
        cx = getActivity();

        save_plan.setOnClickListener(this);
        create_goal.setOnClickListener(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(cx);
        editor = preferences.edit();
        Log.i("pref", preferences.getAll().toString());

        writeThePlan();
        writeTheGoals();
    }

    private void writeThePlan(){
        String hour = preferences.getString("hour", "");
        days = preferences.getStringSet("days", Collections.singleton(""));
        String planString = "Plan Of The Week: \n\n";
        for (String s: days){
            planString+= ("<-> "+s.toUpperCase());
            planString+="\n";
        }
        planString+="\nAlarm Hour: " + hour;
        plan.setText(planString);
    }

    private void writeTheGoals() {
        String goalString = "";
        goalString+= "Current Goals:\n\n";
        int weekly = preferences.getInt("weekly", 0);
        int monthly = preferences.getInt("monthly", 0);
        int yearly = preferences.getInt("yearly", 0);
        goalString+=("Weekly: " + weekly+ " km\n\n");
        goalString+=("Monthly: " + monthly+ " km\n\n");
        goalString+=("Yearly: " + yearly+ " km\n\n");
        goals.setText(goalString);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button4:
                Log.i("tag", "create a goal");
                createAGoal();
                break;
            case R.id.button3:
                Log.i("tag", "save the plan");
                saveThePlan();
                deleteOldAlarms();
                setAlarm();
                break;
            default:
                break;
        }
    }

    private void deleteOldAlarms() {
        for(String d: days){
            if (preferences.getInt(d, 0) >0){
                AlarmManager alarmManager = (AlarmManager) cx.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(cx, AlertReciever.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(cx, preferences.getInt(d, 0), intent, 0);
                alarmManager.cancel(pendingIntent);
                Log.i("alarm", "alarm cancelled");
                editor.putInt(d, 0);
                editor.apply();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setAlarm() {
        String hour = preferences.getString("hour", "");
        String[] hourList = hour.split(":");
        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int i = 0;
        for (String d: days){
            editor.putInt(d, i);
            editor.apply();
            Calendar c = Calendar.getInstance();
            int diff = Arrays.asList(daysString).indexOf(d)+2-today;
            Log.i("diff", diff+" ");
            if (diff<=0){
                diff = 7+diff;
            }
            c.add(Calendar.DATE, diff);
            c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourList[0]));
            c.set(Calendar.MINUTE, Integer.parseInt(hourList[1]));
            AlarmManager alarmManager = (AlarmManager) cx.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(cx, AlertReciever.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(cx, i, intent, 0);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
            Log.i("alarm", c.toString());
            Log.i("alarm", diff+" ");
            i+=1;
        }
    }

    private void createAGoal(){
        if (weekly.isChecked()){
            editor.putBoolean("congratsWeek", false);
            int km = Integer.parseInt(goal_km.getText().toString());
            editor.putInt("weekly", km);
            weekly.setChecked(false);
            goal_km.setText("");
            editor.apply();
        }
        if(monthly.isChecked()){
            editor.putBoolean("congratsMonth", false);
            int km = Integer.parseInt(goal_km.getText().toString());
            editor.putInt("monthly", km);
            monthly.setChecked(false);
            goal_km.setText("");
            editor.apply();
        }
        if(yearly.isChecked()){
            editor.putBoolean("congratsYear", false);
            int km = Integer.parseInt(goal_km.getText().toString());
            editor.putInt("yearly", km);
            yearly.setChecked(false);
            goal_km.setText("");
            editor.apply();
        }
        writeTheGoals();
        Toast.makeText(cx,"Created", Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void saveThePlan() {
        days = new HashSet<>();
        if (Monday.isChecked()){days.add("monday"); Monday.setChecked(false);}
        if (Tuesday.isChecked()){days.add("tuesday"); Tuesday.setChecked(false);}
        if (Wednesday.isChecked()){days.add("wednesday"); Wednesday.setChecked(false);}
        if (Thursday.isChecked()){days.add("thursday"); Thursday.setChecked(false);}
        if (Friday.isChecked()){days.add("friday"); Friday.setChecked(false);}
        if (Saturday.isChecked()){days.add("saturday"); Saturday.setChecked(false);}
        if (Sunday.isChecked()){days.add("sunday"); Sunday.setChecked(false);}

        editor.putStringSet("days", days);
        editor.putString("hour", alarm_hour.getText().toString());
        editor.apply();
        alarm_hour.setText("");
        writeThePlan();
        Toast.makeText(cx,"Saved", Toast.LENGTH_LONG).show();
    }
}
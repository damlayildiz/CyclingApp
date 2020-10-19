package my.app.cyclingapp.ui.home;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import my.app.cyclingapp.MainActivity;
import my.app.cyclingapp.R;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.*;
import java.util.ArrayList;
import java.util.TreeMap;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private HomeViewModel homeViewModel;
    public TextView weather;
    private Button enter_km, check_list;
    private EditText km;
    private ImageView image;
    private CheckBox helmet, gloves, sunglasses, bottles, mask, food, phone, id, napkin, money;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context cx;
    private boolean checked = false;
    private SortedMap<String, Integer> km_book = new TreeMap<>();
    private int weatherId,humidity, wind;
    private double feelTemp;
    private String notificationText = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onViewCreated(View root, Bundle savedInstanceState){
        weather = getView().findViewById(R.id.textView);
        image = getView().findViewById(R.id.imageView);
        helmet = getView().findViewById(R.id.checkBox);
        gloves = getView().findViewById(R.id.checkBox2);
        sunglasses = getView().findViewById(R.id.checkBox3);
        bottles = getView().findViewById(R.id.checkBox4);
        mask =  getView().findViewById(R.id.checkBox5);
        food = getView().findViewById(R.id.checkBox6);
        phone = getView().findViewById(R.id.checkBox7);
        id = getView().findViewById(R.id.checkBox12);
        napkin = getView().findViewById(R.id.checkBox9);
        money = getView().findViewById(R.id.checkBox8);
        check_list = getView().findViewById(R.id.button);
        km = getView().findViewById(R.id.editTextNumber);
        enter_km = getView().findViewById(R.id.button2);
        check_list.setOnClickListener(this);
        enter_km.setOnClickListener(this);
        cx = getActivity();
        preferences = PreferenceManager.getDefaultSharedPreferences(cx);
        editor = preferences.edit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            readTheKmBook();
        }

        if(!(preferences.getString("yesterday", "").equalsIgnoreCase(LocalDate.now().getDayOfWeek().toString()))){
            editor.putBoolean("dateChecked", false);
            editor.putString("yesterday", LocalDate.now().getDayOfWeek().toString());
            editor.apply();
        }

        if (!preferences.getBoolean("dateChecked", false)){
            check_date();
        }

        Log.i("preferences", preferences.getAll().toString());
        Log.i("km_book_to_string", km_book.toString());
        Log.i("weather", preferences.getString("weather", ""));

        getWeather();
        setWeatherIcon();

        if(feelTemp>=10 && humidity<90 && weatherId!=781 && weatherId/100 != 2 && weatherId/100 != 5
                && weatherId/100 != 6){
            wasItAlarm();
        }

        LocalDate today_date = LocalDate.now();
    }

    private void setWeatherIcon() {
        if (weatherId/100 == 2){image.setImageResource(R.mipmap.thunderstorm);}
        else if (weatherId/100 == 3){image.setImageResource(R.mipmap.drizzle);}
        else if (weatherId/100 == 5){image.setImageResource(R.mipmap.rain);}
        else if (weatherId/100 == 6){image.setImageResource(R.mipmap.snow);}
        else if (weatherId/100 == 7){image.setImageResource(R.mipmap.atmosphere);}
        else if (weatherId == 800){image.setImageResource(R.mipmap.sunny);}
        else {image.setImageResource(R.mipmap.cloudy);}
    }

    private void getWeather() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            HttpResponse<String> response = Unirest.get("http://api.openweathermap.org/data/2.5/weather?q=Istanbul,tr&APPID=a5ef4aea6d2d983233fbc9e267a53d78")
                    .header("x-rapidapi-host", "community-open-weather-map.p.rapidapi.com")
                    .header("x-rapidapi-key", "f160641a15msh8b722ea32d01617p138788jsn80731e9a5f7c")
                    .asString();
            Log.i("response", response.getBody());
            JSONObject obj = new JSONObject(response.getBody());
            JSONArray weatherArray = obj.getJSONArray("weather");
            JSONObject main = obj.getJSONObject("main");
            JSONObject weatherJ = weatherArray.getJSONObject(0);
            weatherId = weatherJ.getInt("id");
            feelTemp = main.getDouble("feels_like") - 273;
            humidity = main.getInt("humidity");
            wind = obj.getJSONObject("wind").getInt("speed");
            String condition = weatherJ.getString("main");
            String weatherText ="TODAY'S WEATHER: \n\n Condition: " + condition +"\n Temperature: " +
                    (int)feelTemp+ "°C \n Humidity: %" + humidity + "\n Wind: " + wind + " km/h";
            weather.setText(weatherText);
        } catch (UnirestException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void wasItAlarm() {
        if (preferences.getBoolean("alarm", false)){
            final MediaPlayer mediaPlayer = MediaPlayer.create(cx, R.raw.song);
            mediaPlayer.start();
            new AlertDialog.Builder(cx)
                    .setTitle("Wake Up Alarm")
                    .setMessage("Are you ready to ride?")
                    .setPositiveButton("Yess", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i("alarm", "stop");
                            mediaPlayer.stop();
                        }
                    })
                    .show();
            editor.putBoolean("alarm", false);
            editor.apply();
            notificationText = "Don't forget to take the things you need.";
            sendNotification();
        }
    }

    private void sendNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel1";
            String description = "notification channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager =  (NotificationManager) cx.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        Intent newIntent = new Intent(cx, MainActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(cx, 0, newIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(cx, "1")
                .setSmallIcon(R.drawable.ic_baseline_directions_bike_24)
                .setContentTitle("Don't Forget To Take Your:")
                .setContentText("Reminder!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(cx);
        notificationManager.notify(1, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void check_date() {
        Calendar cal = Calendar.getInstance();
        int thisWeek = cal.get(Calendar.WEEK_OF_YEAR);
        int year = cal.YEAR;
        int month = cal.MONTH;

        if (!(thisWeek == preferences.getInt("lastWeek", 0))){
            editor.putInt("lastWeek", thisWeek);
            editor.putBoolean("congratsWeek", false);
            editor.putInt("week", 0);
        }
        if (!(month == preferences.getInt("lastMonth", 0))){
            editor.putInt("lastMonth", month);
            editor.putBoolean("congratsMonth", false);
            editor.putInt("month", 0);
        }
        if (year!=preferences.getInt("current year", 0)){
            editor.putInt("current year", year);
            editor.putBoolean("congratsYear", false);
            editor.putInt("year", 0);
        }

        editor.putBoolean("dateChecked", true);
        editor.apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void readTheKmBook() {
        km_book = new TreeMap<>();
        try {
            FileInputStream fIn = cx.openFileInput("KmBookFile");
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button2:
                readTheKmBook();

                LocalDate today_date = LocalDate.now();
                String date = String.valueOf(today_date);
                String[] dates= date.split("-");
                int year = Integer.parseInt(dates[0]);
                int month = Integer.parseInt(dates[1]);
                int day = Integer.parseInt(dates[2]);

                String today = year+" "+month+" "+day;
                int today_km = 0;

                try{
                    today_km = Integer.parseInt(String.valueOf(km.getText()));
                }catch(Exception e){
                    System.out.println(e.getStackTrace());
                    break;
                }

                editor.putInt("month" ,preferences.getInt("month", 0)+today_km);
                editor.putInt("year", preferences.getInt("year", 0)+today_km);
                editor.putInt("week", preferences.getInt("week", 0)+today_km);
                editor.apply();

                if(km_book.containsKey(today)){
                    km_book.put(today, today_km+km_book.get(today));
                } else{
                    km_book.put(today, today_km);
                }

                writeTheKmBook();
                km.setText("");
                break;
            case R.id.button:
                ArrayList<String> forget = new ArrayList<>();
                Set<String> checked = new HashSet<>();

                if (!helmet.isChecked()){forget.add("helmet");}
                else{checked.add("helmet");}
                if (!gloves.isChecked()){forget.add("gloves");}
                else{checked.add("gloves");}
                if (!sunglasses.isChecked()){forget.add("sunglasses");}
                else{checked.add("sunglasses");}
                if (!bottles.isChecked()){forget.add("bottles");}
                else{checked.add("bottles");}
                if (!mask.isChecked()){forget.add("mask");}
                else{checked.add("mask");}
                if (!food.isChecked()){forget.add("food");}
                else{checked.add("food");}
                if (!id.isChecked()){forget.add("id");}
                else{checked.add("id");}
                if (!napkin.isChecked()){forget.add("napkin");}
                else{checked.add("napkin");}
                if (!money.isChecked()){forget.add("money");}
                else{checked.add("money");}
                if (!phone.isChecked()){forget.add("phone");}
                else{checked.add("phone");}
                editor.putStringSet("checked", checked);
                editor.apply();
                if (!forget.isEmpty()){
                    notificationText = "";
                    for (String s: forget){
                        notificationText += ("*" + s.toLowerCase() + "\n");
                    }
                    sendNotification();
                }
                break;
            default:
                break;
        }
    }

    private void writeTheKmBook() {
        try {
            FileOutputStream fOut = cx.openFileOutput("KmBookFile", Context.MODE_PRIVATE);
            for (Map.Entry<String, Integer> e:km_book.entrySet()){
                String entry = e.getKey() + " "+ e.getValue();
                Log.i("entry", entry);
                fOut.write(entry.getBytes());
                fOut.write("\n".getBytes());
            }
            fOut.close();
            Toast.makeText(cx,"Saved", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            Toast.makeText(cx,"Failed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
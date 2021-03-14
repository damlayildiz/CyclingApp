# Cycling App

This is an android app for cycling enthusiasts like myself. Implemented with Java, XML, and [OpenWeatherMap API](https://openweathermap.org/api). I created this app to solve specifically the problems and difficulties I faced. 

## Features
- **Current weather:**

  Users can look at the current temperature, humidity and wind from the *Home* page. 

- **Goals:**
 
Users can create weekly, monthly and yearly kilometre goals. And when the user reaches her goal, the app celebrates the user with a confetti. This behaviour is implemented with [Konfetti](https://github.com/DanielMartinus/Konfetti) library. 
- **Stats:**
 
After every ride, user can save the distance and look at their previous records from the calendar. They can also see the total distance they have ridden in a particular week, month or a year. 
- **Alarm:**

 >To avoid traffic and sometimes the sun, cyclists usually get up early. And if the weather is not suitable for riding, then you are awake at 6 am for nothing. This was my main motivation for creating this app.  
 
Users can create alarms for the days they want to ride. But the alarm rings only if the wind is under 20 km/h humidity is below 60% and temperature is between 10ºC and 30ºC at that hour. 


- **Don't forget:**
30 minutes after the alarm is closed user receives a notification which includes a list of things she has to take  like water bottles or helmet before leaving home. And this notifications only stop when all of the things on list is checked from the *home* page. 

## Screenshots

<img src="https://user-images.githubusercontent.com/56313500/111072433-a7a93f80-84eb-11eb-8828-df531b5fd3e3.jpg" width="180">&nbsp;<img src="https://user-images.githubusercontent.com/56313500/111072434-aaa43000-84eb-11eb-9860-0a86321ba26f.jpg" width="180">&nbsp;<img src="https://user-images.githubusercontent.com/56313500/111072441-aed04d80-84eb-11eb-83a2-97ce7f364d62.jpg" width="180">&nbsp;<img src="https://user-images.githubusercontent.com/56313500/111072423-9829f680-84eb-11eb-9a68-9995a99b0d4f.jpg" width="180">&nbsp;<img src="https://user-images.githubusercontent.com/56313500/111072429-a546e580-84eb-11eb-9be2-da2c78bfc1c6.jpg" width="180">

## Permissions
- INTERNET, to make API calls to access current weather reports. 
- ACCESS_NOTIFICATION_POLICY,  to ring an alarm with full sound even when the *do not disturb* mode is on. 
- WAKE_LOCK, to wake up the device when it is locked to close the alarm.



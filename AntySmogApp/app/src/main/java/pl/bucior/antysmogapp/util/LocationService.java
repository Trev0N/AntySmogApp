package pl.bucior.antysmogapp.util;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import pl.bucior.antysmogapp.MainActivity;
import pl.bucior.antysmogapp.R;
import pl.bucior.antysmogapp.api.MeasurementResponse;

public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private final static String apiKey = "ObUbGJ1Ara4KVOI2mArOdADnOTjkXssK";
    private final static String url = "https://airapi.airly.eu/";
    private static final String TAG = "LocationService";
    static final int NOTIFICATION_ID = 543;

    private boolean currentlyProcessingLocation = false;
    public static boolean isServiceRunning = false;
    private GoogleApiClient googleApiClient;
    private Location firstLocation;
    private MeasurementResponse measurementResponse;
    private SharedPreferences sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = this.getSharedPreferences("NotificationPreferences", Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("Notification",false)) {
            isServiceRunning=false;
            startServiceAndSendNotification("AntySmogApp","Aplikacja działa w tle", false);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!currentlyProcessingLocation) {
            currentlyProcessingLocation = true;
            startTracking();
        }

        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void startServiceAndSendNotification(String textTitle,String text, boolean isAirBad) {
        if (!isServiceRunning) {
            Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            String NOTIFICATION_CHANNEL_ID = "pl.bucior.antysmogapp";
            String channelName = "LocationService";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);
            Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setOngoing(false)
                    .setContentTitle(textTitle)
                    .setTicker(getResources().getString(R.string.app_name))
                    .setContentIntent(contentPendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText(text)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setCategory(Notification.CATEGORY_EVENT)
                    .build();
            notification.flags = notification.flags | Notification.DEFAULT_VIBRATE;     // NO_CLEAR makes the notification stay when the user performs a "delete all" command
            startForeground(NOTIFICATION_ID, notification);
            isServiceRunning = true;
        } else if (isAirBad)  {
                Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
                String NOTIFICATION_CHANNEL_ID = "pl.bucior.antysmogapp";
                String channelName = "LocationService";
                NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
                chan.setLightColor(Color.BLUE);
                chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                assert manager != null;
                manager.createNotificationChannel(chan);
                Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setOngoing(false)
                        .setContentTitle(textTitle)
                        .setTicker(getResources().getString(R.string.app_name))
                        .setContentIntent(contentPendingIntent)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentText(text)
                        .setPriority(NotificationManager.IMPORTANCE_HIGH)
                        .setCategory(Notification.CATEGORY_EVENT)
                        .build();
                manager.notify(NOTIFICATION_ID, notification);

        }
    }

    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000;

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }

    private void startTracking() {
        Log.d(TAG, "startTracking");
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
            googleApiClient.connect();
        }
    }

    void processLocation(Location location) {
        double distance = distance(firstLocation.getLatitude(), location.getLatitude(), firstLocation.getLongitude(), location.getLongitude());
        if (distance>50) {
            firstLocation = location;
            getNearestMeasurementByLocation(location.getLatitude(), location.getLongitude());
        }
    }

    public void getNearestMeasurementByLocation(double latitude, double longitude) {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .hostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
                .build();
        AndroidNetworking.initialize(getApplicationContext(), okHttpClient);

        final String airUrl = url + "v2/measurements/nearest?lat=" + latitude + "&lng=" + longitude;
        AndroidNetworking.get(airUrl)
                .addHeaders("Accept", "*/*")
                .addHeaders("apikey", apiKey)
                .addHeaders("accept-language", "pl-PL")
                .addHeaders("Host", "airapi.airly.eu")
                .setPriority(Priority.HIGH)
                .setOkHttpClient(okHttpClient)
                .build()
                .getAsObject(MeasurementResponse.class, new ParsedRequestListener<MeasurementResponse>() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onResponse(MeasurementResponse response) {
                        measurementResponse = response;
                        Log.i(TAG, "onResponse: " + response.getCurrent().toString());
                        if (measurementResponse != null &&
                                measurementResponse.getCurrent().getIndexes().size() > 0 &&
                                measurementResponse.getCurrent().getIndexes().get(0).getValue() > sharedPreferences.getInt("Notification_value",50)) {
                            startServiceAndSendNotification(measurementResponse.getCurrent().getIndexes().get(0).getDescription()
                                    ,measurementResponse.getCurrent().getIndexes().get(0).getAdvice(), true);
                        }
                    }

                    @SuppressLint("NewApi")
                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onResponse: " + anError.toString());
                        startServiceAndSendNotification("AntySmogApp","Brak danych na temat powietrza :(", false);
                    }
                });

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (!sharedPreferences.getBoolean("Notification", false)){
            isServiceRunning=false;
            stopLocationUpdates();
            stopSelf();
        }
        if (location != null) {
            if (firstLocation == null) {
                firstLocation = location;
                return;
            }
            Log.e(TAG, "position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy());
            processLocation(location);
        }
    }


    private void stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(600000);
        locationRequest.setFastestInterval(600000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } catch (SecurityException se) {
            Log.e(TAG, "Go into settings and find AntySmogApp and enable Location.");
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");
        stopLocationUpdates();
        stopSelf();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "GoogleApiClient connection has been suspended.");
    }
}
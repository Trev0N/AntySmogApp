package pl.bucior.antysmogapp.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import pl.bucior.antysmogapp.R;
import pl.bucior.antysmogapp.api.DataDto;
import pl.bucior.antysmogapp.api.MeasurementDto;
import pl.bucior.antysmogapp.api.MeasurementResponse;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class HomeFragment extends Fragment {

    private final static String apiKey = "ObUbGJ1Ara4KVOI2mArOdADnOTjkXssK";
    private final static String url = "https://airapi.airly.eu/";

    private RatingBar homeRatingBar;
    private TextView homeCoordiantes, homeAddress, homePM1, homePM25, homePM10, homeHumidity, homePressure, homeTemperature, homeGrade, homeTip;
    private Location locationToCheck = null;
    private final static int PERMISSION_ID = 44;
    private MeasurementResponse measurementResponse;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = ViewModelProviders.of(requireActivity()).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        homeCoordiantes = root.findViewById(R.id.home_coordinates);
        homeAddress = root.findViewById(R.id.home_address);
        homePM1 = root.findViewById(R.id.home_textToPM1);
        homePM10 = root.findViewById(R.id.home_textToPM10);
        homePM25 = root.findViewById(R.id.home_textToPM25);
        homeHumidity = root.findViewById(R.id.home_textToHumidity);
        homePressure = root.findViewById(R.id.home_textToPressure);
        homeTemperature = root.findViewById(R.id.home_textToTemperature);
        homeGrade = root.findViewById(R.id.home_grade);
        homeTip = root.findViewById(R.id.home_tip);
        homeRatingBar = root.findViewById(R.id.home_ratingBar);
        checkAndRequestPermissions();
        if (locationToCheck == null) {
            getLocationAndSetDataOnUi();
        }

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getNearestMeasurementByLocation(double latitude, double longitude) {
        if (homeViewModel.getMutableLiveData().getValue() != null &&
                homeViewModel.getMutableLiveData().getValue().size() > 0) {
            updateUI(homeViewModel.getMutableLiveData().getValue().get(0));
        } else {
            AsyncTask.execute(() -> {
                OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                        .connectTimeout(120, TimeUnit.SECONDS)
                        .readTimeout(120, TimeUnit.SECONDS)
                        .writeTimeout(120, TimeUnit.SECONDS)
                        .hostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
                        .build();
                AndroidNetworking.initialize(requireContext(), okHttpClient);

                final String airUrl = url + "v2/measurements/nearest?lat=" + latitude + "&lng=" + longitude + "&maxDistanceKM=5";
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
                                updateUI(measurementResponse.getCurrent());
                                homeViewModel.setMutableLiveData(measurementResponse.getHistory());
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.d(TAG, "onResponse: " + anError.toString());
                            }
                        });
            });
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateUI(MeasurementDto measurementDto) {
        homePM1.setText(String.format(new Locale("PL"), "%s µg/m³", measurementDto.getValues().stream().filter(m -> m.getName().equals("PM1")).findAny().orElse(new DataDto("PM1", null)).getValue()));
        homePM25.setText(String.format(new Locale("PL"), "%s µg/m³", measurementDto.getValues().stream().filter(m -> m.getName().equals("PM25")).findAny().orElse(new DataDto("PM25", null)).getValue()));
        homePM10.setText(String.format(new Locale("PL"), "%s µg/m³", measurementDto.getValues().stream().filter(m -> m.getName().equals("PM10")).findAny().orElse(new DataDto("PM10", null)).getValue()));
        homePressure.setText(String.format(new Locale("PL"), "%s hPa", measurementDto.getValues().stream().filter(m -> m.getName().equals("PRESSURE")).findAny().orElse(new DataDto("PRESSURE", null)).getValue()));
        homeHumidity.setText(String.format(new Locale("PL"), "%s %%", measurementDto.getValues().stream().filter(m -> m.getName().equals("HUMIDITY")).findAny().orElse(new DataDto("HUMIDITY", null)).getValue()));
        homeTemperature.setText(String.format(new Locale("PL"), "%s °C", measurementDto.getValues().stream().filter(m -> m.getName().equals("TEMPERATURE")).findAny().orElse(new DataDto("TEMPERATURE", null)).getValue()));
        homeGrade.setText(String.format(new Locale("PL"), "%s", measurementDto.getIndexes().size() > 0 ? measurementDto.getIndexes().get(0).getDescription() : "b/d"));
        homeTip.setText(String.format(new Locale("PL"), "%s", measurementDto.getIndexes().size() > 0 ? measurementDto.getIndexes().get(0).getAdvice() : "b/d"));
        homeRatingBar.setRating(measurementDto.getIndexes().size() > 0 ? (100f - measurementDto.getIndexes().get(0).getValue().floatValue()) / 20f : 0f);
    }

    public void getLocationAndSetDataOnUi() {
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        homeCoordiantes.setText(String.format("%s, %s", location.getLatitude(), location.getLongitude()));
                        getNearestMeasurementByLocation(location.getLatitude(), location.getLongitude());
                        Geocoder gcd = new Geocoder(requireContext(), Locale.getDefault());
                        try {
                            List<Address> addressList = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addressList.size() > 0) {
                                Address address = addressList.get(0);
                                homeAddress.setText(String.format("%s, %s %s, ul. %s %s", address.getCountryName(), address.getLocality(),
                                        address.getPostalCode(), address.getThoroughfare(), address.getSubThoroughfare()));
                            }
                        } catch (IOException e) {
                            Log.d(TAG, "onSuccess: " + e);
                            e.printStackTrace();
                        }
                        locationToCheck = location;
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        getLocationAndSetDataOnUi();
    }

    private void checkAndRequestPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();
        int acl = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        int afl = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int internet = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.INTERNET);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            int cfl = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            if (cfl != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            }
        }
        if (acl != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (internet != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.INTERNET);
        }
        if (afl != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionsNeeded.size() > 0) {
            requestPermissions(
                    permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                    PERMISSION_ID
            );
        }
    }

}
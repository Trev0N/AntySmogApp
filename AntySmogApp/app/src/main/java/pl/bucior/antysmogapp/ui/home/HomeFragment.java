package pl.bucior.antysmogapp.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import pl.bucior.antysmogapp.R;
import pl.bucior.antysmogapp.api.MeasurementResponse;
import pl.bucior.antysmogapp.util.MeasurementTextParser;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class HomeFragment extends Fragment {

    public final static String apiKey = "ObUbGJ1Ara4KVOI2mArOdADnOTjkXssK";
    public final static String url = "https://airapi.airly.eu/";

    private TextView textView, addressTextView, measurementText;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location locationToCheck = null;
    private int PERMISSION_ID = 44;
    private MeasurementResponse measurementResponse;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(requireActivity()).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        textView = root.findViewById(R.id.text_home);
        addressTextView = root.findViewById(R.id.textView4);
        measurementText = root.findViewById(R.id.measurementText);
        checkAndRequestPermissions();
        if (locationToCheck == null) {
            getLocationAndSetDataOnUi();
        }

        return root;
    }

    public void getNearestInstalltionByLocation(double latitude, double longitude) {
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
                        measurementText.setText(MeasurementTextParser.parseToScreen(measurementResponse.getCurrent()));
                        homeViewModel.setMutableLiveData(measurementResponse.getHistory());
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onResponse: " + anError.toString());
                    }
                });

    }


    public void getLocationAndSetDataOnUi() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object

                            textView.setText(location.getLatitude() + ", " + location.getLongitude());
                            getNearestInstalltionByLocation(location.getLatitude(), location.getLongitude());
                            Geocoder gcd = new Geocoder(requireContext(),
                                    Locale.getDefault());
                            try {
                                List<Address> addressList = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                if (addressList.size() > 0) {
                                    Address address = addressList.get(0);
                                    addressTextView.setText(String.format("%s, %s %s, ul. %s %s", address.getCountryName(), address.getLocality(),
                                            address.getPostalCode(), address.getThoroughfare(), address.getSubThoroughfare()));
                                }
                            } catch (IOException e) {
                                Log.d(TAG, "onSuccess: " + e);
                                e.printStackTrace();
                            }
                            locationToCheck = location;
                        }
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

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
        }
    };

    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }


}
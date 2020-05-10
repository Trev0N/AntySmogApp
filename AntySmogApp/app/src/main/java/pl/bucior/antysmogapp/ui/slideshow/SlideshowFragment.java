package pl.bucior.antysmogapp.ui.slideshow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.Map;
import java.util.Objects;

import pl.bucior.antysmogapp.R;
import pl.bucior.antysmogapp.util.LocationService;

public class SlideshowFragment extends Fragment {

    private SharedPreferences sharedpreferences;
    private Switch notification;
    private EditText notificationPercent;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel = ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notification, container, false);
        sharedpreferences = requireContext().getSharedPreferences("NotificationPreferences", Context.MODE_PRIVATE);
        notification = root.findViewById(R.id.switchNotification);
        notificationPercent = root.findViewById(R.id.notificationPercent);
        if(sharedpreferences.contains("Notification")) {
            notification.setChecked(sharedpreferences.getBoolean("Notification", false));
        }
        if (sharedpreferences.contains("Notification_value")) {
            notificationPercent.setText(String.valueOf(sharedpreferences.getInt("Notification_value",0)));
        }
        changeUserPreferences();
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }

    public void changeUserPreferences() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        notification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isChecked()) {
                editor.putBoolean("Notification", true);
                editor.apply();
                Intent serviceIntent = new Intent(requireContext(), LocationService.class);
                requireContext().getApplicationContext().startService(serviceIntent);
            } else {
                editor.putBoolean("Notification", false);
                editor.apply();
            }

        });
        notificationPercent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0 && Integer.parseInt(notificationPercent.getText().toString())<100) {
                    editor.putInt("Notification_value", Integer.parseInt(notificationPercent.getText().toString()));
                    editor.apply();
                }
            }
        });

    }
}

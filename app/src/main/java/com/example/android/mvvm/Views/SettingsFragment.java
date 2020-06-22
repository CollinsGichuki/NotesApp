package com.example.android.mvvm.Views;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.android.mvvm.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //Attach the xml to the activity
        addPreferencesFromResource(R.xml.settings_preferences);

        final ListPreference themesListPreference = findPreference("night_mode_state_key");
        if (themesListPreference.getValue() == null) {
            //To avoid getting a null value,set it to the default value..value 1
            themesListPreference.setValueIndex(1);
        }

        themesListPreference.setSummary(themesListPreference.getValue());
        String value = themesListPreference.getValue();
        Log.d("Them: ", value);
        themesListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                //Update the selected option
                themesListPreference.setSummary(newValue.toString());
                //Get
                String selectedTheme = newValue.toString();
                //Get the
                if (selectedTheme.equals("System Default")) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                } else if (selectedTheme.equals("Dark Mode")) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                return true;
            }
        });
    }
}
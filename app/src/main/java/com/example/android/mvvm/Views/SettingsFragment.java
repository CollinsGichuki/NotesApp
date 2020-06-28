package com.example.android.mvvm.Views;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.example.android.mvvm.R;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //Attach the xml to the activity
        addPreferencesFromResource(R.xml.settings_preferences);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();

        //Used index 0 to get the ListPreference since there is only one preference in our pref screens at the moment
        Preference p = prefScreen.getPreference(0);

        //Get the value of the listPreference from sharedPref
        //Set the default value of the preference to System Default
        String value = sharedPreferences.getString(p.getKey(), String.valueOf(R.string.default_mode_value));
        setPreferenceSummary(p, value);
    }

    private void setPreferenceSummary(Preference p, String value) {
        // Figure out the label of the selected value
        ListPreference listPreference = (ListPreference) p;
        int prefIndex = listPreference.findIndexOfValue(value);
        if (prefIndex >= 0) {
            // Set the summary to that label
            listPreference.setSummary(listPreference.getEntries()[prefIndex]);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //Find the Preference that was changed
        Preference preference = findPreference(key);
        //Update the summary of the preference
        if (preference != null){
            //Get the value
            String value = sharedPreferences.getString(key, String.valueOf(R.string.default_mode_value));

            //Change the theme
            switch (value) {
                case "System Default":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    break;
                case "Dark Mode":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    break;
                case "Day Mode":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
            }

            //Set the summary
            setPreferenceSummary(preference, value);
        }
    }
}
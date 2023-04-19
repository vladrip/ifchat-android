package com.vladrip.ifchat.ui.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.vladrip.ifchat.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}
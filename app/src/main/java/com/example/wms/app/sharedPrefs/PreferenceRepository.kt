package com.example.wms.app.sharedPrefs

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import com.example.wms.app.sharedPrefs.PreferenceHelper.set
import javax.inject.Inject

class PreferenceRepository @Inject constructor(@ApplicationContext context : Context) {

    private val prefs = PreferenceHelper.defaultPrefs(context = context)

    fun getStringValue(tag: String): String? {
        return prefs.getString(tag, "")
    }

    fun setValue (tag: String, value: Any) {
        prefs[tag] = value
    }
}
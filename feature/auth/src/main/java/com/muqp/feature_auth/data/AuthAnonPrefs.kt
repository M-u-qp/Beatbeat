package com.muqp.feature_auth.data

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import java.lang.ref.WeakReference

class AuthAnonPrefs(context: Application) {
    private val contextRef = WeakReference(context)
    private val sharedPrefs: SharedPreferences? =
        contextRef.get()?.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE)

    fun saveAnonUserUid(uid: String) {
        sharedPrefs?.edit()?.putString(USER_UID, uid)?.apply()
    }

    fun getAnonUserUid(): String? {
        return sharedPrefs?.getString(USER_UID, null)
    }

    companion object {
        private const val AUTH_PREFS = "authPrefs"
        private const val USER_UID = "anonUserUid"
    }
}
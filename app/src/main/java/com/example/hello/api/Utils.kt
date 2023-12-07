package com.example.hello.api

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences


class Utils private constructor(context: Context) {
    private val mContext: Context = context

    companion object {
        private const val PREFS = "prefs"
        private const val Access_Token = "Access_Token"
        private lateinit var Grant_Type: String
        private lateinit var prefs: SharedPreferences
        private lateinit var prefsEditor: SharedPreferences.Editor
        @SuppressLint("StaticFieldLeak")
        private var instance: Utils? = null
        @Synchronized
        fun init(context: Context): Utils? {
            if (instance == null) instance = Utils(context)
            return instance
        }

        fun setGrantType(value: String){
            Grant_Type = value
        }

        fun setAccessToken(value: String?) {
            prefsEditor.putString(Access_Token, value).commit()
        }

        fun getAccessToken(defValue: String?): String? {
            return prefs.getString(Access_Token, defValue)
        }

        fun clearToken() {
            prefsEditor.clear().apply()
        }
    }

    init {
        prefs = mContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefsEditor = prefs.edit()
    }
}
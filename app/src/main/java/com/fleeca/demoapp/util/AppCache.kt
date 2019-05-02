package com.wm.whatsappautomationkotlin.util

import android.content.Context
import android.content.SharedPreferences



object AppCache {

    private val PREF_CACHE = "fleeca"

    private val USER_EMAIL = "email"
    private val USER_PASSWORD = "password"



    fun getPref(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_CACHE, Context.MODE_PRIVATE)
    }

    fun getEditor(context: Context): SharedPreferences.Editor {
        return getPref(context).edit()
    }


    fun clearCache(context: Context) {
        getEditor(context).clear().apply()
    }




    fun setEmail(context: Context, email: String) {
        getPref(context).edit().putString(USER_EMAIL, email).apply()
    }


    fun getEmail(context: Context): String {
        return getPref(context).getString(USER_EMAIL, "")
    }

    fun setPassWord(context: Context, password: String) {
        getPref(context).edit().putString(USER_PASSWORD, password).apply()
    }


    fun getPassword(context: Context): String {
        return getPref(context).getString(USER_PASSWORD, "")
    }



}

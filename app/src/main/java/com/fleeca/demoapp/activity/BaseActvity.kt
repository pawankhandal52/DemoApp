package com.fleeca.demoapp.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.fleeca.demoapp.R

open class BaseActvity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    protected fun startActivity(aClass: Class<*>) {
        startActivity(Intent(this, aClass))
    }
}

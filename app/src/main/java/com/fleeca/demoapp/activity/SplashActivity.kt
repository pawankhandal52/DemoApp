package com.fleeca.demoapp.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.fleeca.demoapp.R
import com.wm.whatsappautomationkotlin.util.AppCache

class SplashActivity : BaseActvity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        goNext()
    }
    private fun goNext() {
        Handler().postDelayed({
            if ((AppCache.getEmail(this) == null)&&(AppCache.getPassword(this)==null)) {
                startActivity(SignInActivity::class.java);
                finish();
            } else {
                startActivity(SignInActivity::class.java)
                finish()
            }

        }, 1000)

    }
}

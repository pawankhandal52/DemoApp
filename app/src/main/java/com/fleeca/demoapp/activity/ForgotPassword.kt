package com.fleeca.demoapp.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.fleeca.demoapp.R
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword : BaseActvity(),View.OnClickListener {
    private lateinit var etEmail:EditText
    private lateinit var txtsubmit:TextView
    private var mAuth: FirebaseAuth? = null

    private lateinit var ivBack:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        mAuth = FirebaseAuth.getInstance()
        uiBind()
    }

    private fun uiBind() {

        etEmail=findViewById(R.id.et_email)
        txtsubmit=findViewById(R.id.txt_submit)
        txtsubmit.setOnClickListener(this)
        ivBack=findViewById(R.id.iv_back)
        ivBack.setOnClickListener(this)

    }

    private fun sendPasswordResetEmail() {
        val email = etEmail?.text.toString()
        if (!TextUtils.isEmpty(email)) {
            mAuth!!
                .sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val message = "Email sent."
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()


                    } else {

                        Toast.makeText(this, "No user found with this email.", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onClick(v: View?) {
    when(v?.id)
  {
    R.id.txt_submit ->
    {
        sendPasswordResetEmail()
    }
    R.id.iv_back ->
    {
        onBackPressed()
    }
}
    }
}

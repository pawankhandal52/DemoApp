package com.fleeca.demoapp.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.fleeca.demoapp.R

class SignUpActivity : BaseActvity(),View.OnClickListener {


    private  lateinit var etUserId:EditText
    private lateinit var etPassword:EditText
    private lateinit var etFullName:EditText
    private lateinit var txtLoigin:TextView
    private lateinit var txtSignUp:TextView
    private  var fullName:String ?=null
    private  var emailId:String ?=null
    private  var password:String ?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        uiBind()
    }

    private fun uiBind() {
        etUserId=findViewById(R.id.et_email);
        etPassword=findViewById(R.id.et_password)
        etFullName=findViewById(R.id.et_name)
        txtLoigin=findViewById(R.id.txt_signin)
        txtSignUp=findViewById(R.id.txt_signup)
        txtLoigin.setOnClickListener(this)
        txtSignUp.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.txt_signup ->
            {
               //doValidate()
            }
            R.id.txt_login ->
            {
               // startActivity(SignInActivity::class.java)
              //  finish()
            }
        }

    }

    private fun doValidate() {
        fullName=etFullName.text.toString().trim()
        emailId=etUserId.text.toString().trim()
        password=etPassword.text.toString().trim()
        if(TextUtils.isEmpty(fullName))
        {
            etFullName.error="can't empty"
            return
        }
        if(TextUtils.isEmpty(emailId))
        {
            etUserId.error="can't empty"
            return
        }

        if(TextUtils.isEmpty(password))
        {
            etPassword.error="can't empty"
            return
        }

    }
}

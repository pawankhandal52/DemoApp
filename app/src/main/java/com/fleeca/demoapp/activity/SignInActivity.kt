package com.fleeca.demoapp.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.wm.whatsappautomationkotlin.util.AppCache
import android.content.pm.PackageManager
import android.provider.SyncStateContract.Helpers.update
import android.content.pm.PackageInfo
import android.R
import android.support.annotation.NonNull
import android.util.Base64
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import java.security.MessageDigest
import com.facebook.login.widget.LoginButton
import com.facebook.login.LoginResult
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.*
import java.util.*


class SignInActivity : BaseActvity(),View.OnClickListener {


    private  lateinit var etUserId:EditText
    private lateinit var etPassword:EditText
    private lateinit var txtLoigin:TextView
    private lateinit var txtForgotPassword:TextView
    private lateinit var txtSignUp:TextView
    private lateinit var ivGoogle: SignInButton
    private lateinit var remmberMe: CheckBox
    private var mAuth: FirebaseAuth? = null
    private var REQUEST_CODE_SIGN_IN:Int=101

    private  var emailId:String ?=null
    private  var password:String ?=null
    private var mGoogleApiClient: GoogleApiClient? = null
    var callbackManager: CallbackManager? = null
   private lateinit var loginButton: LoginButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.fleeca.demoapp.R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        uiBind()
        getHashKey()
        FacebookSdk.sdkInitialize(getApplicationContext())
        AppEventsLogger.activateApp(this)
        callbackManager = CallbackManager.Factory.create();


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.fleeca.demoapp.R.string.web_client_id))
            .requestEmail()
            .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
    }

    private fun uiBind() {
        etUserId=findViewById(com.fleeca.demoapp.R.id.et_email);
        etPassword=findViewById(com.fleeca.demoapp.R.id.et_password)
         loginButton=findViewById(com.fleeca.demoapp.R.id.facebook);
        loginButton.setOnClickListener(this)
        txtForgotPassword=findViewById(com.fleeca.demoapp.R.id.txt_forgot_password)
        txtLoigin=findViewById(com.fleeca.demoapp.R.id.txt_login)
        txtSignUp=findViewById(com.fleeca.demoapp.R.id.txt_signup)

        ivGoogle=findViewById(com.fleeca.demoapp.R.id.iv_google)
        remmberMe=findViewById(com.fleeca.demoapp.R.id.cb_remember_me)

        txtLoigin.setOnClickListener(this)
        txtSignUp.setOnClickListener(this)
        txtForgotPassword.setOnClickListener(this)

        ivGoogle.setOnClickListener(this)



    }
    fun getHashKey() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))

            }
        } catch (e: PackageManager.NameNotFoundException) {

        }

    }
    private fun doValidate() {

        emailId=etUserId.text.toString().trim()
        password=etPassword.text.toString().trim()

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
        signIn(emailId!!, password!!)

    }
    override fun onClick(v: View?) {
        when (v?.id)
        {
            com.fleeca.demoapp.R.id.txt_login ->
            {
              doValidate()
            }
            com.fleeca.demoapp.R.id.txt_forgot_password ->
            {
                startActivity(ForgotPassword::class.java)
            }
            com.fleeca.demoapp.R.id.txt_signup ->
            {
                startActivity(SignUpActivity::class.java)
            }
            com.fleeca.demoapp.R.id.facebook ->
            {
                loginButton.setReadPermissions("email")
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
                loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        // App code
                        handleFacebookAccessToken(loginResult.accessToken);
                    }
                    override fun onCancel() {
                        // App code
                    }
                    override fun onError(exception: FacebookException) {
                        // App code
                    }
                })
            }
            com.fleeca.demoapp.R.id.iv_google ->
            {
                val intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
                startActivityForResult(intent, REQUEST_CODE_SIGN_IN)
            }

        }

    }
    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth!!.currentUser
                    startActivity(Intent(this, DashBoardActivity::class.java))
                } else {
                    Toast.makeText(this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
    fun signIn(email: String, password: String){

        mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
            if(task.isSuccessful){
                var intent = Intent(this, DashBoardActivity::class.java)
                intent.putExtra("id", mAuth!!.currentUser?.email)
                startActivity(intent)
                AppCache.setEmail(this,email)
                AppCache.setPassWord(this,password)
                Toast.makeText(applicationContext,"User Successful Login ",Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(applicationContext,"Error"+ ""+ {task.exception?.message},Toast.LENGTH_SHORT).show()

            }
        })

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                val account = result.signInAccount
                firebaseAuthWithGoogle(account!!)
            } else {

            }
        }
    }
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    AppCache.setEmail(this, mAuth!!.currentUser!!.email!!)
                    startActivity(DashBoardActivity::class.java)


                } else {
                    Toast.makeText(applicationContext,"fails"+ mAuth!!.currentUser,Toast.LENGTH_SHORT).show()

                }
            }
    }


}

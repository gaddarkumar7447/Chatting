package com.example.chatting.login

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.chatting.R
import com.example.chatting.databinding.ActivityLogInBinding
import com.example.chatting.loadingdialog.LoadingDialog
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*
import java.util.concurrent.TimeUnit

class LogIn : AppCompatActivity() {
    private lateinit var dataBinding : ActivityLogInBinding
    private lateinit var loadingDialog : LoadingDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storeVerificationId : String
    private lateinit var resendToken : PhoneAuthProvider.ForceResendingToken
    private lateinit var callBacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var dbRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_log_in)

        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#E0E0E0")

        firebaseAuth = FirebaseAuth.getInstance()
        loadingDialog = LoadingDialog(this)



        dataBinding.buttonCreateAccount.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        })

        dataBinding.buttonLogIn.setOnClickListener(View.OnClickListener {
            var number = dataBinding.mobileNumberLogIn.text.trim().toString()
            if (number.isEmpty() || number.length > 10 || number.length < 10){
                Toast.makeText(this, "Number invalid", Toast.LENGTH_SHORT).show()
            }else{
                number = "+91$number"
                checkUser(number)
            }
        })

        callBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                loadingDialog.isDismiss()
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                loadingDialog.isDismiss()
                Toast.makeText(this@LogIn, "Server problem", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                loadingDialog.isDismiss()
                storeVerificationId = p0
                resendToken = p1

                val intenet = Intent(this@LogIn, LogInOtpActivity::class.java)
                intenet.putExtra("storeVerificationId", storeVerificationId)
                startActivity(intenet)
                finish()
            }

        }
    }

    private fun checkUser(number: String) {
        dbRef = FirebaseDatabase.getInstance().reference
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("Users").hasChild(number)){
                    loadingDialog.startDialog()
                    sendVerification(number)
                }else{
                    Toast.makeText(this@LogIn, "Number not resistered", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun sendVerification(number: String) {
        val option = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(callBacks)
            .setActivity(this)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(option)
        Log.d("MSE", "Auth started")
    }
}
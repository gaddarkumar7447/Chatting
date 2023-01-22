package com.example.chatting.login

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.chatting.R
import com.example.chatting.createprofile.CreateProfile
import com.example.chatting.databinding.ActivitySignupOtpactivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class SignupOTPactivity : AppCompatActivity() {
    private lateinit var dataBinding : ActivitySignupOtpactivityBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_signup_otpactivity)

        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#000000")

        firebaseAuth = FirebaseAuth.getInstance()

        val storeVerificationId = intent.getStringExtra("storeVerificationId")
        dataBinding.otpVerificationButton.setOnClickListener(View.OnClickListener {
            val otp = dataBinding.otpEditText.text.trim().toString()
            if (otp.isNotEmpty()){
                val cred : PhoneAuthCredential = PhoneAuthProvider.getCredential(storeVerificationId.toString(), otp)
                signInWithPhoneAuthCred(cred)
            } else{
                Toast.makeText(this, "Enter otp", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun signInWithPhoneAuthCred(cred: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(cred).addOnCompleteListener(this){
            if (it.isSuccessful){
                startActivity(Intent(this, CreateProfile::class.java))
                finish()
            }else{
                Toast.makeText(this, "Invalid otp", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
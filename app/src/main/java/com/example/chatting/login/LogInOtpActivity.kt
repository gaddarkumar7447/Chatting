package com.example.chatting.login

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.chatting.R
import com.example.chatting.databinding.ActivityLogInOtpBinding
import com.example.chatting.friendlist.FriendsList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference

class LogInOtpActivity : AppCompatActivity() {
    private lateinit var dataBinding : ActivityLogInOtpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dbRef : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_log_in_otp)

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#000000")

        firebaseAuth = FirebaseAuth.getInstance()
        val storedVerification = intent.getStringExtra("storeVerificationId")
        dataBinding.otpVerifyButton.setOnClickListener(View.OnClickListener {
            val otp = dataBinding.otpEditText.text.trim().toString()
            if (otp.isNotEmpty()){
                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(storedVerification.toString(), otp)
                signInCred(credential)
            }else{
                Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show()
            }
        })


    }

    private fun signInCred(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this){
            if (it.isSuccessful){
                val pref : SharedPreferences = getSharedPreferences("login", MODE_PRIVATE)
                val editor : SharedPreferences.Editor = pref.edit()
                editor.putBoolean("LogedIn", true).apply()
                startActivity(Intent(this, FriendsList::class.java))
                finish()

            }else{
                Toast.makeText(this, "Invalid otp", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
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
import androidx.lifecycle.withStarted
import com.example.chatting.R
import com.example.chatting.databinding.ActivitySignUpBinding
import com.example.chatting.loadingdialog.LoadingDialog
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.*
import com.google.firebase.database.*
import java.util.concurrent.TimeUnit

class SignUp : AppCompatActivity() {
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dbRef : DatabaseReference
    private lateinit var storeVerificationId : String
    private lateinit var resendToken : PhoneAuthProvider.ForceResendingToken
    private lateinit var callBacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var dataBinding : ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        firebaseAuth = FirebaseAuth.getInstance()
        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = Color.parseColor("#E0E0E0")

        loadingDialog = LoadingDialog(this)

        callBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                loadingDialog.isDismiss()
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                loadingDialog.isDismiss()
                Toast.makeText(this@SignUp, "Server error, please try later", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                loadingDialog.isDismiss()
                storeVerificationId = verificationId
                resendToken = token

                val intent = Intent(applicationContext, SignupOTPactivity::class.java)
                intent.putExtra("storeVerificationId", storeVerificationId)
                startActivity(intent)
                finish()
            }
        }

        dataBinding.signupSignupButton.setOnClickListener(View.OnClickListener {
            resisteredUser()
        })

    }

    private fun resisteredUser() {
        var number = dataBinding.signupEditTextPhone.text.trim().toString()
        if (number.isEmpty() || number.length < 10 || number.length > 10){
            Toast.makeText(this, "Enter the valid number", Toast.LENGTH_SHORT).show()
        }else{
            dbRef = FirebaseDatabase.getInstance().reference
            dbRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    number = "+91$number"
                    if (snapshot.child("Users").hasChild(number)){
                        Toast.makeText(this@SignUp, "Number already resistered", Toast.LENGTH_SHORT).show()
                    }else{
                        loadingDialog.startDialog()
                        sendVerificationCode(number)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    loadingDialog.isDismiss()
                    Toast.makeText(this@SignUp, "Some problem let me check", Toast.LENGTH_SHORT).show()
                }

            })
        }

    }

    private fun sendVerificationCode(number : String) {
        val option = PhoneAuthOptions.newBuilder(firebaseAuth).setPhoneNumber(number).setTimeout(60L, TimeUnit.SECONDS).setActivity(this).setCallbacks(callBacks).build()
        PhoneAuthProvider.verifyPhoneNumber(option)
        Log.d("Auth check", "Auth check")
    }

}
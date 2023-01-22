package com.example.chatting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.chatting.databinding.ActivityMainBinding
import com.example.chatting.login.LogIn

class MainActivity : AppCompatActivity() {
    lateinit var dataBinding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

    }
    fun clickAble(view: View) {startActivity(Intent(this, LogIn::class.java))}
}
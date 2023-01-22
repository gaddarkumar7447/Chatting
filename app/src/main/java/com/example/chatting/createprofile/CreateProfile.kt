package com.example.chatting.createprofile

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.example.chatting.user.User
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.example.chatting.R
import com.example.chatting.databinding.ActivityCreateProfileBinding
import com.example.chatting.friendlist.FriendsList
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CreateProfile : AppCompatActivity() {
    private lateinit var dataBinding : ActivityCreateProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseStore : FirebaseStorage
    private lateinit var dbDef : DatabaseReference
    private lateinit var stoteRefrence : StorageReference
    private lateinit var phoneNumber : String
    private lateinit var userImageUrl : String
    private var storageReqCode : Int = 100
    private var imagePicCode : Int = 200


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_profile)
        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#FFFFFF")

        firebaseAuth = FirebaseAuth.getInstance()
        phoneNumber = firebaseAuth.currentUser?.phoneNumber!!
        firebaseStore = FirebaseStorage.getInstance()

        userImageUrl = "https://instagram.fixb2-1.fna.fbcdn.net/v/t51.2885-19/322327737_179498298006458_4585926227345056937_n.jpg?stp=dst-jpg_s320x320&_nc_ht=instagram.fixb2-1.fna.fbcdn.net&_nc_cat=102&_nc_ohc=iNqs2bTMxpEAX-Nbn1J&edm=AOQ1c0wBAAAA&ccb=7-5&oh=00_AfB3tqevpiNzJqdQwvKMPa5bsDvMdQbmFj3QRT-mweWnSQ&oe=63C5615D&_nc_sid=8fd12b"

        dataBinding.setProfilePicIV.setOnClickListener(View.OnClickListener {
            if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, storageReqCode)
            }else{
                pickImageFormGallery()
            }
        })

        dataBinding.ContinueButton.setOnClickListener(View.OnClickListener {
            val name = dataBinding.UserName.text.trim().toString()
            val about = dataBinding.UserAbout.text.trim().toString()
            if (name.isEmpty() || about.isEmpty()){
                Toast.makeText(this, "Please enter details", Toast.LENGTH_SHORT).show()
                /*dataBinding.UserName.setText("Please enter name")
                dataBinding.UserAbout.setText("Please enter name")*/
            }else{
                val pref : SharedPreferences = getSharedPreferences("login", MODE_PRIVATE)
                val editor : SharedPreferences.Editor = pref.edit()
                editor.putBoolean("LogedIn", true).apply()
                dbDef = FirebaseDatabase.getInstance().reference
                dbDef.child("Users").child(phoneNumber).setValue(User(name,phoneNumber,userImageUrl,about))
                startActivity(Intent(this, FriendsList::class.java))
                finish()
            }
        })
    }

    private fun pickImageFormGallery() {
        val intenet = Intent(Intent.ACTION_PICK)
        intenet.type = "image/*"
        startActivityForResult(intenet, imagePicCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            storageReqCode ->{
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickImageFormGallery()
                }
                else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Activity.RESULT_OK && requestCode == imagePicCode){
            if (data == null || data.data == null){
                return
            }
            else{
                dataBinding.setProfilePicIV.setImageURI(data.data)
                val fileName = phoneNumber
                stoteRefrence = firebaseStore.reference.child("UserProfilePictures/$fileName")
                stoteRefrence.putFile(data.data!!).addOnSuccessListener { task ->
                    task.storage.downloadUrl.addOnSuccessListener {
                        userImageUrl = it.toString()
                    }
                }
                    .addOnFailureListener(OnFailureListener {
                        Toast.makeText(this, "it.toString()", Toast.LENGTH_SHORT).show()
                    })
            }
        }
    }
}
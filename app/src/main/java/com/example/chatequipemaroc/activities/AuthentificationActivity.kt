package com.example.chatequipemaroc.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.chatequipemaroc.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthentificationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth;

    lateinit var tvRegister : TextView
    lateinit var email : TextInputLayout
    lateinit var password : TextInputLayout
    lateinit var connect : MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentification)
        auth = Firebase.auth
        email = findViewById(R.id.layoutTextInputEmail)
        password = findViewById(R.id.layoutTextInputPassword)
        connect = findViewById(R.id.btnConnect)
        tvRegister = findViewById(R.id.tvRegister)
    }

    override fun onStart() {
        super.onStart()
        tvRegister.setOnClickListener{
            Intent(this,RegisterActivity::class.java).also {
                startActivity(it)
            }
        }
        connect.setOnClickListener {
            email.isErrorEnabled=false
            password.isErrorEnabled=false
            val inputEmail = email.editText?.text.toString()
            val inputPassword = password.editText?.text.toString()
            if(inputEmail.isEmpty() || inputPassword.isEmpty()){
                if (inputPassword.isEmpty()){
                    password.error="Password is required !!"
                    password.isErrorEnabled = true
                }
                if (inputEmail.isEmpty()){
                    email.error="Email is required !!"
                    email.isErrorEnabled = true
                }
            }else{
                signIn(inputEmail,inputPassword)
            }
        }
    }

    fun signIn(inputEmail : String , inputPassword : String) {
        Log.d("signIn","signIn user....")
        auth.signInWithEmailAndPassword(inputEmail,inputPassword).addOnCompleteListener {task->
            if (task.isSuccessful){
                Intent(this,HomeActivity::class.java).also {
                    startActivity(it)
                }
                finish()
            }else{
                password.error="Authentification failed !!"
                password.isErrorEnabled = true
                email.isErrorEnabled = true
            }
        }
    }
}
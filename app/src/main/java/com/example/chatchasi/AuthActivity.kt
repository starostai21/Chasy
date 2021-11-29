package com.example.chatchasi

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.widget.Button
import android.widget.TextView
import com.example.chatchasi.common.Myapp
import org.json.JSONObject

class AuthActivity : Activity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)


        val loginText = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.login)
        val passwordText = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.login_button)
        loginButton.setOnClickListener {
            if(loginText.text!!.isNotEmpty() && passwordText.text!!.isNotEmpty())
            {
                startActivity(Intent(this, ChatActivity::class.java))
            }
            else
                AlertDialog.Builder(this)
                    .setTitle("Ошибка")
                    .setMessage("Должны быть введены логин и пароль")
                    .setPositiveButton("OK", null)
                    .create()
                    .show()
        }

    }

}
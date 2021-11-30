package com.example.chatchasi

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.chatchasi.common.HTTP
import com.example.chatchasi.common.Myapp
import org.json.JSONObject

class AuthActivity : Activity() {

    private lateinit var app: Myapp


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        app = applicationContext as Myapp

        val loginText = findViewById<EditText>(R.id.login)
        val passwordText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.login_button)
        val logoutButton = findViewById<Button>(R.id.logout_button)
        loginButton.setOnClickListener {
            if(loginText.text!!.isNotEmpty() && passwordText.text!!.isNotEmpty())
            {
                app.loginText = loginText.text.toString()
                app.passwordText = passwordText.text.toString()
                startActivity(Intent(this, MainActivity::class.java))
            }
            else
                AlertDialog.Builder(this)
                    .setTitle("Ошибка")
                    .setMessage("Должны быть введены логин и пароль")
                    .setPositiveButton("OK", null)
                    .create()
                    .show()
        }
        logoutButton.setOnClickListener {
                HTTP.requestPOST(
                        "http://s4a.kolei.ru/logout",
                        JSONObject().put("username", app.username),
                        mapOf(
                                "Content-Type" to "application/json"
                        )
                ) { result, error ->
                    // при выходе не забываем стереть существующий токен
                    app.token = ""

                    // каких-то осмысленных действий дальше не предполагается
                    // разве что снова вызвать форму авторизации
                    runOnUiThread {
                        if (result != null) {
                            Toast.makeText(this, "Logout success!", Toast.LENGTH_LONG).show()
                        } else {
                            androidx.appcompat.app.AlertDialog.Builder(this)
                                    .setTitle("Ошибка http-запроса")
                                    .setMessage(error)
                                    .setPositiveButton("OK", null)
                                    .create()
                                    .show()
                        }
                    }
                }
        }

    }

}
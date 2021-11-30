package com.example.chatchasi

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.wearable.activity.WearableActivity
import android.widget.ImageView
import com.example.chatchasi.common.HTTP
import com.example.chatchasi.common.Myapp
import org.json.JSONObject

class MainActivity : Activity() {
    private lateinit var app: Myapp
    var counter = 0
    var ready = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        app = applicationContext as Myapp
        var splash = findViewById<ImageView>(R.id.splash)
        object : CountDownTimer(5000,1000){
            override fun onTick(millisUntilFinished: Long) {
                // заставляем пялиться на нашу заставку как минимум 3 секунды
                counter++
                if(counter>3 && ready){
                    // данные получены - скрываем заставку
                    splash.elevation = 0F
                    this.cancel()
                }
            }

            override fun onFinish(){
                splash.elevation = 0F
            }
        }.start()
        //вызов onLoginResponce
        onLoginResponce(app.loginText, app.passwordText)
        startActivity(Intent(this, ChatActivity::class.java))

        //setAmbientEnabled()
    }

    fun onLoginResponce (login: String, password: String)
    {
        // первым делом сохраняем имя пользователя,
        // чтобы при необходимости можно было разлогиниться
        app.username = login



        // затем формируем JSON объект с нужными полями
        val json = JSONObject()
        json.put("username", login)
        json.put("password", password)

        // и вызываем POST-запрос /login
        // в параметрах не забываем указать заголовок Content-Type
        HTTP.requestPOST(
            "http://s4a.kolei.ru/login",
            json,

            mapOf(
                "Content-Type" to "application/json"
            )
        ){result, error ->
            if(result!=null){
                try {
                    // анализируем ответ
                    val jsonResp = JSONObject(result)

                    // если нет объекта notice
                    if(!jsonResp.has("notice"))
                        throw Exception("Не верный формат ответа, ожидался объект notice")

                    // есть какая-то ошибка
                    if(jsonResp.getJSONObject("notice").has("answer"))
                        throw Exception(jsonResp.getJSONObject("notice").getString("answer"))

                    // есть токен!!!
                    if(jsonResp.getJSONObject("notice").has("token")) {
                        app.token = jsonResp.getJSONObject("notice").getString("token")
                    }
                    else {
                        throw Exception("Не верный формат ответа, ожидался объект token")

                    }

                }
                catch (e: Exception)
                {
                    runOnUiThread {
                        AlertDialog.Builder(this)
                            .setTitle("Ошибка")
                            .setMessage(e.message)
                            .setPositiveButton("OK", null)
                            .create()
                            .show()
                        startActivity(Intent(this, AuthActivity::class.java))
                    }
                }
            }
            else
                runOnUiThread {
                    AlertDialog.Builder(this)
                        .setTitle("Ошибка http-запроса")
                        .setMessage(error)
                        .setPositiveButton("OK", null)
                        .create()
                        .show()
                    startActivity(Intent(this, AuthActivity::class.java))
                }

        }
    }
}
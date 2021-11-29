package com.example.chatchasi


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import com.example.chatchasi.common.HTTP
import com.example.chatchasi.common.Myapp
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timer

class ChatActivity : WearableActivity() {
    private lateinit var wrc: WearableRecyclerView
    val chatList = ArrayList<Chat>()
    private val token = "d4c9eea0d00fb43230b479793d6aa78f"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        wrc = findViewById(R.id.chatRecyclerView)
//wrc.setHasFixedSize(true)

// этот параметр позволяет прокручивать
// крайние элементы списка на середину экрана
// (иначе на круглых часах можно не разглядеть содержимое)
        wrc.isEdgeItemsCenteringEnabled = true

// менеждер тоже свой
        wrc.layoutManager = WearableLinearLayoutManager(this)

// адаптер не отличается

        wrc.adapter = MyAdapter(chatList, this)

        timer(period = 5000L, startAt = Date()){
            updateChat()

        }
    }
    fun updateChat(){
        // тут вызвать GET /chat
        HTTP.requestGET(
            "http://s4a.kolei.ru/chat",


            mapOf(
                "Token" to token
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

                    if (jsonResp.getJSONObject("notice").has("messages")) {
                        chatList.clear()
                        val data = jsonResp.getJSONObject("notice").getJSONArray("messages")
                        for (i in 0 until data.length()) {
                            val item = data.getJSONObject(i)
                            chatList.add(
                                Chat(
                                    item.getString("user"),
                                    item.getString("message")
                                )
                            )
                        }
                        runOnUiThread {
                            wrc.adapter?.notifyDataSetChanged()

                        }                        }
                    else
                        throw Exception("Не верный формат ответа, ожидался объект messages")
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
                }

        }
    }
}
package com.example.chasy

import android.os.Bundle
import android.support.wearable.activity.WearableActivity

class MenuActiviti : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_activiti)

        // Enables Always-on
        setAmbientEnabled()
    }
}
package com.david.qmul_room_timetable_app.util

import android.content.Intent
import android.os.Build
import java.io.Serializable

class GetSerializableExtra {

    companion object {
        @Suppress("DEPRECATION", "ObsoleteSdkInt")
        fun <T: Serializable?> getSerializableExtra(intent: Intent, key: String, className: Class<T>): T {
            return if (Build.VERSION.SDK_INT >= 33)
                intent.getSerializableExtra(key, className)!!
            else
                intent.getSerializableExtra(key) as T
        }
    }
}
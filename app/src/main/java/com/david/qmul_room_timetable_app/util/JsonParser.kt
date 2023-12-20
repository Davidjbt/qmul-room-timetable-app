package com.david.qmul_room_timetable_app.util

import android.content.Context
import com.david.qmul_room_timetable_app.model.Campus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader


class JsonParser(private val context: Context) {

    public fun parseJsonData(): List<Campus> {
        val inputStream = context.assets.open("qmul_rooms.json")
        val reader = InputStreamReader(inputStream)

        val gson = Gson()
        val campusType = object : TypeToken<List<Campus>>() {}.type

        return gson.fromJson(reader, campusType)
    }

}
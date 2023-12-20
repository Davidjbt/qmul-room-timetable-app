package com.david.qmul_room_timetable_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun addRoomTimetableQuery(view: View) {
        val intent = Intent(this, AddRoomTimetable::class.java)
        startActivity(intent)

    }

}
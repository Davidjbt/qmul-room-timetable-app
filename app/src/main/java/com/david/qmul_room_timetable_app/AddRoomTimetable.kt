package com.david.qmul_room_timetable_app

import android.os.Bundle
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddRoomTimetable : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_room_timetable)

        val campus = arrayOf("Mile End Campus")

        var autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView2)
        var adapterItems = ArrayAdapter(this, R.layout.dropdown_item, campus)

        autoCompleteTextView.setAdapter(adapterItems)
        autoCompleteTextView.setOnItemClickListener(AdapterView.OnItemClickListener(
            fun(adapterView :AdapterView<out Adapter>, view: View, i: Int, l: Long) {
                val item = adapterView.getItemAtPosition(i).toString()
                Toast.makeText(this, "Item: $item", Toast.LENGTH_SHORT).show()
            }
        ))

    }

}
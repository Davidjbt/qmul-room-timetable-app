package com.david.qmul_room_timetable_app

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.david.qmul_room_timetable_app.model.Campus
import com.david.qmul_room_timetable_app.util.JsonParser

class AddRoomTimetable : AppCompatActivity() {

    private lateinit var campusList: List<Campus>
    private lateinit var adapterBuilding: ArrayAdapter<String>
    private lateinit var adapterCampus: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_room_timetable)

        val jsonParser = JsonParser(this)
        campusList = jsonParser.parseJsonData()

        val autoCompleteTextViewCampus = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView2)
        val autoCompleteTextViewBuilding = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView5)


        adapterCampus = ArrayAdapter(this, R.layout.dropdown_item, getCampusNames())
        adapterBuilding = ArrayAdapter(this, R.layout.dropdown_item, mutableListOf<String>())


        autoCompleteTextViewCampus.setAdapter(adapterCampus)
        autoCompleteTextViewBuilding.setAdapter(adapterBuilding)

        autoCompleteTextViewCampus.setOnItemClickListener { _, _, position, _ ->
            val selectedCampus = campusList[position]
            Toast.makeText(this, "Item: ${selectedCampus.campus}", Toast.LENGTH_SHORT).show()
            updateBuildingDropdown(selectedCampus.buildings.map { it.building })
        }
    }

    private fun getCampusNames(): List<String> {
        return campusList.map { campus -> campus.campus }
    }

    private fun updateBuildingDropdown(buildingNames: List<String>) {
        if (!adapterBuilding.isEmpty) adapterBuilding.clear()
        adapterBuilding.addAll(buildingNames)
        adapterBuilding.notifyDataSetChanged()
    }

}
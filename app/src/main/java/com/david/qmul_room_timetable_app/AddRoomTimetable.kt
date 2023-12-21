package com.david.qmul_room_timetable_app

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.david.qmul_room_timetable_app.model.Building
import com.david.qmul_room_timetable_app.model.Campus
import com.david.qmul_room_timetable_app.util.JsonParser

class AddRoomTimetable : AppCompatActivity() {

    private lateinit var campusList: List<Campus>
    private lateinit var adapterBuilding: ArrayAdapter<String>
    private lateinit var adapterCampus: ArrayAdapter<String>
    private lateinit var textViewRooms: TextView
    private lateinit var selectedCampus: Campus
    private lateinit var selectedBuilding: Building
    private lateinit var selectedRooms: Array<String>
    private lateinit var selectedRoomsBoolean: BooleanArray
    private lateinit var roomsList: ArrayList<Int>
    private lateinit var roomsArray: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_room_timetable)

        val jsonParser = JsonParser(this)
        campusList = jsonParser.parseJsonData()

        val autoCompleteTextViewCampus = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView2)
        val autoCompleteTextViewBuilding = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView5)
        textViewRooms = findViewById(R.id.textView)
        adapterCampus = ArrayAdapter(this, R.layout.dropdown_item, getCampusNames())
        adapterBuilding = ArrayAdapter(this, R.layout.dropdown_item, mutableListOf<String>())

        autoCompleteTextViewCampus.setAdapter(adapterCampus)
        autoCompleteTextViewBuilding.setAdapter(adapterBuilding)

        autoCompleteTextViewCampus.setOnItemClickListener { _, _, position, _ ->
            selectedCampus = campusList[position]
            Toast.makeText(this, "Item: ${selectedCampus.campus}", Toast.LENGTH_SHORT).show()
            updateBuildingDropdown(selectedCampus.buildings.map { it.building })
        }

        autoCompleteTextViewBuilding.setOnItemClickListener { _, _, position, _ ->
            selectedBuilding = selectedCampus.buildings[position]
            Toast.makeText(this, "Item: ${selectedBuilding.building}", Toast.LENGTH_SHORT).show()
            updateRoomDropdown();
        }
    }

    private fun getCampusNames(): List<String> {
        return campusList.map { campus -> campus.campus }
    }

    private fun updateBuildingDropdown(buildingNames: List<String>) {
        adapterBuilding.clear()
        adapterBuilding.addAll(buildingNames)
        adapterBuilding.notifyDataSetChanged()
    }

    private fun updateRoomDropdown() {
        roomsArray = selectedBuilding.rooms.toTypedArray();
        roomsList = ArrayList()
        selectedRoomsBoolean = BooleanArray(roomsArray.size)
    }

    fun showRoomsDialog(view: View) {
        var builder: AlertDialog.Builder = AlertDialog.Builder(this)

        builder.setTitle("Select Room(s)")
        builder.setCancelable(false)

        builder.setMultiChoiceItems(roomsArray, selectedRoomsBoolean
        ) { dialog: DialogInterface, which: Int, isChecked: Boolean ->
            if (isChecked) {
                roomsList.add(which)
            } else {
                roomsList.remove(which)
            }
        }

        builder.setPositiveButton("OK"
        ) {dialog, i ->
            val stringBuilder: StringBuilder = StringBuilder()

            for (j in 0 until roomsList.size) {
                stringBuilder.append(roomsArray[roomsList[j]])

                if (j != roomsList.size - 1) {
                    stringBuilder.append(", ")
                }
            }

            textViewRooms.text = stringBuilder.toString()
        }

        builder.setNegativeButton("Cancel"
        ) { dialog, i ->
            dialog.dismiss()
        }

        builder.setNeutralButton("Clear All"
        ) {dialog, i ->
            selectedRoomsBoolean.fill(false)
            roomsList.clear()
            textViewRooms.text = ""
        }

        builder.show()
    }

}
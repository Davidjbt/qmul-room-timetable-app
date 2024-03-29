package com.david.qmul_room_timetable_app

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
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
import com.david.qmul_room_timetable_app.util.GetSerializableExtra.Companion.getSerializableExtra
import com.david.qmul_room_timetable_app.util.JsonParser

class AddRoomTimetable : AppCompatActivity() {

    private lateinit var autoCompleteTextViewCampus: AutoCompleteTextView;
    private lateinit var autoCompleteTextViewBuilding: AutoCompleteTextView
    private lateinit var adapterBuilding: ArrayAdapter<String>
    private lateinit var adapterCampus: ArrayAdapter<String>
    private lateinit var textViewRooms: TextView
    private lateinit var campusList: List<Campus>
    private lateinit var selectedCampus: Campus
    private lateinit var selectedBuilding: Building
    private lateinit var selectedRooms: ArrayList<String>
    private lateinit var selectedRoomsBoolean: BooleanArray
    private lateinit var roomsArray: Array<String>

    private lateinit var previousRoomTimeQuery: RoomTimetableQuery

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_room_timetable)

        val jsonParser = JsonParser(this)
        campusList = jsonParser.parseJsonData()

        autoCompleteTextViewCampus = findViewById(R.id.autoCompleteTextView2)
        autoCompleteTextViewBuilding = findViewById(R.id.autoCompleteTextView5)
        textViewRooms = findViewById(R.id.textView)
        adapterCampus = ArrayAdapter(this, R.layout.dropdown_item, getCampusNames())
        adapterBuilding = ArrayAdapter(this, R.layout.dropdown_item, mutableListOf<String>())
        selectedRooms = ArrayList()

        autoCompleteTextViewCampus.setAdapter(adapterCampus)
        autoCompleteTextViewBuilding.setAdapter(adapterBuilding)

        autoCompleteTextViewCampus.setOnItemClickListener { _, _, position, _ ->
            selectedCampus = campusList[position]
            selectedRooms.clear()
            Toast.makeText(this, "Item: ${selectedCampus.campus}", Toast.LENGTH_SHORT).show()
            updateBuildingDropdown(selectedCampus.buildings.map { it.building })
            autoCompleteTextViewBuilding.setText("Select Building", false)
        }

        autoCompleteTextViewBuilding.setOnItemClickListener { _, _, position, _ ->
            selectedBuilding = selectedCampus.buildings[position]
            Toast.makeText(this, "Item: ${selectedBuilding.building}", Toast.LENGTH_SHORT).show()
            updateRoomDropdown()
        }

        if (intent.hasExtra("roomTimetableQuery")) {
            initialiseForm(intent)
        }
    }

    private fun initialiseForm(intent: Intent) {
        val intentData = getSerializableExtra(intent, "roomTimetableQuery", ByteArray::class.java)
        val roomTimetableQuery = RoomTimetableQuery.parseFrom(intentData)
        previousRoomTimeQuery = RoomTimetableQuery.parseFrom(intentData)

        autoCompleteTextViewCampus.setText(roomTimetableQuery.campus, false)
        autoCompleteTextViewBuilding.setText(roomTimetableQuery.building, false)

        selectedCampus = campusList.find { it.campus == roomTimetableQuery.campus }!!
        selectedBuilding = selectedCampus.buildings.find { it.building == roomTimetableQuery.building }!!

        updateBuildingDropdown(selectedCampus.buildings.map { it.building })
        updateRoomDropdown()

        val stringBuilder = StringBuilder()
        roomTimetableQuery.roomsList.forEach { room ->
            val index = roomsArray.indexOf(room)

            if (index != -1) {
                selectedRoomsBoolean[index] = true
                selectedRooms.add(room)
                stringBuilder.append(room)
                if (index != selectedRooms.size - 1)
                    stringBuilder.append(", ")
            }
        }

        textViewRooms.text = stringBuilder.toString()
    }

    private fun getCampusNames(): List<String> {
        return campusList.map { campus -> campus.campus }
    }

    private fun updateBuildingDropdown(buildingNames: List<String>) {
        adapterBuilding.clear()
        adapterBuilding.addAll(buildingNames)
        adapterBuilding.notifyDataSetChanged()
        textViewRooms.text = ""
    }

    private fun updateRoomDropdown() {
        roomsArray = selectedBuilding.rooms.toTypedArray();
        selectedRoomsBoolean = BooleanArray(roomsArray.size)
        selectedRooms.clear()
        textViewRooms.text = ""
    }

    fun showRoomsDialog(view: View) {
        if (!::selectedCampus.isInitialized || !::selectedBuilding.isInitialized ||  !selectedCampus.buildings.contains(selectedBuilding)) {
            Toast.makeText(this, "Please select a campus and building first", Toast.LENGTH_SHORT).show()
            return
        }

        println("${selectedCampus.campus} ${selectedBuilding.building}")

        println("here+")

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        builder.setTitle("Select Room(s)")
        builder.setCancelable(false)

        builder.setMultiChoiceItems(roomsArray, selectedRoomsBoolean
        ) { dialog: DialogInterface, which: Int, isChecked: Boolean ->
            if (isChecked) {
                selectedRooms.add(roomsArray[which])
            } else {
                selectedRooms.remove(roomsArray[which])
            }
        }

        builder.setPositiveButton("OK"
        ) {dialog, i ->
            val stringBuilder: StringBuilder = StringBuilder()

            for (j in 0 until selectedRooms.size) {
                stringBuilder.append(selectedRooms[j])

                if (j != selectedRooms.size - 1) {
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
            textViewRooms.text = ""
            selectedRooms.clear();
        }

        builder.show()
    }

    fun addRoomTimetableQuery(view: View) {
        if (selectedRooms.size == 0) {
            Toast.makeText(this, "Rooms have not selected", Toast.LENGTH_SHORT).show()
            return;
        }

        var isFetched = false
        val index = intent.getIntExtra("index", -1)

        if (index != -1) { // if query is unchanged then result was already fetched (true), else false
            isFetched = !hasRoomTimetableQueryChanged()
        }

        val roomTimetableQuery = RoomTimetableQuery.newBuilder()
            .setCampus(selectedCampus.campus)
            .setBuilding(selectedBuilding.building)
            .addAllRooms(selectedRooms)
            .setIsFetched(isFetched)
            .build()

        intent.putExtra("roomTimetableQuery", roomTimetableQuery.toByteArray())
        setResult(Activity.RESULT_OK, intent.putExtra("index", index))
        finish()
    }

    private fun hasRoomTimetableQueryChanged(): Boolean {
        if (!previousRoomTimeQuery.campus.equals(selectedCampus.campus)) return true
        if (!previousRoomTimeQuery.building.equals(selectedBuilding.building)) return true
        if (!previousRoomTimeQuery.roomsList.containsAll(selectedRooms)) return true

        return false
    }

}
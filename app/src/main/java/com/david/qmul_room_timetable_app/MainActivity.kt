package com.david.qmul_room_timetable_app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.lifecycleScope
import com.david.qmul_room_timetable_app.service.RoomTimetableService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import kotlin.random.Random

private const val ROOM_TIMETABLE_QUERY_LIST_NAME = "room_timetable_query_list"
private const val DATA_STORE_FILE_NAME = "room_timetable_query_list.pb"

private val Context.roomTimetableQueryListStore: DataStore<RoomTimetableQueryList> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = RoomTimetableQueryListSerializer
)

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showRoomTimetableQueries()
    }

    fun addRoomTimetableQuery(view: View) {
        val intent = Intent(this, AddRoomTimetable::class.java)
        startForResult.launch(intent)
    }

    private var startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val roomTimetableQuery = data.getSerializableExtra("roomTimetableQuery", AddRoomTimetable.RoomTimetableQuery::class.java)

//                    Toast.makeText(this, "Rooms: ${roomTimetableQuery?.rooms?.size}", Toast.LENGTH_SHORT).show()

                    if (roomTimetableQuery != null) {
                        lifecycleScope.launch {
                            saveRoomTimetableQuery(roomTimetableQuery)
                        }
                    }
                }
            }
    }

    private suspend fun saveRoomTimetableQuery(roomTimetableQuery: AddRoomTimetable.RoomTimetableQuery) {
        var currentData = roomTimetableQueryListStore.data.first()
        val roomTimetableQueryProto = RoomTimetableQuery.newBuilder()
            .setCampus(roomTimetableQuery.campus)
            .setBuilding(roomTimetableQuery.building)
            .addAllRooms(roomTimetableQuery.rooms.map { it })

        val updatedData = currentData.toBuilder()
            .addRoomTimetableQuery(roomTimetableQueryProto)
            .build()

        roomTimetableQueryListStore.updateData { updatedData }

        currentData = roomTimetableQueryListStore.data.first()
        Toast.makeText(this, "Entries: ${currentData.roomTimetableQueryCount}", Toast.LENGTH_SHORT).show()
        showRoomTimetableQueries()  // Call will add the next query to the query table
    }

    private val r = Random(0)

    private fun showRoomTimetableQueries() {

        lifecycleScope.launch {

            val currentData = roomTimetableQueryListStore.data.first()
            val linearLayout = findViewById<LinearLayout>(R.id.linearLayout)

            linearLayout.removeAllViews()

            for ((index, roomTimetableQuery) in currentData.roomTimetableQueryList.withIndex()) {
                val queryView = layoutInflater.inflate(R.layout.room_timetable_query_entry, linearLayout, false)

                val campusTextView = queryView.findViewById<TextView>(R.id.campusTextView)
                val buildingTextView = queryView.findViewById<TextView>(R.id.buildingTextView)
                val editButton = queryView.findViewById<ImageButton>(R.id.editButton)
                val deleteButton = queryView.findViewById<ImageButton>(R.id.deleteButton)

                campusTextView.text = roomTimetableQuery.campus
                buildingTextView.text = roomTimetableQuery.building

                editButton.setOnClickListener {

                }

                deleteButton.setOnClickListener {
                    deleteRoomTimetableQuery(index)
                }

                if (index % 2 == 0) queryView.setBackgroundColor(Color.LTGRAY)

                linearLayout.addView(queryView)
            }
        }

    }

    private fun deleteRoomTimetableQuery(index: Int) {
        lifecycleScope.launch {
            val currentData = roomTimetableQueryListStore.data.first()

            roomTimetableQueryListStore.updateData {
                currentData.toBuilder()
                    .removeRoomTimetableQuery(index)
                    .build()
            }

            showRoomTimetableQueries()
        }
    }

    fun submitRoomTimetableQueries(view: View) {
        lifecycleScope.launch {
            val roomTimetableService = RoomTimetableService()
            val currentData = roomTimetableQueryListStore.data.first()

            val results = roomTimetableService.getRoomTimetable(
                currentData.roomTimetableQueryList.map {
                    query -> AddRoomTimetable.RoomTimetableQuery(
                        query.campus,
                        query.building,
                        query.roomsList.toTypedArray()
                    )
                }.toTypedArray()
            )

            val fileName = "result.json"
            val file = File(fileName)
//
//            results.forEach { file.writeText(it) }
//
//            println("Done")
        }
//        val options = UiAutomator2Options()
//            .setAppPackage("com.android.chrome")
//            .setAppActivity("com.google.android.apps.chrome.Main")
//
//        val driver: AppiumDriver = AndroidDriver(URL("https://timetables.qmul.ac.uk/default.aspx"), options)
    }

}
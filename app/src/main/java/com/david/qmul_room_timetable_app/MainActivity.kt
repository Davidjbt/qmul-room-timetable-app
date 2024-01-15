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
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.lifecycleScope
import com.david.qmul_room_timetable_app.service.RoomTimetableService
import com.david.qmul_room_timetable_app.util.GetSerializableExtra.Companion.getSerializableExtra
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
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
                    val roomTimetableQuery = RoomTimetableQuery.parseFrom(
                        getSerializableExtra(data, "roomTimetableQuery", ByteArray::class.java)
                    )

                    if (roomTimetableQuery != null) {
                          lifecycleScope.launch {
                              val index = data.getIntExtra("index", -1)

                              if (index == -1) {
                                  saveRoomTimetableQuery(roomTimetableQuery)
                              } else {
                                  updateRoomTimetableQuery(roomTimetableQuery, index)
                              }
                        }
                    }
                }
            }
    }

    private suspend fun saveRoomTimetableQuery(roomTimetableQuery: RoomTimetableQuery) {
        var currentData = roomTimetableQueryListStore.data.first()
        val updatedData = currentData.toBuilder()
            .addRoomTimetableQuery(roomTimetableQuery)
            .build()

        roomTimetableQueryListStore.updateData { updatedData }

        currentData = roomTimetableQueryListStore.data.first()
        Toast.makeText(this, "Entries: ${currentData.roomTimetableQueryCount}", Toast.LENGTH_SHORT).show()
        showRoomTimetableQueries()  // Call will add the next query to the query table
    }

    private suspend fun updateRoomTimetableQuery(roomTimetableQuery: RoomTimetableQuery, index: Int) {
        val currentData = roomTimetableQueryListStore.data.first()
        val updateData =  currentData.toBuilder()
            .setRoomTimetableQuery(index, roomTimetableQuery)
            .build()

        roomTimetableQueryListStore.updateData { updateData }
        showRoomTimetableQueries()  // Call will update query table
    }

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
                    onEditButtonClick(index)
                }

                deleteButton.setOnClickListener {
                    deleteRoomTimetableQuery(index)
                }

                if (index % 2 == 0) queryView.setBackgroundColor(Color.LTGRAY)

                linearLayout.addView(queryView)
            }
        }

    }

    private fun onEditButtonClick(index: Int) {
        val intent = Intent(this, AddRoomTimetable::class.java)

        lifecycleScope.launch {
            val roomTimetableQuery = roomTimetableQueryListStore.data.first().getRoomTimetableQuery(index)

            intent.putExtra("roomTimetableQuery", roomTimetableQuery.toByteArray())
            intent.putExtra("index", index)

            startForResult.launch(intent)
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

            val results = roomTimetableService.getRoomTimetable(currentData.roomTimetableQueryList.toTypedArray())

            val folderName = "results"
            val folder = File(filesDir, folderName)

            deleteSavedResults(folder)

            if (!folder.exists()) folder.mkdir()

            for ((i, result) in results.withIndex()) {
                val file = File(folder, "query_${i}.html")
                file.writeText(result.resultHtml)
            }

            val styleSheets = results.filter { it.resultStyling != null}[0].resultStyling

            if (styleSheets != null) {
                for ((name, sheet) in styleSheets.entries) {
                    val file = File(folder, name)
                    file.writeText(sheet)
                }
            }

        }

        val intent = Intent(this, ShowResultsActivity::class.java)
        startForResult.launch(intent)
    }

    private fun deleteSavedResults(resultsFolder: File) {
        resultsFolder.listFiles()
            ?.filter { it.extension.endsWith("html") }
            ?.forEach { it.delete() ; println("deleting")}
    }

    fun showResults(view: View) {
        val intent = Intent(this, ShowResultsActivity::class.java)
        startForResult.launch(intent)
    }

}
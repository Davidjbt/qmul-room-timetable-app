package com.david.qmul_room_timetable_app

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import java.time.DayOfWeek
import java.time.LocalDate

private const val ROOM_TIMETABLE_QUERY_LIST_NAME = "room_timetable_query_list"
private const val DATA_STORE_FILE_NAME = "room_timetable_query_list.pb"
private const val LAST_FETCH_STORE_FILE_NAME = "last_fetch.pb"

private val Context.roomTimetableQueryListStore: DataStore<RoomTimetableQueryList> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = RoomTimetableQueryListSerializer
)

private val Context.lastFetchStore: DataStore<LastFetch> by dataStore(
    fileName = LAST_FETCH_STORE_FILE_NAME,
    serializer = LastFetchSerializer
)

class MainActivity : AppCompatActivity() {

    private val roomTimetableService = RoomTimetableService() // todo: DI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showRoomTimetableQueries()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        checkIfFetchIsNeeded()
    }

    private fun checkIfFetchIsNeeded() {
        lifecycleScope.launch {
            val currentDate = LocalDate.now()
            val lastFetchDate = if (!lastFetchStore.data.first().date.equals(""))
                                    LocalDate.parse(lastFetchStore.data.first().date)
                                else
                                    currentDate

            if (currentDate.equals(lastFetchDate)) {
                return@launch
            } else if (currentDate.dayOfWeek == DayOfWeek.SATURDAY) {
                if (!lastFetchDate.isEqual(currentDate)) {
                    submitRoomTimetableQueries(View(this@MainActivity))
                }
            } else if (currentDate.dayOfWeek == DayOfWeek.SUNDAY) {
                if (!lastFetchDate.equals(currentDate) && !lastFetchDate.plusDays(1).equals(currentDate)) {
                    submitRoomTimetableQueries(View(this@MainActivity))
                }
            }
        }
    }

    private fun updateFetchStatus() {

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
        val currentData = roomTimetableQueryListStore.data.first()
        val updatedData = currentData.toBuilder()
            .addRoomTimetableQuery(roomTimetableQuery)
            .build()

        roomTimetableQueryListStore.updateData { updatedData }
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
            deleteSavedResult(index)
        }
    }

    private fun deleteSavedResult(index: Int) {
        val folderName = "results"
        val folder = File(filesDir, folderName)
        val resultCount = folder.listFiles()?.filter { it.extension.endsWith("html") }?.size?: 0

        if (index == resultCount - 1) {
            folder.listFiles()?.find { it.name == "query_$index.html" }?.delete()
        } else {
            for (i in index until resultCount - 1) {
                val file = File(folder, "query_${i + 1}.html")
                file.renameTo(File(folder, "query_${i}.html"))
                if (i == resultCount - 2) folder.listFiles()?.find { it.name == "query_${i + 1}.html" }?.delete()
            }
        }
    }

    fun submitRoomTimetableQueries(view: View) {
        lifecycleScope.launch {
            val currentData = roomTimetableQueryListStore.data.first()

            if (currentData.roomTimetableQueryCount == 0) {
                Toast.makeText(view.context, "Not queries saved", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val results = roomTimetableService.getRoomTimetable(currentData)

            val folderName = "results"
            val folder = File(filesDir, folderName)

            deleteSavedResults(folder)

            if (!folder.exists()) folder.mkdir()

            for ((i, result) in results.withIndex()) {
                val file = File(folder, "query_${i}_${result.day}.html")
                file.writeText(result.resultHtml)
            }

            val styleSheets = results.find { it.resultStyling != null}?.resultStyling

            if (styleSheets != null) {
                for ((name, sheet) in styleSheets.entries) {
                    val file = File(folder, name)
                    file.writeText(sheet)
                }
            }

            val updatedQueries = currentData.roomTimetableQueryList.map { currentQuery ->
                currentQuery.toBuilder()
                    .setIsFetched(true)
                    .build()
            }

            roomTimetableQueryListStore.updateData {
                currentData.toBuilder()
                    .clearRoomTimetableQuery()
                    .addAllRoomTimetableQuery(updatedQueries)
                    .build()
            }

            val intent = Intent(view.context, ShowResultsActivity::class.java)
            startForResult.launch(intent)
        }
    }

    private fun deleteSavedResults(resultsFolder: File) {
        resultsFolder.listFiles()
            ?.find { it.extension == "html" }
            ?.delete()
    }

    fun showResults(view: View) {
        var queryCount = 0
        var allFetched = false

        lifecycleScope.launch {
            val data = roomTimetableQueryListStore.data.first()
            queryCount = data.roomTimetableQueryCount
            allFetched = !data.roomTimetableQueryList
                .map { it.isFetched }
                .contains(false)
        }

        if (!allFetched) {
            Toast.makeText(this, "Upload new queries", Toast.LENGTH_SHORT).show()
        } else if (queryCount == 0) {
            Toast.makeText(this, "Not queries saved", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(this, ShowResultsActivity::class.java)
            startForResult.launch(intent)
        }
    }

}
package com.david.qmul_room_timetable_app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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
            .addRoomTimetableQueryList(roomTimetableQueryProto)
            .build()

        roomTimetableQueryListStore.updateData { updatedData }

        currentData = roomTimetableQueryListStore.data.first()
        Toast.makeText(this, "Entries: ${currentData.roomTimetableQueryListCount}", Toast.LENGTH_SHORT).show()

    }

}
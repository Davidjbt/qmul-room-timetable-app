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

private const val ROOM_TIMETABLE_QUERY_NAME = "room_timetable_query"
private const val DATA_STORE_FILE_NAME = "room_timetable_queries.pb"

private val Context.roomTimetableQueryStore: DataStore<RoomTimetableQuery> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = RoomTimetableQuerySerializer
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

    var startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val roomTimetable = data.getSerializableExtra("roomTimetableQuery", AddRoomTimetable.RoomTimetableQuery::class.java)

                    Toast.makeText(this, "Rooms: ${roomTimetable?.rooms?.size}", Toast.LENGTH_SHORT).show()
                }

            }
    }

}
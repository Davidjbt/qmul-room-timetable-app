package com.david.qmul_room_timetable_app.service

import com.david.qmul_room_timetable_app.RoomTimetableQueryList
import com.david.qmul_room_timetable_app.model.QueryResult
import com.david.qmul_room_timetable_app.model.RoomTimetableQueryJson
import com.david.qmul_room_timetable_app.network.RoomTimetableApi
import java.time.LocalDate

class RoomTimetableService {

    companion object {
        private val WEEKDAYS = arrayOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY")
    }

    suspend fun getRoomTimetable(roomTimetableQueries: RoomTimetableQueryList): Array<QueryResult> {
        val currentDay = LocalDate.now().dayOfWeek.toString()
        val week = if (WEEKDAYS.contains(currentDay)) "This Week" else "Next Week"
        val roomTimetableQueriesJson = WEEKDAYS.flatMap { day -> protoToJson(roomTimetableQueries, day, week) }

        return RoomTimetableApi.retrofitService.getRoomsTimetables(roomTimetableQueriesJson)
    }

    private fun protoToJson(
        roomTimetableQueries: RoomTimetableQueryList,
        day: String,
        week: String
    ): List<RoomTimetableQueryJson> {
        return roomTimetableQueries.roomTimetableQueryList.map {
            q -> RoomTimetableQueryJson(q.building, q.roomsList, week, day)
        }
    }

}

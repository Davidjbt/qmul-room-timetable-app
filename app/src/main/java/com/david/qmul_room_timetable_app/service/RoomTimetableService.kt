package com.david.qmul_room_timetable_app.service

import com.david.qmul_room_timetable_app.RoomTimetableQueryList
import com.david.qmul_room_timetable_app.network.RoomTimetableApi

class RoomTimetableService {

    companion object {
        private val WEEKDAYS = arrayOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY")
    }

    suspend fun getRoomTimetable(roomTimetableQueries: RoomTimetableQueryList) : Array<String> {
        val roomTimetableQueriesJson = protoToJson(roomTimetableQueries)


        return RoomTimetableApi.retrofitService.getRoomsTimetables(roomTimetableQueriesJson)
    }

    private fun protoToJson(roomTimetableQueries: RoomTimetableQueryList): List<RoomTimetableQueryJson> {
        return roomTimetableQueries.roomTimetableQueryList.map {
            q -> RoomTimetableQueryJson(q.building, q.roomsList, "This Week", "MONDAY")
        }
    }

    data class RoomTimetableQueryJson (
        val building: String,
        val rooms: List<String>,
        val week: String,
        val day: String
    )

//    fun getRoomTimetable(roomTimetableQueries: Array<RoomTimetableQuery>): Array<QueryResult> {
//        val nThreads = Runtime.getRuntime().availableProcessors()
//        val executorService = Executors.newFixedThreadPool(nThreads)
//
//        val currentDay = LocalDate.now().dayOfWeek.toString()
//        val week = if (WEEKDAYS.contains(currentDay)) "This Week" else "Next Week"
//        val tasks = mutableListOf<FetchRoomTimetableTask>()
//
//        for (roomTimetableQuery in roomTimetableQueries) {
//            for (day in WEEKDAYS.slice(WEEKDAYS.indexOf("MONDAY")..<WEEKDAYS.size)) {
//                tasks.add(FetchRoomTimetableTask(roomTimetableQuery, day, week))
//            }
//        }
//
//        tasks.forEach { task -> executorService.execute(task) }
//        executorService.shutdown()
//        executorService.awaitTermination(30, TimeUnit.SECONDS)
//
//        return tasks.map { QueryResult(it.day, it.roomTimetableHtml, it.roomTimetableCss)  }.toTypedArray()
//    }

    data class QueryResult (
        val day: String,
        val resultHtml: String,
        val resultStyling: Map<String, String>?
    )

}
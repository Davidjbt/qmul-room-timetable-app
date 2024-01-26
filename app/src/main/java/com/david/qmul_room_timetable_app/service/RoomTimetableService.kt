package com.david.qmul_room_timetable_app.service

import com.david.qmul_room_timetable_app.RoomTimetableQuery
import java.time.LocalDate
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class RoomTimetableService {


    companion object {
        private val WEEKDAYS = arrayOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY")
    }

    fun getRoomTimetable(roomTimetableQueries: Array<RoomTimetableQuery>): Array<QueryResult> {
        val nThreads = Runtime.getRuntime().availableProcessors()
        val executorService = Executors.newFixedThreadPool(nThreads)

        val currentDay = LocalDate.now().dayOfWeek.toString()
        val tasks = mutableListOf<FetchRoomTimetableTask>()

        for (roomTimetableQuery in roomTimetableQueries) {
            for (day in WEEKDAYS.slice(WEEKDAYS.indexOf(currentDay)..<WEEKDAYS.size)) {
                tasks.add(FetchRoomTimetableTask(roomTimetableQuery, day))
            }
        }

        tasks.forEach { task -> executorService.execute(task) }
        executorService.shutdown()
        executorService.awaitTermination(30, TimeUnit.SECONDS)

        return tasks.map { QueryResult(it.day, it.roomTimetableHtml, it.roomTimetableCss)  }.toTypedArray()
    }

    data class QueryResult (
        val day: String,
        val resultHtml: String,
        val resultStyling: Map<String, String>?
    )

}
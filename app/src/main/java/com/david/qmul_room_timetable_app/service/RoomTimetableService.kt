package com.david.qmul_room_timetable_app.service

import com.david.qmul_room_timetable_app.AddRoomTimetable.RoomTimetableQuery
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class RoomTimetableService {

    fun getRoomTimetable(roomTimetableQueries: Array<RoomTimetableQuery>): Array<QueryResult> {
        val nThreads = Runtime.getRuntime().availableProcessors()
        val executorService = Executors.newFixedThreadPool(nThreads)

        val tasks = roomTimetableQueries.map { FetchRoomTimetableTask(it) }

        tasks.forEach { task -> executorService.execute(task) }
        executorService.shutdown()
        executorService.awaitTermination(30, TimeUnit.SECONDS)

        return tasks.map { QueryResult(it.roomTimetableHtml, it.roomTimetableCss)  }.toTypedArray()
    }

    data class QueryResult (
        val resultHtml: String,
        val resultStyling: Map<String, String>?
    )

}
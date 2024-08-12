package com.david.qmul_room_timetable_app.model

data class RoomTimetableQueryJson (
    val building: String,
    val rooms: List<String>,
    val week: String,
    val day: String
)

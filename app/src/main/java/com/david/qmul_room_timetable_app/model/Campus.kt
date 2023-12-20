package com.david.qmul_room_timetable_app.model

data class Campus (

    val campus: String,
    val buildings: List<Building>
)

data class Building (

    val building: String,
    val rooms: List<String>
)

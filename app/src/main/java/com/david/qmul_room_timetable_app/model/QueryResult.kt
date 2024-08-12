package com.david.qmul_room_timetable_app.model

data class QueryResult (
    val day: String,
    val resultHtml: String,
    val resultStyling: Map<String, String>?
)
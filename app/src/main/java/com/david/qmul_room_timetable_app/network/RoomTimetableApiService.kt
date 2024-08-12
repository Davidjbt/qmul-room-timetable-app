package com.david.qmul_room_timetable_app.network

import com.david.qmul_room_timetable_app.model.QueryResult
import com.david.qmul_room_timetable_app.model.RoomTimetableQueryJson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

private const val BASE_URL = "http://10.0.2.2:8080"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
    .baseUrl(BASE_URL)
    .build()

interface RoomTimetableApiService {
    @POST("room/timetable")
    suspend fun getRoomsTimetables(@Body roomTimetableQueries: List<RoomTimetableQueryJson>): Array<QueryResult>
}

object RoomTimetableApi {
    val retrofitService: RoomTimetableApiService = retrofit.create(RoomTimetableApiService::class.java)
}

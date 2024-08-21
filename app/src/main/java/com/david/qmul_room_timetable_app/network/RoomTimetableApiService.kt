package com.david.qmul_room_timetable_app.network

import com.david.qmul_room_timetable_app.model.QueryResult
import com.david.qmul_room_timetable_app.model.RoomTimetableQueryJson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

private const val BASE_URL = "http://10.0.2.2:8080"

private val httpClient = OkHttpClient.Builder()
    .callTimeout(30, TimeUnit.SECONDS)
    .connectTimeout(20, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
    .baseUrl(BASE_URL)
    .client(httpClient.build())
    .build()

interface RoomTimetableApiService {
    @POST("room/timetable")
    suspend fun getRoomsTimetables(@Body roomTimetableQueries: List<RoomTimetableQueryJson>): Array<QueryResult>
}

object RoomTimetableApi {
    val retrofitService: RoomTimetableApiService = retrofit.create(RoomTimetableApiService::class.java)
}
